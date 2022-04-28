package org.geogebra.web.html5.css;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;
import org.gwtproject.resources.client.TextResource;

@Resource
public interface PDFResources extends ClientBundle {

	PDFResources INSTANCE = new PDFResourcesImpl();

	@Source("pdfjs_dist/pdf.min.js")
	TextResource pdfJs();

	@Source("pdfjs_dist/pdf.worker.min.js")
	TextResource pdfWorkerJs();
}