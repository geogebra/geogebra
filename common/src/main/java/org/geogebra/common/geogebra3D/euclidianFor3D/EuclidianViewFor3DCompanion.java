package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.euclidian.draw.DrawParametricCurve;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * companion for view for 3D
 * 
 * @author mathieu
 *
 */
public class EuclidianViewFor3DCompanion extends EuclidianViewCompanion {

	/**
	 * constructor
	 * 
	 * @param view
	 *            view attached
	 */
	public EuclidianViewFor3DCompanion(EuclidianView view) {
		super(view);
	}

	@Override
	public DrawableND newDrawable(GeoElement geo) {

		// first try super method
		DrawableND d = super.newDrawable(geo);
		if (d != null) {
			return d;
		}

		// try 3D geos
		switch (geo.getGeoClassType()) {
		case ANGLE3D:
			d = new DrawAngleFor3D(view, (GeoAngle) geo);
			break;

		case CURVE_CARTESIAN3D:
			d = newDrawParametricCurve((GeoCurveCartesian3D) geo);
			break;
		}

		return d;
	}

	/**
	 * 
	 * @param geo
	 *            curve
	 * @return drawable for curve
	 */
	protected DrawableND newDrawParametricCurve(GeoCurveCartesian3D geo) {
		return new DrawParametricCurve(view, new CurveEvaluableFor3D(geo));
	}

	@Override
	public GAffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev) {

		// use already computed for this view middlepoint M and eigen vecs ev
		GAffineTransform transform = org.geogebra.common.factories.AwtFactory.prototype
				.newAffineTransform();
		transform.setTransform(ev[0].getX(), ev[0].getY(), ev[1].getX(),
				ev[1].getY(), M.getX(), M.getY());

		return transform;
	}

	@Override
	public Coords getCoordsForView(GeoPointND point) {
		return view.getCoordsForView(point.getInhomCoordsInD3());
	}

	@Override
	public void getXMLid(StringBuilder sbxml) {
		getXMLidNoCheck(sbxml);
	}
}
