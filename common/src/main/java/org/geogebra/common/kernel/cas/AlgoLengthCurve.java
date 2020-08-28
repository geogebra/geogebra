package org.geogebra.common.kernel.cas;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.LengthCurve;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

/**
 * @author Victor Franco Espino
 * @version 19-04-2007
 * 
 *          Calculate Curve Length between the parameters t0 and t1: integral
 *          from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoLengthCurve extends AlgoUsingTempCASalgo {

	private GeoNumeric t0; // input
	private GeoNumeric t1; // input
	private GeoCurveCartesianND c; // c1 is c'(x)

	private GeoNumeric length; // output
	private UnivariateFunction lengthCurve; // is T = sqrt(a'(t)^2+b'(t)^2)

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param c
	 *            curve
	 * @param t0
	 *            start parameter
	 * @param t1
	 *            end parameter
	 */
	public AlgoLengthCurve(Construction cons, String label,
			GeoCurveCartesianND c, GeoNumeric t0, GeoNumeric t1) {
		super(cons);
		this.t0 = t0;
		this.t1 = t1;
		this.c = c;
		length = new GeoNumeric(cons);

		refreshCASResults();

		setInputOutput();
		compute();
		length.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = c;
		input[1] = t0;
		input[2] = t1;

		setOutputLength(1);
		setOutput(0, length);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting length
	 */
	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		double a = t0.getValue();
		double b = t1.getValue();

		double lenVal = Math.abs(
				AlgoIntegralDefinite.numericIntegration(lengthCurve, a, b));
		length.setValue(lenVal);
	}

	@Override
	public void refreshCASResults() {
		// First derivative of curve f
		// use fast non-CAS version!
		algoCAS = new AlgoDerivative(cons, c, null, null, true,
				new EvalInfo(false));
		GeoCurveCartesianND c1 = (GeoCurveCartesianND) ((AlgoDerivative) algoCAS)
				.getResult();
		cons.removeFromConstructionList(algoCAS);
		lengthCurve = new LengthCurve(c1);
	}
}
