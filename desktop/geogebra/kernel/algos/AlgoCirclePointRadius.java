/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCirclePointRadius.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.algos.AlgoCirclePointRadiusInterface;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoSegment;
import geogebra.kernel.kernelND.GeoQuadricND;

/**
 * 
 * @author Markus added TYPE_SEGMENT Michael Borcherds 2008-03-14
 * @version
 */
public class AlgoCirclePointRadius extends AlgoSphereNDPointRadius implements AlgoCirclePointRadiusInterface {

	public AlgoCirclePointRadius(AbstractConstruction cons, String label, GeoPoint2 M,
			NumberValue r) {

		super(cons, label, M, r);
	}

	public AlgoCirclePointRadius(AbstractConstruction cons, String label, GeoPoint2 M,
			GeoSegment segment, boolean dummy) {

		super(cons, label, M, segment, dummy);
	}

	public AlgoCirclePointRadius(AbstractConstruction cons, GeoPoint2 M, NumberValue r) {

		super(cons, M, r);

	}

	AlgoCirclePointRadius(AbstractConstruction cons, GeoPoint2 M, GeoSegment rgeo,
			boolean dummy) {

		super(cons, M, rgeo, dummy);
	}

	@Override
	protected GeoQuadricND createSphereND(AbstractConstruction cons) {
		return new GeoConic(cons);
	}

	@Override
	public String getClassName() {
		return "AlgoCirclePointRadius";
	}

	@Override
	public int getRelatedModeID() {
		switch (super.getType()) {
		case AlgoSphereNDPointRadius.TYPE_RADIUS:
			return EuclidianConstants.MODE_CIRCLE_POINT_RADIUS;
		default:
			return EuclidianConstants.MODE_COMPASSES;
		}
	}

	public GeoConic getCircle() {
		return (GeoConic) getSphereND();
	}

	@Override
	final public String toString() {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return app.getPlain("CircleWithCenterAandRadiusB", getM().getLabel(),
				getRGeo().getLabel());
	}
}
