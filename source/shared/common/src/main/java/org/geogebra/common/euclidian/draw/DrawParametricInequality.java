package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Drawable for drawing inequalities like x &lt; sin(y) or y &lt; x^3. Never
 * stands on its own, always part of DrawInequality tree
 */
class DrawParametricInequality extends SetDrawable {

	private Inequality paramIneq;
	private GeneralPathClippedForCurvePlotter gp;

	/**
	 * @param ineq
	 *            parametric inequality
	 * @param view
	 *            view
	 * @param geo
	 *            top level element (the function which may consist of several
	 *            inequalities)
	 */
	protected DrawParametricInequality(Inequality ineq, EuclidianView view,
			GeoElement geo) {
		this.view = view;
		this.paramIneq = ineq;
		this.geo = geo;
	}

	@Override
	public GArea getShape() {
		return AwtFactory.getPrototype().newArea(gp);
	}

	/**
	 * @return border of the inequality (function of x or y)
	 */
	GeoElement getBorder() {
		return paramIneq.getBorder();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isHighlighted()) {
			g2.setPaint(geo.getSelColor());
			g2.setStroke(selStroke);
			g2.draw(gp);
		}

		fill(g2, gp); // fill using default/hatching/image as
		// appropriate

		if (geo.getLineThickness() > 0) {
			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.draw(gp);
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return gp.contains(x, y) || gp.intersects(x - hitThreshold,
				y - hitThreshold, 2 * hitThreshold, 2 * hitThreshold);
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
		if (gp == null) {
			gp = new GeneralPathClippedForCurvePlotter(view);
		}
		gp.resetWithThickness(geo.getLineThickness());
		GeoFunction border = paramIneq.getFunBorder();
		border.setLineThickness(geo.getLineThickness());
		updateStrokes(border);
		GPoint labelPos;
		if (paramIneq.getType() == IneqType.INEQUALITY_PARAMETRIC_X) {
			double bx = view.toRealWorldCoordY(-10);
			double ax = view.toRealWorldCoordY(view.getHeight() + 10);
			double axEv = view.toScreenCoordYd(ax);
			if (paramIneq.isAboveBorder()) {
				gp.moveTo(view.getWidth() + 10, axEv);
				labelPos = CurvePlotter.plotCurve(border, ax, bx, view, gp,
						true, Gap.RESET_XMAX);
				gp.lineTo(view.getWidth() + 10, gp.getCurrentPoint().getY());
				gp.lineTo(view.getWidth() + 10, axEv);
				gp.closePath();
			} else {
				gp.moveTo(-10, axEv);
				labelPos = CurvePlotter.plotCurve(border, ax, bx, view, gp,
						true, Gap.RESET_XMIN);
				gp.lineTo(-10, gp.getCurrentPoint().getY());
				gp.lineTo(-10, axEv);
				gp.closePath();
			}
		} else {
			double ax = view.toRealWorldCoordX(-10);
			double bx = view.toRealWorldCoordX(view.getWidth() + 10);
			double axEv = view.toScreenCoordXd(ax);
			if (paramIneq.isAboveBorder()) {
				gp.moveTo(axEv, -10);
				labelPos = CurvePlotter.plotCurve(border, ax, bx, view, gp,
						true, Gap.RESET_YMIN);
				gp.lineTo(gp.getCurrentPoint().getX(), -10);
				gp.lineTo(axEv, -10);
				gp.closePath();
			} else {
				gp.moveTo(axEv, view.getHeight() + 10);
				labelPos = CurvePlotter.plotCurve(border, ax, bx, view, gp,
						true, Gap.RESET_YMAX);
				gp.lineTo(gp.getCurrentPoint().getX(), view.getHeight() + 10);
				gp.lineTo(axEv, view.getHeight() + 10);
				gp.closePath();
			}
			border.evaluateCurve(ax);

		}
		if (this.geo.isLabelVisible() && labelPos != null) {
			xLabel = labelPos.getX();
			yLabel = labelPos.getY();
			addLabelOffset();
		}
	}

	/**
	 * @return true when x is the parameter (false for y)
	 */
	boolean isXparametric() {
		return paramIneq.getType() == IneqType.INEQUALITY_PARAMETRIC_X;
	}
}