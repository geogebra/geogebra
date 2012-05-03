package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.main.AbstractApplication;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Simon Weitzhofer
 *  27th of April 2012
 * 
 */
public class AlgoAreParallel extends AlgoElement implements
		SymbolicParametersAlgo {

	private GeoLine inputLine1; // input
	private GeoLine inputLine2; // input

	private GeoBoolean outputBoolean; // output
	private Polynomial[] polynomials;

	/**
	 * Tests if two lines are parallel
	 * @param cons The construction the lines depend on
	 * @param label the name of the resulting boolean
	 * @param inputLine1 the first line
	 * @param inputLine2 the second line
	 */
	public AlgoAreParallel(Construction cons, String label, GeoLine inputLine1,
			GeoLine inputLine2) {
		super(cons);
		this.inputLine1 = inputLine1;
		this.inputLine2 = inputLine2;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
		outputBoolean.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoAreParallel;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputLine1;
		input[1] = inputLine2;

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}


	/**
	 * Gets the result of the test
	 * @return true if the lines are parallel and false otherwise
	 */
	
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(Kernel.isZero(inputLine1.x * inputLine2.y
				- inputLine2.x * inputLine1.y));
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (inputLine1 != null && inputLine2 != null) {
			int[] degree1 = inputLine1.getFreeVariablesAndDegrees(variables);
			int[] degree2 = inputLine2.getFreeVariablesAndDegrees(variables);
			int[] degree = new int[1];
			degree[0]=Math.max(degree1[0]+degree2[1], degree1[1]+degree2[0]);
			return degree;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (inputLine1 != null && inputLine2 != null) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getExactCoordinates(values);
			BigInteger[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			coords[0] = coords1[0].multiply(coords2[1]).subtract(
					coords1[1].multiply(coords2[0]));

			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		AbstractApplication.debug(polynomials);
		if (polynomials != null) {
			return polynomials;
		}
		if (inputLine1 != null && inputLine2 != null) {
			Polynomial[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getPolynomials();
			Polynomial[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getPolynomials();
			polynomials = new Polynomial[1];
			polynomials[0] = coords1[0].multiply(coords2[1]).subtract(
					coords1[1].multiply(coords2[0]));

			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars() {
		// TODO Auto-generated method stub
		return null;
	}

	public Polynomial[] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		// TODO Auto-generated method stub
		return null;
	}

}
