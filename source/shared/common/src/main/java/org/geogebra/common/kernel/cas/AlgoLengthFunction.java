package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 *  Calculate Function Length between the numbers A and B: integral from
 *  A to B on T = sqrt(1+(f')^2)
 * @author Victor Franco Espino
 */

public class AlgoLengthFunction extends AlgoUsingTempCASalgo {

	private GeoNumeric A;
	private GeoNumeric B; // input
	private GeoFunction f; // f1 is f'(x)
	private GeoNumeric length; // output
	private LengthFunction lengthFunction; // is T = sqrt(1+(f')^2)

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param A
	 *            start parameter
	 * @param B
	 *            end parameter
	 */
	public AlgoLengthFunction(Construction cons, String label, GeoFunction f,
			GeoNumeric A, GeoNumeric B) {
		this(cons, f, A, B);
		length.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param A
	 *            start parameter
	 * @param B
	 *            end parameter
	 */
	public AlgoLengthFunction(Construction cons, GeoFunction f, GeoNumeric A,
			GeoNumeric B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.f = f;
		length = new GeoNumeric(cons);

		refreshCASResults();

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

		setOnlyOutput(length);
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

		length.setValue(lengthFunction.integral(a, b));
	}

	@Override
	public void refreshCASResults() {
		// First derivative of function f
		// use fast non-CAS version!
		algoCAS = new AlgoDerivative(cons, f, null, null, true,
				new EvalInfo(false));
		GeoFunction f1 = (GeoFunction) ((AlgoDerivative) algoCAS).getResult();

		// Integral of length function
		lengthFunction = new LengthFunction(f1, f);
		cons.removeFromConstructionList(algoCAS);
	}
}