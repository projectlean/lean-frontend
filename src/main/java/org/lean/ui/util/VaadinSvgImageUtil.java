package org.lean.ui.util;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.hop.core.exception.HopFileException;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.svg.SvgSupport;
import org.apache.hop.core.vfs.HopVfs;
import org.lean.core.VaadinUniversalImage;
import org.lean.core.VaadinUniversalImageBitmap;
import org.lean.core.VaadinUniversalImageSvg;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.PropsUi;

import java.io.*;
import java.net.URL;

/**
 * Class for loading images from SVG, PNG, or other bitmap formats.
 * <p>
 * Logic is: if SVG is enabled, then SVG icon loaded if exist. Otherwise, class trying to change name into PNG and try
 * to load. If initial name is PNG, then PNG icon will be loaded.
 */
public class VaadinSvgImageUtil {

    private static ILogChannel log = new LogChannel( "VaadinSvgImageUtil" );

    private static final String NO_IMAGE = "frontend/images/no_image.svg";

    private static FileObject base;

    private static double zoomFactor = PropsUi.getInstance().getZoomFactor();

    static {
        try {
            base = HopVfs.getFileSystemManager().resolveFile( System.getProperty( "user.dir" ) );
        } catch ( FileSystemException e ) {
            e.printStackTrace();
            base = null;
        }
    }

    /**
     * Get the image for when all other fallbacks have failed.  This is an image
     * drawn on the canvas, a square with a red X.
     *
     * @return the missing image
     */
    public static VaadinUniversalImage getMissingImage( ) {
        Image img = new Image("", "Image not found");
        img.setWidth(ConstUi.ICON_SIZE_PX);
        img.setHeight(ConstUi.ICON_SIZE_PX);
        return new VaadinUniversalImageBitmap( img, zoomFactor );
    }
    /**
     * Load image from several sources.
     */
    public static VaadinUniversalImage getImage(String location ) {
        return getImageAsResource( location );
    }

    /**
     * Load image from several sources.
     */
    private static VaadinUniversalImage getImageAsResourceInternal(String location ) {
        VaadinUniversalImage result = null;
        if ( result == null ) {
            result = loadFromCurrentClasspath( location );
        }
        if ( result == null ) {
            result = loadFromBasedVFS( location );
        }
        if ( result == null ) {
            result = loadFromSimpleVFS( location );
        }
        return result;
    }

    /**
     * Load image from several sources.
     */
    public static VaadinUniversalImage getImageAsResource(String location ) {
        if (location==null) {
            throw new RuntimeException( "No location given to load image resource");
        }
        VaadinUniversalImage result = null;
        if ( result == null && SvgSupport.isSvgEnabled() ) {
            result = getImageAsResourceInternal(SvgSupport.toSvgName( location ) );
        }
        if ( result == null ) {
            result = getImageAsResourceInternal(SvgSupport.toPngName( location ) );
        }
        if ( result == null && !location.equals( NO_IMAGE ) ) {
            log.logError( "Unable to load image [" + location + "]", new Exception() );
            result = getImageAsResource( NO_IMAGE );
        }
        if ( result == null ) {
            log.logError( "Unable to load image [" + location + "]", new Exception() );
            result = getMissingImage( );
        }
        return result;
    }

    /**
     * Get an image using the provided classLoader and path.  An attempt will be made to load the image with the
     * classLoader first using SVG (regardless of extension), and falling back to PNG.  If the image cannot be
     * loaded with the provided classLoader, the search path will be expanded to include the file system (ui/images).
     *
     * @param display     the device to render the image to
     * @param classLoader the classLoader to use to load the image resource
     * @param filename    the path to the image
     * @param width       the width to scale the image to
     * @param height      the height to scale the image to
     * @return an swt Image with width/height dimensions
     */
    public static Image getImage(ClassLoader classLoader, String filename, int width, int height ) {
        VaadinUniversalImage u = getUniversalImage(classLoader, filename );
        return u.getAsBitmapForSize(width, height );
    }

    private static VaadinUniversalImage getUniversalImageInternal(ClassLoader classLoader, String filename ) {
        VaadinUniversalImage result = loadFromClassLoader(classLoader, filename );
        if ( result == null ) {
            result = loadFromClassLoader(classLoader, "/" + filename );
            if ( result == null ) {
                result = loadFromClassLoader( classLoader, "ui/images/" + filename );
                if ( result == null ) {
                    result = getImageAsResourceInternal( filename );
                }
            }
        }
        return result;
    }

