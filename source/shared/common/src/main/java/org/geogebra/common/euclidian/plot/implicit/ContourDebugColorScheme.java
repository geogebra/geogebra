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

import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.plot.implicit.BernsteinPlotCell.BernsteinPlotCellKind;

/**
 * Provides a debug-only color mapping for {@link BernsteinPlotCellKind}.
 *
 * <p><strong>NOTE:</strong> this mapping is used only for visual debugging
 * and is not part of the core marching square algorithms.</p>
 */
final class ContourDebugColorScheme {
	private static final Map<BernsteinPlotCellKind, GColor> COLORS = Map.of(
			BernsteinPlotCellKind.CELL2, GColor.GRAY,
			BernsteinPlotCellKind.CELL1, GColor.YELLOW,
			BernsteinPlotCellKind.CELL0, GColor.GREEN);

	/**
	 * Returns the debug color associated with the given contour cell kind.
	 * @param kind the {@link BernsteinPlotCellKind} whose debug color is requested
	 * @return the {@link GColor} to use when rendering this kind, or {@code GColor.BLACK}
	 * if the kind is unmapped
	 */
	static GColor of(BernsteinPlotCellKind kind) {
		return COLORS.getOrDefault(kind, GColor.BLACK);
	}

	private ContourDebugColorScheme() {
		// prevent instantiation
	}
}
