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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

public class AlgoIntersectPlaneQuadricPart
		extends AlgoIntersectPlaneQuadricLimited {

	private GeoConicND bottom;
	private GeoConicND top;

	private AlgoQuadricEnds algoEnds = null;

	public AlgoIntersectPlaneQuadricPart(Construction cons, GeoPlane3D plane,
			GeoQuadricND quadric) {
		super(cons, plane, quadric);
	}

	@Override
	protected void end() {

		if (quadric.getParentAlgorithm() instanceof AlgoQuadricSide) {
			// use quadric limited parent ends
			GeoQuadric3DLimited parent = ((AlgoQuadricSide) quadric
					.getParentAlgorithm()).getInputQuadric();
			bottom = parent.getBottom();
			top = parent.getTop();
		} else {
			algoEnds = new AlgoQuadricEnds(cons, quadric);
			cons.removeFromConstructionList(algoEnds);
			bottom = algoEnds.getSection1();
			top = algoEnds.getSection2();

		}

		super.end();
	}

	@Override
	public void compute() {

		if (algoEnds != null) {
			algoEnds.compute();
		}

		super.compute();
	}

	@Override
	protected GeoConicND getBottom() {
		return bottom;
	}

	@Override
	protected GeoConicND getTop() {
		return top;
	}

	@Override
	protected GeoQuadric3DPart getSide() {
		return (GeoQuadric3DPart) quadric;
	}

	@Override
	protected double getBottomParameter() {
		return ((GeoQuadric3DPart) quadric).getBottomParameter();
	}

	@Override
	protected double getTopParameter() {
		return ((GeoQuadric3DPart) quadric).getTopParameter();
	}

}
