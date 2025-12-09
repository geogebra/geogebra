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
