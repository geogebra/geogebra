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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoCoords4D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Helper for Sum/Product when points or vectors are involved
 */
public class PointNDFold implements FoldComputer {

	private GeoElement result;
	private double x;
	private double y;
	private double z;

	@Override
	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return this.result = doGetTemplate(cons, listElement);
	}

	private static GeoElement doGetTemplate(Construction cons,
			GeoClass listElement) {
		switch (listElement) {
		case POINT:
			return new GeoPoint(cons);
		case POINT3D:
			return (GeoElement) cons.getKernel().getGeoFactory().newPoint(3,
					cons);
		case VECTOR:
			return new GeoVector(cons);
		case VECTOR3D:
			return (GeoElement) cons.getKernel().getGeoFactory().newPoint(3,
					cons);
		}
		return new GeoPoint(cons);
	}

	@Override
	public void add(GeoElement p, Operation op) {
		if (op == Operation.MULTIPLY) {
			double x0 = x;
			if (p instanceof GeoPoint) {
				((GeoPoint) p).updateCoords();
				x = x0 * ((GeoPoint) p).getInhomX()
						- y * ((GeoPoint) p).getInhomY();
				y = y * ((GeoPoint) p).getInhomX()
						+ x0 * ((GeoPoint) p).getInhomY();
				Log.debug(x + "," + y);
			} else if (p instanceof GeoVector) {
				double[] coords = ((GeoVectorND) p).getInhomCoords();
				x = x0 * coords[0] - y * coords[1];
				y = y * coords[0] + x0 * coords[1];
			} else {
				x = Double.NaN;
			}
			return;
		}
		if (p instanceof GeoPoint) {
			x += ((GeoPoint) p).getInhomX();
			y += ((GeoPoint) p).getInhomY();
		} else if (p instanceof GeoPointND) { // 3D
			double[] coords = new double[3];
			((GeoPointND) p).getInhomCoords(coords);
			x += coords[0];
			y += coords[1];
			z += coords[2];
		} else if (p.isGeoVector()) {
			double[] coords = ((GeoVectorND) p).getInhomCoords();
			x += coords[0];
			y += coords[1];
			if (coords.length == 3) {
				z += coords[2];
			}
		} else if (p instanceof NumberValue) {
			// changed from GeoGebra 4.2 so that Sum[{(1,2),3}] gives (4,5)
			// not (4,2)
			// to be consistent with the CAS View
			double val = p.evaluateDouble();
			x += val;
			y += val;
			z += val;
		} else {
			result.setUndefined();
		}

	}

	@Override
	public void setFrom(GeoElement geoElement, Kernel kernel) {
		x = 0;
		y = 0;
		z = 0;
		add(geoElement, Operation.PLUS);
	}

	@Override
	public boolean check(GeoElement geoElement) {
		return geoElement instanceof VectorNDValue;
	}

	@Override
	public void finish() {
		if (result instanceof GeoVec3D) {
			// 2D Point / Vector
			((GeoVec3D) result).setCoords(x, y, 1.0);
		} else if (result instanceof GeoCoords4D) {
			// 3D Point / Vector
			((GeoCoords4D) result).setCoords(x, y, z, 1.0);
		}

	}

}
