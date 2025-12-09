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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

public class AlgoConicCoefficients extends AlgoEquationCoefficients {

	public AlgoConicCoefficients(Construction cons, String label,
			GeoQuadricND c) {
		super(cons, label, c, 6);
	}

	@Override
	public void extractCoefficients() {
		double[] matrix = ((GeoQuadricND) eqn).getFlatMatrix();
		for (int i = 0; i < 6; i++) {
			setCoeff(i, i < 3 ? matrix[i] : matrix[i] * 2);
		}
	}
}
