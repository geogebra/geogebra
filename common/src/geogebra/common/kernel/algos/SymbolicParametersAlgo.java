package geogebra.common.kernel.algos;


import java.math.BigInteger;
import java.util.HashSet;

import geogebra.common.kernel.prover.FreeVariable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;

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
	 * Calculates the set of free variables and the maximum degrees.
	 * @param freeVariables all free variables used
	 * @return the degrees of the coordinates
	 * @throws NoSymbolicParametersException thrown if no symbolic parameters are available.
	 * 
	 */
	public int[] getFreeVariablesAndDegrees(HashSet<FreeVariable> freeVariables) throws NoSymbolicParametersException;
	
	/**
	 * Calculates the homogeneous coordinates of the object when substituting the variables by its values.
	 * @return the coordinates
	 */
	public BigInteger[] getExactCoordinates();
}
