package geogebra.web.gui.util;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Grid;

import geogebra.common.awt.Color;
import geogebra.web.awt.Dimension;
import geogebra.web.main.Application;



public class SelectionTable extends Grid {

	private int sliderValue;	
	private int rollOverRow = -1;
	private int rollOverColumn = -1;
	
	public SelectionTable(Application app, Object[] data, Integer rows,
            Integer columns, Dimension iconSize, Integer mode) {
		super(rows,columns);
    }
	
	public void setFgColor(Color fgColor) {
	    // TODO Auto-generated method stub
	    
    }

	public void setSliderValue(int value) {
		this.sliderValue = sliderValue;
    }

	public void setSelectedIndex(Integer index) {
		if(index == -1){
			this.clearSelection();
			return;
		}
		int row = (int) Math.floor(index / getColumnCount()) ;
		int column = index - (row * getColumnCount());
		this.changeSelection(row, column, false, false);
		rollOverRow = -1;
		rollOverColumn = -1;
    }

	private void changeSelection(int row, int column, boolean b, boolean c) {
	    // TODO Auto-generated method stub
	    
    }

	private void clearSelection() {
	    // TODO Auto-generated method stub
	    
    }

	public int getSelectedIndex() {
		int index = this.getColumnCount() * this.getSelectedRow()  + this.getSelectedColumn();
		if(index <-1) index = -1;
		return index;	
    }

	public void setSelectedIndex(int index){
		if(index == -1){
			this.clearSelection();
			return;
		}
		int row = (int) Math.floor(index / getColumnCount()) ;
		int column = index - (row * getColumnCount());
		this.changeSelection(row, column, false, false);
		rollOverRow = -1;
		rollOverColumn = -1;
	}
	private int getSelectedColumn() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	private Integer getSelectedRow() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public int getColumnWidth() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public int getRowHeight() {
	    // TODO Auto-generated method stub
	    return 0;
    }

}
