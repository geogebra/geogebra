/**
 * 
 */
package geogebra.cas.view;

import geogebra.common.cas.view.CASTable;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.layout.DockManager;
import geogebra.gui.layout.DockPanel;
import geogebra.main.AppD;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

/**
 * @author Quan
 * 
 */
public class CASTableD extends JTable implements CASTable{

	private static final long serialVersionUID = 1L;
		
	private CASTableModel tableModel;
	private Kernel kernel;
	/** application */
	protected AppD app;
	/** CAS view */
	protected CASViewD view;

	private CASTableCellEditorD editor;
	private CASTableCellRenderer renderer;
	private int currentWidth;
	private boolean rightClick = false;
	private int clickedRow;
	/** row that was last rolled over or -1 if mouse exited CAS view*/
	protected int rollOverRow = -1;
	/** whether the mouse is hovering over output*/
	protected boolean isOutputRollOver;
	/** whether current cell input/output should be highlighted*/
	protected boolean highlight = false;


	/**
	 * Constructs a <code>CASTable</code> that displays CAS cells
	 * 
	 * @param view
	 *            CASView that accommodates the table
	 */
	public CASTableD(final CASViewD view) {
		this.view = view;
		app = view.getApp();
		kernel = app.getKernel();

		setShowGrid(true);
		setGridColor(geogebra.awt.GColorD
				.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
		setBackground(Color.white);

		tableModel = new CASTableModel();
		this.setModel(tableModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		// init editor and renderer
		editor = new CASTableCellEditorD(view);
		renderer = new CASTableCellRenderer(view);
		getColumnModel().getColumn(COL_CAS_CELLS).setCellEditor(editor);
		getColumnModel().getColumn(COL_CAS_CELLS).setCellRenderer(renderer);
		setTableHeader(null);

		/**
		 * Remove all mouse listeners to make sure they don't start editing
		 * cells when a row is clicked. This is need to be able to have full
		 * control over whether editing should be started or the output of a
		 * cell inserted into another one. This also prevents Exception in
		 * thread "AWT-EventQueue-0" java.lang.NullPointerException at
		 * javax.swing.plaf.basic.BasicTableUI$Handler.mousePressed(Unknown
		 * Source)
		 */
		
		for (MouseListener ml : getMouseListeners()) {
			removeMouseListener(ml);
		}

		
		// listen to mouse pressed on table cells, make sure to start editing
		addMouseListener(new MyMouseListener());
		
				
		// add listener for mouse roll over
		RollOverListener rollOverListener = new RollOverListener();
		addMouseMotionListener(rollOverListener);
		addMouseListener(rollOverListener);
				
		

	
		// keep editor value after changing width
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (getCurrentWidth() == getWidth()) {
					return;
				}

				setCurrentWidth(getWidth());

				if (isEditing()) {
					// keep editor value after resizing
					int row = getEditor().getEditingRow();
					if (row >= 0 && row < getRowCount()) {
						getEditor().stopCellEditing();
						updateRow(row);
					}
				}
			}
		});

