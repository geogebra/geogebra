package geogebra.common.kernel.algos;

import geogebra.common.kernel.prover.Variable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;

/**
 * This interface describes the symbolic parameters of algorithms
 * for the Botana method. Based on Simon's SymbolicParametersAlgo.java.
 * @author Zoltan Kovacs
 */
public interface SymbolicParametersBotanaAlgo {
	
	/**
	 * Calculates the free variables of an object for the Botana method
	 * @return array of the free variables
	 */
	public Variable[] getBotanaVars();
	
	/**
	 * Calculates the polynomials of an object for the Botana method
	 * @return array of the polynomials
	 * @throws NoSymbolicParametersException if it is not possible to obtain suitable polynomials
	 */
	public Polynomial[] getBotanaPolynomials() throws NoSymbolicParametersException;
}
