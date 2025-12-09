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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class StatsDialogTV extends ComponentDialog {

	private final int column;
	private final TableValuesView view;
	private FlowPanel statPanel;

	/**
	 * @param app application
	 * @param view table view
	 * @param column column
	 * @param data dialog data
	 */
	public StatsDialogTV(AppW app, TableValuesView view, int column, DialogData data) {
		super(app, data, true, true);
		addStyleName("statistics");
		this.column = column;
		this.view = view;
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
					row.getHeading(), "heading");
			group.add(heading);

			for (String value: row.getValues()) {
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
		addDialogContent(statPanel);
	}

	/**
	 * Add regression UI and show
	 * @param initialRegression pre-selected (linear) regression
	 */
	public void addRegressionChooser(List<RegressionSpecification> available,
			List<StatisticGroup> initialRegression) {
		List<String> items = new ArrayList<>();
		available.forEach(spec -> items.add(app.getLocalization().getMenu(spec.getLabel())));

		ComponentDropDown regressionChooser = new ComponentDropDown((AppW) app,
				app.getLocalization().getMenu("RegressionModel"), items, 0);
		regressionChooser.setFullWidth(true);
		regressionChooser.addChangeHandler(() -> {
			RegressionSpecification regression = available
					.get(regressionChooser.getSelectedIndex());
			setRows(view.getRegression(column, regression));
		});

		addDialogContent(regressionChooser);

		setOnPositiveAction(() -> {
			RegressionSpecification regression = available
					.get(regressionChooser.getSelectedIndex());
			if (regression.canPlot()) {
				view.plotRegression(column, regression);
			}
		});
		setRowsAndShow(initialRegression);
	}
}
