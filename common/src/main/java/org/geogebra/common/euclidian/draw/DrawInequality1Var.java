package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author Zbynek
 * 
 */
public class DrawInequality1Var extends SetDrawable {

	/** ratio of dot radius and line thickness */
	public static final double DOT_RADIUS = 1;
	private Inequality ineq;
	private GeneralPathClipped[] gp;
	private GLine2D[] lines;
	private GEllipse2DDouble[] circle;
	private boolean varIsY;

	private double maxBound = 1000000;
	private double minBound = -1000000;

	/**
	 * Creates new drawable inequality
	 * 
	 * @param view
	 *            view
	 * @param geo
	 *            the top-level function (e.g. x &gt; 1 &amp;&amp; x &lt; 3)
	 * @param ineq
	 *            inequality
	 * @param varIsY
	 *            true if this is inequality in Y
	 */
	public DrawInequality1Var(Inequality ineq, EuclidianView view,
			GeoElement geo, boolean varIsY) {
		super();
		this.ineq = ineq;
		this.geo = geo;
		this.view = view;
		this.varIsY = varIsY;
		setBoundary(this.ineq);
	}

	// set min or max boundary for inequality
	private void setBoundary(Inequality ineq2) {
		if (ineq2.getOperation().equals(Operation.GREATER)
				|| ineq2.getOperation().equals(Operation.GREATER_EQUAL)) {
			ExpressionNode expr = ineq2.getNormalExpression();
			if (expr.getOperation().equals(Operation.MINUS)) {
				if (expr.getLeft() instanceof FunctionVariable
						&& isNumber(expr.getRight())) {
					double min = expr.getRight().evaluateDouble();
					minBound = min;
				}
				if (expr.getLeft() instanceof ExpressionNode
						&& isVariableNegated((ExpressionNode) expr.getLeft())
						&& isNumber(expr.getRight())) {
					double max = expr.getRight().evaluateDouble();
					maxBound = -max;
				}
				if (isNumber(expr.getLeft())
						&& expr.getRight() instanceof ExpressionNode
						&& isVariableNegated(
								(ExpressionNode) expr.getRight())) {
					double min = expr.getLeft().evaluateDouble();
					minBound = -min;
				}
				if (isNumber(expr.getLeft())
						&& expr.getRight() instanceof FunctionVariable) {
					double max = expr.getLeft().evaluateDouble();
					maxBound = max;
				}
			}
			if (expr.getOperation().equals(Operation.PLUS)) {
				if (expr.getLeft() instanceof FunctionVariable
						&& isNumber(expr.getRight())) {
					double min = expr.getRight().evaluateDouble();
					minBound = -min;
				}
				if (expr.getLeft() instanceof ExpressionNode
						&& isVariableNegated((ExpressionNode) expr.getLeft())
						&& isNumber(expr.getRight())) {
					double max = expr.getRight().evaluateDouble();
					maxBound = max;
				}
			}
		}
		if (ineq2.getOperation().equals(Operation.LESS)
				|| ineq2.getOperation().equals(Operation.LESS_EQUAL)) {
			ExpressionNode expr = ineq2.getNormalExpression();
			if (expr.getOperation().equals(Operation.MINUS)) {
				if (isNumber(expr.getLeft())
						&& expr.getRight() instanceof FunctionVariable) {
					double max = expr.getLeft().evaluateDouble();
					maxBound = max;
				}
				if (expr.getLeft() instanceof FunctionVariable
						&& isNumber(expr.getRight())) {
					double min = expr.getRight().evaluateDouble();
					minBound = min;
				}
			}
			if (expr.getOperation().equals(Operation.PLUS)) {
				if (isNumber(expr.getLeft())
						&& expr.getRight() instanceof FunctionVariable) {
					double min = expr.getLeft().evaluateDouble();
					minBound = -min;
				}
			}
		}

	}

	private static boolean isNumber(ExpressionValue expr) {
		return expr instanceof NumberValue
				&& !(expr instanceof FunctionVariable);
	}

	// e.g. -x
	private static boolean isVariableNegated(ExpressionNode expr) {
		if (expr.getOperation().equals(Operation.MULTIPLY)
				&& DoubleUtil.isEqual(expr.getLeft().evaluateDouble(), -1)
				&& expr.getRight() instanceof FunctionVariable) {
			return true;
		}
		return false;
	}

	/**
	 * @return operation is greater equal of less equal
	 */
	public boolean isGrtLessEqual() {
		return this.ineq.getOperation().equals(Operation.GREATER_EQUAL)
				|| this.ineq.getOperation().equals(Operation.LESS_EQUAL);
	}

	/**
	 * @return true if min bound was set
	 */
	public boolean isMinBoundSet() {
		return !DoubleUtil.isEqual(minBound, -1000000);
	}

	/**
	 * @return get max bound
	 */
	public double getMaxBound() {
		return maxBound;
	}

