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

import java.util.List;

import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.states.DerivedState;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.geogebra.common.util.AttributedString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.j2objc.annotations.Weak;

/**
 * A reactive table values statistics view model that updates the UI state
 * when the underlying table data changes.
 */
public final class TableValuesStatisticsViewModel implements TableValuesListener {
	private final @Weak @NonNull TableValues tableValues;
	private final @NonNull Localization localization;
	private final @NonNull MutableState<@Nullable Content> content = new MutableState<>(null);
	private @Nullable MutableState<@NonNull List<StatisticGroup>> statisticsGroups;
	private @Nullable MutableState<@NonNull List<RegressionSpecification>> regressionSpecifications;
	private @Nullable MutableState<@NonNull Integer> selectedRegressionIndex;
	private @Nullable MutableState<@NonNull List<StatisticGroup>> regressionGroups;
	private @Nullable GeoEvaluatable selectedEvaluatable;
	private @Nullable Mode mode;
	private @Nullable RegressionSpecification selectedRegressionSpecification;

	/** Content rendered by Table Values statistics sheets. */
	public sealed interface Content {
		/** @return localized sheet title */
		@NonNull String title();

		/** @return attributed data header */
		@NonNull AttributedString header();

		/** Statistics state with its calculated groups. */
		record Statistics(@NonNull String title, @NonNull AttributedString header,
				@NonNull State<@NonNull List<StatisticGroup>> groups)
				implements Content { }

		/** Regression state with the available specifications and selected result. */
		record Regression(@NonNull String title, @NonNull AttributedString header,
				@NonNull State<@NonNull List<String>> regressionModels,
				@NonNull State<@NonNull Integer> selectedRegressionIndex,
				@NonNull State<@NonNull List<StatisticGroup>> groups,
				@Nullable Runnable plotAction) implements Content { }

		/** State shown when the selected data cannot produce statistics. */
		record Error(@NonNull String title, @NonNull AttributedString header,
				@NonNull String message) implements Content { }
	}

	/** Statistics sheet modes. */
	public enum Mode {
		ONE_VARIABLE, TWO_VARIABLE, REGRESSION
	}

	/**
	 * Constructs the view model.
	 * @param tableValues table of values supplying the statistics
	 * @param localization localization used for sheet labels
	 */
	public TableValuesStatisticsViewModel(@NonNull TableValues tableValues,
			@NonNull Localization localization) {
		this.tableValues = tableValues;
		this.localization = localization;
	}

	/** Opens a statistics sheet for a column. */
	public void show(@NonNull Mode mode, int columnIndex) {
		GeoEvaluatable evaluatable = tableValues.getEvaluatable(columnIndex);
		if (evaluatable == null) {
			return;
		}
		tableValues.getTableValuesModel().unregisterListener(this);
		this.mode = mode;
		selectedEvaluatable = evaluatable;
		selectedRegressionSpecification = null;
		tableValues.getTableValuesModel().registerListener(this);
		rebuildContent();
	}

	/** @return the content of the statistics view, or {@code null} when the sheet is closed */
	public @NonNull State<@Nullable Content> getContent() {
		return content;
	}

	/** Selects a regression specification by its index in the current state. */
	public void selectedRegressionIndexChanged(int regressionIndex) {
		if (regressionSpecifications == null) {
			return;
		}
		selectedRegressionSpecification = regressionSpecifications.get().get(regressionIndex);
		refreshContent();
	}

	/** Closes the sheet and stops observing Table Values changes. */
	public void close() {
		tableValues.getTableValuesModel().unregisterListener(this);
		mode = null;
		selectedEvaluatable = null;
		selectedRegressionSpecification = null;
		statisticsGroups = null;
		regressionSpecifications = null;
		selectedRegressionIndex = null;
		regressionGroups = null;
		content.set(null);
	}

	private void rebuildContent() {
		statisticsGroups = null;
		regressionSpecifications = null;
		selectedRegressionIndex = null;
		regressionGroups = null;
		refreshContent();
	}

	private void refreshContent() {
		int columnIndex = tableValues.getColumn(selectedEvaluatable);
		if (columnIndex < 0 || mode != Mode.ONE_VARIABLE && columnIndex == 0) {
			close();
			return;
		}
		switch (mode) {
			case ONE_VARIABLE -> updateVariableContent(columnIndex, false);
			case TWO_VARIABLE -> updateVariableContent(columnIndex, true);
			case REGRESSION -> updateRegressionContent(columnIndex);
		}
	}

