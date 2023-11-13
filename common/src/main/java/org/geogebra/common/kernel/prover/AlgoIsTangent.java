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

	private GeoLine inputElement1; // input
	private GeoConic inputElement2; // input

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
	public AlgoIsTangent(Construction cons, GeoLine inputElement1,
			GeoConic inputElement2) {
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
			GeoLine inputElement1, GeoConic inputElement2) {
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

		setOnlyOutput(outputBoolean);
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
		GeoLine l = inputElement1;
		GeoConic c = inputElement2;
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

		GeoLine l = inputElement1;
		GeoConic c = inputElement2;

        PVariable[] lv = l.getBotanaVars(l);
		PVariable[] cv = c.getBotanaVars(c);

        if (c.isCircle()) {
			// intersection of the perpendicular
			// from the center of the circle to the line (foot)
			PVariable[] foot = new PVariable[2];
			foot[0] = new PVariable(kernel);
			foot[1] = new PVariable(kernel);

			botanaPolynomials = new PPolynomial[1][3];
			botanaPolynomials[0][0] = PPolynomial.collinear(foot[0], foot[1], lv[0], lv[1], lv[2], lv[3]);
			botanaPolynomials[0][1] = PPolynomial.perpendicular(foot[0], foot[1], lv[0], lv[1], foot[0], foot[1],
					cv[0], cv[1]);
			botanaPolynomials[0][2] = PPolynomial.equidistant(foot[0], foot[1], cv[0], cv[1], cv[2], cv[3]);
            // FIXME: find some better equations, these yield "true on parts, false on parts"

			return botanaPolynomials;
		}

        if (c.isParabola()) {
            // mirroring the focus about the tangent should
            // be an element of the directrix,
            // creating the foot first and then the mirror image
            PVariable[] foot = new PVariable[2];
            foot[0] = new PVariable(kernel);
            foot[1] = new PVariable(kernel);
            PVariable[] mirror = new PVariable[2];
            mirror[0] = new PVariable(kernel);
            mirror[1] = new PVariable(kernel);

            botanaPolynomials = new PPolynomial[1][5];
            botanaPolynomials[0][0] = PPolynomial.collinear(foot[0], foot[1], lv[0], lv[1], lv[2], lv[3]);
            botanaPolynomials[0][1] = PPolynomial.perpendicular(foot[0], foot[1], lv[0], lv[1], foot[0], foot[1],
                    cv[8], cv[9]);
            botanaPolynomials[0][2] = new PPolynomial(foot[0]).multiply(new PPolynomial(2)).
                    subtract(new PPolynomial(mirror[0])).subtract(new PPolynomial(cv[8]));
            botanaPolynomials[0][3] = new PPolynomial(foot[1]).multiply(new PPolynomial(2)).
                    subtract(new PPolynomial(mirror[1])).subtract(new PPolynomial(cv[9]));
            botanaPolynomials[0][4] = PPolynomial.collinear(mirror[0], mirror[1], cv[4], cv[5], cv[6], cv[7]);
            // FIXME: find some better equations if possible, these result in three tangents

            return botanaPolynomials;
        }

		// TODO: implement all other cases

		throw new NoSymbolicParametersException();
    }
}
