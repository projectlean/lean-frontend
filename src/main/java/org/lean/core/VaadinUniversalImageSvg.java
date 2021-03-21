package org.lean.core;

import com.vaadin.flow.component.html.Image;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.ext.awt.image.codec.png.PNGRegistryEntry;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.hop.core.SwingUniversalImage;
import org.apache.hop.core.SwingUniversalImageSvg;
import org.apache.hop.core.svg.SvgImage;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

public class VaadinUniversalImageSvg extends VaadinUniversalImage{

    private final GraphicsNode svgGraphicsNode;
    private final Dimension2D svgGraphicsSize;

    private SvgImage svgImage;

    static {
        // workaround due to known issue in batik 1.8 - https://issues.apache.org/jira/browse/BATIK-1125
        ImageTagRegistry registry = ImageTagRegistry.getRegistry();
        registry.register( new PNGRegistryEntry() );
    }

    public VaadinUniversalImageSvg( SvgImage svg ) {
        svgImage = svg;
        // get GraphicsNode and size from svg document
        UserAgentAdapter userAgentAdapter = new UserAgentAdapter();
        DocumentLoader documentLoader = new DocumentLoader( userAgentAdapter );
        BridgeContext ctx = new BridgeContext( userAgentAdapter, documentLoader );
        GVTBuilder builder = new GVTBuilder();
        svgGraphicsNode = builder.build( ctx, svg.getDocument() );
        svgGraphicsSize = ctx.getDocumentSize();
    }

    // TODO: Swing --> remove?
    @Override
    protected Image renderSimple(int width, int height ) {

        Image image = new Image(svgImage.getDocument().toString(), "");
        image.setWidth(width + "px");
        image.setHeight(height + "px");
        return image;

    }

    // TODO: Swing --> remove?
    @Override
    protected Image renderRotated(int width, int height, double angleRadians ) {
        return null;
    }

}
