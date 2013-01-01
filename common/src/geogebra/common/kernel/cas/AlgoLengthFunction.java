package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
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
	private GeoNumeric length; // output
	private RealRootFunction lengthFunction; // is T = sqrt(1+(f')^2)
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param f function
	 * @param A start parameter
	 * @param B end parameter
	 */
	public AlgoLengthFunction(Construction cons, String label, GeoFunction f,
			GeoNumeric A, GeoNumeric B) {
		this(cons, f, A, B);
		length.setLabel(label);
	}
	/**
	 * @param cons construction
	 * @param f function
	 * @param A start parameter
	 * @param B end parameter
	 */
	public AlgoLengthFunction(Construction cons, GeoFunction f, GeoNumeric A,
			GeoNumeric B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.f = f;
		length = new GeoNumeric(cons);

		// First derivative of function f
		algoCAS = new AlgoDerivative(cons, f);
		GeoFunction f1 = (GeoFunction) ((AlgoDerivative) algoCAS).getResult();

		// Integral of length function
		lengthFunction = new LengthFunction(f1);
		cons.removeFromConstructionList(algoCAS);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
        return Commands.Length;
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

	/**
	 * @return resulting length
	 */
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

	// TODO Consider locusequability
}