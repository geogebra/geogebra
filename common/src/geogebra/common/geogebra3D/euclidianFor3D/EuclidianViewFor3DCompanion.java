package geogebra.common.geogebra3D.euclidianFor3D;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewCompanion;
import geogebra.common.euclidian.draw.DrawParametricCurve;
import geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;

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
			d = new DrawParametricCurve(view, new CurveEvaluableFor3D(
					(GeoCurveCartesian3D) geo));
			break;
		}

		return d;
	}

	@Override
	public GAffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev) {

		// use already computed for this view middlepoint M and eigen vecs ev
		GAffineTransform transform = geogebra.common.factories.AwtFactory.prototype
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
