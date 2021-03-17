package org.lean.ui.core.metadata;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.extension.ExtensionPointHandler;
import org.apache.hop.core.extension.HopExtensionPoint;
import org.apache.hop.core.gui.plugin.action.GuiAction;
import org.apache.hop.core.gui.plugin.action.GuiActionType;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.metadata.util.HopMetadataUtil;
import org.lean.ui.leangui.context.GuiContextHandler;
import org.lean.ui.leangui.context.GuiContextUtil;
import org.lean.ui.core.MetadataEditor;
import org.lean.ui.core.dialog.ErrorDialog;
import org.lean.ui.core.gui.vaadin.components.messagebox.MessageBox;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.plugins.perspective.metadata.MetadataPerspective;
import org.lean.ui.views.MetadataExplorerDialog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@VaadinSessionScope
public class MetadataManager<T extends IHopMetadata> {

    private IHopMetadataProvider metadataProvider;
    private IVariables variables;
    private ClassLoader classLoader;
    private Class<T> managedClass;
    private LeanGuiLayout leanGuiLayout;
    private MetadataPerspective metadataPerspective;

    public MetadataManager(LeanGuiLayout leanGuiLayout, IVariables variables, IHopMetadataProvider metadataProvider, Class<T> managedClass){
        this.leanGuiLayout = leanGuiLayout;
        this.metadataPerspective = (MetadataPerspective) leanGuiLayout.getPerspectiveManager().findPerspective(MetadataPerspective.class);
        this.variables = variables;
        this.classLoader = managedClass.getClassLoader();
        this.metadataProvider = metadataProvider;
        this.managedClass = managedClass;
    }


    public void openMetaStoreExplorer() {
        MetadataExplorerDialog dialog = new MetadataExplorerDialog(leanGuiLayout);
        dialog.open();
    }


/**
     * edit an element
     *
     * @return True if anything was changed
     */

    public boolean editMetadata() {
        try {
            List<String> names = getNames();

            // Plugin details from the managed class...
            //
            HopMetadata hopMetadata = HopMetadataUtil.getHopMetadataAnnotation(managedClass);

            // Show an action dialog...
            //
            List<GuiAction> actions = new ArrayList<>();
            for ( final String name : names ) {
                GuiAction action = new GuiAction( name, GuiActionType.Modify, name, name + " : " + hopMetadata.description(), hopMetadata.image(),
                        ( shiftAction, controlAction, t ) -> editMetadata( name ) );
                action.setClassLoader( getClassLoader() );
                actions.add( action );
            }
            return GuiContextUtil.getInstance().handleActionSelection(leanGuiLayout, "Select the " + hopMetadata.name() + " to edit", new GuiContextHandler( "HopGuiMetadataContext", actions ) );

        } catch ( Exception e ) {
            new ErrorDialog("Error", "Error editing metadata", e );
            return false;
        }
    }


    /**
     * delete an element
     *
     * @return True if anything was changed
     */

    public boolean deleteMetadata() {
        try {
            List<String> names = getNames();

            HopMetadata hopMetadata = HopMetadataUtil.getHopMetadataAnnotation( managedClass );

            // Show an action dialog...
            //
            List<GuiAction> actions = new ArrayList<>();
            for ( final String name : names ) {
                GuiAction action = new GuiAction( name, GuiActionType.Delete, name, name + " : " + hopMetadata.description(), hopMetadata.image(),
                        ( shiftAction, controlAction, t ) -> deleteMetadata( name ) );
                action.setClassLoader( getClassLoader() );
                actions.add( action );
            }
            return GuiContextUtil.getInstance().handleActionSelection(leanGuiLayout, "Select the " + hopMetadata.name() + " to delete after confirmation", new GuiContextHandler( "HopGuiMetadaContext", actions ) );

        } catch ( Exception e ) {
            new ErrorDialog("Error", "Error deleting metadata", e );
            return false;
        }
    }


/**
     * We look at the managed class name, add Dialog to it and then simply us that class to edit the dialog.
     *
     * @param elementName The name of the element to edit
     * @return True if anything was changed
     */

