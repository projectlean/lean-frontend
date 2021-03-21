package org.lean.ui.core.metadata;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.file.IHasFilename;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;
import org.lean.ui.plugins.file.ILeanFileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MetadataFileType implements ILeanFileType {

    @Override public String getName() {
        return "meta";
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == this ) {
            return true;
        }
        if ( obj.getClass().equals( this.getClass() ) ) {
            return true; // same class is enough
        }
        return false;
    }

    @Override public String getDefaultFileExtension() {
        return null;
    }

    @Override public String[] getFilterExtensions() {
        return new String[ 0 ];
    }

    @Override public String[] getFilterNames() {
        return new String[ 0 ];
    }

    @Override public Properties getCapabilities() {
        Properties capabilities = new Properties();
        capabilities.setProperty( IHopFileType.CAPABILITY_NEW, "true" );
        capabilities.setProperty( IHopFileType.CAPABILITY_CLOSE, "true" );
        capabilities.setProperty( IHopFileType.CAPABILITY_SAVE, "true" );

        return capabilities;
    }

    @Override public boolean hasCapability( String capability ) {
        if ( getCapabilities() == null ) {
            return false;
        }
        Object available = getCapabilities().get( capability );
        if (available==null) {
            return false;
        }
        return "true".equalsIgnoreCase( available.toString() );
    }

    @Override
    public ILeanFileTypeHandler openFile(LeanGuiLayout leanGuiLayout, String filename, IVariables parentVariableSpace) throws HopException{
        return new MetadataFileTypeHandler();
    }

    @Override
    public ILeanFileTypeHandler newFile(LeanGuiLayout leanGuiLayout, IVariables parentVariableSpace) throws HopException{
        return new MetadataFileTypeHandler();
    }

    @Override
    public boolean isHandledBy(String filename, boolean checkContent) throws HopException{
        return false;
    }

    @Override
    public boolean supportsFile(IHasFilename metaObject){
        return false;
    }

    @Override
    public List<IGuiContextHandler> getContextHandlers(LeanGuiLayout leanGuiLayout){
        List<IGuiContextHandler> handlers = new ArrayList<>();
        return handlers;
    }

    @Override
    public String getFileTypeImage(){
        return "./frontend/images/perspectives/metadata.svg";
    }

}
