package org.geogebra.common.kernel.prover.adapters;

import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class ProverAdapter {
	protected PPolynomial[] botanaPolynomials;
	protected PVariable[] botanaVars;

	public PVariable[] getBotanaVars() {
		return botanaVars;
	}
}
