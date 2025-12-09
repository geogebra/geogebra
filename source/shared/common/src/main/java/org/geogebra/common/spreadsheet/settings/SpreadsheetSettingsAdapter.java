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

package org.geogebra.common.spreadsheet.settings;

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.spreadsheet.core.CellSizes;
import org.geogebra.common.spreadsheet.core.Spreadsheet;

/**
 * Synchronizes cell size and styling info between the {@code Spreadsheet} / {@code TableLayout}
 * and the {@code SpreadsheetSettings}.
 */
public final class SpreadsheetSettingsAdapter {

	private final App app;
	private final Spreadsheet spreadsheet;
	private String previousCellFormatXml;

	/**
	 * @param spreadsheet the spreadsheet
	 * @param app the app
	 */
	public SpreadsheetSettingsAdapter(@Nonnull Spreadsheet spreadsheet, @Nonnull App app) {
		this.spreadsheet = spreadsheet;
		this.app = app;
	}

	/**
	 * Register the adapter as a listener on both the SpreadsheetSettings and the Spreadsheet.
	 */
	public void registerListeners() {
		// careful: the SpreadsheetSettings instance may change at runtime, don't store a reference!
		SpreadsheetSettings spreadsheetSettings = app.getSettings().getSpreadsheet();
		// OK: the SpreadsheetSettings listeners are carried over when a new instance is created
		spreadsheetSettings.addListener((settings) -> {
			notifyIfSettingsCellFormatChanged();
		});
		spreadsheet.tabularDataDimensionsDidChange(spreadsheetSettings);
		previousCellFormatXml = spreadsheetSettings.getCellFormatXml();
		spreadsheet.setCellFormatXml(previousCellFormatXml);

		spreadsheet.cellSizesChanged.addListener(this::spreadsheetCellSizesDidChange);
		spreadsheet.cellFormatXmlChanged.addListener(this::spreadsheetCellFormatDidChange);
	}

	/**
	 * @return the current cell format XML from the {@link SpreadsheetSettings}.
	 */
	private @CheckForNull String getCellFormatXml() {
		SpreadsheetSettings spreadsheetSettings = app.getSettings().getSpreadsheet();
		return spreadsheetSettings.getCellFormatXml();
	}

	/**
	 * Sync SpreadsheetSettings style changes -> Spreadsheet (SpreadsheetStyling)
	 */
	private void notifyIfSettingsCellFormatChanged() {
		String newCellFormatXml = getCellFormatXml();
		if (Objects.equals(previousCellFormatXml, newCellFormatXml)) {
			return;
		}
		spreadsheet.setCellFormatXml(newCellFormatXml);
		previousCellFormatXml = newCellFormatXml;
	}

	/**
	 * Sync Spreadsheet/TableLayout cell size changes -> SpreadsheetSettings
	 * @param cellSizes cell size info
	 */
	private void spreadsheetCellSizesDidChange(@CheckForNull CellSizes cellSizes) {
		if (cellSizes == null) {
			return;
		}
		SpreadsheetSettings spreadsheetSettings = app.getSettings().getSpreadsheet();
		spreadsheetSettings.setCellSizesNoFire(cellSizes.customColumnWidths,
				cellSizes.customRowHeights);
	}

	/**
	 * Sync Spreadsheet/SpreadsheetStyleBarModel styling changes -> SpreadsheetSettings
	 * @param cellFormatXml cell styling info in the XML format expected by the
	 * {@link SpreadsheetSettings}.
	 */
	private void spreadsheetCellFormatDidChange(@CheckForNull String cellFormatXml) {
		SpreadsheetSettings spreadsheetSettings = app.getSettings().getSpreadsheet();
		spreadsheetSettings.setCellFormatXml(cellFormatXml);
		previousCellFormatXml = cellFormatXml;
	}
}
