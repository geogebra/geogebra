package org.geogebra.web.html5.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface PDFResources extends ClientBundle {

	PDFResources INSTANCE = GWT.create(PDFResources.class);

	@Source("org/geogebra/web/resources/js/pdfjs/pdf.min.js")
	TextResource pdfJs();

	@Source("org/geogebra/web/resources/js/pdfjs/pdf.worker.min.js")
	TextResource pdfWorkerJs();
}