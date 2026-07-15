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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.contextmenu.TableValuesContextMenuActionHandler.PlotActionHandler;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.sideSheet.ComponentSideSheet;
import org.geogebra.web.full.gui.components.sideSheet.SideSheetData;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class StatsSideSheetTV extends FlowPanel {
	private final AppW app;
	private ComponentSideSheet sideSheet;
	private FlowPanel statPanel;

	/**
	 * @param app application
	 * @param data side sheet data
	 * @param subTitle sub-title
	 */
	public StatsSideSheetTV(AppW app, SideSheetData data, String subTitle) {
		super();
		this.app = app;
		buildSideSheet(data, subTitle);
		addStyleName("statistics");
	}

	private void buildSideSheet(SideSheetData data, String subTitle) {
		sideSheet = new ComponentSideSheet(app, data, this::close);
		Label subTitleLabel = BaseWidgetFactory.INSTANCE.newPrimaryText("", "subTitle");
		subTitleLabel.getElement().setInnerHTML(subTitle);
		sideSheet.addToContent(subTitleLabel);
		add(sideSheet);
	}

	/**
	 * @param rowData row data
	 */
	public void setRowsAndShow(List<StatisticGroup> rowData) {
		setRows(rowData);
		show();
	}

	private void setRows(List<StatisticGroup> statistics) {
		if (statPanel != null) {
			statPanel.removeFromParent();
		}
		this.statPanel = new FlowPanel();
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
			statPanel.add(group);
		}
		sideSheet.addToContent(statPanel);
	}

	private void show() {
		app.getAppletFrame().add(this);
		addStyleName("floatingSettings animateIn");
	}

	/**
	 * Close and remove side sheet after closing animation done.
	 */
	public void close() {
		removeStyleName("animateIn");
		addStyleName("animateOut");
		CSSEvents.runOnAnimation(() -> app.getAppletFrame().remove(this),
				getElement(), "animateOut");
	}

	/**
	 * Add regression UI and show
	 * @param regressionGroups map from regression specification to its statistic groups
	 * @param plotActionHandler callback to plot the selected regression curve
	 */
	public void addRegressionChooser(
			Map<RegressionSpecification, List<StatisticGroup>> regressionGroups,
			@CheckForNull PlotActionHandler plotActionHandler) {
		List<RegressionSpecification> available = new ArrayList<>(regressionGroups.keySet());
		List<String> items = new ArrayList<>();
		available.forEach(spec -> items.add(app.getLocalization().getMenu(spec.getLabel())));

		ComponentDropDown regressionChooser = new ComponentDropDown(app,
				app.getLocalization().getMenu("RegressionModel"), items, 0);
		regressionChooser.setFullWidth(true);
		regressionChooser.addChangeHandler(() -> {
			RegressionSpecification regression = available
					.get(regressionChooser.getSelectedIndex());
			setRows(regressionGroups.get(regression));
		});

		sideSheet.addToContent(regressionChooser);

		if (plotActionHandler != null) {
			sideSheet.addPositiveButtonRunnable(() -> plotActionHandler.onPlotButtonPressed(
					available.get(regressionChooser.getSelectedIndex())));
		}
		setRowsAndShow(regressionGroups.get(available.get(0)));
	}

	/**
	 * Add error panel and show.
	 * @param errorMessage error message to show
	 */
	public void showError(String errorMessage) {
		addStyleName("error");
		InfoErrorData errorData = new InfoErrorData(
				app.getLocalization().getMenu("StatsDialog.NoData"),
				errorMessage, null, MaterialDesignResources.INSTANCE.bar_chart_black());
		ComponentInfoErrorPanel infoPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				errorData, null);
		sideSheet.addToContent(infoPanel);
		show();
	}
}
