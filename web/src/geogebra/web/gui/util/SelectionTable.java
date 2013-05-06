package geogebra.web.gui.util;

import geogebra.common.awt.GColor;
import geogebra.common.main.App;
import geogebra.web.awt.GDimensionW;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;



public class SelectionTable extends Grid implements ClickHandler {

	private int sliderValue;	
	private int rollOverRow = -1;
	private int rollOverColumn = -1;
	private geogebra.common.gui.util.SelectionTable mode;
	private int numRows, numColumns, rowHeight, columnWidth;
	private GDimensionW iconSize;
	private AppW app;
	
	private float alpha;
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public SelectionTable(AppW app, Object[] data, Integer rows,
            Integer columns, GDimensionW iconSize, geogebra.common.gui.util.SelectionTable mode) {
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
	
	public void setFgColor(GColor fgColor) {
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
	private boolean isIniting = true;
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
	  	if (data.length > 0) {
			if (data[0] instanceof ImageResource) {
				AppResourcesConverter.convertImageResourceToImageData(data, this);
			} else {
				populateModelCallback(data);
			}
	  	}
    }

	public void populateModelCallback(Object[] data) {
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
	    } else {
	    	for(int i=0; i < Math.min(data.length, this.numRows * this.numColumns); i++){
				if (getWidget(r, c) instanceof Canvas) {
					Canvas canvas = (Canvas) getWidget(r, c);
					canvas.getContext2d().putImageData((ImageData) data[i], 0, 0);
					
					++c;
					if(c == this.numColumns){
						c = 0;
						++r;
					}
				}
			}
	    }
    }

	private Widget createWidget(Object object) {
		Widget w = null;
		Context2d ctx = null;
		int width = 0;
		int height = 0;
		switch (mode) {
		case MODE_TEXT:
			if (object instanceof String) {
				w = new Anchor((String)object);
			} else {
				App.debug("Problem in SelectionTable.createWidget (String wanted)");
			}
			break;
		case MODE_ICON:
		case MODE_IMAGE: //fall through
			if (!(object instanceof ImageData)) {
				App.debug("Problem in SelectionTable.createWidget (ImageData wanted)");
				return w;
			}
			w = Canvas.createIfSupported();
			width = ((ImageData)object).getWidth();
			height = ((ImageData)object).getHeight();
			((Canvas)w).setWidth(width+"px");
			((Canvas)w).setHeight(height+"px");
			((Canvas)w).setCoordinateSpaceWidth(width);
			((Canvas)w).setCoordinateSpaceHeight(height);
			ctx = ((Canvas) w).getContext2d();
			ctx.putImageData((ImageData) object,0, 0);
			break;
		case MODE_LATEX:
			App.debug("SelectionTable mode latex");
			break;
	  	}
		return w;
    }

	public void onClick(ClickEvent event) {
	   Cell clicked = getCellForEvent(event);
	   selectedColumn = clicked.getCellIndex();
	   selectedRow = clicked.getRowIndex();
	   clearSelectedCells();
	   Widget w = getWidget(clicked.getRowIndex(),clicked.getCellIndex());
	   if (w != null) {
		   w.addStyleName("selected");
	   }
    }

	public ImageData getSelectedValue() {
		if(getSelectedRow() != -1 && getSelectedColumn() != -1)
			return getValueAt(getSelectedRow(), getSelectedColumn());
		return null;
    }

	private ImageData getValueAt(int row, int column) {
		Canvas c = (Canvas) getWidget(row, column);
		Context2d ctx = c.getContext2d();
	    return ctx.getImageData(0, 0, c.getCoordinateSpaceWidth(), c.getCoordinateSpaceHeight());
    }

	public void repaint() {
	  //should we do here something?
	    
    }

	public void updateFonts() {
	    // TODO Auto-generated method stub
	    
    }
	
	public ImageData getDataIcon(Object value){

		ImageData icon = null;
		if(value == null) return 
		GeoGebraIcon.createEmptyIcon(1, 1);
		//GeoGebraIcon.createStringIcon("\u00D8", app.getPlainFont(), true, false, true, iconSize , Color.GRAY, null);

		switch (mode){

		case MODE_IMAGE:
			icon = GeoGebraIcon.createFileImageIcon( app, (String)value, alpha, iconSize);
			break;

		case MODE_ICON:
		case MODE_LATEX:
			icon = (ImageData) value;
			break;

		}

		return icon;
	}

}
