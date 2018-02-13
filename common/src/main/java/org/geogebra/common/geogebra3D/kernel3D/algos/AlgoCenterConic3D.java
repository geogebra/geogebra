package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoCenterConic;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Center of 3D conic
 * 
 * @author mathieu
 *
 */
public class AlgoCenterConic3D extends AlgoCenterConic {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	public AlgoCenterConic3D(Construction cons, String label, GeoConicND c) {
		super(cons, label, c);
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

	@Override
	protected void setCoords(double x, double y) {
		((GeoPoint3D) midpoint)
				.setCoords(((GeoConicND) c).getCoordSys().getPoint(x, y));
	}
}
