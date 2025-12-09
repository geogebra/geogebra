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

package org.geogebra.web.html5.css;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;
import org.gwtproject.resources.client.TextResource;

/**
 * PDF.js resources
 */
@Resource
public interface PDFResources extends ClientBundle {

	PDFResources INSTANCE = new PDFResourcesImpl();

	@Source("pdfjs_dist/build/pdf.min.mjs")
	TextResource pdfJs();

	@Source("pdfjs_dist/build/pdf.worker.min.mjs")
	TextResource pdfWorkerJs();
}