package org.lean.ui.plugins.perspective.presentation.editor.connector;

import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.apache.commons.lang.StringUtils;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.ui.core.ConstUi;

public class ConnectorTreeGridHelper {

    private ConnectorHandler connectorHandler;
    private ILeanConnector connector;
    private String displayName, image;
    private Image connectorImage;

    public ConnectorTreeGridHelper(ConnectorHandler connectorHandler, String displayName, String image, ILeanConnector connector){
        this.connectorHandler = connectorHandler;
        this.image = image;
        this.connector = connector;
        this.displayName = displayName;

        if(StringUtils.isNotEmpty(image)){
            connectorImage = new Image(image, "");
            connectorImage.setHeight(ConstUi.SMALL_ICON_SIZE_PX);
            connectorImage.setWidth(ConstUi.SMALL_ICON_SIZE_PX);
        }
    }

    public HorizontalLayout getConnectorComponent(){
        HorizontalLayout connectorLayout = new HorizontalLayout();
        connectorLayout.setSizeFull();
        connectorLayout.setId("connector-layout");

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(connectorLayout);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("New", e -> connectorHandler.newConnector(connector, displayName));

        if(StringUtils.isNotEmpty(image)){
            connectorLayout.add(connectorImage);
        }else{
            contextMenu.addItem("Edit", e -> {});
            contextMenu.addItem("Rename", e -> {});
            contextMenu.addItem("Duplicate", e -> {});
            contextMenu.addItem("Delete", e -> {});
        }
        connectorLayout.add(new Label(displayName));
        return connectorLayout;
    }
}