		// tableModel listener to resize the column width after row updates
		// note: this only adjusts column 0
		tableModel.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE
						|| e.getType() == TableModelEvent.DELETE) {
					TableCellRenderer tableCellRenderer;
					int prefWidth = 0;
					// iterate through all rows and get max preferred width
					for (int r = 0; r < getRowCount(); r++) {
						tableCellRenderer = getCellRenderer(r, 0);
						int w = prepareRenderer(tableCellRenderer, r, 0)
								.getPreferredSize().width;
						prefWidth = Math.max(prefWidth, w);
					}

					// adjust the width
					if (prefWidth != getTable().getColumnModel().getColumn(0)
							.getPreferredWidth()) {
						getTable().getColumnModel().getColumn(0)
								.setPreferredWidth(prefWidth);
						getTable().getColumnModel().getColumn(0)
								.setMinWidth(prefWidth);
					}
				}
			}
		});

		// Set the width of the index column;
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);

		// this.sizeColumnsToFit(0);
		// this.setSurrendersFocusOnKeystroke(true);
		
		this.getSelectionModel().addListSelectionListener(new SelectionListener(this));


		
		this.setFont(app.getPlainFont());
	}
	

	/**
	 * listen to mouse pressed on table cells, make sure to start editing
	 *
	 */
	protected class MyMouseListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			setClickedRow(rowAtPoint(e.getPoint()));

			// make sure the CAS view gets the focus and its toolbar when
			// clicked on the table
			// for some reason this is not working out of the box as
			// DockManager.eventDispatched()
			// sometimes things that this click comes from the EuclidianView
			DockManager dockManager = app.getGuiManager().getLayout()
					.getDockManager();
			DockPanel panel = dockManager.getFocusedPanel();
			if (panel == null
					|| panel.getViewId() != App.VIEW_CAS)
				app.getGuiManager().getLayout().getDockManager()
						.setFocusedPanel(App.VIEW_CAS);
		
			e.consume();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (getClickedRow() >= 0) {
				setRightClick(AppD.isRightClick(e));
				if (isRightClick()) {
					return;
				}		
			
				if (isRightClick() && isOutputPanelClicked(e.getPoint())) {
					GeoCasCell clickedCell = getTable().getGeoCasCell(
							getClickedRow());
					if (!clickedCell.isEmpty() && !clickedCell.isError()) {
						RowContentPopupMenu popupMenu = new RowContentPopupMenu(
								clickedCell, getTable());
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
				
				/* set/unset euclidian visibility for CasCells
				 * if there is no twinGeo which can be displayed, run the plot method
				 * which creates a name and a twinGeo for the cell
				 */
				if (isEditing()
							&& getEditor().getEditingRow() != getClickedRow()) {
						if(e.isAltDown()){
							getEditor().insertText("$" + (getClickedRow()+1));
						}
						// output panel click
						else if (isOutputPanelClicked(e.getPoint())){
							getEditor().insertText(view.getRowOutputValue(getClickedRow()));
						}else{
									getSelectionModel().setSelectionInterval(getClickedRow(),
											getClickedRow());
									startEditingRow(getClickedRow());
									return;
						}
						
						
						view.styleBar.updateStyleBar();
						repaint();
						// set clickedRow selected
					} else {
		
						getSelectionModel().setSelectionInterval(getClickedRow(),
								getClickedRow());
						startEditingRow(getClickedRow());
					}
				

				
			}	
			e.consume();
		}
	}
	
	
	/**
	 * Handles mouse over events
	 */
	protected class RollOverListener extends MouseInputAdapter {

		@Override
		public void mouseMoved(MouseEvent e) {
			int row = rowAtPoint(e.getPoint());
			if (row != getOpenRow() && 
					(row != rollOverRow  || isOutputRollOver != isOutputPanelClicked(e.getPoint())) ) {
				rollOverRow = row;
				isOutputRollOver = isOutputPanelClicked(e.getPoint());
				repaint();
			}
			highlight = e.isAltDown() || 
					(isOutputRollOver && getGeoCasCell(row).getLaTeXOutput()!=null &&
					getGeoCasCell(row).getLaTeXOutput().length()>0);
				
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			rollOverRow = -1;
			repaint();
		}
	}
	
	
	/**
	 * Selection listener to repaint selection frame when selection changes 
	 */
	public class SelectionListener implements ListSelectionListener {
	    private JTable table;
	    /**
	     * @param table CAS table to be listened to
	     */
	    SelectionListener(JTable table) {
	        this.table = table;
	    }
	    
	    public void valueChanged(ListSelectionEvent e) {
	        if (!e.getValueIsAdjusting()) {
	           table.repaint();
	        }
	    }
	}

	
	
	/**
	 * Returns the CAS view which uses this table
	 * 
	 * @return CAS view
	 */
	public CASViewD getCASView() {
		return view;
	}

	/**
	 * Returns whether the output panel of a cell row was clicked.
	 * 
	 * @param p
	 *            clicked position in table coordinates
	 * @return true if output panel of a cell row was clicked
	 */
	boolean isOutputPanelClicked(Point p) {
		int row = rowAtPoint(p);
		if (row < 0)
			return false;

		// calculate sum of row heights before
		int rowHeightsAbove = 0;
		for (int i = 0; i < row; i++) {
			rowHeightsAbove += getRowHeight(i);
		}

		// get height of input panel in clicked row
		TableCellRenderer tableCellRenderer = getCellRenderer(row, 0);
		CASTableCell tableCell = (CASTableCell) prepareRenderer(
				tableCellRenderer, row, 0);
		int inputAreaHeight = tableCell.getInputPanelHeight();

		// check if we clicked below input area
		boolean outputClicked = p.y > rowHeightsAbove + inputAreaHeight;
		return outputClicked;
	}
	
	@Override
	public boolean isEditing() {
		return editor != null && editor.isEditing();
	}

	/**
	 * Stops editing of current cell
	 */
	public void stopEditing() {
		if (!isEditing())
			return;
		// stop editing
		CellEditor editor1 = (CellEditor) getEditorComponent();
		if (editor1 != null)
			editor1.stopCellEditing();
	}

	/**
	 * Returns the cell editor
	 * 
	 * @return cell editor
	 */
	public CASTableCellEditorD getEditor() {
		return editor;
	}

	/**
	 * Inserts a row at the end and starts editing the new row.
	 * 
	 * @param newValue
	 *            CAS cell to be added
	 * @param startEditing
	 *            true to start editing
	 */
	public void insertRow(GeoCasCell newValue, boolean startEditing) {
		GeoCasCell toInsert = newValue;
		int lastRow = tableModel.getRowCount() - 1;
		if (isRowEmpty(lastRow)) {
			if (toInsert == null) {
				toInsert = new GeoCasCell(kernel.getConstruction());
				// kernel.getConstruction().setCasCellRow(newValue, lastRow);
			}
			setRow(lastRow, toInsert);
			if (startEditing)
				startEditingRow(lastRow);
		} else {
			insertRow(lastRow + 1, toInsert, startEditing);
		}
	}

	/**
	 * Inserts a row at selectedRow and starts editing the new row.
	 * 
	 * @param selectedRow
	 *            row index
	 * @param newValue
	 *            new value of the cell
	 * @param startEditing
	 *            true to start editing
	 */
	public void insertRow(final int selectedRow, GeoCasCell newValue,
			final boolean startEditing) {
		if (startEditing)
			stopEditing();
		GeoCasCell toInsert = newValue;
		if (toInsert == null) {
			toInsert = new GeoCasCell(kernel.getConstruction());
			if (selectedRow != tableModel.getRowCount())
				// tell construction about new GeoCasCell if it is not at the
				// end
				kernel.getConstruction().setCasCellRow(toInsert, selectedRow);
		}

		tableModel.insertRow(selectedRow, new Object[] { toInsert });
		// make sure the row is shown when at the bottom of the viewport
		getTable().scrollRectToVisible(
				getTable().getCellRect(selectedRow, 0, false));

		// update height of new row
		if (startEditing)
			startEditingRow(selectedRow);
	}

	/**
	 * Puts casCell into given row.
	 * 
	 * @param row
	 *            row index (starting from 0)
	 * @param casCell
	 *            CAS cell
	 */
	final public void setRow(final int row, final GeoCasCell casCell) {
		if (row < 0)
			return;

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
		} else {
			// add new rows
			for (int pos = rowCount; pos <= row; pos++) {
				tableModel.addRow(new Object[] { "" });
			}
			tableModel.setValueAt(casCell, row, COL_CAS_CELLS);
		}
	}

	/**
	 * Returns the preferred height of a row. The result is equal to the tallest
	 * cell in the row.
	 * 
	 * @param rowIndex
	 *            Row-Index.
	 * @return The preferred height.
	 * 
	 * @see "http://www.exampledepot.com/egs/javax.swing.table/RowHeight.html"
	 */
	public int getPreferredRowHeight(int rowIndex) {
		// Get the current default height for all rows
		int height = getRowHeight();

		// Determine highest cell in the row
		for (int c = 0; c < getColumnCount(); c++) {
			TableCellRenderer tableCellRenderer = getCellRenderer(rowIndex, c);
			Component comp = prepareRenderer(tableCellRenderer, rowIndex, c);
			int h = comp.getPreferredSize().height; // + 2*margin;
			height = Math.max(height, h);
		}
		return height;
	}

	/**
	 * The height of each row is set to the preferred height of the tallest cell
	 * in that row.
	 */
	public void packRows() {
		packRows(0, getRowCount());
	}

	/**
	 * For each row >= start and < end, the height of a row is set to the
	 * preferred height of the tallest cell in that row.
	 * 
	 * @param start
	 *            start row
	 * @param end
	 *            end row
	 */
	public void packRows(int start, int end) {
		for (int r = start; r < end; r++) {
			// Get the preferred height
			int h = getPreferredRowHeight(r);

			// Now set the row height using the preferred height
			if (getRowHeight(r) != h) {
				setRowHeight(r, h);
			}
		}
	}

	/**
	 * Updates given row
	 * 
	 * @param row
	 *            row to update
	 */
	public void updateRow(int row) {
		tableModel.fireTableRowsUpdated(row, row);
	}

	/**
	 * Updates all rows
	 */
	public void updateAllRows() {
		int rowCount = tableModel.getRowCount();
		if (rowCount > 0)
			tableModel.fireTableRowsUpdated(0, rowCount - 1);
	}

	/**
	 * @param row
	 *            row index (starting from 0)
	 * @return CAS cell on given row
	 */
	public GeoCasCell getGeoCasCell(int row) {
		return (GeoCasCell) tableModel.getValueAt(row, COL_CAS_CELLS);
	}

	/**
	 * @param row
	 *            row index (starting from 0)
	 * @return true if given cell is empty
	 */
	public boolean isRowEmpty(int row) {
		if (row < 0)
			return false;

		GeoCasCell value = (GeoCasCell) tableModel.getValueAt(row, 0);
		return value.isEmpty();
	}

	/**
	 * Delete all rows
	 */
	public void deleteAllRows() {
		tableModel.setRowCount(0);
	}

	/**
	 * Delete a row, and set the focus at the right position
	 * 
	 * @param row
	 *            row (staring from 0)
	 */
	public void deleteRow(int row) {
		// we must stop editing here, otherwise content of deleted cell is
		// copied below
		boolean wasEditing = this.isEditing();
		stopEditing();
		if (row > -1 && row < tableModel.getRowCount())
			tableModel.removeRow(row);
		if (wasEditing) {
			startEditingRow(row);
		}
	}

	// /**
	// * Changes all dynamic references in rows according to the row number
	// change.
	// * @param fromRow
	// * @param toRow
	// */
	// private void changeRowNumberChanged(int fromRow, int toRow) {
	// int rowCount = tableModel.getRowCount();
	// if (rowCount == 0) return;
	//
	// String oldRef = "\\" + CASInputHandler.ROW_REFERENCE_DYNAMIC +
	// Integer.toString(fromRow-1);
	// String newRef = "\\" + CASInputHandler.ROW_REFERENCE_DYNAMIC +
	// Integer.toString(toRow-1);
	// for (int i=0; i < rowCount; i++) {
	// CASTableCellValue cellValue = getCASTableCellValue(i);
	// cellValue.replaceRowReferences(oldRef, newRef);
	// }
	// }

	/**
	 * Set the focus on the specified row
	 * 
	 * @param editRow
	 *            row number (starting from 0)
	 */
	public void startEditingRow(final int editRow) {
		
		rollOverRow = -1;
		if (editRow >= tableModel.getRowCount()) {
			// insert new row, this starts editing
			insertRow(null, true);
		} else {
			// start editing
			doEditCellAt(editRow, COL_CAS_CELLS);
		}
	}

	private void doEditCellAt(final int editRow, final int editCol) {
		if (editRow < 0)
			return;
		setRowSelectionInterval(editRow, editRow);
		scrollRectToVisible(getCellRect(editRow, COL_CAS_CELLS, true));
		boolean success = editCellAt(editRow, editCol);
		if (success && editCol == COL_CAS_CELLS) {
			editor.setInputAreaFocused();
		}
	}

	@Override
	public void setFont(Font ft) {
		super.setFont(ft);
		if (editor != null){
			if(isEditing()){
				editor.stopCellEditing();
			}
			editor.setFont(getFont());
		}
		if (renderer != null)
			renderer.setFont(getFont());
		repaint();
	}

	// ===============================================================
	// Workaround for java horizontal scrolling bug, see:
	// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4127936

	/**
	 * When the viewport shrinks below the preferred size, stop tracking the
	 * viewport width
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		if (autoResizeMode != AUTO_RESIZE_OFF) {
			if (getParent() instanceof JViewport) {
				return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
			}
		}
		return false;
	}

	/**
	 * When the viewport shrinks below the preferred size, return the minimum
	 * size so that scrollbars will be shown
	 */
	@Override
	public Dimension getPreferredSize() {
		if (getParent() instanceof JViewport) {
			if (((JViewport) getParent()).getWidth() < super.getPreferredSize().width) {
				return getMinimumSize();
			}
		}

		return super.getPreferredSize();
	}

	// End horizontal scrolling fix
	// ================================================================

	/**
	 * When the table is smaller than the viewport fill this extra space with
	 * the same background color as the table.
	 */
	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			((JViewport) p).setBackground(getBackground());
		}
	}

	/**
	 * Updates labels to match current locale
	 */
	public void setLabels() {
		editor.setLabels();
	}

	/**
	 * @return the clickedRow
	 */
	public int getClickedRow() {
		return clickedRow;
	}

	/**
	 * @return the rightClick
	 */
	public boolean isRightClick() {
		return rightClick;
	}

	/**
	 * @param rightClick
	 *            the rightClick to set
	 */
	public void setRightClick(boolean rightClick) {
		this.rightClick = rightClick;
	}

	/**
	 * @param clickedRow
	 *            the clickedRow to set
	 */
	public void setClickedRow(int clickedRow) {
		this.clickedRow = clickedRow;
		view.getCASStyleBar().setSelectedRow(this.getGeoCasCell(clickedRow));
	}

	/**
	 * @return the table
	 */
	public CASTableD getTable() {
		return this;
	}

	/**
	 * @return the currentWidth
	 */
	public int getCurrentWidth() {
		return currentWidth;
	}

	/**
	 * @param currentWidth
	 *            the currentWidth to set
	 */
	public void setCurrentWidth(int currentWidth) {
		this.currentWidth = currentWidth;
	}
	
	
	/**
	 * @return currently open row
	 */
	public int getOpenRow(){
		if(getEditingRow() >= 0){
			return getEditingRow();
		}
		return(getRowCount()-1);
	}
	/** dash pattern for selection */
	final static float dash1[] = { 2f, 1f };
	/** dashed stroke for selection */
	final static BasicStroke dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

	/**
	 * Highlights the selected row
	 */
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		Graphics2D g2 = (Graphics2D) graphics;
		if (this.getSelectedRow() >= 0) {
			Rectangle r = getCellRect(getSelectedRow(), 0, true);
			g2.setColor(SystemColor.controlHighlight);
			g2.drawRect(r.x, r.y, r.width - 2, r.height - 2);

			if (isEditing()) {
				CASTableCell panel = (CASTableCell) getCellRenderer(
						getSelectedRow(), 0).getTableCellRendererComponent(
						this, null, false, false, rollOverRow,
						CASTable.COL_CAS_CELLS);
				int offset = panel.outputPanel.getY();
				r.height = r.height - offset;
				// g2.drawRect(r.x+1,r.y+1,r.width-4,r.height-4);
				g2.setColor(Color.red);
				// g2.drawRect(r.x+2,r.y+2,r.width-6,r.height-6);;
			}

		}	
		
		CASTableCell rollOverCell = null;

		{
			
			// shade the all rows except the editing row
			
			g2.setColor(new Color(0, 100, 100, 15));
			
			
			if (rollOverRow >= 0 && highlight) {
			
				rollOverCell = (CASTableCell) getCellRenderer(rollOverRow,
						CASTable.COL_CAS_CELLS).getTableCellRendererComponent(
						this, null, false, false, rollOverRow,
						CASTable.COL_CAS_CELLS);
				
				int offset = rollOverCell.outputPanel.getY();

				Rectangle r = getCellRect(rollOverRow, CASTable.COL_CAS_CELLS,
						true);
				
				//do not highlight the showHideControl
				r.width = r.width -10;
				
				if (isOutputRollOver) {
					r.y = r.y + offset;
					r.height = r.height - offset;
				} else {
					r.height = offset;
				}
		
		
					g2.setColor(new Color(0, 0, 200, 40));
					g2.fillRect(r.x+2,r.y+2,r.width-6,r.height-6);
					g2.setColor(Color.GRAY);
				g2.setStroke(dashed);
				g2.drawRect(r.x + 1, r.y + 1, r.width - 4, r.height - 4);
			}
			
		}
		
	}

	public App getApplication() {
		return app;
	}

}
