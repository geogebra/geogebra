package geogebra.gui.view.data;

import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.view.spreadsheet.MyTableD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class StatTable extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private MyTable myTable;
	private MyRowHeader rowHeader;
	boolean isRowHeaderPainted = true;

	// layout
	private static final Color TABLE_GRID_COLOR = DataAnalysisViewD.TABLE_GRID_COLOR;
	private static final Color TABLE_HEADER_COLOR = DataAnalysisViewD.TABLE_HEADER_COLOR;
	private static final Color SELECTED_BACKGROUND_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR);

	protected DefaultTableModel tableModel;
	private HashMap<Point, MyComboBoxEditor> comboBoxEditorMap;
	private HashMap<Point, MyComboBoxRenderer> comboBoxRendererMap;
	private ActionListener al;
	AppD app;

	public StatTable(AppD app) {

		this.app = app;

		// create and initialize the table
		initTable();

		// enclose the table in this scrollPane
		setViewportView(myTable);
		myTable.setBorder(BorderFactory.createEmptyBorder());
		// setBorder(BorderFactory.createEmptyBorder());
		myTable.setBorder(BorderFactory
				.createLineBorder(SystemColor.controlShadow));

		// set the corners
		setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new Corner());
		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, new Corner());
		setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, new Corner());
		this.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, new Corner());

		if (isRowHeaderPainted) {
			((JPanel) this.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER))
					.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
							TABLE_GRID_COLOR));
		}

		myTable.setPreferredScrollableViewportSize(myTable.getPreferredSize());
		myTable.setBackground(this.getBackground());

	}

	public MyTable getTable() {
		return myTable;
	}

	private void initTable() {

		myTable = new MyTable();

		// table settings
		myTable.setDefaultRenderer(Object.class, new MyCellRenderer(this));
		myTable.setColumnSelectionAllowed(true);
		myTable.setRowSelectionAllowed(true);
		myTable.setShowGrid(true);
		myTable.setGridColor(TABLE_GRID_COLOR);
		myTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		// ((JLabel)
		// statTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		myTable.setBackground(Color.white);

	}

	private static class Corner extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(this.getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	/**
	 * Sets the dimensions and header values for the table. This should only be
	 * called once.
	 * 
	 * @param rows
	 *            number of rows
	 * @param rowNames
	 *            array of row header strings, if null then a row header is not
	 *            drawn
	 * @param columns
	 *            number of columns
	 * @param columnNames
	 *            array of column header strings, if null then a column header
	 *            is not drawn
	 */
	public void setStatTable(int rows, String[] rowNames, int columns,
			String[] columnNames) {

		// TODO: cannot remove columns ... call this again with fewer columns
		// and the older columns persist ????

		tableModel = new DefaultTableModel(rows, columns);
		myTable.setModel(tableModel);

		// set column names
		if (columnNames == null) {
			myTable.setTableHeader(null);
			this.setColumnHeaderView(null);
		} else {
			tableModel.setColumnCount(0);
			for (int i = 0; i < columnNames.length; i++)
				tableModel.addColumn(columnNames[i]);
		}

		// create row header
		if (rowNames != null) {
			rowHeader = new MyRowHeader(myTable, rowNames, this);
			// rowHeaderModel = new DefaultListModel();
			// .setModel(rowHeaderModel);
			setRowHeaderView(rowHeader);
		} else {
			setRowHeaderView(null);
		}

		myTable.setPreferredScrollableViewportSize(myTable.getPreferredSize());
		// statTable.setMinimumSize(statTable.getPreferredSize());

		this.revalidate();

		repaint();

	}

	/**
	 * Sets all cells values to the blank string " ". Does not change table
	 * dimensions.
	 */
	public void clear() {
		for (int r = 0; r < myTable.getRowCount(); r++)
			for (int c = 0; c < myTable.getColumnCount(); c++)
				myTable.setValueAt(" ", r, c);
	}

	/**
	 * Sets the table cells that will use a ComboBox
	 * 
	 * @param cellMap
	 */
	public void setComboBoxCells(HashMap<Point, String[]> cellMap,
			ActionListener al) {

		this.al = al;

		if (comboBoxEditorMap == null)
			comboBoxEditorMap = new HashMap<Point, MyComboBoxEditor>();
		comboBoxEditorMap.clear();
		if (comboBoxRendererMap == null)
			comboBoxRendererMap = new HashMap<Point, MyComboBoxRenderer>();
		comboBoxRendererMap.clear();

		for (Point cell : cellMap.keySet()) {

			// get the String data for this combo box
			String[] items = cellMap.get(cell);

			// extract the menu items and the combo box label
			String comboBoxLabel = items[items.length - 1];
			String[] comboBoxItems = new String[items.length - 1];
			System.arraycopy(items, 0, comboBoxItems, 0, comboBoxItems.length);

			// create the comboBox editors/renderers and map them
			comboBoxEditorMap.put(cell, new MyComboBoxEditor(comboBoxItems));
			comboBoxRendererMap.put(cell, new MyComboBoxRenderer(comboBoxLabel,
					comboBoxItems));

		}
	}

	/**
	 * Gets the selected index for a cell given cell comboBox
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public Integer getComboCellEditorSelectedIndex(int row, int column) {
		if (comboBoxEditorMap == null)
			return null;

		int modelColumn = myTable.convertColumnIndexToModel(column);
		Point cell = new Point(row, modelColumn);
		if (comboBoxEditorMap.keySet().contains(cell)) {
			return comboBoxEditorMap.get(cell).getSelectedIndex();
		}
		return null;
	}

	/**
	 * Sets the selected index for a cell given cell comboBox
	 * 
	 * @param index
	 * @param row
	 * @param column
	 * @return
	 */
	public boolean setComboCellSelectedIndex(int index, int row, int column) {

		if (comboBoxRendererMap == null)
			return false;

		int modelColumn = myTable.convertColumnIndexToModel(column);
		Point cell = new Point(row, modelColumn);

		if (comboBoxEditorMap.keySet().contains(cell)) {
			comboBoxEditorMap.get(cell).setSelectedIndex(index);
			return true;
		}
		return false;
	}

	public void setLabels(String[] rowNames, String[] columnNames) {

		// set column names
		if (columnNames != null) {
			for (int i = 0; i < columnNames.length; i++)
				myTable.getColumnModel().getColumn(i)
						.setHeaderValue(columnNames[i]);
		}

		if (rowNames != null) {
			rowHeader = new MyRowHeader(myTable, rowNames, this);
			setRowHeaderView(rowHeader);
		}

		repaint();
	}

	public DefaultTableModel getModel() {
		return tableModel;
	}

	public void updateFonts(Font font) {
		setFont(font);

		if (myTable != null && myTable.getRowCount() > 0) {
			myTable.setFont(font);
			autoFitRowHeight();
			if (rowHeader != null) {
				rowHeader.setFont(font);
				rowHeader.setFixedCellHeight(myTable.getRowHeight());
			}

			if (myTable.getTableHeader() != null)
				myTable.getTableHeader().setFont(font);
		}

		if (myTable != null)
			myTable.setPreferredScrollableViewportSize(myTable
					.getPreferredSize());
	}

	/**
	 * Adjust the width of a column to fit the maximum preferred width of its
	 * cell contents.
	 */
	public void autoFitColumnWidth(int column, int defaultColumnWidth) {

		MyTable table = myTable;
		if (table.getRowCount() <= 0)
			return;

		TableColumn tableColumn = table.getColumnModel().getColumn(column);

		int prefWidth = 0;
		int tempWidth = -1;
		for (int row = 0; row < table.getRowCount(); row++) {
			if (table.getValueAt(row, column) != null) {
				tempWidth = (int) table
						.getCellRenderer(row, column)
						.getTableCellRendererComponent(table,
								table.getValueAt(row, column), false, false,
								row, column).getPreferredSize().getWidth();
				prefWidth = Math.max(prefWidth, tempWidth);
			}
		}

		// set the new column width
		if (tempWidth == -1) {
			// column is empty
			prefWidth = defaultColumnWidth - table.getIntercellSpacing().width;
		} else {

			prefWidth = Math.max(prefWidth, tableColumn.getMinWidth());
			// System.out.println("pref width: " + prefWidth);
		}
		table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(prefWidth + table.getIntercellSpacing().width);
	}

	/**
	 * Adjust the height of a row to fit the maximum preferred height of its
	 * cell contents.
	 */
	public void autoFitRowHeight() {

		MyTable table = myTable;
		if (table.getRowCount() <= 0)
			return;

		// iterate through the rows and find the preferred height
		int prefHeight = table.getRowHeight();
		int tempHeight = -1;
		for (int row = 0; row < table.getRowCount(); row++) {
			for (int column = 0; column < table.getColumnCount(); column++) {
				if (table.getValueAt(row, column) != null) {
					tempHeight = (int) table
							.getCellRenderer(row, column)
							.getTableCellRendererComponent(table,
									table.getValueAt(row, column), false,
									false, row, column).getPreferredSize()
							.getHeight();
					prefHeight = Math.max(prefHeight, tempHeight);
				}
			}
		}

		// set the new row height
		table.setRowHeight(prefHeight);
	}

	private int alignment = SwingConstants.LEFT;

	public void setHorizontalAlignment(int alignment) {
		this.alignment = alignment;

		TableCellRenderer renderer = myTable.getTableHeader()
				.getDefaultRenderer();
		JLabel label = (JLabel) renderer;
		label.setHorizontalAlignment(alignment);
	}

	public int getHorizontalAlignment() {
		return alignment;
	}

	// ======================================================
	// Table Cell Renderer
	// ======================================================

	private static class MyCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private StatTable statTable;

		public MyCellRenderer(StatTable statTable) {
			// cell padding
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
			this.statTable = statTable;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setFont(table.getFont());
			setText((String) value);
			setHorizontalAlignment(statTable.getHorizontalAlignment());

			if (isSelected)
				setBackground(SELECTED_BACKGROUND_COLOR);
			else
				setBackground(Color.white);

			return this;
		}

	}

	// ======================================================
	// Row Header
	// ======================================================

	public class MyRowHeader extends JList {
		private static final long serialVersionUID = 1L;
		JTable table;
		private StatTable statTable;

		public MyRowHeader(JTable table, String[] rowNames, StatTable statTable) {
			super(rowNames);
			this.table = table;
			this.statTable = statTable;
			setCellRenderer(new RowHeaderRenderer(table));
			setFixedCellHeight(table.getRowHeight());

		}

		class RowHeaderRenderer extends JLabel implements ListCellRenderer {
			private static final long serialVersionUID = 1L;

			public RowHeaderRenderer(JTable table) {

				if (isRowHeaderPainted) {
					setOpaque(true);
					setBackground(TABLE_HEADER_COLOR);

					setBorder(BorderFactory.createCompoundBorder(BorderFactory
							.createMatteBorder(0, 0, 1, 1, TABLE_GRID_COLOR),
							BorderFactory.createEmptyBorder(2, 5, 2, 5)));
				} else {
					setOpaque(true);
					setBackground(table.getBackground());
					setBorder(BorderFactory.createCompoundBorder(BorderFactory
							.createMatteBorder(0, 0, 0, 1, TABLE_GRID_COLOR),
							BorderFactory.createEmptyBorder(0, 5, 0, 5)));
				}

				setFont(table.getFont());

			}

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				setFont(table.getFont());
				setText((String) value);
				// setHorizontalAlignment(statTable.getHorizontalAlignment());
				return this;
			}
		}
	}

	// ======================================================
	// ComboBox Renderer
	// ======================================================

	public class MyComboBoxRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		JComboBox comboBox;
		JLabel label;

		public MyComboBoxRenderer(String text, String[] items) {

			setLayout(new BorderLayout());
			comboBox = new JComboBox(items);
			add(comboBox, BorderLayout.EAST);
			if (text != null) {
				label = new JLabel(text);
				add(label, BorderLayout.CENTER);
			}

		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			setFont(table.getFont());
			setForeground(table.getForeground());
			setBackground(table.getBackground());
			comboBox.setSelectedIndex(getComboCellEditorSelectedIndex(row,
					column));
			return this;
		}
	}

	// ======================================================
	// ComboBox Editor
	// ======================================================

	public class MyComboBoxEditor extends DefaultCellEditor implements
			ItemListener {
		private static final long serialVersionUID = 1L;
		JComboBox comboBox;
		JLabel label;
		int row, column;

		public MyComboBoxEditor(String[] items) {
			super(new JComboBox(items));
			comboBox = (JComboBox) editorComponent;
			comboBox.addItemListener(this);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			setFont(table.getFont());
			this.row = row;
			this.column = column;
			return editorComponent;

		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}

			myTable.getModel().setValueAt(comboBox.getSelectedIndex(), row,
					column);
			al.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, "updateTable"));
		}

		public int getSelectedIndex() {
			return comboBox.getSelectedIndex();
		}

		public void setSelectedIndex(int index) {
			comboBox.setSelectedIndex(index);
		}

	}

	/**
	 * @param allowCellEdit true if table cell can be edited
	 */
	public void setAllowCellEdit(boolean allowCellEdit) {
		myTable.setAllowCellEdit(allowCellEdit);
	}
	

	// ======================================================
	// MyTable
	// ======================================================
	public class MyTable extends JTable {
		private static final long serialVersionUID = 1L;
		
		private boolean allowCellEdit = false;
		
		/**
		 * @param allowCellEdit true if table cell can be edited
		 */
		public void setAllowCellEdit(boolean allowCellEdit) {
			this.allowCellEdit = allowCellEdit;
		}

		// disable cell editing
		@Override
		public boolean isCellEditable(int rowIndex, int colIndex) {

			if (allowCellEdit == true) {
				return true;
			}

			if (comboBoxEditorMap == null)
				return false;

			int modelColumn = convertColumnIndexToModel(colIndex);
			Point cell = new Point(rowIndex, modelColumn);
			return comboBoxEditorMap.keySet().contains(cell);
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

		// Determine if comboCellEditor should be used
		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			if (comboBoxEditorMap == null)
				return super.getCellEditor(row, column);

			int modelColumn = convertColumnIndexToModel(column);
			Point cell = new Point(row, modelColumn);
			if (comboBoxEditorMap.keySet().contains(cell)) {
				return comboBoxEditorMap.get(cell);
			}
			return super.getCellEditor(row, column);
		}

		// Determine if comboCellRenderer should be used
		@Override
		public TableCellRenderer getCellRenderer(int row, int column) {
			if (comboBoxRendererMap == null)
				return super.getCellRenderer(row, column);

			int modelColumn = convertColumnIndexToModel(column);
			Point cell = new Point(row, modelColumn);
			if (comboBoxRendererMap.keySet().contains(cell)) {
				return comboBoxRendererMap.get(cell);
			}
			return super.getCellRenderer(row, column);
		}
	}

}
