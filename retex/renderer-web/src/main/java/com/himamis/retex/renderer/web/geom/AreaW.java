package com.himamis.retex.renderer.web.geom;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Area;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

/**
 * 
 * Just needs to hold serveal objects eg Arrow + extension (ShapeW +
 * Rectangle2DW) or eg (Arrow + extension + Arrow)
 *
 */
public class AreaW
		implements com.himamis.retex.renderer.share.platform.geom.Area {

	private ArrayList<Shape> shapes = new ArrayList<>();

	public AreaW(Shape s) {
		shapes.add(s);
	}

	public AreaW(ArrayList<Shape> s) {
		shapes.addAll(s);
	}

	public Rectangle2D getBounds2DX() {

		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;

		int n = shapes.size();
		for (int i = 0; i < n; i++) {
			Shape shape = shapes.get(i);
			Rectangle2D bounds = shape.getBounds2DX();

			minX = Math.min(minX, bounds.getX());
			minY = Math.min(minY, bounds.getY());
			maxX = Math.max(maxX, bounds.getX() + bounds.getWidth());
			maxY = Math.max(maxY, bounds.getY() + bounds.getHeight());

		}

		return new Rectangle2DW(minX, minY, maxX - minX, maxY - minY);
	}

	public void add(Area a) {
		shapes.addAll(((AreaW) a).getShapes());
	}

	public Area duplicate() {
		return new AreaW(shapes);
	}

	public void fill(JLMContext2d ctx) {

		int n = shapes.size();
		for (int i = 0; i < n; i++) {
			ctx.fill(shapes.get(i));
		}

	}

	public ArrayList<Shape> getShapes() {
		return shapes;
	}

	/**
	 * not needed in web
	 */
	public void scale(double x) {
		FactoryProvider.getInstance().debug("AreaW.scale not implemented " + x);
	}

	public void translate(double x, double y) {
		int n = shapes.size();
		for (int i = 0; i < n; i++) {
			Shape shape = shapes.get(i);
			if (shape instanceof ShapeW) {
				((ShapeW)shape).translate(x, y);
			} else if (shape instanceof Rectangle2DW) {
				((Rectangle2DW) shape).translate(x, y);
			} else {
				FactoryProvider.getInstance().debug("other type");
			}
		}
		
	}

}
