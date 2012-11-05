package geogebra.common.kernel.prover;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.App;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Decides if the objects are equal.
 * Can be embedded into the Prove command to work symbolically.
 * This class should work exactly the same as the "==" operation. 
 * @author Simon Weitzhofer
 *  17th of may 2012
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoAreEqual extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoElement inputElement1; // input
	private GeoElement inputElement2; // input

	private GeoBoolean outputBoolean; // output
	private Polynomial[] polynomials;
	private Polynomial[][] botanaPolynomials;

	/**
	 * Tests if two objects are equal
	 * @param cons The construction the objects depend on
	 * @param label the name of the resulting boolean
	 * @param inputElement1 the first object
	 * @param inputElement2 the second object
	 */
	public AlgoAreEqual(Construction cons, String label, GeoElement inputElement1,
			GeoElement inputElement2) {
		super(cons);
		this.inputElement1 = inputElement1;
		this.inputElement2 = inputElement2;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
		// outputBoolean.setLabel(label);
	}
		
	@Override
	public Algos getClassName() {
		return Algos.AlgoAreEqual;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputElement1;
		input[1] = inputElement2;

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Gets the result of the test
	 * @return true if the objects are equal and false otherwise
	 */
	
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		// Formerly we used this:
		// outputBoolean.setValue(inputElement1.isEqual(inputElement2));
		// But we prefer to have the same output as for inputElement1 == inputElement2 for being consistent: 
		outputBoolean.setValue(ExpressionNodeEvaluator.evalEquals(kernel, inputElement1, inputElement2).getBoolean());
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment) || (inputElement2 instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))||
				((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))||
				((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))){
				((SymbolicParametersAlgo)inputElement1).getFreeVariables(variables);
				((SymbolicParametersAlgo)inputElement2).getFreeVariables(variables);
				return;
			}
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment) || (inputElement2 instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))||
				((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))||
				((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))){
				int[] degrees1=((SymbolicParametersAlgo)inputElement1).getDegrees();
				int[] degrees2=((SymbolicParametersAlgo)inputElement2).getDegrees();
				int[] degrees=new int[1];
				degrees[0] = Math.max(Math.max(degrees1[0]+degrees2[2], degrees2[0]+degrees1[2]),
						Math.max(degrees1[1]+degrees2[2], degrees2[1]+degrees1[2]));
				return degrees;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))) {
				BigInteger[] coords1 = ((SymbolicParametersAlgo) inputElement1)
						.getExactCoordinates(values);
				BigInteger[] coords2 = ((SymbolicParametersAlgo) inputElement2)
						.getExactCoordinates(values);
				BigInteger[] coords = new BigInteger[1];
				coords[0] = coords1[0]
						.multiply(coords2[2])
						.subtract(coords2[0].multiply(coords1[2]))
						.abs()
						.add(coords1[1].multiply(coords2[2])
								.subtract(coords2[1].multiply(coords1[2]))
								.abs());
				return coords;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		App.debug(polynomials);
		if (polynomials != null) {
			return polynomials;
		}
		if ((inputElement1 instanceof GeoSegment) || (inputElement2 instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))||
				((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))||
				((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))){
				Polynomial[] coords1=((SymbolicParametersAlgo)inputElement1).getPolynomials();
				Polynomial[] coords2=((SymbolicParametersAlgo)inputElement2).getPolynomials();
				polynomials=new Polynomial[2];
				polynomials[0]=coords1[0].multiply(coords2[2]).subtract(coords2[0].multiply(coords1[2]));
				polynomials[1]=coords1[1].multiply(coords2[2]).subtract(coords2[1].multiply(coords1[2]));
				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoPoint && inputElement2 instanceof GeoPoint) {
			botanaPolynomials = new Polynomial[2][1];

			Variable[] v1 = new Variable[2];
			Variable[] v2 = new Variable[2];
			v1 = ((GeoPoint) inputElement1).getBotanaVars(inputElement1); // A=(x1,y1)
			v2 = ((GeoPoint) inputElement2).getBotanaVars(inputElement2); // B=(x2,y2)

			// We want to prove: 1) x1-x2==0, 2) y1-y2==0
			botanaPolynomials[0][0] = new Polynomial(v1[0]).subtract(new Polynomial(v2[0])); 
			botanaPolynomials[1][0] = new Polynomial(v1[1]).subtract(new Polynomial(v2[1])); 
			return botanaPolynomials;
		}
		
		// Order is important here: a GeoSegment is also a GeoLine!
		if (inputElement1 instanceof GeoSegment && inputElement2 instanceof GeoSegment) {
			// We check whether their length are equal.
			botanaPolynomials = new Polynomial[1][1];

			Variable[] v1 = new Variable[4];
			Variable[] v2 = new Variable[4];
			v1 = ((GeoSegment) inputElement1).getBotanaVars(inputElement1); // AB
			v2 = ((GeoSegment) inputElement2).getBotanaVars(inputElement2); // CD

			// We want to prove: d(AB)=d(CD) => (a1-b1)^2+(a2-b2)^2=(c1-d1)^2+(c2-d2)^2
			// => (a1-b1)^2+(a2-b2)^2-(c1-d1)^2-(c2-d2)^2
			Polynomial a1 = new Polynomial(v1[0]);
			Polynomial a2 = new Polynomial(v1[1]);
			Polynomial b1 = new Polynomial(v1[2]);
			Polynomial b2 = new Polynomial(v1[3]);
			Polynomial c1 = new Polynomial(v2[0]);
			Polynomial c2 = new Polynomial(v2[1]);
			Polynomial d1 = new Polynomial(v2[2]);
			Polynomial d2 = new Polynomial(v2[3]);
			botanaPolynomials[0][0] = ((Polynomial.sqr(a1.subtract(b1)).add(
					Polynomial.sqr(a2.subtract(b2)))).subtract(Polynomial.sqr(c1.subtract(d1)))).
					subtract(Polynomial.sqr(c2.subtract(d2))); 
			 
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoLine && inputElement2 instanceof GeoLine) {
			botanaPolynomials = new Polynomial[2][1];

			Variable[] v1 = new Variable[4];
			Variable[] v2 = new Variable[4];
			v1 = ((GeoLine) inputElement1).getBotanaVars(inputElement1); // AB
			v2 = ((GeoLine) inputElement2).getBotanaVars(inputElement2); // CD

			// We want to prove: 1) ABC collinear, 2) ABD collinear
			botanaPolynomials[0][0] = Polynomial.collinear(v1[0], v1[1], v1[2], v1[3], v2[0], v2[1]); 
			botanaPolynomials[1][0] = Polynomial.collinear(v1[0], v1[1], v1[2], v1[3], v2[2], v2[3]); 
			return botanaPolynomials;
		}

		// TODO: Implement circles etc.
		
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability
}
