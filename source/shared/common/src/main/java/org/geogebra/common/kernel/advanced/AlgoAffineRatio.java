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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * This class calculate affine ratio of 3 points: (A,B,C) = (t(C)-t(A)) :
 * (t(C)-t(B))
 * 
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 * 
 */

public class AlgoAffineRatio extends AlgoElement {
	// input
	private GeoPointND A;
	private GeoPointND B;
	private GeoPointND C;
	// output
	private GeoNumeric M;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point
	 * @param B
	 *            point
	 * @param C
	 *            point
	 */
	public AlgoAffineRatio(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		super(cons);
		this.A = A;
		this.B = B;
		this.C = C;
		// create new GeoNumeric Object to return the result
		M = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.AffineRatio;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = A.toGeoElement();
		input[1] = B.toGeoElement();
		input[2] = C.toGeoElement();

		setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return ratio
	 */
	public GeoNumeric getResult() {
		return M;
	}

	@Override
	public final void compute() {
		// Check if the points are aligned
		if (GeoPoint.collinearND(A, B, C)) {
			if (B.isEqualPointND(C)) {
				M.setValue(1.0); // changed, was undefined
			} else {
				M.setValue(GeoPoint.affineRatio(A, B, C));
			}
		} else {
			M.setUndefined();
		}
	}

}