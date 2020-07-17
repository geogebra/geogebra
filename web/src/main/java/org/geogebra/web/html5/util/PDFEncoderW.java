package org.geogebra.web.html5.util;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.export.ExportLoader;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper class for the canvas2pdf.js library to allow multi-page PDF export
 *
 * adapted from GifShot class
 */
public class PDFEncoderW implements Encoder {

	private EuclidianViewW ev;

	private Context2d ctx;

	private GGraphics2D g4copy;

	private boolean firstPage = true;

	/**
	 * @param view
	 *            EV to export
	 */
	public PDFEncoderW(EuclidianViewWInterface view) {

		this.ev = (EuclidianViewW) view;

		initialize();
	}

	/**
	 * @param url
	 *            adds a new frame
	 */
	@Override
	public void addFrame(String url) {
		if (firstPage) {
			firstPage = false;
		} else {
			addPagePDF(ctx);
		}
		ev.exportPaintPre(g4copy, 1, false);
		ev.drawObjects(g4copy);
	}

	/**
	 * Finish PDF and return it
	 */
	@Override
	public String finish(int width, int height) {
		ev.getApplication().setExporting(ExportType.NONE, 1);

		return getPDF(ctx);
	}

	/**
	 * Load JS and set up
	 */
	public void initialize() {

		double scale = 1;
		int width = (int) Math.floor(ev.getExportWidth() * scale);
		int height = (int) Math.floor(ev.getExportHeight() * scale);

		ctx = getContext(width, height);

		g4copy = new GGraphics2DW(ctx);
		ev.getApplication().setExporting(ExportType.PDF_HTML5, scale);

	}

	/**
	 * 
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @return canvas2pdf object
	 */
	public static native Context2d getCanvas2PDF(double width,
			double height) /*-{
		if ($wnd.canvas2pdf) {
			return new $wnd.canvas2pdf.PdfContext(width, height);
		}

		return null;
	}-*/;

	/**
	 * 
	 * @param pdfcontext
	 *            canvas2pdf object
	 * @return the resulting PDF (as base64 URL)
	 */
	public static native String getPDF(JavaScriptObject pdfcontext) /*-{
		return pdfcontext.getPDFbase64();
	}-*/;

	/**
	 * @param ctx
	 *            context
	 */
	public static native void addPagePDF(JavaScriptObject ctx) /*-{
		ctx.addPage();
	}-*/;

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @return context if available (or null)
	 */
	public static Context2d getContext(int width, int height) {

		if (ExportLoader.getPako() == null) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.pakoJs());
		}

		if (ExportLoader.getCanvas2Pdf() == null) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.canvas2Pdf());
		}

		return PDFEncoderW.getCanvas2PDF(width, height);
	}

}
