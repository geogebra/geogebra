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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.MyPoint;

/**
 * Combines multiple visual debug overlays into one.
 */
final class CompositeVisualDebug implements VisualDebug {
	private final VisualDebug[] debuggers;

	CompositeVisualDebug(VisualDebug... debuggers) {
		this.debuggers = debuggers;
	}

	@Override
	public void draw(GGraphics2D g2) {
		for (VisualDebug debugger : debuggers) {
			debugger.draw(g2);
		}
	}

	@Override
	public void fill(GGraphics2D g2) {
		for (VisualDebug debugger : debuggers) {
			debugger.fill(g2);
		}
	}

	@Override
	public void setEdgePoints(List<MyPoint> edgePoints) {
		for (VisualDebug debugger : debuggers) {
			debugger.setEdgePoints(edgePoints);
		}
	}
}
