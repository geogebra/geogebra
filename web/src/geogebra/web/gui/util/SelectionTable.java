package geogebra.web.gui.util;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

import geogebra.common.awt.Color;
import geogebra.common.main.AbstractApplication;
import geogebra.web.awt.Dimension;
import geogebra.web.main.Application;



public class SelectionTable extends Grid implements ClickHandler {

	private int sliderValue;	
	private int rollOverRow = -1;
	private int rollOverColumn = -1;
	private int mode;
	private int numRows, numColumns, rowHeight, columnWidth;
	private Dimension iconSize;
	private Application app;
	
	public SelectionTable(Application app, Object[] data, Integer rows,
            Integer columns, Dimension iconSize, Integer mode) {
		super();
		this.app = app;	
		this.mode = mode;
		this.iconSize = iconSize;
		

		//=======================================
		// determine the dimensions of the table

		// rows = -1, cols = -1  ==> square table to fit data
		if(rows == -1 && columns == -1){
			rows = (int) Math.floor(Math.sqrt(data.length));
			columns = (int) Math.ceil(1.0 * data.length / rows);
		}

		// rows = -1  ==> fixed cols, rows added to fit data
		else if(rows == -1){
			rows = (int) (Math.ceil(1.0 *data.length / columns));
		}

		// cols = -1 ==> fixed rows, cols added to fit data
		else if(columns == -1){
			columns = (int) (1.0 * Math.ceil(data.length / rows));
		}
		
		numRows = rows;
		numColumns = columns;
		resize(numRows,numColumns);
		
		// set the table model with the data
		populateModel(data);
		addClickHandler(this);
		addStyleName("SelectionTable");
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
	
	private int selectedColumn = -1;
	
	private int getSelectedColumn() {
	    return selectedColumn;
    }
	
	private int selectedRow = -1;

	private int getSelectedRow() {
	    return selectedRow;
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

	public void populateModel(Object[] data) {
	  	
		int r=0;
		int c=0;
		
		for(int i=0; i < Math.min(data.length, this.numRows * this.numColumns); i++){
			setWidget(r, c, createWidget(data[i]));
			++c;
			if(c == this.numColumns){
				c = 0;
				++r;
			}
		}
    }

	private Widget createWidget(Object object) {
		Widget w = null;
		Context2d ctx = null;
		switch (mode) {
		case geogebra.common.gui.util.SelectionTable.MODE_TEXT:
			w = new Anchor((String)object);
			break;
		case geogebra.common.gui.util.SelectionTable.MODE_ICON:
			w = Canvas.createIfSupported();
			((Canvas)w).setWidth(((CanvasElement)object).getWidth()+"px");
			((Canvas)w).setHeight(((CanvasElement)object).getHeight()+"px");
			((Canvas)w).setCoordinateSpaceWidth(((CanvasElement)object).getWidth());
			((Canvas)w).setCoordinateSpaceWidth(((CanvasElement)object).getWidth());
			ctx = ((Canvas) w).getContext2d();
			ctx.drawImage((CanvasElement)object, 0, 0);
			break;
		case geogebra.common.gui.util.SelectionTable.MODE_IMAGE:
			w = Canvas.createIfSupported();
			((Canvas)w).setWidth(((CanvasElement)object).getWidth()+"px");
			((Canvas)w).setHeight(((CanvasElement)object).getHeight()+"px");
			((Canvas)w).setCoordinateSpaceWidth(((CanvasElement)object).getWidth());
			((Canvas)w).setCoordinateSpaceWidth(((CanvasElement)object).getWidth());
			ctx = ((Canvas) w).getContext2d();
			ctx.drawImage((CanvasElement)object, 0, 0);
			break;
		case geogebra.common.gui.util.SelectionTable.MODE_LATEX:
			AbstractApplication.debug("SelectionTable mode latex");
			break;
	  	}
		return w;
    }

	public void onClick(ClickEvent event) {
	   Cell clicked = getCellForEvent(event);
	   selectedColumn = clicked.getCellIndex();
	   selectedRow = clicked.getRowIndex();
    }

}
