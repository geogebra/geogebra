/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.awt;

import org.geogebra.common.awt.GFontRenderContext;

import elemental2.dom.TextMetrics;

/**
 * Font rendering context for Web
 */
public class GFontRenderContextW extends GFontRenderContext {

	private JLMContext2D context;

	/**
	 * @param ctx
	 *            context
	 */
	public GFontRenderContextW(JLMContext2D ctx) {
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
