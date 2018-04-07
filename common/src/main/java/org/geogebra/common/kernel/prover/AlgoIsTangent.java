package org.geogebra.common.kernel.prover;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;

/**
 * Decides if the first object is tangent to the second one. Can be embedded into the Prove command to
 * work symbolically.
 * 
 * @author Zoltan Kovacs
 */
public class AlgoIsTangent extends org.geogebra.common.kernel.algos.AlgoElement
		implements org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre {

	private org.geogebra.common.kernel.geos.GeoElement inputElement1; // input
	private org.geogebra.common.kernel.geos.GeoElement inputElement2; // input

	private org.geogebra.common.kernel.geos.GeoBoolean outputBoolean; // output
	private PPolynomial[][] botanaPolynomials;

	/**
	 * Tests if the first object is tangent to the second one
	 *
	 * @param cons
	 *            The construction the objects depend on
	 * @param inputElement1
	 *            the first object
	 * @param inputElement2
	 *            the second object
	 */
	public AlgoIsTangent(Construction cons, org.geogebra.common.kernel.geos.GeoElement inputElement1,
                         org.geogebra.common.kernel.geos.GeoElement inputElement2) {
		super(cons);
		this.inputElement1 = inputElement1;
		this.inputElement2 = inputElement2;

		outputBoolean = new org.geogebra.common.kernel.geos.GeoBoolean(cons);

		setInputOutput();
		compute();

	}

	/**
	 * Tests if the first object is tangent to the second one
	 *
	 * @param cons
	 *            The construction the objects depend on
	 * @param label
	 *            the label for the AlgoIsTangent object
	 * @param inputElement1
	 *            the first object
	 * @param inputElement2
	 *            the second object
	 */
	public AlgoIsTangent(Construction cons, String label,
						 org.geogebra.common.kernel.geos.GeoElement inputElement1, org.geogebra.common.kernel.geos.GeoElement inputElement2) {
		this(cons, inputElement1, inputElement2);
		outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.IsTangent;
	}

	@Override
	protected void setInputOutput() {
		input = new org.geogebra.common.kernel.geos.GeoElement[2];
		input[0] = inputElement1;
		input[1] = inputElement2;

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Gets the result of the test
	 * 
	 * @return true if the first object is tangent to the second one
	 */

	public org.geogebra.common.kernel.geos.GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		org.geogebra.common.kernel.geos.GeoLine l = (org.geogebra.common.kernel.geos.GeoLine) inputElement1;
		org.geogebra.common.kernel.geos.GeoConic c = (org.geogebra.common.kernel.geos.GeoConic) inputElement2;
		Boolean value = null;
		if (l.isDefinedTangent(c)) {
			value = true;
		} else {
			// intersect line and conic (code copied from RelationNumerical)
			org.geogebra.common.kernel.geos.GeoPoint[] points = { new org.geogebra.common.kernel.geos.GeoPoint(cons), new org.geogebra.common.kernel.geos.GeoPoint(cons) };
			int type = org.geogebra.common.kernel.algos.AlgoIntersectLineConic.intersectLineConic(l, c, points,
					Kernel.STANDARD_PRECISION);
			value = type == org.geogebra.common.kernel.algos.AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE;
		}
		outputBoolean.setValue(value);
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		throw new NoSymbolicParametersException();
		// TODO: Implement this.
	}
}
