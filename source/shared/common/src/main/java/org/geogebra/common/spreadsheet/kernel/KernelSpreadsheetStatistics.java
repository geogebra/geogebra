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

package org.geogebra.common.spreadsheet.kernel;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.gui.view.table.regression.RegressionSpecificationBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.spreadsheet.core.TabularRange;

/**
 * An adapter between the {@code Kernel} and {@code Spreadsheet} that provides statistics
 * calculations to the spreadsheet.
 */
public final class KernelSpreadsheetStatistics implements SpreadsheetStatistics {

	private final Kernel kernel;
	private final StatisticGroupsBuilder statisticGroupsBuilder;
	private final RegressionSpecificationBuilder regressionSpecificationBuilder;

	/**
	 * Creates a kernel / spreadsheet statistics adapter.
	 * @param kernel the kernel
	 */
	public KernelSpreadsheetStatistics(@Nonnull Kernel kernel) {
		this.kernel = kernel;
		this.statisticGroupsBuilder = kernel.getStatisticGroupsBuilder();
		this.regressionSpecificationBuilder = kernel.getApplication().getRegressionSpecBuilder();
	}

	@Override
	public @Nonnull SpreadsheetStatisticsView.OneVar getOneVarStatistics(
			@Nonnull TabularRange range) {
		return new KernelOneVarSpreadsheetStatisticsView(kernel, statisticGroupsBuilder, range);
	}

	@Override
	public @Nonnull SpreadsheetStatisticsView.TwoVar getTwoVarStatistics(
			@Nonnull TabularRange range) {
		return new KernelTwoVarSpreadsheetStatisticsView(kernel, statisticGroupsBuilder, range);
	}

	@Override
	public @Nonnull SpreadsheetStatisticsView.Regression getRegression(
			@Nonnull TabularRange range) {
		return new KernelSpreadsheetRegressionView(kernel, statisticGroupsBuilder,
				regressionSpecificationBuilder, range);
	}
}
