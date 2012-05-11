package geogebra.web.gui.util;

import javax.swing.ImageIcon;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Grid;

import geogebra.common.awt.Color;
import geogebra.common.main.AbstractApplication;
import geogebra.web.awt.Dimension;
import geogebra.web.main.Application;



public class SelectionTable extends Grid {

	private int sliderValue;	
	private int rollOverRow = -1;
	private int rollOverColumn = -1;
	private int mode;
	private int numRows, numColumns, rowHeight, columnWidth;
	private Dimension iconSize;
	
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
	
	private String[] toolTipArray = null;
	/**
	 * sets the tooTip strings for the selection table; 
	 * the toolTipArray should have a 1-1 correspondence with the data array 
	 * @param toolTipArray
	 */
	public void setToolTipArray(String[] toolTipArray) {
		this.toolTipArray = toolTipArray;
	}
	
	boolean useColorSwatchBorder = false;
	public void setUseColorSwatchBorder(boolean useColorSwatchBorder) {
		this.useColorSwatchBorder = useColorSwatchBorder;
		setCellDimensions();
	}
	
	// set cell dimensions
	private void setCellDimensions(){

		int padding = useColorSwatchBorder ? 1 : 4;

		// match row height to specified icon height
		// when mode=text then let font size adjust row height automatically  
		if(!(mode == geogebra.common.gui.util.SelectionTable.MODE_TEXT || mode == geogebra.common.gui.util.SelectionTable.MODE_LATEX)){		
			rowHeight = iconSize.getHeight() + padding;	
		} else{
			rowHeight = getMaxRowHeight() + padding;
		}

		setRowHeight(rowHeight);


		// set the column widths
		columnWidth = iconSize.getWidth() + padding;
		int w;
		for (int i = 0; i < getColumnCount(); ++ i) {	
			// for mode=text, adjust column width to the maximum width in the column	
			if(mode == geogebra.common.gui.util.SelectionTable.MODE_TEXT || mode == geogebra.common.gui.util.SelectionTable.MODE_LATEX){
				w = getMaxColumnWidth(); 
				//getColumnModel().getColumn(i).setPreferredWidth(w);
				columnWidth = Math.max(w, columnWidth);
			}else{
				//getColumnModel().getColumn(i).setPreferredWidth(columnWidth);
			}

		}
		//repaint();
	}

	private void setRowHeight(int rowHeight2) {
	    // TODO Auto-generated method stub
	    
    }

	private int getMaxColumnWidth() {
	    return 50;
    }

	private int getMaxRowHeight() {
	    return 50;
    }

	public void populateModel(Object[] colorSwatchIcons) {
	   AbstractApplication.debug("build up the model from <a>-s");
    }

}
