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

import javax.annotation.Nonnull;

/**
 * Show an auto-updating spreadsheet statistics view in the UI (sidebar).
 * <p>
 * <em>Design Notes</em>
 * <p>
 * This interface exists mostly to decouple the spreadsheet core from its
 * surrounding/embedding UI, because the statistics views are hosted outside the spreadsheet
 * view.
 */
public interface SpreadsheetStatisticsDelegate {

	/**
	 * Show one-variable statistics in the UI.
	 * @param statisticsView An auto-updating spreadsheet statistics view.
	 */
	void showOneVarStatistics(@Nonnull SpreadsheetStatisticsView.OneVar statisticsView);

	/**
	 * Show two-variable statistics in the UI.
	 * @param statisticsView An auto-updating spreadsheet statistics view.
	 */
	void showTwoVarStatistics(@Nonnull SpreadsheetStatisticsView.TwoVar statisticsView);

	/**
	 * Show regression metrics in the UI.
	 * @param statisticsView An auto-updating spreadsheet statistics view.
	 */
	void showRegression(@Nonnull SpreadsheetStatisticsView.Regression statisticsView);
}
