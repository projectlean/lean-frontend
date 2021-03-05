package org.lean.ui.core.metadata;

import com.vaadin.flow.component.dialog.Dialog;
import org.lean.ui.core.MetadataEditor;

public class MetadataEditorDialog extends Dialog {

    private MetadataEditor<?> editor;

    public MetadataEditorDialog(MetadataEditor<?> editor){
        this.editor = editor;


    }
}
