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

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.implicit.BernsteinCurveFiller;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.jspecify.annotations.NonNull;

public class DrawImplicitPoly extends SetDrawable implements MatchBorder {

	private final GeneralPathClippedForCurvePlotter gp;
	private final BernsteinCurveFiller bernsteinCurveFiller;

	/**
	 * Constructs a solid shape with the specified border on the given view.
	 * @param ineq the {@link Inequality} defining the inequality
	 * @param view the {@link EuclidianView} to draw on
	 * @param geo the {@link GeoElement} to draw
	 */
	public DrawImplicitPoly(Inequality ineq, EuclidianView view, GeoElement geo) {
		super();
		this.geo = geo;
		this.view = view;
		gp = new GeneralPathClippedForCurvePlotter(view);

		bernsteinCurveFiller = new BernsteinCurveFiller(ineq, view, gp);

	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
		view.getEuclidianController()
				.addZoomerAnimationListener(bernsteinCurveFiller, geo);
	}

	@Override
	public void update() {
		if (bernsteinCurveFiller.needsUpdate()) {
			updateFillerAndRefreshShape();
		}
	}

	private void updateFillerAndRefreshShape() {
		bernsteinCurveFiller.update();
		refreshShapeFromFiller();
	}

	private void refreshShapeFromPendingFillerUpdate() {
		// The filler listens to pan/zoom independently from DrawableND.needsUpdate.
		// A repaint may arrive with this drawable marked clean while the filler is enabled.
		updateFillerAndRefreshShape();
	}

	private void refreshShapeFromFiller() {
		GArea area = bernsteinCurveFiller.getArea();
		setShape(area != null ? area : AwtFactory.getPrototype().newArea());
	}

	@Override
	public void draw(GGraphics2D g2) {
		refreshShapeFromPendingFillerUpdate();
		if (!isForceNoFill()) {
			bernsteinCurveFiller.draw(g2);
		}
		bernsteinCurveFiller.drawBorder(g2, getObjectColor(), isHighlighted(), selStroke);
		double[] pos = bernsteinCurveFiller.getLabelPosition();
		if (pos != null) {
			xLabel = (int) pos[0];
			yLabel = (int) pos[1];
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		GArea area = bernsteinCurveFiller.getArea();
		return area != null && area.intersects(x - hitThreshold,
				y - hitThreshold, 2 * hitThreshold,
				2 * hitThreshold);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(gp.getBounds());
	}

	@Override
	public boolean matchBorder(@NonNull GeoElement border) {
		return border instanceof GeoImplicitCurve
				&& bernsteinCurveFiller.matchesBorder((GeoImplicitCurve) border);
	}
}
