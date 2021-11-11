package org.geogebra.web.html5.util;

import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.kernel.ProverReciosMethodW;

/**
 * @author Zoltan Kovacs
 * 
 *         Implements web dependent parts of the Prover.
 */
public class ProverW extends Prover {

	@Override
	protected AbstractProverReciosMethod getNewReciosProver() {
		return new ProverReciosMethodW();
	}

	@Override
	public void compute() {
		decideStatement();
	}

	@Override
	protected ProofResult openGeoProver(ProverEngine pe) {
		Log.debug("OGP is not implemented for the web");
		return ProofResult.UNKNOWN;
	}
}
