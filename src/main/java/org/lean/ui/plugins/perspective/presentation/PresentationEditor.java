package org.lean.ui.plugins.perspective.presentation;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.variables.IVariables;
//import org.lean.core.gui.plugin.GuiPlugin;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.presentation.LeanPresentation;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.plugins.file.ILeanFileType;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;
import org.lean.ui.plugins.file.presentation.LeanPresentationFileType;
import org.lean.ui.plugins.perspective.presentation.editor.connector.ConnectorEditor;
import org.lean.ui.plugins.perspective.presentation.editor.layout.LayoutEditor;
import org.lean.ui.plugins.perspective.presentation.editor.theme.ThemeEditor;

import java.util.List;
import java.util.Map;

@GuiPlugin(description = "The presentation handler takes care of viewing and editing presentations")
public class PresentationEditor extends Composite<HorizontalLayout> implements ILeanFileTypeHandler {

    public static final String GUI_PRESHANDLER_TOOLBAR_PARENT_ID = "Presentation-Handler-Toolbar";
    public static final String TOOLBAR_PRES_VIEW = "Presentation-Handler-View";
    public static final String TOOLBAR_PRES_EDIT = "Presentation-Handler-Edit";
    public static final String TOOLBAR_PRES_CONNECTOR = "Presentation-Handler-Connector";
    public static final String TOOLBAR_PRES_COMPONENT = "Presentation-Handler-Component";
    public static final String TOOLBAR_PRES_THEME = "Presentation-Handler-Theme";

    public LeanGuiLayout leanGuiLayout;
    private static String leanGuiLayoutId;

    public LeanToolbar presToolbar;
    public GuiToolbarWidgets toolbarWidgets;

    public VerticalLayout presentationNavBar;

    private ConnectorEditor connectorEditor;
    private LayoutEditor layoutEditor;
    private ThemeEditor themeEditor;

    private LeanPresentation presentation;
    private LeanPresentationFileType fileType;

    public PresentationEditor(LeanGuiLayout leanGuiLayout, LeanPresentation presentation){

        this.leanGuiLayout = leanGuiLayout;
        this.leanGuiLayoutId = leanGuiLayout.getLeanGuiLayoutId();
        this.presentation = presentation;

        fileType = new LeanPresentationFileType();

        this.getContent().setId("presentation-handler");
        this.getContent().setSizeFull();

        presentationNavBar = new VerticalLayout();
        presentationNavBar.setWidth(ConstUi.VBAR_WIDTH);
        presentationNavBar.setHeightFull();
        presentationNavBar.setId("presentation-navbar");

        createToolbar();

        connectorEditor = new ConnectorEditor(leanGuiLayout, presentation);
        connectorEditor.setId("connector-handler");
        connectorEditor.setSizeFull();
        connectorEditor.setVisible(false);

        layoutEditor = new LayoutEditor();
        layoutEditor.setId("layout-handler");
        layoutEditor.setSizeFull();
        layoutEditor.setVisible(false);

        themeEditor = new ThemeEditor();
        themeEditor.setId("theme-handler");
        themeEditor.setSizeFull();
        themeEditor.setVisible(false);

        getContent().add(presentationNavBar, connectorEditor, layoutEditor, themeEditor);

    }



    @Override
    public List<IGuiContextHandler> getContextHandlers() {
        return null;
    }

    @Override
    public Object getSubject() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public ILeanFileType getFileType() {
        return fileType;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public void setFilename(String filename) {

    }

    @Override
    public void save() throws HopException {

    }

    @Override
    public void saveAs(String filename) throws HopException {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void preview() {

    }

    @Override
    public void debug() {

    }

    @Override
    public void redraw() {

    }

    @Override
    public void updateGui() {

    }

    @Override
    public void selectAll() {

    }

    @Override
    public void unselectAll() {

    }

    @Override
    public void copySelectedToClipboard() {

    }

    @Override
    public void cutSelectedToClipboard() {

    }

    @Override
    public void deleteSelected() {

    }

    @Override
    public void pasteFromClipboard() {

    }

    @Override
    public boolean isCloseable() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasChanged() {
        return false;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public Map<String, Object> getStateProperties() {
        return null;
    }

    @Override
    public void applyStateProperties(Map<String, Object> stateProperties) {

    }

    @Override
    public IVariables getVariables() {
        return null;
    }

    public void createToolbar(){
        presToolbar = new LeanToolbar(LeanToolbar.ORIENTATION.VERTICAL);
        presToolbar.setId("presentation-toolbar");

        toolbarWidgets = new GuiToolbarWidgets(leanGuiLayoutId);
        toolbarWidgets.registerGuiPluginObject(this);
        toolbarWidgets.createToolbarWidgets(presToolbar, GUI_PRESHANDLER_TOOLBAR_PARENT_ID);

        presentationNavBar.add(presToolbar);
    }

    @GuiToolbarElement(
            root = GUI_PRESHANDLER_TOOLBAR_PARENT_ID,
            id = TOOLBAR_PRES_EDIT,
            toolTip = "View/Edit",
            image = "frontend/images/view.svg"
    )
    public void onToggleView(){

    }

    @GuiToolbarElement(
            root = GUI_PRESHANDLER_TOOLBAR_PARENT_ID,
            id = TOOLBAR_PRES_CONNECTOR,
            toolTip = "Connectors",
            image = "frontend/images/connector.svg"
    )
    public void onConnector(){
        toggleView(connectorEditor);
    }

    @GuiToolbarElement(
            root = GUI_PRESHANDLER_TOOLBAR_PARENT_ID,
            id = TOOLBAR_PRES_COMPONENT,
            toolTip = "Components",
            image = "frontend/images/component.svg"
    )
    public void onComponent(){
        toggleView(layoutEditor);
    }

    @GuiToolbarElement(
            root = GUI_PRESHANDLER_TOOLBAR_PARENT_ID,
            id = TOOLBAR_PRES_THEME,
            toolTip = "Theme",
            image = "frontend/images/theme.svg"
    )
    public void onTheme(){
        toggleView(themeEditor);
    }


    public void toggleView(HorizontalLayout layout){
        if(layout instanceof ConnectorEditor){
            connectorEditor.setVisible(true);
            layoutEditor.setVisible(false);
            themeEditor.setVisible(false);
        }else if(layout instanceof LayoutEditor){
            connectorEditor.setVisible(false);
            layoutEditor.setVisible(true);
            themeEditor.setVisible(false);
        }else if(layout instanceof ThemeEditor){
            connectorEditor.setVisible(false);
            layoutEditor.setVisible(false);
            themeEditor.setVisible(true);
        }

    }
}
