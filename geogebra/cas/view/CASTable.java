/**
 * 
 */
package geogebra.cas.view;

import geogebra.gui.layout.DockManager;
import geogebra.gui.layout.DockPanel;
import geogebra.kernel.GeoCasCell;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

/**
 * @author Quan
 * 
 */
public class CASTable extends JTable {
	
	public final static int COL_CAS_CELLS = 0;

	private CASTableModel tableModel;
	protected Kernel kernel;
	protected Application app;
	private CASView view;
	private CASTable table; 
	
	private CASTableCellEditor editor;
	private CASTableCellRenderer renderer;
	private int currentWidth;

	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = new Color(185,
			185, 210);

	public CASTable(final CASView view) {
		this.view = view;
		app = view.getApp();
		kernel = app.getKernel();
		this.table = this;

		setShowGrid(true);
		setGridColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
		setBackground(Color.white);

		tableModel = new CASTableModel();
		this.setModel(tableModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		// init editor and renderer
		editor = new CASTableCellEditor(view);
		renderer = new CASTableCellRenderer(view);
		getColumnModel().getColumn(COL_CAS_CELLS).setCellEditor(editor);
		getColumnModel().getColumn(COL_CAS_CELLS).setCellRenderer(renderer);				
		setTableHeader(null); 
		
		// remove all mouse listeners to make sure they don't start editing cells
		// when a row is clicked. This is need to be able to have full control over
		// whether editing should be started or the output of a cell inserted into another one
		// This also prevents
		// Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
		//  at javax.swing.plaf.basic.BasicTableUI$Handler.mousePressed(Unknown Source)
		for (MouseListener ml : getMouseListeners()) {
			removeMouseListener(ml);
		}
					
		// listen to mouse pressed on table cells, make sure to start editing
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int clickedRow = rowAtPoint(e.getPoint());
				
				// make sure the CAS view gets the focus and its toolbar when clicked on the table
				// for some reason this is not working out of the box as DockManager.eventDispatched()
				// sometimes things that this click comes from the EuclidianView		
				DockManager dockManager = app.getGuiManager().getLayout().getDockManager();
				DockPanel panel = dockManager.getFocusedPanel();
				if (panel == null || panel.getViewId() != Application.VIEW_CAS)
					app.getGuiManager().getLayout().getDockManager().setFocusedPanel(Application.VIEW_CAS);
				
				if (clickedRow >= 0) {			
					if (isEditing() && isOutputPanelClicked(e.getPoint())) {	
						// currently editing and output clicked: insert into currently editing row
						if (editor.getEditingRow() != clickedRow)
							editor.insertText(view.getRowOutputValue(clickedRow));				
					} 
					else {						
						// set clickedRow selected
						getSelectionModel().setSelectionInterval(clickedRow, clickedRow);
						startEditingRow(clickedRow);
					}
				}
			}
		});
		
		// keep editor value after changing width
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (currentWidth == getWidth()) 
					return; 
				else 
					currentWidth = getWidth();
				
				if (isEditing()) {					
					// keep editor value after resizing
					int row = editor.getEditingRow();
					if (row >=0 && row < getRowCount()) {
						editor.stopCellEditing();
						updateRow(row);
					}
				}
			}
		});

		// tableModel listener to resize the column width after row updates
		// note: this only adjusts column 0
		tableModel.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				if(e.getType()==TableModelEvent.UPDATE || e.getType()==TableModelEvent.DELETE){
					TableCellRenderer renderer; 
					int prefWidth = 0; 
					// iterate through all rows and get max preferred width
					for (int r=0; r < getRowCount(); r++) {
						renderer =  getCellRenderer(r, 0);
						int w = prepareRenderer(renderer, r, 0).getPreferredSize().width;
						prefWidth = Math.max(prefWidth, w);
					}

					// adjust the width
					if(prefWidth != table.getColumnModel().getColumn(0).getPreferredWidth()) {   	  
						table.getColumnModel().getColumn(0).setPreferredWidth(prefWidth);
						table.getColumnModel().getColumn(0).setMinWidth(prefWidth);
					}
				}
			}
		});
		
		
		
		// Set the width of the index column;
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);

		// this.sizeColumnsToFit(0);
		//this.setSurrendersFocusOnKeystroke(true);
	}
			
	public CASView getCASView() {
		return view;
	}
	
	/**
	 * Returns whether the output panel of a cell row was clicked.
	 * @param p clicked position in table coordinates
	 * @return
	 */
	private boolean isOutputPanelClicked(Point p) {
		int row = rowAtPoint(p);
		if (row < 0) return false;
		
		// calculate sum of row heights before
		int rowHeightsAbove = 0;
		for (int i=0; i < row; i++) {
			rowHeightsAbove += getRowHeight(i);
		}
		
		// get height of input panel in clicked row
		TableCellRenderer renderer = getCellRenderer(row, 0);		
		CASTableCell tableCell = (CASTableCell) prepareRenderer(renderer, row, 0);
		int inputAreaHeight = tableCell.getInputPanelHeight();
		
		// check if we clicked below input area
		boolean outputClicked = p.y > rowHeightsAbove + inputAreaHeight ;
		return outputClicked;
	}
	
	public boolean isEditing() {
		return editor != null && editor.isEditing();		
	}
	
	public void stopEditing() {
		if (!isEditing()) return;
		// stop editing 
		CellEditor editor = (CellEditor) getEditorComponent();
		if (editor != null) editor.stopCellEditing();
	}		
		
	public CASTableCellEditor getEditor() {
		return editor;		
	}		

		
	/**
	 * Inserts a row at the end and starts editing
	 * the new row.
	 * @param newValue 
	 * @param startEditing 
	 */
	public void insertRow(GeoCasCell newValue, boolean startEditing) {
		int lastRow = tableModel.getRowCount()-1;
		if (isRowEmpty(lastRow)) {
			if (newValue == null) {
				newValue = new GeoCasCell(kernel.getConstruction());
				//kernel.getConstruction().setCasCellRow(newValue, lastRow);
			}
			setRow(lastRow, newValue);
			if (startEditing)
				startEditingRow(lastRow);
		} else {
			insertRow(lastRow + 1, newValue, startEditing);	
		}	
	}
	
	/**
	 * Inserts a row at selectedRow and starts editing
	 * the new row.
	 * @param selectedRow 
	 * @param newValue 
	 * @param startEditing 
	 */
	public void insertRow(final int selectedRow, GeoCasCell newValue,final  boolean startEditing) {	
		if (newValue == null) {
			newValue = new GeoCasCell(kernel.getConstruction());
			if (selectedRow != tableModel.getRowCount())
				// tell construction about new GeoCasCell if it is not at the end
				kernel.getConstruction().setCasCellRow(newValue, selectedRow);
		}
		
		tableModel.insertRow(selectedRow,  new Object[] { newValue });
		// make sure the row is shown when at the bottom of the viewport
		table.scrollRectToVisible(table.getCellRect(selectedRow, 0, false));
		
		// update height of new row
		if (startEditing)
			startEditingRow(selectedRow);		
	}	
	
	/**
	 * Puts casCell into given row.
	 * 
	 * @param row 
	 * @param casCell
	 */
	final public void setRow(final int row, final GeoCasCell casCell) {	
		if (row < 0) return;
		
		// cancel editing
		if (editor.isEditing() && editor.getEditingRow() == row)
			editor.cancelCellEditing();
		
		int rowCount = tableModel.getRowCount();
		if (row < rowCount) {
			if (casCell == tableModel.getValueAt(row, COL_CAS_CELLS)) {
				tableModel.fireTableRowsUpdated(row, row);
			} else {
				tableModel.setValueAt(casCell, row, COL_CAS_CELLS);
			}
		}
		else  {
			// add new rows
			for (int pos = rowCount; pos <= row; pos++) {
				tableModel.addRow(new Object[] {""});
			}
			tableModel.setValueAt(casCell, row, COL_CAS_CELLS);
		}				
	}	
	
	/**
	 * Returns the preferred height of a row.
	 * The result is equal to the tallest cell in the row.
	 * @param rowIndex Row-Index.
	 * @return The preferred height.
	 * 
	 * @see "http://www.exampledepot.com/egs/javax.swing.table/RowHeight.html"
	 */    
    public int getPreferredRowHeight(int rowIndex) {
        // Get the current default height for all rows
        int height = getRowHeight();
    
        // Determine highest cell in the row
        for (int c=0; c < getColumnCount(); c++) {
            TableCellRenderer renderer =  getCellRenderer(rowIndex, c);
            Component comp = prepareRenderer(renderer, rowIndex, c);
            int h = comp.getPreferredSize().height; // + 2*margin;
            height = Math.max(height, h);
        }
        return height;
    }

    /** 
     * The height of each row is set to the preferred height of the
     * tallest cell in that row.
     */
    public void packRows() {
        packRows(0, getRowCount());
    }
    
    /**
     *  For each row >= start and < end, the height of a
     *  row is set to the preferred height of the tallest cell
     *  in that row.
     * @param start 
     * @param end 
     */
    public void packRows(int start, int end) {
        for (int r=start; r < end; r++) {
            // Get the preferred height
            int h = getPreferredRowHeight(r);
    
            // Now set the row height using the preferred height
            if (getRowHeight(r) != h) {
                setRowHeight(r, h);
            }
        }
    }

 

	
	public void updateRow(int row) {
		tableModel.fireTableRowsUpdated(row, row);	
	}
	
	public void updateAllRows() {
		int rowCount = tableModel.getRowCount();
		if (rowCount > 0)
			tableModel.fireTableRowsUpdated(0, rowCount - 1);
	}
	
	public GeoCasCell getGeoCasCell(int row) {
		return (GeoCasCell) tableModel.getValueAt(row, COL_CAS_CELLS);
	}
	
	public boolean isRowEmpty(int row) {	
		if (row < 0) return false;
		
		GeoCasCell value = (GeoCasCell) tableModel.getValueAt(row, 0);
		return value.isEmpty();
	}

	
	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteAllRows() {
		tableModel.setRowCount(0);		
	}	

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteRow(int row) {
//		stopEditing();
		if (row > -1 && row < tableModel.getRowCount())
			tableModel.removeRow(row);

//		int rowCount = tableModel.getRowCount();
//		if (rowCount == 0)
//			insertRow(null, true);
//		else 
//			startEditingRow(Math.min(row, rowCount-1));
	}
	
