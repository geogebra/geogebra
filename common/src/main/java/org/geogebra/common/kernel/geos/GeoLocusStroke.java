package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for polylines created using pen
 * 
 * @author Zbynek
 */
public class GeoLocusStroke extends GeoLocus
		implements MatrixTransformable, Translateable {

	/**
	 * @param cons
	 *            construction
	 */
	public GeoLocusStroke(Construction cons) {
		super(cons);
		setVisibleInView3D(false);
	}



	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.PENSTROKE;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return label;
	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return false;
	}

	@Override
	public boolean isLabelVisible() {
		return false;
	}

	@Override
	public boolean needToShowBothRowsInAV() {
		return false;
	}

	@Override
	public GeoElement copy() {
		GeoLocusStroke ret = new GeoLocusStroke(cons);
		ret.set(this);
		return ret;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;
			pt.x = a00 * x + a01 * y;
			pt.y = a10 * x + a11 * y;
		}

	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;
			double z = a20 * x + a21 * y + a22;
			pt.x = (a00 * x + a01 * y + a02) / z;
			pt.y = (a10 * x + a11 * y + a12) / z;
		}

	}

	/**
	 * @return the definitng points
	 */
	public GeoPointND[] getPointsND() {
		if (getParentAlgorithm() instanceof AlgoLocusStroke) {
			return ((AlgoLocusStroke) getParentAlgorithm()).getPointsND();
		}
		GeoPointND[] pts = new GeoPointND[getPoints().size()];
		int i = 0;
		for (MyPoint pt : getPoints()) {
			pts[i++] = new GeoPoint(cons, pt.x, pt.y, 1);
		}
		return pts;
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void translate(Coords v) {
		for (MyPoint pt : getPoints()) {
			pt.x += v.getX();
			pt.y += v.getY();
		}

	}

}
