package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

/**
 * @author mathieu
 * 
 *         Drawable for locus
 * 
 */
public class DrawImplicitCurve3D extends DrawLocus3D {

	/**
	 * @param a_view3d
	 *            the 3D view where the curve is drawn
	 * @param curve
	 *            the curve to draw
	 */
	public DrawImplicitCurve3D(EuclidianView3D a_view3d, GeoImplicit curve) {
		super(a_view3d, curve.getLocus(), curve.toGeoElement(),
				curve.getTransformedCoordSys());

	}

	@Override
	protected GeoLocusND<? extends MyPoint> getLocus() {
		return ((GeoImplicitCurve) getGeoElement()).getLocus();
	}

}
