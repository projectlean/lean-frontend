package org.lean.ui.core.dialog;

import com.vaadin.flow.component.dialog.Dialog;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.hop.core.extension.ExtensionPointHandler;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.vfs.HopVfs;
import org.apache.hop.i18n.BaseMessages;
//import org.apache.hop.ui.core.dialog.IFileDialog;
//import org.apache.hop.ui.core.dialog.NativeFileDialog;
//import org.apache.hop.ui.core.vfs.HopVfsFileDialog;
//import org.apache.hop.ui.hopgui.HopGui;
import org.apache.hop.ui.hopgui.HopGuiExtensionPoint;
//import org.apache.hop.ui.hopgui.delegates.HopGuiFileDialogExtension;
//import org.apache.hop.ui.hopgui.delegates.HopGuiFileOpenedExtension;
import org.lean.ui.core.PropsUi;
import org.lean.ui.core.component.TextVar;
import org.lean.ui.core.vfs.LeanVfsFileDialog;
import org.lean.ui.leangui.delegates.LeanGuiFileDialogExtension;
import org.lean.ui.leangui.delegates.LeanGuiFileOpenedExtension;
import org.lean.ui.layout.LeanGuiLayout;

import java.util.concurrent.atomic.AtomicBoolean;

public class BaseDialog extends Dialog {

    private static final Class<?> PKG = BaseDialog.class;

    public static final int MARGIN_SIZE = 15;
    public static final int LABEL_SPACING = 5;
    public static final int ELEMENT_SPACING = 10;
    public static final int MEDIUM_FIELD = 250;
    public static final int MEDIUM_SMALL_FIELD = 150;
    public static final int SMALL_FIELD = 50;
    public static final int SHELL_WIDTH_OFFSET = 16;

    protected PropsUi props;
    protected int width = -1;
    protected String title;

    private int footerTopPadding = org.apache.hop.ui.core.dialog.BaseDialog.ELEMENT_SPACING * 4;

    public BaseDialog(){
        this(null, -1);
    }

    public BaseDialog(String title, final int width){
        super();
        this.title = title;
        this.width = width;
    }

    public static final String presentFileDialog(LeanGuiLayout leanGuiLayout, String[] filterExtensions, String[] filterNames, boolean folderAndFile){
        return presentFileDialog(leanGuiLayout, false, null, null, null, filterExtensions, filterNames, folderAndFile);
    }

    public static final String presentFileDialog(
            LeanGuiLayout leanGuiLayout,
            boolean save,
            String[] filterExtensions,
            String[] filterNames,
            boolean folderAndFile) {
        return presentFileDialog(
                leanGuiLayout, save, null, null, null, filterExtensions, filterNames, folderAndFile);
    }

    public static final String presentFileDialog(
            LeanGuiLayout leanGuiLayout,
            TextVar textVar,
            FileObject fileObject,
            String[] filterExtensions,
            String[] filterNames,
            boolean folderAndFile) {
        return presentFileDialog(
                leanGuiLayout, false, textVar, null, fileObject, filterExtensions, filterNames, folderAndFile);
    }

    public static final String presentFileDialog(
            LeanGuiLayout leanGuiLayout,
            boolean save,
            TextVar textVar,
            FileObject fileObject,
            String[] filterExtensions,
            String[] filterNames,
            boolean folderAndFile) {
        return presentFileDialog(
                leanGuiLayout, save, textVar, null, fileObject, filterExtensions, filterNames, folderAndFile);
    }

    public static final String presentFileDialog(
            LeanGuiLayout leanGuiLayout,
            TextVar textVar,
            IVariables variables,
            String[] filterExtensions,
            String[] filterNames,
            boolean folderAndFile) {
        return presentFileDialog(
                leanGuiLayout, false, textVar, variables, null, filterExtensions, filterNames, folderAndFile);
    }

    public static final String presentFileDialog(
            LeanGuiLayout leanGuiLayout,
            boolean save,
            TextVar textVar,
            IVariables variables,
            String[] filterExtensions,
            String[] filterNames,
            boolean folderAndFile) {
        return presentFileDialog(
                leanGuiLayout, save, textVar, variables, null, filterExtensions, filterNames, folderAndFile);
    }

