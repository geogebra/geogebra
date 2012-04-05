package geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashSet;

import geogebra.common.kernel.prover.FreeVariable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;

/**
 * This class provides all symbolic information necessary for the provers.
 * 
 * @author Simon Weitzhofer
 * 
 */
public class SymbolicParameters {
	/**
	 * The symbolic degree of the coordinates
	 */
	protected int[] degree;
	private HashSet<FreeVariable> freeVariables;
	private SymbolicParametersAlgo spa;

	/**
	 * 
	 * @param spa The algorithm which calculates the objects
	 */
	public SymbolicParameters(final SymbolicParametersAlgo spa) {
		this.spa=spa;
	}

	/**
	 * Getter for the degrees
	 * 
	 * @return the degrees of the polynomial
	 * @throws NoSymbolicParametersException if no symbolic parameters can be obtained
	 */
	public int[] getDegrees() throws NoSymbolicParametersException {
		if (freeVariables == null) {
			initDegrees();
		}
		return degree;
	}

	/**
	 * Returns the maximum degree of degree1 and degree2 in each coordinate.
	 * 
	 * @param degree1
	 *            The first degree
	 * @param degree2
	 *            The second degree
	 * @return the maximum degrees
	 */
	public static int[] getMaxDegree(int[] degree1, int[] degree2) {
		if (degree1 == null || degree2 == null || degree1.length != 3
				|| degree2.length != 3)
			return null;
		int[] maxDegree = new int[3];
		maxDegree[0] = Math.max(degree1[0], degree2[0]);
		maxDegree[1] = Math.max(degree1[1], degree2[1]);
		maxDegree[2] = Math.max(degree1[2], degree2[2]);
		return maxDegree;
	}
	
	/**
	 * Returns the sum of degree1 and degree2 in each coordinate.
	 * 
	 * @param degree1
	 *            The first degree
	 * @param degree2
	 *            The second degree
	 * @return the sum
	 */
	public static int[] addDegree(int[] degree1, int[] degree2) {
		if (degree1 == null || degree2 == null || degree1.length != 3
				|| degree2.length != 3)
			return null;
		int[] addDegree = new int[3];
		addDegree[0] = degree1[0]+degree2[0];
		addDegree[1] = degree1[1]+degree2[1];
		addDegree[2] = degree1[2]+degree2[2];
		return addDegree;
	}
	
	/**
	 * 
	 * Calculates the cross product of two vectors of dimension three.
	 * @param a the first vector
	 * @param b the second vector
	 * @return the cross product of the two vectors
	 */
	public static BigInteger[] crossProduct(final BigInteger[] a, final BigInteger[] b){
		BigInteger[] result=new BigInteger[3];
		result[0]=(a[1].multiply(b[2])).subtract(a[2].multiply(b[1]));
		result[1]=(a[2].multiply(b[0])).subtract(a[0].multiply(b[2]));
		result[2]=(a[0].multiply(b[1])).subtract(a[1].multiply(b[0]));
		return result;
	}

	/**
	 * Returns the number of free variables of the polynomial describing the object
	 * @return the number of free variables
	 * @throws NoSymbolicParametersException if no symbolic parameters can be obtained
	 */
	public int getDimension() throws NoSymbolicParametersException {
		if (freeVariables == null) {
			initDegrees();
		}
		return freeVariables.size();
	}
	
	/**
	 * Returns a set of all free variables of the polynomial describing the object
	 * @return the set of all free variables
	 * @throws NoSymbolicParametersException if no symbolic parameters can be obtained
	 */
	public HashSet<FreeVariable> getFreeVariables() throws NoSymbolicParametersException{
		if (freeVariables == null) {
			initDegrees();
		}
		return freeVariables;
	}
	
	/**
	 * Calculates the homogeneous coordinates of the object when substituting the variables by its values.
	 * @return the coordinates
	 */
	public BigInteger[] getExactCoordinates(){
		return spa.getExactCoordinates();
	}

	/**
	 * Calculates Degrees and FreeVariables
	 * @throws NoSymbolicParametersException 
	 */
	private void initDegrees() throws NoSymbolicParametersException {
		freeVariables = new HashSet<FreeVariable>();
		degree=spa.getFreeVariablesAndDegrees(freeVariables);
	}
}
