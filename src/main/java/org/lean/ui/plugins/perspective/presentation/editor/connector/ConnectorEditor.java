package org.lean.ui.plugins.perspective.presentation.editor.connector;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.LeanDatabaseConnection;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.core.metadata.LeanMetadataUtil;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.connector.IConnectorDialog;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.BaseConnectorDialog;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanConnectorPluginType;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.layout.LeanGuiLayout;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GuiPlugin(description = "The Connector handles all connectors for a presentation.")
public class ConnectorEditor extends HorizontalLayout {

    public static final String GUI_CONNECTOR_TOOLBAR_PARENT_ID = "Connectors-toolbar";
    public static final String GUI_CONNECTOR_EDIT = "Connector-edit";
    public static final String GUI_CONNECTOR_DUPLICATE = "Connector-duplicate";
    public static final String GUI_CONNECTOR_DELETE = "Connector-delete";
    public static final String GUI_CONNECTORS_REFRESH = "Connector-refresh";

    private Div connectorHolderDiv, connectorsTreeDiv, connectorsTreeHolderDiv, connectorsContentDiv;
    private Tabs connectorTabs;
    private Map<Tab, BaseConnectorDialog> tabsToPages;

    private LeanToolbar toolbar;
    private GuiToolbarWidgets toolbarWidgets;

    private LeanGuiLayout leanGuiLayout;
    private static String leanGuiLayoutId;
    private IHopMetadataProvider metadataProvider;

    private TreeGrid<ConnectorTreeGridHelper> connectorTree;

    private LeanPresentation presentation;

    public ConnectorEditor(LeanGuiLayout leanGuiLayout, LeanPresentation presentation){
        this.leanGuiLayout = leanGuiLayout;
        this.leanGuiLayoutId = leanGuiLayout.getLeanGuiLayoutId();
        this.presentation = presentation;
        this.metadataProvider = leanGuiLayout.getMetadataProvider();

        this.setSizeFull();
        this.setId("connectors-handler");

        SplitLayout connectorsSplit = new SplitLayout();
        connectorsSplit.setId("connectors-split");
        connectorsSplit.setSizeFull();

        connectorsTreeDiv = new Div();
        connectorsTreeDiv.setId("connectors-tree");
        connectorsTreeDiv.setSizeFull();

        connectorsContentDiv = new Div();
        connectorsContentDiv.setId("connectors-content");
        connectorsContentDiv.setSizeFull();

        connectorTabs = new Tabs();
        connectorTabs.setOrientation(Tabs.Orientation.HORIZONTAL);

        connectorHolderDiv = new Div();
        connectorHolderDiv.setId("connector-holder");
        connectorHolderDiv.setSizeFull();

        tabsToPages = new HashMap<>();
        connectorsContentDiv.add(connectorTabs, connectorHolderDiv);
        connectorTabs.addSelectedChangeListener(e -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(connectorTabs.getSelectedTab());
            selectedPage.setVisible(true);
        });

        connectorsSplit.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        connectorsSplit.addToPrimary(connectorsTreeDiv);
        connectorsSplit.addToSecondary(connectorsContentDiv);
        this.add(connectorsSplit);

