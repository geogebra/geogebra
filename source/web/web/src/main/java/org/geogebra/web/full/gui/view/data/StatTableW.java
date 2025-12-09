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

package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.util.debug.Log;
import org.gwtproject.dom.client.Element;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * @author gabor
 * 
 *         Stattable for web
 *
 */
public class StatTableW extends FlowPanel {

	private StatDataTable statDataTable;

	/**
	 * Create new stats table
	 */
	public StatTableW() {
		initTable();
	}

	private void initTable() {
		statDataTable = new StatDataTable();
		this.add(statDataTable);

		// coloring and things here with css....
	}

	/**
	 * Sets the dimensions and header values for the table. This should only be
	 * called once.
	 * 
	 * @param rows
	 *            number of rows
	 * @param rowNames
	 *            array of row header strings, if null then a row header is not
	 *            drawn
	 * @param columns
	 *            number of columns
	 * @param columnNames
	 *            array of column header strings, if null then a column header
	 *            is not drawn
	 */
	public void setStatTable(int rows, String[] rowNames, int columns,
			String[] columnNames) {

		statDataTable.resize(rows, columns);
		// set column names
		statDataTable.setHeaderCells(columnNames);

		// create row header
		if (rowNames != null) {
			// rowHeader = new MyRowHeader(myTable, rowNames, this);
			// rowHeaderModel = new DefaultListModel();
			// .setModel(rowHeaderModel);
			for (int i = 0; i < rowNames.length; i++) {
				statDataTable.setWidget(i, 0, new Label(rowNames[i]));
			}
		} else {
			// setRowHeaderView(null);
		}
	}

	/**
	 * Sets all cells values to the blank string " ". Does not change table
	 * dimensions.
	 * 
	 * AG: Why does this needed?
	 */
	@Override
	public void clear() {
		for (int r = 0; r < statDataTable.getRowCount(); r++) {
			for (int c = 0; c < statDataTable.getColumnCount(); c++) {
				statDataTable.setWidget(r, c, new Label(" "));
			}
		}
	}

	/**
	 * @param rowNames
	 *            row names
	 * @param columnNames
	 *            column names
	 */
	public void setLabels(String[] rowNames, String[] columnNames) {
		setLabels(rowNames, columnNames, true);
	}

