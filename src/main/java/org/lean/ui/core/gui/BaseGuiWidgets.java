package org.lean.ui.core.gui;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.core.exception.LeanException;
import org.lean.core.gui.plugin.GuiRegistry;
import org.lean.core.gui.plugin.toolbar.GuiToolbarItem;
import org.lean.ui.layout.LeanGuiLayout;

import java.lang.reflect.Method;
import java.util.List;

@UIScope
public class BaseGuiWidgets {

    private String leanGuiLayoutId;

    /**
     * Every set of widgets (toolbar, composite, menu, ...) gets its own unique ID It will cause a new
     * object to be created per unique ID for the listener or GUI plugins if this plugin wasn't
     * registered yet.
     */
    protected String instanceId;

    public BaseGuiWidgets(String leanGuiLayoutId, String instanceId) {
        this.leanGuiLayoutId = leanGuiLayoutId;
        this.instanceId = instanceId;
    }

    /**
     * Let the GUI plugin system know that there is no need to instantiate new objects for the given
     * class. Instead this object can be taken. Make sure to call dispose() to prevent a (slow) memory
     * leak. Call this method before creating the widgets themselves.
     *
     * @param guiPluginObject
     */
    public void registerGuiPluginObject(Object guiPluginObject) {

        GuiRegistry guiRegistry = GuiRegistry.getInstance();
        String guiPluginClassName = guiPluginObject.getClass().getName();
        guiRegistry.registerGuiPluginObject(
                leanGuiLayoutId, guiPluginClassName, instanceId, guiPluginObject);
    }

    protected Object findGuiPluginInstance(ClassLoader classLoader, String listenerClassName)
            throws Exception {
        try {
            // This is the class that owns the listener method
            // It's a GuiPlugin class in other words
            //
            Object guiPluginObject =
                    GuiRegistry.getInstance().findGuiPluginObject(leanGuiLayoutId, listenerClassName, instanceId);
            if (guiPluginObject == null) {
                // Create a new instance
                //
                guiPluginObject = classLoader.loadClass(listenerClassName).newInstance();

                // Store it
                //
                GuiRegistry.getInstance()
                        .registerGuiPluginObject(leanGuiLayoutId, listenerClassName, instanceId, guiPluginObject);
            }
            return guiPluginObject;
        } catch (Exception e) {
            throw new LeanException(
                    "Error finding GuiPlugin instance for class '"
                            + listenerClassName
                            + "' and instance ID : "
                            + instanceId,
                    e);
        }
    }

    protected String[] getComboItems(GuiToolbarItem toolbarItem) {
        try {
            Object singleton =
                    findGuiPluginInstance(toolbarItem.getClassLoader(), toolbarItem.getListenerClass());
            if (singleton == null) {
                LogChannel.UI.logError(
                        "Could not get instance of class '"
                                + toolbarItem.getListenerClass()
                                + " for toolbar item "
                                + toolbarItem
                                + ", combo values method : "
                                + toolbarItem.getGetComboValuesMethod());
                return new String[] {};
            }

            // TODO: create a method finder where we can simply give a list of objects that we have
            // available
            // You can find them in any order that the developer chose and just pass them that way.
            //
            Method method;
            boolean withArguments = true;
            try {
                method =
                        singleton
                                .getClass()
                                .getMethod(
                                        toolbarItem.getGetComboValuesMethod(),
                                        ILogChannel.class,
                                        IHopMetadataProvider.class);
            } catch (NoSuchMethodException nsme) {
                // Try to find the method without arguments...
                //
                try {
                    method = singleton.getClass().getMethod(toolbarItem.getGetComboValuesMethod());
                    withArguments = false;
                } catch (NoSuchMethodException nsme2) {
                    throw new LeanException(
                            "Unable to find method '"
                                    + toolbarItem.getGetComboValuesMethod()
                                    + "' without parameters or with parameters ILogChannel and IHopMetadataProvider in class '"
                                    + toolbarItem.getListenerClass()
                                    + "'",
                            nsme2);
                }
            }
            List<String> values;
            if (withArguments) {
                LeanGuiLayout leanGuiLayout = (LeanGuiLayout) UI.getCurrent().getChildren().filter(component -> component.getClass() == LeanGuiLayout.class).findFirst().orElse(null);
                values =
                        (List<String>)
                                method.invoke(singleton, LogChannel.UI, leanGuiLayout.getMetadataProvider());
            } else {
                values = (List<String>) method.invoke(singleton);
            }
            return values.toArray(new String[0]);
        } catch (Exception e) {
            LogChannel.UI.logError(
                    "Error getting list of combo items for method '"
                            + toolbarItem.getGetComboValuesMethod()
                            + "' in class : "
                            + toolbarItem.getListenerClass(),
                    e);
            return new String[] {};
        }
    }

// TODO: Vaadin implemention
    protected ComponentEventListener getListener(
            ClassLoader classLoader, String listenerClassName, String listenerMethodName) {

        // Call the method to which the GuiToolbarElement annotation belongs.
        //
        return e -> {
            try {
                Object singleton = findGuiPluginInstance(classLoader, listenerClassName);
                Method listenerMethod = singleton.getClass().getDeclaredMethod(listenerMethodName);
                if (listenerMethod == null) {
                    throw new LeanException(
                            "Unable to find method " + listenerMethodName + " in class " + listenerClassName);
                }
                try {
                    listenerMethod.invoke(singleton);
                } catch (Exception ie) {
                    System.err.println(
                            "Unable to call method "
                                    + listenerMethodName
                                    + " in class "
                                    + listenerClassName
                                    + " : "
                                    + ie.getMessage());
                    throw ie;
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        };
    }

    /**
     * Gets instanceId
     *
     * @return value of instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /** @param instanceId The instanceId to set */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
