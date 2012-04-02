package geogebra.common.kernel.algos;

import geogebra.common.kernel.arithmetic.Polynomial;

/**
 * This class contains all symbolic information necessary for the provers.
 * 
 * @author Simon Weitzhofer
 *
 */
public class SymbolicParameters {
	/**
	 * The symbolic degree of the coordinates
	 */
	protected int[] degree;
	protected Polynomial polynomial;
	
	/**
	 * Constructor with only the degrees
	 * 
	 * @param dx The symbolic degree of the polynomial in the first coordinate.
	 * @param dy The symbolic degree of the polynomial in the second coordinate.
	 * @param dz The symbolic degree of the polynomial in the third coordinate.
	 */
	public SymbolicParameters(int dx, int dy,int dz){
		degree=new int[3];
		degree[0]=dx;
		degree[1]=dy;
		degree[2]=dz;
		polynomial=null;
	}
	
	/**
	 * Constructor with only the degrees.
	 * @param degree the degrees
	 */
	public SymbolicParameters(final int[] degree){
		this.degree=degree;
	}
	
	/**
	 * Constructor with degrees and polynomial
	 * 
	 * @param dx The symbolic degree of the polynomial in the first coordinate.
	 * @param dy The symbolic degree of the polynomial in the second coordinate.
	 * @param dz The symbolic degree of the polynomial in the third coordinate.
	 * @param p The polynomial.
	 */
	public SymbolicParameters(int dx, int dy, int dz, Polynomial p){
		this(dx,dy,dz);
		polynomial=p;
	}
	
	/**
	 * Getter for the degrees
	 * @return the degrees of the polynomial
	 */
	public int[] getDegree(){
		return degree;
	}
	
	/**
	 * Getter for the polynomial
	 * @return the polynomial
	 */
	public Polynomial getPolynomial(){
		return polynomial;
	}
	
	/**
	 * Returns the maximum degree of degree1 and degree2 in each coordinate.
	 * @param degree1 The first degree
	 * @param degree2 The second degree
	 * @return the maximum degrees
	 */
	public static int[] getMaxDegree(int[] degree1, int[] degree2){
		if (degree1==null || degree2==null || degree1.length != 3 || degree2.length != 3)
			return null;
		int[] maxDegree = new int[3];
		maxDegree[0] = Math.max(degree1[0], degree2[0]);
		maxDegree[1] = Math.max(degree1[1], degree2[1]);
		maxDegree[2] = Math.max(degree1[2], degree2[2]);
		return maxDegree;
	}
}
