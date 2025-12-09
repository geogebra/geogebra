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

package org.geogebra.common.kernel.prover;

import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.debug.Log;

/**
 * A prover which uses pure symbolic method to prove geometric theorems.
 * 
 * @author Simon Weitzhofer
 * @author Zoltan Kovacs
 *
 */
public class ProverPureSymbolicMethod {

	/**
	 * Proves the statement by using pure symbolic method
	 * 
	 * @param prover
	 *            the prover to be used
	 * @return if the proof was successful
	 */
	public static ProofResult prove(Prover prover) {

		GeoElement statement = prover.getStatement();

		if (statement instanceof SymbolicParametersAlgo) {
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement;
			SymbolicParameters parameters = statementSymbolic
					.getSymbolicParameters();
			try {
				parameters.getFreeVariables();
				// TODO: write here Recio's prover
				// FIXME: No, something else is required here!
			} catch (NoSymbolicParametersException e) {
				return ProofResult.UNKNOWN;
			}
		} else if (statement
				.getParentAlgorithm() instanceof SymbolicParametersAlgo) {
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement
					.getParentAlgorithm();
			try {
				PPolynomial[] poly = statementSymbolic.getPolynomials();
				for (PPolynomial polynomial : poly) {
					Log.debug(polynomial);
					if (!polynomial.isZero()) {
						return ProofResult.FALSE;
					}
				}
				return ProofResult.TRUE;
				// TODO: write here Recio's prover
				// FIXME: No, something else is required here!
			} catch (NoSymbolicParametersException e) {
				return ProofResult.UNKNOWN;
			}
		}
		return ProofResult.UNKNOWN;
	}
}
