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
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Decides if the points are concyclic. Can be embedded into the Prove command
 * to work symbolically.
 * 
 * @author Simon Weitzhofer 27th of April 2012
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoAreConcyclic extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoPoint inputPoint1; // input
	private GeoPoint inputPoint2; // input
	private GeoPoint inputPoint3; // input
	private GeoPoint inputPoint4; // input

	private GeoBoolean outputBoolean; // output
	private PPolynomial[] polynomials;
	private PPolynomial[][] botanaPolynomials;

	/**
	 * Tests if four points are concyclic
	 * 
	 * @param cons
	 *            The construction the lines depend on
	 * @param inputPoint1
	 *            the first point
	 * @param inputPoint2
	 *            the second point
	 * @param inputPoint3
	 *            the third point
	 * @param inputPoint4
	 *            the forth point
	 */
	public AlgoAreConcyclic(Construction cons, GeoPoint inputPoint1,
			GeoPoint inputPoint2, GeoPoint inputPoint3, GeoPoint inputPoint4) {
		super(cons);
		this.inputPoint1 = inputPoint1;
		this.inputPoint2 = inputPoint2;
		this.inputPoint3 = inputPoint3;
		this.inputPoint4 = inputPoint4;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

	/**
	 * Tests if four points are concyclic
	 * 
	 * @param cons
	 *            The construction the lines depend on
	 * @param label
	 *            the label for the AlgoAreConcyclic object
	 * @param inputPoint1
	 *            the first point
	 * @param inputPoint2
	 *            the second point
	 * @param inputPoint3
	 *            the third point
	 * @param inputPoint4
	 *            the forth point
	 */
	public AlgoAreConcyclic(Construction cons, String label,
			GeoPoint inputPoint1, GeoPoint inputPoint2, GeoPoint inputPoint3,
			GeoPoint inputPoint4) {
		this(cons, inputPoint1, inputPoint2, inputPoint3, inputPoint4);
		outputBoolean.setLabel(label);
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

		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Gets the result of the test
	 * 
	 * @return true if the points are concyclic and false otherwise
	 */

	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(GeoPoint.concyclic(inputPoint1, inputPoint2,
				inputPoint3, inputPoint4));
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null
				&& inputPoint4 != null) {
			inputPoint1.getFreeVariables(variables);
			inputPoint2.getFreeVariables(variables);
			inputPoint3.getFreeVariables(variables);
			inputPoint4.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null
				&& inputPoint4 != null) {
			int[] degree1 = inputPoint1.getDegrees(a);
			int[] degree2 = inputPoint2.getDegrees(a);
			int[] degree3 = inputPoint3.getDegrees(a);
			int[] degree4 = inputPoint4.getDegrees(a);

			int[] degree = new int[1];

			int[] list = {
					degree1[1] + degree1[2] + degree2[0] + degree2[2]
							+ 2 * degree3[0],
					degree1[0] + degree1[2] + degree2[1] + degree2[2]
							+ 2 * degree3[0],
					degree1[1] + degree1[2] + degree2[0] + degree2[2]
							+ 2 * degree3[1],
					degree1[0] + degree1[2] + degree2[1] + degree2[2]
							+ 2 * degree3[1],
					degree1[1] + degree1[2] + 2 * degree2[0] + degree3[0]
							+ degree3[2],
					degree1[1] + degree1[2] + 2 * degree2[1] + degree3[0]
							+ degree3[2],
					2 * degree1[0] + degree2[1] + degree2[2] + degree3[0]
							+ degree3[2],
					2 * degree1[1] + degree2[1] + degree2[2] + degree3[0]
							+ degree3[2],
					2 * degree1[2] + degree2[1] + degree2[2] + degree3[0]
							+ degree3[2],
					degree1[1] + degree1[2] + 2 * degree2[2] + degree3[0]
							+ degree3[2],
					degree1[0] + degree1[2] + 2 * degree2[0] + degree3[1]
							+ degree3[2],
					degree1[0] + degree1[2] + 2 * degree2[1] + degree3[1]
							+ degree3[2],
					2 * degree1[0] + degree2[0] + degree2[2] + degree3[1]
							+ degree3[2],
					2 * degree1[1] + degree2[0] + degree2[2] + degree3[1]
							+ degree3[2],
					2 * degree1[2] + degree2[0] + degree2[2] + degree3[1]
							+ degree3[2],
					degree1[0] + degree1[2] + 2 * degree2[2] + degree3[1]
							+ degree3[2],
					degree1[1] + degree1[2] + degree2[0] + degree2[2]
							+ 2 * degree3[2],
					degree1[0] + degree1[2] + degree2[1] + degree2[2]
							+ 2 * degree3[2],
					2 * degree4[0],
					2 * degree1[2] + degree2[1] + degree2[2] + 2 * degree3[0]
							+ degree4[0],
					degree1[1] + degree1[2] + 2 * degree2[2] + 2 * degree3[0]
							+ degree4[0],
					2 * degree1[2] + degree2[1] + degree2[2] + 2 * degree3[1]
							+ degree4[0],
					degree1[1] + degree1[2] + 2 * degree2[2] + 2 * degree3[1]
							+ degree4[0],
					2 * degree1[2] + 2 * degree2[0] + degree3[1] + degree3[2]
							+ degree4[0],
					2 * degree1[2] + 2 * degree2[1] + degree3[1] + degree3[2]
							+ degree4[0],
					2 * degree1[0] + 2 * degree2[2] + degree3[1] + degree3[2]
							+ degree4[0],
					2 * degree1[1] + 2 * degree2[2] + degree3[1] + degree3[2]
							+ degree4[0],
					degree1[1] + degree1[2] + 2 * degree2[0] + 2 * degree3[2]
							+ degree4[0],
					degree1[1] + degree1[2] + 2 * degree2[1] + 2 * degree3[2]
							+ degree4[0],
					2 * degree1[0] + degree2[1] + degree2[2] + 2 * degree3[2]
							+ degree4[0],
					2 * degree1[1] + degree2[1] + degree2[2] + 2 * degree3[2]
							+ degree4[0],
					2 * degree4[1],
					2 * degree1[2] + degree2[0] + degree2[2] + 2 * degree3[0]
							+ degree4[1],
					degree1[0] + degree1[2] + 2 * degree2[2] + 2 * degree3[0]
							+ degree4[1],
					2 * degree1[2] + degree2[0] + degree2[2] + 2 * degree3[1]
							+ degree4[1],
					degree1[0] + degree1[2] + 2 * degree2[2] + 2 * degree3[1]
							+ degree4[1],
					2 * degree1[2] + 2 * degree2[0] + degree3[0] + degree3[2]
							+ degree4[1],
					2 * degree1[2] + 2 * degree2[1] + degree3[0] + degree3[2]
							+ degree4[1],
					2 * degree1[0] + 2 * degree2[2] + degree3[0] + degree3[2]
							+ degree4[1],
					2 * degree1[1] + 2 * degree2[2] + degree3[0] + degree3[2]
							+ degree4[1],
					degree1[0] + degree1[2] + 2 * degree2[0] + 2 * degree3[2]
							+ degree4[1],
					degree1[0] + degree1[2] + 2 * degree2[1] + 2 * degree3[2]
							+ degree4[1],
					2 * degree1[0] + degree2[0] + degree2[2] + 2 * degree3[2]
							+ degree4[1],
					2 * degree1[1] + degree2[0] + degree2[2] + 2 * degree3[2]
							+ degree4[1],
					degree4[2], 2 * degree4[2] };

			// find max of list
			int max = list[0];
			for (int i = 1; i < list.length; i++) {
				if (list[i] > max) {
					max = list[i];
				}
			}

			degree[0] = max;

			return degree;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null
				&& inputPoint4 != null) {

			BigInteger[] coords1 = inputPoint1.getExactCoordinates(values);
			BigInteger[] coords2 = inputPoint2.getExactCoordinates(values);
			BigInteger[] coords3 = inputPoint3.getExactCoordinates(values);
			BigInteger[] coords4 = inputPoint4.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			BigInteger[][] matrix = new BigInteger[4][4];
			matrix[0][0] = coords1[0].multiply(coords1[2]);
			matrix[0][1] = coords1[1].multiply(coords1[2]);
			matrix[0][2] = coords1[0].multiply(coords1[0])
					.add(coords1[1].multiply(coords1[1]));
			matrix[0][3] = coords1[2].multiply(coords1[2]);

			matrix[1][0] = coords2[0].multiply(coords2[2]);
			matrix[1][1] = coords2[1].multiply(coords2[2]);
			matrix[1][2] = coords2[0].multiply(coords2[0])
					.add(coords2[1].multiply(coords2[1]));
			matrix[1][3] = coords2[2].multiply(coords2[2]);

			matrix[2][0] = coords3[0].multiply(coords3[2]);
			matrix[2][1] = coords3[1].multiply(coords3[2]);
			matrix[2][2] = coords3[0].multiply(coords3[0])
					.add(coords3[1].multiply(coords3[1]));
			matrix[2][3] = coords3[2].multiply(coords3[2]);

			matrix[3][0] = coords4[0].multiply(coords4[2]);
			matrix[3][1] = coords4[1].multiply(coords4[2]);
			matrix[3][2] = coords4[0].multiply(coords4[0])
					.add(coords4[1].multiply(coords4[1]));
			matrix[3][3] = coords4[2].multiply(coords4[2]);

			coords[0] = SymbolicParameters.det4(matrix);

			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null
				&& inputPoint4 != null) {
			PPolynomial[] coords1 = inputPoint1.getPolynomials();
			PPolynomial[] coords2 = inputPoint2.getPolynomials();
			PPolynomial[] coords3 = inputPoint3.getPolynomials();
			PPolynomial[] coords4 = inputPoint4.getPolynomials();
			polynomials = new PPolynomial[1];
			PPolynomial[][] matrix = new PPolynomial[4][4];
			matrix[0][0] = coords1[0].multiply(coords1[2]);
			matrix[0][1] = coords1[1].multiply(coords1[2]);
			matrix[0][2] = coords1[0].multiply(coords1[0])
					.add(coords1[1].multiply(coords1[1]));
			matrix[0][3] = coords1[2].multiply(coords1[2]);

			matrix[1][0] = coords2[0].multiply(coords2[2]);
			matrix[1][1] = coords2[1].multiply(coords2[2]);
			matrix[1][2] = coords2[0].multiply(coords2[0])
					.add(coords2[1].multiply(coords2[1]));
			matrix[1][3] = coords2[2].multiply(coords2[2]);

			matrix[2][0] = coords3[0].multiply(coords3[2]);
			matrix[2][1] = coords3[1].multiply(coords3[2]);
			matrix[2][2] = coords3[0].multiply(coords3[0])
					.add(coords3[1].multiply(coords3[1]));
			matrix[2][3] = coords3[2].multiply(coords3[2]);

			matrix[3][0] = coords4[0].multiply(coords4[2]);
			matrix[3][1] = coords4[1].multiply(coords4[2]);
			matrix[3][2] = coords4[0].multiply(coords4[0])
					.add(coords4[1].multiply(coords4[1]));
			matrix[3][3] = coords4[2].multiply(coords4[2]);

			polynomials[0] = PPolynomial.det4(matrix);

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
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null
				&& inputPoint4 != null) {
			PVariable[] coords1 = inputPoint1.getBotanaVars(inputPoint1);
			PVariable[] coords2 = inputPoint2.getBotanaVars(inputPoint2);
			PVariable[] coords3 = inputPoint3.getBotanaVars(inputPoint3);
			PVariable[] coords4 = inputPoint4.getBotanaVars(inputPoint4);
			botanaPolynomials = new PPolynomial[1][1];
			PPolynomial[][] matrix = new PPolynomial[4][4];

			matrix[0][0] = new PPolynomial(coords1[0]);
			matrix[0][1] = new PPolynomial(coords1[1]);
			matrix[0][2] = matrix[0][0].multiply(matrix[0][0])
					.add(matrix[0][1].multiply(matrix[0][1]));
			matrix[0][3] = new PPolynomial(BigInteger.ONE);

			matrix[1][0] = new PPolynomial(coords2[0]);
			matrix[1][1] = new PPolynomial(coords2[1]);
			matrix[1][2] = matrix[1][0].multiply(matrix[1][0])
					.add(matrix[1][1].multiply(matrix[1][1]));
			matrix[1][3] = new PPolynomial(BigInteger.ONE);

			matrix[2][0] = new PPolynomial(coords3[0]);
			matrix[2][1] = new PPolynomial(coords3[1]);
			matrix[2][2] = matrix[2][0].multiply(matrix[2][0])
					.add(matrix[2][1].multiply(matrix[2][1]));
			matrix[2][3] = new PPolynomial(BigInteger.ONE);

			matrix[3][0] = new PPolynomial(coords4[0]);
			matrix[3][1] = new PPolynomial(coords4[1]);
			matrix[3][2] = matrix[3][0].multiply(matrix[3][0])
					.add(matrix[3][1].multiply(matrix[3][1]));
			matrix[3][3] = new PPolynomial(BigInteger.ONE);

			botanaPolynomials[0][0] = PPolynomial.det4(matrix);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

}
