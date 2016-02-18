package org.geogebra.desktop.awt;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPolygon;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;

public class GPolygonD implements GPolygon {

	private Polygon polygon;

	public GPolygonD() {
		polygon = new Polygon();
	}

	public Polygon getPolygon() {
		return polygon;
	}
	public boolean intersects(int i, int j, int k, int l) {
		return polygon.intersects(i, j, k, l);
	}

	public GRectangle getBounds() {
		return (GRectangle) polygon.getBounds();
	}

	public GRectangle2D getBounds2D() {
		return (GRectangle2D) polygon.getBounds2D();
	}

	public boolean contains(GRectangle2D rectangle) {
		return false;// polygon.contains((Rectangle2D) rectangle);

	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return (GPathIterator) polygon
				.getPathIterator((AffineTransform) affineTransform);
	}

	public boolean intersects(GRectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	public void reset() {
		polygon.reset();
	}

	public void invalidate() {
		polygon.invalidate();
	}

	public void translate(int deltaX, int deltaY) {
		polygon.translate(deltaX, deltaY);
	}

	public void addPoint(int x, int y) {
		polygon.addPoint(x, y);
	}

	public boolean contains(int x, int y) {
		return polygon.contains(x, y);
	}

	public boolean contains(double x, double y) {
		return polygon.contains(x, y);
	}

	public boolean intersects(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return polygon.intersects(x, y, w, h);
	}

	public boolean contains(double x, double y, double w, double h) {
		return polygon.contains(x, y, w, h);
	}
}
