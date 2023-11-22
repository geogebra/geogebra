package org.geogebra.desktop.gui.util;

import static org.geogebra.desktop.gui.util.JSVGConstants.BLANK_SVG;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

import io.sf.carte.echosvg.bridge.DocumentLoader;
import io.sf.carte.echosvg.bridge.UserAgent;

class SVGDocumentLoaderNoError extends DocumentLoader {
	public SVGDocumentLoaderNoError(UserAgent userAgent) {
		super(userAgent);
	}

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

}
