package org.geogebra.web.html5.util.pdf;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.File;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Wrapper class for pdf.js
 * 
 * @author laszlo
 *
 */
public class PDFWrapper {

	private PDFListener listener;
	private int pageNumber = 1;
	private PDFDocumentProxy document;

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

		/**
		 * After the pdf loaded, the progress bar should be finished quickly.
		 * 
		 * @param result
		 *            true if the loading of the pdf was successful
		 */
		void finishLoading(boolean result);

		/**
		 * Sets the value of the progress bar for the given percent.
		 * 
		 * @param percent
		 *            the new value of the progress bar
		 */
		void setProgressBarPercent(double percent);

	}

	/**
	 * Constructor
	 * 
	 * @param file
	 *            PDF to handle.
	 * @param listener
	 *            to communicate with PDF container.
	 */
	public PDFWrapper(File file, PDFListener listener) {
		this.listener = listener;
		read(file);
	}

	private void setProgressBarPercent(double percent) {
		listener.setProgressBarPercent(percent);
	}

	private void read(File file) {
		FileReader reader = new FileReader();
		reader.onprogress = event -> {
			if (event.lengthComputable) {
				double percent = (event.loaded / event.total) * 100;
				setProgressBarPercent(percent);
			}
			return null;
		};
		reader.addEventListener("load", evt -> {
			load(reader.result.asString());
		});

		if (Js.isTruthy(file)) {
			reader.readAsDataURL(file);
		}
	}

	private void load(String src) {
		PdfDocumentLoadingTask task = PdfJsLib.get().getDocument(src);
		task.promise.then(document -> {
			this.document = document;
			listener.finishLoading(true);
			getPage();
			return null;
		});
	}

	private void getPage() {
		HTMLCanvasElement canvas = (HTMLCanvasElement)
				DomGlobal.document.createElement("canvas");
		document.getPage(pageNumber).then(page -> {
			PageViewPort viewport = page.getViewport(getViewportOptions());
			RenderTask renderTask = page.render(getRendererContext(viewport,
					Js.uncheckedCast(canvas.getContext("2d"))));
			canvas.width = viewport.width;
			canvas.height = viewport.height;
			return renderTask.promise;
		}).then(dummy -> {
			onPageDisplay(canvas.toDataURL());
			return null;
		});
	}

	private JsPropertyMap<Object> getRendererContext(PageViewPort viewport,
			CanvasRenderingContext2D context2d) {
		JsPropertyMap<Object> rendererContext = JsPropertyMap.of();
		rendererContext.set("canvasContext", context2d);
		rendererContext.set("viewport", viewport);
		return rendererContext;
	}

	private JsPropertyMap<Object> getViewportOptions() {
		JsPropertyMap<Object> options = JsPropertyMap.of();
		options.set("scale", 2);
		return options;
	}

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
	public int getNumberOfPages() {
		return document.numPages;
	}

	/**
	 * load previous page of the PDF if any.
	 */
	public void previousPage() {
		if (pageNumber > 1) {
			setPageNumber(pageNumber - 1);
			getPage();
		}
	}

	/**
	 * load next page of the PDF if any.
	 */
	public void nextPage() {
		if (pageNumber < getNumberOfPages()) {
			setPageNumber(pageNumber + 1);
			getPage();
		}
	}

	/**
	 * 
	 * @return the current page index.
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * 
	 * @param num
	 *            page number to set.
	 * @return if page change was successful.
	 */
	public boolean setPageNumber(int num) {
		if (num > 0 && num <= getNumberOfPages()) {
			pageNumber = num;
			return true;
		}
		return false;
	}
}
