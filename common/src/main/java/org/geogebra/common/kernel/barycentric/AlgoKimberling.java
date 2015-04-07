package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.AlgoKimberlingWeightsParams;

/**
 * @author Zbynek Konecny, credit goes to Jason Cantarella of the Univerity of
 *         Georgia for creating a perl script which was used to create this
 *         class.
 * @version 30-09-2011
 * 
 *          This class calculates n-th Kimberling center of a triangle.
 * 
 */

public class AlgoKimberling extends AlgoElement {

	private GeoPoint A, B, C; // input
	private GeoPoint M; // output
	private NumberValue n;

	/**
	 * Creates new algo for triangle center
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
	 * @param n
	 *            index in ETC
	 */
	public AlgoKimberling(Construction cons, String label, GeoPoint A,
			GeoPoint B, GeoPoint C, NumberValue n) {
		super(cons);
		kernel.getApplication().getAlgoKimberlingWeights();
		this.A = A;
		this.B = B;
		this.C = C;
		this.n = n;
		M = new GeoPoint(cons);
		setInputOutput();
		compute();
		M.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.TriangleCenter;
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
		setOutput(0, M);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting point
	 */
	public GeoPoint getResult() {
		return M;
	}

	@Override
	public final void compute() {

		// Check if the points are aligned
		double c = A.distance(B);
		double b = C.distance(A);
		double a = B.distance(C);
		double m = Math.min(Math.min(a, b), c);
		a = a / m;
		b = b / m;
		c = c / m;
		int k = (int) n.getDouble();

		if (kernel.getApplication().getAlgoKimberlingWeights() == null) {
			M.setUndefined();
		} else {
			double wA = kernel.getApplication().kimberlingWeight(
					new AlgoKimberlingWeightsParams(k, a, b, c));
			double wB = kernel.getApplication().kimberlingWeight(
					new AlgoKimberlingWeightsParams(k, b, c, a));
			double wC = kernel.getApplication().kimberlingWeight(
					new AlgoKimberlingWeightsParams(k, c, a, b));
			double w = wA + wB + wC;
			if (Double.isNaN(w) || Kernel.isZero(w))
				M.setUndefined();
			else
				M.setCoords((A.x / A.z * wA + B.x / B.z * wB + C.x / C.z * wC)
						/ w, (A.y / A.z * wA + B.y / B.z * wB + C.y / C.z * wC)
						/ w, 1);
		}
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		// In case this is considered, I would very much like to
		// use that perl script
		return false;
	}
}
