/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Circular arc or sector defined by the circle's center, one point on the
 * circle (start point) and another point (angle for end-point), and
 * orientation.
 */
public class AlgoConicPartCircle3DOrientation extends AlgoConicPartCircle3D {

	private GeoDirectionND orientation;

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param center
	 *            center
	 * @param startPoint
	 *            arc start point
	 * @param endPoint
	 *            arc endpoint
	 * @param orientation
	 *            orientation
	 * @param type
	 *            sector or arc
	 */
	public AlgoConicPartCircle3DOrientation(Construction cons, String label,
			GeoPointND center, GeoPointND startPoint, GeoPointND endPoint,
			GeoDirectionND orientation, int type) {
		super(cons, label, center, startPoint, endPoint, orientation, type);
	}

	@Override
	protected void setOrientation(GeoDirectionND orientation) {
		this.orientation = orientation;
	}

	@Override
	protected boolean getPositiveOrientation() {
		Coords d = orientation.getDirectionInD3();
		if (d == null) {
			return true;
		}
		return conic.getMainDirection().dotproduct(d) >= 0;
	}

	@Override
	protected void setInput() {
		setInput(4);
		input[3] = (GeoElement) orientation;
	}

	@Override
	protected void semiCircle(Coords center, Coords v1) {
		Coords d = orientation.getDirectionInD3();
		if (d == null) {
			conicPart.setUndefined();
		} else {
			conicPart.setDefined();
			AlgoCircle3DAxisPoint.setCircle(conic, conic.getCoordSys(), center, v1, d);
			setConicPart(0, Math.PI);
		}
	}
}
