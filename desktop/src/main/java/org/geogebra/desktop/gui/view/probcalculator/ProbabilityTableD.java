package org.geogebra.desktop.gui.view.probcalculator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityTable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.desktop.gui.view.data.StatTable;
import org.geogebra.desktop.main.AppD;

public class ProbabilityTableD extends ProbabilityTable
		implements ListSelectionListener {

	private StatTable statTable;

	private JPanel wrappedPanel;

	public ProbabilityTableD(AppD app, ProbabilityCalculatorViewD probCalc) {
		super(app, probCalc);

		this.wrappedPanel = new JPanel();

		wrappedPanel.setLayout(new BorderLayout());
		statTable = new StatTable(app);
		statTable.getTable()
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		statTable.getTable().setColumnSelectionAllowed(false);
		statTable.getTable().setRowSelectionAllowed(true);
		statTable.getTable().getSelectionModel().addListSelectionListener(this);
		statTable.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		statTable.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		// blank table
		setTable(null, null, 0, 10);

		wrappedPanel.add(statTable, BorderLayout.CENTER);

		statTable.getTable()
				.setPreferredScrollableViewportSize(new Dimension(125, 10));
		wrappedPanel.setMinimumSize(statTable.getPreferredSize());

	}

	@Override
	public void setTable(Dist distType, GeoNumberValue[] parms, int xMin, int xMax) {

		setIniting(true);

		this.setTableModel(distType, parms, xMin, xMax);

		statTable.setStatTable(xMax - xMin + 1, null, 2, getColumnNames());

		DefaultTableModel model = statTable.getModel();
		int x = xMin;
		int row = 0;

		// set the table model with the prob. values for this distribution
		double prob;
		while (x <= xMax) {

			model.setValueAt("" + x, row, 0);
			if (distType != null) {
				prob = getProbManager().probability(x, parms, distType,
						isCumulative());
				model.setValueAt("" + getProbCalc().format(prob), row, 1);
			}
			x++;
			row++;
		}

		updateFonts(((AppD) getApp()).getPlainFont());

		// need to get focus so that the table will finish resizing columns (not
		// sure why)
		statTable.getTable().requestFocus();
		setIniting(false);
	}

	public void updateFonts(Font font) {
		statTable.updateFonts(font);
		statTable.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		statTable.autoFitColumnWidth(0, 3);
		statTable.autoFitColumnWidth(1, 3);
		statTable.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		int w = statTable.getTable().getColumnModel().getTotalColumnWidth();
		statTable.getTable()
				.setPreferredScrollableViewportSize(new Dimension(w + 10, 10));
		wrappedPanel.setMinimumSize(statTable.getPreferredSize());

	}



	@Override
	public void valueChanged(ListSelectionEvent e) {

		JTable table = statTable.getTable();

		int[] selRow = table.getSelectedRows();

		// exit if initing or nothing selected
		if (isIniting() || selRow.length == 0) {
			return;
		}

		if (getProbCalc()
				.getProbMode() == ProbabilityCalculatorView.PROB_INTERVAL) {
			// System.out.println(Arrays.toString(selectedRow));
			String lowStr = (String) table.getModel().getValueAt(selRow[0], 0);
			String highStr = (String) table.getModel()
					.getValueAt(selRow[selRow.length - 1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			// System.out.println(low + " , " + high);
			getProbCalc().setInterval(low, high);
		} else if (getProbCalc()
				.getProbMode() == ProbabilityCalculatorView.PROB_LEFT) {
			String lowStr = (String) statTable.getTable().getModel()
					.getValueAt(0, 0);
			String highStr = (String) statTable.getTable().getModel()
					.getValueAt(selRow[selRow.length - 1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			// System.out.println(low + " , " + high);
			getProbCalc().setInterval(low, high);

			// adjust the selection
			table.getSelectionModel().removeListSelectionListener(this);
			if (isCumulative()) {
				// single row selected
				table.changeSelection(selRow[selRow.length - 1], 0, false,
						false);
			} else {
				// select multiple rows: first up to selected
				table.changeSelection(0, 0, false, false);
				table.changeSelection(selRow[selRow.length - 1], 0, false,
						true);
				table.scrollRectToVisible(
						table.getCellRect(selRow[selRow.length - 1], 0, true));
			}
			table.getSelectionModel().addListSelectionListener(this);
		} else if (getProbCalc()
				.getProbMode() == ProbabilityCalculatorView.PROB_RIGHT) {
			String lowStr = (String) statTable.getTable().getModel()
					.getValueAt(selRow[0], 0);
			int maxRow = statTable.getTable().getRowCount() - 1;
			String highStr = (String) statTable.getTable().getModel()
					.getValueAt(maxRow, 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			// System.out.println(low + " , " + high);
			((ProbabilityCalculatorViewD) getProbCalc()).setInterval(low, high);

			table.getSelectionModel().removeListSelectionListener(this);
			table.changeSelection(maxRow, 0, false, false);
			table.changeSelection(selRow[0], 0, false, true);
			// table.scrollRectToVisible(table.getCellRect(selRow[0], 0, true));
			table.getSelectionModel().addListSelectionListener(this);

		}

	}

	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {

		// if(!probManager.isDiscrete(distType))
		// return;

		try {
			statTable.getTable().getSelectionModel()
					.removeListSelectionListener(this);

			int lowIndex = lowValue - getXMin();
			if (lowIndex < 0) {
				lowIndex = 0;
			}
			int highIndex = highValue - getXMin();
			// System.out.println("-------------");
			// System.out.println(lowIndex + " , " + highIndex);

			if (isCumulative()) {
				statTable.getTable().changeSelection(highIndex, 0, false,
						false);
			} else {
				statTable.getTable().changeSelection(lowIndex, 0, false, false);
				statTable.getTable().changeSelection(highIndex, 0, false, true);
			}
			wrappedPanel.repaint();
			statTable.getTable().getSelectionModel()
					.addListSelectionListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the wrapped panel
	 */
	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

}
