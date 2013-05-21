package geogebra.html5.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.main.App;

public class GenericShape implements geogebra.html5.awt.GShapeW {

	private geogebra.html5.openjdk.awt.geom.Shape impl;
	
	private GenericShape(){}
	
	
	public GenericShape(geogebra.html5.openjdk.awt.geom.Shape s) {
	    impl = s;
    }


	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	
	public GRectangle getBounds() {
		return new geogebra.html5.awt.GRectangleW(impl.getBounds());
	}

	
	public GRectangle2D getBounds2D() {
		return new geogebra.html5.awt.GRectangle2DW(impl.getBounds2D());
	}

	
	public boolean contains(GRectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.html5.awt.GPathIteratorW(impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(affineTransform)));
	}

	
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return (GPathIterator) impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(at),flatness);
	}

	
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(r.getX(),r.getY(),r.getWidth(),r.getHeight());
	}


	public static geogebra.html5.openjdk.awt.geom.Shape getGawtShape(GShape shape) {
		if (shape == null)
			return null;
		GShape shapeCommon = shape instanceof GeneralPathClipped ?
			((GeneralPathClipped)shape).getGeneralPath() : shape;
				
		if((shapeCommon instanceof geogebra.html5.awt.GShapeW)){
			return ((geogebra.html5.awt.GShapeW)shapeCommon).getGawtShape();
		}
		App.debug("unimplemented class in GenericShape.getGawtShape() "+shapeCommon.getClass());
		return null;
	}


	public geogebra.html5.openjdk.awt.geom.Shape getGawtShape() {
	    return impl;
    }

}
