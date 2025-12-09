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

package com.himamis.retex.renderer.web.geom;

import org.geogebra.web.awt.JLMContext2D;

import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper;
import com.himamis.retex.renderer.web.graphics.FontGlyph;

/**
 * 
 * Wrapper for Opentype.js Glyph
 *
 */
public class ShapeW implements Shape {

	private FontGlyph outline;
	private Rectangle2D rect;

	// translate (tx,ty) when drawing
	private double tx = 0;
	private double ty = 0;

	public ShapeW(FontGlyph outline, Rectangle2D rect) {
		this.outline = outline;
		this.rect = rect;
	}

	public void fill(JLMContext2D ctx) {
		ctx.translate2(tx, ty);
		outline.fill = ctx.fillStyle;
		OpentypeFontWrapper.drawPath(outline, 0, 0, ctx);
		ctx.translate2(-tx, -ty);
	}

	@Override
	public Rectangle2D getBounds2DX() {
		return rect;
	}

	public void translate(double x, double y) {
		tx += x;
		ty += y;

	}

}
