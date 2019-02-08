package com.himamis.retex.renderer.web.geom;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Area;
import com.himamis.retex.renderer.share.platform.geom.AreaBase;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

/**
 * 
 * Just needs to hold serveal objects eg Arrow + extension (ShapeW +
 * Rectangle2DW) or eg (Arrow + extension + Arrow)
 *
 */
public class AreaW extends AreaBase {

	public AreaW(Shape shape) {
		super(shape);
	}

	public AreaW(ArrayList<Shape> shape) {
		super(shape);
	}

	public AreaW() {
		super();
	}

	@Override
	protected Rectangle2D createRectangle(double x, double y, double width, double height) {
		return new Rectangle2DW(x, y, width, height);
	}

	@Override
	public Area duplicate() {
		return new AreaW(shapes);
	}

	public void fill(JLMContext2d ctx) {

		if (scale != 1) {
			ctx.saveTransform();
			ctx.scale2(scale, scale);
		}

		for (Shape shape : shapes) {
			ctx.fill(shape);
		}

		if (scale != 1) {
			ctx.restoreTransform();
		}

	}

	@Override
	public void translate(double x, double y) {
		if (scale != 1) {
			FactoryProvider.getInstance()
					.debug("warning: AreaW.translate not implemented when scale != 1"
							+ scale + " " + x + " " + y);
		}

		for (Shape shape : shapes) {
			if (shape instanceof ShapeW) {
				((ShapeW) shape).translate(x, y);
			} else if (shape instanceof Rectangle2DW) {
				((Rectangle2DW) shape).translate(x, y);
			}
		}
	}

}
