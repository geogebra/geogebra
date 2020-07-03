package org.geogebra.web.html5.util.pdf;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import elemental2.dom.File;
import elemental2.dom.FileReader;
import elemental2.promise.Promise;
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
	private int numberOfPages;
	private int pageNumber = 1;
	private Promise<PDFDocumentProxy> document;

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
		 * Updates the max number of pages
		 * in the client.
		 */
		void updateNumberOfPages();

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
		document = task.promise;
		listener.finishLoading(true);
		getPage();

	}

	private void getPage() {
		Canvas canvas = Canvas.createIfSupported();
		document.then(document -> {
			setNumberOfPages(document.numPages);
			return document.getPage(pageNumber);
		}).then(page -> {
			PageViewPort viewport = page.getViewport(getViewportOptions());
			RenderTask renderTask = page.render(getRendererContext(viewport,
					canvas.getContext2d()));
			canvas.setCoordinateSpaceWidth(viewport.width);
			canvas.setCoordinateSpaceHeight(viewport.height);
			return renderTask.promise;
		}).then(dummy -> {
			onPageDisplay(canvas.toDataUrl());
			return null;
		});
	}

	private JsPropertyMap<Object> getRendererContext(PageViewPort viewport, Context2d context2d) {
		JsPropertyMap<Object> rendererContext = JsPropertyMap.of();
		rendererContext.set("canvasContext", context2d);
		rendererContext.set("viewport", viewport);
		return rendererContext;
	}

	private JsPropertyMap<Object> getViewportOptions() {
		JsPropertyMap<Object> options = JsPropertyMap.of();
		options.set("scale", 1);
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
		return numberOfPages;
	}

	/**
	 * 
	 * @param numberOfPages
	 *            to set.
	 */
	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
		listener.updateNumberOfPages();
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
		if (pageNumber < numberOfPages) {
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
		if (num > 0 && num <= numberOfPages) {
			pageNumber = num;
			return true;
		}
		return false;
	}
}
