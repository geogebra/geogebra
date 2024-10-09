package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.debug.Log;

/**
 * Decides if the lines are perpendicular. Can be embedded into the Prove
 * command to work symbolically.
 * 
 * @author Simon Weitzhofer 17th of May 2012
 * @author Zoltan Kovacs
 */
public class AlgoArePerpendicular extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoLine inputLine1; // input
	private GeoLine inputLine2; // input

	private GeoBoolean outputBoolean; // output
	private PPolynomial[] polynomials;
	private PPolynomial[][] botanaPolynomials;

	/**
	 * Tests if two lines are perpendicular
	 * 
	 * @param cons
	 *            The construction the lines depend on
	 * @param inputLine1
	 *            the first line
	 * @param inputLine2
	 *            the second line
	 */
	public AlgoArePerpendicular(Construction cons, GeoElement inputLine1,
			GeoElement inputLine2) {
		super(cons);
		this.inputLine1 = (GeoLine) inputLine1;
		this.inputLine2 = (GeoLine) inputLine2;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

	/**
	 * Tests if two lines are perpendicular
	 * 
	 * @param cons
	 *            The construction the lines depend on
	 * @param label
	 *            the label for the AlgoArePerpendicular object
	 * @param inputLine1
	 *            the first line
	 * @param inputLine2
	 *            the second line
	 */
	public AlgoArePerpendicular(Construction cons, String label,
			GeoElement inputLine1, GeoElement inputLine2) {

		this(cons, inputLine1, inputLine2);
		outputBoolean.setLabel(label);
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

		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Gets the result of the test
	 * 
	 * @return true if the lines are perpendicular and false otherwise
	 */

	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(inputLine1.isPerpendicular(inputLine2));
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (inputLine1 != null && inputLine2 != null) {
			inputLine1.getFreeVariables(variables);
			inputLine2.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (inputLine1 != null && inputLine2 != null) {
			int[] degree1 = inputLine1.getDegrees(a);
			int[] degree2 = inputLine2.getDegrees(a);
			int[] degree = new int[1];
			degree[0] = Math.max(degree1[0] + degree2[0],
					degree1[1] + degree2[1]);
			return degree;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (inputLine1 != null && inputLine2 != null) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getExactCoordinates(values);
			BigInteger[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			coords[0] = coords1[0].multiply(coords2[0])
					.add(coords1[1].multiply(coords2[1]));

			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		Log.debug(polynomials);
		if (polynomials != null) {
			return polynomials;
		}
		if (inputLine1 != null && inputLine2 != null) {
			PPolynomial[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getPolynomials();
			PPolynomial[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getPolynomials();
			polynomials = new PPolynomial[1];
			polynomials[0] = coords1[0].multiply(coords2[0])
					.add(coords1[1].multiply(coords2[1]));

			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (inputLine1 != null && inputLine2 != null) {
			PVariable[] v1 = ((SymbolicParametersBotanaAlgo) inputLine1)
					.getBotanaVars(inputLine1); // (a1,a2,b1,b2)
			PVariable[] v2 = ((SymbolicParametersBotanaAlgo) inputLine2)
					.getBotanaVars(inputLine2); // (c1,c2,d1,d2)

			botanaPolynomials = new PPolynomial[1][1];
			botanaPolynomials[0][0] = PPolynomial.perpendicular(v1[0], v1[1],
					v1[2], v1[3], v2[0], v2[1], v2[2], v2[3]);
			return botanaPolynomials;
		}
		return null;
	}

}
