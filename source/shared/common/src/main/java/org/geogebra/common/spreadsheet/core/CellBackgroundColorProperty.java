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

package org.geogebra.common.spreadsheet.core;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Background color of a fixed set of spreadsheet cells. Does not store undo points.
 */
public class CellBackgroundColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {

	private final SpreadsheetStyling styling;
	private final List<TabularRange> ranges;

	/**
	 * @param styling the spreadsheet styling
	 * @param ranges the cell ranges this property applies to; the value is read from the upper
	 * left cell of the last range
	 * @param localization the localization used
	 */
	public CellBackgroundColorProperty(
			@NonNull SpreadsheetStyling styling,
			@NonNull List<TabularRange> ranges,
			@NonNull Localization localization) {
		super(localization, "Color");
		this.styling = styling;
		this.ranges = List.copyOf(ranges);
	}

	@Override
	public boolean hasUniformValue() {
		// Multi-cell ranges expose a representative color; avoid scanning large selections.
		return ranges.size() == 1 && ranges.get(0).isSingleCell();
	}

	@Override
	protected void doSetValue(GColor value) {
		styling.setBackgroundColor(value, ranges);
	}

	@Override
	public @Nullable GColor getValue() {
		if (ranges.isEmpty()) {
			return null;
		}
		TabularRange lastRange = ranges.get(ranges.size() - 1);
		return styling.getBackgroundColor(
				lastRange.getFromRow(), lastRange.getFromColumn(), styling.getDefaultBackgroundColor());
	}
}
