package org.geogebra.web.html5.util.pdf;

/**
 * Wrapper class for pdf.js
 * 
 * @author laszlo
 *
 */
public class PDFWrapper {
	/**
	 * Constructor
	 * 
	 * @param fileName
	 *            PDF to handle.
	 */
	public PDFWrapper(String fileName) {
		read(fileName);
	}

	private native void read(String filename) /*-{
		var file = $doc.querySelector('input[type=file]').files[0];
		var reader = new FileReader();
		var that = this;

		reader
				.addEventListener(
						"load",
						function() {
							var src = reader.result;
							//		@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)(src);
							that.@org.geogebra.web.html5.util.pdf.PDFWrapper::load(Ljava/lang/String;)(src);
						}, false);

		if (file) {
			reader.readAsDataURL(file);
		}

	}-*/;

	private native void load(String src) /*-{
		$wnd.PDFJS.disableWorker = true;
		var loadingTask = $wnd.PDFJS.getDocument(src);
		loadingTask.promise
				.then(
						function(pdf) {
							@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)('PDF loaded');

							var pageNumber = 1;
							// choose which page
							if (pdf.numPages > 1) {
								pageNumber = Math
										.round(prompt("document has "
												+ pdf.numPages
												+ " pages.\nWhich page?") * 1);
							}
							pdf
									.getPage(pageNumber)
									.then(
											function(page) {
												@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)('Page loaded');

												var scale = 1;
												var viewport = page
														.getViewport(scale);

												return page
														.getOperatorList()
														.then(
																function(opList) {
																	var svgGfx = new PDFJS.SVGGraphics(
																			page.commonObjs,
																			page.objs);
																	return svgGfx
																			.getSVG(
																					opList,
																					viewport)
																			.then(
																					function(
																							svg) {
																						svgs = (new XMLSerializer())
																								.serializeToString(svg);
																						// convert to base64 URL for <img>
																						document
																								.getElementById('output').src = "data:image/svg+xml;base64,"
																								+ btoa(unescape(encodeURIComponent(svgs)));
																						console
																								.log(svgs);
																					});
																});
											});
						},
						function(reason) {
							// PDF loading error
							@org.geogebra.common.util.debug.Log::error(Ljava/lang/String;)(reason);
						});
	}-*/;

}
