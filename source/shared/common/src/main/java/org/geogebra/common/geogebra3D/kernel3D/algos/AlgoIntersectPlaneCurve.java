package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoIntersectCoordSysCurve;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Computes intersection between plane and curve.
 */
public class AlgoIntersectPlaneCurve extends AlgoIntersectCoordSysCurve {
	private final GeoCoordSys2D plane;

	/**
	 * @param cons
	 *            construction
	 * @param plane
	 *            plane
	 * @param c
	 *            curve
	 * @param labels
	 *            output labels
	 */
	public AlgoIntersectPlaneCurve(Construction cons, GeoCoordSys2D plane,
			GeoCurveCartesianND c, String[] labels) {
		super(cons);

		outputPoints = createOutputPoints(true);

		this.plane = plane;
		this.curve = c;

		compute();

		setInputOutput(); // for AlgoElement

		outputPoints.setLabelsMulti(labels);

		update();
	}

	@Override
	public void compute() {

		Coords coeffs = plane.getCoordSys().getEquationVector();

		FunctionVariable fv = curve.getFun(0).getFunctionVariable();

		// substitute x = x(t), y=y(t), z=z(t) into
		// ax + by + cz = d
		ExpressionNode enx;

		if (DoubleUtil.isZero(coeffs.getW())) {
			enx = new ExpressionNode(kernel, 0);
			for (int i = 0; i < curve.getDimension(); i++) {
				enx = enx.plus(curve.getFun(i).getExpression().multiply(coeffs.get(i + 1)));
			}

		} else {
			// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
			enx = new ExpressionNode(kernel, 1);
			for (int i = 0; i < curve.getDimension(); i++) {
				enx = enx.plus(curve.getFun(i).getExpression()
						.multiply(coeffs.get(i + 1) / coeffs.getW()));
			}

		}

		findIntersections(enx, fv);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = plane.toGeoElement();
		input[1] = curve;

		setDependencies();
	}

}
