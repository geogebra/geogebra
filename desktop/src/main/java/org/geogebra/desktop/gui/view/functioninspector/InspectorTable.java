package org.geogebra.desktop.gui.view.functioninspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

public class InspectorTable extends JTable {

	private static final long serialVersionUID = 1L;

	AppD app;
	FunctionInspectorD inspector;

	boolean doRedNegative = false;
	HashSet<Point> editableCell;

	public static final int TYPE_INTERVAL = 0;
	public static final int TYPE_XY = 1;
	int tableType;

	public InspectorTable(AppD app, FunctionInspectorD inspector, int minRows,
			int tableType) {
		super(minRows, 2);

		this.app = app;
		this.inspector = inspector;
		this.tableType = tableType;

		// set visual appearance
		setShowGrid(true);
		setGridColor(
				GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
		// setSelectionBackground(new Color(255, 130, 171));
		setSelectionBackground(
				GColorD.getAwtColor(GeoGebraColorConstants.PINK));
		setBorder(BorderFactory.createEmptyBorder());

		// set resizing fields
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		setPreferredScrollableViewportSize(this.getPreferredSize());

		// this.addKeyListener(this);

		// set renderer and editor
		setDefaultRenderer(Object.class, new MyCellRenderer(this));
		setDefaultEditor(Object.class, new MyEditor());

		editableCell = new HashSet<Point>();
	}

	public boolean isDoRedNegative() {
		return doRedNegative;
	}

	public void setDoRedNegative(boolean doRedNegative) {
		this.doRedNegative = doRedNegative;
	}

	public void setCellEditable(int rowIndex, int colIndex) {
		if (rowIndex == -1 && colIndex == -1) {
			editableCell.clear();
		} else {
			editableCell.add(new Point(rowIndex, colIndex));
		}

	}

	// control cell editing
	@Override
	public boolean isCellEditable(int rowIndex, int colIndex) {
		return editableCell.contains(new Point(rowIndex, colIndex));
	}

	// fill empty scroll pane space with table background color
	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			((JViewport) p).setBackground(getBackground());
		}
	}

	public void setColumnWidths() {
		setColumnWidths(this);
	}

	private void setColumnWidths(JTable table) {

		int w;
		for (int i = 0; i < getColumnCount(); ++i) {
			w = getMaxColumnWidth(table, i) + 5;
			table.getColumnModel().getColumn(i).setPreferredWidth(w);
		}

		int gap = table.getParent().getPreferredSize().width
				- table.getPreferredSize().width;
		// System.out.println(table.getParent().getPreferredSize().width);
		if (gap > 0) {
			w = table.getColumnCount() - 1;
			int newWidth = gap + table.getColumnModel()
					.getColumn(table.getColumnCount() - 1).getWidth();
			table.getColumnModel().getColumn(w).setPreferredWidth(newWidth);
		}
	}

	/**
	 * Finds the maximum preferred width of a column.
	 */
	public int getMaxColumnWidth(JTable table, int column) {

		TableColumn tableColumn = table.getColumnModel().getColumn(column);

		// iterate through the rows and find the preferred width
		int maxPrefWidth = tableColumn.getPreferredWidth();
		int colPrefWidth = 0;
		for (int row = 0; row < table.getRowCount(); row++) {
			if (table.getValueAt(row, column) != null) {
				colPrefWidth = (int) table.getCellRenderer(row, column)
						.getTableCellRendererComponent(table,
								table.getValueAt(row, column), false, false,
								row, column)
						.getPreferredSize().getWidth();
				maxPrefWidth = Math.max(maxPrefWidth, colPrefWidth);
			}
		}

		return maxPrefWidth + table.getIntercellSpacing().width;
	}

	public void setMyCellEditor(int colIndex) {
		getColumnModel().getColumn(colIndex).setCellEditor(new MyEditor());
	}

	// ====================================================
	// Cell Renderer
	// ====================================================

	private class MyCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		private JTextField tf;
		private Border editCellBorder;
		private JTable table;
		private Border paddingBorder;
		private boolean doRedNegative;

		private MyCellRenderer(InspectorTable table) {
			this.table = table;
			tf = new JTextField();
			http: // dev.geogebra.org/trac/ticket/4316#
			this.doRedNegative = table.isDoRedNegative();
			paddingBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
			// paddingBorder =
			// BorderFactory.createMatteBorder(3,3,3,3,Color.RED);
			editCellBorder = BorderFactory.createCompoundBorder(tf.getBorder(),
					paddingBorder);

		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				final int row, int column) {

			setFont(app.getPlainFont());

			if (table.isCellEditable(row, column)) {
				setBorder(editCellBorder);
			} else {
				setBorder(paddingBorder);
			}

			if (isSelected && !table.isCellEditable(row, column)) {
				setBackground(table.getSelectionBackground());
				// setForeground(table.getSelectionForeground());
				setForeground(Color.RED);
			} else {
				setBackground(rowColor(row));
				setForeground(getForeground());
			}

			setForeground(Color.black);

			if (value != null) {
				try {
					double val = Double.parseDouble((String) value);
					if (val < 0 && doRedNegative) {
						setForeground(Color.red);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}

			setText((String) value);
			return this;

		}

		// shade alternate rows
		private Color rowColor(int row) {
			Color c;
			// if (row % 2 == 0)
			// c = EVEN_ROW_COLOR;
			// else
			c = table.getBackground();
			return c;
		}

	}

	// ====================================================
	// Cell Editor
	// ====================================================

	private class MyEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		public MyEditor() {
			super(new MyTextFieldD(app));
			this.setClickCountToStart(1);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			JTextField editor = (JTextField) super.getTableCellEditorComponent(
					table, value, isSelected, row, column);
			editor.setForeground(Color.RED);
			editor.setFont(app.getPlainFont());
			return editor;
		}

		@Override
		public boolean stopCellEditing() {
			boolean isStopped = super.stopCellEditing();

			try {
				if (isStopped) {
					double val = Double
							.parseDouble((String) this.getCellEditorValue());
					// change
					inspector.changeStart(val);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			return isStopped;
		}
	}

}
