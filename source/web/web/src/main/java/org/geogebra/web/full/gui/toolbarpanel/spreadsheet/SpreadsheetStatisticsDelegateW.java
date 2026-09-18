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

package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.main.Localization;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetReferenceParsing;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Grouping;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Result;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.states.State;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.components.sideSheet.ComponentSideSheet;
import org.geogebra.web.full.gui.components.sideSheet.SideSheetData;
import org.geogebra.web.full.gui.dialog.ProcessInput;
import org.geogebra.web.full.gui.toolbarpanel.StatsSideSheetTV;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.Label;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class SpreadsheetStatisticsDelegateW implements SpreadsheetStatisticsView.Delegate {
	private final AppW app;
	private final Spreadsheet<?> spreadsheet;
	private ComponentSideSheet sideSheet;
	private final FlowPanel inputPanel;
	private final FlowPanel outputPanel;
	private ComponentInputField xRange;
	private @Nullable ComponentInputField yRange;
	private State.@Nullable Subscription visibilityCheck;

	/**
	 * @param app application
	 * @param spreadsheet spreadsheet
	 */
	public SpreadsheetStatisticsDelegateW(AppW app, Spreadsheet<?> spreadsheet) {
		this.app = app;
		this.spreadsheet = spreadsheet;
		inputPanel = new FlowPanel();
		outputPanel = new FlowPanel();
	}

	@Override
	public void statisticsViewChanged() {
		SpreadsheetStatisticsView<?> statisticsView = spreadsheet.getStatisticsView();
		if (visibilityCheck != null) {
			visibilityCheck.cancel();
		}
		if (statisticsView instanceof SpreadsheetStatisticsView.OneVar oneVarStatistics) {
			showOneVarStatistics(oneVarStatistics);
		} else if (statisticsView instanceof SpreadsheetStatisticsView.TwoVar twoVarStatistics) {
			showTwoVarStatistics(twoVarStatistics);
		} else if (statisticsView instanceof SpreadsheetStatisticsView.Regression regression) {
			showRegression(regression);
		} else if (statisticsView instanceof SpreadsheetStatisticsView.FrequencyTable table) {
			showTable(table);
		} else if (sideSheet != null) {
			sideSheet.close();
			sideSheet = null;
		}
	}

	private void showOneVarStatistics(SpreadsheetStatisticsView.@NonNull OneVar statisticsView) {
		inputPanel.clear();
		xRange = new ComponentInputField(
				app,
				null,
				"Statistics.DataRange",
				null,
				rangeToString(statisticsView.getInput().cellRange()));
		inputPanel.add(xRange);
		ProcessInput update = () -> statisticsView.setInput(new SpreadsheetStatistics.Input.OneVarInput(
				SpreadsheetReferenceParsing.parseReference(xRange.getText())));
		yRange = null;
		xRange.addInputHandler(update);
		addEnterHandlers(statisticsView);
		statisticsView.setInputChangeListener(input -> updateRangeInput(xRange, input.cellRange()));
		showSideSheet(statisticsView, null);
	}

	private void showTwoVarStatistics(SpreadsheetStatisticsView.@NonNull TwoVar twoVarStatistics) {
		inputPanel.clear();
		xRange = new ComponentInputField(
				app,
				null,
				"Statistics.XDataRange",
				null,
				rangeToString(twoVarStatistics.getInput().cellRangeX()));
		inputPanel.add(xRange);
		yRange = new ComponentInputField(
				app,
				null,
				"Statistics.YDataRange",
				null,
				rangeToString(twoVarStatistics.getInput().cellRangeY()));
		inputPanel.add(yRange);
		ProcessInput update =
				() -> twoVarStatistics.setInput(new SpreadsheetStatistics.Input.TwoVarInput(
						SpreadsheetReferenceParsing.parseReference(xRange.getText()),
						SpreadsheetReferenceParsing.parseReference(yRange.getText())));
		xRange.addInputHandler(update);
		yRange.addInputHandler(update);
		addEnterHandlers(twoVarStatistics);
		twoVarStatistics.setInputChangeListener(input -> {
			updateRangeInput(xRange, input.cellRangeX());
			updateRangeInput(yRange, input.cellRangeY());
		});
		showSideSheet(twoVarStatistics, null);
	}

	private void showRegression(SpreadsheetStatisticsView.@NonNull Regression regression) {
		inputPanel.clear();
		xRange = new ComponentInputField(
				app,
				null,
				"Statistics.XDataRange",
				null,
				rangeToString(regression.getInput().cellRangeX()));
		inputPanel.add(xRange);
		yRange = new ComponentInputField(
				app,
				null,
				"Statistics.YDataRange",
				null,
				rangeToString(regression.getInput().cellRangeY()));
		inputPanel.add(yRange);
		List<RegressionSpecification> specs = regression.getRegressionSpecifications();
		List<String> items = new ArrayList<>();
		specs.forEach(spec -> items.add(app.getLocalization().getMenu(spec.getLabel())));

		ComponentDropDown regressionChooser =
				new ComponentDropDown(app, app.getLocalization().getMenu("RegressionModel"), items, 0);
		regressionChooser.setFullWidth(true);
		ProcessInput update = () -> regression.setInput(new SpreadsheetStatistics.Input.RegressionInput(
				SpreadsheetReferenceParsing.parseReference(xRange.getText()),
				SpreadsheetReferenceParsing.parseReference(yRange.getText()),
				specs.get(regressionChooser.getSelectedIndex())));
		regressionChooser.addChangeHandler(() -> {
			update.onInput();
			commit(regression);
		});
		xRange.addInputHandler(update);
		yRange.addInputHandler(update);
		addEnterHandlers(regression);
		regression.setInputChangeListener(input -> {
			updateRangeInput(xRange, input.cellRangeX());
			updateRangeInput(yRange, input.cellRangeY());
		});
		inputPanel.add(regressionChooser);
		showSideSheet(regression, "Plot");
		sideSheet.addPositiveButtonRunnable(regression::plotResult);
	}

	private void showTable(SpreadsheetStatisticsView.@NonNull FrequencyTable frequencyTable) {
		inputPanel.clear();
		xRange = new ComponentInputField(
				app, null, "Data", null, rangeToString(frequencyTable.getInput().dataRange()));
		yRange = new ComponentInputField(
				app,
				null,
				"ClassBoundaries",
				null,
				rangeToString(frequencyTable.getInput().classesRange()));
		Localization loc = app.getLocalization();
		List<String> items = List.of(loc.getMenu("Values"), loc.getMenu("Intervals"));

		ComponentDropDown groupingChooser = new ComponentDropDown(app, "GroupBy", items, 0);

		groupingChooser.setFullWidth(true);
		Consumer<Boolean> updateWithCumulative =
				(flag) -> frequencyTable.setInput(new SpreadsheetStatistics.Input.FrequencyTableInput(
						SpreadsheetReferenceParsing.parseReference(xRange.getText()),
						SpreadsheetReferenceParsing.parseReference(yRange.getText()),
						Grouping.values()[groupingChooser.getSelectedIndex()],
						flag));
		ComponentCheckbox cumulative =
				new ComponentCheckbox(app.getLocalization(), false, "Cumulative", (checked) -> {
					updateWithCumulative.accept(checked);
					commit(frequencyTable);
				});
		ProcessInput update = () -> updateWithCumulative.accept(cumulative.isSelected());
		groupingChooser.addChangeHandler(() -> {
			yRange.setError(null);
			update.onInput();
			frequencyTable.commitInput();
			updateErrorsAndFocus(frequencyTable);
			fillContent(frequencyTable.getResult());
		});
		xRange.addInputHandler(update);
		yRange.addInputHandler(update);
		addEnterHandlers(frequencyTable);
		frequencyTable.setInputChangeListener(input -> {
			updateRangeInput(xRange, input.dataRange());
			updateRangeInput(yRange, input.classesRange());
		});
		yRange.setVisible(frequencyTable.getClassesVisible().get());
		visibilityCheck = frequencyTable.getClassesVisible().subscribe(yRange::setVisible);
		inputPanel.add(groupingChooser);
		inputPanel.add(xRange);
		inputPanel.add(yRange);
		inputPanel.add(cumulative);
		showSideSheet(frequencyTable, null);
	}

	private void addEnterHandlers(SpreadsheetStatisticsView<?> view) {
		for (ComponentInputField inputField : Arrays.asList(xRange, yRange)) {
			if (inputField != null) {
				inputField.addEnterHandler(ignore -> commit(view), true);
				inputField.addFocusHandler(ignore -> view.setFocusedDataRange(
						inputField == xRange
								? SpreadsheetStatistics.DataRange.X
								: SpreadsheetStatistics.DataRange.Y));
				inputField.addBlurHandler(ignore -> view.setFocusedDataRange(null));
			}
		}
	}

	private void commit(SpreadsheetStatisticsView<?> view) {
		view.commitInput();
		validateInputs();
		fillContent(view.getResult());
	}

	private void validateInputs() {
		for (ComponentInputField inputField : Arrays.asList(xRange, yRange)) {
			if (inputField != null) {
				SpreadsheetReference parsed =
						SpreadsheetReferenceParsing.parseReference(inputField.getText());
				String message = isValidStatisticReference(parsed)
						? null
						: app.getLocalization().getMenu("Statistics.Error.EnterValidRange");
				inputField.setError(message);
			}
		}
	}

	private static boolean isValidStatisticReference(SpreadsheetReference parsed) {
		return !(parsed == null || parsed.isSingleCell());
	}

	private ComponentInputField getFirstInvalidRange(Result.Invalid invalid) {
		return invalid.dataRange() == SpreadsheetStatistics.DataRange.Y ? yRange : xRange;
	}

	private void showSideSheet(
			SpreadsheetStatisticsView<?> statisticsView, String positiveButtonKey) {
		String titleKey = statisticsView.getTitleLocalizationKey();
		SideSheetData data = new SideSheetData(titleKey, null, positiveButtonKey);
		if (sideSheet == null) {
			sideSheet = new ComponentSideSheet(app, data);
			sideSheet.addAttachHandler(evt -> {
				if (!evt.isAttached()) {
					spreadsheet.closeStatisticsView();
				}
			});
			sideSheet.addStyleName("statistics");
		} else {
			sideSheet.update(data);
		}
		outputPanel.addStyleName("sideSheetStats");
		sideSheet.addToContent(inputPanel);
		sideSheet.addToContent(outputPanel);
		fillContent(statisticsView.getResult());
		updateErrorsAndFocus(statisticsView);
		statisticsView.setResultChangeListener(this::fillContent);
		Scheduler.get().scheduleDeferred(sideSheet::show);
	}

	private void updateErrorsAndFocus(SpreadsheetStatisticsView<?> statisticsView) {
		if (statisticsView.getResult() instanceof Result.Invalid invalid) {
			ComponentInputField firstInvalidRange = getFirstInvalidRange(invalid);
			if (firstInvalidRange != null) {
				firstInvalidRange.focusDeferred();
			} else {
				validateInputs();
			}
		} else {
			validateInputs();
		}
	}

	private void fillContent(Result result) {
		outputPanel.clear();
		if (result instanceof Result.GroupList valid) {
			StatsSideSheetTV.renderGroups(valid.statisticGroups(), app, outputPanel);
		} else if (result instanceof Result.Tabular tabular) {
			renderTabular(tabular.headings(), tabular.data(), outputPanel);
		} else if (result instanceof Result.Invalid invalid) {
			outputPanel.add(new ComponentInfoErrorPanel(
					app.getLocalization(),
					new InfoErrorData("Error.InvalidInput", invalid.error().localizationKey),
					null));
		}
	}

	private void renderTabular(
			List<String> headings, @NonNull List<List<String>> rows, FlowPanel outputPanel) {
		Grid resultGrid = new Grid(rows.size() + 1, headings.size());
		resultGrid.addStyleName("frequencyTable");
		for (int col = 0; col < headings.size(); col++) {
			Label header = BaseWidgetFactory.INSTANCE.newPrimaryText(headings.get(col), "cell");
			resultGrid.setWidget(0, col, header);
		}
		int rowIdx = 1;
		for (List<String> row : rows) {
			for (int col = 0; col < row.size(); col++) {
				Label cell = BaseWidgetFactory.INSTANCE.newPrimaryText(row.get(col), "cell");
				resultGrid.setWidget(rowIdx, col, cell);
			}
			rowIdx++;
		}
		outputPanel.add(resultGrid);
	}

	private void updateRangeInput(
			@Nullable ComponentInputField inputField, @Nullable SpreadsheetReference reference) {
		if (inputField != null
				&& reference != null
				&& !reference.toString().equals(inputField.getText())) {
			inputField.setInputText(reference.toString());
		}
	}

	private String rangeToString(SpreadsheetReference reference) {
		return reference == null ? "" : reference.toString();
	}
}
