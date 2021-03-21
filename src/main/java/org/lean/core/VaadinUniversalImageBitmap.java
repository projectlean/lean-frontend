package org.lean.core;


import com.vaadin.flow.component.html.Image;

public class VaadinUniversalImageBitmap extends VaadinUniversalImage{

    private final Image bitmap;
    private final double zoomFactor;

    public VaadinUniversalImageBitmap( Image bitmap, double zoomFactor ) {
        this.bitmap = bitmap;
        this.zoomFactor = zoomFactor;
    }

    @Override
    protected Image renderSimple(int width, int height ) {
        return renderRotated( width, height, 0d );
    }

    @Override
    protected Image renderRotated( int width, int height, double angleRadians ) {
        return null;
    }
}
