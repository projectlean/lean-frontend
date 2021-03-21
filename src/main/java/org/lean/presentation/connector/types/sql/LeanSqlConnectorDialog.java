package org.lean.presentation.connector.types.sql;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.lean.core.LeanDatabaseConnection;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.connector.IConnectorDialog;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.type.BaseConnectorDialog;
import org.lean.presentation.connector.type.ILeanConnector;

public class LeanSqlConnectorDialog extends BaseConnectorDialog implements IConnectorDialog {

    private ComboBox<String> connectionsBox;
    private TextArea queryField;
    private LeanSqlConnector sqlConnector;

    public LeanSqlConnectorDialog(){
        super();

        connectionsBox = new ComboBox<>();
        queryField = new TextArea();
        queryField.setSizeFull();

        connectorForm.add(connectionsBox, 2);
        connectorForm.addFormItem(queryField, "Query");

    }

    @Override
    public void openConnector(IHopMetadataProvider metadataProvider, LeanPresentation presentation, ILeanConnector connector){
        super.openConnector(metadataProvider, presentation, connector);

        try {
            IHopMetadataSerializer<LeanDatabaseConnection> dbSerializer = metadataProvider.getSerializer(LeanDatabaseConnection.class);
            connectionsBox.setItems(dbSerializer.listObjectNames());
            sqlConnector = (LeanSqlConnector) connector;
//            connectionsBox.setValue(sqlConnector.getDatabaseConnectionName());
//            queryField.setValue(sqlConnector.getSql());
        } catch (HopException e) {
            e.printStackTrace();
        }


    }

    @Override
    public LeanConnector getConnector(){
        LeanConnector connector = super.getConnector();
        if(sqlConnector == null){
            sqlConnector = new LeanSqlConnector();
        }
        sqlConnector.setDatabaseConnectionName(connectionsBox.getValue());
        sqlConnector.setSql(queryField.getValue());
        connector.setConnector(sqlConnector);
        return connector;
    }
}