    public void /*boolean*/ editMetadata( String elementName ) {

/*
        if ( StringUtils.isEmpty( elementName ) ) {
            return false;
        }
*/

        try {
            IHopMetadataSerializer<T> serializer = metadataProvider.getSerializer( managedClass );

            // Load the metadata element from the metadata
            //
            T element = serializer.load( elementName );
            if ( element == null ) {
                // Something removed or renamed the element in the background
                //
                throw new HopException( "Unable to find element '" + elementName + "' in the metadata" );
            }

            initializeElementVariables( element );

            MetadataEditor<T> editor = this.createEditor(element);
            editor.setTitle(getManagedName());
            MetadataEditorDialog dialog = new MetadataEditorDialog(editor);
            dialog.open();

/*
            String result = dialog.open();

            if (result != null) {
                ExtensionPointHandler.callExtensionPoint(
                        hopGui.getLog(), variables, HopExtensionPoint.HopGuiMetadataObjectUpdated.id, element );
                return true;
            } else {
                return false;
            }
*/

        } catch ( Exception e ) {
            new ErrorDialog("Error", "Error editing metadata", e );
//            return false;
        }
    }


    public void editWithEditor(String name) {
        if (name == null) {
            return;
        }

        try {
            HopMetadata annotation = HopMetadataUtil.getHopMetadataAnnotation(managedClass);

            MetadataEditor<?> editor = metadataPerspective.findEditor(annotation.key(), name);

            if (editor == null) {

                // Load the metadata element from the metadata
                //
                IHopMetadataSerializer<T> serializer = metadataProvider.getSerializer(managedClass);
                T element = serializer.load(name);
                if (element == null) {
                    // Something removed or renamed the element in the background
                    //
                    throw new HopException("Unable to find element '" + name + "' in the metadata");
                }

                initializeElementVariables(element);

                metadataPerspective.addEditor(createEditor(element));
            } else {
                metadataPerspective.setActiveEditor(editor);
            }
        } catch (Exception e) {
            new ErrorDialog("Error", "Error editing metadata", e);
        }
    }



    private void initializeElementVariables( T element ) {
        if ( element instanceof IVariables ) {
            ( (IVariables) element ).initializeFrom( variables );
        }
    }




    /**
         * delete an element
         *
         * @param elementName The name of the element to delete
         * @return True if anything was deleted
         */

    public void /*boolean*/ deleteMetadata( String elementName ) {

/*
        if ( StringUtils.isEmpty( elementName ) ) {
            return false;
        }
*/

        MessageBox confirmBox = new MessageBox(MessageBox.MessageType.CONFIRM, "Delete?", "Are you sure you want to delete element " + elementName + "?", "Delete", e -> {
            try {
                IHopMetadataSerializer<T> serializer = getSerializer();

                // delete the metadata object from the metadata
                //
                T object = serializer.delete( elementName );

                // Just to be precise.
                //
                initializeElementVariables( object );

                ExtensionPointHandler.callExtensionPoint( leanGuiLayout.getLog(), variables, HopExtensionPoint.HopGuiMetadataObjectDeleted.id, object );

//                return true;

            } catch ( Exception ex ) {
                new ErrorDialog("Error", "Error deleting metadata element " + elementName, ex );
//                return false;
            }
        }, "Cancel", e -> { if(e.getSource().isOpened()){ e.getSource().close(); }});
        confirmBox.open();
//        return false;
    }


/*
    public boolean rename(String oldName, String newName) throws HopException {
        IHopMetadataSerializer<T> serializer = this.getSerializer();

        if (serializer.exists(newName)) {
            MessageBox messageBox =
                    new MessageBox(HopGui.getInstance().getShell(), SWT.ICON_ERROR | SWT.OK);
            messageBox.setText(getManagedName());
            messageBox.setMessage("Name '" + newName + "' already existe.");
            messageBox.open();

            return false;
        }

        T metadata = this.loadElement(oldName);
        metadata.setName(newName);
        serializer.save(metadata);
        serializer.delete( oldName );

        return true;
    }
*/

