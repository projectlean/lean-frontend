package org.lean.ui.layout;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.gui.plugin.key.GuiKeyboardShortcut;
import org.apache.hop.core.gui.plugin.key.GuiOsxKeyboardShortcut;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.ui.core.metadata.MetadataManager;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElementType;
import org.lean.core.metadata.LeanMetadataUtil;
import org.lean.ui.LeanGui;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.delegates.LeanGuiContextDelegate;
import org.lean.ui.plugins.perspective.*;
import org.lean.ui.views.MainBody;

import java.util.Map;
import java.util.Objects;

@RoutePrefix("lean")
@Route(value = "lean")
@ParentLayout(value = LeanGui.class)
@CssImport(value = "./styles/toolbar-tabs.css", themeFor = "vaadin-tab")
@CssImport(value = "./styles/lean-layout.css")
@GuiPlugin(description = "This is the main Lean UI")
@VaadinSessionScope
public class LeanGuiLayout extends Composite<Div> implements RouterLayout{

    private static LeanGuiLayout leanGuiLayout;

    public static String leanGuiLayoutId;

    private VerticalLayout leanMainVerticalLayout;

    public MainBody mainBody;

    public static LeanPerspectiveManager perspectiveManager;

    private IHopMetadataProvider metadataProvider;

    public MetadataManager<DatabaseMeta> databaseMetaManager;

    private Map<Class<? extends ILeanPerspective>, ILeanPerspective> perspectivesMap;

    private IVariables variables;

    private LeanToolbar leanMainToolbar, perspectivesToolbar;

    public static final String ID_MAIN_TOOLBAR = "LeanGui-Toolbar";
    public static final String ID_MAIN_TOOLBAR_NEW_LABEL = "toolbar-10010-new-label";
    public static final String ID_MAIN_TOOLBAR_NEW = "toolbar-10020-new";
    public static final String ID_MAIN_TOOLBAR_OPEN = "toolbar-10030-open";
    public static final String ID_MAIN_TOOLBAR_SAVE = "toolbar-10040-save";
    public static final String ID_MAIN_TOOLBAR_SAVE_AS = "toolbar-10050-save-as";

    private LeanToolbar leanToolbar;
    private GuiToolbarWidgets toolbarWidgets;
    public LeanGuiContextDelegate contextDelegate;




    public LeanGuiLayout(){
        leanGuiLayoutId = String.valueOf(VaadinSession.getCurrent().hashCode());

        // TODO: move variables and metadataprovider to singleton
        LeanMetadataUtil leanMetadataUtil = LeanMetadataUtil.getInstance();
        variables = leanMetadataUtil.getInstance().variables;
        metadataProvider = leanMetadataUtil.getInstance().metadataProvider;

        getContent().setId("lean-gui-layout");
        getContent().setSizeFull();

        leanMainVerticalLayout = new VerticalLayout();
        leanMainVerticalLayout.setId("lean-main-vertical-layout");
        leanMainVerticalLayout.setSizeFull();
        HorizontalLayout bodyHL = new HorizontalLayout();
        bodyHL.setId("lean-main-horizontal-layout");
        bodyHL.setSizeFull();

        perspectivesToolbar = new LeanToolbar(LeanToolbar.ORIENTATION.VERTICAL);
        perspectivesToolbar.setId("perspectives-toolbar");

        mainToolbar();

        mainBody = new MainBody();
        mainBody.setId("lean-main-body");
        mainBody.setSizeFull();

        bodyHL.add(perspectivesToolbar, mainBody);
        leanMainVerticalLayout.add(leanMainToolbar, bodyHL);

        getContent().add(leanMainVerticalLayout);

        perspectiveManager = new LeanPerspectiveManager(this, metadataProvider);
        loadPerspectives();

    }

    public void mainToolbar(){
        leanMainToolbar = new LeanToolbar();
        leanMainToolbar.getContent().setWidthFull();
        leanMainToolbar.getContent().setHeight(ConstUi.HBAR_HEIGHT);

        toolbarWidgets = new GuiToolbarWidgets(leanGuiLayoutId);
        toolbarWidgets.registerGuiPluginObject(this);
        toolbarWidgets.createToolbarWidgets(leanMainToolbar, ID_MAIN_TOOLBAR);
    }

    /**
     * Gets the unique id of this HopGui instance
     *
     * @return value of id
     */
    public String getLeanGuiLayoutId() {
        return leanGuiLayoutId;
    }

    /**
     * Gets metadataProvider
     *
     * @return value of metadataProvider
     */
    public IHopMetadataProvider getMetadataProvider() {
        return metadataProvider;
    }

    /** @param metadataProvider The metadataProvider to set */
    public void setMetadataProvider(IHopMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
        updateMetadataManagers();
    }

    private void updateMetadataManagers() {
        databaseMetaManager = new MetadataManager<>(variables, metadataProvider, DatabaseMeta.class);
    }

    private void loadPerspectives(){
        // TODO: perspectives order/priority
        perspectivesMap = perspectiveManager.getPerspectives();
        for(ILeanPerspective perspective : perspectivesMap.values()){

            Image perspectiveIcon = new Image(perspective.getClass().getAnnotation(LeanPerspectivePlugin.class).image(), perspective.getClass().getAnnotation(LeanPerspectivePlugin.class).name());
            perspectiveIcon.setWidth("1.3vw");
            Component perspectiveComponent = (Component)perspective;
            perspectiveComponent.setVisible(false);
            Button perspectiveButton = new Button(perspectiveIcon);
            perspectiveButton.addClickListener(e -> {
                mainBody.removeAll();
                mainBody.add((Component) perspective);
                ((Component)perspective).setVisible(true);
            });
            mainBody.add(perspectiveComponent);
            perspectivesToolbar.add(perspectiveButton);

        }
    }

    @GuiToolbarElement(
            root = ID_MAIN_TOOLBAR,
            id = ID_MAIN_TOOLBAR_NEW,
            type = GuiToolbarElementType.BUTTON,
            image = "frontend/images/new.svg",
            toolTip = "Create New"
    )
    @GuiKeyboardShortcut(control = true, key = 'n')
    @GuiOsxKeyboardShortcut(command = true, key = 'n')
    public void menuFileNew(){
        contextDelegate.fileNew();
    }

    @GuiToolbarElement(
            root = ID_MAIN_TOOLBAR,
            id = ID_MAIN_TOOLBAR_OPEN,
            type = GuiToolbarElementType.BUTTON,
            image = "frontend/images/open.svg",
            toolTip = "Open"
    )
    @GuiKeyboardShortcut(control = true, key = 'n')
    @GuiOsxKeyboardShortcut(command = true, key = 'n')
    public void menuFileOpen(){
        contextDelegate.fileNew();
    }

    @GuiToolbarElement(
            root = ID_MAIN_TOOLBAR,
            id = ID_MAIN_TOOLBAR_SAVE,
            type = GuiToolbarElementType.BUTTON,
            image = "frontend/images/save.svg",
            toolTip = "Save"
    )
    @GuiKeyboardShortcut(control = true, key = 'n')
    @GuiOsxKeyboardShortcut(command = true, key = 'n')
    public void menuFileSave(){
        contextDelegate.fileNew();
    }

    @Override
    public void showRouterLayoutContent(HasElement hasElement) {
        Objects.requireNonNull(hasElement);
        Objects.requireNonNull(hasElement.getElement());
        mainBody.removeAll();
        mainBody.getElement().appendChild(hasElement.getElement());
    }

}
