package geogebra.common.kernel.prover;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Decides if the points are concyclic.
 * Can be embedded into the Prove command to work symbolically.
 * @author Simon Weitzhofer
 *  27th of April 2012
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoAreConcyclic extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoPoint inputPoint1; // input
	private GeoPoint inputPoint2; // input
	private GeoPoint inputPoint3; // input
	private GeoPoint inputPoint4; // input

	private GeoBoolean outputBoolean; // output
	private Polynomial[] polynomials;
	private Polynomial[][] botanaPolynomials;
	
	/**
	 * Tests if four points are concyclic
	 * @param cons The construction the lines depend on
	 * @param label the name of the resulting boolean
	 * @param inputPoint1 the first point
	 * @param inputPoint2 the second point
	 * @param inputPoint3 the third point
	 * @param inputPoint4 the forth point
	 */
	public AlgoAreConcyclic(Construction cons, String label, GeoPoint inputPoint1,
			GeoPoint inputPoint2, GeoPoint inputPoint3, GeoPoint inputPoint4) {
		super(cons);
		this.inputPoint1 = inputPoint1;
		this.inputPoint2 = inputPoint2;
		this.inputPoint3 = inputPoint3;
		this.inputPoint4 = inputPoint4;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
		// outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
        return Commands.AreConcyclic;
    }

	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = inputPoint1;
		input[1] = inputPoint2;
		input[2] = inputPoint3;
		input[3] = inputPoint4;

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}


	/**
	 * Gets the result of the test
	 * @return true if the points are concyclic and false otherwise
	 */
	
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		
		double ax=inputPoint1.getX(), ay=inputPoint1.getY(), az=inputPoint1.getZ(),
				bx=inputPoint2.getX(), by=inputPoint2.getY(), bz=inputPoint2.getZ(),
				cx=inputPoint3.getX(), cy=inputPoint3.getY(), cz=inputPoint3.getZ(),
				dx=inputPoint4.getX(), dy=inputPoint4.getY(), dz=inputPoint4.getZ();
		
		//Using Ptolomy's theorem
		
		double ab=cz*dz*Math.sqrt((bx*az-ax*bz)*(bx*az-ax*bz)+(by*az-ay*bz)*(by*az-ay*bz));
		double ac=bz*dz*Math.sqrt((cx*az-ax*cz)*(cx*az-ax*cz)+(cy*az-ay*cz)*(cy*az-ay*cz));
		double ad=bz*cz*Math.sqrt((dx*az-ax*dz)*(dx*az-ax*dz)+(dy*az-ay*dz)*(dy*az-ay*dz));
		double bc=az*dz*Math.sqrt((cx*bz-bx*cz)*(cx*bz-bx*cz)+(cy*bz-by*cz)*(cy*bz-by*cz));
		double bd=az*cz*Math.sqrt((dx*bz-bx*dz)*(dx*bz-bx*dz)+(dy*bz-by*dz)*(dy*bz-by*dz));
		double cd=az*bz*Math.sqrt((dx*cz-cx*dz)*(dx*cz-cx*dz)+(dy*cz-cy*dz)*(dy*cz-cy*dz));
		
		double precision = Kernel.getEpsilon();
		Kernel.setMinPrecision();
		
		if (Kernel.isZero((ab*cd+bc*ad-ac*bd)/(az*bz*cz*dz)) 
				|| Kernel.isZero((ab*cd+ac*bd-bc*ad)/(az*bz*cz*dz)) 
				|| Kernel.isZero((bc*ad+ac*bd-ab*cd)/(az*bz*cz*dz))){
			outputBoolean.setValue(true);
		} else {
			outputBoolean.setValue(false);
		}
		
		Kernel.setEpsilon(precision);
		
		/*
		
		double ax2=ax*ax, ay2=ay*ay, az2=az*az,
				bx2=bx*bx, by2=by*by, bz2=bz*bz,
				cx2=cx*cx, cy2=cy*cy, cz2=cz*cz,
				dx2=dx*dx, dy2=dy*dy, dz2=dz*dz;
		
		double det=
				ax2*bx*bz*cy*cz*dz2 - ax2*bx*bz*cz2*dy*dz - ax2*by*bz*cx*cz*dz2 + 
				ax2*by*bz*cz2*dx*dz + ax2*bz2*cx*cz*dy*dz - ax2*bz2*cy*cz*dx*dz - 
				ax*az*bx2*cy*cz*dz2 + ax*az*bx2*cz2*dy*dz - ax*az*by2*cy*cz*dz2 + 
				ax*az*by2*cz2*dy*dz + ax*az*by*bz*cx2*dz2 + ax*az*by*bz*cy2*dz2 - 
				ax*az*by*bz*cz2*dx2 - ax*az*by*bz*cz2*dy2 - ax*az*bz2*cx2*dy*dz - 
				ax*az*bz2*cy2*dy*dz + ax*az*bz2*cy*cz*dx2 + ax*az*bz2*cy*cz*dy2 + 
				ay2*bx*bz*cy*cz*dz2 - ay2*bx*bz*cz2*dy*dz - ay2*by*bz*cx*cz*dz2 + 
				ay2*by*bz*cz2*dx*dz + ay2*bz2*cx*cz*dy*dz - ay2*bz2*cy*cz*dx*dz + 
				ay*az*bx2*cx*cz*dz2 - ay*az*bx2*cz2*dx*dz - ay*az*bx*bz*cx2*dz2 - 
				ay*az*bx*bz*cy2*dz2 + ay*az*bx*bz*cz2*dx2 + ay*az*bx*bz*cz2*dy2 + 
				ay*az*by2*cx*cz*dz2 - ay*az*by2*cz2*dx*dz + ay*az*bz2*cx2*dx*dz - 
				ay*az*bz2*cx*cz*dx2 - ay*az*bz2*cx*cz*dy2 + ay*az*bz2*cy2*dx*dz - 
				az2*bx2*cx*cz*dy*dz + az2*bx2*cy*cz*dx*dz + az2*bx*bz*cx2*dy*dz + 
				az2*bx*bz*cy2*dy*dz - az2*bx*bz*cy*cz*dx2 - az2*bx*bz*cy*cz*dy2 - 
				az2*by2*cx*cz*dy*dz + az2*by2*cy*cz*dx*dz - az2*by*bz*cx2*dx*dz + 
				az2*by*bz*cx*cz*dx2 + az2*by*bz*cx*cz*dy2 - az2*by*bz*cy2*dx*dz;
		// There may be awful numerical errors introduced, so switching to
		// minimal precision for the current calculation (and then back):
		double precision = Kernel.getEpsilon();
		Kernel.setMinPrecision();
	    //outputBoolean.setValue(Kernel.isZero(det));
	    AbstractApplication.debug(det);
	    Kernel.setEpsilon(precision);
	    */
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint1 != null && inputPoint2 != null) {
			inputPoint1.getFreeVariables(variables);
			inputPoint2.getFreeVariables(variables);
			inputPoint3.getFreeVariables(variables);
			inputPoint4.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint1 != null && inputPoint2 != null) {
			int[] degree1 = inputPoint1.getDegrees();
			int[] degree2 = inputPoint2.getDegrees();
			int[] degree3 = inputPoint3.getDegrees();
			int[] degree4 = inputPoint4.getDegrees();

			int[] degree = new int[1];
			degree[0]=Math.max(degree1[1] + degree1[2] + degree2[0] + degree2[2] +   2*degree3[0],
							Math.max( degree1[0] + degree1[2] + degree2[1] + degree2[2] +   2*degree3[0],
							Math.max( degree1[1] + degree1[2] + degree2[0] + degree2[2] +   2*degree3[1],
							Math.max( degree1[0] + degree1[2] + degree2[1] + degree2[2] +   2*degree3[1],
							Math.max( degree1[1] + degree1[2] + 2*degree2[0] + degree3[0] +   degree3[2],
							Math.max( degree1[1] + degree1[2] + 2*degree2[1] + degree3[0] +   degree3[2],
							Math.max( 2*degree1[0] + degree2[1] + degree2[2] + degree3[0] +   degree3[2],
							Math.max( 2*degree1[1] + degree2[1] + degree2[2] + degree3[0] +   degree3[2],
							Math.max( 2*degree1[2] + degree2[1] + degree2[2] + degree3[0] +   degree3[2],
							Math.max( degree1[1] + degree1[2] + 2*degree2[2] + degree3[0] +   degree3[2],
							Math.max( degree1[0] + degree1[2] + 2*degree2[0] + degree3[1] +   degree3[2],
							Math.max( degree1[0] + degree1[2] + 2*degree2[1] + degree3[1] +   degree3[2],
							Math.max( 2*degree1[0] + degree2[0] + degree2[2] + degree3[1] +   degree3[2],
							Math.max( 2*degree1[1] + degree2[0] + degree2[2] + degree3[1] +   degree3[2],
							Math.max( 2*degree1[2] + degree2[0] + degree2[2] + degree3[1] +   degree3[2],
							Math.max( degree1[0] + degree1[2] + 2*degree2[2] + degree3[1] +   degree3[2],
							Math.max( degree1[1] + degree1[2] + degree2[0] + degree2[2] +   2*degree3[2],
							Math.max( degree1[0] + degree1[2] + degree2[1] + degree2[2] +   2*degree3[2],
							Math.max(2*degree4[0],
							Math.max( 2*degree1[2] + degree2[1] + degree2[2] + 2*degree3[0] +   degree4[0],
							Math.max( degree1[1] + degree1[2] + 2*degree2[2] + 2*degree3[0] +   degree4[0],
							Math.max( 2*degree1[2] + degree2[1] + degree2[2] + 2*degree3[1] +   degree4[0],
							Math.max( degree1[1] + degree1[2] + 2*degree2[2] + 2*degree3[1] +   degree4[0],
							Math.max( 2*degree1[2] + 2*degree2[0] + degree3[1] + degree3[2] +   degree4[0],
							Math.max( 2*degree1[2] + 2*degree2[1] + degree3[1] + degree3[2] +   degree4[0],
							Math.max( 2*degree1[0] + 2*degree2[2] + degree3[1] + degree3[2] +   degree4[0],
							Math.max( 2*degree1[1] + 2*degree2[2] + degree3[1] + degree3[2] +   degree4[0],
							Math.max( degree1[1] + degree1[2] + 2*degree2[0] + 2*degree3[2] +   degree4[0],
							Math.max( degree1[1] + degree1[2] + 2*degree2[1] + 2*degree3[2] +   degree4[0],
							Math.max( 2*degree1[0] + degree2[1] + degree2[2] + 2*degree3[2] +   degree4[0],
							Math.max( 2*degree1[1] + degree2[1] + degree2[2] + 2*degree3[2] +   degree4[0],
							Math.max(2*degree4[1],
							Math.max( 2*degree1[2] + degree2[0] + degree2[2] + 2*degree3[0] +   degree4[1],
							Math.max( degree1[0] + degree1[2] + 2*degree2[2] + 2*degree3[0] +   degree4[1],
							Math.max( 2*degree1[2] + degree2[0] + degree2[2] + 2*degree3[1] +   degree4[1],
							Math.max( degree1[0] + degree1[2] + 2*degree2[2] + 2*degree3[1] +   degree4[1],
							Math.max( 2*degree1[2] + 2*degree2[0] + degree3[0] + degree3[2] +   degree4[1],
							Math.max( 2*degree1[2] + 2*degree2[1] + degree3[0] + degree3[2] +   degree4[1],
							Math.max( 2*degree1[0] + 2*degree2[2] + degree3[0] + degree3[2] +   degree4[1],
							Math.max( 2*degree1[1] + 2*degree2[2] + degree3[0] + degree3[2] +   degree4[1],
							Math.max( degree1[0] + degree1[2] + 2*degree2[0] + 2*degree3[2] +   degree4[1],
							Math.max( degree1[0] + degree1[2] + 2*degree2[1] + 2*degree3[2] +   degree4[1],
							Math.max( 2*degree1[0] + degree2[0] + degree2[2] + 2*degree3[2] +   degree4[1],
							Math.max( 2*degree1[1] + degree2[0] + degree2[2] + 2*degree3[2] +   degree4[1],
							Math.max(degree4[2], 2*degree4[2])))))))))))))))))))))))))))))))))))))))))))));
			return degree;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null && inputPoint4 != null) {
			
			
			BigInteger[] coords1 = inputPoint1.getExactCoordinates(values);
			BigInteger[] coords2 = inputPoint2.getExactCoordinates(values);
			BigInteger[] coords3 = inputPoint3.getExactCoordinates(values);
			BigInteger[] coords4 = inputPoint4.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			BigInteger[][] matrix=new BigInteger[4][4];
			matrix[0][0]=coords1[0].multiply(coords1[2]);
			matrix[0][1]=coords1[1].multiply(coords1[2]);
			matrix[0][2]=coords1[0].multiply(coords1[0]).add(coords1[1].multiply(coords1[1]));
			matrix[0][3]=coords1[2].multiply(coords1[2]);
			
			matrix[1][0]=coords2[0].multiply(coords2[2]);
			matrix[1][1]=coords2[1].multiply(coords2[2]);
			matrix[1][2]=coords2[0].multiply(coords2[0]).add(coords2[1].multiply(coords2[1]));
			matrix[1][3]=coords2[2].multiply(coords2[2]);
			
			matrix[2][0]=coords3[0].multiply(coords3[2]);
			matrix[2][1]=coords3[1].multiply(coords3[2]);
			matrix[2][2]=coords3[0].multiply(coords3[0]).add(coords3[1].multiply(coords3[1]));
			matrix[2][3]=coords3[2].multiply(coords3[2]);
			
			matrix[3][0]=coords4[0].multiply(coords4[2]);
			matrix[3][1]=coords4[1].multiply(coords4[2]);
			matrix[3][2]=coords4[0].multiply(coords4[0]).add(coords4[1].multiply(coords4[1]));
			matrix[3][3]=coords4[2].multiply(coords4[2]);
			
			coords[0] = SymbolicParameters.det4(matrix);

			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null && inputPoint4 != null) {
			Polynomial[] coords1 = inputPoint1.getPolynomials();
			Polynomial[] coords2 = inputPoint2.getPolynomials();
			Polynomial[] coords3 = inputPoint3.getPolynomials();
			Polynomial[] coords4 = inputPoint4.getPolynomials();
			polynomials = new Polynomial[1];
			Polynomial[][] matrix=new Polynomial[4][4];
			matrix[0][0]=coords1[0].multiply(coords1[2]);
			matrix[0][1]=coords1[1].multiply(coords1[2]);
			matrix[0][2]=coords1[0].multiply(coords1[0]).add(coords1[1].multiply(coords1[1]));
			matrix[0][3]=coords1[2].multiply(coords1[2]);
			
			matrix[1][0]=coords2[0].multiply(coords2[2]);
			matrix[1][1]=coords2[1].multiply(coords2[2]);
			matrix[1][2]=coords2[0].multiply(coords2[0]).add(coords2[1].multiply(coords2[1]));
			matrix[1][3]=coords2[2].multiply(coords2[2]);
			
			matrix[2][0]=coords3[0].multiply(coords3[2]);
			matrix[2][1]=coords3[1].multiply(coords3[2]);
			matrix[2][2]=coords3[0].multiply(coords3[0]).add(coords3[1].multiply(coords3[1]));
			matrix[2][3]=coords3[2].multiply(coords3[2]);
			
			matrix[3][0]=coords4[0].multiply(coords4[2]);
			matrix[3][1]=coords4[1].multiply(coords4[2]);
			matrix[3][2]=coords4[0].multiply(coords4[0]).add(coords4[1].multiply(coords4[1]));
			matrix[3][3]=coords4[2].multiply(coords4[2]);
			
			polynomials[0] = Polynomial.det4(matrix);

			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null && inputPoint4 != null) {
			Variable[] coords1 = inputPoint1.getBotanaVars(inputPoint1);
			Variable[] coords2 = inputPoint2.getBotanaVars(inputPoint2);
			Variable[] coords3 = inputPoint3.getBotanaVars(inputPoint3);
			Variable[] coords4 = inputPoint4.getBotanaVars(inputPoint4);
			botanaPolynomials = new Polynomial[1][1];
			Polynomial[][] matrix=new Polynomial[4][4];

			matrix[0][0]=new Polynomial(coords1[0]);
			matrix[0][1]=new Polynomial(coords1[1]);
			matrix[0][2]=matrix[0][0].multiply(matrix[0][0]).add(matrix[0][1].multiply(matrix[0][1]));
			matrix[0][3]=new Polynomial(1);
			
			matrix[1][0]=new Polynomial(coords2[0]);
			matrix[1][1]=new Polynomial(coords2[1]);
			matrix[1][2]=matrix[1][0].multiply(matrix[1][0]).add(matrix[1][1].multiply(matrix[1][1]));
			matrix[1][3]=new Polynomial(1);
			
			matrix[2][0]=new Polynomial(coords3[0]);
			matrix[2][1]=new Polynomial(coords3[1]);
			matrix[2][2]=matrix[2][0].multiply(matrix[2][0]).add(matrix[2][1].multiply(matrix[2][1]));
			matrix[2][3]=new Polynomial(1);
			
			matrix[3][0]=new Polynomial(coords4[0]);
			matrix[3][1]=new Polynomial(coords4[1]);
			matrix[3][2]=matrix[3][0].multiply(matrix[3][0]).add(matrix[3][1].multiply(matrix[3][1]));
			matrix[3][3]=new Polynomial(1);
			
			botanaPolynomials[0][0] = Polynomial.det4(matrix);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability

}
