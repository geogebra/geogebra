package geogebra.gui.util;



import geogebra.common.main.GeoGebraColorConstants;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Creates a table to display and select images and other custom icons. The
 * table takes a one dimensional array of data objects as input and then, using
 * row and column size parameters, displays the data as GeoGebra icons in a 2D
 * table. User selection is returned as an index to the data array.
 * 
 * The table is intended for use in a popup menu.
 * 
 * @author G.Sturr
 * 
 */
public class SelectionTable extends JTable {

	private static final long serialVersionUID = 1L;
	
	private AppD app;
	private MyCellRenderer renderer;
	private DefaultTableModel model;

	private int rollOverRow = -1;
	private int rollOverColumn = -1;


	private int sliderValue;	

	private int horizontalAlignment = SwingConstants.LEFT;

	private boolean showSelection = true;

	public void setShowSelection(boolean showSelection) {
		this.showSelection = showSelection;
	}


	public void setHorizontalAlignment(int horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}



	private Object[] data;
	private int numRows, numColumns, rowHeight, columnWidth;
	public int getColumnWidth() {
		return columnWidth;
	}


	private Dimension iconSize;

	private geogebra.common.gui.util.SelectionTable mode;

	private Color fgColor, bgColor;
	private float alpha;

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
		repaint();
	}
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}


	boolean useColorSwatchBorder = false;
	public void setUseColorSwatchBorder(boolean useColorSwatchBorder) {
		this.useColorSwatchBorder = useColorSwatchBorder;
		setCellDimensions();
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


	/********************************************************
	 * Constructor
	 * @param app
	 * @param data
	 * @param rows
	 * @param columns
	 * @param iconSize
	 * @param mode
	 */
	public SelectionTable(AppD app, Object[] data, int rows, int columns, Dimension iconSize, geogebra.common.gui.util.SelectionTable mode){

		this.app = app;	
		this.mode = mode;
		this.iconSize = iconSize;
		if(mode == geogebra.common.gui.util.SelectionTable.MODE_LATEX)
			data = createLatexIconArray((String[]) data);
		this.data = data;

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
		
		//=======================================
		
		
		// set the table model with the data
		populateModel(data);


		//=======================================	
		// set cell renderer
		renderer = new MyCellRenderer();
		this.setDefaultRenderer(Object.class, renderer);


		//=======================================
		// set various display properties
		this.setAutoResizeMode(AUTO_RESIZE_OFF);
		this.setAutoCreateColumnsFromModel(false);
		setShowGrid(false);
		setGridColor(Color.GRAY);
		this.setTableHeader(null);
		this.setBorder(null);
		setCellDimensions();
		setFont(app.getPlainFont());
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// set cell selection properties
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//=======================================
		// add listener for mouse roll over
		RollOverListener rollOverListener = new RollOverListener();
		addMouseMotionListener(rollOverListener);
		addMouseListener(rollOverListener);

	}



	/** Disables cell editing */
	@Override
	public boolean isCellEditable(int rowIndex, int vColIndex) { 
		return false; 
	}  



	/** Loads a one dimensional array of data into the table model*/
	public void populateModel( Object [] data){
		
		model = new DefaultTableModel(numRows, numColumns);
		int r=0;
		int c=0;
		
		for(int i=0; i < Math.min(data.length, this.numRows * this.numColumns); i++){
			model.setValueAt(data[i], r, c);
			++c;
			if(c == this.numColumns){
				c = 0;
				++r;
			}
		}
		
		setModel(model);
	}


	public ImageIcon[] createLatexIconArray(String[] symbols){
		ImageIcon[] iconArray = new ImageIcon[symbols.length];
		for(int i= 0; i < symbols.length; i++){
			iconArray[i] = GeoGebraIcon.createLatexIcon(app, symbols[i], app.getPlainFont(), true, Color.BLACK, null);
		}
		return iconArray;
	}




	// set cell dimensions
	private void setCellDimensions(){

		int padding = useColorSwatchBorder ? 1 : 4;

		// match row height to specified icon height
		// when mode=text then let font size adjust row height automatically  
		if(!(mode == geogebra.common.gui.util.SelectionTable.MODE_TEXT || mode == geogebra.common.gui.util.SelectionTable.MODE_LATEX)){		
			rowHeight = iconSize.height + padding;	
		} else{
			rowHeight = getMaxRowHeight(this) + padding;
		}

		setRowHeight(rowHeight);


		// set the column widths
		columnWidth = iconSize.width + padding;
		int w;
		for (int i = 0; i < getColumnCount(); ++ i) {	
			// for mode=text, adjust column width to the maximum width in the column	
			if(mode == geogebra.common.gui.util.SelectionTable.MODE_TEXT || mode == geogebra.common.gui.util.SelectionTable.MODE_LATEX){
				w = getMaxColumnWidth(this,i); 
				getColumnModel().getColumn(i).setPreferredWidth(w);
				columnWidth = Math.max(w, columnWidth);
			}else{
				getColumnModel().getColumn(i).setPreferredWidth(columnWidth);
			}

		}
		repaint();
	}


	public void updateFonts(){
		setFont(app.getPlainFont());
		setCellDimensions();
	}


	//==============================================
	//    Listeners
	//==============================================


	private class RollOverListener extends MouseInputAdapter {

		@Override
		public void mouseExited(MouseEvent e) {
			rollOverRow = -1;
			rollOverColumn = -1;
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int row = rowAtPoint(e.getPoint());
			int column = columnAtPoint(e.getPoint());
			if( row != rollOverRow || column != rollOverColumn ) {
				rollOverRow = row;
				rollOverColumn = column;

				if(toolTipArray!=null){
					int index = getColumnCount() * row  + column;
					if(index < data.length)
						setToolTipText(toolTipArray[index]);
					else
						setToolTipText(null);
				}
				repaint();
			}
		}
	}





	//==============================================
	//    Getters/Setters
	//==============================================


	public int getSelectedIndex(){
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

	public Object getSelectedValue(){
		if(getSelectedRow() != -1 && getSelectedColumn() != -1)
			return model.getValueAt(getSelectedRow(), getSelectedColumn());
		return null;
	}

	public int getSliderValue() {
		return sliderValue;
	}

	public void setSliderValue(int sliderValue) {
		this.sliderValue = sliderValue;
	}


	public Object[] getData(){
		return data;
	}



	public ImageIcon getDataIcon(Object value){

		ImageIcon icon = null;
		if(value == null) return 
		GeoGebraIcon.createEmptyIcon(1, 1);
		//GeoGebraIcon.createStringIcon("\u00D8", app.getPlainFont(), true, false, true, iconSize , Color.GRAY, null);

		switch (mode){

		case MODE_IMAGE:
			icon = GeoGebraIcon.createFileImageIcon( app, (String)value, alpha, iconSize);
			break;

		case MODE_ICON:
		case MODE_LATEX:
			icon = (ImageIcon) value;
			break;

		}

		return icon;
	}


	//==============================================
	//    Cell Renderer
	//==============================================


	class MyCellRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;
		
		private Border normalBorder, selectedBorder, rollOverBorder, paddingBorder;
		private Color selectionColor, rollOverColor;

		public MyCellRenderer() {

			//TODO --- selection color should be centralized, not from spreadsheet

			selectionColor =  geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR) ;
			rollOverColor =  Color.LIGHT_GRAY;

			paddingBorder = BorderFactory.createEmptyBorder(0,5,0,5);

			rollOverBorder = BorderFactory.createLineBorder(Color.GRAY, 3);
			normalBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
			if(mode == geogebra.common.gui.util.SelectionTable.MODE_LATEX)
				selectedBorder = rollOverBorder;
			else
				selectedBorder = BorderFactory.createLineBorder(Color.BLACK, 3);

			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);		

		}


		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected,boolean hasFocus, int row,int column) 
		{
			setAlignmentX(CENTER_ALIGNMENT);
			setAlignmentY(CENTER_ALIGNMENT);

			if(mode == geogebra.common.gui.util.SelectionTable.MODE_TEXT){
				this.setHorizontalAlignment(horizontalAlignment);
				this.setVerticalAlignment(SwingConstants.CENTER);
				setText((String)value);
				setFont(app.getFontCanDisplayAwt((String)value, Font.PLAIN));
				setBorder(paddingBorder);

			}else{		
				setText("");
				setIcon(getDataIcon(value));
			}


			if(useColorSwatchBorder){
				setBackground(table.getBackground());
				if (showSelection && isSelected) {
					setBorder(selectedBorder);
				} 
				else if(row == rollOverRow && column == rollOverColumn) {
					setBorder(rollOverBorder);
				}
				else{			
					setBorder(normalBorder);
				}

			}else{

				if (showSelection && isSelected) {
					setBackground(selectionColor);
				} 
				else if(row == rollOverRow && column == rollOverColumn) {
					setBackground(rollOverColor);
				}
				else{
					setBackground(table.getBackground());
				}
			}

			return this;
		}
	}




	/**
	 * Finds the maximum preferred width of a column.
	 */
	public int getMaxColumnWidth(JTable table, int column){

		TableColumn tableColumn = table.getColumnModel().getColumn(column); 

		// iterate through the rows and find the preferred width
		int maxPrefWidth = 0;
		int colPrefWidth = 0;
		for (int row = 0; row < table.getRowCount(); row++) {
			if(table.getValueAt(row, column)!=null){
				colPrefWidth = (int) table.getCellRenderer(row, column)
				.getTableCellRendererComponent(table,
						table.getValueAt(row, column), false, false,
						row, column).getPreferredSize().getWidth();
				maxPrefWidth = Math.max(maxPrefWidth, colPrefWidth);
			}
		}

		return maxPrefWidth + table.getIntercellSpacing().width;
	}


	/**
	 * Finds the maximum preferred height of all cells.
	 */
	public int getMaxRowHeight(JTable table){

		TableColumn tableColumn; 

		// iterate through all cells
		int maxPrefHeight = 0;
		int cellPrefHeight = 0;
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				tableColumn = table.getColumnModel().getColumn(c); 
				if(table.getValueAt(r, c) != null){
					cellPrefHeight = (int) table.getCellRenderer(r, c).getTableCellRendererComponent(table,
							table.getValueAt(r, c), false, false,
							r, c).getPreferredSize().getHeight();
					maxPrefHeight = Math.max(maxPrefHeight, cellPrefHeight);
				}
			}
		}
		return maxPrefHeight + table.getIntercellSpacing().height;
	}

}
