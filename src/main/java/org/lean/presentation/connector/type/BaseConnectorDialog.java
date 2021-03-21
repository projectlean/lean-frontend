package org.lean.presentation.connector.type;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.hop.core.row.RowMeta;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.connector.IConnectorDialog;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.LeanRow;
import org.lean.presentation.datacontext.PresentationDataContext;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseConnectorDialog extends Composite<VerticalLayout> implements IConnectorDialog {

    private SplitLayout connectorSplit;
    private Grid<LeanRow> connectorPreview;
    public TextField fConnectorName;
    public Checkbox cbShared;
    public Button bPreview;

    private VerticalLayout baseForm;
    public FormLayout connectorForm;
    public IHopMetadataProvider metadataProvider;
    public LeanPresentation presentation;
    public ILeanConnector connector;

    public BaseConnectorDialog(){
        getContent().setSizeFull();

        connectorSplit = new SplitLayout();
        connectorSplit.setId("connector-split");
        connectorSplit.setSizeFull();
        connectorSplit.setOrientation(SplitLayout.Orientation.VERTICAL);

        baseForm = new VerticalLayout();
        connectorForm = new FormLayout();

        fConnectorName = new TextField();
        cbShared = new Checkbox("Shared");

        bPreview = new Button("Preview");
        bPreview.addClickListener(e -> { doPreview(); });

        baseForm.add(new HorizontalLayout(fConnectorName, cbShared), connectorForm, bPreview);
        connectorPreview = new Grid<LeanRow>();

        connectorSplit.addToPrimary(baseForm);
        connectorSplit.addToSecondary(connectorPreview);

        getContent().add(connectorSplit);
    }

    @Override
    public void openConnector(IHopMetadataProvider metadataProvider, LeanPresentation presentation, ILeanConnector connector) {
        this.metadataProvider = metadataProvider;
        this.presentation = presentation;
        this.connector = connector;
    }

    public void doPreview(){
        AtomicInteger rowCounter = new AtomicInteger( 0 );
        AtomicBoolean endReceived = new AtomicBoolean( false );

        if(presentation != null){
            connectorPreview.removeAllColumns();
            ArrayList<LeanRow> rows = new ArrayList<>();
            LeanConnector theConnector = getConnector();
            LeanBaseConnector baseConnector = (LeanBaseConnector) theConnector.getConnector();
            try {
                baseConnector.addRowListener((rowMeta, rowData) -> {
                    if(rowMeta != null && rowData != null){
                        rowCounter.incrementAndGet();
                        rows.add(new LeanRow(rowData));
                    }
                    if(rowMeta == null && rowData == null){
                        endReceived.set(true);
                    }
                });
                connectorPreview.setItems(rows);

                // create a temporary presentation
                LeanPresentation tmpPresentation = new LeanPresentation();
                PresentationDataContext dataContext = new PresentationDataContext(presentation, metadataProvider);

                baseConnector.startStreaming(dataContext);
                baseConnector.waitUntilFinished();

                RowMeta rowMeta = (RowMeta)theConnector.describeOutput(dataContext);
                String[] fieldNames = rowMeta.getFieldNames();

                for(int i=0; i < fieldNames.length; i++){
                    int rowIndex = i;
                    connectorPreview.addColumn(leanRow -> leanRow.getItem(rowIndex)).setHeader(fieldNames[i]);
                }
            }catch(LeanException e){
                e.printStackTrace();
            }
        }
    }

    public LeanConnector getConnector(){
        LeanConnector connector = new LeanConnector();
        connector.setName(fConnectorName.getValue());
        connector.setShared(cbShared.getValue());
        return connector;
    }


}
