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
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * Decides if the points are collinear. Can be embedded into the Prove command
 * to work symbolically.
 * 
 * @author Simon Weitzhofer 18th April 2012
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoAreCollinear extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoPointND inputPoint1, inputPoint2, inputPoint3; // input

	private GeoBoolean outputBoolean; // output
	private Polynomial[] polynomials;
	private Polynomial[][] botanaPolynomials;

	/**
	 * Creates a new AlgoAreCollinear function
	 * 
	 * @param cons
	 *            the Construction
	 * @param inputPoint1
	 *            the first point
	 * @param inputPoint2
	 *            the second point
	 * @param inputPoint3
	 *            the third point
	 */
	public AlgoAreCollinear(final Construction cons,
			final GeoPointND inputPoint1, final GeoPointND inputPoint2,
			final GeoPointND inputPoint3) {
		super(cons);
		this.inputPoint1 = inputPoint1;
		this.inputPoint2 = inputPoint2;
		this.inputPoint3 = inputPoint3;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}



	@Override
	public Commands getClassName() {
		return Commands.AreCollinear;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = inputPoint1.toGeoElement();
		input[1] = inputPoint2.toGeoElement();
		input[2] = inputPoint3.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the result of the test
	 * 
	 * @return true if the three points lie on one line, false otherwise
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(GeoPoint.collinearND(inputPoint1, inputPoint2,
				inputPoint3));
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (getInputPoint1() != null && getInputPoint2() != null && getInputPoint3() != null) {
			getInputPoint1().getFreeVariables(variables);
			getInputPoint2().getFreeVariables(variables);
			getInputPoint3().getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	public int[] getDegrees() throws NoSymbolicParametersException {
		if (getInputPoint1() != null && getInputPoint2() != null && getInputPoint3() != null) {
			int[] degree1 = getInputPoint1().getDegrees();
			int[] degree2 = getInputPoint2().getDegrees();
			int[] degree3 = getInputPoint3().getDegrees();
			int[] result = new int[1];
			result[0] = Math.max(degree1[0] + degree2[1] + degree3[2], Math
					.max(degree2[0] + degree3[1] + degree1[2], Math.max(
							degree3[0] + degree1[1] + degree2[2],
							Math.max(
									degree3[0] + degree2[1] + degree1[2],
									Math.max(degree2[0] + degree1[1]
											+ degree3[2], degree1[0]
											+ degree3[1] + degree2[2])))));
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			final HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (getInputPoint1() != null && getInputPoint2() != null && getInputPoint3() != null) {
			BigInteger[] coords1 = getInputPoint1().getExactCoordinates(values);
			BigInteger[] coords2 = getInputPoint2().getExactCoordinates(values);
			BigInteger[] coords3 = getInputPoint3().getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			coords[0] = coords1[0]
					.multiply(coords2[1])
					.multiply(coords3[2])
					.add(coords2[0].multiply(coords3[1]).multiply(coords1[2]))
					.add(coords3[0].multiply(coords1[1]).multiply(coords2[2]))
					.subtract(

							coords3[0]
									.multiply(coords2[1])
									.multiply(coords1[2])
									.add(coords2[0].multiply(coords1[1])
											.multiply(coords3[2]))
									.add(coords1[0].multiply(coords3[1])
											.multiply(coords2[2])));
			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}

		if (getInputPoint1() != null && getInputPoint2() != null && getInputPoint3() != null) {
			Polynomial[] coords1 = getInputPoint1().getPolynomials();
			Polynomial[] coords2 = getInputPoint2().getPolynomials();
			Polynomial[] coords3 = getInputPoint3().getPolynomials();
			polynomials = new Polynomial[1];
			polynomials[0] = coords1[0]
					.multiply(coords2[1])
					.multiply(coords3[2])
					.add(coords2[0].multiply(coords3[1]).multiply(coords1[2]))
					.add(coords3[0].multiply(coords1[1]).multiply(coords2[2]))
					.subtract(

							coords3[0]
									.multiply(coords2[1])
									.multiply(coords1[2])
									.add(coords2[0].multiply(coords1[1])
											.multiply(coords3[2]))
									.add(coords1[0].multiply(coords3[1])
											.multiply(coords2[2])));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (getInputPoint1() != null && getInputPoint2() != null && getInputPoint3() != null) {

			Variable[] fv1 = new Variable[2];
			Variable[] fv2 = new Variable[2];
			Variable[] fv3 = new Variable[2];
			fv1 = getInputPoint1().getBotanaVars(getInputPoint1());
			fv2 = getInputPoint2().getBotanaVars(getInputPoint2());
			fv3 = getInputPoint3().getBotanaVars(getInputPoint3());

			botanaPolynomials = new Polynomial[1][1];
			botanaPolynomials[0][0] = Polynomial.collinear(fv1[0], fv1[1],
					fv2[0], fv2[1], fv3[0], fv3[1]);
			return botanaPolynomials;

		}
		throw new NoSymbolicParametersException();
	}

	private GeoPoint getInputPoint1() {
		return (GeoPoint) inputPoint1;
	}

	private GeoPoint getInputPoint2() {
		return (GeoPoint) inputPoint2;
	}

	private GeoPoint getInputPoint3() {
		return (GeoPoint) inputPoint3;
	}

	// TODO Consider locusequability

}
