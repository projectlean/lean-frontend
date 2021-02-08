package org.lean.core;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import org.apache.xmlgraphics.xmp.Metadata;
import org.lean.core.LeanDatabaseConnection;
import org.lean.ui.core.MetadataEditor;
import org.lean.ui.core.metadata.IMetadataEditor;
import org.lean.ui.core.metadata.MetadataManager;
import org.lean.ui.plugins.perspective.metadata.MetadataPerspective;

public class LeanDatabaseConnectionEditor extends MetadataEditor<LeanDatabaseConnection> implements IMetadataEditor {

    public LeanDatabaseConnectionEditor(MetadataPerspective metadataPerspective, MetadataManager<LeanDatabaseConnection> manager, LeanDatabaseConnection connection){
        super(metadataPerspective, manager, connection);
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("Connection: " + connection.getName());
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("######################################");
        System.out.println("######################################");
    }

    @Override
    public void setWidgetsContent() {

    }

    @Override
    public void getWidgetsContent(LeanDatabaseConnection meta) {

    }

    @Override
    public void createControl(Component editorComponent){

        Div div = (Div)editorComponent;
        div.add(new Label("DFGDFASDFADSFADSFSADFASDFASDFASDFASDF"));

    }
}
