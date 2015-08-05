package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.algos.TangentAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;

/**
 * A tangent through point on implicit curve This algorithm works only if the
 * curve differentiable
 * 
 * @author GSoCImplicitCurve2015
 *
 */
public class AlgoTangentImplicitCurve extends AlgoElement implements
		TangentAlgo {
	/**
	 * Maximum number of line in output
	 */
	public static final int OUTPUT_SIZE = 10;
	/**
	 * true: Point is created by Point on Path
	 */
	private boolean pointOnPath;

	/**
	 * Input implicit curve
	 */
	private GeoImplicitCurve inputCurve;

	/**
	 * Input point
	 */
	private GeoPointND point;

	/**
	 * Output tangent lines
	 */
	private OutputHandler<GeoLine> tangents;

	/**
	 * labels
	 */
	private String[] labels;

	/**
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param labels
	 *            Labels
	 * @param point
	 *            Point on the path
	 * @param curve
	 *            {@link GeoImplicitCurve}
	 */
	public AlgoTangentImplicitCurve(Construction cons, String[] labels,
			GeoPointND point, GeoImplicitCurve curve) {
		super(cons);
		this.labels = labels;
		this.point = point;
		this.inputCurve = curve;

		if (point.getParentAlgorithm() != null
				&& point.getParentAlgorithm() instanceof AlgoPointOnPath) {
			AlgoPointOnPath alg = (AlgoPointOnPath) point.getParentAlgorithm();
			pointOnPath = alg.getPath() == curve;
		}

		setInputOutput();
		compute();
	}

	@Override
	public GeoPointND getTangentPoint(GeoElement geo, GeoLine line) {
		return point;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputCurve;
		input[1] = (GeoElement) point;

		tangents = new OutputHandler<GeoLine>(new elementFactory<GeoLine>() {
			public GeoLine newElement() {
				GeoLine line = new GeoLine(cons);
				line.setParentAlgorithm(AlgoTangentImplicitCurve.this);
				return line;
			}
		});

		tangents.setLabels(labels);
		setDependencies();
	}

	@Override
	public void compute() {

		if (!inputCurve.hasDerivative()) {
			tangents.adjustOutputSize(0);
			return;
		}

		if (!pointOnPath) {
			computeTangentFromPoint();
			return;
		}

		FunctionNVar func = inputCurve.getDerivativeXY();
		double x = point.getInhomX();
		double y = point.getInhomY();
		double m = func.evaluate(new double[] { x, y });
		if (Double.isNaN(m)) {
			tangents.adjustOutputSize(0);
			return;
		}
		tangents.adjustOutputSize(1);
		tangents.getElement(0).setCoords(-m, 1.0, m * x - y);
	}


	private void computeTangentFromPoint() {
		// Find the intersection between curve and following equation:
		// f'(x, y) = (y - y1) / (x - x1), where (x1, y1): given point
		// In other words find all the points on curve where slope of the curve
		// is same as that line joining this point and given point

		FunctionNVar f1 = inputCurve.getExpression();

		FunctionVariable x = f1.getFunctionVariables()[0];
		FunctionVariable y = f1.getFunctionVariables()[1];

		// build expression
		ExpressionNode x1 = new ExpressionNode(kernel, x, Operation.MINUS,
				new MyDouble(kernel, point.getInhomX()));
		ExpressionNode y1 = new ExpressionNode(kernel, y, Operation.MINUS,
				new MyDouble(kernel, point.getInhomY()));

		x1 = x1.multiply(inputCurve.getDerivativeX().getExpression());
		y1 = y1.multiply(inputCurve.getDerivativeY().getExpression());

		FunctionNVar f2 = new FunctionNVar(x1.plus(y1), new FunctionVariable[] {
				x, y });

		double[] params = kernel.getViewBoundsForGeo(inputCurve);

		// find roots
		double[][] roots = AlgoIntersectImplicitCurve.findIntersections(f1, f2,
				params[0], params[2], params[1], params[3],
				AlgoIntersectImplicitCurve.SAMPLE_SIZE_2D, OUTPUT_SIZE);

		if (roots == null || roots.length == 0) {
			tangents.adjustOutputSize(0);
			return;
		}

		// adjust output size and add lines
		int n = Math.min(roots.length, 10);
		tangents.adjustOutputSize(n);
		double px = point.getInhomX(), dx;
		double py = point.getInhomY(), dy;

		for (int i = 0; i < n; i++) {
			dx = px - roots[i][0];
			dy = py - roots[i][1];
			tangents.getElement(i).setCoords(dy, -dx, dx * py - dy * px);
		}
	}

	/**
	 * set Labels to tangents
	 * 
	 * @param labels
	 *            Labels
	 */
	public void setLabels(String[] labels) {
		tangents.setLabels(labels);
		update();
	}

	/**
	 * @return all tangents
	 */
	public GeoLine[] getTangents() {
		return tangents.getOutput(new GeoLine[tangents.size()]);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Tangent;
	}

}
