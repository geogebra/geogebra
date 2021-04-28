/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.IntervalPlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.advanced.AlgoFunctionInvert;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.interval.IntervalFunction;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

/**
 * Draws graphs of parametric curves and functions
 * 
 * @author Markus Hohenwarter, with ideas from John Gillam (see below)
 */
public class DrawParametricCurve extends Drawable {

	private IntervalPlotter intervalPlotter;
	private CurveEvaluable curve;
	private GeneralPathClippedForCurvePlotter gp;
	private boolean isVisible;
	private boolean labelVisible;
	private boolean fillCurve;

	private StringBuilder labelSB = new StringBuilder();
	private int nPoints = 0;
	private ArrayList<GPoint2D> points;
	private GLine2D diag1;
	private GLine2D diag2;
	private ExpressionNode dataExpression;
	private FunctionVariable invFV;
	private ExpressionNode invert;

	private static final Inspecting containsLog = new Inspecting() {
		@Override
		public boolean check(ExpressionValue v) {
			if (v instanceof ExpressionNode) {
				Operation op = ((ExpressionNode) v).getOperation();

				return op == Operation.LOG || op == Operation.LOG2
						|| op == Operation.LOG10 || op == Operation.LOGB;
			}

			return false;
		}
	};

	/**
	 * Creates graphical representation of the curve
	 * 
	 * @param view
	 *            Euclidian view in which it should be drawn
	 * @param curve
	 *            Curve to be drawn
	 */
	public DrawParametricCurve(EuclidianView view, CurveEvaluable curve) {
		this.view = view;
		this.curve = curve;
		geo = curve.toGeoElement();
		createGeneralPath();
		createIntervalPlotter();
		update();
	}

	private void createIntervalPlotter() {
		intervalPlotter = new IntervalPlotter(view, gp);
		if (this.geo != null && this.geo.isGeoFunction()) {
			GeoFunction function = (GeoFunction) this.geo;
			if (IntervalFunction.isSupported(function)) {
				intervalPlotter.enableFor(function);
			} else {
				intervalPlotter.disable();
			}
		}
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}

