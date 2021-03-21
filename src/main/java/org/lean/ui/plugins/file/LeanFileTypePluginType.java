package org.lean.ui.plugins.file;

import org.apache.hop.core.plugins.BasePluginType;
import org.apache.hop.core.plugins.PluginAnnotationType;
import org.apache.hop.core.plugins.PluginMainClassType;

@PluginMainClassType(ILeanFileType.class)
@PluginAnnotationType(LeanFileTypePlugin.class)
public class LeanFileTypePluginType extends BasePluginType<LeanFileTypePlugin> {

    private LeanFileTypePluginType() { super(LeanFileTypePlugin.class, "LEAN_FILE_TYPES", "Lean File Type");}

    private static LeanFileTypePluginType pluginType;

    public static LeanFileTypePluginType getInstance(){
        if(pluginType == null){
            pluginType = new LeanFileTypePluginType();
        }
        return pluginType;
    }

    @Override
    protected String extractDesc(LeanFileTypePlugin annotation){ return annotation.description(); }

    @Override
    protected String extractID(LeanFileTypePlugin annotation){ return annotation.id(); }

    @Override
    protected String extractName(LeanFileTypePlugin annotation){ return annotation.name();}

    @Override
    protected String extractImageFile(LeanFileTypePlugin annotation){ return annotation.image();}

}
