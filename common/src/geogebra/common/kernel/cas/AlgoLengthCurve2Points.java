package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.roots.RealRootFunction;

/**
 * @author Victor Franco Espino
 * @version 19-04-2007
 * 
 *          Calculate Curve Length between the points A and B: integral from t0
 *          to t1 on T = sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoLengthCurve2Points extends AlgoUsingTempCASalgo {

	private GeoPoint A, B; // input
	private GeoCurveCartesian c;
	private GeoCurveCartesian derivative;
	private GeoNumeric length; // output
	private RealRootFunction lengthCurve; // is T = sqrt(a'(t)^2+b'(t)^2)

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param c curve
	 * @param A start point
	 * @param B end point
	 */
	public AlgoLengthCurve2Points(Construction cons, String label,
			GeoCurveCartesian c, GeoPoint A, GeoPoint B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.c = c;
		length = new GeoNumeric(cons);

		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, c);
		derivative = (GeoCurveCartesian) ((AlgoDerivative) algoCAS).getResult();
		cons.removeFromConstructionList(algoCAS);

		lengthCurve = new LengthCurve(derivative);

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
		input[1] = A;
		input[2] = B;

		setOutputLength(1);
		setOutput(0, length);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting legth
	 */
	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		if (!derivative.isDefined()) {
			length.setUndefined();
			return;
		}

		double a = c.getClosestParameter(A, c.getMinParameter());
		double b = c.getClosestParameter(B, c.getMinParameter());
		double lenVal = Math.abs(AlgoIntegralDefinite.numericIntegration(
				lengthCurve, a, b));
		length.setValue(lenVal);
	}

	//locus equability makes no sense here
}
