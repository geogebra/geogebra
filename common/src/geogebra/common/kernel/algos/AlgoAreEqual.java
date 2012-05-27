package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.main.AbstractApplication;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Simon Weitzhofer
 *  17th of may 2012
 * 
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
		outputBoolean.setLabel(label);
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
		outputBoolean.setValue(inputElement1.isEqual(inputElement2));
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment) || (inputElement2 instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint2) && (inputElement2 instanceof GeoPoint2))||
				((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))||
				((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))){
				int[] degrees1=((SymbolicParametersAlgo)inputElement1).getFreeVariablesAndDegrees(variables);
				int[] degrees2=((SymbolicParametersAlgo)inputElement2).getFreeVariablesAndDegrees(variables);
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
			if (((inputElement1 instanceof GeoPoint2) && (inputElement2 instanceof GeoPoint2))
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
		AbstractApplication.debug(polynomials);
		if (polynomials != null) {
			return polynomials;
		}
		if ((inputElement1 instanceof GeoSegment) || (inputElement2 instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint2) && (inputElement2 instanceof GeoPoint2))||
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

		if (inputElement1 instanceof GeoPoint2 && inputElement2 instanceof GeoPoint2) {
			botanaPolynomials = new Polynomial[2][1];

			Variable[] v1 = new Variable[2];
			Variable[] v2 = new Variable[2];
			v1 = ((GeoPoint2) inputElement1).getBotanaVars(); // A=(x1,y1)
			v2 = ((GeoPoint2) inputElement2).getBotanaVars(); // B=(x2,y2)

			// We want to prove: 1) x1-x2==0, 2) y1-y2==0
			botanaPolynomials[0][0] = new Polynomial(v1[0]).subtract(new Polynomial(v2[0])); 
			botanaPolynomials[1][0] = new Polynomial(v1[1]).subtract(new Polynomial(v2[1])); 
			return botanaPolynomials;
		}
		
		// TODO: Implement lines, and maybe segments, also circles etc.
		
		throw new NoSymbolicParametersException();
	}
}
