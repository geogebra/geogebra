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
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetReferenceParsing;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Result;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.components.sideSheet.ComponentSideSheet;
import org.geogebra.web.full.gui.components.sideSheet.SideSheetData;
import org.geogebra.web.full.gui.toolbarpanel.StatsSideSheetTV;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.jspecify.annotations.NonNull;

public class SpreadsheetStatisticsDelegateW implements SpreadsheetStatisticsView.Delegate {
	private final AppW app;
	private final Spreadsheet<?> spreadsheet;
	private ComponentSideSheet sideSheet;
	private final FlowPanel inputPanel;
	private final FlowPanel outputPanel;
	private ComponentInputField xRange;
	private ComponentInputField yRange;

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
		if (statisticsView instanceof SpreadsheetStatisticsView.OneVar oneVarStatistics) {
			showOneVarStatistics(oneVarStatistics);
		} else if (statisticsView instanceof SpreadsheetStatisticsView.TwoVar twoVarStatistics) {
			showTwoVarStatistics(twoVarStatistics);
		} else if (statisticsView instanceof SpreadsheetStatisticsView.Regression regression) {
			showRegression(regression);
		} else if (sideSheet != null) {
			sideSheet.close();
			sideSheet = null;
		}
	}

	private void showOneVarStatistics(SpreadsheetStatisticsView.@NonNull OneVar statisticsView) {
		inputPanel.clear();
		xRange = new ComponentInputField(
				app, null, "Statistics.DataRange", null,
				rangeToString(statisticsView.getInput().cellRange()));
		inputPanel.add(xRange);
		Consumer<String> update = (input) -> {
			statisticsView.setInput(new SpreadsheetStatistics.Input.OneVarInput(
					SpreadsheetReferenceParsing.parseReference(input)
			));
			statisticsView.commitInput();
			validateInputs();
			fillContent(statisticsView.getResult());
		};
		xRange.addEnterHandler(update);
		showSideSheet(statisticsView, null);
	}

	private void showTwoVarStatistics(SpreadsheetStatisticsView.@NonNull TwoVar twoVarStatistics) {
		inputPanel.clear();
		xRange = new ComponentInputField(
				app, null, "Statistics.XDataRange", null,
				rangeToString(twoVarStatistics.getInput().cellRangeX()));
		inputPanel.add(xRange);
		yRange = new ComponentInputField(
				app, null, "Statistics.YDataRange", null,
				rangeToString(twoVarStatistics.getInput().cellRangeY()));
		inputPanel.add(yRange);
		Consumer<String> update = (ignore) -> {
			twoVarStatistics.setInput(new SpreadsheetStatistics.Input.TwoVarInput(
					SpreadsheetReferenceParsing.parseReference(xRange.getText()),
					SpreadsheetReferenceParsing.parseReference(yRange.getText()))
			);
			twoVarStatistics.commitInput();
			validateInputs();
			fillContent(twoVarStatistics.getResult());
		};
		xRange.addEnterHandler(update);
		yRange.addEnterHandler(update);
		showSideSheet(twoVarStatistics, null);
	}

	private void showRegression(SpreadsheetStatisticsView.@NonNull Regression regression) {
		inputPanel.clear();
		xRange = new ComponentInputField(
				app, null, "Statistics.XDataRange", null,
				rangeToString(regression.getInput().cellRangeX()));
		inputPanel.add(xRange);
		yRange = new ComponentInputField(
				app, null, "Statistics.YDataRange", null,
				rangeToString(regression.getInput().cellRangeY()));
		inputPanel.add(yRange);
		List<RegressionSpecification> specs = regression.getRegressionSpecifications();
		List<String> items = new ArrayList<>();
		specs.forEach(spec -> items.add(app.getLocalization().getMenu(spec.getLabel())));

		ComponentDropDown regressionChooser = new ComponentDropDown(app,
				app.getLocalization().getMenu("RegressionModel"), items, 0);
		regressionChooser.setFullWidth(true);
		Runnable update = () -> {
			regression.setInput(new SpreadsheetStatistics.Input.RegressionInput(
					SpreadsheetReferenceParsing.parseReference(xRange.getText()),
					SpreadsheetReferenceParsing.parseReference(yRange.getText()),
					specs.get(regressionChooser.getSelectedIndex())
			));
			regression.commitInput();
			validateInputs();
			fillContent(regression.getResult());
		};
		regressionChooser.addChangeHandler(update);
		xRange.addEnterHandler(ignore -> update.run());
		yRange.addEnterHandler(ignore -> update.run());
		inputPanel.add(regressionChooser);
		showSideSheet(regression, "Plot");
		sideSheet.addPositiveButtonRunnable(regression::plotResult);
	}

	private void validateInputs() {
		for (ComponentInputField inputField: List.of(xRange, yRange)) {
			if (inputField != null) {
				SpreadsheetReference parsed = SpreadsheetReferenceParsing.parseReference(
								inputField.getText());
				String message = parsed == null || parsed.isSingleCell()
						? app.getLocalization().getMenu("Statistics.Error.EnterValidRange")
						: null;
				inputField.setError(message);
			}
		}
	}

	private ComponentInputField getFirstInvalidRange(Result.Invalid invalid) {
		return invalid.dataRange() == SpreadsheetStatistics.DataRange.Y
				? yRange : xRange;
	}

	private void showSideSheet(SpreadsheetStatisticsView<?> statisticsView,
			String positiveButtonKey) {
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
		if (statisticsView.getResult() instanceof Result.Invalid invalid) {
			ComponentInputField firstInvalidRange = getFirstInvalidRange(invalid);
			if (firstInvalidRange != null) {
				firstInvalidRange.focusDeferred();
			} else {
				validateInputs();
			}
		}
		statisticsView.setChangeListener(this::fillContent);
		sideSheet.show();
	}

	private void fillContent(Result result) {
		outputPanel.clear();
		if (result instanceof Result.Valid valid) {
			StatsSideSheetTV.renderGroups(valid.statisticGroups(), app, outputPanel);
		} else if (result instanceof Result.Invalid invalid) {
			outputPanel.add(new ComponentInfoErrorPanel(app.getLocalization(),
					new InfoErrorData("Error.Error", invalid.error().localizationKey), null));
		}
	}

	private String rangeToString(SpreadsheetReference reference) {
		return reference == null ? "" : reference.toString();
	}
}
