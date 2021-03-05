package org.lean.ui.leangui.file;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.lean.ui.leangui.file.presentation.ILeanFileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeanFileTypeRegistry {

    private static LeanFileTypeRegistry leanFileTypeRegistry;

    private List<ILeanFileType> leanFileTypes;

    public static final LeanFileTypeRegistry getInstance(){
        if(leanFileTypeRegistry == null){
            leanFileTypeRegistry = new LeanFileTypeRegistry();
        }
        return leanFileTypeRegistry;
    }

    public List<ILeanFileType> getFileTypes(){
        return leanFileTypes;
    }

    public void registerLeanFile(ILeanFileType leanFileType){
        if(!leanFileTypes.contains(leanFileType)){
            leanFileTypes.add(leanFileType);
        }
    }

    /**
     * This method first tries to find a LeanFile by looking at the extension.
     * If none can be found the content is looked at by each IHopFileType
     *
     * @param filename The filename to search with
     * @return The ILeanFileType we can use to open the file itself.
     * @throws HopException
     */
    public ILeanFileType findLeanFileType(String filename) throws HopException{
        for(ILeanFileType leanFileType : leanFileTypes){
            if(leanFileType.isHandledBy(filename, false)){
                return leanFileType;
            }
        }
        for(ILeanFileType leanFile : leanFileTypes){
            if(leanFile.isHandledBy(filename, true)){
                return leanFile;
            }
        }
        return null;
    }

    /**
     * Get all the filter extensions of all the LeanFile plugins
     *
     * @return all the file extensions
     */
    public String[] getFilterExtensions(){
        List<String> filterExtensions = new ArrayList<>();
        for(ILeanFileType leanFile : leanFileTypes){
            filterExtensions.addAll(Arrays.asList(leanFile.getFilterExtensions()));
        }
        if(filterExtensions.size() > 1){
            String all = "";
            for(String filterExtension : filterExtensions){
                if(all.length() > 0) {
                    all += ";";
                }
                all += filterExtension;
            }
            filterExtensions.add(0, all);
        }
        return filterExtensions.toArray(new String[0]);
    }

    /** Get all the filter names of the LeanFile plugins
     *
     * @return all the file names
     */
    public String[] getFilterNames(){
        List<String> filterNames = new ArrayList<>();
        for(ILeanFileType leanFile : leanFileTypes){
            filterNames.addAll(Arrays.asList(leanFile.getFilterNames()));
        }
        if(filterNames.size() > 1){
            filterNames.add(0, "All Lean file types");
        }
        return filterNames.toArray(new String[0]);
    }

    public List<String> getFileTypeNames(){
        List<String> names = new ArrayList<>();
        for(ILeanFileType leanFile : leanFileTypes){
            names.add(leanFile.getName());
        }
        return  names;
    }

    public ILeanFileType getFileTypeByName(String name){
        if(StringUtils.isEmpty(name)){
            return null;
        }
        for(ILeanFileType leanFile : leanFileTypes){
            if(leanFile.getName().equalsIgnoreCase(name)){
                return leanFile;
            }
        }
        return null;
    }

}
