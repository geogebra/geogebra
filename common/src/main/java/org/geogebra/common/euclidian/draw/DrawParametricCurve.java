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
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.advanced.AlgoFunctionInvert;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;

/**
 * Draws graphs of parametric curves and functions
 * 
 * @author Markus Hohenwarter, with ideas from John Gillam (see below)
 */
public class DrawParametricCurve extends Drawable {

	private CurveEvaluable curve;
	private GeneralPathClippedForCurvePlotter gp;
	private boolean isVisible, labelVisible, fillCurve;
	private boolean pointwise = false;

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
		update();
	}

	private StringBuilder labelSB = new StringBuilder();

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();		
		if (!isVisible)
			return;
		dataExpression = null;
		if (geo.getLineType() == EuclidianStyleConstants.LINE_TYPE_POINTWISE
				&& (curve instanceof GeoFunction)) {
			((GeoFunction) curve).getFunctionExpression().inspect(
					checkPointwise());
		}
		labelVisible = geo.isLabelVisible();
		updateStrokes(geo);
		if (dataExpression != null) {
			updatePointwise();
			return;
		}
		if (gp == null)
			gp = new GeneralPathClippedForCurvePlotter(view);
		gp.reset();

		fillCurve = filling(curve);

		double min = curve.getMinParameter();
		double max = curve.getMaxParameter();
		if (curve.toGeoElement().isGeoFunction()) {
			double minView = view.getXmin();
			double maxView = view.getXmax();
			if (min < minView || Double.isInfinite(min))
				min = minView;
			if (max > maxView || Double.isInfinite(max))
				max = maxView;
		}
		GPoint labelPoint;
		if (Kernel.isEqual(min, max)) {
			double[] eval = new double[2];
			curve.evaluateCurve(min, eval);
			view.toScreenCoords(eval);
			labelPoint = new GPoint((int) eval[0], (int) eval[1]);
		} else {
			labelPoint = CurvePlotter.plotCurve(curve, min, max, view, gp,
					labelVisible, fillCurve ? CurvePlotter.Gap.CORNER
							: CurvePlotter.Gap.MOVE_TO);
		}

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelPoint != null) {
			xLabel = labelPoint.x;
			yLabel = labelPoint.y;
			switch (geo.labelMode) {
			case GeoElement.LABEL_NAME_VALUE:
				StringTemplate tpl = StringTemplate.latexTemplate;
				labelSB.setLength(0);
				labelSB.append('$');
				labelSB.append(geo.getLabel(tpl));
				labelSB.append('(');
				labelSB.append(((VarString) geo).getVarString(tpl));
				labelSB.append(")\\;=\\;");
				labelSB.append(geo.getLaTeXdescription());
				labelSB.append('$');

				labelDesc = labelSB.toString();
				break;

			case GeoElement.LABEL_VALUE:
				labelSB.setLength(0);
				labelSB.append('$');
				labelSB.append(geo.getLaTeXdescription());
				labelSB.append('$');

				labelDesc = labelSB.toString();
				break;

			case GeoElement.LABEL_CAPTION:
			default: // case LABEL_NAME:
				labelDesc = geo.getLabelDescription();
			}
			addLabelOffsetEnsureOnScreen();
		}
		// shape for filling

		if (geo.isInverseFill()) {
			setShape(AwtFactory.prototype.newArea(view.getBoundingPath()));
			getShape().subtract(AwtFactory.prototype.newArea(gp));
		}
		// draw trace
		if (curve.getTrace()) {
			isTracing = true;
			org.geogebra.common.awt.GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}
	}

	private int nPoints = 0;
	private ArrayList<GPoint2D> points;
	private GLine2D diag1, diag2;
	private void updatePointwise() {
		if(points == null){
			points = new ArrayList<GPoint2D>();
		}

		diag1 = AwtFactory.prototype.newLine2D();
		int size = geo.getLineThickness();
		diag1.setLine(-size, -size, size, size);
		diag2 = AwtFactory.prototype.newLine2D();
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
					.toScreenCoordYd(((GeoFunction) curve).evaluate(xRW));

			if (y < 0 || y > view.getHeight()) {
				continue;
			}
			GPoint2D pt = AwtFactory.prototype.newPoint2D(x, y);
			if (points.size() > nPoints) {
				points.set(nPoints, pt);
			} else {
				points.add(pt);
			}
			nPoints++;
		}

	}

	private ExpressionNode dataExpression;
	private FunctionVariable invFV;
	private ExpressionNode invert;
	private Inspecting checkPointwise() {
		return new Inspecting(){

			public boolean check(ExpressionValue v) {
				/*
				 * if (v.isExpressionNode() && ((ExpressionNode)
				 * v).getOperation() == Operation.FUNCTION) {
				 * if(((ExpressionNode) v).getLeft() instanceof GeoFunction &&
				 * ((GeoFunction)((ExpressionNode)
				 * v).getLeft()).getFunctionExpression().inspect(this)){ return
				 * true; } }
				 */
				if (v.isExpressionNode()
						&& ((ExpressionNode) v).getOperation() == Operation.DATA) {

					dataExpression = ((ExpressionNode) v);
					if (dataExpression.getLeft().unwrap() instanceof FunctionVariable) {
						invert = null;
						return true;
					}
					invFV = new FunctionVariable(view.getApplication()
							.getKernel());
					invert = AlgoFunctionInvert.invert(
dataExpression.getLeft()
							.unwrap(),
							((GeoFunction) curve).getFunctionVariables()[0],
							invFV, geo.getKernel());
					if (invert == null) {
						App.printStacktrace("" + dataExpression.getLeft());
						dataExpression = null;
					}
					return true;
				}
				return false;
			}};
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (dataExpression != null) {
				g2.setPaint(getObjectColor());
				if (geo.doHighlighting()) {
					g2.setPaint(geo.getSelColor());
					g2.setStroke(selStroke);
					drawPoints(g2);
				}
				g2.setStroke(objStroke);
				drawPoints(g2);
				return;
			}
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.drawWithValueStrokePure(gp);
			}

			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.drawWithValueStrokePure(gp);

			if (fillCurve) {
				try {
					// fill using default/hatching/image as appropriate
					fill(g2, (geo.isInverseFill() ? getShape() : gp), false);

				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
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
	protected final void drawTrace(org.geogebra.common.awt.GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		g2.drawWithValueStrokePure(gp);
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
				// strokedShape = new
				// geogebra.awt.GenericShape(geogebra.awt.BasicStroke.getAwtStroke(objStroke).createStrokedShape(geogebra.awt.GenericShape.getAwtShape(gp)));
				strokedShape = objStroke.createStrokedShape(gp);
			}
			if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
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
				double left = f.evaluate(rwx - dx);
				if (left >= low && left <= high) {
					return true;
				}
				double right = f.evaluate(rwx + dx);
				if (right >= low && right <= high) {
					return true;
				}
				double middle = f.evaluate(rwx);
				if (middle >= low && middle <= high) {
					return true;
				}
				if ((right < low && left < low && middle < low)
						|| (right > high && left > high && middle > high)
						|| (!MyDouble.isFinite(right)
								&& !MyDouble.isFinite(left) && !MyDouble
									.isFinite(middle))) {
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
				// strokedShape = new
				// geogebra.awt.GenericShape(geogebra.awt.BasicStroke.getAwtStroke(objStroke).createStrokedShape(geogebra.awt.GenericShape.getAwtShape(gp)));
				strokedShape = objStroke.createStrokedShape(gp);
			}
			if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
				return t.intersects(rect);
			}

			return strokedShape.intersects(rect);

		}
		return false;
	}

	@Override
	final public boolean isInside(org.geogebra.common.awt.GRectangle rect) {
		return gp != null && rect.contains(gp.getBounds());
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public org.geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !curve.isClosedPath()
				|| !geo.isEuclidianVisible()) {
			return null;
		}
		return AwtFactory.prototype.newRectangle(gp.getBounds());
	}

	final private static boolean filling(CurveEvaluable curve) {
		return !curve.isFunctionInX()
				&& (curve.toGeoElement().getAlphaValue() > 0 || curve
						.toGeoElement().isHatchingEnabled());
	}

}
