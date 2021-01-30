package org.lean.ui.plugins.perspective;

import org.apache.hop.core.plugins.*;

@PluginMainClassType(ILeanPerspective.class)
@PluginAnnotationType(LeanPerspectivePlugin.class)
public class LeanPerspectivePluginType extends BasePluginType<LeanPerspectivePlugin> implements IPluginType<LeanPerspectivePlugin> {

    private LeanPerspectivePluginType(){
        super(LeanPerspectivePlugin.class, "LEAN_PERSPECTIVES", "Lean Perspective");

        pluginFolders.add(new PluginFolder("plugins", false, true));
    }

    private static LeanPerspectivePluginType pluginType;

    public static LeanPerspectivePluginType getInstance(){
        if(pluginType == null){
            pluginType = new LeanPerspectivePluginType();
        }
        return pluginType;
    }


    @Override
    protected String extractID(LeanPerspectivePlugin leanPerspectivePlugin) {
        return leanPerspectivePlugin.id();
    }

    @Override
    protected String extractName(LeanPerspectivePlugin leanPerspectivePlugin) {
        return leanPerspectivePlugin.name();
    }

    @Override
    protected String extractDesc(LeanPerspectivePlugin leanPerspectivePlugin) {
        return leanPerspectivePlugin.description();
    }

    @Override
    protected String extractImageFile(LeanPerspectivePlugin leanPerspectivePlugin){
        return leanPerspectivePlugin.image();
    }

    protected String extractRoute(LeanPerspectivePlugin leanPerspectivePlugin){
        return leanPerspectivePlugin.route();
    }
}
