package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.euclidian.draw.DrawParametricCurve;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;

/**
 * companion for view for 3D
 * 
 * @author mathieu
 *
 */
public class EuclidianViewFor3DCompanion extends EuclidianViewCompanion {

	private GAffineTransform transform = AwtFactory.getPrototype()
			.newAffineTransform();

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
	/**
	 * Creates drawable for geo, also covers the 3D cases that can be flattened.
	 * 
	 * Coverage of all classes tested in DrawablesTest
	 */
	public DrawableND newDrawable(GeoElementND geo) {
		// first try super method
		DrawableND d = super.newDrawable(geo);
		if (d != null) {
			return d;
		}

		// try 3D geos
		if (geo.getGeoClassType() == GeoClass.ANGLE3D) {
			d = new DrawAngleFor3D(view, (GeoAngle) geo);
		}

		return d;
	}

	/**
	 * 
	 * @param geo
	 *            curve
	 * @return drawable for curve
	 */
	@Override
	public DrawableND newDrawParametricCurve(ParametricCurve geo) {
		if (geo instanceof GeoCurveCartesian3D) {
			return new DrawParametricCurve(view,
					new CurveEvaluableFor3D((GeoCurveCartesian3D) geo));
		}
		return super.newDrawParametricCurve(geo);
	}

	@Override
	public GAffineTransform getTransform(GeoConicND conic, Coords M,
			Coords[] ev) {

		// use already computed for this view middlepoint M and eigen vecs ev
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