	/**
	 * @param rowNames
	 *            row names
	 * @param columnNames
	 *            column names
	 * @param hasHeader
	 *            whether to include header
	 */
	public void setLabels(String[] rowNames, String[] columnNames,
			boolean hasHeader) {
		// set column names
		if (columnNames != null && rowNames != null) {
			statDataTable.resize(rowNames.length + 1, columnNames.length + 1);
		}
		try {
			if (columnNames != null) {
				for (int i = 0; i < columnNames.length; i++) {
					statDataTable.setWidget(0, i + 1, new Label(columnNames[i]));
				}
			}

			int startRow = hasHeader ? 1 : 0;
			if (rowNames != null) {
				for (int i = 0; i < rowNames.length; i++) {
					statDataTable.setWidget(startRow + i, 0, new Label(rowNames[i]));
				}
			}
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	/**
	 * @author gabor
	 * 
	 *         Table for StatTable
	 *
	 */
	public static class StatDataTable extends Grid /* do it with CellTable later */ {

		private int firstRow = 0;

		/**
		 * @param columnNames
		 *            column names
		 */
		public void setHeaderCells(String[] columnNames) {
			if (columnNames != null) {
				firstRow = 0;
				resizeRows(getRowCount() + 1);
				for (int i = 0; i < columnNames.length; i++) {
					this.setWidget(0, i, new Label(columnNames[i]));
					this.getCellFormatter().getElement(0, i)
							.addClassName("headercell");
				}
				firstRow = 1;
			}
		}

		/**
		 * Update selection after click
		 * 
		 * @param event
		 *            click event
		 */
		public void handleSelection(ClickEvent event) {
			Cell c = this.getCellForEvent(event);
			Element parentRow = c.getElement().getParentElement();
			if (c.getElement().hasClassName("headercell")) {
				parentRow.removeClassName("selected");
				return;
			}

			if (!event.isShiftKeyDown()) {
				toggleSelection(parentRow);
				if (isSelected(parentRow)) {
					if (!(isSelected(parentRow.getPreviousSiblingElement())
							|| isSelected(parentRow.getNextSiblingElement()))) {
						clearSelection(c);
					}
				} else {
					clearSelectionFrom(c);
				}
			} else {
				selectTableRows(getFirstSelectedRow(-1), c.getRowIndex());
			}
		}

		private static boolean isSelected(Element row) {
			return row != null && row.hasClassName("selected");
		}

		private static void toggleSelection(Element parentRow) {
			if (parentRow.hasClassName("selected")) {
				parentRow.removeClassName("selected");
			} else {
				parentRow.addClassName("selected");
			}
		}

		private void clearSelectionFrom(Cell c) {
			if (c != null) {
				for (int i = c.getRowIndex(); i < this.getRowCount(); i++) {
					getRowFormatter().getElement(i).removeClassName("selected");
				}
			}
		}

		private void clearSelection(Cell c) {
			for (int i = firstRow; i < this.getRowCount(); i++) {
				if (c != null) {
					if (c.getRowIndex() != i) {
						getRowFormatter().getElement(i)
								.removeClassName("selected");
					}
				} else {
					getRowFormatter().getElement(i).removeClassName("selected");
				}
			}
		}

		/**
		 * @return array of selected rows
		 */
		public int[] getSelectedRows() {
			int start = firstRow;
			int end = 0;
			int[] result;
			for (int i = firstRow; i < this.getRowCount(); i++) {
				if (this.getRowFormatter().getElement(i)
						.hasClassName("selected")) {
					if (end == 0) {
						start = i;
					}
					end++;
				}
			}
			result = new int[end];
			for (int i = 0; i < end; i++) {
				result[i] = start + i;
			}

			return result;
		}

		private int getFirstSelectedRow(int to) {
			int t = to > -1 ? to : this.getRowCount();
			for (int i = firstRow; i < this.getRowCount(); i++) {
				if (this.getRowFormatter().getElement(i)
						.hasClassName("selected") && i <= t) {
					return i;
				}
			}
			return -1;

		}

		private void selectTableRows(int rowFrom, int rowTo) {
			int maxRows = getRowCount() - 1;
			int from = Math.min(rowFrom > -1 ? rowFrom : firstRow,
					maxRows);
			int to = Math.min(Math.max(0, rowTo), maxRows);

			if (from > to) {
				int tmp = from;
				from = to;
				to = tmp;
			}

			selectCells(from, to, getRowFormatter());
		}

		private void selectCells(int from, int to, RowFormatter rowFormatter) {
			for (int i = from; i <= to; i++) {
				rowFormatter.getElement(i).addClassName("selected");
			}
		}

		/**
		 * @param row
		 *            selected row
		 * @param toggle
		 *            when false, this is no-op
		 * @param extend
		 *            whether to keep old selection
		 */
		public void changeSelection(int row, boolean toggle, boolean extend) {
			int start;
			int r = row + firstRow;
			if (r < getRowCount()) {
				if (!toggle && !extend) {
					clearSelection(null);
					this.getRowFormatter().getElement(r)
							.addClassName("selected");
				} else if (!toggle && extend) {
					start = getFirstSelectedRow(r);
					if (start > -1) {
						selectTableRows(start, r);
					}
				}
			}
		}

		/**
		 * @param row
		 *            row
		 * @param column
		 *            column
		 * @param widget
		 *            widget
		 */
		@Override
		public void setWidget(int row, int column, Widget widget) {
			super.setWidget(row + firstRow, column, widget);
		}

		/**
		 * Change cell content.
		 * 
		 * @param value
		 *            cell content
		 * @param row
		 *            row
		 * @param col
		 *            column
		 */
		public void setValueAt(String value, int row, int col) {
			setWidget(row, col, new Label(value));
		}

		/**
		 * @param lowIndex select from beginning to.
		 * @param highIndex select from to the end.
		 */
		public void setTailSelection(int lowIndex, int highIndex) {
			clearSelection(null);
			int start = Math.min(lowIndex, highIndex) + 1;
			int end = Math.max(lowIndex, highIndex) + 1;
			selectTableRows(1, start);
			selectTableRows(end, numRows - 1);
		}
	}

	/**
	 * Change cell content.
	 * 
	 * @param value
	 *            cell content
	 * @param row
	 *            row
	 * @param column
	 *            column
	 */
	public void setValueAt(String value, int row, int column) {
		statDataTable.setValueAt(value, row, column);
	}

	/**
	 * @return wrapped table
	 */
	public StatDataTable getTable() {
		return statDataTable;
	}

}
