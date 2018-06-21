package org.geogebra.web.html5.util.pdf;

/**
 * Wrapper class for pdf.js
 * 
 * @author laszlo
 *
 */
public class PDFWrapper {
	/**
	 * Interface to communicate with PDF Container.
	 *
	 */
	public interface PDFListener {
		/**
		 * Call this to build image from pdf.
		 * 
		 * @param imgSrc
		 *            the image data as source.
		 */
		void onPageDisplay(String imgSrc);

	}

	private PDFListener listener;
	private int pageCount;
	/**
	 * Constructor
	 * 
	 * @param fileName
	 *            PDF to handle.
	 * @param listener
	 *            to communicate with PDF container.
	 */
	public PDFWrapper(String fileName, PDFListener listener) {
		this.listener = listener;
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
		var loadingTask = $wnd.PDFJS.getDocument(src);
		var that = this;

		loadingTask.promise
				.then(
						function(pdf) {
							@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)('PDF loaded');
							that.@org.geogebra.web.html5.util.pdf.PDFWrapper::setPageCount(I)(pdf.numPages);
							var pageNumber = 1;
							// choose which page

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
																	var svgGfx = new $wnd.PDFJS.SVGGraphics(
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
																						var data = "data:image/svg+xml;base64,"
																								+ btoa(unescape(encodeURIComponent(svgs)));
																						that.@org.geogebra.web.html5.util.pdf.PDFWrapper::onPageDisplay(Ljava/lang/String;)(data);
																					});
																});
											});
						},
						function(reason) {
							// PDF loading error
							@org.geogebra.common.util.debug.Log::error(Ljava/lang/String;)(reason);
						});
	}-*/;

	private void onPageDisplay(String src) {
		if (listener == null) {
			return;
		}
		listener.onPageDisplay(src);
	}

	/**
	 * 
	 * @return the number of pages in the PDF.
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * 
	 * @param pageCount
	 *            to set.
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
}
