package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Decides if the lines are concurrent. Can be embedded into the Prove command
 * to work symbolically.
 * 
 * @author Simon Weitzhofer 18th April 2012
 * @author Zoltan Kovacs
 */
public class AlgoAreConcurrent extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoLine inputLine1, inputLine2, inputLine3; // input

	private GeoBoolean outputBoolean; // output
	private PPolynomial[] polynomials;
	private PPolynomial[][] botanaPolynomials;

	/**
	 * Creates a new AlgoAreConcurrent function
	 * 
	 * @param cons
	 *            the Construction
	 * @param inputLine1
	 *            the first line
	 * @param inputLine2
	 *            the second line
	 * @param inputLine3
	 *            the third line
	 */
	public AlgoAreConcurrent(final Construction cons, final GeoLine inputLine1,
			final GeoLine inputLine2, final GeoLine inputLine3) {
		super(cons);
		this.inputLine1 = inputLine1;
		this.inputLine2 = inputLine2;
		this.inputLine3 = inputLine3;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

	/**
	 * Creates a new AlgoAreConcurrent function
	 * 
	 * @param cons
	 *            the Construction
	 * @param label
	 *            the label for the AlgoAreConcurrent object
	 * @param inputLine1
	 *            the first line
	 * @param inputLine2
	 *            the second line
	 * @param inputLine3
	 *            the third line
	 */
	public AlgoAreConcurrent(final Construction cons, final String label,
			final GeoLine inputLine1, final GeoLine inputLine2,
			final GeoLine inputLine3) {
		this(cons, inputLine1, inputLine2, inputLine3);
		outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.AreConcurrent;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = inputLine1;
		input[1] = inputLine2;
		input[2] = inputLine3;

		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the result of the test
	 * 
	 * @return true if the three lines are concurrent i.e. they intersect in a
	 *         common point, false otherwise
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(
				GeoLine.concurrent(inputLine1, inputLine2, inputLine3));
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			inputLine1.getFreeVariables(variables);
			inputLine2.getFreeVariables(variables);
			inputLine3.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			int[] degree1 = inputLine1.getDegrees(a);
			int[] degree2 = inputLine2.getDegrees(a);
			int[] degree3 = inputLine3.getDegrees(a);
			int[] result = new int[1];
			result[0] = Math.max(degree1[0] + degree2[1] + degree3[2], Math.max(
					degree2[0] + degree3[1] + degree1[2],
					Math.max(degree3[0] + degree1[1] + degree2[2], Math.max(
							degree3[0] + degree2[1] + degree1[2], Math.max(
									degree2[0] + degree1[1] + degree3[2],
									degree1[0] + degree3[1] + degree2[2])))));
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			BigInteger[] coords1 = inputLine1.getExactCoordinates(values);
			BigInteger[] coords2 = inputLine2.getExactCoordinates(values);
			BigInteger[] coords3 = inputLine3.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			coords[0] = coords1[0].multiply(coords2[1]).multiply(coords3[2])
					.add(coords2[0].multiply(coords3[1]).multiply(coords1[2]))
					.add(coords3[0].multiply(coords1[1]).multiply(coords2[2]))
					.subtract(

							coords3[0].multiply(coords2[1]).multiply(coords1[2])
									.add(coords2[0].multiply(coords1[1])
											.multiply(coords3[2]))
									.add(coords1[0].multiply(coords3[1])
											.multiply(coords2[2])));
			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			PPolynomial[] coords1 = inputLine1.getPolynomials();
			PPolynomial[] coords2 = inputLine2.getPolynomials();
			PPolynomial[] coords3 = inputLine3.getPolynomials();
			polynomials = new PPolynomial[1];
			polynomials[0] = coords1[0].multiply(coords2[1])
					.multiply(coords3[2])
					.add(coords2[0].multiply(coords3[1]).multiply(coords1[2]))
					.add(coords3[0].multiply(coords1[1]).multiply(coords2[2]))
					.subtract(

							coords3[0].multiply(coords2[1]).multiply(coords1[2])
									.add(coords2[0].multiply(coords1[1])
											.multiply(coords3[2]))
									.add(coords1[0].multiply(coords3[1])
											.multiply(coords2[2])));
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

		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {

			PVariable[][] v = new PVariable[3][4];
			v[0] = inputLine1.getBotanaVars(inputLine1);
			v[1] = inputLine2.getBotanaVars(inputLine2);
			v[2] = inputLine3.getBotanaVars(inputLine3);

			PVariable[] nv = new PVariable[2]; // new point for collinearity
			nv[0] = new PVariable(kernel);
			nv[1] = new PVariable(kernel);

			// We need three collinearities with an extra point.
			botanaPolynomials = new PPolynomial[1][3];
			for (int i = 0; i < 3; ++i) {
				botanaPolynomials[0][i] = PPolynomial.collinear(v[i][0], v[i][1],
						v[i][2], v[i][3], nv[0], nv[1]);
			}

			return botanaPolynomials;

		}
		throw new NoSymbolicParametersException();
	}

}
