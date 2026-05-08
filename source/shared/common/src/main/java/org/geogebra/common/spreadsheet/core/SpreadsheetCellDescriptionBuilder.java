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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.Localization;

/**
 * Builds spreadsheet cell descriptions for accessibility output.
 */
public final class SpreadsheetCellDescriptionBuilder {

	private final @Nonnull TabularData<?> tabularData;
	private final @CheckForNull Localization localization;

	/**
	 * @param tabularData Spreadsheet data
	 * @param localization Localization used for accessibility descriptions
	 */
	public SpreadsheetCellDescriptionBuilder(@Nonnull TabularData<?> tabularData,
			@CheckForNull Localization localization) {
		this.tabularData = tabularData;
		this.localization = localization;
	}

	/**
	 * @param row Row index
	 * @param column Column index
	 * @return Description of the cell for accessibility output.
	 */
	@Nonnull String getCellDescription(int row, int column) {
		ScreenReaderBuilder description = new ScreenReaderBuilder(localization);
		appendCellValue(description, row, column);
		if (tabularData.hasFormulaAt(row, column)) {
			appendMenuDefault(description, "Spreadsheet.HasFormula", "Has formula");
		}
		append(description, tabularData.getCellName(row, column));
		return description.toString().trim();
	}

	/**
	 * @param content The textual content of the cell editor.
	 * @return Description of the cell editor content for accessibility output.<br>
	 * Makes sure to communicate to the user if a cell is empty.
	 */
	@Nonnull String getEditorDescription(@Nonnull String content) {
		ScreenReaderBuilder description = new ScreenReaderBuilder(localization);
		if (!content.isEmpty()) {
			append(description, content);
		} else {
			appendMenuDefault(description, "Spreadsheet.EmptyCell", "Empty cell");
		}
		return description.toString().trim();
	}

	/**
	 * @return Description for attempts to move beyond the spreadsheet boundary.
	 */
	@Nonnull String getNoMoreCellsDescription() {
		ScreenReaderBuilder description = new ScreenReaderBuilder(localization);
		appendMenuDefault(description, "Spreadsheet.NoMoreCells", "No more cells.");
		return description.toString().trim();
	}

	private void appendCellValue(ScreenReaderBuilder description, int row, int column) {
		if (tabularData.hasError(row, column)) {
			append(description, tabularData.getErrorString());
			return;
		}
		Object content = tabularData.contentAt(row, column);
		if (content != null) {
			if (content instanceof GeoElement geo) {
				append(description, geo.toValueString(StringTemplate.defaultTemplate));
				return;
			}
			append(description, content.toString());
		}
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
