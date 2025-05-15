package org.geogebra.web.html5.util;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.export.Canvas2Pdf;

import jsinterop.base.JsPropertyMap;

/**
 * Wrapper class for the canvas2pdf.js library to allow multi-page PDF export
 *
 * adapted from GifShot class
 */
public class PDFEncoderW implements FrameCollectorW {

	private final StringConsumer consumer;
	private EuclidianViewW ev;

	private Canvas2Pdf.PdfContext ctx;

	private GGraphics2D g4copy;

	public PDFEncoderW(StringConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void addFrame(EuclidianViewWInterface view,
			double exportScale) {
		if (ev == null) {
			initialize(view);
		} else {
			ctx.addPage();
		}
		ev.exportPaintPre(g4copy, 1, false);
		ev.drawObjects(g4copy);
	}

	@Override
	public void finish(int width, int height) {
		ev.getApplication().setExporting(ExportType.NONE, 1);
		consumer.consume(ctx.getPDFbase64());
	}

	/**
	 * Load JS and set up
	 */
	private void initialize(EuclidianViewWInterface ev) {
		this.ev = (EuclidianViewW) ev;
		double scale = 1;
		int width = (int) Math.floor(ev.getExportWidth() * scale);
		int height = (int) Math.floor(ev.getExportHeight() * scale);

		ctx = getContext(width, height, null);

		g4copy = new GGraphics2DW(ctx);
		ev.getApplication().setExporting(ExportType.PDF_HTML5, scale);

	}

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @return context if available (or null)
	 */
	public static Canvas2Pdf.PdfContext getContext(int width, int height,
			JsPropertyMap<?> pageOptions) {
		if (Canvas2Pdf.get() != null) {
			return new Canvas2Pdf.PdfContext(width, height, pageOptions);
		}

		return null;
	}
}
