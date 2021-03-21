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
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.gui.plugin.key.GuiKeyboardShortcut;
import org.apache.hop.core.gui.plugin.key.GuiOsxKeyboardShortcut;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElementType;
import org.lean.core.metadata.LeanMetadataUtil;
import org.lean.ui.LeanGui;
import org.lean.ui.core.metadata.MetadataManager;
import org.lean.ui.leangui.context.IActionContextHandlersProvider;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.leangui.context.metadata.MetadataContext;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.leangui.delegates.LeanGuiContextDelegate;
import org.lean.ui.leangui.delegates.LeanGuiFileDelegate;
import org.lean.ui.plugins.file.ILeanFileType;
import org.lean.ui.plugins.file.LeanFileTypeRegistry;
import org.lean.ui.plugins.perspective.*;
import org.lean.ui.plugins.perspective.presentation.PresentationPerspective;
import org.lean.ui.views.MainBody;

import java.util.*;

@RoutePrefix("lean")
@Route(value = "lean")
@ParentLayout(value = LeanGui.class)
@CssImport(value = "./styles/toolbar-tabs.css", themeFor = "vaadin-tab")
@CssImport(value = "./styles/lean-layout.css")
@GuiPlugin(description = "This is the main Lean UI")
@UIScope
public class LeanGuiLayout extends Composite<Div> implements RouterLayout, IActionContextHandlersProvider {

    public static final String DEFAULT_LEAN_GUI_NAMESPACE = "lean-gui";

    public static final String APP_NAME = "Lean";

//    private static LeanGuiLayout leanGuiLayout;

    public static String leanGuiLayoutId; //UUID.randomUUID().toString();

    private VerticalLayout leanMainVerticalLayout;

    public MainBody mainBody;

    public static LeanPerspectiveManager perspectiveManager;
    private ILeanPerspective activePerspective;

    private IHopMetadataProvider metadataProvider;

    public MetadataManager<DatabaseMeta> databaseMetaManager;

    private Map<Class<? extends ILeanPerspective>, ILeanPerspective> perspectivesMap;

    private IVariables variables;

    private ILoggingObject loggingObject;
    private ILogChannel log;

    private LeanToolbar leanMainToolbar, perspectivesToolbar;
//    private LeanToolbar leanToolbar;
    private GuiToolbarWidgets toolbarWidgets;


    public static final String ID_MAIN_TOOLBAR = "LeanGui-Toolbar";
    public static final String ID_MAIN_TOOLBAR_NEW_LABEL = "toolbar-10010-new-label";
    public static final String ID_MAIN_TOOLBAR_NEW = "toolbar-10020-new";
    public static final String ID_MAIN_TOOLBAR_OPEN = "toolbar-10030-open";
    public static final String ID_MAIN_TOOLBAR_SAVE = "toolbar-10040-save";
    public static final String ID_MAIN_TOOLBAR_SAVE_AS = "toolbar-10050-save-as";

    public static LeanGuiContextDelegate contextDelegate;
    public LeanGuiFileDelegate fileDelegate;


    public LeanGuiLayout(){
        getContent().setId("lean-gui-layout");
        getContent().setSizeFull();

//        leanGuiLayoutId = String.valueOf(System.identityHashCode(this)); //String.valueOf(VaadinSession.getCurrent().hashCode());
        leanGuiLayoutId = String.valueOf(UI.getCurrent().hashCode());

        // TODO: move variables and metadataprovider to singleton
        LeanMetadataUtil leanMetadataUtil = LeanMetadataUtil.getInstance();
        variables = leanMetadataUtil.getInstance().variables;
        metadataProvider = leanMetadataUtil.getInstance().metadataProvider;

        perspectiveManager = new LeanPerspectiveManager(this, metadataProvider);
        contextDelegate = new LeanGuiContextDelegate(this);

        loggingObject = new LoggingObject(APP_NAME);
        log = new LogChannel(APP_NAME);

        fileDelegate = new LeanGuiFileDelegate(this);

        leanMainVerticalLayout = new VerticalLayout();
        leanMainVerticalLayout.setId("lean-main-vertical-layout");
        leanMainVerticalLayout.setSizeFull();
        leanMainVerticalLayout.setPadding(false);
        leanMainVerticalLayout.setSpacing(false);
        leanMainVerticalLayout.setMargin(false);
        HorizontalLayout bodyHL = new HorizontalLayout();
        bodyHL.setId("lean-main-horizontal-layout");
        bodyHL.setPadding(false);
        bodyHL.setSpacing(false);
        bodyHL.setMargin(false);
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
     * Gets the unique id of this LeanGui instance
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
        databaseMetaManager = new MetadataManager<>(this, variables, metadataProvider, DatabaseMeta.class);
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
                perspectiveManager.setActivePerspective(perspective);
                activePerspective = perspective;
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
        fileDelegate.fileOpen();
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
        fileDelegate.fileSave();
    }

    @GuiToolbarElement(
            root = ID_MAIN_TOOLBAR,
            id = ID_MAIN_TOOLBAR_SAVE_AS,
            type = GuiToolbarElementType.BUTTON,
            image = "frontend/images/save-as.svg",
            toolTip = "Save As"
    )
    public void menuFileSaveAs(){ fileDelegate.fileSaveAs(); }

    @Override
    public void showRouterLayoutContent(HasElement hasElement) {
        Objects.requireNonNull(hasElement);
        Objects.requireNonNull(hasElement.getElement());
        mainBody.removeAll();
        mainBody.getElement().appendChild(hasElement.getElement());
    }

    /**
     * Gets the variables
     *
     * @return value of variables
     */
    public IVariables getVariables(){
        return variables;
    }

    /**
     * Gets log
     *
     * @return value of log
     */
    public ILogChannel getLog() {
        return log;
    }

    /**
     * Gets activePerspective
     *
     * @return value of activePerspective
     */
    public ILeanPerspective getActivePerspective() {
        return activePerspective;
    }

    public LeanPerspectiveManager getPerspectiveManager(){
        return perspectiveManager;
    }

    @Override
    public List<IGuiContextHandler> getContextHandlers() {
        List<IGuiContextHandler> contextHandlers = new ArrayList<>();

        // Get all file context handlers
        //
        LeanFileTypeRegistry registry = LeanFileTypeRegistry.getInstance();
        List<ILeanFileType> leanFileTypes = registry.getFileTypes();
        for(ILeanFileType leanFileType : leanFileTypes){
            contextHandlers.addAll( leanFileType.getContextHandlers(this));
        }

        // Get all the metadata context handlers
        //
        contextHandlers.addAll(new MetadataContext(this, metadataProvider).getContextHandlers());

        return contextHandlers;
    }

    public static PresentationPerspective getPresentationPerspective(){
        return (PresentationPerspective) perspectiveManager.findPerspective(PresentationPerspective.class);
    }

    /**
     * We're given a bunch of capabilities from {@link ILeanFileType} In this method we'll
     * enable/disable menu and toolbar items
     *
     * @param fileType The type of file to handle giving you its capabilities to take into account
     *     from {@link ILeanFileType} or set by a plugin
     * @param running set this to true if the current file is running
     * @param paused set this to true if the current file is paused
     */
    public void handleFileCapabilities(ILeanFileType fileType, boolean running, boolean paused){

    }
}
