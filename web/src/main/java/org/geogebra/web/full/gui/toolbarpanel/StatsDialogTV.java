package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.table.RegressionSpecification;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.web.full.gui.components.CompDropDown;
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
	public void addRegressionChooserHasError(List<RegressionSpecification> available,
			List<StatisticGroup> initialRegression) {
		List<String> items = new ArrayList<>();
		available.forEach(spec -> items.add(app.getLocalization().getMenu(spec.getLabel())));

		CompDropDown regressionChooser = new CompDropDown((AppW) app,
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
			view.plotRegression(column, regression);
		});
		setRowsAndShow(initialRegression);
	}
}
