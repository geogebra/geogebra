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

import static org.geogebra.common.main.PreviewFeature.IMPLICIT_PLOTTER;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.implicit.BernsteinPlotter;
import org.geogebra.common.euclidian.plot.implicit.CoordSystemAnimatedPlotter;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.main.PreviewFeature;

/**
 * Draw GeoImplicitCurve on euclidian view
 */
public class DrawImplicitCurve extends DrawLocus {

	private CoordSystemAnimatedPlotter bernsteinPlotter;
	private final GeoImplicit implicitCurve;
	private final boolean bernsteinBasedPlotter;
	private GeneralPathClippedForCurvePlotter gp;

	// private int fillSign; //0=>no filling, only curve -1=>fill the negative
	// part, 1=>fill positive part

	/**
	 * Creates new drawable for implicit curve
	 * @param view view
	 * @param implicitCurve implicit curve
	 */
	public DrawImplicitCurve(EuclidianView view, GeoImplicit implicitCurve) {
		this(view, implicitCurve, PreviewFeature.isAvailable(IMPLICIT_PLOTTER)
				&& BernsteinPolynomialConverter.iSupported(implicitCurve.toGeoElement()));
	}

	/**
	 * Creates new drawable for implicit curve
	 * @param view view
	 * @param implicitCurve implicit curve
	 */
	public DrawImplicitCurve(EuclidianView view, GeoImplicit implicitCurve,
			boolean bernsteinBasedPlotter) {
		super(view, implicitCurve.getLocus(),
				implicitCurve.getTransformedCoordSys());
		this.view = view;
		this.implicitCurve = implicitCurve;
		this.geo = implicitCurve.toGeoElement();
		this.bernsteinBasedPlotter = bernsteinBasedPlotter;

		if (this.bernsteinBasedPlotter) {
			createBernsteinPlotter();
		} else {
			update();
		}
	}

	private void createBernsteinPlotter() {
		gp = new GeneralPathClippedForCurvePlotter(view);
		bernsteinPlotter = new BernsteinPlotter(geo, new EuclidianViewBoundsImp(view),
				gp, implicitCurve.getTransformedCoordSys());
		if (!createdByDrawList()) {
			view.getEuclidianController()
					.addZoomerAnimationListener(bernsteinPlotter, geo);
		}
	}

	@Override
	protected void drawLocus(GGraphics2D g2) {
		if (bernsteinBasedPlotter) {
			bernsteinPlotter.draw(g2);
			drawPath(g2, gp);
		} else {
			super.drawLocus(g2);
		}
	}

	@Override
	protected void drawHighlighted(GGraphics2D g2) {
		if (bernsteinBasedPlotter) {
			g2.setPaint(geo.getSelColor());
			g2.setStroke(selStroke);

			bernsteinPlotter.draw(g2);

			drawStrokedPath(g2, gp);
		} else {
			super.drawHighlighted(g2);
		}
	}

	@Override
	public GArea getShape() {
		return AwtFactory.getPrototype().newArea();
	}

	/**
	 * Returns the Curve to be draw (might not be equal to geo, if this is part
	 * of bigger geo)
	 * @return Curve
	 */
	public GeoImplicit getCurve() {
		return implicitCurve;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (!implicitCurve.isDefined()) {
			return false;
		}
		return super.hit(x, y, hitThreshold);
	}

	@Override
	protected void updateAlgos() {
		if (bernsteinBasedPlotter) {
			return;
		}
		implicitCurve.getLocus();
	}
}
