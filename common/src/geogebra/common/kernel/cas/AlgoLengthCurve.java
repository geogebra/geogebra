package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.roots.RealRootFunction;

/**
 * @author Victor Franco Espino
 * @version 19-04-2007
 * 
 *          Calculate Curve Length between the parameters t0 and t1: integral
 *          from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoLengthCurve extends AlgoUsingTempCASalgo {

	private GeoNumeric t0, t1; // input
	private GeoCurveCartesian c; // c1 is c'(x)
	
	private GeoNumeric length; // output
	private RealRootFunction lengthCurve; // is T = sqrt(a'(t)^2+b'(t)^2)

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param c curve
	 * @param t0 start parameter
	 * @param t1 end parameter
	 */
	public AlgoLengthCurve(Construction cons, String label,
			GeoCurveCartesian c, GeoNumeric t0, GeoNumeric t1) {
		super(cons);
		this.t0 = t0;
		this.t1 = t1;
		this.c = c;
		length = new GeoNumeric(cons);

		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, c);
		GeoCurveCartesian c1 = (GeoCurveCartesian) ((AlgoDerivative) algoCAS).getResult();
		cons.removeFromConstructionList(algoCAS);

		lengthCurve = new LengthCurve(c1);

		setInputOutput();
		compute();
		length.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoLengthCurve;
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

		double lenVal = Math.abs(AlgoIntegralDefinite.numericIntegration(
				lengthCurve, a, b));
		length.setValue(lenVal);
	}

	// TODO Consider locusequability
}
