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