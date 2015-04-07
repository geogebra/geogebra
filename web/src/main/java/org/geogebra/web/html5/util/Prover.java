package org.geogebra.web.html5.util;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.kernel.ProverReciosMethod;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Implements web dependent parts of
 *         the Prover
 */
public class Prover extends org.geogebra.common.util.Prover {

	static {
		reciosProver = new ProverReciosMethod();
	}

	@Override
	public void compute() {
		decideStatement();
	}

	@Override
	protected ProofResult openGeoProver(ProverEngine pe) {
		App.debug("OGP is not implemented for the web");
		return ProofResult.UNKNOWN;
	}
}
