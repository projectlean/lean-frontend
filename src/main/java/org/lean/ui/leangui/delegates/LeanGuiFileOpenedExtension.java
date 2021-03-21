package org.lean.ui.leangui.delegates;

import org.apache.hop.core.variables.IVariables;
import org.lean.ui.core.dialog.IFileDialog;

public class LeanGuiFileOpenedExtension {
    public IFileDialog fileDialog;
    public IVariables variables;
    public String filename;

    public LeanGuiFileOpenedExtension(IFileDialog fileDialog, IVariables variables, String filename ) {
        this.fileDialog = fileDialog;
        this.variables = variables;
        this.filename = filename;
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

    /**
     * Gets variables
     *
     * @return value of variables
     */
    public IVariables getVariables() {
        return variables;
    }

    /**
     * @param variables The variables to set
     */
    public void setVariables( IVariables variables ) {
        this.variables = variables;
    }

    /**
     * Gets filename
     *
     * @return value of filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename The filename to set
     */
    public void setFilename( String filename ) {
        this.filename = filename;
    }
}
