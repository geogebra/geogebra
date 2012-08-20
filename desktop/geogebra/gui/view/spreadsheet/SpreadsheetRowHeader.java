package geogebra.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.main.App;
import geogebra.gui.layout.LayoutD;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class SpreadsheetRowHeader extends JList implements MouseListener,
		MouseMotionListener, KeyListener, ListSelectionListener

{
	private static final long serialVersionUID = 1L;
	private AppD app;
	private SpreadsheetView view;
	private MyTableD table;
	private MyListModel listModel;

	// note: MyTable uses its own minSelectionRow and maxSelectionRow.
	// The selection listener keeps them in sync.
	private int minSelectionRow = -1;
	private int maxSelectionRow = -1;
	private ListSelectionModel selectionModel;

	// fields for resizing rows
	private static Cursor resizeCursor = Cursor
			.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	private Cursor otherCursor = resizeCursor;
	private int mouseYOffset, resizingRow;
	private boolean doRowResize = false;

	protected int row0 = -1;

	/***************************************************
	 * Constructor
	 */
	public SpreadsheetRowHeader(AppD app, MyTableD table) {

		this.app = app;
		this.table = table;
		this.view = table.getView();

		listModel = new MyListModel((DefaultTableModel) table.getModel());
		this.setModel(listModel);

		setFocusable(true);
		setAutoscrolls(false);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFixedCellWidth(SpreadsheetView.ROW_HEADER_WIDTH);

		setCellRenderer(new RowHeaderRenderer(table, this));

		table.getSelectionModel().addListSelectionListener(this);

	}

	public static class MyListModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;
		protected DefaultTableModel model;

		public MyListModel(DefaultTableModel model) {
			this.model = model;
		}

		public int getSize() {
			return model.getRowCount();
		}

		public Object getElementAt(int index) {
			return "" + (index + 1);
		}

		// forces update of rowHeader, called after row resizing
		public Void changed() {
			this.fireContentsChanged(this, 0, model.getRowCount());
			return null;
		}

	}

	public void updateRowHeader() {
		listModel.changed();
	}

	// ===============================================
	// Renderer
	// ===============================================

	public class RowHeaderRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;

		protected JList rowHeader;
		private Color defaultBackground;

		public RowHeaderRenderer(JTable table, JList rowHeader) {
			super("", SwingConstants.CENTER);
			setOpaque(true);
			defaultBackground = MyTableD.BACKGROUND_COLOR_HEADER;
			this.rowHeader = rowHeader;
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
					MyTableD.HEADER_GRID_COLOR));
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			setFont(app.getPlainFont());

			// adjust row height to match spreadsheet table row height
			Dimension size = getPreferredSize();
			size.height = table.getRowHeight(index);
			setPreferredSize(size);

			setText((value == null) ? "" : value.toString());

			if (table.getSelectionType() == MyTable.COLUMN_SELECT) {
				setBackground(defaultBackground);
			} else {
				if (table.selectedRowSet.contains(index)
						|| (index >= minSelectionRow && index <= maxSelectionRow)) {
					setBackground(MyTableD.SELECTED_BACKGROUND_COLOR_HEADER);
				} else {
					setBackground(defaultBackground);
				}
			}
			return this;
		}
	}

	/**
	 * Update the rowHeader list when row selection changes in the table
	 */
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel selectionModel = (ListSelectionModel) e.getSource();
		minSelectionRow = selectionModel.getMinSelectionIndex();
		maxSelectionRow = selectionModel.getMaxSelectionIndex();
		repaint();
	}

	// Returns index of row to be resized if mouse point P is
	// near a row boundary (within 3 pixels)
	private int getResizingRow(java.awt.Point p) {
		int resizeRow = -1;
		GPoint point = table.getIndexFromPixel(p.x, p.y);
		if (point != null) {
			// test if mouse is 3 pixels from row boundary
			int cellRow = point.getY();
			if (cellRow >= 0) {
				Rectangle r = table.getCellRect(cellRow, 0, true);
				// near row bottom
				if (p.y < r.y + 3) {
					resizeRow = cellRow - 1;
				} 
				// near row top
				if (p.y > r.y + r.height - 3) {
					resizeRow = cellRow;
				}
			}
		}
		return resizeRow;
	}

	// Cursor change for when mouse is over a row boundary
	private void swapCursor() {
		Cursor tmp = getCursor();
		setCursor(otherCursor);
		otherCursor = tmp;
	}

	// ===============================================
	// Mouse Listener Methods
	// ===============================================

	public void mouseClicked(MouseEvent e) {

		// Double clicking on a row boundary auto-adjusts the
		// height of the row above the boundary (the resizingRow)

		if (resizingRow >= 0 && !AppD.isRightClick(e) && e.getClickCount() == 2) {
			table.fitRow(resizingRow);
			e.consume();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		boolean shiftPressed = e.isShiftDown();
		boolean rightClick = AppD.isRightClick(e);

		int x = e.getX();
		int y = e.getY();

		if (!view.hasViewFocus())
			((LayoutD) app.getGuiManager().getLayout()).getDockManager()
					.setFocusedPanel(App.VIEW_SPREADSHEET);

		// Update resizingRow. If nonnegative, then mouse is over a boundary
		// and it gives the row to be resized (resizing is done in
		// mouseDragged).
		java.awt.Point p = e.getPoint();
		resizingRow = getResizingRow(p);
		mouseYOffset = p.y - table.getRowHeight(resizingRow);
		//

		// left click
		if (!rightClick) {

			if (resizingRow >= 0)
				return; // GSTURR 2010-1-9

			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {
				// G.STURR 2010-1-29
				if (table.getSelectionType() != MyTable.ROW_SELECT) {
					table.setSelectionType(MyTable.ROW_SELECT);
					requestFocusInWindow();
				}

				if (shiftPressed) {
					if (row0 != -1) {
						int row = point.getY();
						table.setRowSelectionInterval(row0, row);
					}
				}

				// ctrl-select is handled in table

				else {
					row0 = point.getY();
					table.setRowSelectionInterval(row0, row0);
				}
				table.repaint();
			}
		}

	}

	public void mouseReleased(MouseEvent e) {

		boolean rightClick = AppD.isRightClick(e);

		if (rightClick) {
			if (!app.letShowPopupMenu())
				return;

			GPoint p = table.getIndexFromPixel(e.getX(), e.getY());
			if (p == null)
				return;

			// if click is outside current selection then change selection
			if (p.getY() < minSelectionRow || p.getY() > maxSelectionRow
					|| p.getX() < table.minSelectionColumn
					|| p.getX() > table.maxSelectionColumn) {

				// switch to row selection mode and select row
				if (table.getSelectionType() != MyTable.ROW_SELECT) {
					table.setSelectionType(MyTable.ROW_SELECT);
				}

				table.setRowSelectionInterval(p.getY(), p.getY());
			}

			// show contextMenu
			SpreadsheetContextMenu popupMenu = new SpreadsheetContextMenu(
					table, e.isShiftDown());
			popupMenu.show(e.getComponent(), e.getX(), e.getY());

		}

		// If row resize has happened, resize all other selected rows
		if (doRowResize) {
			if (minSelectionRow != -1 && maxSelectionRow != -1
					&& (maxSelectionRow - minSelectionRow > 1)) {
				if (table.isSelectAll())
					table.setRowHeight(table.getRowHeight(resizingRow));
				else
					for (int row = minSelectionRow; row <= maxSelectionRow; row++) {
						table.setRowHeight(row, table.getRowHeight(resizingRow));
					}
			}
			doRowResize = false;
		}
	}

	// ===============================================
	// MouseMotion Listener Methods
	// ===============================================

	public void mouseDragged(MouseEvent e) {
		if (AppD.isRightClick(e))
			return; // G.Sturr 2009-9-30

		// G.STURR 2010-1-9
		// On mouse drag either resize or select a row
		int x = e.getX();
		int y = e.getY();
		if (resizingRow >= 0) {
			// resize row
			int newHeight = y - mouseYOffset;
			if (newHeight > 0) {
				table.setRowHeight(resizingRow, newHeight);
				// set this flag to resize all selected rows on mouse release
				doRowResize = true;
			}

		} else { // select row
			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {
				int row = point.getY();
				table.setRowSelectionInterval(row0, row);

				// G.Sturr 2010-4-4
				// keep the row header updated when drag selecting multiple rows
				view.updateRowHeader();
				table.scrollRectToVisible(table.getCellRect(point.y, point.x,
						true));
				table.repaint();
			}
		}

	}

	public void mouseMoved(MouseEvent e) {
		// Show resize cursor when mouse is over a row boundary
		if ((getResizingRow(e.getPoint()) >= 0) != (getCursor() == resizeCursor)) {
			swapCursor();
		}
	}

	// ===============================================
	// Key Listener Methods
	// ===============================================

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {

		int keyCode = e.getKeyCode();

		boolean metaDown = AppD.isControlDown(e);
		boolean altDown = e.isAltDown();
		boolean shiftDown = e.isShiftDown();

		// Application.debug(keyCode);
		switch (keyCode) {

		case KeyEvent.VK_UP:

			if (shiftDown) {
				// extend the column selection
				int row = table.getSelectionModel().getLeadSelectionIndex();
				table.changeSelection(row - 1, -1, false, true);
			} else {
				// select topmost cell in first column to the left of the
				// selection
				if (table.minSelectionRow > 0)
					table.setSelection(0, table.minSelectionRow - 1);
				else
					table.setSelection(0, table.minSelectionRow);
				table.requestFocus();
			}
			break;

		case KeyEvent.VK_DOWN:
			if (shiftDown) {
				// extend the column selection
				int row = table.getSelectionModel().getLeadSelectionIndex();
				table.changeSelection(row + 1, -1, false, true);
			} else {
				// select topmost cell in first column to the left of the
				// selection
				if (table.minSelectionRow > 0)
					table.setSelection(0, table.minSelectionRow + 1);
				else
					table.setSelection(0, table.minSelectionRow);
				table.requestFocus();
			}
			break;

		case KeyEvent.VK_C: // control + c
			if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
				table.copyPasteCut.copy(0, minSelectionRow, table.getModel()
						.getColumnCount() - 1, maxSelectionRow, altDown);
				e.consume();
			}
			break;
		case KeyEvent.VK_V: // control + v
			if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
				boolean storeUndo = table.copyPasteCut.paste(0,
						minSelectionRow, table.getModel().getColumnCount() - 1,
						maxSelectionRow);
				if (storeUndo)
					app.storeUndoInfo();
				e.consume();
			}
			break;
		case KeyEvent.VK_X: // control + x
			if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
				table.copyPasteCut.copy(0, minSelectionRow, table.getModel()
						.getColumnCount() - 1, maxSelectionRow, altDown);
				e.consume();
			}
			boolean storeUndo = table.copyPasteCut.delete(0, minSelectionRow,
					table.getModel().getColumnCount() - 1, maxSelectionRow);
			if (storeUndo)
				app.storeUndoInfo();
			break;

		case KeyEvent.VK_DELETE: // delete
		case KeyEvent.VK_BACK_SPACE: // delete on MAC
			storeUndo = table.copyPasteCut.delete(0, minSelectionRow, table
					.getModel().getColumnCount() - 1, maxSelectionRow);
			if (storeUndo)
				app.storeUndoInfo();
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

}