	private void updateVariableContent(int columnIndex, boolean twoVariable) {
		AttributedString header = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), columnIndex, twoVariable, localization);
		List<StatisticGroup> groups = twoVariable ? tableValues.getStatistics2Var(columnIndex)
				: tableValues.getStatistics1Var(columnIndex);
		String title = localization.getMenu(twoVariable ? "2VariableStatistics"
				: "1VariableStatistics");
		String noDataMessage = twoVariable ? "StatsDialog.NoDataMsg2VarStats"
				: "StatsDialog.NoDataMsg1VarStats";
		if (groups.isEmpty()) {
			setErrorContent(title, header, localization.getMenu(noDataMessage));
		} else if (statisticsGroups != null) {
			statisticsGroups.set(List.copyOf(groups));
		} else {
			statisticsGroups = new MutableState<>(List.copyOf(groups));
			content.set(new Content.Statistics(title, header, statisticsGroups));
		}
	}

	private void updateRegressionContent(int columnIndex) {
		AttributedString header = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), columnIndex, false, localization);
		String title = localization.getMenu("Regression");
		List<RegressionSpecification> specifications =
				tableValues.getRegressionSpecifications(columnIndex);
		if (specifications.isEmpty()) {
			setErrorContent(title, header, localization.getMenu("StatsDialog.NoDataMsgRegression"));
			return;
		}
		int selectedIndex = Math.max(0, specifications.indexOf(selectedRegressionSpecification));
		RegressionSpecification specification = specifications.get(selectedIndex);
		selectedRegressionSpecification = specification;
		List<StatisticGroup> groups = tableValues.getRegression(columnIndex, specification);
		boolean canPlot = specifications.stream().allMatch(RegressionSpecification::canPlot);
		if (regressionSpecifications != null) {
			if (selectedRegressionIndex.get() < specifications.size()) {
				regressionSpecifications.set(specifications);
				selectedRegressionIndex.set(selectedIndex);
			} else {
				selectedRegressionIndex.set(selectedIndex);
				regressionSpecifications.set(specifications);
			}
			regressionGroups.set(List.copyOf(groups));
		} else {
			regressionSpecifications = new MutableState<>(specifications);
			selectedRegressionIndex = new MutableState<>(selectedIndex);
			regressionGroups = new MutableState<>(List.copyOf(groups));
			content.set(new Content.Regression(title, header, DerivedState.of(
					regressionSpecifications, specs -> specs.stream()
							.map(spec -> localization.getMenu(spec.getLabel())).toList()),
					selectedRegressionIndex, regressionGroups,
					canPlot ? this::plotRegression : null));
		}
	}

	private void setErrorContent(String title, AttributedString header, String message) {
		statisticsGroups = null;
		regressionSpecifications = null;
		selectedRegressionIndex = null;
		regressionGroups = null;
		content.set(new Content.Error(title, header, message));
	}

	private void plotRegression() {
		if (selectedEvaluatable == null || selectedRegressionSpecification == null) {
			return;
		}
		tableValues.plotRegression(tableValues.getColumn(selectedEvaluatable),
				selectedRegressionSpecification);
	}

	private boolean isRelevantColumn(int column) {
		return selectedEvaluatable != null && (column == tableValues.getColumn(selectedEvaluatable)
				|| mode != Mode.ONE_VARIABLE && column == 0);
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		if (evaluatable == selectedEvaluatable) {
			close();
		} else if (mode != Mode.ONE_VARIABLE && column == 0) {
			refreshContent();
		}
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		if (isRelevantColumn(column)) {
			refreshContent();
		}
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		// no-op
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		if (isRelevantColumn(column)) {
			rebuildContent();
		}
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		if (isRelevantColumn(column)) {
			refreshContent();
		}
	}

	@Override
	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		refreshContent();
	}

	@Override
	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		refreshContent();
	}

	@Override
	public void notifyRowChanged(TableValuesModel model, int row) {
		if (mode != Mode.ONE_VARIABLE || tableValues.getColumn(selectedEvaluatable) == 0) {
			refreshContent();
		}
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		refreshContent();
	}
}
