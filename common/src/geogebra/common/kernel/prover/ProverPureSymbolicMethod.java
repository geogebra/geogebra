package geogebra.common.kernel.prover;

import java.util.HashSet;
import java.util.Iterator;

import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.Prover.ProofResult;
import geogebra.common.main.AbstractApplication;

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
	public static ProofResult prove(GeoElement statement){
		ProofResult result = ProofResult.UNKNOWN;

		if (statement instanceof SymbolicParametersAlgo){
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement;
			SymbolicParameters parameters = statementSymbolic.getSymbolicParameters();
			try {
				parameters.getFreeVariables();
				// TODO: write here Recio's prover
			} catch (NoSymbolicParametersException e) {
				AbstractApplication.warn("This prover cannot give an answer, try another one");
				// TODO: to implement this correctly
			}
		} else if (statement.getParentAlgorithm() instanceof SymbolicParametersAlgo){
			SymbolicParametersAlgo statementSymbolic = (SymbolicParametersAlgo) statement.getParentAlgorithm();
			/*SymbolicParameters parameters = statementSymbolic.getSymbolicParameters();
			try {
				parameters.getFreeVariables();
			} catch (NoSymbolicParametersException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			try {
				Polynomial[] poly = statementSymbolic.getPolynomials();
				AbstractApplication.debug(poly[0]);
				if (poly.length==1 && poly[0].isZero()){
					result = Prover.ProofResult.TRUE;
				} else {
					result = Prover.ProofResult.FALSE;
				}
				return result;
				
				// TODO: write here Recio's prover
			} catch (NoSymbolicParametersException e) {
				AbstractApplication.warn("Pure symbolic prover cannot give an answer, try another one");
				// TODO: to implement this correctly
			}
		}
		return result;
	}
}
