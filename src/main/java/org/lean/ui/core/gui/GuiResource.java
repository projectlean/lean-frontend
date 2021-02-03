package org.lean.ui.core.gui;

import com.vaadin.flow.component.html.Image;
import org.apache.hop.core.SwtUniversalImage;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.plugins.ActionPluginType;
import org.apache.hop.core.plugins.IPluginTypeListener;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.plugins.TransformPluginType;
import org.apache.hop.ui.hopgui.ISingletonProvider;
import org.apache.hop.ui.hopgui.ImplementationLoader;
import org.apache.hop.ui.util.SwtSvgImageUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.lean.core.VaadinUniversalImage;
import org.lean.core.gui.plugin.GuiRegistry;
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
        Image img = new Image(location, "");
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
            VaadinUniversalImage svg = VaadinSvgImageUtil.getUniversalImage(classLoader, location);
            image = getZoomedImage(svg, width, height);
            imageMap.put(location, image);
        }
        return image;
    }

    private Image getZoomedImage(VaadinUniversalImage universalImage, int width, int height) {
        return universalImage.getAsBitmapForSize( (int)(zoomFactor*width), (int)(zoomFactor*height) );
    }
}
