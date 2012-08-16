package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.roots.RealRootFunction;

/**
 * @author Victor Franco Espino
 * @version 19-04-2007
 * 
 *          Calculate Function Length between the numbers A and B: integral from
 *          A to B on T = sqrt(1+(f')^2)
 */

public class AlgoLengthFunction extends AlgoUsingTempCASalgo {

	private GeoNumeric A, B; // input
	private GeoFunction f; // f1 is f'(x)
	GeoFunction f1;
	private GeoNumeric length; // output
	private RealRootFunction lengthFunction; // is T = sqrt(1+(f')^2)

	public AlgoLengthFunction(Construction cons, String label, GeoFunction f,
			GeoNumeric A, GeoNumeric B) {
		this(cons, f, A, B);
		length.setLabel(label);
	}

	public AlgoLengthFunction(Construction cons, GeoFunction f, GeoNumeric A,
			GeoNumeric B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.f = f;
		length = new GeoNumeric(cons);

		// First derivative of function f
		algoCAS = new AlgoDerivative(cons, f);
		this.f1 = (GeoFunction) ((AlgoDerivative) algoCAS).getResult();

		// Integral of length function
		lengthFunction = new LengthFunction();
		cons.removeFromConstructionList(algoCAS);

		setInputOutput();
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoLengthFunction;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f;
		input[1] = A;
		input[2] = B;

		setOutputLength(1);
		setOutput(0, length);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		double a = A.getValue();
		double b = B.getValue();

		double lenVal = Math.abs(AlgoIntegralDefinite.numericIntegration(
				lengthFunction, a, b));
		length.setValue(lenVal);
	}

	/**
	 * T = sqrt( 1 + f'(x)^2)
	 */
	private class LengthFunction implements RealRootFunction {
		public LengthFunction() {
			// TODO Auto-generated constructor stub
		}

		public double evaluate(double t) {
			double p = f1.evaluate(t);
			return Math.sqrt(1 + p * p);
		}
	}

	// TODO Consider locusequability
}