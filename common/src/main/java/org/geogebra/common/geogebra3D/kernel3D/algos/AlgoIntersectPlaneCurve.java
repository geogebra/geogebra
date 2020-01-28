package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoIntersectCoordSysCurve;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Computes intersection between plane and curve.
 */
public class AlgoIntersectPlaneCurve extends AlgoIntersectCoordSysCurve {
	protected OutputHandler<GeoElement> outputPoints;
	private GeoCoordSys2D plane;

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

		outputPoints = createOutputPoints();

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
				enx = enx.plus(new ExpressionNode(kernel,
						new MyDouble(kernel, coeffs.get(i + 1)),
						Operation.MULTIPLY, curve.getFun(i).getExpression()));
			}

		} else {
			// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
			enx = new ExpressionNode(kernel, 1);
			for (int i = 0; i < curve.getDimension(); i++) {
				enx = enx.plus(new ExpressionNode(kernel,
						new MyDouble(kernel, coeffs.get(i + 1) / coeffs.getW()),
						Operation.MULTIPLY, curve.getFun(i).getExpression()));
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

	/**
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoElement> createOutputPoints() {
		return new OutputHandler<>(new ElementFactory<GeoElement>() {
			@Override
			public GeoPoint3D newElement() {
				GeoPoint3D p = new GeoPoint3D(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectPlaneCurve.this);
				return p;
			}
		});
	}

	@Override
	protected OutputHandler<GeoElement> getOutputPoints() {
		return outputPoints;
	}

	@Override
	protected void updatePoint(GeoPointND point, double param,
			FunctionVariable fv) {
		ExpressionNode xFun = curve.getFun(0).getExpression();
		ExpressionNode yFun = curve.getFun(1).getExpression();
		double z = 0;
		fv.set(param);
		if (curve.getDimension() > 2) {
			z = curve.getFun(2).getExpression().evaluateDouble();
		}
		point.setCoords(xFun.evaluateDouble(), yFun.evaluateDouble(), z, 1.0);

	}

	@Override
	protected boolean inCoordSys(GeoPointND point) {
		return true;
	}

}
