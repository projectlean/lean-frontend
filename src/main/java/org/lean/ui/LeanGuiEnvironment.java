package org.lean.ui;

import org.apache.hop.core.action.GuiContextAction;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.gui.plugin.GuiPluginType;
import org.apache.hop.core.gui.plugin.key.GuiKeyboardShortcut;
import org.apache.hop.core.gui.plugin.key.GuiOsxKeyboardShortcut;
import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.search.SearchableAnalyserPluginType;
import org.lean.core.gui.plugin.GuiRegistry;
import org.lean.core.gui.plugin.GuiWidgetElement;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.ui.plugins.perspective.LeanPerspectivePluginType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class LeanGuiEnvironment extends LeanClientEnvironment{

    public static void init() throws HopException {
        init(Arrays.asList(
                GuiPluginType.getInstance(),
                LeanPerspectivePluginType.getInstance(),
                SearchableAnalyserPluginType.getInstance()
            )
        );
    }

    public static void init( List<IPluginType> pluginTypes ) throws HopException {
        pluginTypes.forEach( PluginRegistry::addPluginType );

        for ( IPluginType pluginType : pluginTypes ) {
            pluginType.searchPlugins();
        }

        initGuiPlugins();
    }

    /**
     * Look for GuiWidgetElement annotated fields in all the GuiPlugins.
     * Put them in the Gui registry
     *
     * @throws HopException
     */
    public static void initGuiPlugins() throws HopException {

        try {
            GuiRegistry guiRegistry = GuiRegistry.getInstance();
            PluginRegistry pluginRegistry = PluginRegistry.getInstance();

            List<IPlugin> guiPlugins = pluginRegistry.getPlugins( GuiPluginType.class );
            for ( IPlugin guiPlugin : guiPlugins ) {
                ClassLoader classLoader = pluginRegistry.getClassLoader( guiPlugin );
                Class<?>[] typeClasses = guiPlugin.getClassMap().keySet().toArray( new Class<?>[ 0 ] );
                String guiPluginClassName = guiPlugin.getClassMap().get( typeClasses[ 0 ] );
                Class<?> guiPluginClass = classLoader.loadClass( guiPluginClassName );

                // Component widgets are defined on fields
                //
                List<Field> fields = findDeclaredFields( guiPluginClass );

                for ( Field field : fields ) {
                    GuiWidgetElement guiElement = field.getAnnotation( GuiWidgetElement.class );
                    if ( guiElement != null ) {
                        // Add the GUI Element to the registry...
                        //
                        guiRegistry.addGuiWidgetElement( guiPluginClassName, guiElement, field );
                    }
                }

                // Menu and toolbar items are defined on methods
                //
                List<Method> methods = findDeclaredMethods( guiPluginClass );
                for ( Method method : methods ) {
/*
                    GuiMenuElement menuElement = method.getAnnotation( GuiMenuElement.class );
                    if ( menuElement != null ) {
                        guiRegistry.addGuiWidgetElement( guiPluginClassName, menuElement, method, classLoader );
                    }
*/
                    GuiToolbarElement toolbarElement = method.getAnnotation( GuiToolbarElement.class );
                    if ( toolbarElement != null ) {
                        guiRegistry.addGuiToolbarElement( guiPluginClassName, toolbarElement, method, classLoader );
                    }
                    GuiKeyboardShortcut shortcut = method.getAnnotation( GuiKeyboardShortcut.class );
                    if ( shortcut != null ) {
                        guiRegistry.addKeyboardShortcut( guiPluginClassName, method, shortcut );
                    }
                    GuiOsxKeyboardShortcut osxShortcut = method.getAnnotation( GuiOsxKeyboardShortcut.class );
                    if ( osxShortcut != null ) {
                        guiRegistry.addKeyboardShortcut( guiPluginClassName, method, osxShortcut );
                    }
                    GuiContextAction contextAction = method.getAnnotation( GuiContextAction.class );
                    if ( contextAction != null ) {
                        guiRegistry.addGuiContextAction( guiPluginClassName, method, contextAction, classLoader );
                    }
                }
            }

            // Sort all GUI elements once.
            //
            guiRegistry.sortAllElements();

            // Now populate the HopFileTypeRegistry
            //
            // Get all the file handler plugins
            //

/*
            PluginRegistry registry = PluginRegistry.getInstance();
            List<IPlugin> plugins = registry.getPlugins( HopFileTypePluginType.class );
            for ( IPlugin plugin : plugins ) {
                try {
                    IHopFileType hopFileTypeInterface = registry.loadClass( plugin, IHopFileType.class );
                    HopFileTypeRegistry.getInstance().registerHopFile( hopFileTypeInterface );
                } catch ( HopPluginException e ) {
                    throw new HopException( "Unable to load plugin with ID '" + plugin.getIds()[ 0 ] + "' and type : " + plugin.getPluginType().getName(), e );
                }
            }
*/
        } catch ( Exception e ) {
            throw new HopException( "Error looking for Elements in GUI Plugins ", e );
        }
    }
}
