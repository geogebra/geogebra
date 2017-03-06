package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.util.SpreadsheetTraceSettings;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

public class SpreadsheetColumnControllerD
		implements KeyListener, MouseListener, MouseMotionListener {

	private AppD app;
	private SpreadsheetViewD view;
	private Kernel kernel;
	private MyTableD table;
	private DefaultTableModel model;

	protected int column0 = -1;
	protected boolean isResizing = false;

	private int overTraceButtonColumn = -1;
	/** localization */
	final LocalizationD loc;

	public SpreadsheetColumnControllerD(AppD app, MyTableD table) {

		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.table = table;
		this.view = table.getView();
		this.model = (DefaultTableModel) table.getModel();

	}

	// =========================================================
	// Mouse Listener Methods
	// =========================================================

	@Override
	public void mouseClicked(MouseEvent e) {

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

	@Override
	public void mouseEntered(MouseEvent e) {
		// nothing to do
	}

	@Override
	public void mouseExited(MouseEvent e) {
		overTraceButtonColumn = -1;
		if (table.getTableHeader() != null) {
			table.getTableHeader().resizeAndRepaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		boolean shiftDown = e.isShiftDown();
		boolean rightClick = AppD.isRightClick(e);

		if (!view.hasViewFocus()) {
			((LayoutD) app.getGuiManager().getLayout()).getDockManager()
					.setFocusedPanel(App.VIEW_SPREADSHEET);
		}

		// ensure that table header keeps the focus
		table.getTableHeader().requestFocus();

		if (!rightClick) {
			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {

				// check if the cursor is within the resizing region (i.e.
				// border +- 3pixels)
				GPoint point2 = table.getPixel(point.getX(), point.getY(),
						true);
				GPoint point3 = table.getPixel(point.getX(), point.getY(),
						false);
				int x2 = point2.getX();
				int x3 = point3.getX();
				isResizing = !(x > x2 + 2 && x < x3 - 3);

				if (!isResizing) {

					// launch trace dialog if over a trace button
					if (point.x == this.overTraceButtonColumn) {
						int column = point.getX();
						app.getTraceManager().togglePauseTraceGeo(column);
						view.repaintView();
						e.consume();
						return;
					}

					// otherwise handle column selection
					if (table
							.getSelectionType() != MyTableInterface.COLUMN_SELECT) {
						table.setSelectionType(MyTableInterface.COLUMN_SELECT);
						if (table.getTableHeader() != null) {
							table.getTableHeader().requestFocusInWindow();
						}
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
	public void mouseReleased(MouseEvent e) {
		boolean rightClick = AppD.isRightClick(e);

		if (!((AppD) kernel.getApplication()).letShowPopupMenu()) {
			return;
		}

		if (rightClick) {

			if (!app.letShowPopupMenu()) {
				return;
			}

			GPoint p = table.getIndexFromPixel(e.getX(), e.getY());
			if (p == null) {
				return;
			}

			// if click is outside current selection then change selection
			if (p.getY() < table.minSelectionRow
					|| p.getY() > table.maxSelectionRow
					|| p.getX() < table.minSelectionColumn
					|| p.getX() > table.maxSelectionColumn) {
				// switch to column selection mode and select column
				if (table
						.getSelectionType() != MyTableInterface.COLUMN_SELECT) {
					table.setSelectionType(MyTableInterface.COLUMN_SELECT);
				}

				// selectNone();
				table.setColumnSelectionInterval(p.getX(), p.getX());
			}

			// show contextMenu
			SpreadsheetContextMenuD contextMenu = new SpreadsheetContextMenuD(
					table);
			JPopupMenu popup = (JPopupMenu) contextMenu.getMenuContainer();
			popup.show(e.getComponent(), e.getX(), e.getY());

		} else if (isResizing) {

			if (e.getClickCount() == 2) {
				return;
			}

			int x = e.getX();
			int y = e.getY();
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

			int width = table.getColumnModel().getColumn(column).getWidth();
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
				table.getColumnModel().getColumn(selected[i])
						.setPreferredWidth(width);
			}
		}

		// ensure that table header keeps the focus
		table.getTableHeader().requestFocus();

	}

	// =========================================================
	// MouseMotion Listener Methods
	// =========================================================

	@Override
	public void mouseDragged(MouseEvent e) {

		if (AppD.isRightClick(e)) {
			return; // G.Sturr 2009-9-30
		}

		if (isResizing) {
			return;
		}
		int x = e.getX();
		int y = e.getY();
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

	@Override
	public void mouseMoved(MouseEvent e) {

		// handles mouse over a trace button

		int column = -1;
		boolean isOver = false;
		Point mouseLoc = e.getPoint();
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
								.isOverTraceButton(column, mouseLoc,
										table.getColumnModel().getColumn(column)
												.getHeaderValue());
			}
		}

		// System.out.println("isOver = " + isOver );
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
		}
	}

	// =========================================================
	// Key Listener Methods
	// =========================================================

	@Override
	public void keyTyped(KeyEvent e) {
		// only handle key pressed
	}

	@Override
	public void keyPressed(KeyEvent e) {

		boolean metaDown = AppD.isControlDown(e);
		boolean altDown = e.isAltDown();
		boolean shiftDown = e.isShiftDown();
		int keyCode = e.getKeyCode();

		switch (keyCode) {

		default:
			// do nothing
			break;
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

	@Override
	public void keyReleased(KeyEvent e) {
		// only handle key pressed
	}

	// =========================================================
	// Renderer Class
	// =========================================================

	protected class ColumnHeaderRenderer extends JPanel
			implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		private Color defaultBackground;
		private JLabel lblHeader;
		private JButton btnTrace;
		private BorderLayout layout;

		private ImageIcon pauseIcon = app
				.getScaledIcon(GuiResourcesD.SPREADSHEETTRACE_PAUSE);
		private ImageIcon recordIcon = app
				.getScaledIcon(GuiResourcesD.SPREADSHEETTRACE_RECORD);

		public ColumnHeaderRenderer() {
			super(new BorderLayout());

			lblHeader = new JLabel();
			lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(lblHeader, BorderLayout.CENTER);

			btnTrace = new JButton();
			btnTrace.setBorderPainted(false);
			btnTrace.setContentAreaFilled(false);
			btnTrace.setPreferredSize(new Dimension(18, 18));

			setOpaque(true);
			defaultBackground = MyTableD.BACKGROUND_COLOR_HEADER;

			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 1,
							MyTableD.HEADER_GRID_COLOR),
					BorderFactory.createEmptyBorder(0, 5, 0, 0)));

			layout = (BorderLayout) this.getLayout();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table0,
				Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int colIndex) {

			MyTableD table1;

			if (table0 instanceof MyTableD) {
				table1 = (MyTableD) table0;
			} else {
				return null;
			}

			lblHeader.setFont(app.getPlainFont());

			lblHeader.setText(value.toString());

			if (table1.getSelectionType() == MyTableInterface.ROW_SELECT) {
				setBackground(defaultBackground);
			} else {
				if (table1.selectedColumnSet.contains(colIndex)
						|| (colIndex >= table1.minSelectionColumn
								&& colIndex <= table1.maxSelectionColumn)) {
					setBackground(MyTableD.SELECTED_BACKGROUND_COLOR_HEADER);
				} else {
					setBackground(defaultBackground);
				}
			}

			// add/remove trace button
			if (app.hasTraceManager()) {
				SpreadsheetTraceSettings t = app.getTraceManager()
						.getTraceSettings(colIndex);
				if (t == null) { // no geo traced in this column
					if (layout.getLayoutComponent(loc.borderWest()) != null) {
						this.remove(
								layout.getLayoutComponent(loc.borderWest()));
					}
				} else {
					this.add(btnTrace, loc.borderWest());
					// set icon
					if (t.pause) {
						btnTrace.setIcon(pauseIcon);
						setToolTipText(
								loc.getMenuTooltip("TraceToSpreadsheet")); // button
																			// switches
																			// back
																			// to
																			// record
					} else {
						btnTrace.setIcon(recordIcon);
						setToolTipText(loc.getMenuTooltip("Pause")); // button
																		// pauses
																		// the
																		// trace
					}
				}

			}

			return this;
		}

		private Rectangle rect = new Rectangle();

		/**
		 * Returns true if the given mouse location (in local coordinates of the
		 * header component) is over a trace button.
		 * 
		 * @param colIndex
		 * @param loc
		 * @param value
		 * @return
		 */
		public boolean isOverTraceButton(int colIndex, Point loc,
				Object value) {

			if (!app.getTraceManager().isTraceColumn(colIndex)) {
				return false;
			}

			try {
				getTableCellRendererComponent(table, value, false,
						false, -1, colIndex);

				// layout.getLayoutComponent(app.borderWest()).getBounds(rect);
				btnTrace.getBounds(rect);

				// System.out.println(loc.toString() + " : " +
				// rect.toString());
				return rect.contains(loc);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return false;
		}
	}

}
