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
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.IntervalPathPlotter;
import org.geogebra.common.euclidian.plot.interval.IntervalPlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.advanced.AlgoFunctionInvert;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.kernel.interval.function.IntervalFunctionSupport;
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
public class DrawParametricCurve extends Drawable implements RemoveNeeded {

	private IntervalPlotter intervalPlotter;
	private final CurveEvaluable curve;
	private GeneralPathClippedForCurvePlotter gp;
	private boolean isVisible;
	private boolean labelVisible;
	private boolean fillCurve;

	private final StringBuilder labelSB = new StringBuilder();
	private int nPoints = 0;
	private ArrayList<GPoint2D> points;
	private GLine2D diag1;
	private GLine2D diag2;
	private ExpressionNode dataExpression;
	private FunctionVariable invFV;
	private ExpressionNode invert;

	private final PlotConditionalFunction plotConditional;

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
		plotConditional = new PlotConditionalFunction(view, gp);
		createIntervalPlotter();
		update();
	}

	private void createIntervalPlotter() {
		IntervalPathPlotter plotter = view.createIntervalPathPlotter(gp);
		GeoFunctionConverter converter = view.getKernel().getFunctionConverter();
		intervalPlotter = new IntervalPlotter(converter, new EuclidianViewBoundsImp(view), plotter);
		if (this.geo != null && this.geo.isGeoFunction()) {
			if (isIntervalPlotterPreferred()) {
				GeoFunction function = (GeoFunction) this.geo;
				intervalPlotter.enableFor(function, view);
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
		if (isIntervalPlotterPreferred()) {
			if (!intervalPlotter.isEnabled()) {
				intervalPlotter.enableFor((GeoFunction) geo, view);
			}
		} else {
			intervalPlotter.disable();
		}
	}

	private boolean isIntervalPlotterPreferred() {
		return IntervalFunctionSupport.isSupported(geo)
				&& !view.isPlotPanel() && !view.isViewForPlane();
	}

	private boolean isIntervalPlotterActive() {
		return isIntervalPlotterPreferred()
				&& intervalPlotter.isEnabled();
	}

	private void updateIntervalPlot() {
		gp.resetWithThickness(geo.getLineThickness());
		intervalPlotter.update();
		updateLabelPoint();
		drawAndUpdateTraceIfNeeded(geo.getTrace());
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

		if (curve.toGeoElement().isGeoFunction()) {
			GeoFunction function = (GeoFunction) curve.toGeoElement();
			double minView, maxView;

			minView = view.getXmin();
			maxView = view.getXmax();

			if (min < minView || Double.isInfinite(min)) {
				min = minView;
			}
			if (max > maxView || Double.isInfinite(max)) {
				max = maxView;
			}

			if (plotConditional.update(function, min, max, labelVisible, fillCurve)) {
				updateLabelAndTrace(plotConditional.getLabelPoint());
				return;
			}
		}
		GPoint labelPoint;
		if (DoubleUtil.isEqual(min, max)) {
			double[] eval = new double[2];
			curve.evaluateCurve(min, eval);
			view.toScreenCoords(eval);
			labelPoint = new GPoint((int) eval[0], (int) eval[1]);
		} else {
			labelPoint = CurvePlotter.plotCurve(curve, min, max, view, gp,
					labelVisible, fillCurve ? Gap.CORNER
							: Gap.MOVE_TO);
		}

		// gp on screen?
		if (!view.intersects(gp)) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		updateLabelAndTrace(labelPoint);
	}

	private void updateLabelAndTrace(GPoint labelPoint) {
		if (labelPoint != null) {
			updateLabel(labelPoint);
		}
		// shape for filling

		if (geo.isInverseFill()) {
			setShape(AwtFactory.getPrototype().newArea(view.getBoundingPath()));
			getShape().subtract(AwtFactory.getPrototype().newArea(gp));
		}
		drawAndUpdateTraceIfNeeded(curve.getTrace());
	}

	private void updateLabel(GPoint labelPoint) {
		xLabel = labelPoint.x;
		yLabel = labelPoint.y;
		switch (geo.getLabelMode()) {
		case GeoElementND.LABEL_NAME_VALUE:
			StringTemplate tpl = StringTemplate.latexTemplate;
			labelSB.setLength(0);
			labelSB.append('$');
			if (getTopLevelGeo().isLabelSet() && getTopLevelGeo().isAlgebraLabelVisible()) {
				labelSB.append(getTopLevelGeo().getLabel(tpl));
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
			double xRW = lvX.get(i).evaluateDouble();
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
		return v -> {
			if (v.isExpressionNode() && ((ExpressionNode) v)
					.getOperation() == Operation.DATA) {

				return updateDataExpression((ExpressionNode) v);
			}
			return false;
		};
	}

	/**
	 * @param v
	 *            new node
	 * @return whether this is a valid datafunction
	 */
	protected boolean updateDataExpression(ExpressionNode v) {
		dataExpression = v;
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
					fill(g2, geo.isInverseFill() ? getShape() : gp);

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
				fill(g2, geo.isInverseFill() ? getShape() : gp);
			} catch (Exception e) {
				Log.error(e.getMessage());
			}
		}

	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
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
					|| (!Double.isFinite(right)
					&& !Double.isFinite(left)
					&& !Double.isFinite(middle))) {
				return false;
			}

			return gp.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold)
					&& !gp.contains(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);
		}

		if (!ensureStrokedShape()) {
			return false;
		}

		// not GeoFunction, eg parametric
		return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
				2 * hitThreshold, 2 * hitThreshold);
	}

	private boolean ensureStrokedShape() {
		if (strokedShape != null) {
			return true;
		}

		// AND-547, initial buffer size
		try {
			strokedShape = decoStroke.createStrokedShape(gp, 800);
		} catch (Throwable e) {
			Log.error(
					"problem creating Curve shape: " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (isVisible) {
			GShape t = geo.isInverseFill() ? getShape() : gp;

			if (geo.isFilled()) {
				return t.intersects(rect);
			}

			if (!ensureStrokedShape()) {
				return false;
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

	private static boolean filling(CurveEvaluable curve) {
		return !curve.isFunctionInX() && curve.toGeoElement().isFilled();
	}

	@Override
	public boolean isCompatibleWithGeo() {
		// generic curve (parametric) or function R->R, but not inequality
		return !curve.isFunctionInX() || geo.isGeoFunction();
	}

	@Override
	public void remove() {
		if (intervalPlotter != null) {
			intervalPlotter.disable();
		}
	}

	public boolean isIntervalPlotterEnabled() {
		return this.intervalPlotter.isEnabled();
	}
}
