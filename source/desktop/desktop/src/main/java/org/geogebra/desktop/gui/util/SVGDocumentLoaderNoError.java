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
