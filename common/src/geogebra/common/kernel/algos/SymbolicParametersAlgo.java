package geogebra.common.kernel.algos;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;

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
	 * @param variables all free variables used
	 * @return the degrees of the coordinates
	 * @throws NoSymbolicParametersException thrown if no symbolic parameters are available.
	 * 
	 */
	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables) throws NoSymbolicParametersException;
	
	/**
	 * Calculates the homogeneous coordinates of the object when substituting the variables by its values.
	 * @param values The values the variables are substituted with
	 * @return the coordinates
	 * @throws NoSymbolicParametersException is thrown if it is not possible to obtain the exact coordinates
	 */
	public BigInteger[] getExactCoordinates(final HashMap<Variable,BigInteger> values) throws NoSymbolicParametersException;
	
	/**
	 * Calculates the polynomial describing the algorithm or statement
	 * @return the polynomial
	 * @throws NoSymbolicParametersException if it is not possible to obtain an algebraic description
	 */
	public Polynomial[] getPolynomials() throws NoSymbolicParametersException;

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
