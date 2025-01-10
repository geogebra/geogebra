package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityTable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.web.full.gui.view.data.StatTableW;
import org.geogebra.web.full.gui.view.data.StatTableW.StatDataTable;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Probability table for Web
 * @author gabor
 *
 */
public class ProbabilityTableW extends ProbabilityTable implements ClickHandler {

	private final FlowPanel wrappedPanel;
	private final StatTableW statTable;

	/**
	 * @param app Application
	 * @param probCalc ProbabilityCalculator
	 */
	public ProbabilityTableW(App app, ProbabilityCalculatorViewW probCalc) {
		super(app, probCalc);

		this.wrappedPanel = new FlowPanel();
		this.wrappedPanel.addStyleName("ProbabilityTableW");

		statTable = new StatTableW();
		statTable.getTable().addClickHandler(this);
		wrappedPanel.add(statTable);

		//blank table
		setTable(null, null, 0, 10);
	}
	
	@Override
	public void setTable(Dist distType, GeoNumberValue[] params, int xMin, int xMax) {

		setIniting(true);

		setTableModel(distType, params, xMin, xMax);
		
		statTable.setStatTable(xMax - xMin + 1, null, 2, getColumnNames());

		// set the table model with the prob. values for this distribution
		fillRows(distType, params, xMin, xMax);
		setIniting(false);
	}

	@Override
	protected void setRowValues(int row, String k, String prob) {
		statTable.setValueAt(k, row, 0);
		statTable.setValueAt(prob, row, 1);
	}

	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		// TODO reuse desktop?

		int lowIndex = lowValue - getXMin();
		if (lowIndex < 0) {
			lowIndex = 0;
		}
		int highIndex = highValue - getXMin();

		if (isCumulative()) {
			statTable.getTable().changeSelection(highIndex, false, false);
		} else {
			statTable.getTable().changeSelection(lowIndex, false, false);
			statTable.getTable().changeSelection(highIndex, false, true);
		}
	}

	@Override
	public void setTwoTailedSelection(int lowValue, int highValue) {
		int lowIndex = lowValue - getXMin();
		if (lowIndex < 0) {
			lowIndex = 0;
		}
		int highIndex = highValue - getXMin();

		if (isCumulative()) {
			statTable.getTable().changeSelection(highIndex, false, false);
		} else {
			statTable.getTable().setTailSelection(lowIndex, highIndex);

		}
	}
	
	/**
	 * @return UI component
	 */
	public FlowPanel getWrappedPanel() {
		return wrappedPanel;
	}
	
	/**
	 * @return stats table
	 */
	public StatTableW getStatTable() {
		return statTable;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		StatDataTable table = statTable.getTable();
		
		table.handleSelection(event);

		int[] selRow = table.getSelectedRows();

		// exit if initing or nothing selected
		if (isIniting() || selRow.length == 0) {
			return;
		}

		if (getProbCalc()
				.getProbMode() == ProbabilityCalculatorView.PROB_INTERVAL) {
			String lowStr = table.getText(selRow[0], 0);
			String highStr = table.getText(selRow[selRow.length - 1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			getProbCalc().setInterval(low, high);
		} else if (getProbCalc()
				.getProbMode() == ProbabilityCalculatorView.PROB_LEFT) {
			String lowStr = statTable.getTable().getText(1, 0);
			String highStr = statTable.getTable()
					.getText(selRow[selRow.length - 1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			getProbCalc().setInterval(low, high);

			// adjust the selection
			// table.getSelectionModel().removeListSelectionListener(this);
			if (isCumulative()) {
				// single row selected
				table.changeSelection(selRow[selRow.length - 1], false, false);
			} else {
				// select multiple rows: first up to selected
				table.changeSelection(0, false, false);
				table.changeSelection(selRow[selRow.length - 1], false, true);
			}
			// table.getSelectionModel().addListSelectionListener(this);
		} else if (getProbCalc()
				.getProbMode() == ProbabilityCalculatorView.PROB_RIGHT) {
			String lowStr = statTable.getTable().getText(selRow[0], 0);
			int maxRow = statTable.getTable().getRowCount() - 1;
			String highStr = statTable.getTable().getText(maxRow, 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			getProbCalc().setInterval(low, high);

			// table.getSelectionModel().removeListSelectionListener(this);
			table.changeSelection(maxRow, false, false);
			table.changeSelection(selRow[0], false, true);
		}
	}
}
