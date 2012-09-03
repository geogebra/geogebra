/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.euclidian.draw.DrawParametricCurve.Gap;
import geogebra.common.kernel.algos.AlgoIntegralFunctions;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Draws definite Integral of a GeoFunction
 * 
 * @author Markus Hohenwarter
 */
public class DrawIntegralFunctions extends Drawable {

	private GeoNumeric n;
	private GeoFunction f, g;
	private NumberValue a, b;

	private GeneralPathClipped gp;
	private boolean isVisible, labelVisible;

	/**
	 * Creates drawable for integral between two functions
	 * 
	 * @param view view
	 * @param n integral between functions
	 */
	public DrawIntegralFunctions(EuclidianView view, GeoNumeric n) {
		this.view = view;
		this.n = n;
		geo = n;

		n.setDrawable(true);

		init();

		update();
	}

	private void init() {
		AlgoIntegralFunctions algo = (AlgoIntegralFunctions) n
				.getDrawAlgorithm();
		f = algo.getF();
		g = algo.getG();
		a = algo.getA();
		b = algo.getB();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		updateStrokes(n);

		if (n.isAlgoMacroOutput())
			init();

		// init gp
		double aRW = a.getDouble();
		double bRW = b.getDouble();

		// for DrawParametricCurve.plotCurve to work with special values,
		// these changes are needed (also filter out out of screen integrals)
		// see #1234
		aRW = Math.max(aRW, view.getXmin() - EuclidianStatic.CLIP_DISTANCE);
		if (aRW > view.getXmax() + EuclidianStatic.CLIP_DISTANCE)
			return;

		bRW = Math.min(bRW, view.getXmax() + EuclidianStatic.CLIP_DISTANCE);
		if (bRW < view.getXmin() - EuclidianStatic.CLIP_DISTANCE)
			return;

		// init first point of gp as (ax, ay)
		double ax = view.toClippedScreenCoordX(aRW);
		double ay = view.toClippedScreenCoordY(f.evaluate(aRW));

		// plot area between f and g
		if (gp == null)
			gp = new GeneralPathClipped(view);
		gp.reset();
		gp.moveTo(ax, ay);
		DrawParametricCurve.plotCurve(f, aRW, bRW, view, gp, false,
				Gap.LINE_TO);
		DrawParametricCurve.plotCurve(g, bRW, aRW, view, gp, false,
				Gap.LINE_TO);
		gp.closePath();

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelVisible) {
			int bx = view.toClippedScreenCoordX(bRW);
			xLabel = (int) Math.round((ax + bx) / 2);
			aRW = view.toRealWorldCoordX(xLabel);
			double y = (f.evaluate(aRW) + g.evaluate(aRW)) / 2;
			yLabel = view.toClippedScreenCoordY(y);
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(n.getSelColor());
				g2.setStroke(selStroke);
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
			}

			fill(g2, gp, true); // fill using default/hatching/image as
								// appropriate

			g2.setPaint(n.getObjectColor());
			g2.setStroke(objStroke);
			EuclidianStatic.drawWithValueStrokePure(gp, g2);

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	final public boolean hit(int x, int y) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6));
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return false;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}
}
