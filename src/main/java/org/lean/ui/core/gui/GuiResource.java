package org.lean.ui.core.gui;

import com.vaadin.flow.component.html.Image;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.plugins.IPluginTypeListener;
import org.apache.hop.core.plugins.PluginRegistry;
import org.lean.core.VaadinUniversalImage;
import org.lean.presentation.component.type.LeanComponentPluginType;
import org.lean.presentation.connector.type.LeanConnectorPluginType;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.PropsUi;
import org.lean.ui.util.VaadinSvgImageUtil;

import java.util.Map;

/*
 * colors etc. are allocated once and released once at the end of the program.
 *
 */

public class GuiResource {

    private static GuiResource guiResource;

    private static ILogChannel log = LogChannel.UI;

    private double zoomFactor;

    private VaadinUniversalImage imageFile;
    private VaadinUniversalImage imageFolder;
    private VaadinUniversalImage imageVariable;

    private Map<String, Image> imageMap;

    private GuiResource() {

        getResources();

        // Reload images as required by changes in the plugins
        PluginRegistry.getInstance().addPluginListener( LeanConnectorPluginType.class, new IPluginTypeListener() {
            @Override
            public void pluginAdded(Object serviceObject) {
            }

            @Override
            public void pluginRemoved(Object serviceObject) {
            }

            @Override
            public void pluginChanged( Object serviceObject ) {
            }
        } );

        PluginRegistry.getInstance().addPluginListener( LeanComponentPluginType.class, new IPluginTypeListener() {
            @Override
            public void pluginAdded(Object serviceObject) {
            }

            @Override
            public void pluginRemoved(Object serviceObject) {
            }

            @Override public void pluginChanged(Object serviceObject ) {
                // nothing needed here
            }
        } );

    }

/*
    private static final ISingletonProvider PROVIDER;
    static {
        PROVIDER = (ISingletonProvider) ImplementationLoader.newInstance( GuiResource.class );
    }
    public static final GuiResource getInstance() {
        return (GuiResource) PROVIDER.getInstanceInternal();
    }
*/

    public static GuiResource getInstance(){
        if(guiResource == null){
            guiResource = new GuiResource();
        }
        return guiResource;
    }


    private void getResources() {

        PropsUi props = PropsUi.getInstance();
        loadCommonImages();

    }

    /**
     * Loads an image from a location once. The second time, the image comes from a cache. Because of this, it's important
     * to never dispose of the image you get from here. (easy!) The images are automatically disposed when the application
     * ends.
     *
     * @param location the location of the image resource to load
     * @return the loaded image
     */
    public Image getImage(String location ) {
        Image img = new Image(ConstUi.LEAN_FRONTEND_FOLDER + location, "");
        img.setWidth(ConstUi.SMALL_ICON_SIZE_PX);
        img.setHeight(ConstUi.SMALL_ICON_SIZE_PX);
        return img;
    }

    /**
     * Loads an image from a location once. The second time, the image comes from a cache. Because of
     * this, it's important to never dispose of the image you get from here. (easy!) The images are
     * automatically disposed when the application ends.
     *
     * @param location the location of the image resource to load
     * @param width The height to resize the image to
     * @param height The width to resize the image to
     * @return the loaded image
     */
    public Image getImage(String location, int width, int height) {
        StringBuilder builder = new StringBuilder(location);
        builder.append('|');
        builder.append(width);
        builder.append('|');
        builder.append(height);
        String key = builder.toString();

        Image image = imageMap.get(key);
        if (image == null) {
            VaadinUniversalImage svg = VaadinSvgImageUtil.getImage(location);
            int realWidth = (int) Math.round(zoomFactor * width);
            int realHeight = (int) Math.round(zoomFactor * height);
            image = svg.getAsBitmapForSize(realWidth, realHeight);
//            svg.dispose();
            imageMap.put(key, image);
        }
        return image;
    }

    /**
     * Loads an image from a location once. The second time, the image comes from a cache. Because of
     * this, it's important to never dispose of the image you get from here. (easy!) The images are
     * automatically disposed when the application ends.
     *
     * @param location the location of the image resource to load
     * @param classLoader the ClassLoader to use to locate resources
     * @param width The height to resize the image to
     * @param height The width to resize the image to
     * @return the loaded image
     */
    public Image getImage(String location, ClassLoader classLoader, int width, int height) {
        // Build image key for a specific size
        StringBuilder builder = new StringBuilder(location);
        builder.append('|');
        builder.append(width);
        builder.append('|');
        builder.append(height);
        String key = builder.toString();

        Image image = imageMap.get(key);
        if (image == null) {
            VaadinUniversalImage svg = VaadinSvgImageUtil.getUniversalImage(classLoader, ConstUi.LEAN_FRONTEND_FOLDER + location);
            image = getZoomedImage(svg, width, height);
            imageMap.put(ConstUi.LEAN_FRONTEND_FOLDER + location, image);
        }
        return image;
    }

    private void loadCommonImages(){
        imageFile = VaadinSvgImageUtil.getImageAsResource("ui/images/file.svg");
        imageFolder = VaadinSvgImageUtil.getImageAsResource("ui/images/folder.svg");
        imageVariable = VaadinSvgImageUtil.getImageAsResource("ui/images/variable.svg");

    }

    private Image getZoomedImage(VaadinUniversalImage universalImage, int width, int height) {
        return universalImage.getAsBitmapForSize( (int)(zoomFactor*width), (int)(zoomFactor*height) );
    }

    public Image getImageVariable(){
        return getZoomedImage(imageVariable, 10, 10);
    }

    /**
     * @return the imageArrow
     */
    public Image getImageFolder() {
        return getZoomedImage( imageFolder, ConstUi.SMALL_ICON_SIZE, ConstUi.SMALL_ICON_SIZE );
    }

    /**
     * @return the imageFile
     */
    public Image getImageFile() {
        return getZoomedImage( imageFile, ConstUi.SMALL_ICON_SIZE, ConstUi.SMALL_ICON_SIZE );
    }

}
