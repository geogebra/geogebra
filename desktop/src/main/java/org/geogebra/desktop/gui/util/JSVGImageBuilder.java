

package org.geogebra.desktop.gui.util;

import static org.geogebra.desktop.gui.util.JSVGConstants.BLANK_SVG;
import static org.geogebra.desktop.gui.util.JSVGConstants.HEADER;
import static org.geogebra.desktop.gui.util.JSVGConstants.NO_URI;
import static org.geogebra.desktop.gui.util.JSVGConstants.UNSUPPORTED_SVG;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.geogebra.common.awt.GColor;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.UtilD;
import org.w3c.dom.svg.SVGDocument;

import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.dom.util.SAXIOException;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class JSVGImageBuilder {

	private static JSVGImage blankImage = null;
	private static JSVGImage unsupportedImage = null;

	private JSVGImageBuilder() {
		// utility class
	}

	/**
	 * Create {@link JSVGImage} from file
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
	 * @param content of the SVG.
	 * @return the new {@link JSVGImage}.
	 */
	public static JSVGImage fromContent(String content) {
		return fromContent(new JSVGModel(content));
	}


	private static JSVGImage fromContent(JSVGModel model) {
		Reader reader = new StringReader(model.content);
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		try {
			model.doc = f.createSVGDocument(NO_URI, reader);
		} catch (SAXIOException se) {
			model.content = fixHeader(model.content);
			return fromContent(model);
		} catch (IOException e) {
			return blankImage();
		}

		return newImage(model);
	}

	private static JSVGImage blankImage() {
		if (blankImage == null) {
			blankImage = fromContent(BLANK_SVG);
		}
		return blankImage;
	}

	private static JSVGImage newImage(JSVGModel model) {
		model.nextTry();
		try {
			model.build();
			return new JSVGImage(model);
		} catch (Exception e) {
			if (model.isMaxTriesReached()) {
				return unsupportedImage();
			}
			model.content = fixHeader(ImageManagerD.fixSVG(model.content));
			return fromContent(model);
		}
	}

	private static JSVGImage unsupportedImage() {
		if (unsupportedImage == null) {
			unsupportedImage = fromContent(UNSUPPORTED_SVG);
		}
		return unsupportedImage;
	}

	private static String fixHeader(String content) {
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
	public static JSVGImage fromUrl(URL url) {
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		try {
			SVGDocument doc = f.createSVGDocument(url.toString());
			return newImage(new JSVGModel(doc));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static JSVGImage tint(SVGDocument doc, GColor color) {
		doc.getDocumentElement().setAttribute("fill", color.toString());
		((JSVGModel) doc).build();
		return new JSVGImage((JSVGModel) doc);
	}
}
