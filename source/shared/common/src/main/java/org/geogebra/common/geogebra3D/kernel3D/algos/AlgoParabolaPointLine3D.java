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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoParabolaPointLineND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author Markus
 */
public class AlgoParabolaPointLine3D extends AlgoParabolaPointLineND {

	public AlgoParabolaPointLine3D(Construction cons, String label,
			GeoPointND F, GeoLineND l) {
		super(cons, label, F, l);
	}

	public AlgoParabolaPointLine3D(Construction cons, GeoPointND F,
			GeoLineND l) {
		super(cons, F, l);
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons1) {
		GeoConic3D ret = new GeoConic3D(cons1);
		ret.setCoordSys(new CoordSys(2));
		return ret;
	}

	// compute parabola with focus F and line l
	@Override
	public final void compute() {

		Coords Fc = F.getInhomCoordsInD3();
		Coords lo = line.getStartInhomCoords().getInhomCoordsInSameDimension();
		Coords ld = line.getDirectionInD3();

		CoordSys cs = parabola.getCoordSys();
		cs.resetCoordSys();

		cs.addPoint(Fc);
		cs.addVector(ld);
		cs.addPoint(lo);

		if (!cs.makeOrthoMatrix(false, false)) {
			parabola.setUndefined();
			return;
		}

		double y0 = cs.getNormalProjection(lo)[1].getY();
		parabola.setParabola(y0);
	}

}
