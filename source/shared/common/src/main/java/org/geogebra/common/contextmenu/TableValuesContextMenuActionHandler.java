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

package org.geogebra.common.contextmenu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AttributedString;

import com.google.j2objc.annotations.Weak;

/** Action handler for table values context menu items. */
public final class TableValuesContextMenuActionHandler {
	private final int columnIndex;
	private final TableValues tableValues;
	private final App app;
	private final Localization localization;
	private final @Weak Delegate delegate;

	/** Delegate interface to perform UI-related and platform-specific operations. */
	public interface Delegate {
		/** Opens the dialog to create table value items. */
		void showTableValuesDialog();

		/**
		 * Opens and focuses the given element in the algebra view for editing.
		 * @param geoElement the element for focus in the algebra view
		 */
		void startEditingAlgebraViewItem(GeoElement geoElement);

		/** Starts the process of importing data into the table values. */
		void startDataImport();

		/**
		 * Opens the statistics dialog with the computed 1- or 2-variable statistics.
		 * @param title dialog title
		 * @param header column label
		 * @param statisticGroups list of statistic groups to display
		 */
		void showStatisticsDialog(@Nonnull String title, @Nonnull AttributedString header,
				@Nonnull List<StatisticGroup> statisticGroups);

		/**
		 * Opens the regression dialog. {@code plotActionHandler} is a callback invoked when the
		 * user plots the selected regression curve on the graph.
		 * @param title dialog title
		 * @param header column label
		 * @param regressionGroups map from regression specification to its statistic groups;
		 * keys populate a dropdown, and selecting one shows the corresponding statistic groups
		 * @param plotActionHandler callback to plot the selected regression curve when the
		 * "Plot" button is pressed or {@code null} when no "Plot" button is shown
		 */
		void showRegressionDialog(@Nonnull String title, @Nonnull AttributedString header,
				@Nonnull Map<RegressionSpecification, List<StatisticGroup>> regressionGroups,
				@CheckForNull PlotActionHandler plotActionHandler);

		/**
		 * Opens an error dialog when statistics or regression cannot be computed.
		 * @param title dialog title
		 * @param header column label
		 * @param errorMessage human-readable error description
		 */
		void showErrorDialog(@Nonnull String title, @Nonnull AttributedString header,
				@Nonnull String errorMessage);
	}

	/** Callback to be invoked when the user presses the "Plot" button in the regression view. */
	@FunctionalInterface
	public interface PlotActionHandler {
		/**
		 * Called when the user presses the "Plot" button.
		 * @param selectedRegressionSpecification the regression specification selected by the user
		 */
		void onPlotButtonPressed(@Nonnull RegressionSpecification selectedRegressionSpecification);
	}

	/**
	 * Constructs the action handler for table values context menu items.
	 * @param columnIndex the index of the column for which the context menu was open
	 * @param tableValues table of values
	 * @param app the active {@link App}
	 * @param delegate the delegate for the platform-specific operations
	 */
	public TableValuesContextMenuActionHandler(int columnIndex, @Nonnull TableValues tableValues,
			@Nonnull App app, @Nonnull Localization localization, @Nonnull Delegate delegate) {
		this.columnIndex = columnIndex;
		this.tableValues = tableValues;
		this.app = app;
		this.localization = localization;
		this.delegate = delegate;
	}

	/**
	 * Perform the action for the selected context menu item.
	 * @param selectedItem the selected context menu item
	 */
	public void handleSelectedItem(@Nonnull TableValuesContextMenuItem selectedItem) {
		switch (selectedItem.getItem()) {
			case Edit -> edit();
			case ClearColumn -> clearColumn();
			case RemoveColumn -> removeColumn();
			case ShowPoints -> setPointsVisibility(true);
			case HidePoints -> setPointsVisibility(false);
			case ImportData -> delegate.startDataImport();
			case Statistics1 -> oneVariableStatistics();
			case Statistics2 -> twoVariableStatistics();
			case Regression -> app.getAsyncManager().scheduleCallback(this::regression);
			case Separator -> {
			}
		}
	}

	private void edit() {
		if (columnIndex == 0) {
			delegate.showTableValuesDialog();
		} else {
			delegate.startEditingAlgebraViewItem((GeoElement)
					tableValues.getEvaluatable(columnIndex));
		}
	}

	private void clearColumn() {
		tableValues.getTableValuesModel().startBatchUpdate();
		tableValues.clearValues();
		tableValues.getTableValuesModel().endBatchUpdate(true);
	}

	private void removeColumn() {
		GeoEvaluatable column = tableValues.getEvaluatable(columnIndex);
		tableValues.hideColumn(column);
		if (!column.isGeoList()) {
			app.dispatchEvent(new Event(EventType.REMOVE_TV, (GeoElement) column));
		}
	}

	private void setPointsVisibility(boolean visible) {
		app.getGuiManager().getTableValuesPoints().setPointsVisible(columnIndex, visible);
		app.dispatchEvent(new Event(EventType.SHOW_POINTS_TV).setJsonArgument(
				Map.of("column", columnIndex, "show", visible)));
	}

	private void oneVariableStatistics() {
		AttributedString header = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), columnIndex, false, localization);
		List<StatisticGroup> statisticGroups = tableValues.getStatistics1Var(columnIndex);
		String title = localization.getMenu("1VariableStatistics");
		if (statisticGroups.isEmpty()) {
			delegate.showErrorDialog(title, header, localization
					.getMenu("StatsDialog.NoDataMsg1VarStats"));
		} else {
			delegate.showStatisticsDialog(title, header, statisticGroups);
		}
	}

	private void twoVariableStatistics() {
		AttributedString header = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), columnIndex, true, localization);
		List<StatisticGroup> groups = tableValues.getStatistics2Var(columnIndex);
		String title = localization.getMenu("2VariableStatistics");
		if (groups.isEmpty()) {
			delegate.showErrorDialog(title, header, localization
					.getMenu("StatsDialog.NoDataMsg2VarStats"));
		} else {
			delegate.showStatisticsDialog(title, header, groups);
		}
	}

	private void regression() {
		List<RegressionSpecification> regressionSpecifications =
				tableValues.getRegressionSpecifications(columnIndex);
		AttributedString header = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), columnIndex, false, localization);
		if (regressionSpecifications.isEmpty()) {
			delegate.showErrorDialog(localization.getMenu("Regression"), header,
					localization.getMenu("StatsDialog.NoDataMsgRegression"));
			return;
		}
		Map<RegressionSpecification, List<StatisticGroup>> statisticGroups =
				regressionSpecifications.stream().collect(Collectors.toMap(
						regressionSpecification -> regressionSpecification,
						regressionSpecification -> tableValues.getRegression(
								columnIndex, regressionSpecification),
						(existing, replacement) -> existing, LinkedHashMap::new));
		boolean canPlot = regressionSpecifications.stream()
				.allMatch(RegressionSpecification::canPlot);
		delegate.showRegressionDialog(localization.getMenu("Regression"), header, statisticGroups,
				canPlot ? selectedRegressionSpecification -> tableValues.plotRegression(
						columnIndex, selectedRegressionSpecification) : null);
	}
}
