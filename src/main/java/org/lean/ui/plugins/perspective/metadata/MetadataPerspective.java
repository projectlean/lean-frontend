package org.lean.ui.plugins.perspective.metadata;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.ui.core.metadata.MetadataFileType;
import org.apache.hop.ui.hopgui.file.empty.EmptyFileType;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.core.metadata.LeanMetadataUtil;
import org.lean.ui.context.IGuiContextHandler;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.plugins.perspective.BasePerspective;
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
public class MetadataPerspective extends BasePerspective implements ILeanPerspective {

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

    public TreeGrid<String> metadataTree;

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

        metadataTree = new TreeGrid<>();
        metadataTree.addHierarchyColumn(String::valueOf);
        metadataTree.setHeightFull();
        // detect level, create 'new' or 'new/edit/delete' context menu.
        metadataTree.addItemClickListener(e -> System.out.println("Clicked: " + e.getItem() + " using button " + e.getButton()) );
        metadataTreeHolderDiv.add(metadataTree);

        metadataTreeDiv.add(toolbar, metadataTreeHolderDiv);

        refresh();

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

        // TODO: ok when initially built, NullPointerException when refreshed because of new instanceId.
//        metadataTreeHolderDiv.removeAll();


        metadataProvider = LeanMetadataUtil.getInstance().metadataProvider;
        List<Class<IHopMetadata>> metadataClasses = metadataProvider.getMetadataClasses();
        HashMap<String, List<String>> metadataClassMap = new HashMap<>();

        List<String> metadataClassnames = new ArrayList<>();
        TreeData<String> treeData = new TreeData<>();

        for(Class<IHopMetadata> metadataClass : metadataClasses){
            try {
                HopMetadata annotation = LeanMetadataUtil.getInstance().getHopMetadataAnnotation(metadataClass);

                IHopMetadataSerializer<IHopMetadata> serializer = metadataProvider.getSerializer(metadataClass);
                List<String> names = serializer.listObjectNames();
                if(metadataClass.getName().equals("org.lean.presentation.connector.LeanConnector")){
                    names.remove("SteelWheels");
                }
                metadataClassnames.add(annotation.name());
                metadataClassMap.put(annotation.name(), names);

            }catch(HopException e){
                e.printStackTrace();
            }
        }

        treeData.addItems(metadataClassnames, metadataItem -> metadataClassMap.get(metadataItem) != null ? metadataClassMap.get(metadataItem) : Collections.emptyList());
        TreeDataProvider<String> dataProvider = new TreeDataProvider<>(treeData);
        metadataTree.setDataProvider(dataProvider);

    }
}
