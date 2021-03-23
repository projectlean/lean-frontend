package org.lean.ui.plugins.perspective.metadata;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.*;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.metadata.util.HopMetadataUtil;
//import org.apache.hop.ui.core.metadata.MetadataFileType;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.apache.hop.ui.hopgui.file.IHopFileTypeHandler;
import org.apache.hop.ui.hopgui.file.empty.EmptyFileType;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.core.metadata.LeanMetadataUtil;
import org.lean.ui.core.metadata.MetadataFileType;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.core.MetadataEditor;
import org.lean.ui.core.PropsUi;
import org.lean.ui.core.dialog.ErrorDialog;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.core.metadata.MetadataManager;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.plugins.file.ILeanFileType;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;
import org.lean.ui.plugins.perspective.LeanPerspectiveBase;
import org.lean.ui.plugins.perspective.ILeanPerspective;
import org.lean.ui.plugins.perspective.LeanPerspectivePlugin;

import java.util.*;

@LeanPerspectivePlugin(
        id = "LeanMetadataPerspective",
        name = "LeanMetadataPerspective",
        description = "Lean Metadata Perspective",
        image = "./frontend/images/perspectives/metadata.svg",
        route = "metadata"
)
@GuiPlugin(description = "This perspective allows you to modify different types of metadata")
@Route(value="metadata", layout = LeanGuiLayout.class)
public class MetadataPerspective extends LeanPerspectiveBase implements ILeanPerspective {

    private static final String METADATA_PERSPECTIVE_TREE = "Metadata perspective tree";

    public static final String GUI_PLUGIN_TOOLBAR_PARENT_ID = "MetadataPerspective-Toolbar";

    public static final String TOOLBAR_ITEM_EDIT = "MetadataPerspective-Toolbar-10010-Edit";
    public static final String TOOLBAR_ITEM_DUPLICATE = "MetadataPerspective-Toolbar-10030-Duplicate";
    public static final String TOOLBAR_ITEM_DELETE = "MetadataPerspective-Toolbar-10040-Delete";
    public static final String TOOLBAR_ITEM_REFRESH = "MetadataPerspective-Toolbar-10100-Refresh";

    public static final String KEY_HELP = "Help";

    private IHopMetadataProvider metadataProvider;

    private final EmptyFileType emptyFileType;
    private final MetadataFileType metadataFileType;

    private LeanToolbar toolbar;
    private GuiToolbarWidgets toolbarWidgets;

    private Div metadataTreeDiv, metadataTreeHolderDiv, metadataContentDiv;

    private Tabs metadataTabs;
    private Div tabHolderDiv;
    private Map<Tab, Div> tabsToPages;

    public TreeGrid<MetadataTreeGridHelper> metadataTree;

    private Map<Tab, MetadataEditor> editorsTabsMap = new HashMap<>();


    public MetadataPerspective(){
        super();
        setId("metadata-perspective");


        this.emptyFileType = new EmptyFileType();
        this.metadataFileType = new MetadataFileType();

        SplitLayout metadataSplit = new SplitLayout();
        metadataSplit.setId("metadata-perspective-split");
        metadataSplit.setSizeFull();
        metadataTreeDiv = new Div();
        metadataTreeDiv.setId("metadata-tree");
        metadataTreeDiv.setSizeFull();
        metadataContentDiv = new Div();
        metadataContentDiv.setId("metadata-content");
        metadataContentDiv.setSizeFull();

        metadataTabs = new Tabs();
        metadataTabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabHolderDiv = new Div();
        tabHolderDiv.setSizeFull();
        tabHolderDiv.setId("metadata-tabholder");
        tabsToPages = new HashMap<>();

        metadataContentDiv.add(metadataTabs, tabHolderDiv);
        metadataTabs.addSelectedChangeListener(e -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(metadataTabs.getSelectedTab());
            selectedPage.setVisible(true);
        });

