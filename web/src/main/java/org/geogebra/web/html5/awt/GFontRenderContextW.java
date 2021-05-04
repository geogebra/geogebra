package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GFontRenderContext;

import com.himamis.retex.renderer.web.graphics.JLMContext2d;

import elemental2.dom.TextMetrics;

/**
 * Font rendering context for Web
 */
public class GFontRenderContextW extends GFontRenderContext {

	private JLMContext2d context;

	/**
	 * @param ctx
	 *            conext
	 */
	public GFontRenderContextW(JLMContext2d ctx) {
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
			double width = measure.width;
			return (int) Math.round(width);
		} catch (Exception e) {
			return text.length() * 12;
		}
	}

}
