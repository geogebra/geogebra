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

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;

/**
 * Test mock of {@code EuclidianViewBounds} that computes origin and scales from world
 * bounds and screen size. Provides forward/back transforms between world and screen for
 * unit tests.
 */
public class EuclidianViewBoundsRWSCMock extends EuclidianViewBoundsMock {

	private double xscale;
	private double yscale;
	private double xZero;
	private double yZero;
	private double invXScale;
	private double invYSscale;

	/**
	 * Creates the mock for a world rectangle and screen size, then initializes scales and
	 * origin.
	 *
	 * @param xmin   min x in world units
	 * @param xmax   max x in world units
	 * @param ymin   min y in world units
	 * @param ymax   max y in world units
	 * @param width  screen width in pixels
	 * @param height screen height in pixels
	 */
	public EuclidianViewBoundsRWSCMock(double xmin, double xmax, double ymin, double ymax,
			int width, int height) {
		super(xmin, xmax, ymin, ymax);
		setSize(width, height);
		update();
	}

	/**
	 * Recomputes scales, their inverses, and the screen origin from bounds and size.
	 */
	void update() {
		xscale = getWidth() / (getXmax() - getXmin());
		yscale = getHeight() / (getYmax() - getYmin());
		invXScale = 1 / xscale;
		invYSscale = 1 / yscale;
		xZero = -getXmin() * xscale;
		yZero = getYmax() * yscale;

	}

	/**
	 * Converts a world x to a screen x (pixels).
	 *
	 * @param xRW world x
	 * @return screen x in pixels
	 */
	@Override
	public double toScreenCoordXd(double xRW) {
		return getXZero() + (xRW * xscale);
	}

	/**
	 * Converts a world y to a screen y (pixels).
	 *
	 * @param yRW world y
	 * @return screen y in pixels
	 */
	@Override
	public double toScreenCoordYd(double yRW) {
		return getYZero() + (yRW * yscale);
	}

	/**
	 * Converts a screen x (pixels) to world x.
	 *
	 * @param x screen x
	 * @return world x
	 */
	@Override
	public double toRealWorldCoordX(double x) {
		return (x - getXZero()) * invXScale;
	}

	/**
	 * Converts a screen y (pixels) to world y.
	 *
	 * @param y screen y
	 * @return world y
	 */
	@Override
	public double toRealWorldCoordY(double y) {
		return (y - getYZero()) * invYSscale;
	}

	/**
	 * Screen x coordinate of the world origin.
	 *
	 * @return x0 in pixels
	 */
	@Override
	public double getXZero() {
		return xZero;
	}

	/**
	 * Screen y coordinate of the world origin.
	 *
	 * @return y0 in pixels
	 */
	@Override
	public double getYZero() {
		return yZero;
	}

	/**
	 * World units per one screen pixel along x.
	 *
	 * @return inverse x scale
	 */
	@Override
	public double getInvXscale() {
		return invXScale;
	}

	/**
	 * World units per one screen pixel along y.
	 *
	 * @return inverse y scale
	 */
	@Override
	public double getInvYscale() {
		return invYSscale;
	}
}