    /**
     * Load image from several sources.
     */
    public static VaadinUniversalImage getUniversalImage(ClassLoader classLoader, String filename ) {
        if ( StringUtils.isBlank( filename ) ) {
            // log.logError( "Unable to load blank image [" + filename + "]", new Exception() );
            return getImageAsResource( NO_IMAGE );
        }

        VaadinUniversalImage result = null;
        if ( SvgSupport.isSvgEnabled() ) {
            result = getUniversalImageInternal( classLoader, SvgSupport.toSvgName( filename ) );
        }

        // if we haven't loaded SVG attempt to use PNG
        if ( result == null ) {
            result = getUniversalImageInternal( classLoader, SvgSupport.toPngName( filename ) );
        }

        // if we can't load PNG, use default "no_image" graphic
        if ( result == null ) {
            log.logError( "Unable to load image [" + filename + "]", new Exception() );
            result = getImageAsResource( NO_IMAGE );
        }
        return result;
    }


    /**
     * Internal image loading by ClassLoader.getResourceAsStream.
     */
    private static VaadinUniversalImage loadFromClassLoader(ClassLoader classLoader, String location ) {
        InputStream s = null;
        try {
            s = classLoader.getResourceAsStream( location );
        } catch ( Throwable t ) {
            log.logDebug( "Unable to load image from classloader [" + location + "]", t );
        }
        if ( s == null ) {
            return null;
        }
        try {
            return loadImage( s, location );
        } finally {
            IOUtils.closeQuietly( s );
        }
    }

    /**
     * Internal image loading by Thread.currentThread.getContextClassLoader.getResource.
     */
    private static VaadinUniversalImage loadFromCurrentClasspath(String location ) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if ( cl == null ) {
            // Can't count on Thread.currentThread().getContextClassLoader() being non-null on Mac
            // Have to provide some fallback
            cl = VaadinSvgImageUtil.class.getClassLoader();
        }
        URL res = null;
        try {
            res = cl.getResource( location );
        } catch ( Throwable t ) {
            log.logDebug( "Unable to load image from classloader [" + location + "]", t );
        }
        if ( res == null ) {
            return null;
        }
        InputStream s;
        try {
            s = res.openStream();
        } catch ( IOException ex ) {
            return null;
        }
        if ( s == null ) {
            return null;
        }
        try {
            return loadImage(s, location );
        } finally {
            IOUtils.closeQuietly( s );
        }
    }

    /**
     * Internal image loading from Hop's user.dir VFS.
     */
    private static VaadinUniversalImage loadFromBasedVFS(String location ) {
        try {
            FileObject imageFileObject = HopVfs.getFileSystemManager().resolveFile( base, location );
            InputStream s = HopVfs.getInputStream( imageFileObject );
            if ( s == null ) {
                return null;
            }
            try {
                return loadImage(s, location );
            } finally {
                IOUtils.closeQuietly( s );
            }
        } catch ( FileSystemException ex ) {
            return null;
        }
    }

    /**
     * Internal image loading from Hop's VFS.
     */
    private static VaadinUniversalImage loadFromSimpleVFS( String location ) {
        try {
            InputStream s = HopVfs.getInputStream( location );
            if ( s == null ) {
                return null;
            }
            try {
                return loadImage(s, location );
            } finally {
                IOUtils.closeQuietly( s );
            }
        } catch ( HopFileException e ) {
            // do nothing. try to load next
        }
        return null;
    }

    /**
     * Load image from InputStream as bitmap image, or SVG image conversion to bitmap image.
     */
    private static VaadinUniversalImage loadImage(InputStream in, String filename ) {
        if ( !SvgSupport.isSvgName( filename ) ) {
            // bitmap image
            Image image = new Image(new StreamResource(filename, () -> {
                try{
                    return new FileInputStream(new File(filename));
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                return null;
            }), filename);
            return new VaadinUniversalImageBitmap(image, zoomFactor );
        } else {
            // svg image - need to convert to bitmap
            try {
                return new VaadinUniversalImageSvg( SvgSupport.loadSvgImage( in ) );
            } catch ( Exception ex ) {
                throw new RuntimeException( ex );
            }
        }
    }
}
