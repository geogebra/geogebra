package geogebra.gui.view.spreadsheet;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.main.AppD;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;


public class SpreadsheetKeyListener implements KeyListener 
{
	
	private AppD app;
	private SpreadsheetView view;
	private Kernel kernel;
	private MyTableD table;
	private DefaultTableModel model;	
	private MyCellEditor editor;

	
	public SpreadsheetKeyListener(AppD app, MyTableD table){
		
		this.app = app;
		this.kernel = app.getKernel();
		this.table = table;
		this.view = table.getView();
		this.model = (DefaultTableModel) table.getModel();  
		this.editor = table.editor;
		
	}
	
	
	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		//Application.debug(keyCode+"");
		//boolean shiftDown = e.isShiftDown(); 	 
		boolean altDown = e.isAltDown(); 	 
		boolean ctrlDown = AppD.isControlDown(e) // Windows ctrl/Mac Meta
		|| e.isControlDown(); // Fudge (Mac ctrl key)	
							
		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();
		
		
		switch (keyCode) {
		
		case KeyEvent.VK_UP:
			if (AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
					// move to top of current "block"
					// if shift pressed, select cells too
					while ( row > 0 && model.getValueAt(row - 1, column) != null) row--;
					table.changeSelection(row, column, false, e.isShiftDown());
				} else {
					// move up to next defined cell
					while ( row > 0 && model.getValueAt(row - 1, column) == null) row--;
					table.changeSelection(Math.max(0, row - 1), column, false, false);
					
				}
				e.consume();
			}
			// copy description into input bar when a cell is entered
//			GeoElement geo = (GeoElement) getModel().getValueAt(table.getSelectedRow() - 1, table.getSelectedColumn());
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}
			
			break;
			
		case KeyEvent.VK_LEFT:
			if (AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
					// move to left of current "block"
					// if shift pressed, select cells too
					while ( column > 0 && model.getValueAt(row, column - 1) != null) column--;
					table.changeSelection(row, column, false, e.isShiftDown());
				} else {
					// move left to next defined cell
					while ( column > 0 && model.getValueAt(row, column - 1) == null) column--;
					table.changeSelection(row, Math.max(0, column - 1), false, false);						
				}
				
				e.consume();
			}
//			// copy description into input bar when a cell is entered
//			geo = (GeoElement) getModel().getValueAt(table.getSelectedRow(), table.getSelectedColumn() - 1);
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}
			break;

		
		case KeyEvent.VK_DOWN:
			// auto increase spreadsheet size when you go off the bottom	
			if (table.getSelectedRow() + 1 == table.getRowCount() && table.getSelectedRow() + 1 < Kernel.MAX_SPREADSHEET_ROWS) {
				model.setRowCount(table.getRowCount() +1);
				
				//getView().getRowHeader().revalidate();   //G.STURR 2010-1-9
			}
			
			else if (AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
				
					// move to bottom of current "block"
					// if shift pressed, select cells too
					while ( row < table.getRowCount()-1 && model.getValueAt(row + 1, column) != null) row++;
					table.changeSelection(row, column, false, e.isShiftDown());
				} else {
					// move down to next selected cell
					while ( row < table.getRowCount()-1 && model.getValueAt(row + 1, column) == null) row++;
					table.changeSelection(Math.min(table.getRowCount() - 1, row + 1), column, false, false);
					
				}
				
				e.consume();
			}


//			// copy description into input bar when a cell is entered
//			geo = (GeoElement) getModel().getValueAt(table.getSelectedRow()+1, table.getSelectedColumn());
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}

			
			break;
			
		case KeyEvent.VK_HOME:

			// if shift pressed, select cells too
			if (AppD.isControlDown(e)) {
				// move to top left of spreadsheet
				table.changeSelection(0, 0, false, e.isShiftDown());
			}
			else {
				// move to left of current row
				table.changeSelection(row, 0, false, e.isShiftDown());
			}
			
			e.consume();
			break;
			
		case KeyEvent.VK_END:

			// move to bottom right of spreadsheet
			// if shift pressed, select cells too
			
			// find rectangle that will contain all cells 
			for (int c = 0 ; c <table.getColumnCount() ; c++)
			for (int r = 0 ; r < table.getRowCount() ; r++)
				if ((r > row || c > column) && model.getValueAt(r, c) != null) {
					if (r > row) row = r;
					if (c > column) column = c;
				}
			table.changeSelection(row, column, false, e.isShiftDown());
			
			e.consume();
			break;

		case KeyEvent.VK_RIGHT:
			// auto increase spreadsheet size when you go off the right
			
			if (table.getSelectedColumn() + 1 == table.getColumnCount() && table.getSelectedColumn() + 1 < Kernel.MAX_SPREADSHEET_COLUMNS) {
				model.setColumnCount(table.getColumnCount() +1);		
				view.getColumnHeader().revalidate();
				
				// these two lines are a workaround for Java 6
				// (Java bug?)
				table.changeSelection(row, column + 1, false, false);
				e.consume();
			}
			else if (AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
					// move to bottom of current "block"
					// if shift pressed, select cells too
					while ( column < table.getColumnCount() - 1 && model.getValueAt(row, column + 1) != null) column++;
					table.changeSelection(row, column, false, e.isShiftDown());
				} else {
					// move right to next defined cell
					while ( column < table.getColumnCount() - 1 && model.getValueAt(row, column + 1) == null) column++;
					table.changeSelection(row, Math.min(table.getColumnCount() - 1, column + 1), false, false);
					
				}
				e.consume();
			}

