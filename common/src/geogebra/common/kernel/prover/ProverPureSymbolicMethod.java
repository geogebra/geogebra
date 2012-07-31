package geogebra.common.kernel.prover;

import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.util.Prover;
import geogebra.common.util.Prover.ProofResult;

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
	 * @param statement the statement to prove
	 * @return if the proof was successful
	 */
	public static ProofResult prove(Prover prover){
		
		GeoElement statement = prover.getStatement();
		
		if (statement instanceof SymbolicParametersAlgo){
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement;
			SymbolicParameters parameters = statementSymbolic.getSymbolicParameters();
			try {
				parameters.getFreeVariables();
				// TODO: write here Recio's prover
				// FIXME: No, something else is required here!
			} catch (NoSymbolicParametersException e) {
				return ProofResult.UNKNOWN;
			}
		} else if (statement.getParentAlgorithm() instanceof SymbolicParametersAlgo){
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement.getParentAlgorithm();
			try {
				Polynomial[] poly = statementSymbolic.getPolynomials();
				for (Polynomial polynomial:poly){
					App.debug(polynomial);
					if (!polynomial.isZero()){
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