//	/**
//	 * Changes all dynamic references in rows according to the row number change.
//	 * @param fromRow
//	 * @param toRow
//	 */
//	private void changeRowNumberChanged(int fromRow, int toRow) {
//		int rowCount = tableModel.getRowCount();
//		if (rowCount == 0) return;
//		
//		String oldRef = "\\" + CASInputHandler.ROW_REFERENCE_DYNAMIC + Integer.toString(fromRow-1);
//		String newRef =  "\\" + CASInputHandler.ROW_REFERENCE_DYNAMIC + Integer.toString(toRow-1);
//		for (int i=0; i < rowCount; i++) {
//			CASTableCellValue cellValue = getCASTableCellValue(i);
//			cellValue.replaceRowReferences(oldRef, newRef);
//		}
//	}

	/*
	 * Function: Set the focus on the specified row
	 */
	public void startEditingRow(final int editRow) {									
		if (editRow >= tableModel.getRowCount()) {
			// insert new row, this starts editing
			insertRow(null, true);
		}
		else {		
			// start editing				    	
	        doEditCellAt(editRow, COL_CAS_CELLS);											
		}
	}
	
	private void doEditCellAt(final int editRow, final int editCol) {
    	setRowSelectionInterval(editRow, editRow);	
        scrollRectToVisible(getCellRect( editRow, COL_CAS_CELLS, true ) );
    	boolean success = editCellAt(editRow, editCol);
		if (success && editCol == COL_CAS_CELLS) {
			editor.setInputAreaFocused();
		}			
	}

	public void setFont(Font ft) {
		super.setFont(ft);
		if (editor != null)
			editor.setFont(getFont());
		if (renderer != null)
			renderer.setFont(getFont());
	}


	
	//===============================================================
	// Workaround for java horizontal scrolling bug, see:
	// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4127936
	
	/**
	 * When the viewport shrinks below the preferred size, stop tracking the
	 * viewport width
	 */
	public boolean getScrollableTracksViewportWidth() {
	    if (autoResizeMode != AUTO_RESIZE_OFF) {
	        if (getParent() instanceof JViewport) {
	            return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
	        }
	    }
	    return false;
	}
	
	/**
	 * When the viewport shrinks below the preferred size, return the minimum
	 * size so that scrollbars will be shown
	 */
	public Dimension getPreferredSize() {
	    if (getParent() instanceof JViewport) {
	        if (((JViewport)getParent()).getWidth() < super.getPreferredSize().width) {
	            return getMinimumSize();
	        }
	    }

	    return super.getPreferredSize();
	}

	// End horizontal scrolling fix
	//================================================================
	
	
	
	
	/** When the table is smaller than the viewport fill this extra  
	 * space with the same background color as the table.*/
	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			((JViewport) p).setBackground(getBackground());
		}
	}
	
	public void setLabels(){
		editor.setLabels();
	}
	


}
