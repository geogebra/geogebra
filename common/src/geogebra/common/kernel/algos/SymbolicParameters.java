package geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashSet;

import geogebra.common.kernel.prover.FreeVariable;

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
	 * @param spa
	 */
	public SymbolicParameters(final SymbolicParametersAlgo spa) {
		this.spa=spa;
	}

	/**
	 * Getter for the degrees
	 * 
	 * @return the degrees of the polynomial
	 */
	public int[] getDegrees() {
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
	
	public static BigInteger[] crossProduct(BigInteger[] a, BigInteger[] b){
		BigInteger[] result=new BigInteger[3];
		result[0]=(a[1].multiply(b[2])).subtract(a[2].multiply(b[1]));
		result[1]=(a[2].multiply(b[0])).subtract(a[0].multiply(b[2]));
		result[2]=(a[0].multiply(b[1])).subtract(a[1].multiply(b[0]));
		return result;
	}

	public int getDimension() {
		if (freeVariables == null) {
			initDegrees();
		}
		return freeVariables.size();
	}
	
	public HashSet<FreeVariable> getFreeVariables(){
		if (freeVariables == null) {
			initDegrees();
		}
		return freeVariables;
	}
	
	public BigInteger[] getExactCoordinates(){
		return spa.getExactCoordinates();
	}

	/**
	 * Calculates Degrees and FreeVariables
	 */
	private void initDegrees() {
		freeVariables = new HashSet<FreeVariable>();
		degree = new int[3];
		spa.getFreeVariablesAndDegrees(freeVariables, degree);
	}
}
