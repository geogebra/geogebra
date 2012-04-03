package geogebra.common.kernel.algos;


import java.math.BigInteger;
import java.util.HashSet;

import geogebra.common.kernel.prover.FreeVariable;

/**
 * This interface describes the symbolic parameters of algorithms
 * @author Simon Weitzhofer
 *
 */
public interface SymbolicParametersAlgo {
	
	/**
	 * Getter for the SymbolicParameters
	 * @return the SymbolicParameters
	 */
	public SymbolicParameters getSymbolicParameters();
	
	/**
	 * Returns the set of free variables and the maximum degrees.
	 * 
	 * @param freeVariables needs to be initialized
	 * @param degrees should be null when the function is called
	 */
	public void getFreeVariablesAndDegrees(HashSet<FreeVariable> freeVariables, int[] degrees);
	
	public BigInteger[] getExactCoordinates();
}
