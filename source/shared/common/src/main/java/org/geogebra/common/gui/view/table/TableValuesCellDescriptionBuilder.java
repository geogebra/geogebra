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

package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Builds table of values cell descriptions for accessibility output.
 */
public final class TableValuesCellDescriptionBuilder {

	private final @NonNull TableValuesModel tableValuesModel;
	private final @Nullable Localization localization;

	/**
	 * @param tableValuesModel Table values data
	 * @param localization Localization used for accessibility descriptions
	 */
	public TableValuesCellDescriptionBuilder(
			@NonNull TableValuesModel tableValuesModel, @Nullable Localization localization) {
		this.tableValuesModel = tableValuesModel;
		this.localization = localization;
	}

	/**
	 * @param row Row index
	 * @param column Column index
	 * @return Description of the cell for accessibility output.
	 */
	public @NonNull String getCellDescription(int row, int column) {
		ScreenReaderBuilder description = new ScreenReaderBuilder(localization);
		TableValuesCell cell = hasCellAt(row, column) ? tableValuesModel.getCellAt(row, column) : null;
		appendCellValue(description, cell);
		appendError(description, cell);
		return description.toString().trim();
	}

	private void appendCellValue(ScreenReaderBuilder description, @Nullable TableValuesCell cell) {
		if (cell == null || StringUtil.isTrimmedEmpty(cell.getInput())) {
			appendMenuDefault(description, "Spreadsheet.EmptyCell", "Empty cell");
		} else {
			append(description, cell.getInput());
		}
	}

	private void appendError(ScreenReaderBuilder description, @Nullable TableValuesCell cell) {
		if (cell != null && cell.isErroneous()) {
			append(description, localization.getError("Error"));
			appendMenuDefault(description, "UseNumbersOnly", "Use numbers only");
		}
	}

	private boolean hasCellAt(int row, int column) {
		return row < tableValuesModel.getRowCount() && column < tableValuesModel.getColumnCount();
	}

	private void append(ScreenReaderBuilder description, String part) {
		if (part.isEmpty()) {
			return;
		}
		description.append(part);
		description.endSentence();
	}

	private void appendMenuDefault(ScreenReaderBuilder description, String key, String fallback) {
		description.appendMenuDefault(key, fallback);
		description.endSentence();
	}
}
