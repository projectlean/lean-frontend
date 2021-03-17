package org.lean.ui;

import org.apache.hop.core.Const;
import org.apache.hop.core.database.DatabasePluginType;
import org.apache.hop.core.encryption.Encr;
import org.apache.hop.core.encryption.TwoWayPasswordEncoderPluginType;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.extension.ExtensionPointPluginType;
import org.apache.hop.core.logging.*;
import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.row.value.ValueMetaPluginType;
import org.apache.hop.core.util.EnvUtil;
import org.apache.hop.core.vfs.plugin.VfsPluginType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class LeanClientEnvironment {

    private static final Class<?> PKG = Const.class;

    private static LeanClientEnvironment instance = null;

    private static Boolean initialized;

    public enum ClientType {
        LEAN_GUI, CLI, SERVER, OTHER;

        public String getID() {
            if ( this != OTHER ) {
                return this.name();
            }
            return instance.clientID;
        }
    }

    private LeanClientEnvironment.ClientType client;
    // used when type is OTHER
    private String clientID = null;

    public static synchronized void init() throws HopException {
        init( Arrays.asList(
                LoggingPluginType.getInstance(),
                ValueMetaPluginType.getInstance(),
                DatabasePluginType.getInstance(),
                ExtensionPointPluginType.getInstance(),
                TwoWayPasswordEncoderPluginType.getInstance(),
                VfsPluginType.getInstance()
                )
        );
    }

    public static synchronized void init( List<IPluginType> pluginsToLoad ) throws HopException {
        if ( initialized != null ) {
            return;
        }

        if ( LeanClientEnvironment.instance == null ) {
            LeanClientEnvironment.instance = new LeanClientEnvironment();
        }

        // Check the Lean Configuration backend
        //


        // Initialize the logging back-end.
        //
        HopLogStore.init();

        // Add console output so that folks see what's going on...
        //
        if ( !"Y".equalsIgnoreCase( System.getProperty( Const.HOP_DISABLE_CONSOLE_LOGGING, "N" ) ) ) {
            HopLogStore.getAppender().addLoggingEventListener( new ConsoleLoggingEventListener() );
        }
        HopLogStore.getAppender().addLoggingEventListener( new Slf4jLoggingEventListener() );

        // Load plugins
        //
        pluginsToLoad.forEach( PluginRegistry::addPluginType );
        PluginRegistry.init();

        List<IPlugin> logginPlugins = PluginRegistry.getInstance().getPlugins( LoggingPluginType.class );
        initLogginPlugins( logginPlugins );

        String passwordEncoderPluginID = Const.NVL( EnvUtil.getSystemProperty( Const.HOP_PASSWORD_ENCODER_PLUGIN ), "Hop" );

        Encr.init( passwordEncoderPluginID );

        initialized = new Boolean( true );
    }

    /**
     * Get all declared fields from the given class, also the ones from all super classes
     *
     * @param parentClass
     * @return A unique list of fields.
     */
    protected static final List<Field> findDeclaredFields(Class<?> parentClass ) {
        Set<Field> fields = new HashSet<>();

        for ( Field field : parentClass.getDeclaredFields() ) {
            fields.add( field );
        }
        Class<?> superClass = parentClass.getSuperclass();
        while ( superClass != null ) {
            for ( Field field : superClass.getDeclaredFields() ) {
                fields.add( field );
            }

            superClass = superClass.getSuperclass();
        }

        return new ArrayList<>( fields );
    }

    /**
     * Get all declared methods from the given class, also the ones from all super classes
     *
     * @param parentClass
     * @return A unique list of methods.
     */
    protected static final List<Method> findDeclaredMethods(Class<?> parentClass ) {
        Set<Method> methods = new HashSet<>();

        for ( Method method : parentClass.getDeclaredMethods() ) {
            methods.add( method );
        }
        Class<?> superClass = parentClass.getSuperclass();
        while ( superClass != null ) {
            for ( Method method : superClass.getDeclaredMethods() ) {
                methods.add( method );
            }

            superClass = superClass.getSuperclass();
        }

        return new ArrayList<>( methods );
    }

    public static boolean isInitialized() {
        return initialized != null;
    }

    private static void initLogginPlugins( List<IPlugin> logginPlugins ) throws HopPluginException {
        for ( IPlugin plugin : logginPlugins ) {
            ILoggingPlugin loggingPlugin = (ILoggingPlugin) PluginRegistry.getInstance().loadClass( plugin );
            loggingPlugin.init();
        }
    }


    public void setClient( LeanClientEnvironment.ClientType client ) {
        this.client = client;
    }

    /**
     * Set the Client ID which has significance when the ClientType == OTHER
     *
     * @param id
     */
    public void setClientID( String id ) {
        this.clientID = id;
    }

    public LeanClientEnvironment.ClientType getClient() {
        return this.client;
    }

    /**
     * Return this singleton. Create it if it hasn't been.
     *
     * @return
     */
    public static LeanClientEnvironment getInstance() {

        if ( LeanClientEnvironment.instance == null ) {
            LeanClientEnvironment.instance = new LeanClientEnvironment();
        }

        return LeanClientEnvironment.instance;
    }

    public static void reset() {
        if ( HopLogStore.isInitialized() ) {
            HopLogStore.getInstance().reset();
        }
        PluginRegistry.getInstance().reset();
        initialized = null;
    }
}
