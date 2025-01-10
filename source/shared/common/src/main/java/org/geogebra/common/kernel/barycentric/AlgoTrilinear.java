package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.MyMath;

/**
 * @author Darko Drakulic
 * @version 17-10-2011
 * 
 *          This class make point with given trilinear coordinates.
 * 
 */

public class AlgoTrilinear extends AlgoElement {

	private GeoPointND P1, P2, P3; // input
	private GeoNumberValue v1, v2, v3; // input
	private GeoPointND point; // output

	/**
	 * Creates new trilinear algo
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
	 * @param a
	 *            first trilinear coord
	 * @param b
	 *            second trilinear coord
	 * @param c
	 *            third trilinear coord
	 */
	public AlgoTrilinear(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue c) {
		super(cons);
		this.P1 = A;
		this.P2 = B;
		this.P3 = C;
		this.v1 = a;
		this.v2 = b;
		this.v3 = c;

		int dim = MyMath.max(A.getDimension(), B.getDimension(),
				C.getDimension());
		point = kernel.getGeoFactory().newPoint(dim, cons);
		setInputOutput();
		compute();
		point.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Trilinear;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[6];
		input[0] = P1.toGeoElement();
		input[1] = P2.toGeoElement();
		input[2] = P3.toGeoElement();
		input[3] = v1.toGeoElement();
		input[4] = v2.toGeoElement();
		input[5] = v3.toGeoElement();

		setOnlyOutput(point);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting point
	 */
	public GeoPointND getResult() {
		return point;
	}

	@Override
	public final void compute() {

		double p1 = P2.distance(P3);
		double p2 = P1.distance(P3);
		double p3 = P1.distance(P2);

		double wA = v1.getDouble() * p1, wB = v2.getDouble() * p2,
				wC = v3.getDouble() * p3;
		double sum = wA + wB + wC;
		GeoPoint.setBarycentric(P1, P2, P3, wA, wB, wC, sum, point);
	}

}