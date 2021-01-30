package org.lean.core;


import com.vaadin.flow.component.html.Image;

public class VaadinUniversalImageBitmap extends VaadinUniversalImage{

    private final Image bitmap;
    private final double zoomFactor;

    public VaadinUniversalImageBitmap( Image bitmap, double zoomFactor ) {
        this.bitmap = bitmap;
        this.zoomFactor = zoomFactor;
    }

/*
    @Override
    public synchronized void dispose() {
        super.dispose();
        if ( !bitmap.isDisposed() ) {
            bitmap.dispose();
        }
    }
*/

/*
    @Override
    protected Image renderSimple() {
        return bitmap;
    }
*/

    @Override
    protected Image renderSimple(int width, int height ) {
        return renderRotated( width, height, 0d );
    }

    @Override
    protected Image renderRotated( int width, int height, double angleRadians ) {
/*
        Image result = new Image( width * 2, height * 2 );

        GC gc = new GC( result );

        int bw = bitmap.getBounds().width;
        int bh = bitmap.getBounds().height;
        Transform affineTransform = new Transform( device );
        affineTransform.translate( width, height );
        affineTransform.rotate( (float) Math.toDegrees( angleRadians ) );
        affineTransform.scale( (float) zoomFactor * width / bw, (float) zoomFactor * height / bh );
        gc.setTransform( affineTransform );

        gc.drawImage( bitmap, 0, 0, bw, bh, -bw / 2, -bh / 2, bw, bh );

        gc.dispose();

        return result;
*/
        return null;
    }
}
