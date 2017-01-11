package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class SpreadsheetColumnControllerW implements
	MouseDownHandler, MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler {

	private AppW app;
	private SpreadsheetViewW view;
	private Kernel kernel;
	private MyTableW table;

	protected int column0 = -1;
	protected boolean isResizing = false;

	private int overTraceButtonColumn = -1;

	private boolean isMouseDown = false;

	public SpreadsheetColumnControllerW(AppW app, MyTableW table) {

		this.app = app;
		this.kernel = app.getKernel();
		this.table = table;
		this.view = (SpreadsheetViewW)table.getView();

	}

	// =========================================================
	// Mouse Listener Methods
	// =========================================================

	/*public void mouseClicked(MouseEvent e) {

		// Double clicking on a column boundary auto-adjusts the
		// width of the column on the left

		if (isResizing && !AppD.isRightClick(e) && e.getClickCount() == 2) {

			// get column to adjust
			int x = e.getX();
			int y = e.getY();
			GPoint point = table.getIndexFromPixel(x, y);
			GPoint testPoint = table.getIndexFromPixel(x - 4, y);
			int col = point.getX();
			if (point.getX() != testPoint.getX()) {
				col = col - 1;
			}

			// enlarge or shrink to fit the contents
			table.fitColumn(col);

			e.consume();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
		overTraceButtonColumn = -1;
		if (table.getTableHeader() != null)
			table.getTableHeader().resizeAndRepaint();
	}*/

	@Override
	public void onMouseDown(MouseDownEvent e) {

		isMouseDown = true;
		e.preventDefault();

		int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
		int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
		boolean shiftDown = e.isShiftKeyDown();
		boolean rightClick = (e.getNativeButton() == NativeEvent.BUTTON_RIGHT);//AppW.isRightClick(e);

		//?//if (!view.hasViewFocus())
		//?//	((LayoutW) app.getGuiManager().getLayout()).getDockManager()
		//?//			.setFocusedPanel(App.VIEW_SPREADSHEET);

		if (!rightClick) {
			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {

				// check if the cursor is within the resizing region (i.e.
				// border +- 3pixels)
				GPoint point2 = table
						.getPixel(point.getX(), point.getY(), true);
				GPoint point3 = table.getPixel(point.getX(), point.getY(),
						false);
				int x2 = point2.getX();
				int x3 = point3.getX();
				isResizing = !(x > x2 + 2 && x < x3 - 3);

				if (!isResizing) {

					// launch trace dialog if over a trace button
					if (point.x == this.overTraceButtonColumn) {
						int column = point.getX();
						table.setColumnSelectionInterval(column, column);
						//?//view.showTraceDialog(null,
						//?//		table.selectedCellRanges.get(0));
						//?//e.consume();
						return;
					}

					// otherwise handle column selection
					if (table.getSelectionType() != MyTable.COLUMN_SELECT) {
						table.setSelectionType(MyTable.COLUMN_SELECT);
						//?//if (table.getTableHeader() != null) {
						//?//	table.getTableHeader().requestFocusInWindow();
						//?//}
					}

					if (shiftDown) {
						if (column0 != -1) {
							int column = point.getX();
							table.setColumnSelectionInterval(column0, column);
						}
						// } else if (metaDown) {
						// column0 = point.getX();
						// // Note: ctrl-select now handled in
						// // table.changeSelection
						// table.setColumnSelectionInterval(column0, column0);
					} else {
						column0 = point.getX();
						table.setColumnSelectionInterval(column0, column0);
					}
					// repaint();
				}
			}

		}
	}

	@Override
	public void onMouseUp(MouseUpEvent e) {

		isMouseDown = false;
		e.preventDefault();

		boolean rightClick = (e.getNativeButton() == NativeEvent.BUTTON_RIGHT);

		if (!((AppW) kernel.getApplication()).letShowPopupMenu()) {
			return;
		}

		if (rightClick) {

			if (!app.letShowPopupMenu()) {
				return;
			}

			GPoint p = table.getIndexFromPixel(SpreadsheetMouseListenerW.getAbsoluteX(e, app), SpreadsheetMouseListenerW.getAbsoluteY(e, app));
			if (p == null) {
				return;
			}

			// if click is outside current selection then change selection
			if (p.getY() < table.minSelectionRow
					|| p.getY() > table.maxSelectionRow
					|| p.getX() < table.minSelectionColumn
					|| p.getX() > table.maxSelectionColumn) {
				// switch to column selection mode and select column
				if (table.getSelectionType() != MyTable.COLUMN_SELECT) {
					table.setSelectionType(MyTable.COLUMN_SELECT);
				}

				// selectNone();
				table.setColumnSelectionInterval(p.getX(), p.getX());
			}

			// show contextMenu			
			SpreadsheetContextMenuW contextMenu = ((GuiManagerW) app
			        .getGuiManager()).getSpreadsheetContextMenu(table);
			GPopupMenuW popup = (GPopupMenuW) contextMenu.getMenuContainer();
			popup.show(view.getFocusPanel(), e.getX(), e.getY());
			
			

		} else if (isResizing) {

			//?//if (e.getClickCount() == 2) {
			//?//	return;
			//?//}

			int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
			GPoint point = table.getIndexFromPixel(x, y);
			if (point == null) {
				return;
			}
			GPoint point2 = table.getPixel(point.getX(), point.getY(), false);
			int column = point.getX();
			if (x < point2.getX() - 3) {
				--column;
			}

			if (x <= 0) {
				x = 0; // G.Sturr 2010-4-10 prevent x=-1 with very small row
						// size
			}

			int width = table.getGrid().getColumnFormatter().getElement(column).getOffsetWidth();
			int[] selected = table.getSelectedColumns();

			boolean in = false;
			for (int i = 0; i < selected.length; ++i) {
				if (column == selected[i]) {
					in = true;
				}
			}
			if (!in) {
				return;
			}
			for (int i = 0; i < selected.length; ++i) {
				table.getGrid().getColumnFormatter().setWidth(selected[i], width+"px");
				// FIXME: don't forget to write it as:
				// table.getColumnFormatter().getElement(selected[i]).getStyle().setWidth(width, Style.Unit.PX);
				// the other syntax doesn't work probably
			}
		}
	}

	// =========================================================
	// MouseMotion Listener Methods
	// =========================================================

	@Override
	public void onMouseMove(MouseMoveEvent e) {

		e.preventDefault();

		// handles mouse over a trace button

		/*TODO int column = -1;
		boolean isOver = false;
		java.awt.Point mouseLoc = e.getPoint();
		GPoint cellLoc = table.getIndexFromPixel(mouseLoc.x, mouseLoc.y);
		if (cellLoc != null) {
			column = cellLoc.x;
			if (app.getTraceManager().isTraceColumn(column)) {
				// adjust mouseLoc to the coordinate space of this column header
				mouseLoc.x = mouseLoc.x - table.getCellRect(0, column, true).x;

				// int lowBound = table.getCellRect(0, column, true).x + 3;
				// isOver = mouseLoc.x > lowBound && mouseLoc.x < lowBound + 24;

				// Point sceeenMouseLoc =
				// MouseInfo.getPointerInfo().getLocation();
				isOver = ((ColumnHeaderRenderer) table.getColumnModel()
						.getColumn(column).getHeaderRenderer())
						.isOverTraceButton(column, mouseLoc, table
								.getColumnModel().getColumn(column)
								.getHeaderValue());
			}
		}

		// "isOver = " + isOver );
		if (isOver && overTraceButtonColumn != column) {
			overTraceButtonColumn = column;
			if (table.getTableHeader() != null) {
				table.getTableHeader().resizeAndRepaint();
			}
		}
		if (!isOver && overTraceButtonColumn > 0) {
			overTraceButtonColumn = -1;
			if (table.getTableHeader() != null) {
				table.getTableHeader().resizeAndRepaint();
			}
		}*/

		// DRAG

		if (isMouseDown) {
			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
				return; // G.Sturr 2009-9-30
			}

			if (isResizing) {
				return;
			}
			int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {
				int column = point.getX();
				if (column0 == -1) {
					column0 = column;
				}
				table.setColumnSelectionInterval(column0, column);
				// repaint();
			}
		}
	}

	// =========================================================
	// Key Listener Methods
	// =========================================================

	/*public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {

		boolean metaDown = AppD.isControlDown(e);
		boolean altDown = e.isAltDown();
		boolean shiftDown = e.isShiftDown();
		int keyCode = e.getKeyCode();

		switch (keyCode) {

		case KeyEvent.VK_LEFT:

			if (shiftDown) {
				// extend the column selection
				int column = table.getColumnModel().getSelectionModel()
						.getLeadSelectionIndex();
				table.changeSelection(-1, column - 1, false, true);
			} else {
				// select topmost cell in first column to the left of the
				// selection
				if (table.minSelectionColumn > 0) {
					table.setSelection(table.minSelectionColumn - 1, 0);
				} else {
					table.setSelection(table.minSelectionColumn, 0);
				}
				table.requestFocus();
			}
			break;

		case KeyEvent.VK_RIGHT:

			if (shiftDown) {
				// extend the column selection
				int column = table.getColumnModel().getSelectionModel()
						.getLeadSelectionIndex();
				table.changeSelection(-1, column + 1, false, true);
			} else {
				// select topmost cell in first column to the right of the
				// selection
				if (table.minSelectionColumn > 0) {
					table.setSelection(table.minSelectionColumn + 1, 0);
				} else {
					table.setSelection(table.minSelectionColumn, 0);
				}
				table.requestFocus();
			}

			break;

		case KeyEvent.VK_C: // control + c
			// Application.debug(minSelectionColumn);
			// Application.debug(maxSelectionColumn);
			if (metaDown && table.minSelectionColumn != -1
					&& table.maxSelectionColumn != -1) {
				table.copyPasteCut.copy(table.minSelectionColumn, 0,
						table.maxSelectionColumn, model.getRowCount() - 1,
						altDown);
				e.consume();
			}
			break;

		case KeyEvent.VK_V: // control + v
			if (metaDown && table.minSelectionColumn != -1
					&& table.maxSelectionColumn != -1) {
				boolean storeUndo = table.copyPasteCut.paste(
						table.minSelectionColumn, 0, table.maxSelectionColumn,
						model.getRowCount() - 1);
				if (storeUndo) {
					app.storeUndoInfo();
				}
				view.getRowHeader().revalidate();
				e.consume();
			}
			break;

		case KeyEvent.VK_X: // control + x
			if (metaDown && table.minSelectionColumn != -1
					&& table.maxSelectionColumn != -1) {
				boolean storeUndo = table.copyPasteCut.cut(
						table.minSelectionColumn, 0, table.maxSelectionColumn,
						model.getRowCount() - 1);
				if (storeUndo) {
					app.storeUndoInfo();
				}
				e.consume();
			}
			break;

		case KeyEvent.VK_BACK_SPACE: // delete
		case KeyEvent.VK_DELETE: // delete
			boolean storeUndo = table.copyPasteCut.delete(
					table.minSelectionColumn, 0, table.maxSelectionColumn,
					model.getRowCount() - 1);
			if (storeUndo) {
				app.storeUndoInfo();
			}
			break;
		}
	}

	public void keyReleased(KeyEvent e) {

	}*/

	// =========================================================
	// Renderer Class
	// =========================================================

	protected static class ColumnHeaderRenderer {

		/*private JLabel lblHeader;
		private JButton btnTrace;
		private BorderLayout layout;

		private ImageIcon traceIcon = app
				.getImageIcon("spreadsheettrace_button.gif");
		private ImageIcon traceRollOverIcon = app
				.getImageIcon("spreadsheettrace_hover.gif");*/

		public ColumnHeaderRenderer() {

			/*lblHeader = new JLabel();
			lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(lblHeader, BorderLayout.CENTER);

			btnTrace = new JButton();
			btnTrace.setBorderPainted(false);

			setOpaque(true);*/
			/*setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
					MyTableD.HEADER_GRID_COLOR));

			layout = (BorderLayout) this.getLayout();*/
		}

		/**
		 * Returns true if the given mouse location (in local coordinates of the
		 * header component) is over a trace button.
		 * 
		 * @param colIndex
		 * @param loc
		 * @param value
		 * @return
		 */
		/*public boolean isOverTraceButton(int colIndex, java.awt.Point loc,
				Object value) {

			if (!app.getTraceManager().isTraceColumn(colIndex)) {
				return false;
			}

			try {
				Component c = getTableCellRendererComponent(table, value,
						false, false, -1, colIndex);

				// layout.getLayoutComponent(BorderLayout.WEST).getBounds(rect);
				btnTrace.getBounds(rect);

				// loc.toString() + "  :  " +
				// rect.toString());
				return rect.contains(loc);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return false;
		}*/
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	public void onClick(ClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
