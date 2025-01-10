package com.himamis.retex.renderer.web.geom;

import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper;
import com.himamis.retex.renderer.web.graphics.FontGlyph;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

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

	public void fill(JLMContext2d ctx) {
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
