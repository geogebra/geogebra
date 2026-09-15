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

package org.geogebra.web.full.gui.toolbarpanel;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValuesStatisticsViewModel;
import org.geogebra.common.gui.view.table.TableValuesStatisticsViewModel.Content;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.states.State;
import org.geogebra.common.util.AttributedString;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.sideSheet.ComponentSideSheet;
import org.geogebra.web.full.gui.components.sideSheet.SideSheetData;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Panel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class StatsSideSheetTV {
	private final AppW app;
	private FlowPanel statPanel;
	private final ComponentSideSheet sideSheet;
	private State.Subscription dataUpdateRegistration;

	/**
	 * @param app application
	 * @param data side sheet data
	 */
	public StatsSideSheetTV(AppW app, SideSheetData data) {
		this.app = app;
		sideSheet = new ComponentSideSheet(app, data);
		sideSheet.addStyleName("statistics");
		sideSheet.addAttachHandler(evt -> {
			if (!evt.isAttached()) {
				cancelRegistration();
			}
		});
	}

	private void buildSideSheet(String subTitle) {
		Label subTitleLabel = BaseWidgetFactory.INSTANCE.newPrimaryText("", "subTitle");
		subTitleLabel.getElement().setInnerHTML(subTitle);
		sideSheet.addToContent(subTitleLabel);
	}

	/**
	 * @param rowData row data
	 */
	public void setRowsAndShow(State<List<StatisticGroup>> rowData) {
		cancelRegistration();
		setRows(rowData.get());
		this.dataUpdateRegistration = rowData.subscribe(this::setRows);
		sideSheet.show();
	}

	private void cancelRegistration() {
		if (dataUpdateRegistration != null) {
			dataUpdateRegistration.cancel();
		}
	}

	private void setRows(List<StatisticGroup> statistics) {
		if (statPanel != null) {
			statPanel.removeFromParent();
		}
		this.statPanel = new FlowPanel();
		renderGroups(statistics, app, statPanel);
		sideSheet.addToContent(statPanel);
	}

	/**
	 * Convert statistics data into UI elements and add them to a panel.
	 * @param statistics statistics result
	 * @param app app
	 * @param parent parent panel
	 */
	public static void renderGroups(List<StatisticGroup> statistics, AppW app, Panel parent) {
		for (StatisticGroup row: statistics) {
			FlowPanel group = new FlowPanel();
			group.addStyleName("group");

			Label heading = BaseWidgetFactory.INSTANCE.newSecondaryText(
					row.heading(), "heading");
			group.add(heading);

			for (String value: row.values()) {
				if (row.isLaTeX()) {
					Canvas canvas = Canvas.createIfSupported();
					((DrawEquationW) app.getDrawEquation()).paintOnCleanCanvas(
							value, canvas, 16,
							GColor.newColor(0, 0, 0, 0.87), false);
					group.add(canvas);
				} else {
					Label valueLbl = BaseWidgetFactory.INSTANCE.newPrimaryText(value, "value");
					group.add(valueLbl);
				}
			}
			parent.add(group);
		}
	}

	/**
	 * Add regression UI and show
	 * @param plotActionHandler callback to plot the selected regression curve
	 */
	public void addRegressionChooser(TableValuesStatisticsViewModel model,
			@NonNull State<@NonNull List<StatisticGroup>> groups,
			@NonNull State<@NonNull List<String>> models,
			@Nullable Runnable plotActionHandler) {
		List<String> items = models.get();

		ComponentDropDown regressionChooser = new ComponentDropDown(app,
				app.getLocalization().getMenu("RegressionModel"), items, 0);
		regressionChooser.setFullWidth(true);
		regressionChooser.addChangeHandler(() -> {
			model.selectedRegressionIndexChanged(regressionChooser.getSelectedIndex());
			setRows(groups.get());
		});

		sideSheet.addToContent(regressionChooser);

		if (plotActionHandler != null) {
			sideSheet.addPositiveButtonRunnable(plotActionHandler);
		}
		setRowsAndShow(groups);
	}

	/**
	 * Add error panel and show.
	 * @param errorMessage error message to show
	 */
	public void showError(String errorMessage) {
		cancelRegistration();
		sideSheet.addStyleName("error");
		InfoErrorData errorData = new InfoErrorData(
				app.getLocalization().getMenu("StatsDialog.NoData"),
				errorMessage, null, MaterialDesignResources.INSTANCE.bar_chart_black());
		ComponentInfoErrorPanel infoPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				errorData, null);
		sideSheet.addToContent(infoPanel);
		sideSheet.show();
	}

	/**
	 * @param content view content; if null, view will be closed
	 * @param model table stats model
	 */
	public void update(Content content, TableValuesStatisticsViewModel model) {
		if (content instanceof Content.Statistics stats) {
			showStatisticsDialog(content.title(), content.header(), stats.groups());
		} else if (content instanceof Content.Regression regression) {
			showRegressionDialog(content.title(), content.header(),
					model,
					regression.groups(),
					regression.regressionModels(), regression.plotAction());
		} else if (content instanceof Content.Error err) {
			showErrorDialog(err.title(), err.header(), err.message());
		} else { // null or invalid
			sideSheet.close();
		}
	}

	private void showStatisticsDialog(@NonNull String title, @NonNull AttributedString header,
			@NonNull State<List<StatisticGroup>> statisticGroups) {
		SideSheetData sideSheetData = new SideSheetData(title, null, null);
		sideSheet.update(sideSheetData);
		buildSideSheet(TableUtil.toHtml(header));
		setRowsAndShow(statisticGroups);
	}

	private void showRegressionDialog(@NonNull String title,
			@NonNull AttributedString header,
			TableValuesStatisticsViewModel model,
			@NonNull State<@NonNull List<StatisticGroup>> groups,
			@NonNull State<@NonNull List<String>> models, @Nullable Runnable plotActionHandler) {
		SideSheetData sideSheetData = new SideSheetData(title, null,
				plotActionHandler != null ? "Plot" : null);
		sideSheet.update(sideSheetData);
		buildSideSheet(TableUtil.toHtml(header));
		addRegressionChooser(model, groups, models, plotActionHandler);
	}

	private void showErrorDialog(@NonNull String title, @NonNull AttributedString header,
			@NonNull String errorMessage) {
		SideSheetData sideSheetData = new SideSheetData(title, null, null);
		sideSheet.update(sideSheetData);
		buildSideSheet(TableUtil.toHtml(header));
		showError(errorMessage);
	}
}
