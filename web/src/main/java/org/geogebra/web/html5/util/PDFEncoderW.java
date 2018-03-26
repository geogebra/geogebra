package org.geogebra.web.html5.util;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper class for the Whammy.js library.
 *
 * adapted from GifShot class
 */
public class PDFEncoderW implements Encoder {

	/**
	 * Reference to the gif object created internally.
	 */
	private JavaScriptObject internal;


	private String filename;

	private EuclidianViewW ev;

	private Context2d ctx;

	private GGraphics2D g4copy;

	private boolean firstPage = true;

	/**
	 * @param frameDelay
	 *            delay between the frames in milliseconds
	 * @param repeat
	 *            true to repeat the animation
	 */
	public PDFEncoderW(EuclidianViewWInterface view, String filename0) {

		this.ev = (EuclidianViewW) view;
		this.filename = filename0;

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
			ev.addPagePDF(ctx);
		}
		ev.exportPaintPre(g4copy, 1, false);
		ev.drawObjects(g4copy);
	}

	/**
	 * Finishes the internal gif object and starts rendering.
	 */
	@Override
	public String finish(int width, int height) {
		ev.getApplication().setExporting(ExportType.NONE, 1);

		return ev.getPDF(ctx);
	}


	/**
	 * Load JS and clear state.
	 */
	public void initialize() {
		if (!ev.canvas2PdfLoaded()) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.canvas2Pdf());
		}

		double scale = 1;
		int width = (int) Math.floor(ev.getExportWidth() * scale);
		int height = (int) Math.floor(ev.getExportHeight() * scale);

		EuclidianView view2 = null;

		ctx = ev.getCanvas2PDF(width, height).cast();
		g4copy = new GGraphics2DW(ctx);
		ev.getApplication().setExporting(ExportType.PDF_HTML5, scale);


	}

}
