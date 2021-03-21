package org.lean.ui.leangui.delegates;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.exception.HopException;
import org.lean.ui.core.dialog.BaseDialog;
import org.lean.ui.core.dialog.ErrorDialog;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;
import org.lean.ui.plugins.file.LeanFileTypeRegistry;
import org.lean.ui.plugins.file.ILeanFileType;
import org.lean.ui.plugins.perspective.ILeanPerspective;

public class LeanGuiFileDelegate {

    private LeanGuiLayout leanGuiLayout;

    public LeanGuiFileDelegate(LeanGuiLayout leanGuiLayout){
        this.leanGuiLayout = leanGuiLayout;
    }

    public ILeanFileTypeHandler getActiveFileTypeHandler(){
        ILeanFileTypeHandler typeHandler = leanGuiLayout.getActivePerspective().getActiveFileTypeHandler();
        return typeHandler;
    }

    public void fileOpen(){
        try{
            LeanFileTypeRegistry fileRegistry = LeanFileTypeRegistry.getInstance();

            String filename = BaseDialog.presentFileDialog(leanGuiLayout, fileRegistry.getFilterExtensions(),
                    fileRegistry.getFilterNames(),
                    true);
            if(filename == null){
                return;
            }
            fileOpen(leanGuiLayout.getVariables().resolve(filename));


        }catch(Exception e){
            new ErrorDialog("Error", "Error opening file", e);
        }
    }

//    public void fileOpen(String filename){
//
//    }

    public ILeanFileTypeHandler fileOpen(String filename) throws Exception{
        LeanFileTypeRegistry fileTypeRegistry = LeanFileTypeRegistry.getInstance();
        ILeanFileType leanFile = LeanFileTypeRegistry.getInstance().findLeanFileType(filename);
        if(leanFile == null){
            throw new HopException(
                    "we looked at "
                    + fileTypeRegistry.getFileTypes().size()
                    + " different Lean Gui file types but non know how to open file '"
                    + filename
                    + "'");
        }
        ILeanFileTypeHandler fileTypeHandler = leanFile.openFile(leanGuiLayout, filename, leanGuiLayout.getVariables());
        leanGuiLayout.handleFileCapabilities(leanFile, false, false);
        return fileTypeHandler;
    }

    public String fileSaveAs(){
        try{
            ILeanFileTypeHandler typeHandler = getActiveFileTypeHandler();
            ILeanFileType fileType = typeHandler.getFileType();
            if(!fileType.hasCapability((ILeanFileType.CAPABILITY_SAVE_AS))){
                return null;
            }

            String filename = BaseDialog.presentFileDialog(
                    leanGuiLayout,
                    fileType.getFilterExtensions(),
                    fileType.getFilterNames(),
                    true);

            if(filename == null){
                return null;
            }

            filename = leanGuiLayout.getVariables().resolve(filename);

            typeHandler.saveAs(filename);

            return filename;
        }catch(Exception e){
            new ErrorDialog("Error", "Error saving file", e);
            return null;
        }

    }

    public void fileSave(){
        try{
            ILeanFileTypeHandler typeHandler = getActiveFileTypeHandler();
            ILeanFileType fileType = typeHandler.getFileType();
            if(fileType.hasCapability(ILeanFileType.CAPABILITY_SAVE)) {
                if (StringUtils.isEmpty(typeHandler.getFilename())) {
                    // Ask for the filename: saveAs
                    //
                    fileSaveAs();
                } else {
                    typeHandler.save();
                }
            }
        }catch(Exception e){
            new ErrorDialog("Error", "Error saving file", e);
        }
    }

    public boolean fileClose() {
        try {
            ILeanPerspective perspective = leanGuiLayout.getActivePerspective();
            ILeanFileTypeHandler typeHandler = getActiveFileTypeHandler();
            ILeanFileType fileType = typeHandler.getFileType();
            if (fileType.hasCapability(ILeanFileType.CAPABILITY_CLOSE)) {
                perspective.remove(typeHandler);
            }
        } catch (Exception e) {
            new ErrorDialog("Error", "Error saving/closing file", e);
        }
        return false;
    }

    public boolean saveGuardAllFiles(){
        return true;
    }

    public void closeAllFiles(){
    }

    public boolean fileExit(){
        return false;
    }

    public void fileOpenRecent(){

    }

    public void exportToSvg(){

    }
}
