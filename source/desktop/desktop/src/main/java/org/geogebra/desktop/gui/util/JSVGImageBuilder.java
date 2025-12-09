/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.util;

import static org.geogebra.desktop.gui.util.JSVGConstants.NO_URI;
import static org.geogebra.desktop.gui.util.JSVGConstants.UNSUPPORTED_SVG;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.geogebra.common.jre.io.StreamUtil;
import org.geogebra.desktop.util.LoggerD;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGDocument;

import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.bridge.BridgeException;
import io.sf.carte.echosvg.dom.util.SAXIOException;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class JSVGImageBuilder {

	private static SVGImage unsupportedImage = null;

	private JSVGImageBuilder() {
		// utility class
	}

	/**
	 * Create {@link SVGImage} from file
	 * @param file of the svg.
	 * @return the new {@link SVGImage}.
	 * @throws IOException if there is some I/O issue.
	 */
	public static SVGImage fromFile(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		String content = StreamUtil.loadIntoString(is);
		is.close();
		return fromContent(content);
	}

	/**
	 * Create {@link SVGImage} from SVG string content.
	 * @param content of the SVG.
	 * @return the new {@link SVGImage}.
	 */
	public static SVGImage fromContent(String content) {
		return fromContent(new JSVGModel(content));
	}

	private static SVGImage fromContent(JSVGModel model) {
		model.tidyContent();
		Reader reader = new StringReader(model.content);
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();

		try {
			model.doc = f.createSVGDocument(NO_URI, reader);

		} catch (SAXIOException e) {
			LoggerD.debug(e.getMessage());
		} catch (DOMException | IOException e) {
			LoggerD.debug(e);
			return unsupportedImage();
		}

		return newImage(model);
	}

	private static SVGImage newImage(JSVGModel model) {
		model.nextTry();
		try {
			model.build();
			return new SVGImage(model);
		} catch (JSVGModel.InvalidLinkException | BridgeException ex) {
			LoggerD.debug(ex.getMessage());
			return unsupportedImage();
		} catch (Exception e) {
			LoggerD.debug(e);
			return unsupportedImage();
		}
	}

	static SVGImage unsupportedImage() {
		if (unsupportedImage == null) {
			unsupportedImage = fromContent(UNSUPPORTED_SVG);
		}
		return unsupportedImage;
	}

	/**
	 * Method to fetch the SVG image from an url
	 * @param url the url from which to fetch the SVG image
	 * @return SVG image
	 */
	public static SVGImage fromUrl(URL url) {
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		try {
			SVGDocument doc = f.createSVGDocument(url.toString());
			return newImage(new JSVGModel(doc));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