        metadataSplit.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        metadataSplit.addToPrimary(metadataTreeDiv);
        metadataSplit.addToSecondary(metadataContentDiv);
        getContent().add(metadataSplit);

    }

    @Override
    public String getPluginId() {
        return "metadata-perspective";
    }

    @Override
    public void activate() {
        LeanGuiLayout.perspectiveManager.setActivePerspective(this);
    }

    @Override
    public void perspectiveActivated() {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void initialize(LeanGuiLayout leanGuiLayout, IHopMetadataProvider metadataProvider) {
        this.leanGuiLayout = leanGuiLayout;
        this.metadataProvider = metadataProvider;

        createTree();

    }

    @Override
    public List<IGuiContextHandler> getContextHandlers() {
        return null;
    }

    protected void createTree(){

        toolbar = new LeanToolbar(LeanToolbar.ORIENTATION.HORIZONTAL);
        toolbarWidgets = new GuiToolbarWidgets(leanGuiLayout.getLeanGuiLayoutId());
        toolbarWidgets.registerGuiPluginObject(this);
        toolbarWidgets.createToolbarWidgets(toolbar, GUI_PLUGIN_TOOLBAR_PARENT_ID);

        metadataTreeHolderDiv = new Div();
        metadataTreeHolderDiv.setId("metadata-treegrid-holder");
        metadataTreeHolderDiv.setSizeFull();

        metadataTree = new TreeGrid<MetadataTreeGridHelper>(MetadataTreeGridHelper.class);
        metadataTree.setHeightFull();
        metadataTreeHolderDiv.add(metadataTree);

        metadataTreeDiv.add(toolbar, metadataTreeHolderDiv);

        refresh();

    }

    public void onNewMetadata(){
        Set<MetadataTreeGridHelper> selectedItems = metadataTree.getSelectedItems();
        System.out.println("Selected items: " + selectedItems.size());
    }

    public void onNewMetadata(Class<IHopMetadata> metadataClass){
        System.out.println("Creating new " + metadataClass.getName());
        MetadataManager<IHopMetadata> manager = new MetadataManager<>(leanGuiLayout, leanGuiLayout.getVariables(), metadataProvider, metadataClass);
        manager.newMetadataWithEditor();

    }

    public void onEditMetadata(){}

    public void onEditMetadata(Class<IHopMetadata> metadataClass, String displayName){

        MetadataEditor<?> editor = this.findEditor(metadataClass.getName(), displayName);
        if(editor != null){
            this.setActiveEditor(editor);
        }else{
            try{
                MetadataManager<IHopMetadata> manager = new MetadataManager<>(leanGuiLayout, leanGuiLayout.getVariables(), metadataProvider, metadataClass);
                manager.editWithEditor(displayName);
            }catch(Exception e){
                new ErrorDialog("Error", "Error editing metadata", e);
            }
        }

    }

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_EDIT,
            toolTip = "Edit",
            image = "frontend/images/edit.svg"
    )
    public void onRenameMetadata(){}

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_DELETE,
            toolTip = "Delete",
            image = "frontend/images/delete.svg"
    )
    public void onDeleteMetadata(){}

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_DUPLICATE,
            toolTip = "Create a copy",
            image = "frontend/images/duplicate.svg"
    )
    public void duplicateMetadata(){}

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_REFRESH,
            toolTip = "Refresh",
            image = "frontend/images/refresh.svg"
    )
    public void refresh(){

        metadataTree.removeAllColumns();
        metadataProvider = LeanMetadataUtil.getInstance().metadataProvider;
        List<Class<IHopMetadata>> metadataClasses = metadataProvider.getMetadataClasses();
        HashMap<String, List<String>> metadataClassMap = new HashMap<>();

        TreeDataProvider<MetadataTreeGridHelper> metadataDataProvider = (TreeDataProvider<MetadataTreeGridHelper>)metadataTree.getDataProvider();
        TreeData<MetadataTreeGridHelper> metadataData = metadataDataProvider.getTreeData();
        metadataData.clear();

        for(Class<IHopMetadata> metadataClass : metadataClasses){
            try {
                HopMetadata annotation = LeanMetadataUtil.getInstance().getHopMetadataAnnotation(metadataClass);

                IHopMetadataSerializer<IHopMetadata> serializer = metadataProvider.getSerializer(metadataClass);
                List<String> names = serializer.listObjectNames();
                MetadataTreeGridHelper metadataTreeGridHelper = new MetadataTreeGridHelper(this, annotation.name(), annotation.image(), metadataClass);

                // add hierarchical data to TreeData (null for parent items, Class<IHopMetadata>)
                metadataData.addItem(null, metadataTreeGridHelper);
                for(String name: names){
                    metadataData.addItem(metadataTreeGridHelper, new MetadataTreeGridHelper(this, name, null, metadataClass));
                }

            }catch(HopException e){
                e.printStackTrace();
            }
        }
        metadataTree.addComponentHierarchyColumn(MetadataTreeGridHelper::getMetadataComponent);
    }

    public void addEditor(MetadataEditor<?> editor){
        PropsUi props = PropsUi.getInstance();

        Div editorDiv = new Div();
        editorDiv.setId("editor-div");
        editorDiv.setSizeFull();
//        editorDiv.setVisible(false);

        HorizontalLayout tabLayout = new HorizontalLayout();

        if(editor.getTitleImage() != null){
            Image tabImage  = editor.getTitleImage();
            tabLayout.add(tabImage);
        }
        if(StringUtils.isNotEmpty(editor.getTitle())){
            Label tabLabel = new Label(editor.getTitle());
            tabLayout.add(tabLabel);
        }else{
            Label tabLabel = new Label("New Editor");
            tabLayout.add(tabLabel);
        }
        Tab editorTab = new Tab(tabLayout);

        tabHolderDiv.add(editorDiv);
        tabsToPages.put(editorTab, editorDiv);
        metadataTabs.add(editorTab);
        metadataTabs.setSelectedTab(editorTab);

        editorsTabsMap.put(editorTab, editor);

        Button[] buttons = editor.createButtonsForButtonBar(editorDiv);
        // add buttons to bottom of editorDiv

        HorizontalLayout editorLayout = new HorizontalLayout();
        editorLayout.setSizeFull();
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setHeight("3vw");
        buttonsLayout.setWidthFull();
        if(buttons != null){
            buttonsLayout.add(buttons);
        }
        editorDiv.add(editorLayout, buttonsLayout);

        editor.createControl(editorLayout);

    }

    public void updateEditor(MetadataEditor<?> editor) {

        if (editor == null) return;

        // Update TabItem
        //
/*
        for (CTabItem item : tabFolder.getItems()) {
            if (editor.equals(item.getData())) {
                item.setText(editor.getTitle());
                if (editor.isChanged()) item.setFont(GuiResource.getInstance().getFontBold());
                else item.setFont(tabFolder.getFont());
                break;
            }
        }
*/

        // Update TreeItem
        //
        this.refresh();
    }


    public ILogChannel getLog(){
        return leanGuiLayout.getLog();
    }

    /**
     * Find a metadata editor
     *
     * @param objectKey the metadata annotation key
     * @param name the name of the metadata
     * @return the metadata editor or null if not found
     */
    public MetadataEditor<?> findEditor(String objectKey, String name) {
        if (objectKey == null || name == null) return null;

        for (MetadataEditor<?> editor : editorsTabsMap.values()) {
            IHopMetadata metadata = editor.getMetadata();
            HopMetadata annotation = HopMetadataUtil.getHopMetadataAnnotation(metadata.getClass());
            if (annotation != null
                    && annotation.key().equals(objectKey)
                    && name.equals(metadata.getName())) {
                return editor;
            }
        }
        return null;
    }

    public void setActiveEditor(MetadataEditor<?> editor){
        for(Tab editorTab : editorsTabsMap.keySet() ){
            if(editorsTabsMap.get(editorTab).equals(editor)){
                metadataTabs.setSelectedTab(editorTab);
            }
        }
    }

    public IVariables getVariables(){
        return leanGuiLayout.getVariables();
    }

    @Override
    public boolean remove(ILeanFileTypeHandler typeHandler){
        if(typeHandler instanceof MetadataEditor){
            MetadataEditor<?> editor = (MetadataEditor<?>)typeHandler;
            if(editor.isCloseable()){
                editorsTabsMap.remove(editor);

                // remove from metadataTabs
            }
        }
        return false;
    }

    public MetadataEditor<?> getActiveEditor(){
        if(metadataTabs.getSelectedTab() == null){
            return null;
        }
        return (MetadataEditor<?>) editorsTabsMap.get(metadataTabs.getSelectedTab());
    }

    @Override
    public ILeanFileTypeHandler getActiveFileTypeHandler() {
        MetadataEditor<?> editor = getActiveEditor();
        if(editor != null){
            return editor;
        }
        return null;
    }

    @Override
    public void setActiveFileTypeHandler(ILeanFileTypeHandler activeFileTypeHandler) {
        if(activeFileTypeHandler instanceof MetadataEditor){
            this.setActiveEditor((MetadataEditor<?>) activeFileTypeHandler);
        }
    }

    @Override
    public List<ILeanFileType> getSupportedLeanFileTypes() {
        return Arrays.asList(metadataFileType);
    }

}