//			// copy description into input bar when a cell is entered
//			geo = (GeoElement) getModel().getValueAt(table.getSelectedRow(), table.getSelectedColumn() + 1);
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}
			break;
			
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_META: //MAC_OS Meta
			e.consume(); // stops editing start
			break;

		case KeyEvent.VK_F9:
			kernel.updateConstruction();
			e.consume(); // stops editing start
			break;

		case KeyEvent.VK_R:
			if (AppD.isControlDown(e)) {
				kernel.updateConstruction();
				e.consume();
			}
			else letterOrDigitTyped();
			break;

			// needs to be here to stop keypress starting a cell edit after the undo
		case KeyEvent.VK_Z: //undo
			if (ctrlDown) {
				//Application.debug("undo");
				app.getGuiManager().undo();
				e.consume();
			}
			else letterOrDigitTyped();
			break;

			// needs to be here to stop keypress starting a cell edit after the redo
		case KeyEvent.VK_Y: //redo
			if (ctrlDown) {
				//Application.debug("redo");
				app.getGuiManager().redo();
				e.consume();
			}
			else letterOrDigitTyped();
			break;


		case KeyEvent.VK_C: 	                         
		case KeyEvent.VK_V: 	                        
		case KeyEvent.VK_X: 	                         
		case KeyEvent.VK_DELETE: 	                         
		case KeyEvent.VK_BACK_SPACE:
			if (! editor.isEditing()) {
				if (Character.isLetterOrDigit(e.getKeyChar()) &&
						!editor.isEditing() && !(ctrlDown || e.isAltDown())) {
					letterOrDigitTyped();
				} else	if (ctrlDown) {
					e.consume();

					if (keyCode == KeyEvent.VK_C) {
						table.copy(altDown);
					}
					else if (keyCode == KeyEvent.VK_V) {
						boolean storeUndo = table.paste();
						view.getRowHeader().revalidate();
						if (storeUndo)
							app.storeUndoInfo();
					}
					else if (keyCode == KeyEvent.VK_X) {
						boolean storeUndo = table.cut();
						if (storeUndo)
							app.storeUndoInfo();
					}
				}
				if (keyCode == KeyEvent.VK_DELETE || 	                                         
						keyCode == KeyEvent.VK_BACK_SPACE) {
					e.consume();
					//Application.debug("deleting...");
					boolean storeUndo = table.delete();
					if (storeUndo)
						app.storeUndoInfo();
				}
				return;
			}
			break;		
			
		//case KeyEvent.VK_ENTER:	
		case KeyEvent.VK_F2:	
			if (!editor.isEditing()) {
				table.setAllowEditing(true);
				table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
				 final JTextComponent f = (JTextComponent)table.getEditorComponent();
		            f.requestFocus();
		            f.getCaret().setVisible(true);
				table.setAllowEditing(false);
			}
			e.consume();
			break;	
			
		case KeyEvent.VK_ENTER:	
			if (MyCellEditor.tabReturnCol > -1) {
				table.changeSelection(row , MyCellEditor.tabReturnCol, false, false);
				MyCellEditor.tabReturnCol = -1;
			}
			
			// fall through
		case KeyEvent.VK_PAGE_DOWN:	
		case KeyEvent.VK_PAGE_UP:	
			// stop cell being erased before moving
			break;
			
			// stop TAB erasing cell before moving
		case KeyEvent.VK_TAB:
			// disable shift-tab in column A
			if (table.getSelectedColumn() == 0 && e.isShiftDown()) 
				e.consume();
			break;

		case KeyEvent.VK_A:
			if (AppD.isControlDown(e)) {
				// select all cells
				
				row = 0;
				column = 0;
				// find rectangle that will contain all defined cells 
				for (int c = 0 ; c < table.getColumnCount() ; c++)
				for (int r = 0 ; r < table.getRowCount() ; r++)
					if ((r > row || c > column) && model.getValueAt(r, c) != null) {
						if (r > row) row = r;
						if (c > column) column = c;
					}
				table.changeSelection(0, 0, false, false);
				table.changeSelection(row, column, false, true);

				
				e.consume();
				
			}
			// no break, fall through
		default:
			if (!Character.isIdentifierIgnorable(e.getKeyChar()) &&
					!editor.isEditing() && !(ctrlDown || e.isAltDown())) {
				letterOrDigitTyped();
			} else
				e.consume();
		break;
			
		}
			
		/*
		if (keyCode >= 37 && keyCode <= 40) {
			if (editor.isEditing())	return;			
		}

		for (int i = 0; i < defaultKeyListeners.length; ++ i) {
			if (e.isConsumed()) break;
			defaultKeyListeners[i].keyPressed(e);			
		}
		 */
	}
	
	public void letterOrDigitTyped() {
		table.setAllowEditing(true);
		table.repaint();  //G.Sturr 2009-10-10: cleanup when keypress edit begins
		
		// check if cell fixed
		Object o = model.getValueAt(table.getSelectedRow(), table.getSelectedColumn());			
		if ( o != null && o instanceof GeoElement) {
			GeoElement geo = (GeoElement)o;
			if (geo.isFixed()) return;
		}
	
		model.setValueAt(null, table.getSelectedRow(), table.getSelectedColumn());
		table.editCellAt(table.getSelectedRow(), table.getSelectedColumn()); 
		// workaround, see
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4192625				
        final JTextComponent f = (JTextComponent)table.getEditorComponent();
        f.requestFocus();
        f.getCaret().setVisible(true);
        
        // workaround for Mac OS X 10.5 problem (first character typed deleted)
        if (AppD.MAC_OS)
            SwingUtilities.invokeLater( new Runnable(){ public void
            	run() { f.setSelectionStart(1);
	            f.setSelectionEnd(1);} });

        table.setAllowEditing(false);
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

}

