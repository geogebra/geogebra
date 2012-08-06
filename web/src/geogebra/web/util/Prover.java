package geogebra.web.util;

import geogebra.common.main.App;
import geogebra.web.kernel.prover.ProverReciosMethod;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Implements web dependent parts of the Prover 
 */
public class Prover extends geogebra.common.util.Prover {

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
