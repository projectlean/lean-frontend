package org.lean.core;

import com.vaadin.flow.component.html.Image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

public abstract class VaadinUniversalImage {

    private Map<String, Image> cache = new TreeMap<>();

    protected abstract Image renderSimple(int width, int height );

    protected abstract Image renderRotated(int width, int height, double angleRadians );

    /**
     * Method getAsBitmapForSize(..., angle) can't be called, because it returns bigger picture.
    */
    public synchronized Image getAsBitmapForSize(int width, int height ) {

        String key = width + "x" + height;
        Image result = cache.get( key );
        if ( result == null ) {
            result = renderSimple(width, height );
            cache.put( key, result );
        }
        return result;
    }

   /**
     * Draw rotated image on double canvas size. It required against lost corners on rotate.
   */
    public synchronized Image getAsBitmapForSize(int width, int height, double angleRadians ) {
        int angleDegree = (int) Math.round( Math.toDegrees( angleRadians ) );
        while ( angleDegree < 0 ) {
            angleDegree += 360;
        }
        angleDegree %= 360;
        angleRadians = Math.toRadians( angleDegree );

        String key = width + "x" + height + "/" + angleDegree;
        Image result = cache.get( key );
        if ( result == null ) {
            result = renderRotated(width, height, angleRadians );
            cache.put( key, result );
        }

        return result;
    }

}
