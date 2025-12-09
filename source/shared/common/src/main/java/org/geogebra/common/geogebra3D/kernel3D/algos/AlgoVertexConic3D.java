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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoVertexConic;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Class for vertices of a 3D conic
 * 
 * @author mathieu
 *
 */
public class AlgoVertexConic3D extends AlgoVertexConic {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param c
	 *            conic
	 */
	public AlgoVertexConic3D(Construction cons, String[] labels, GeoConicND c) {
		super(cons, labels, c);
	}

	@Override
	protected void createVertex(Construction cons1) {
		vertex = new GeoPoint3D[4];
		for (int i = 0; i < vertex.length; i++) {
			vertex[i] = new GeoPoint3D(cons1);
		}
	}

	@Override
	protected void setCoords(int i, double x, double y) {
		((GeoPoint3D) vertex[i]).setCoords(c.getCoordSys().getPoint(x, y));
	}
}
