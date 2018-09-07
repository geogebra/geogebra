package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GFontRenderContext;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * Font rendering context for Web
 */
public class GFontRenderContextW extends GFontRenderContext {

	private Context2d context;

	/**
	 * @param ctx
	 *            conext
	 */
	public GFontRenderContextW(Context2d ctx) {
		this.context = ctx;
	}

	/**
	 * @param text
	 *            text
	 * @param cssFontString
	 *            CSS font definition
	 * @return width in PX
	 */
	public int measureText(String text, String cssFontString) {
		String oldFont = context.getFont();
		try {
			context.setFont(cssFontString);
			TextMetrics measure = context.measureText(text);
			context.setFont(oldFont);
			double width = measure.getWidth();
			return (int) Math.round(width);
		} catch (Exception e) {
			return text.length() * 12;
		}
	}

}
