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

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;

/**
 * Simple bounding box with no handlers.
 */
public class SingleBoundingBox extends BoundingBox<GEllipse2DDouble> {

	public SingleBoundingBox(GColor color) {
		setColor(color);
	}

	@Override
	protected void createHandlers() {
		// no handlers
	}

	@Override
	protected GEllipse2DDouble createCornerHandler() {
		return null;
	}

	@Override
	protected GEllipse2DDouble createSideHandler() {
		return null;
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawRectangle(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}
}
