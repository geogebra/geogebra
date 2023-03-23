package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class SelectionTableW extends Grid implements ClickHandler {
	private int selectedColumn = -1;
	private int selectedRow = -1;
	private SelectionTable mode;
	private int mNumRows;
	private int mNumColumns;
	private boolean isIniting = true;
	private ImageOrText[] values;

	/**
	 * @param data
	 *            images / texts for the table
	 * @param rows0
	 *            number of rows or -1 for auto
	 * @param columns0
	 *            number of columns or -1 for auto
	 * @param mode
	 *            image / text / latex
	 */
	public SelectionTableW(ImageOrText[] data, Integer rows0, Integer columns0,
			SelectionTable mode) {
		super();
		this.mode = mode;
		int rows = rows0;
		int columns = columns0;

		// =======================================
		// determine the dimensions of the table

		// rows = -1, cols = -1 ==> square table to fit data
		if (rows == -1 && columns == -1) {
			rows = (int) Math.floor(Math.sqrt(data.length));
			columns = (int) Math.ceil(data.length / (double) rows);
		}

		// rows = -1 ==> fixed cols, rows added to fit data
		else if (rows == -1) {
			rows = (int) (Math.ceil(data.length / (double) columns));
		}

		// cols = -1 ==> fixed rows, cols added to fit data
		else if (columns == -1) {
			columns = (int) (Math.ceil(data.length / (double) rows));
		}

		mNumRows = rows;
		mNumColumns = columns;
		resize(mNumRows, mNumColumns);

		// set the table model with the data
		populateModel(data);
		addClickHandler(this);
		addStyleName("SelectionTable");

		if (this.mode.equals(SelectionTable.MODE_ICON)) {
			setBorderStyleForCells();
		}
	}

	private void setBorderStyleForCells() {
		for (int i = 0; i < this.getRowCount(); i++) {
			for (int j = 0; j < this.getColumnCount(); j++) {
				Widget widget = this.getWidget(i, j);
				if (widget != null) {
					widget.addStyleName("border");
				}
			}
		}
	}

	private void changeSelection(int row, int column) {
		selectedRow = row;
		selectedColumn = column;
		clearSelectedCells();
		Widget w = getWidget(row, column);
		if (w != null) {
			w.addStyleName("selected");
		}
	}

	private void clearSelection() {
		selectedColumn = -1;
		selectedRow = 0;
		clearSelectedCells();
	}

	private void clearSelectedCells() {
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getCellCount(i); j++) {
				Widget w = getWidget(i, j);
				if (w != null) {
					w.removeStyleName("selected");
				}
			}
		}
	}

	/**
	 * @return selected index of the table
	 */
	public int getSelectedIndex() {
		int index = this.getColumnCount() * this.selectedRow
				+ this.selectedColumn;
		if (index < -1) {
			index = -1;
		}
		return index;
	}

	/**
	 * Sets the given index as selected. If the index is invalid, the selection is removed.
	 * @param index {@code int}
	 */
	public void setSelectedIndex(int index) {
		if (index < 0 || index >= getColumnCount() * getRowCount()) {
			this.clearSelection();
			return;
		}
		int row = index / getColumnCount();
		int column = index - (row * getColumnCount());
		this.changeSelection(row, column);
	}

	/**
	 * 
	 * @param index
	 *            {@code int}
	 * @param selected
	 *            whether it was selected or deselected
	 */
	public void changeMultiSelection(int index, boolean selected) {
		selectedRow = index / getColumnCount();
		selectedColumn = index - (selectedRow * getColumnCount());

		Widget w = getWidget(selectedRow, selectedColumn);
		if (w != null) {
			if (selected) {
				w.addStyleName("selected");
			} else {
				w.removeStyleName("selected");
			}
		}
	}

	/**
	 * @param data
	 *            {@link ImageOrText ImageOrText[]}
	 */
	public void populateModel(ImageOrText[] data) {
		values = data;
		if (data.length > 0) {
			populateModelCallback(data);
		}
	}

	private void populateModelCallback(ImageOrText[] data) {
		int r = 0;
		int c = 0;
		if (isIniting) {
			for (int i = 0; i < Math.min(data.length,
					this.mNumRows * this.mNumColumns); i++) {
				setWidget(r, c, createWidget(data[i]));
				++c;
				if (c == this.mNumColumns) {
					c = 0;
					++r;
				}
			}
			isIniting = false;
		} else if (mode != SelectionTable.MODE_TEXT) {
			for (int i = 0; i < Math.min(data.length,
					this.mNumRows * this.mNumColumns); i++) {
				if (getWidget(r, c) instanceof Label) {
					data[i].applyToLabel((Label) getWidget(r, c));

					++c;
					if (c == this.mNumColumns) {
						c = 0;
						++r;
					}
				}
			}
		}
	}

	private Widget createWidget(ImageOrText object) {

		Widget w = null;
		if (object == null) {
			return w;
		}
		switch (mode) {
		case MODE_TEXT:
		case MODE_ICON:
			w = new Label();
			object.applyToLabel((Label) w);
			break;
		default:
		case MODE_LATEX:
			break;
		}
		return w;
	}

	@Override
	public void onClick(ClickEvent event) {
		Cell clicked = getCellForEvent(event);

		if (clicked == null) {
			return;
		}

		selectedColumn = clicked.getCellIndex();
		selectedRow = clicked.getRowIndex();
		clearSelectedCells();
		Widget w = getWidget(clicked.getRowIndex(), clicked.getCellIndex());
		if (w != null) {
			w.addStyleName("selected");
		}
	}

	/**
	 * @return {@link ImageOrText}
	 */
	public ImageOrText getSelectedValue() {
		if (this.selectedRow != -1 && this.selectedColumn != -1) {
			return getValueAt(this.selectedRow, this.selectedColumn);
		}
		return null;
	}

	private ImageOrText getValueAt(int row, int column) {
		if (values == null || values.length <= row * this.mNumColumns + column) {
			return null;
		}
		return values[row * this.mNumColumns + column];
	}

	/**
	 * to update the text of the {@link ImageOrText}
	 * 
	 * @param data
	 *            texts or images
	 */
	public void updateText(ImageOrText[] data) {
		int r = 0;
		int c = 0;
		for (int i = 0; i < Math.min(data.length,
				this.mNumRows * this.mNumColumns); i++) {
			if (getWidget(r, c) instanceof Label) {
				((Label) getWidget(r, c)).setText(data[i].getText());

				++c;
				if (c == this.mNumColumns) {
					c = 0;
					++r;
				}
			}
		}
	}

	/**
	 * removes the default style of the cells
	 */
	public void removeDefaultStyle() {
		for (int i = 0; i < this.getRowCount(); i++) {
			for (int j = 0; j < this.getColumnCount(); j++) {
				this.getWidget(i, j).removeStyleName("border");
			}
		}
	}
}
