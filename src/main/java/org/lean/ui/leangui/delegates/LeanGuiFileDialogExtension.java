package org.lean.ui.leangui.delegates;

import org.lean.ui.core.dialog.IFileDialog;

import java.util.concurrent.atomic.AtomicBoolean;

public class LeanGuiFileDialogExtension {

    public AtomicBoolean doIt;
    public IFileDialog fileDialog;

    public LeanGuiFileDialogExtension(AtomicBoolean doIt, IFileDialog fileDialog){
        this.doIt = doIt;
        this.fileDialog = fileDialog;
    }

    /**
     * Gets doIt
     *
     * @return value of doIt
     */
    public AtomicBoolean getDoIt() {
        return doIt;
    }

    /**
     * @param doIt The doIt to set
     */
    public void setDoIt( AtomicBoolean doIt ) {
        this.doIt = doIt;
    }

    /**
     * Gets fileDialog
     *
     * @return value of fileDialog
     */
    public IFileDialog getFileDialog() {
        return fileDialog;
    }

    /**
     * @param fileDialog The fileDialog to set
     */
    public void setFileDialog( IFileDialog fileDialog ) {
        this.fileDialog = fileDialog;
    }
}
