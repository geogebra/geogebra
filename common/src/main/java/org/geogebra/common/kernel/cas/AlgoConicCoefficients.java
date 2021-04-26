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
