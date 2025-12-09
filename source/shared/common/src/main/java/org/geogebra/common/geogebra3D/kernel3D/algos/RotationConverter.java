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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.CoordConverter;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public class RotationConverter implements CoordConverter {

	/** Difference from closest 90deg multiple that allows snapping */
	public static final double SNAP_PRECISION = 5 * Kernel.PI_180;
	private GeoLineND axis;
	private Coords rotationCenter = new Coords(3);
	private Coords vStart = new Coords(3);
	private Coords vCurrent = new Coords(3);
	private Coords result = Coords.createInhomCoorsInD3();
	private Coords lastStartPoint = Coords.createInhomCoorsInD3();
	private Coords crossProduct = new Coords(4);
	private Coords vStartNormalized = new Coords(4);

	public RotationConverter(GeoLineND axis) {
		this.axis = axis;
	}

	@Override
	public double translationToValue(Coords direction, Coords rwTransVec,
			double startValue, EuclidianView view) {
		vCurrent.setAdd3(vStart, rwTransVec);
		double sin = direction.dotCrossProduct(vStart, vCurrent);
		double cos = vStart.dotproduct3(vCurrent);
		return snap(DoubleUtil
				.convertToAngleValue(Math.atan2(sin, cos) + startValue), view);
	}

	@Override
	public double snap(double val, EuclidianView view) {
		double roundedVal = Math.round(val / Kernel.PI_HALF) * Kernel.PI_HALF;
		if (DoubleUtil.isEqual(val, roundedVal, SNAP_PRECISION)) {
			return roundedVal;
		}
		return val;
	}

	@Override
	public void record(ChangeableParent parent, Coords startPoint) {
		startPoint.projectLine(axis.getStartInhomCoords(),
				axis.getDirectionInD3(), rotationCenter);
		vStart.setSub3(startPoint, rotationCenter);
		lastStartPoint.set3(startPoint);
	}

	@Override
	public void updateTranslation(Coords startPoint3D, Coords direction,
			Coords rayOrigin, Coords rayDirection, Coords translationVec3D) {
		crossProduct.setCrossProduct3(vStart, direction);
		vStartNormalized.set3(vStart);
		vStartNormalized.normalize();
		rayOrigin.projectPlane(vStartNormalized, crossProduct, rayDirection,
				startPoint3D,
				result);
		// if ray direction is parallel to the plane
		if (DoubleUtil.isZero(result.getW())) {
			translationVec3D.setUndefined();
		} else {
			translationVec3D.setSub3(result, startPoint3D);
		}
	}

}
