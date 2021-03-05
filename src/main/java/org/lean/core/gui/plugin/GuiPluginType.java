package org.lean.core.gui.plugin;

import org.apache.hop.core.plugins.BasePluginType;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginAnnotationType;

@PluginAnnotationType(GuiPlugin.class)
public class GuiPluginType extends BasePluginType<GuiPlugin> implements IPluginType<GuiPlugin> {

    private static GuiPluginType pluginType;

    private GuiPluginType(){
        super(GuiPlugin.class, "GUI", "GUI");
//        populateFolders("gui");
    }

    public static GuiPluginType getInstance(){
        if(pluginType == null){
            pluginType = new GuiPluginType();
        }
        return pluginType;
    }

    @Override
    protected String extractID(GuiPlugin guiPlugin) {
        return null;
    }

    @Override
    protected String extractName(GuiPlugin guiPlugin) {
        return null;
    }

    @Override
    protected String extractDesc(GuiPlugin guiPlugin) {
        return null;
    }
}