	/**
	 * @return get min bound
	 */
	public double getMinBound() {
		return minBound;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (lines == null) {
			return;
		}
		int i = 0;
		while (i < lines.length && lines[i] != null) {
			if (isHighlighted()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(lines[i]);
			}

			if (geo.getLineThickness() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(
						EuclidianStatic.getStroke(geo.getLineThickness() / 2.0f,
								((GeoElement) ineq.getFunBorder()).lineType));
				g2.draw(lines[i]);
			}

			// TODO: draw label
			i++;
		}
		if (circle == null) {
			return;
		}
		while (i < circle.length && circle[i] != null) {
			if (isHighlighted()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(circle[i]);
			}

			if (geo.getLineThickness() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(
						EuclidianStatic.getStroke(geo.getLineThickness() / 2.0f,
								EuclidianStyleConstants.LINE_TYPE_FULL));
				g2.draw(circle[i]);
				if (!ineq.isStrict()) {
					g2.fill(circle[i]);
				}
			}

			// TODO: draw label
			i++;
		}

	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		for (int i = 0; i < gp.length; i++) {
			if (gp[i] != null && gp[i].contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public void update() {
		// get x-coords of the lines
		if (varIsY) {
			GeoPoint[] roots = ineq.getZeros();
			double[] x = new double[roots.length + 2];
			x[0] = view.getHeight() + 10;
			int numOfX = 1;
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].x < view.toRealWorldCoordY(-10)
						&& roots[i].x > view
								.toRealWorldCoordY(view.getHeight() + 10)) {
					x[numOfX++] = view.toScreenCoordY(roots[i].x);
				}
			}
			x[numOfX++] = -10;
			if (numOfX > 2 && x[numOfX - 2] > 0
					&& x[numOfX - 2] < view.getHeight()) {
				yLabel = (int) x[numOfX - 2] - 5;
			} else {
				yLabel = 10;
			}
			xLabel = (int) view.getXZero() + 6;
			initGP(numOfX);
			int j = ineq.getFunBorder().value(
					view.toRealWorldCoordY(view.getHeight() + 10)) <= 0 ? 1 : 0;
			GArea a = AwtFactory.getPrototype().newArea();
			for (int i = 0; 2 * i + j + 1 < numOfX; i++) {
				gp[i] = new GeneralPathClipped(view);
				gp[i].resetWithThickness(geo.getLineThickness());
				gp[i].moveTo(-10, x[2 * i + j]);
				gp[i].lineTo(view.getWidth() + 10, x[2 * i + j]);
				gp[i].lineTo(view.getWidth() + 10, x[2 * i + j + 1]);
				gp[i].lineTo(-10, x[2 * i + j + 1]);
				gp[i].lineTo(-10, x[2 * i + j]);
				gp[i].closePath();
				lines[2 * i] = AwtFactory.getPrototype().newLine2D();
				lines[2 * i].setLine(-10, x[2 * i + j], view.getWidth() + 10,
						x[2 * i + j]);
				lines[2 * i + 1] = AwtFactory.getPrototype().newLine2D();
				lines[2 * i + 1].setLine(-10, x[2 * i + j + 1],
						view.getWidth() + 10, x[2 * i + j + 1]);
				a.add(AwtFactory.getPrototype().newArea(gp[i]));
			}
			setShape(a);
		} else {
			GeoPoint[] roots = ineq.getZeros();
			double[] x = new double[roots.length + 2];
			x[0] = -10;
			int numOfX = 1;
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].x > view.toRealWorldCoordX(-10)
						&& roots[i].x < view
								.toRealWorldCoordX(view.getWidth() + 10)) {
					x[numOfX++] = view.toScreenCoordX(roots[i].x);
				}
			}
			x[numOfX++] = view.getWidth() + 10;

			if (numOfX > 2 && x[numOfX - 2] > 0
					&& x[numOfX - 2] < view.getHeight()) {
				xLabel = (int) x[numOfX - 2] - 10;
			} else {
				xLabel = 10;
			}
			yLabel = (int) view.getYZero() + 15;

			initGP(numOfX);

			GArea a = AwtFactory.getPrototype().newArea();
			int circleCount = 0;
			if ((geo instanceof GeoFunction)
					&& ((GeoFunction) geo).showOnAxis()) {
				circle = new GEllipse2DDouble[numOfX];
				for (int i = 0; i < numOfX; i++) {
					if (x[i] < 0) {
						continue;
					}
					if (x[i] > view.getWidth()) {
						break;
					}
					circle[circleCount] = AwtFactory.getPrototype()
							.newEllipse2DDouble();
					double radius = geo.getLineThickness() * DOT_RADIUS;
					circle[circleCount].setFrame(x[i] - radius,
							view.toScreenCoordY(0) - radius, 2 * radius,
							2 * radius);
					circleCount++;
				}
			} else {
				int j = ineq.getFunBorder()
						.value(view.toRealWorldCoordX(-10)) <= 0 ? 1 : 0;

				for (int i = 0; 2 * i + j + 1 < numOfX; i++) {
					gp[i] = new GeneralPathClipped(view);
					gp[i].resetWithThickness(geo.getLineThickness());
					gp[i].moveTo(x[2 * i + j], -10);
					gp[i].lineTo(x[2 * i + j], view.getHeight() + 10);
					gp[i].lineTo(x[2 * i + j + 1], view.getHeight() + 10);
					gp[i].lineTo(x[2 * i + j + 1], -10);
					gp[i].lineTo(x[2 * i + j], -10);
					gp[i].closePath();
					lines[2 * i] = AwtFactory.getPrototype().newLine2D();
					lines[2 * i].setLine(x[2 * i + j], -10, x[2 * i + j],
							view.getHeight() + 10);
					lines[2 * i + 1] = AwtFactory.getPrototype().newLine2D();
					lines[2 * i + 1].setLine(x[2 * i + 1 + j], -10,
							x[2 * i + 1 + j], view.getHeight() + 10);
					a.add(AwtFactory.getPrototype().newArea(gp[i]));
				}
			}
			setShape(a);
		}
		updateStrokes(geo);
	}

	private void initGP(int numOfX) {
		if (gp == null) {
			gp = new GeneralPathClipped[numOfX / 2];
			lines = new GLine2D[numOfX];
		}
	}

	/**
	 * Set all lines to null
	 */
	public void ignoreLines() {
		for (int i = 0; i < lines.length; i++) {
			lines[i] = null;
		}
	}

	/**
	 * @return inequality
	 */
	public Inequality getIneq() {
		return this.ineq;
	}
}
