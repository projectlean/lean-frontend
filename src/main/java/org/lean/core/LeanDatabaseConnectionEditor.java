package org.lean.core;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.hop.core.Const;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.database.DatabasePluginType;
import org.apache.hop.core.database.DatabaseTestResults;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.i18n.BaseMessages;
import org.apache.xmlgraphics.xmp.Metadata;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;
import org.lean.core.LeanDatabaseConnection;
import org.lean.core.exception.LeanException;
import org.lean.ui.core.MetadataEditor;
import org.lean.ui.core.dialog.ShowMessageDialog;
import org.lean.ui.core.metadata.IMetadataEditor;
import org.lean.ui.core.metadata.MetadataManager;
import org.lean.ui.plugins.perspective.metadata.MetadataPerspective;

import javax.xml.ws.handler.HandlerResolver;
import java.util.Arrays;
import java.util.List;

public class LeanDatabaseConnectionEditor extends MetadataEditor<LeanDatabaseConnection> implements IMetadataEditor {

    private static final Class<?> PKG = LeanDatabaseConnectionEditor.class;

    private TextField connTfName, connTfUsername, connTfHostname, connTfPort, connTfDbName;
    private ComboBox<String> connCbType;
    private PasswordField connPwPassword;

    public LeanDatabaseConnectionEditor(MetadataPerspective metadataPerspective, MetadataManager<LeanDatabaseConnection> manager, LeanDatabaseConnection connection){
        super(metadataPerspective, manager, connection);

    }

    @Override
    public void setWidgetsContent() {

    }

    @Override
    public void getWidgetsContent(LeanDatabaseConnection meta) {

    }

    @Override
    public void createControl(Component editorComponent){

        FormLayout dbEditorLayout = new FormLayout();
        connTfName = new TextField("Connection name");
        Hr connHr = new Hr();
        connCbType = new ComboBox("Connection type");
        connCbType.setItems(getConnectionTypes());
        connTfUsername = new TextField("Username");
        connPwPassword = new PasswordField("Password");
        Hr connHrOptions = new Hr();
        connTfHostname = new TextField("Server hostname");
        connTfPort = new TextField("Port number");
        connTfDbName = new TextField("Database name");

        dbEditorLayout.add(connTfName, connHr, connCbType, connTfUsername, connPwPassword, connHrOptions, connTfHostname, connTfPort, connTfDbName);
        dbEditorLayout.setColspan(connTfName, 2);
        dbEditorLayout.setColspan(connHr, 2);
        dbEditorLayout.setColspan(connCbType, 2);
        dbEditorLayout.setColspan(connHrOptions, 2);

        // add without cast
        ((HorizontalLayout)editorComponent).add(dbEditorLayout);


    }

    private String[] getConnectionTypes(){

        PluginRegistry registry = PluginRegistry.getInstance();
        List<IPlugin> plugins = registry.getPlugins(DatabasePluginType.class);
        String[] types = new String[plugins.size()];
        for(int i=0; i < types.length; i++){
            types[i] = plugins.get(i).getName();
        }
        Arrays.sort(types, String.CASE_INSENSITIVE_ORDER);
        return types;
    }

    @Override
    public Button[] createButtonsForButtonBar(Component component){

        Button testButton = new Button("Test");
        testButton.addClickListener(e -> test());

        return new Button[]{testButton};

    }

    private void test() {

        LeanDatabaseConnection testConnection = new LeanDatabaseConnection(connTfName.getValue(), connCbType.getValue(),
                connTfHostname.getValue(), connTfPort.getValue(), connTfDbName.getValue(), connTfUsername.getValue(), connPwPassword.getValue());
        try {
            DatabaseMeta testMeta = testConnection.createDatabaseMeta();
            DatabaseTestResults testResults = testMeta.testConnectionSuccess(metadataPerspective.getVariables());
            String message = testResults.getMessage();
            System.out.println("Test message: " + message);
            boolean success = testResults.isSuccess();

            String title = success ? BaseMessages.getString( PKG, "DatabaseDialog.DatabaseConnectionTestSuccess.title" )
                    : BaseMessages.getString( PKG, "DatabaseDialog.DatabaseConnectionTest.title" );
//            if ( success && message.contains( Const.CR ) ) {
//                message.replaceAll(Const.CR, "<br />");
//                message = message.substring( 0, message.indexOf( Const.CR ) )
//                        + "<br />" + message.substring( message.indexOf( Const.CR ) );
//                message = message.substring( 0, message.lastIndexOf( Const.CR ) );
//            }
            ShowMessageDialog msgDialog = new ShowMessageDialog(VaadinIcon.INFO.ordinal() | VaadinIcon.CHECK.ordinal(),
                    title, message, message.length() > 300 );
            msgDialog.setType( success ? Const.SHOW_MESSAGE_DIALOG_DB_TEST_SUCCESS
                    : Const.SHOW_MESSAGE_DIALOG_DB_TEST_DEFAULT );
            msgDialog.open();


        }catch(LeanException e){
            System.out.print("Error " + e.getMessage());
            e.printStackTrace();
        }
    }
}