    public IHopMetadataSerializer<T> getSerializer() throws HopException {
        return metadataProvider.getSerializer( managedClass );
    }

/*
    public boolean openMetaDialog( T object, IHopMetadataSerializer<T> serializer ) throws Exception {
        if ( object == null ) {
            return false;
        }

        String dialogClassName = calculateDialogClassname();

        // Create the dialog class editor...
        // Always pass the shell, the metadata and the object to edit...
        //
        Class<?>[] constructorArguments = new Class<?>[] {
                Shell.class,
                IHopMetadataProvider.class,
                managedClass
        };
        Object[] constructorParameters = new Object[] {
                hopGui.getShell(), metadataProvider, object
        };

        Class<IMetadataDialog> dialogClass;
        try {
            dialogClass = (Class<IMetadataDialog>) classLoader.loadClass( dialogClassName );
        } catch ( ClassNotFoundException e1 ) {
            String simpleDialogClassName = calculateSimpleDialogClassname();
            try {
                dialogClass = (Class<IMetadataDialog>) classLoader.loadClass( simpleDialogClassName );
            } catch ( ClassNotFoundException e2 ) {
                try {
                    dialogClass = (Class<IMetadataDialog>) Class.forName( dialogClassName );
                } catch ( ClassNotFoundException e3 ) {
                    dialogClass = (Class<IMetadataDialog>) Class.forName( simpleDialogClassName );
                }
            }
        }
        Constructor<IMetadataDialog> constructor;
        try {
            constructor = dialogClass.getDeclaredConstructor( constructorArguments );
        } catch ( NoSuchMethodException nsm ) {
            constructorArguments = new Class<?>[] {
                    Shell.class,
                    IHopMetadataProvider.class,
                    managedClass,
                    IVariables.class
            };
            constructorParameters = new Object[] {
                    hopGui.getShell(), metadataProvider, object, hopGui.getVariables()
            };
            constructor = dialogClass.getDeclaredConstructor( constructorArguments );
        }
        if ( constructor == null ) {
            throw new HopException( "Unable to find dialog class (" + dialogClassName + ") constructor with arguments: Shell, IHopMetadataProvider, T and optionally IVariables" );
        }

        IMetadataDialog dialog = constructor.newInstance( constructorParameters );
        String name = dialog.open();
        if ( name != null ) {
            // Save it in the metadata
            serializer.save( object );

            ExtensionPointHandler.callExtensionPoint( HopGui.getInstance().getLog(), variables, HopExtensionPoint.HopGuiMetadataObjectUpdated.id, object );

            return true;
        } else {
            return false;
        }
    }
*/

/*
    public T newMetadata() {
        try {
            // Create a new instance of the managed class
            //
            T element = managedClass.newInstance();
            initializeElementVariables(element);

            return newMetadata(element);
        } catch (Exception e) {
            new ErrorDialog( "Error", "Error creating new metadata element", e);
            return null;
        }
    }
*/

/*
    public T newMetadata(T element) {
        try {

            ExtensionPointHandler.callExtensionPoint( HopGui.getInstance().getLog(), variables, HopExtensionPoint.HopGuiMetadataObjectCreateBeforeDialog.id, element );

            MetadataEditor<T> editor = this.createEditor(element);
            editor.setTitle(getManagedName());

            MetadataEditorDialog dialog = new MetadataEditorDialog(HopGui.getInstance().getShell(), editor);

            String name = dialog.open();
            if (name != null) {
                ExtensionPointHandler.callExtensionPoint(HopGui.getInstance().getLog(), variables,
                        HopExtensionPoint.HopGuiMetadataObjectCreated.id, element );
            }
            return element;
        } catch ( Exception e ) {
            new ErrorDialog( "Error", "Error editing new metadata element", e );
            return null;
        }
    }
*/


    public T newMetadataWithEditor() {

        try {

            // Create a new instance of the managed class
            //
            T element = managedClass.newInstance();
            initializeElementVariables(element);

            ExtensionPointHandler.callExtensionPoint(
                    leanGuiLayout.getLog(), variables, HopExtensionPoint.HopGuiMetadataObjectCreateBeforeDialog.id, element );

            MetadataEditor<T> editor = this.createEditor(element);
//            editor.setTitle("New " + this.getManagedName());

            metadataPerspective.addEditor(editor);

            return element;
        } catch (Exception e) {
            new ErrorDialog("Error", "Error creating new metadata element", e);
            return null;
        }
    }

