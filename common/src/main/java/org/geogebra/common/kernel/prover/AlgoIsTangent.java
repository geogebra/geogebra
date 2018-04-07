package org.geogebra.common.kernel.prover;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;


/**
 * Decides if the first object is tangent to the second one. Can be embedded into the Prove command to
 * work symbolically.
 * 
 * @author Zoltan Kovacs
 */
public class AlgoIsTangent extends AlgoElement
		implements SymbolicParametersBotanaAlgoAre {

	private GeoElement inputElement1; // input
	private GeoElement inputElement2; // input

	private GeoBoolean outputBoolean; // output
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
	public AlgoIsTangent(Construction cons, GeoElement inputElement1,
                         GeoElement inputElement2) {
		super(cons);
		this.inputElement1 = inputElement1;
		this.inputElement2 = inputElement2;

		outputBoolean = new GeoBoolean(cons);

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
						 GeoElement inputElement1, GeoElement inputElement2) {
		this(cons, inputElement1, inputElement2);
		outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.IsTangent;
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
	 * 
	 * @return true if the first object is tangent to the second one
	 */

	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		GeoLine l = (GeoLine) inputElement1;
		GeoConic c = (GeoConic) inputElement2;
		Boolean value = null;
		if (l.isDefinedTangent(c)) {
			value = true;
		} else {
			// intersect line and conic (code copied from RelationNumerical)
			GeoPoint[] points = { new GeoPoint(cons), new GeoPoint(cons) };
			int type = AlgoIntersectLineConic.intersectLineConic(l, c, points,
					Kernel.STANDARD_PRECISION);
			value = type == AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE;
		}
		outputBoolean.setValue(value);
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
        if (botanaPolynomials != null) {
            return botanaPolynomials;
        }

        GeoLine l = (GeoLine) inputElement1;
        GeoConic c = (GeoConic) inputElement2;

        PVariable[] lv = l.getBotanaVars(l);
		PVariable[] cv = c.getBotanaVars(c);

        if (c.isCircle()) {
			// intersection of the perpendicular
			// from the center of the circle to the line (feet)
			PVariable[] feet = new PVariable[2];
			feet[0] = new PVariable(kernel);
			feet[1] = new PVariable(kernel);

			botanaPolynomials = new PPolynomial[1][3];
			botanaPolynomials[0][0] = PPolynomial.collinear(feet[0], feet[1], lv[0], lv[1], lv[2], lv[3]);
			botanaPolynomials[0][1] = PPolynomial.perpendicular(feet[0], feet[1], lv[0], lv[1], feet[0], feet[1],
					cv[0], cv[1]);
			botanaPolynomials[0][2] = PPolynomial.equidistant(feet[0], feet[1], cv[0], cv[1], cv[2], cv[3]);

			return botanaPolynomials;
		}

		// TODO: implement all other cases

		throw new NoSymbolicParametersException();
    }
}
