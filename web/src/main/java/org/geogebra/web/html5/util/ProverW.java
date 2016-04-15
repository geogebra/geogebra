package org.geogebra.web.html5.util;

import org.geogebra.common.main.App;
import org.geogebra.common.util.Prover;
import org.geogebra.web.html5.kernel.ProverReciosMethodW;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Implements web dependent parts of
 *         the Prover
 */
public class ProverW extends Prover {

	static {
		reciosProver = new ProverReciosMethodW();
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