        createTree();

    }

    private void createTree(){
        toolbar = new LeanToolbar(LeanToolbar.ORIENTATION.HORIZONTAL);
        toolbarWidgets = new GuiToolbarWidgets(leanGuiLayoutId);
        toolbarWidgets.registerGuiPluginObject(this);
        toolbarWidgets.createToolbarWidgets(toolbar, GUI_CONNECTOR_TOOLBAR_PARENT_ID);

        connectorsTreeHolderDiv = new Div();
        connectorsTreeHolderDiv.setId("connectors-tree-holder");
        connectorsTreeHolderDiv.setSizeFull();

        connectorTree = new TreeGrid<ConnectorTreeGridHelper>(ConnectorTreeGridHelper.class);
        connectorTree.setHeightFull();
        connectorsTreeHolderDiv.add(connectorTree);

        connectorsTreeDiv.add(toolbar, connectorsTreeHolderDiv);

        refresh();

    }

    public void refresh(){
        connectorTree.removeAllColumns();

        PluginRegistry registry = PluginRegistry.getInstance();
        List<IPlugin> connectorTypes = registry.getPlugins(LeanConnectorPluginType.class);

        TreeDataProvider<ConnectorTreeGridHelper> dataProvider = (TreeDataProvider<ConnectorTreeGridHelper>) connectorTree.getDataProvider();
        TreeData<ConnectorTreeGridHelper> treeData = dataProvider.getTreeData();
        treeData.clear();

        List<LeanConnector> connectors = presentation.getConnectors();

        for(IPlugin connectorPlugin: connectorTypes){
            try{
                Object clazz = PluginRegistry.getInstance().loadClass(connectorPlugin);
                ConnectorTreeGridHelper treeGridHelper = new ConnectorTreeGridHelper(this, connectorPlugin.getName(), connectorPlugin.getImageFile(), (ILeanConnector)clazz);
;               treeData.addItem(null, treeGridHelper);

                for(LeanConnector connector : connectors){
                    if(connector.getConnector() instanceof IPlugin){
                        treeData.addItem(treeGridHelper, new ConnectorTreeGridHelper(this, connectorPlugin.getName(), connectorPlugin.getImageFile(), connector.getConnector()));
                    }
                }


            }catch(Exception e){
                e.printStackTrace();
            }
        }
        connectorTree.addComponentHierarchyColumn(ConnectorTreeGridHelper::getConnectorComponent);

    }

    public void newConnector(ILeanConnector connectorType, String connectorTypeName){
        try{
            // create a new connector
            LeanConnector connector = new LeanConnector("New " + connectorTypeName, connectorType);

            // get the connector dialog
            PluginRegistry registry = PluginRegistry.getInstance();
            IPlugin plugin = registry.getPlugin(LeanConnectorPluginType.class, connector.getConnector());

            String editorClassname = "";
            if(connector.getConnector().getDialogClassname() != null){
                editorClassname = connector.getConnector().getDialogClassname();
            }else{
                String className = connector.getConnector().getClass().getCanonicalName();
                editorClassname = className + "Dialog";
            }
            Class<IConnectorDialog> dialogClass = registry.getClass(plugin, editorClassname);
            Constructor<IConnectorDialog> dialogConstructor = dialogClass.getConstructor();

            addEditor(dialogClass.newInstance());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | HopPluginException e) {
            e.printStackTrace();
        }
    }

    public void addEditor(IConnectorDialog dialog){
        System.out.println("Adding " + dialog.getClass().getCanonicalName());

        Tab connectorTab = new Tab();
        BaseConnectorDialog connectorDialog = (BaseConnectorDialog)dialog;
        connectorHolderDiv.add(connectorDialog);
        tabsToPages.put(connectorTab, connectorDialog);
        connectorTabs.add(connectorTab);
        connectorTabs.setSelectedTab(connectorTab);

        dialog.openConnector(metadataProvider, presentation, null);

        // TODO: tab handler etc
    }

    @GuiToolbarElement(
            root = GUI_CONNECTOR_TOOLBAR_PARENT_ID,
            id = GUI_CONNECTOR_EDIT,
            toolTip = "Edit",
            image = "frontend/images/edit.svg"
    )
    public void onEdit(){}

    @GuiToolbarElement(
            root = GUI_CONNECTOR_TOOLBAR_PARENT_ID,
            id = GUI_CONNECTOR_DELETE,
            toolTip = "Delete",
            image = "frontend/images/delete.svg"
    )
    public void onDelete(){}

    @GuiToolbarElement(
            root = GUI_CONNECTOR_TOOLBAR_PARENT_ID,
            id = GUI_CONNECTOR_DUPLICATE,
            toolTip = "Duplicate",
            image = "frontend/images/duplicate.svg"
    )
    public void onDuplicate(){}

    @GuiToolbarElement(
            root = GUI_CONNECTOR_TOOLBAR_PARENT_ID,
            id = GUI_CONNECTORS_REFRESH,
            toolTip = "Refresh",
            image = "frontend/images/refresh.svg"
    )
    public void onRefresh(){}
}