		labelVisible = getTopLevelGeo().isLabelVisible();
		if (isIntervalPlotterActive()) {
			updateStrokes(geo);
			updateIntervalPlot();
		} else {
			updateParametric();
		}
	}

	private void enableIntervalPlotterIfSupported() {
		if (IntervalFunction.isSupported(geo)) {
			if (!intervalPlotter.isEnabled()) {
				intervalPlotter.enableFor((GeoFunction) geo);
			}
		} else {
			intervalPlotter.disable();
		}
	}

	private boolean isIntervalPlotterActive() {
		return IntervalFunction.isSupported(geo)
				&& intervalPlotter.isEnabled();
	}

	private void updateIntervalPlot() {
		gp.resetWithThickness(geo.getLineThickness());
		intervalPlotter.update();
		updateLabelPoint();
		updateTrace(geo.getTrace());
	}

	private void updateLabelPoint() {
		GPoint labelPoint = intervalPlotter.getLabelPoint();
		if (labelPoint != null) {
			updateLabel(labelPoint);
		} else {
			labelDesc = null;
		}
	}

	@Override
	public void updateIfNeeded() {
		if (needsUpdate()) {
			setNeedsUpdate(false);
			updateIntervalPlotterIfNeeded();
			update();
		}
	}

	private void updateIntervalPlotterIfNeeded() {
		if (intervalPlotter == null) {
			return;
		}

		enableIntervalPlotterIfSupported();
		if (isIntervalPlotterActive()) {
			intervalPlotter.needsUpdateAll();
		}
	}

	private void updateParametric() {
		dataExpression = null;
		if (geo.getLineType() == EuclidianStyleConstants.LINE_TYPE_POINTWISE
				&& (curve instanceof GeoFunction)) {
			((GeoFunction) curve).getFunctionExpression()
					.inspect(checkPointwise());
		}
		updateStrokes(geo);
		if (dataExpression != null) {
			updatePointwise();
			return;
		}
		if (gp == null) {
			createGeneralPath();
		}
		gp.resetWithThickness(geo.getLineThickness());

		fillCurve = filling(curve);

		double min = curve.getMinParameter();
		double max = curve.getMaxParameter();

		CurveEvaluable toPlot = curve;

		if (curve.toGeoElement().isGeoFunction()) {
			GeoFunction function = (GeoFunction) curve.toGeoElement();
			double minView, maxView;

			GeoFunction inverted;
			if (function.getFunction().inspect(containsLog)
					&& canInvert(function.getFunction().getExpression())
					&& (inverted = invertFunction(function)) != null) {
				toPlot = inverted;

				min = function.hasInterval() ? function.getIntervalMin() : Double.NEGATIVE_INFINITY;
				max = function.hasInterval() ? function.getIntervalMax() : Double.POSITIVE_INFINITY;

				minView = view.getYmin();
				maxView = view.getYmax();
			} else {
				minView = view.getXmin();
				maxView = view.getXmax();
			}

			if (min < minView || Double.isInfinite(min)) {
				min = minView;
			}
			if (max > maxView || Double.isInfinite(max)) {
				max = maxView;
			}
		}
		GPoint labelPoint;
		if (DoubleUtil.isEqual(min, max)) {
			double[] eval = new double[2];
			curve.evaluateCurve(min, eval);
			view.toScreenCoords(eval);
			labelPoint = new GPoint((int) eval[0], (int) eval[1]);
		} else {
			labelPoint = CurvePlotter.plotCurve(toPlot, min, max, view, gp,
					labelVisible, fillCurve ? Gap.CORNER
							: Gap.MOVE_TO);
		}

		// gp on screen?
		if (!view.intersects(gp)) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelPoint != null) {
			updateLabel(labelPoint);
		}
		// shape for filling

		if (geo.isInverseFill()) {
			setShape(AwtFactory.getPrototype().newArea(view.getBoundingPath()));
			getShape().subtract(AwtFactory.getPrototype().newArea(gp));
		}
		// draw trace
		updateTrace(curve.getTrace());
	}

	private void updateTrace(boolean showTrace) {
		if (showTrace) {
			isTracing = true;
			GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) {
				drawTrace(g2);
			}
		} else {
			if (isTracing) {
				isTracing = false;
			}
		}
	}

	private void updateLabel(GPoint labelPoint) {
		xLabel = labelPoint.x;
		yLabel = labelPoint.y;
		switch (geo.getLabelMode()) {
		case GeoElementND.LABEL_NAME_VALUE:
			StringTemplate tpl = StringTemplate.latexTemplate;
			labelSB.setLength(0);
			labelSB.append('$');
			String label = getTopLevelGeo().getLabel(tpl);
			if (LabelManager.isShowableLabel(label)) {
				labelSB.append(label);
				labelSB.append('(');
				labelSB.append(((VarString) geo).getVarString(tpl));
				labelSB.append(")\\;=\\;");
			}
			labelSB.append(geo.getLaTeXdescription());
			labelSB.append('$');

			labelDesc = labelSB.toString();
			break;

		case GeoElementND.LABEL_VALUE:
			labelSB.setLength(0);
			labelSB.append('$');
			labelSB.append(geo.getLaTeXdescription());
			labelSB.append('$');

			labelDesc = labelSB.toString();
			break;

		case GeoElementND.LABEL_CAPTION:
		default: // case LABEL_NAME:
			labelDesc = getTopLevelGeo().getLabelDescription();
		}
		addLabelOffsetEnsureOnScreen(view.getFontConic());
	}

	private void createGeneralPath() {
		gp = new GeneralPathClippedForCurvePlotter(view);
	}

	private void updatePointwise() {
		if (points == null) {
			points = new ArrayList<>();
		}

		diag1 = AwtFactory.getPrototype().newLine2D();
		int size = geo.getLineThickness();
		diag1.setLine(-size, -size, size, size);
		diag2 = AwtFactory.getPrototype().newLine2D();
		diag2.setLine(-size, size, size, -size);

		nPoints = 0;

		ListValue lvX = (ListValue) ((MyNumberPair) dataExpression.getRight())
				.getX();
		/*
		 * ListValue lvY = (ListValue) ((MyNumberPair)
		 * dataExpression.getRight()) .getY();
		 */
		for (int i = 0; i < lvX.size(); i++) {
			double xRW = lvX.getListElement(i).evaluateDouble();
			if (invert != null) {
				invFV.set(xRW);
				xRW = invert.evaluateDouble();
			}
			double x = view.toScreenCoordXd(xRW);
			if (x < 0 || x > view.getWidth()) {
				continue;
			}
			double y = view
					.toScreenCoordYd(((GeoFunction) curve).value(xRW));

			if (y < 0 || y > view.getHeight()) {
				continue;
			}
			GPoint2D pt = new GPoint2D(x, y);
			if (points.size() > nPoints) {
				points.set(nPoints, pt);
			} else {
				points.add(pt);
			}
			nPoints++;
		}

	}

	private Inspecting checkPointwise() {
		return new Inspecting() {

			@Override
			public boolean check(ExpressionValue v) {
				if (v.isExpressionNode() && ((ExpressionNode) v)
						.getOperation() == Operation.DATA) {

					return updateDataExpression((ExpressionNode) v);
				}
				return false;
			}
		};
	}

	private boolean canInvert(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) ev;
			Operation op = en.getOperation();

			if (op != Operation.LOG && op != Operation.LOG2
					&& op != Operation.LOG10 && op != Operation.LOGB
					&& op != Operation.PLUS && op != Operation.MINUS
					&& op != Operation.MULTIPLY && op != Operation.DIVIDE) {
				return false;
			}

			return canInvert(en.getLeft()) && canInvert(en.getRight());
		}

		return ev instanceof MyDouble;
	}

	private GeoFunction invertFunction(GeoFunction function) {
		FunctionVariable oldFV = function.getFunction().getFunctionVariable();
		FunctionVariable newFV = new FunctionVariable(view.getKernel(), "y");

		ExpressionNode inverse = AlgoFunctionInvert.invert(function.getFunctionExpression(),
				oldFV, newFV, view.getKernel());

		if (inverse == null) {
			return null;
		}

		Function func = new Function(inverse, newFV);
		GeoFunction result = new GeoFunction(view.getKernel().getConstruction(), func);
		result.swapEval();

		return result;
	}

	/**
	 * @param v
	 *            new node
	 * @return whether this is a valid datafunction
	 */
	protected boolean updateDataExpression(ExpressionNode v) {
		dataExpression = (v);
		if (dataExpression.getLeft().unwrap() instanceof FunctionVariable) {
			invert = null;
			return true;
		}
		invFV = new FunctionVariable(view.getApplication().getKernel());
		invert = AlgoFunctionInvert.invert(dataExpression.getLeft().unwrap(),
				((GeoFunction) curve).getFunctionVariables()[0], invFV,
				geo.getKernel());
		if (invert == null) {
			dataExpression = null;
		}
		return true;
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (intervalPlotter.isEnabled()) {
			drawIntervalPlot(g2);
		} else {
			drawParametric(g2);
		}
		if (labelVisible && isVisible) {
			g2.setFont(view.getFontConic());
			g2.setPaint(geo.getLabelColor());
			drawLabel(g2);
		}
	}

	private void drawIntervalPlot(GGraphics2D g2) {
		if (!isVisible) {
			return;
		}

		if (isHighlighted()) {
			g2.setPaint(geo.getSelColor());
			g2.setStroke(selStroke);
			intervalPlotter.draw(g2);
		}

		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		intervalPlotter.draw(g2);
	}

	private void drawParametric(GGraphics2D g2) {
		if (isVisible) {
			if (dataExpression != null) {
				g2.setPaint(getObjectColor());
				if (isHighlighted()) {
					g2.setPaint(geo.getSelColor());
					g2.setStroke(selStroke);
					drawPoints(g2);
				}
				g2.setStroke(objStroke);
				drawPoints(g2);
				return;
			}
			if (isHighlighted()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(gp);
			}

			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(gp);

			if (fillCurve) {
				try {
					// fill using default/hatching/image as appropriate
					fill(g2, (geo.isInverseFill() ? getShape() : gp));

				} catch (Exception e) {
					Log.error(e.getMessage());
				}
			}
		}
	}

	private void drawPoints(GGraphics2D g2) {
		for (int i = 0; i < nPoints; i++) {
			g2.saveTransform();
			g2.translate(points.get(i).getX(), points.get(i).getY());
			g2.draw(diag1);
			g2.draw(diag2);
			g2.restoreTransform();
		}

	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		g2.draw(gp);

		if (fillCurve) {
			try {
				// fill using default/hatching/image as appropriate
				fill(g2, (geo.isInverseFill() ? getShape() : gp));

			} catch (Exception e) {
				Log.error(e.getMessage());
			}
		}

	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		if (isVisible) {
			if (dataExpression != null) {
				for (int i = 0; i < nPoints; i++) {
					if (MyMath.length(x - points.get(i).getX(),
							y - points.get(i).getY()) < hitThreshold) {
						return true;
					}
				}
				return false;
			}
			GShape t = geo.isInverseFill() ? getShape() : gp;

			if (strokedShape == null) {
				// AND-547, initial buffer size
				try {
					strokedShape = objStroke.createStrokedShape(gp, 800);
				} catch (Exception e) {
					Log.error(
							"problem creating Curve shape: " + e.getMessage());
					return false;
				}
			}
			if (geo.isFilled()) {
				return t.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
			}

			// workaround for #2364
			if (geo.isGeoFunction()) {
				GeoFunction f = (GeoFunction) geo;
				double rwx = view.toRealWorldCoordX(x);
				double low = view.toRealWorldCoordY(y + hitThreshold);
				double high = view.toRealWorldCoordY(y - hitThreshold);
				double dx = hitThreshold * view.getInvXscale();
				double left = f.value(rwx - dx);
				if (left >= low && left <= high) {
					return true;
				}
				double right = f.value(rwx + dx);
				if (right >= low && right <= high) {
					return true;
				}
				double middle = f.value(rwx);
				if (middle >= low && middle <= high) {
					return true;
				}
				if ((right < low && left < low && middle < low)
						|| (right > high && left > high && middle > high)
						|| (!MyDouble.isFinite(right)
								&& !MyDouble.isFinite(left)
								&& !MyDouble.isFinite(middle))) {
					return false;
				}
				return gp.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold)
						&& !gp.contains(x - hitThreshold, y - hitThreshold,
								2 * hitThreshold, 2 * hitThreshold);
			}

			// not GeoFunction, eg parametric
			return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);

		}
		return false;
		/*
		 * return gp.intersects(x-3,y-3,6,6) && !gp.contains(x-3,y-3,6,6);
		 */
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (isVisible) {
			GShape t = geo.isInverseFill() ? getShape() : gp;
			if (strokedShape == null) {
				// AND-547, initial buffer size
				try {
					strokedShape = objStroke.createStrokedShape(gp, 800);
				} catch (Exception e) {
					Log.error(
							"problem creating Curve shape: " + e.getMessage());
					return false;
				}
			}
			if (geo.isFilled()) {
				return t.intersects(rect);
			}

			return strokedShape.intersects(rect);

		}
		return false;
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return gp != null && rect.contains(gp.getBounds());
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !curve.isClosedPath()
				|| !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		return AwtFactory.getPrototype().newRectangle(gp.getBounds());
	}

	@Override
	public GRectangle2D getBoundsForStylebarPosition() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return AwtFactory.getPrototype().newRectangle(gp.getBounds());
	}

	final private static boolean filling(CurveEvaluable curve) {
		return !curve.isFunctionInX() && curve.toGeoElement().isFilled();
	}

	@Override
	public boolean isCompatibleWithGeo() {
		// generic curve (parametric) or function R->R, but not inequality
		return !curve.isFunctionInX() || geo.isGeoFunction();
	}
}
