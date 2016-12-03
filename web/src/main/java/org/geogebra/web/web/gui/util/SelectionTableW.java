package org.geogebra.web.web.gui.util;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;



public class SelectionTableW extends Grid implements ClickHandler {
	private int selectedColumn = -1;
	private int selectedRow = -1;
	private SelectionTable mode;
	private int numRows, numColumns;
	private boolean isIniting = true;
	private ImageOrText[] values;
	private boolean isMultiselectionEnabled;
	private boolean[] selecteditems;

	/**
	 * @param data
	 * @param rows
	 * @param columns
	 * @param mode
	 */
	public SelectionTableW(ImageOrText[] data, Integer rows, Integer columns,
			SelectionTable mode, boolean ms) {
		this(data, rows, columns, mode);
		isMultiselectionEnabled = ms;
	}
	
	/**
	 * @param data
	 * @param rows
	 * @param columns
	 * @param mode
	 */
	public SelectionTableW(ImageOrText[] data, Integer rows, Integer columns,
	        org.geogebra.common.gui.util.SelectionTable mode) {
		super();
		this.mode = mode;

		//=======================================
		// determine the dimensions of the table

		// rows = -1, cols = -1  ==> square table to fit data
		if(rows == -1 && columns == -1){
			rows = (int) Math.floor(Math.sqrt(data.length));
			columns = (int) Math.ceil(data.length / (double) rows);
		}

		// rows = -1  ==> fixed cols, rows added to fit data
		else if(rows == -1){
			rows = (int) (Math.ceil(data.length / (double) columns));
		}

		// cols = -1 ==> fixed rows, cols added to fit data
		else if(columns == -1){
			columns = (int) (Math.ceil(data.length / (double) rows));
		}
		
		numRows = rows;
		numColumns = columns;
		resize(numRows,numColumns);
		
		// set the table model with the data
		populateModel(data);
		addClickHandler(this);
		addStyleName("SelectionTable");

		if (this.mode.equals(SelectionTable.MODE_ICON)) {
			setBorderStyleForCells();
		}
    }
	
	public void initSelectedItems(boolean[] si) {
		selecteditems = si;
		clearSelection();
		int row, column;
		for (int i = 0; i < selecteditems.length; i++) {
			if (selecteditems[i]) {
				row = i / getColumnCount();
				column = i - (row * getColumnCount());
				Widget w = getWidget(row, column);
				if (w != null) {
					w.addStyleName("selected");
				}
			}
		}
	}

	private void setBorderStyleForCells() {
		for (int i = 0; i < this.getRowCount(); i++) {
			for (int j = 0; j < this.getColumnCount(); j++) {
				this.getWidget(i, j).addStyleName("border");
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
	   selectedColumn = 0;
	   selectedRow = 0;
	   clearSelectedCells();
    }

	private void clearSelectedCells() {
	    for (int i = 0; i < getRowCount(); i++) {
	    	for (int j = 0; j < getCellCount(i); j++) {
	    		Widget w = getWidget(i,j);
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
		if(index <-1) index = -1;
		return index;	
    }

	/**
	 * sets the given index as selected. if {@code index = -1} the selection is
	 * removed.
	 * 
	 * @param index
	 *            {@code int}
	 */
	public void setSelectedIndex(int index){
		if(index == -1){
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
	    int r=0;
	    int c=0;
	    if (isIniting ) {
			for(int i=0; i < Math.min(data.length, this.numRows * this.numColumns); i++){
				setWidget(r, c, createWidget(data[i]));
				++c;
				if(c == this.numColumns){
					c = 0;
					++r;
				}
			}
			isIniting = false;
		} else if (mode != SelectionTable.MODE_TEXT) {
	    	for(int i=0; i < Math.min(data.length, this.numRows * this.numColumns); i++){
				if (getWidget(r, c) instanceof Label) {
					data[i].applyToLabel((Label) getWidget(r, c));

					++c;
					if(c == this.numColumns){
						c = 0;
						++r;
					}
				}
			}
	    }
    }

	private Widget createWidget(ImageOrText object) {
		
		Widget w = null;
		if(object == null){
			return w;
		}
		switch (mode) {
		case MODE_TEXT:
		case MODE_ICON:
			w = new Label();
			object.applyToLabel((Label)w);
			break;
		case MODE_LATEX:
			Log.debug("SelectionTable mode latex");
			break;
	  	}
		return w;
    }

	public void onClick(ClickEvent event) {
	   Cell clicked = getCellForEvent(event);
	   
	   if(clicked == null){
		   return;
	   }
	   
		selectedColumn = clicked.getCellIndex();
		selectedRow = clicked.getRowIndex();
		if (!isMultiselectionEnabled) {
			clearSelectedCells();
		}
	   Widget w = getWidget(clicked.getRowIndex(),clicked.getCellIndex());
	   if (w != null) {
			if (isMultiselectionEnabled) {
				// TODO check for -1 col.count.
				int index = (getColumnCount() == -1) ? selectedColumn
				        : selectedRow * getColumnCount() + selectedColumn;
				selecteditems[index] = !selecteditems[index];
				if (selecteditems[index]) {
					w.addStyleName("selected");
				} else {
					w.removeStyleName("selected");
				}
			} else {
				w.addStyleName("selected");
			}

	   }
    }

	/**
	 * @param index
	 * @return
	 * 
	 */
	public boolean isSelected(int index) {
		if (isMultiselectionEnabled) {
			return selecteditems[index];
		}
		return (index == getSelectedIndex());
	}

	/**
	 * @return {@link ImageOrText}
	 */
	public ImageOrText getSelectedValue() {
		if (this.selectedRow != -1 && this.selectedColumn != -1)
			return getValueAt(this.selectedRow, this.selectedColumn);
		return null;
    }

	private ImageOrText getValueAt(int row, int column) {
		if(values == null || values.length <= row * this.numColumns + column){
			return null;
		}
		return values[row * this.numColumns + column];
    }

	/**
	 * to update the text of the {@link ImageOrText}
	 * 
	 * @param data
	 */
	public void updateText(ImageOrText[] data) {
		int r = 0;
		int c = 0;
		for (int i = 0; i < Math.min(data.length, this.numRows
		        * this.numColumns); i++) {
			if (getWidget(r, c) instanceof Label) {
				((Label) getWidget(r, c)).setText(data[i].getText());

				++c;
				if (c == this.numColumns) {
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
