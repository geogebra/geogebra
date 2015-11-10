package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.AlgoCubicSwitchParams;
import org.geogebra.common.main.App;

/**
 * @author Darko Drakulic
 * @version 23-10-2011
 * 
 *          This class makes a curve in barycentric coordinates
 * 
 */

public class AlgoCubic extends AlgoElement {

	private GeoPoint A, B, C; // input
	private NumberValue n; // number of curve
	private GeoImplicit poly; // output

	/**
	 * Creates new triangle cubic algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 * @param e
	 *            index in CTC
	 */
	public AlgoCubic(Construction cons, String label, GeoPoint A, GeoPoint B,
			GeoPoint C, NumberValue e) {
		super(cons);
		kernel.getApplication().getAlgoCubicSwitch();
		this.A = A;
		this.B = B;
		this.C = C;
		this.n = e;
		poly = new GeoImplicitPoly(cons);
		setInputOutput();
		compute();
		poly.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Cubic;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = A;
		input[1] = B;
		input[2] = C;
		input[3] = n.toGeoElement();

		setOutputLength(1);
		setOutput(0, poly.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting curve
	 * 
	 * @return the resulting curve
	 */
	public GeoImplicit getResult() {
		return poly;
	}

	@Override
	public final void compute() {

		if (kernel.getApplication().getAlgoCubicSwitch() == null) {
			poly.setUndefined();
			return;
		}

		// Check if the points are aligned
		double c = A.distance(B);
		double b = C.distance(A);
		double a = B.distance(C);
		double x1 = A.inhomX;
		double y1 = A.inhomY;
		double x2 = B.inhomX;
		double y2 = B.inhomY;
		double x3 = C.inhomX;
		double y3 = C.inhomY;

		String equation = "";

		double det = (-x2 + x3) * (y1 - y3) + (x1 - x3) * (y2 - y3);
		if (Kernel.isZero(det)) {
			poly.setUndefined();
			return;
		}
		String Astr = "(" + (x3 - x2) / det + "*y  + " + (y2 - y3) / det
				+ "*x - " + ((x3 - x2) * y3 + (y2 - y3) * x3) / det + ")";
		String Bstr = "(" + (x1 - x3) / det + "*y  + " + (y3 - y1) / det
				+ "*x - " + ((x1 - x3) * y1 + (y3 - y1) * x1) / det + ")";
		String Cstr = "(" + (x2 - x1) / det + "*y  + " + (y1 - y2) / det
				+ "*x - " + ((x2 - x1) * y2 + (y1 - y2) * x2) / det + ")";

		equation = kernel.getApplication().cubicSwitch(
				new AlgoCubicSwitchParams(n.getDouble(), a, b, c));

		if (equation == null) {
			poly.setUndefined();
			return;
		}

		equation = equation.replace("A", Astr);
		equation = equation.replace("B", Bstr);
		equation = equation.replace("C", Cstr);
		equation = equation.replace("a", "" + a);
		equation = equation.replace("b", "" + b);
		equation = equation.replace("c", "" + c);

		Parser parser = getKernel().getParser();
		AlgebraProcessor algebraProcessor = getKernel().getAlgebraProcessor();

		ValidExpression ve = null;
		try {
			ve = parser.parseGeoGebraExpression(equation);
			GeoImplicit result = (GeoImplicit) (algebraProcessor
					.processEquation((Equation) ve, true)[0]);
			result.remove();
			poly.setCoeff(result.getCoeff());
			poly.setDefined();
		} catch (ParseException e) {
			poly.setUndefined();
			App.error(equation);
		}
	}

	// TODO Consider locusequability
}
