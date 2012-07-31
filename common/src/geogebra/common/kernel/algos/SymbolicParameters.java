package geogebra.common.kernel.algos;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

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
	private HashSet<Variable> variables;
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
		return spa.getDegrees();
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
	 * calculates the maximum degrees possible of an cross product of two vectors
	 * @param degree1 the degrees of the coordinates of the first vector
	 * @param degree2 the degrees of the coordinates of the second vector
	 * @return the degrees of the cross product of the two vectors
	 */
	public static int[] crossDegree(final int[] degree1, final int[] degree2){
		int[] result =new int[3];
		result[0] = Math.max(degree1[1]+degree2[2],degree1[2]+degree2[1]);
		result[1] = Math.max(degree1[0]+degree2[2],degree1[2]+degree2[0]);
		result[2] = Math.max(degree1[1]+degree2[0],degree1[0]+degree2[1]);
		return result;
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
		return SymbolicParameters.reduce(result);
	}
	
	/**
	 * Returns the number of free variables of the polynomial describing the object
	 * @return the number of free variables
	 * @throws NoSymbolicParametersException if no symbolic parameters can be obtained
	 */
	public int getDimension() throws NoSymbolicParametersException {
		if (variables == null) {
			initFreeVariables();
		}
		return variables.size();
	}
	
	/**
	 * Returns a set of all free variables of the polynomial describing the object
	 * @return the set of all free variables
	 * @throws NoSymbolicParametersException if no symbolic parameters can be obtained
	 */
	public HashSet<Variable> getFreeVariables() throws NoSymbolicParametersException{
		if (variables == null) {
			initFreeVariables();
		}
		return variables;
	}
	
	/**
	 * Calculates the homogeneous coordinates of the object when substituting the variables by its values.
	 * @param values a map of the values the variables are substituted with
	 * @return the coordinates
	 * @throws NoSymbolicParametersException thrown if it is not possible to obtain the exact coordinates
	 */
	public BigInteger[] getExactCoordinates(final HashMap<Variable,BigInteger> values) throws NoSymbolicParametersException{
		return spa.getExactCoordinates(values);
	}
	
	/**
	 * Divides thru the greatest common divisor of the homogeneous coordinates
	 * @param vect the homogeneous coordinates
	 * @return the reduced homogeneous coordinates
	 */
	public static BigInteger[] reduce(final BigInteger[] vect){
		BigInteger gcd=new BigInteger("0");
		for (int i=0;i<vect.length;i++){
			gcd=gcd.gcd(vect[i]);
		}
		if (gcd.equals(BigInteger.ZERO)) {
			gcd=BigInteger.ONE;
		}
		BigInteger[] result=new BigInteger[vect.length];
		for (int i=0;i<vect.length;i++){
			result[i]=vect[i].divide(gcd);
		}
		return result;
	}
	

	/**
	 * Gets the free variables
	 * @throws NoSymbolicParametersException 
	 */
	private void initFreeVariables() throws NoSymbolicParametersException {
		variables = new HashSet<Variable>();
		spa.getFreeVariables(variables);
	}
	
	/**
	 * Calculates the determinant of a 4 times 4 matrix
	 * @param matrix matrix
	 * @return the determinant
	 */
	public static BigInteger det4(final BigInteger[][] matrix){
		return  matrix[0][3].multiply(matrix[1][2]).multiply(matrix[2][1]).multiply(matrix[3][0]).subtract(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][3])).add(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][3]));
	}

	/**
	 * Adds two extra points to the Botana point list (4 extra variables)
	 * @param input Two EV points
	 * @return List of Botana variables (4 elements)
	 */
	public static Variable[] addBotanaVarsJoinPoints(GeoElement[] input) {
		Variable[] botanaVars = new Variable[4];
		Variable[] line1vars = new Variable[2];
		Variable[] line2vars = new Variable[2];
		line1vars = ((SymbolicParametersBotanaAlgo) input[0]).getBotanaVars(input[0]);
		line2vars = ((SymbolicParametersBotanaAlgo) input[1]).getBotanaVars(input[1]);
		botanaVars[0] = line1vars[0];
		botanaVars[1] = line1vars[1];
		botanaVars[2] = line2vars[0];
		botanaVars[3] = line2vars[1];
		return botanaVars;
	}

	/**
	 * Returns Botana polynomials for the midpoint of P and Q  
	 * @param P First endpoint of segment
	 * @param Q Other endpoint of segment
	 * @param botanaVars Botana variables for the new point (midpoint) 
	 * @return the polynomials (2 elements)
	 */
	public static Polynomial[] botanaPolynomialsMidpoint(GeoElement P, GeoElement Q,
			Variable[] botanaVars) {
		Variable[] fv1 = ((SymbolicParametersBotanaAlgo) P).getBotanaVars(P);
		Variable[] fv2 = ((SymbolicParametersBotanaAlgo) Q).getBotanaVars(Q);
		Polynomial[] botanaPolynomials = new Polynomial[2];
		// 2*m1-a1-b1, 2*m2-a2-b2
		botanaPolynomials[0] = (new Polynomial(2)).multiply(new Polynomial(botanaVars[0])).
				subtract(new Polynomial(fv1[0])).subtract(new Polynomial(fv2[0]));
		botanaPolynomials[1] = (new Polynomial(2)).multiply(new Polynomial(botanaVars[1])).
				subtract(new Polynomial(fv1[1])).subtract(new Polynomial(fv2[1]));
		return botanaPolynomials;
	}

	/**
	 * Returns Botana polynomials for the bisector of A and B
	 * @param Ax Botana variable of A (x)
	 * @param Ay Botana variable of A (y)
	 * @param Bx Botana variable of B (x)
	 * @param By Botana variable of B (y)
	 * @param botanaVars Botana variables for the 2 new points (4 variables)
	 * @return the polynomials (4 elements)
	 */
	public static Polynomial[] botanaPolynomialsLineBisector(Variable Ax,
			Variable Ay, Variable Bx, Variable By, Variable[] botanaVars) {
		Polynomial[] botanaPolynomials = new Polynomial[4];

		Polynomial a1=new Polynomial(Ax);
		Polynomial a2=new Polynomial(Ay);

		Polynomial c1=new Polynomial(botanaVars[0]);
		Polynomial c2=new Polynomial(botanaVars[1]);
		Polynomial d1=new Polynomial(botanaVars[2]);
		Polynomial d2=new Polynomial(botanaVars[3]);
		
		// C will be the midpoint of AB  
		// 2*c1-a1-b1, 2*c2-a2-b2
		botanaPolynomials[0] = (new Polynomial(2)).multiply(c1).
				subtract(new Polynomial(Ax)).subtract(new Polynomial(Bx));
		botanaPolynomials[1] = (new Polynomial(2)).multiply(c2).
				subtract(new Polynomial(Ay)).subtract(new Polynomial(By));
	
		// D will be the rotation of A around C by 90 degrees
		// d2=c2+(c1-a1), d1=c1-(c2-a2) => d2-c2-c1+a1, d1-c1+c2-a2
		botanaPolynomials[2] = ((d2.subtract(c2)).subtract(c1)).add(a1);
		botanaPolynomials[3] = ((d1.subtract(c1)).add(c2)).subtract(a2);
				
		return botanaPolynomials;

	
	}
	
}
