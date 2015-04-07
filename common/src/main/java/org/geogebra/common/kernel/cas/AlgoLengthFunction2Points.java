package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.roots.RealRootFunction;

/**
 * @author Victor Franco Espino
 * @version 19-04-2007
 * 
 *          Calculate Function Length between the points A and B: integral from
 *          A to B on T = sqrt(1+(f')^2)
 */

public class AlgoLengthFunction2Points extends AlgoUsingTempCASalgo {

	private GeoPoint A, B; // input
	private GeoFunction f;// f1 is f'(x)
	private GeoNumeric length; // output
	private RealRootFunction lengthFunction; // is T = sqrt(1+(f')^2)

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param A
	 *            start point
	 * @param B
	 *            end point
	 */
	public AlgoLengthFunction2Points(Construction cons, String label,
			GeoFunction f, GeoPoint A, GeoPoint B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.f = f;
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
		double a = A.inhomX;
		double b = B.inhomX;

		double lenVal = Math.abs(AlgoIntegralDefinite.numericIntegration(
				lengthFunction, a, b));
		length.setValue(lenVal);
	}

	// locusequability makes no sense here

	@Override
	public void refreshCASResults() {
		// First derivative of function f
		algoCAS = new AlgoDerivative(cons, f);
		cons.removeFromConstructionList(algoCAS);
		GeoFunction f1 = (GeoFunction) ((AlgoDerivative) algoCAS).getResult();
		lengthFunction = new LengthFunction(f1);
	}
}