package org.geogebra.desktop.gui.util;

import static org.geogebra.desktop.gui.util.JSVGConstants.BLANK_SVG;
import static org.geogebra.desktop.gui.util.JSVGConstants.HEADER;
import static org.geogebra.desktop.gui.util.JSVGConstants.NO_URI;
import static org.geogebra.desktop.gui.util.JSVGConstants.UNSUPPORTED_SVG;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.geogebra.desktop.util.ImageManagerD;
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
import io.sf.carte.echosvg.dom.util.SAXIOException;
import io.sf.carte.echosvg.gvt.GraphicsNode;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class JSVGImage {

	private static String content;
	private BridgeContext ctx;
	private GVTBuilder builder;
	private String name;

	private GraphicsNode node;
	private float width = 0;
	private float height = 0;

	private JSVGImage() {
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(userAgent) {
			@Override
			public Document loadDocument(String uri) throws IOException {
				return createBlank();
			}

			@Override
			public Document loadDocument(String uri, InputStream is) throws IOException {
				return createBlank();
			}

			private SVGDocument createBlank() throws IOException {
				return documentFactory.createSVGDocument(BLANK_SVG);
			}

		};
		ctx = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		builder = new GVTBuilder();
	}

	public JSVGImage(String name) {
		this();
		this.name = name;
	}

	private void build(SVGDocument doc) {
		node = builder.build(ctx, doc);
		SVGSVGElement rootElement = doc.getRootElement();
		width = rootElement.getWidth().getBaseVal().getValue();
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
		return fromContent(content, file.getName());
	}

	/**
	 * Create {@link JSVGImage} from SVG string content.
	 * @param content of the SVG.
	 * @param name
	 * @return the new {@link JSVGImage}.
	 */
	public static JSVGImage fromContent(String content, String name) {
		JSVGImage.content = content;
		Reader reader = new StringReader(content);
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		SVGDocument doc;
		try {
			doc = f.createSVGDocument(NO_URI, reader);
		} catch (SAXIOException se) {
			return fromContentXMNS(content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return newImage(name, doc);
	}

	private static JSVGImage newImage(String name, SVGDocument doc) {
		try {
			JSVGImage image = new JSVGImage(name);
			image.build(doc);
			return image;
		} catch (Exception e) {
			return fromContent(name, process(ImageManagerD.fixSVG(content)));
		}
	}

	private static JSVGImage fromContentXMNS (String content){
			Reader reader = new StringReader(process(content));
			SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
			SVGDocument doc;
			try {
				doc = f.createSVGDocument(NO_URI, reader);
			} catch (IOException e) {
				return fromContentXMNS(UNSUPPORTED_SVG);
			}
			return newImage("", doc);
		}

		private static String process (String content){
			int beginIndex = content.indexOf("<svg");
			if (beginIndex == -1) {
				return BLANK_SVG;
			}
			String body = content.substring(beginIndex);
			return HEADER + body;
		}

		/**
		 * Method to fetch the SVG image from an url
		 * @param url the url from which to fetch the SVG image
		 */
		public static JSVGImage fromUrl (URL url){
			SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
			try {
				SVGDocument doc = f.createSVGDocument(url.toString());
				return newImage(url.getPath(), doc);
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
		public void paint (Graphics2D g,int x, int y, double scaleX, double scaleY){
			if (isInvalid()) {
				return;
			}
			AffineTransform oldTransform = g.getTransform();
			AffineTransform transform = new AffineTransform(scaleX, 0.0, 0.0, scaleY, x, y);
			node.setTransform(transform);
			node.paint(g);
			node.setTransform(oldTransform);
		}

		private boolean isInvalid () {
			return node == null;
		}

		/**
		 * Paints the SVG to the graphics.
		 *
		 * @param g to paint to.
		 */
		public void paint (Graphics2D g){
			if (isInvalid()) {
				return;
			}

			node.paint(g);
		}

		/**
		 *
		 * @return width of the whole SVG.
		 */
		public float getWidth () {
			return width;
		}

		/**
		 *
		 * @return height of the whole SVG.
		 */
		public float getHeight () {
			return height;
		}
	}
