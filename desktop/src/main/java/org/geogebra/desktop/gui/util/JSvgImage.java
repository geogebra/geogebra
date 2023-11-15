package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.w3c.dom.svg.SVGDocument;

import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.bridge.BridgeContext;
import io.sf.carte.echosvg.bridge.DocumentLoader;
import io.sf.carte.echosvg.bridge.GVTBuilder;
import io.sf.carte.echosvg.bridge.UserAgent;
import io.sf.carte.echosvg.bridge.UserAgentAdapter;
import io.sf.carte.echosvg.gvt.GraphicsNode;

public class JSvgImage {

	private final GraphicsNode node;

	private BufferedImage image;
	private JSvgImage(SVGDocument doc) {
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(userAgent);
		BridgeContext ctx = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		GVTBuilder builder = new GVTBuilder();
		node = builder.build(ctx, doc);
	}

	public static JSvgImage fromContent(String content) {
		Reader reader = new StringReader(content);
		String uri = "file:make-something-up";
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		SVGDocument doc;
		try {
			doc = f.createSVGDocument(uri, reader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new JSvgImage(doc);
	}

	/**
	 * Method to fetch the SVG image from an url
	 * @param url the url from which to fetch the SVG image
	 */
	public static JSvgImage fromUrl(String url) {
		String uri = "file:make-something-up";
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		SVGDocument doc = null;
		try {
			doc = f.createSVGDocument(url);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new JSvgImage(doc);
	}


	/**
	 * Method to paint the icon using Graphics2D. Note that the scaling factors have nothing to do with the zoom
	 * operation, the scaling factors set the size your icon relative to the other objects on your canvas.
	 * @param g the graphics context used for drawing
	 * @param x the X coordinate of the top left corner of the icon
	 * @param y the Y coordinate of the top left corner of the icon
	 * @param scaleX the X scaling to be applied to the icon before drawing
	 * @param scaleY the Y scaling to be applied to the icon before drawing
	 */
	public void paint(Graphics2D g, int x, int y, double scaleX, double scaleY) {
		AffineTransform oldTransform = g.getTransform();
		AffineTransform transform =
				new AffineTransform(scaleX, 0.0, 0.0, scaleY, x, y);
		node.setTransform(transform);
		node.paint(g);
		node.setTransform(oldTransform);
	}

	public void paint(Graphics2D g) {
		node.paint(g);
	}

	public int getWidth() {
		return (int) node.getPrimitiveBounds().getWidth();
	}

	public int getHeight() {
		return (int) node.getPrimitiveBounds().getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}
}
