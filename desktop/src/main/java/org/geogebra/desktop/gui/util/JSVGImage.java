package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.geogebra.desktop.util.UtilD;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.bridge.BridgeContext;
import io.sf.carte.echosvg.bridge.DocumentLoader;
import io.sf.carte.echosvg.bridge.GVTBuilder;
import io.sf.carte.echosvg.bridge.UserAgent;
import io.sf.carte.echosvg.bridge.UserAgentAdapter;
import io.sf.carte.echosvg.gvt.GraphicsNode;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class JSVGImage {
	private static final String NO_URI = "file:nouri";
	private static final String BLANK_SVG
			= "data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\"/>";
	private final GraphicsNode node;
	private final float width;
	private final float height;

	/**
	 *
	 * @param doc The SVG document.
	 */
	private JSVGImage(SVGDocument doc) {
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(userAgent) {
			@Override
			public Document loadDocument(String uri) throws IOException {
				return documentFactory.createSVGDocument(BLANK_SVG);
			}

			@Override
			public Document loadDocument(String uri, InputStream is) throws IOException {
				return documentFactory.createSVGDocument(BLANK_SVG);
			}
		};
		BridgeContext ctx = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		GVTBuilder builder = new GVTBuilder();
		node = builder.build(ctx, doc);
		SVGSVGElement rootElement = doc.getRootElement();
		width =  rootElement.getWidth().getBaseVal().getValue();
		height = rootElement.getHeight().getBaseVal().getValue();
	}

	/**
	 * Create {@link JSVGImage} from file
	 *
	 * @param file of the svg.
	 * @return the new {@link JSVGImage}.
	 * @throws IOException if there is some I/O issue.
	 */
	public static JSVGImage fromFile(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		String content = UtilD.loadIntoString(is);
		is.close();
		return fromContent(content);
	}

	/**
	 * Create {@link JSVGImage} from SVG string content.
	 *
	 * @param content of the SVG.
	 * @return the new {@link JSVGImage}.
	 */
	public static JSVGImage fromContent(String content) {
		Reader reader = new StringReader(content);
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		SVGDocument doc;
		try {
			doc = f.createSVGDocument(NO_URI, reader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new JSVGImage(doc);
	}

	/**
	 * Method to fetch the SVG image from an url
	 * @param url the url from which to fetch the SVG image
	 */
	public static JSVGImage fromUrl(String url) {
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		try {
			SVGDocument doc = f.createSVGDocument(url);
			return new JSVGImage(doc);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method to paint the image using Graphics2D.
	 *
	 * @param g the graphics context used for drawing
	 * @param x the X coordinate of the top left corner of the image
	 * @param y the Y coordinate of the top left corner of the image
	 * @param scaleX the X scaling to be applied to the image before drawing
	 * @param scaleY the Y scaling to be applied to the image before drawing
	 */
	public void paint(Graphics2D g, int x, int y, double scaleX, double scaleY) {
		AffineTransform oldTransform = g.getTransform();
		AffineTransform transform = new AffineTransform(scaleX, 0.0, 0.0, scaleY, x, y);
		node.setTransform(transform);
		node.paint(g);
		node.setTransform(oldTransform);
	}

	/**
	 * Paints the SVG to the graphics.
	 *
	 * @param g to paint to.
	 */
	public void paint(Graphics2D g) {
		node.paint(g);
	}

	/**
	 *
	 * @return width of the whole SVG.
	 */
	public float getWidth() {
		return width;
	}

	/**
	 *
	 * @return height of the whole SVG.
	 */
	public float getHeight() {
		return height;
	}
}
