/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.AlgoCasCellInterface;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.cas.AlgoIntegralFunctions;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Draws definite Integral of a GeoFunction
 * 
 * @author Markus Hohenwarter
 */
public class DrawIntegralFunctions extends DrawFunctionArea {

	private GeoNumeric n;
	private GeoFunction f;
	private GeoFunction g;
	private NumberValue a;
	private NumberValue b;

	private GeneralPathClippedForCurvePlotter gp;
	private boolean isVisible;
	private boolean labelVisible;
	private boolean isCasObject;

	/**
	 * Creates drawable for integral between two functions
	 * 
	 * @param view
	 *            view
	 * @param n
	 *            integral between functions
	 * @param casObject
	 *            true if n was created from a GeoCasCell
	 */
	public DrawIntegralFunctions(EuclidianView view, GeoNumeric n,
			boolean casObject) {
		this.view = view;
		this.n = n;
		geo = n;
		isCasObject = casObject;

		n.setDrawable(true);

		init();

		update();
	}

	private void init() {
		if (isCasObject) {
			initFromCasObject();
			return;
		}
		AlgoIntegralFunctions algo = (AlgoIntegralFunctions) n
				.getDrawAlgorithm();
		f = algo.getF();
		g = algo.getG();
		a = algo.getA();
		b = algo.getB();
	}

	private void initFromCasObject() {
		AlgoCasCellInterface algo = (AlgoCasCellInterface) n.getDrawAlgorithm();
		GeoCasCell cell = algo.getCasCell();
		Command cmd = cell.getInputVE().getTopLevelCommand();

		f = asFunction(cmd, 0);
		g = asFunction(cmd, 1);
		a = asDouble(cmd, 2);
		b = asDouble(cmd, 3);
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}
		labelVisible = getTopLevelGeo().isLabelVisible();
		updateStrokes(n);

		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm())
				|| isCasObject) {
			init();
		}

		// init gp
		double aRW = Math.min(a.getDouble(), b.getDouble());
		double bRW = Math.max(a.getDouble(), b.getDouble());

		double clipX = view.toRealWorldCoordX(EuclidianStatic.CLIP_DISTANCE)
				- view.toRealWorldCoordX(0);
		// double clipY = view.toRealWorldCoordY(0)
		// - view.toRealWorldCoordY(EuclidianStatic.CLIP_DISTANCE);

		// for DrawParametricCurve.plotCurve to work with special values,
		// these changes are needed (also filter out out of screen integrals)
		// see TRAC-1036
		aRW = Math.max(aRW, view.getXmin() - clipX);
		if (aRW > view.getXmax() + clipX) {
			// make invisible to prevent NPE on draw
			isVisible = false;
			return;
		}

		bRW = Math.min(bRW, view.getXmax() + clipX);
		if (bRW < view.getXmin() - clipX) {
			isVisible = false;
			return;
		}

		// init first point of gp as (ax, ay)
		double ax = view.toClippedScreenCoordX(aRW);
		double ay = view.toClippedScreenCoordY(f.value(aRW));

		// plot area between f and g
		if (gp == null) {
			gp = new GeneralPathClippedForCurvePlotter(view);
		}
		gp.resetWithThickness(geo.getLineThickness());
		gp.moveTo(ax, ay);
		CurvePlotter.plotCurve(f, aRW, bRW, view, gp, false, Gap.LINE_TO);
		CurvePlotter.plotCurve(g, bRW, aRW, view, gp, false, Gap.LINE_TO);
		gp.closePath();

		// gp on screen?
		if (!view.intersects(gp.getGeneralPath())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelVisible) {
			int bx = view.toClippedScreenCoordX(bRW);
			xLabel = (int) Math.round((ax + bx) / 2);
			aRW = view.toRealWorldCoordX(xLabel);
			double y = (f.value(aRW) + g.value(aRW)) / 2;
			yLabel = view.toClippedScreenCoordY(y);
			labelDesc = getTopLevelGeo().getLabelDescription();
			addLabelOffset();
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {
		if (isVisible) {
			if (isHighlighted()) {
				g2.setPaint(n.getSelColor());
				g2.setStroke(selStroke);
				gp.draw(g2);
			}
			if (gp != null) {
				fill(g2, gp.getGeneralPath()); // fill using default/hatching/image as
			} // appropriate

			if (objStroke.getLineWidth() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(objStroke);
				gp.draw(g2);
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x, y, hitThreshold));
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp != null && gp.intersects(rect);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return false;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible() || gp == null) {
			return null;
		}
		return gp.getBounds();
	}

}
