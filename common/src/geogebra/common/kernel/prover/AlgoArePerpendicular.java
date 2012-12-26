package geogebra.common.kernel.prover;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.main.App;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Decides if the lines are perpendicular.
 * Can be embedded into the Prove command to work symbolically.
 * @author Simon Weitzhofer
 *  17th of May 2012
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoArePerpendicular extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoLine inputLine1; // input
	private GeoLine inputLine2; // input

	private GeoBoolean outputBoolean; // output
	private Polynomial[] polynomials;
	private Polynomial[][] botanaPolynomials;

	/**
	 * Tests if two lines are perpendicular
	 * @param cons The construction the lines depend on
	 * @param label the name of the resulting boolean
	 * @param inputLine1 the first line
	 * @param inputLine2 the second line
	 */
	public AlgoArePerpendicular(Construction cons, String label, GeoLine inputLine1,
			GeoLine inputLine2) {
		super(cons);
		this.inputLine1 = inputLine1;
		this.inputLine2 = inputLine2;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
		// outputBoolean.setLabel(label);
	}
	
	@Override
	public Commands getClassName() {
        return Commands.ArePerpendicular;
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
	 * @return true if the lines are perpendicular and false otherwise
	 */
	
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(inputLine1.isPerpendicular(inputLine2));
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (inputLine1 != null && inputLine2 != null) {
			inputLine1.getFreeVariables(variables);
			inputLine2.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (inputLine1 != null && inputLine2 != null) {
			int[] degree1 = inputLine1.getDegrees();
			int[] degree2 = inputLine2.getDegrees();
			int[] degree = new int[1];
			degree[0]=Math.max(degree1[0]+degree2[0], degree1[1]+degree2[1]);
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
			coords[0] = coords1[0].multiply(coords2[0]).add(
					coords1[1].multiply(coords2[1]));

			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		App.debug(polynomials);
		if (polynomials != null) {
			return polynomials;
		}
		if (inputLine1 != null && inputLine2 != null) {
			Polynomial[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getPolynomials();
			Polynomial[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getPolynomials();
			polynomials = new Polynomial[1];
			polynomials[0] = coords1[0].multiply(coords2[0]).add(
					coords1[1].multiply(coords2[1]));

			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (inputLine1 != null && inputLine2 != null) {
			Variable[] v1 = new Variable[4];
			Variable[] v2 = new Variable[4];
			v1 = ((SymbolicParametersBotanaAlgo) inputLine1).getBotanaVars(inputLine1); // (a1,a2,b1,b2)
			v2 = ((SymbolicParametersBotanaAlgo) inputLine2).getBotanaVars(inputLine2); // (c1,c2,d1,d2)
			
			botanaPolynomials = new Polynomial[1][1];
			botanaPolynomials[0][0] = Polynomial.perpendicular(v1[0], v1[1], v1[2], v1[3], v2[0], v2[1], v2[2], v2[3]);
			return botanaPolynomials;
		}
		return null;
	}

	// TODO Consider locusequability

}
