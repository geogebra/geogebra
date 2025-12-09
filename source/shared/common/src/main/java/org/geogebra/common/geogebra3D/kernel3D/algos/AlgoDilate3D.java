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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoDilate;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for dilate at 3D point
 * 
 * @author mathieu
 *
 */
public class AlgoDilate3D extends AlgoDilate {

	/**
	 * dilate at point
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            point dilated
	 * @param r
	 *            factor
	 * @param S
	 *            reference point
	 */
	public AlgoDilate3D(Construction cons, GeoElement A, GeoNumberValue r,
			GeoPointND S) {
		super(cons, A, r, S);
	}

	@Override
	protected GeoElement copy(GeoElement geo) {
		return kernel.copy3D(geo);
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction
				|| geo instanceof GeoCurveCartesian) {
			return new GeoCurveCartesian3D(cons);
		}

		return super.getResultTemplate(geo);
	}

	@Override
	protected GeoElement copyInternal(Construction cons1, GeoElement geo) {
		return kernel.copyInternal3D(cons1, geo);
	}

	@Override
	protected void setOutGeo() {
		if (inGeo instanceof GeoFunction /* && mirror.isGeoElement3D() */) {
			AlgoTransformation3D.toGeoCurveCartesian(kernel,
					(GeoFunction) inGeo, (GeoCurveCartesian3D) outGeo);
		} else {
			super.setOutGeo();
		}
	}

	@Override
	protected Coords getPointCoords() {
		return S.getInhomCoordsInD3();
	}

}
