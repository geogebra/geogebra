package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.CurvePlotter.Gap;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Drawable for drawing inequalities like x<sin(y) or y<x^3. Never stands of its
 * own, always part of DrawInequality tree
 */
class DrawParametricInequality extends Drawable {

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
	public org.geogebra.common.awt.GArea getShape() {
		return AwtFactory.prototype.newArea(gp);
	}

	/**
	 * @return border of the inequality (function of x or y)
	 */
	GeoElement getBorder() {
		return paramIneq.getBorder();
	}

	@Override
	public void draw(org.geogebra.common.awt.GGraphics2D g2) {
		if (geo.doHighlighting()) {
			g2.setPaint(geo.getSelColor());
			g2.setStroke(selStroke);
			g2.drawWithValueStrokePure(gp);
		}

		fill(g2, gp, true); // fill using default/hatching/image as
		// appropriate

		if (geo.lineThickness > 0) {
			g2.setPaint(getObjectColor());
			g2.setStroke(objStroke);
			g2.drawWithValueStrokePure(gp);
		}

	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return gp.contains(x, y)
				|| gp.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	public boolean isInside(org.geogebra.common.awt.GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;

	}

	@Override
	public void update() {
		if (gp == null)
			gp = new GeneralPathClippedForCurvePlotter(view);
		else
			gp.reset();
		GeoFunction border = paramIneq.getFunBorder();
		border.setLineThickness(geo.lineThickness);
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
		if (this.geo.isLabelVisible()) {
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