    public static final String presentFileDialog(
            LeanGuiLayout leanGuiLayout,
            TextVar textVar,
            IVariables variables,
            FileObject fileObject,
            String[] filterExtensions,
            String[] filterNames,
            boolean folderAndFile) {
        return presentFileDialog(
                leanGuiLayout, false, textVar, variables, fileObject, filterExtensions, filterNames, folderAndFile);
    }

    public static final String presentFileDialog(
            LeanGuiLayout leanGuiLayout,
            boolean save,
            TextVar textVar,
            IVariables variables,
            FileObject fileObject,
            String[] filterExtensions,
            String[] filterNames,
            boolean folderAndFile) {

        IFileDialog dialog;

        LeanVfsFileDialog vfsDialog = new LeanVfsFileDialog(leanGuiLayout, variables, fileObject, false, save);
        if (save) {
            if (fileObject != null) {
                vfsDialog.setSaveFilename(fileObject.getName().getBaseName());
                try {
                    vfsDialog.setFilterPath(HopVfs.getFilename(fileObject.getParent()));
                } catch (FileSystemException fse) {
                    // This wasn't a valid filename, ignore the error to reduce spamming
                }
            } else {
                // Take the first extension with "filename" prepended
                //
                if (filterExtensions != null && filterExtensions.length > 0) {
                    String filterExtension = filterExtensions[0];
                    String extension = filterExtension.substring(filterExtension.lastIndexOf("."));
                    vfsDialog.setSaveFilename("filename" + extension);
                }
            }
        }
            dialog = vfsDialog;

            if (save) {
                dialog.setText(BaseMessages.getString(PKG, "BaseDialog.SaveFile"));
            } else {
                dialog.setText(BaseMessages.getString(PKG, "BaseDialog.OpenFile"));
            }
            if (filterExtensions == null || filterNames == null) {
                dialog.setFilterExtensions(new String[]{"*.*"});
                dialog.setFilterNames(new String[]{BaseMessages.getString(PKG, "System.FileType.AllFiles")});
            } else {
                dialog.setFilterExtensions(filterExtensions);
                dialog.setFilterNames(filterNames);
            }
            if (fileObject != null) {
                dialog.setFileName(HopVfs.getFilename(fileObject));
            }
            if (variables != null && textVar != null && textVar.getElement().getText() != null) {
                dialog.setFileName(variables.resolve(textVar.getElement().getText()));
            }

            AtomicBoolean doIt = new AtomicBoolean(true);
            try {
                ExtensionPointHandler.callExtensionPoint(
                        LogChannel.UI,
                        variables,
                        HopGuiExtensionPoint.HopGuiFileOpenDialog.id,
                        new LeanGuiFileDialogExtension(doIt, dialog));
            } catch (Exception xe) {
                LogChannel.UI.logError("Error handling extension point 'HopGuiFileOpenDialog'", xe);
            }

            String filename = null;
            if (!doIt.get() || dialog.getFileName() != null) {
                if (folderAndFile) {
                    filename = FilenameUtils.concat(dialog.getFilterPath(), dialog.getFileName());
                } else {
                    filename = dialog.getFileName();
                }

                try {
                    LeanGuiFileOpenedExtension openedExtension =
                            new LeanGuiFileOpenedExtension(dialog, variables, filename);
                    ExtensionPointHandler.callExtensionPoint(
                            LogChannel.UI,
                            variables,
                            HopGuiExtensionPoint.HopGuiFileOpenedDialog.id,
                            openedExtension);
                    if (openedExtension.filename != null) {
                        filename = openedExtension.filename;
                    }
                } catch (Exception xe) {
                    LogChannel.UI.logError("Error handling extension point 'HopGuiFileOpenDialog'", xe);
                }

                if (textVar != null) {
                    textVar.getElement().setText(filename);
                }
            }
            return filename;
    }


}
