package org.lean.ui.leangui.file;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.vfs.HopVfs;
import org.lean.ui.leangui.file.presentation.ILeanFileType;

import java.util.Properties;

public abstract class LeanFileTypeBase implements ILeanFileType {

    @Override
    public abstract String getName();

    @Override
    public abstract Properties getCapabilities();

    @Override
    public abstract String[] getFilterExtensions();

    @Override
    public boolean equals( Object obj ) {
        if ( obj == this ) {
            return true;
        }
        return obj.getClass().equals( this.getClass() ); // same class is enough
    }

    @Override
    public boolean isHandledBy( String filename, boolean checkContent ) throws HopException {
        try {
            if ( checkContent ) {
                throw new HopException( "Generic file content validation is not possible at this time for file '" + filename + "'" );
            } else {
                FileObject fileObject = HopVfs.getFileObject( filename );
                FileName fileName = fileObject.getName();
                String fileExtension = fileName.getExtension();

                // No extension
                if ( Utils.isEmpty(fileExtension) ) return false;

                // Verify the extension
                //
                for ( String typeExtension : getFilterExtensions() ) {
                    if ( typeExtension.toLowerCase().endsWith( fileExtension ) ) {
                        return true;
                    }
                }

                return false;
            }
        } catch ( Exception e ) {
            throw new HopException( "Unable to verify file handling of file '" + filename + "' by extension", e );
        }
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

}
