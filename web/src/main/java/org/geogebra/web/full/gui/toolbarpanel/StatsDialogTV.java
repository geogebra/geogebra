package org.geogebra.web.full.gui.toolbarpanel;

import java.util.List;
import java.util.function.Function;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class StatsDialogTV extends ComponentDialog {

	private final int column;
	private final TableValuesView view;
	private FlowPanel statPanel;

	/**
	 * @param app application
	 * @param view table view
	 * @param column column
	 */
	public StatsDialogTV(AppW app, TableValuesView view, int column) {
		super(app, new DialogData("Statistics", "OK", null), false, true);
		this.column = column;
		this.view = view;
		
	}

	/**
	 * @param statFunction row data producer
	 */
	public void updateContent(Function<Integer, List<StatisticGroup>> statFunction) {
		((AppW) app).getAsyncManager().scheduleCallback(() -> {
				setRows(statFunction.apply(column));
				show();
		});
	}

	private void setRows(List<StatisticGroup> statistics) {
		if (statPanel != null) {
			statPanel.removeFromParent();
		}
		this.statPanel = new FlowPanel();
		for (StatisticGroup row: statistics) {
			Label heading = new Label(row.getHeading());
			heading.getElement().getStyle().setColor(StringUtil.toHtmlColor(GColor.LIGHT_GRAY));
			heading.getElement().getStyle().setFontSize(.7, Style.Unit.EM);
			heading.getElement().getStyle().setMarginTop(.5, Style.Unit.EM);
			statPanel.add(heading);
			for (String value: row.getValues()) {
				if (row.isLaTeX()) {
					Canvas canvas = DrawEquationW.paintOnCanvas((AppW) app,
							value, null, 16,
							GColor.newColor(102, 102, 102), false);
					statPanel.add(canvas);
				} else {
					statPanel.add(new Label(value));
				}
			}
		}
		addDialogContent(statPanel);
	}

	/**
	 * Add regression UI and show
	 */
	public void addRegressionChooserAndShow() {
		ListBox regressionChooser = new ListBox();
		for (Regression regression: Regression.values()) {
			if (regression == Regression.NONE) {
				continue;
			}
			regressionChooser.addItem(app.getLocalization().getMenu(regression.getLabel()),
					regression.name());
		}
		regressionChooser.addChangeHandler((change) -> {
			Regression regression = Regression.valueOf(regressionChooser.getSelectedValue());
			setRows(view.getRegression(column, regression, 3));
		});
		addDialogContent(regressionChooser);
		StandardButton plot = new StandardButton("Plot");
		plot.addFastClickHandler((ignore) -> {
			Regression regression = Regression.valueOf(regressionChooser.getSelectedValue());
			view.plotRegression(column, regression, 3);
		});
		addDialogContent(plot);
		plot.addStyleName("dialogContainedButton");
		updateContent(c -> view.getRegression(c, Regression.LINEAR, 1));
	}
}
