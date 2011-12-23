package geogebra.awt;

import java.awt.Shape;

import geogebra.euclidian.GeneralPathClipped;

public class Area implements geogebra.common.awt.Area{
	private java.awt.geom.Area impl;
	public Area(GeneralPathClipped boundingPath) {
		impl = new java.awt.geom.Area(boundingPath);
	}
	public Area() {
		impl = new java.awt.geom.Area();
	}
	public Area(Shape shape) {
		impl = new java.awt.geom.Area(shape);
	}
	public static java.awt.geom.Area getAWTArea(geogebra.common.awt.Area a){
		if(!(a instanceof Area))
			return null;
		return ((Area)a).impl;
	}
	public void subtract(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.subtract(((Area)a).impl);
	}
	public void add(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.add(((Area)a).impl);
	}
	public void intersect(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.intersect(((Area)a).impl);
	}
	public void exclusiveOr(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.exclusiveOr(((Area)a).impl);
	}
}