    public List<String> getNames() throws HopException {
        try {
            List<String> names = getSerializer().listObjectNames();
            Collections.sort( names );
            return names;

        } catch ( Exception e ) {
            throw new HopException( "Unable to get list of element names in the MetaStore for class " + managedClass.getName(), e );
        }
    }


    public String[] getNamesArray() throws HopException {
        try {
            return getNames().toArray( new String[ 0 ] );
        } catch ( Exception e ) {
            throw new HopException( "Unable to get element names array in the MetaStore for class " + managedClass.getName(), e );
        }
    }

    public String calculateDialogClassname() {
        String dialogClassName = managedClass.getName();
        dialogClassName = dialogClassName.replaceFirst( "\\.hop\\.", ".hop.ui." );
        dialogClassName += "Dialog";
        return dialogClassName;
    }

    public String calculateSimpleDialogClassname() {
        String dialogClassName = managedClass.getName();
        dialogClassName += "Dialog";
        return dialogClassName;
    }

    protected MetadataEditor<T> createEditor(T metadata) throws HopException {

        // Find the class editor...
        //
        String className = managedClass.getName();
        className += "Editor";
        Class<MetadataEditor<T>> editorClass;
        try {
            editorClass = (Class<MetadataEditor<T>>) classLoader.loadClass(className);
        } catch (ClassNotFoundException e1) {
            className = managedClass.getName();
            className = className.replaceFirst("\\.hop\\.", ".hop.ui.");
            className += "Editor";
            try {
                editorClass = (Class<MetadataEditor<T>>) classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new HopException("Unable to find editor class (" + className + ")");
            }
        }

        // Create the class editor...
        // Always pass the HopGui, the metadata manager and the object to edit...
        //
        try {

            Class<?>[] constructorArguments =
                    new Class<?>[] {MetadataPerspective.class, MetadataManager.class, metadata.getClass()};

            Constructor<MetadataEditor<T>> constructor;
            constructor = editorClass.getDeclaredConstructor(constructorArguments);

            if (constructor == null) {
                throw new HopException(
                        "Unable to find editor class ("
                                + className
                                + ") constructor with arguments: HopGui, MetadataManager and IHopMetadata, T and optionally IVariables");
            }

            return constructor.newInstance(new Object[] {metadataPerspective, this, metadata});
        } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | SecurityException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new HopException("Unable to create editor for class " + managedClass.getName(), e);
        }
    }

    /**
     * Gets metadataProvider
     *
     * @return value of metadataProvider
     */

    public IHopMetadataProvider getMetadataProvider() {
        return metadataProvider;
    }

    /**
     * @param metadataProvider The metadataProvider to set
     */

    public void setMetadataProvider( IHopMetadataProvider metadataProvider ) {
        this.metadataProvider = metadataProvider;
    }


    /**
     * Gets variables
     variables
     *
     * @return value of variables
    variables
     */

    public IVariables getVariables() {
        return variables;
    }


    /**
     * @param variables The variables
    variables to set
     */

    public void setVariables( IVariables variables ) {
        this.variables = variables;
    }


    /**
     * Gets classLoader
     *
     * @return value of classLoader
     */

    public ClassLoader getClassLoader() {
        return classLoader;
    }


    /**
     * @param classLoader The classLoader to set
     */

    public void setClassLoader( ClassLoader classLoader ) {
        this.classLoader = classLoader;
    }


    /**
     * Gets managedClass
     *
     * @return value of managedClass
     */

    public Class<T> getManagedClass() {
        return managedClass;
    }

    protected String getManagedName() {
        HopMetadata annotation = managedClass.getAnnotation(HopMetadata.class);
        if (annotation != null) {
            return annotation.name();
        }
        return null;
    }

    /**
     * @param managedClass The managedClass to set
     */

    public void setManagedClass( Class<T> managedClass ) {
        this.managedClass = managedClass;
    }

    public T loadElement( String selectedItem ) throws HopException {
        T element = getSerializer().load( selectedItem );
        initializeElementVariables( element );
        return element;
    }

}
