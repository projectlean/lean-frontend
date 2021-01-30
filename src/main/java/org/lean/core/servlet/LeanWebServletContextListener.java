package org.lean.core.servlet;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.plugins.PluginRegistry;
import org.lean.core.LeanEnvironment;
import org.lean.core.exception.LeanException;
import org.lean.core.metadata.LeanMetadataUtil;
import org.lean.ui.LeanGuiEnvironment;
import org.lean.ui.plugins.perspective.LeanPerspectivePluginType;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class LeanWebServletContextListener implements ServletContextListener{


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try{
            LeanEnvironment.init();
            LeanGuiEnvironment.init();

            LeanMetadataUtil util = LeanMetadataUtil.getInstance();

            PluginRegistry pluginRegistry = PluginRegistry.getInstance();
            pluginRegistry.addPluginType(LeanPerspectivePluginType.getInstance());
            pluginRegistry.init(true);
        }catch(LeanException | HopException e){
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
