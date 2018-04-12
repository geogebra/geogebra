package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataItem;
import org.geogebra.common.gui.view.data.DataSource;
import org.geogebra.common.gui.view.data.DataVariable;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.Validation;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.inputfield.MathTextField;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Panel to manage data sources for the DataAnalysisView
 * 
 * @author G. Sturr
 * 
 */
public class DataSourcePanel extends JPanel
		implements ActionListener, FocusListener {

	private static final long serialVersionUID = 1L;

	private static final int MINIMUM_ROW = 8;
	/** application */
	final AppD app;
	/** localization */
	final LocalizationD loc;
	private DataAnalysisViewD dataView;

	// data source and table
	protected DataSource dataSource;
	protected StatTable sourceTable;

	// GUI elements
	private JPanel mainPanel, classesPanel, controlPanel;
	private JLabel lblTitle, lblStart, lblWidth;
	private MyButton btnAdd, btnClear, btnDelete, btnOptions;
	private MyTextFieldD fldStart, fldWidth;

	// flags and other fields
	/** current mode */
	int mode;
	protected int btnHoverColumn = -1;

	private int selectedVarIndex() {
		return dataSource.getSelectedIndex();
	}

	private String[] columnDataTitles;

	public String[] getColumnDataTitles() {
		return columnDataTitles;
	}

	/*************************************************
	 * Constructor
	 * 
	 * @param app
	 * @param mode
	 */
	public DataSourcePanel(AppD app, int mode) {

		this.app = app;
		this.loc = app.getLocalization();
		this.mode = mode;
		dataSource = new DataSource(app);

		createGUIElements();
		createSourceTable();

		updatePanel(mode, true);
		setLabels();
		addFocusListener(this);

	}

	// ====================================================
	// GUI
	// ====================================================

	public void updatePanel(int newMode, boolean doAutoLoadSelectedGeos) {
		this.mode = newMode;

		if (doAutoLoadSelectedGeos) {
			dataSource.setDataListFromSelection(newMode);
		}

		buildGUI();
		updateGUI();
		loadSourceTableFromDataSource();
		revalidate();

	}

	private void buildGUI() {

		buildMainPanel();

		removeAll();
		setLayout(new BorderLayout(2, 2));
		setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));
		add(mainPanel, BorderLayout.CENTER);

	}

	private void createGUIElements() {

		lblTitle = new JLabel();

		btnAdd = new MyButton(app.getScaledIcon(GuiResourcesD.LIST_ADD));
		btnAdd.addActionListener(this);

		btnClear = new MyButton(app.getScaledIcon(GuiResourcesD.EDIT_CLEAR));
		btnClear.addActionListener(this);

		btnDelete = new MyButton(app.getScaledIcon(GuiResourcesD.LIST_REMOVE));
		btnDelete.addActionListener(this);

		btnOptions = new MyButton(
				app.getScaledIcon(GuiResourcesD.VIEW_PROPERTIES_16));
		btnOptions.addActionListener(this);

		lblStart = new JLabel();
		lblWidth = new JLabel();

		fldStart = new MyTextFieldD(app, 4);
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldStart.addActionListener(this);
		fldStart.setText("" + 0.0);
		fldStart.addFocusListener(this);

		fldWidth = new MyTextFieldD(app, 4);
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.addActionListener(this);
		fldWidth.setText("" + 1.0);
		fldWidth.addFocusListener(this);

	}

	private void updateIcons() {
		lblTitle.setIcon(app.getModeIcon(mode));
		btnAdd.setIcon(app.getScaledIcon(GuiResourcesD.LIST_ADD));
		btnClear.setIcon(app.getScaledIcon(GuiResourcesD.EDIT_CLEAR));
		btnDelete.setIcon(app.getScaledIcon(GuiResourcesD.LIST_REMOVE));
		btnOptions.setIcon(app.getScaledIcon(GuiResourcesD.VIEW_PROPERTIES_16));
	}

	private void buildMainPanel() {

		if (mainPanel == null) {
			mainPanel = new JPanel(new BorderLayout(0, 5));
		}

		mainPanel.removeAll();

		buildControlPanel();
		buildClassesPanel();

		mainPanel.add(controlPanel, BorderLayout.NORTH);
		mainPanel.add(sourceTable, BorderLayout.CENTER);
		mainPanel.add(classesPanel, BorderLayout.SOUTH);

	}

	private void buildControlPanel() {

		if (controlPanel == null) {
			controlPanel = new JPanel();
			controlPanel.setLayout(new BorderLayout(0, 0));
		}

		controlPanel.removeAll();
		if (mode == DataAnalysisModel.MODE_MULTIVAR) {
			controlPanel.add(LayoutUtil.flowPanel(0, 0, 0, btnAdd, btnDelete),
					loc.borderWest());
		}

		controlPanel.add(LayoutUtil.flowPanel(0, 0, 0, btnOptions),
				loc.borderEast());

	}

	private void buildClassesPanel() {
		classesPanel = LayoutUtil.flowPanel(4, 2, 0, lblStart, fldStart,
				lblWidth, fldWidth);
	}

	// ====================================================
	// Updates
	// ====================================================

	public void setLabels() {

		lblStart.setText(loc.getMenu("Start") + ":");
		lblWidth.setText(loc.getMenu("Width") + ":");

		btnOptions.setToolTipText(loc.getMenu("Options"));
		btnClear.setToolTipText(loc.getMenu("ClearColumns"));
		btnDelete.setToolTipText(loc.getMenu("fncInspector.removeColumn"));
		btnAdd.setToolTipText(loc.getMenu("fncInspector.addColumn"));

	}


	protected void updateGUI() {

		lblTitle.setIcon(app.getModeIcon(mode));
		classesPanel.setVisible(dataSource.getGroupType() == GroupType.CLASS);

		// updateSourceTableStructure();
		this.revalidate();
		this.repaint();
	}

	private void createSourceTable() {

		int rowCount = 8;
		int columnCount = 4;

		columnDataTitles = new String[columnCount];

		sourceTable = new StatTable(app);
		sourceTable.setHorizontalAlignment(SwingConstants.CENTER);
		sourceTable.setBorder(BorderFactory.createEmptyBorder());

		sourceTable.setStatTable(rowCount, null, columnCount, columnDataTitles);
		sourceTable.getTable().setColumnSelectionAllowed(false);
		sourceTable.getTable().setRowSelectionAllowed(false);
		sourceTable.getTable()
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceTable.setAllowCellEdit(false);
		sourceTable.getTable().getTableHeader().setReorderingAllowed(false);

		sourceTable.clear();

		// sourceTable.getTable().setCellEditor(new MyCellEditor(app));

		sourceTable.getTable().getModel()
				.addTableModelListener(new TableModelListener() {

					@Override
					public void tableChanged(TableModelEvent e) {
						if (e.getType() == TableModelEvent.UPDATE) {
							// updateTableEdit(e.getColumn());
						}
					}
				});

		setColumnHeaders(sourceTable.getTable());

		sourceTable.getTable().getColumnModel().getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						sourceTable.revalidate();
						sourceTable.repaint();
					}
				});

	}

	private static void setTableDimension(JTable table) {

		// height determined by visible rows
		int rows = 8;
		int height = rows * table.getRowHeight();

		// width determined by preferred size within min and max bounds
		int minWidth = 250;
		int maxWidth = 400;
		int width = Math.min(maxWidth,
				Math.max(minWidth, table.getPreferredSize().width));

		table.setPreferredScrollableViewportSize(new Dimension(width, height));
	}

	private void loadSourceTableFromDataSource() {

		DefaultTableModel model = (DefaultTableModel) sourceTable.getTable()
				.getModel();

		if (dataSource.isEmpty()) {
			// create an empty table (should not happen)
			sourceTable.clear();
			model.setColumnCount(1);
			model.setRowCount(MINIMUM_ROW);
			columnDataTitles = new String[1];

		} else {
			columnDataTitles = dataSource.getTitles();
			model.setDataVector(dataSource.getTableData(),
					dataSource.getDescriptions());
		}

		if (model.getRowCount() < MINIMUM_ROW) {
			model.setRowCount(MINIMUM_ROW);
		}

		setColumnHeaders(sourceTable.getTable());
		sourceTable.getTable().getTableHeader()
				.addMouseListener(new ColumnHeaderMouseListener());
		sourceTable.getTable().getTableHeader()
				.addMouseMotionListener(new ColumnHeaderMouseMotionListener());

		sourceTable.updateFonts(app.getPlainFont());
		setTableDimension(sourceTable.getTable());
		this.revalidate();
		this.repaint();

	}

	/**
	 * Loads data from the dataSource list at the given index position into the
	 * corresponding source table column.
	 * 
	 * @param colIndex
	 */

	/*
	 * private void setTableColumn(int colIndex) {
	 * 
	 * if (dataSource.getItem(colIndex) == null ||
	 * dataSource.getItem(colIndex).isEmptyItem()) { return; }
	 * 
	 * DefaultTableModel model = sourceTable.getModel();
	 * 
	 * try { switch (dataSource.getItem(colIndex).getType()) {
	 * 
	 * case LIST: GeoList geoList = dataSource.getItem(colIndex).getGeoList();
	 * 
	 * // ensure the table has enough rows if (model.getRowCount() <
	 * geoList.size()) { model.setRowCount(geoList.size()); }
	 * 
	 * for (int i = 0; i < geoList.size(); i++) {
	 * 
	 * if (geoList.get(i) == null || !(geoList.get(i).isDefined())) { continue;
	 * } if (isValidDataType(geoList.get(i), colIndex)) {
	 * model.setValueAt(geoList.get(i).getValueForInputBar(), i, colIndex); }
	 * else { model.setValueAt("<html><i><font color = gray>" +
	 * geoList.get(i).getValueForInputBar() + "</font></i></html>", i,
	 * colIndex); } }
	 * 
	 * break;
	 * 
	 * case SPREADSHEET: ArrayList<CellRange> rangeList =
	 * dataSource.getItem(colIndex) .getRangeList(); int maxRow = 0; int row =
	 * 0; boolean skipFirstCell = dataSource.enableHeader();
	 * 
	 * for (CellRange cr : rangeList) {
	 * 
	 * ArrayList<GeoElement> list = cr.toGeoList(); maxRow += list.size();
	 * 
	 * // ensure the table has enough rows if (model.getRowCount() < maxRow) {
	 * model.setRowCount(maxRow); }
	 * 
	 * // iterate through the list and set the row values for (int i = 0; i <
	 * list.size(); i++) { if (skipFirstCell) { skipFirstCell = false; continue;
	 * } if (list.get(i) == null || !(list.get(i).isDefined())) { continue; } if
	 * (isValidDataType(list.get(i), colIndex)) {
	 * model.setValueAt(list.get(i).getValueForInputBar(), row, colIndex); }
	 * else { model.setValueAt("<html><i><font color = gray>" +
	 * list.get(i).getValueForInputBar() + "</font></i></html>", row, colIndex);
	 * } row++; } } break;
	 * 
	 * case INTERNAL: String[] str =
	 * dataSource.getItem(colIndex).getStrInternal();
	 * 
	 * // ensure the table has enough rows if (model.getRowCount() < str.length)
	 * { model.setRowCount(str.length); }
	 * 
	 * // load the array into the column for (int i = 0; i <
	 * model.getRowCount(); i++) { if (i < str.length && str[i] != null) {
	 * model.setValueAt(str[i], i, colIndex); } else { model.setValueAt(" ", i,
	 * colIndex); } } break;
	 * 
	 * case CLASS: Double[] leftBorder = dataSource.getItem(colIndex)
	 * .getLeftBorder(); // System.out.println("=====> " +
	 * Arrays.toString(leftBorder));
	 * 
	 * // ensure the table has enough rows if (model.getRowCount() <
	 * leftBorder.length - 1) { model.setRowCount(leftBorder.length - 1); }
	 * 
	 * // load the array into the column for (int i = 0; i < leftBorder.length -
	 * 1; i++) { if (i < leftBorder.length && leftBorder[i] != null) { String
	 * interval = leftBorder[i] + " - " + leftBorder[i + 1];
	 * model.setValueAt(interval, i, colIndex); } else { model.setValueAt(" ",
	 * i, colIndex); } } }
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * // sourceTable.getTable().setColumnSelectionInterval(colIndex, //
	 * colIndex); sourceTable.getTable().revalidate();
	 * 
	 * }
	 */
	/*
	 * private boolean isValidDataType(GeoElement geo, int colIndex) {
	 * 
	 * switch (mode) {
	 * 
	 * case DataAnalysisModel.MODE_ONEVAR: if (dataSource.isNumericData()) {
	 * return geo.isGeoNumeric(); } return geo.isGeoText();
	 * 
	 * case DataAnalysisModel.MODE_REGRESSION: if (dataSource.isPointList()) {
	 * return geo.isGeoPoint(); } return geo.isGeoNumeric();
	 * 
	 * case DataAnalysisModel.MODE_MULTIVAR: return geo.isGeoNumeric();
	 * 
	 * default:
	 * 
	 * return false; } }
	 */

	/**
	 * Sets the dataSource field at the given index to refer to the currently
	 * selected geos and fills the corresponding column in the data table with
	 * data from these geos.
	 * 
	 */
	void addDataToColumn(int colIndex) {

		dataSource.setDataItemToGeoSelection(selectedVarIndex(), colIndex);

		loadSourceTableFromDataSource();
		updateGUI();

	}

	// ====================================================
	// Event handlers
	// ====================================================

	/**
	 * Handles button clicks
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed(source);

		} else if (source == btnAdd) {
			dataSource.getSelectedDataVariable().addNewValue();
			updatePanel(DataAnalysisModel.MODE_MULTIVAR, false);

		} else if (source == btnClear) {
			// int n = dataSource.size();
			// dataSource.clear();
			// dataSource.ensureMinimumSize(n);
			// loadSourceTableFromDataSource();
		}

		else if (source == btnDelete) {
			if (dataSource.getSelectedDataVariable().getValues().size() > 2) {
				dataSource.getSelectedDataVariable().removeLastValue();
				loadSourceTableFromDataSource();
				updatePanel(DataAnalysisModel.MODE_MULTIVAR, false);
			}
		}

		else if (source == btnOptions) {
			JPopupMenu optionsPopup = getOptionsMenu();
			optionsPopup.show(btnOptions, 0, btnOptions.getHeight());
		}

		updateGUI();
		revalidate();

	}

	private void doTextFieldActionPerformed(Object source) {

		if (!(source instanceof JTextField)) {
			return;
		}
		((JTextField) source).setText(((JTextField) source).getText().trim());

		if (source == fldStart) {
			dataSource.setClassStart(Validation.validateDouble(fldStart,
					dataSource.getClassStart()));
			updatePanel(mode, false);

		} else if (source == fldWidth) {
			dataSource.setClassWidth(Validation.validateDouble(fldWidth,
					dataSource.getClassWidth()));
			updatePanel(mode, false);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// do nothing
	}

	@Override
	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed(e.getSource());
	}

	public void updateFonts(Font font) {
		setFont(font);
		sourceTable.updateFonts(font);
		updateIcons();
	}

	public void applySettings() {

		if (dataView == null) {
			dataView = (DataAnalysisViewD) app.getGuiManager()
					.getDataAnalysisView();
		}

		dataView.setView(dataSource, mode, true);
	}

	// ====================================================
	// Column Header
	// ====================================================

	private void setColumnHeaders(JTable table) {

		MyTableHeaderRenderer headerRenderer = new MyTableHeaderRenderer();

		for (int vColIndex = 0; vColIndex < table.getColumnModel()
				.getColumnCount(); vColIndex++) {
			TableColumn col = table.getColumnModel().getColumn(vColIndex);
			col.setHeaderRenderer(headerRenderer);
		}

	}

	public class ColumnHeaderMouseMotionListener
			implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// nothing to do
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			// handles mouse over a button
			boolean isOver = false;
			Point mouseLoc = e.getPoint();
			int column = table().getColumnModel().getColumnIndexAtX(e.getX());

			// adjust mouseLoc to the coordinate space of this column header
			mouseLoc.x = mouseLoc.x - table().getCellRect(0, column, true).x;

			isOver = ((MyTableHeaderRenderer) table().getColumnModel()
					.getColumn(column).getHeaderRenderer()).isOverTraceButton(
							column, mouseLoc, table().getColumnModel()
									.getColumn(column).getHeaderValue());

			if (isOver && (btnHoverColumn != column)) {
				btnHoverColumn = column;

				if (table().getTableHeader() != null) {
					table().getTableHeader().resizeAndRepaint();
				}
			}

			if (!isOver && (btnHoverColumn == column)) {
				btnHoverColumn = -1;

				if (table().getTableHeader() != null) {
					table().getTableHeader().resizeAndRepaint();
				}
			}

		}

	}

	public class ColumnHeaderMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent evt) {

			JTable table = sourceTable.getTable();
			TableColumnModel colModel = table.getColumnModel();

			// The index of the column whose header was clicked
			int vColIndex = colModel.getColumnIndexAtX(evt.getX());
			table.convertColumnIndexToModel(vColIndex);

			// Determine if mouse was clicked between column heads
			Rectangle headerRect = table.getTableHeader()
					.getHeaderRect(vColIndex);
			if (vColIndex == 0) {
				headerRect.width -= 3; // Hard-coded constant
			} else {
				headerRect.grow(-3, 0); // Hard-coded constant
			}
			if (!headerRect.contains(evt.getX(), evt.getY())) {
				// Mouse was clicked between column heads
				// vColIndex is the column head closest to the click
				if (evt.getX() < headerRect.x) {
					// TODO do something
				}
			}

			// select the header column in the table if not already selected
			if (vColIndex != table.getSelectedColumn()) {
				table.setColumnSelectionInterval(vColIndex, vColIndex);
			}
			if (vColIndex == btnHoverColumn) {
				int selectedColumn = table.getSelectedColumn();
				addDataToColumn(vColIndex);
				table.setColumnSelectionInterval(selectedColumn,
						selectedColumn);
				btnHoverColumn = -1;
			}

		}

		@Override
		public void mouseExited(MouseEvent evt) {

			if (btnHoverColumn > -1) {
				btnHoverColumn = -1;
				if (table().getTableHeader() != null) {
					table().getTableHeader().resizeAndRepaint();
				}
			}
		}

	}

	protected JTable table() {
		return sourceTable.getTable();
	}

	/*************************************************
	 * Custom table header render.
	 * 
	 * Displays title and source labels for data columns. Supports a data import
	 * button for a selected column.
	 * 
	 */
	public class MyTableHeaderRenderer extends JPanel
			implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		private JLabel lblDataDescription, lblDataTitle, lblImportBtn;

		protected Border headerBorder = UIManager
				.getBorder("TableHeader.cellBorder");

		protected Font font = UIManager.getFont("TableHeader.font");
		private ImageIcon importIcon, importIconRollover;

		public MyTableHeaderRenderer() {
			setLayout(new BorderLayout());
			setOpaque(true);
			setBorder(headerBorder);

			lblDataDescription = new JLabel("", SwingConstants.CENTER);
			lblDataDescription.setForeground(Color.WHITE);
			lblDataDescription.setBackground(Color.LIGHT_GRAY);
			lblDataDescription.setOpaque(true);
			lblDataDescription
					.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 2));

			lblImportBtn = new JLabel("", SwingConstants.LEFT);
			lblImportBtn.setForeground(Color.WHITE);
			lblImportBtn.setBackground(Color.LIGHT_GRAY);
			lblImportBtn.setOpaque(true);
			lblImportBtn
					.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 10));
			lblImportBtn.setPreferredSize(new Dimension(20, 20));

			lblDataTitle = new JLabel("", SwingConstants.CENTER);
			lblDataTitle.setForeground(Color.BLACK);
			lblDataTitle.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

			importIcon = app.getScaledIcon(GuiResourcesD.ARROW_CURSOR_GRABBING);
			importIconRollover = app.getScaledIcon(
					GuiResourcesD.ARROW_CURSOR_GRABBING_ROLLOVER);

		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int vColIndex) {

			removeAll();
			invalidate();

			if (value != null) {
				lblDataDescription.setText(value.toString());
			} else {
				lblDataDescription.setText("  ");
			}
			lblDataDescription.setFont(app.getPlainFont());

			// set lblDataTitle text to the title string for this column's
			// DataItem
			lblDataTitle.setText(getColumnDataTitles()[vColIndex]);
			lblDataTitle.setFont(app.getItalicFont());
			if (!lblDataDescription.getText().equals(loc.getMenu("Classes")))
			// && vColIndex == table.getSelectedColumn()) {
			{
				if (btnHoverColumn == vColIndex) {
					lblImportBtn.setIcon(importIconRollover);
					setToolTipText(loc.getMenuTooltip("AddSelection"));
				} else {
					lblImportBtn.setIcon(importIcon);
					setToolTipText(null);
				}
			} else {
				lblImportBtn.setIcon(null);
			}

			// layout the header
			JPanel titlePanel = new JPanel(new BorderLayout(0, 0));
			titlePanel.add(lblImportBtn, BorderLayout.WEST);
			titlePanel.add(lblDataDescription, BorderLayout.CENTER);

			JPanel headerPanel = new JPanel(new BorderLayout(0, 0));
			headerPanel.add(titlePanel, BorderLayout.CENTER);
			headerPanel.add(lblDataTitle, BorderLayout.SOUTH);
			add(headerPanel, BorderLayout.CENTER);

			if (vColIndex == table.getSelectedColumn()) {
				setBackground(GColorD.getAwtColor(
						GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER));
			} else {
				setBackground(GColorD.getAwtColor(
						GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
			}

			return this;
		}


		/**
		 * Returns true if the given mouse location (in local coordinates of the
		 * header component) is over a trace button.
		 * 
		 * @param colIndex
		 * @param locPt
		 * @param value
		 * @return
		 */
		public boolean isOverTraceButton(int colIndex, Point locPt,
				Object value) {

			try {
				return locPt.x < 24;
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return false;
		}

	}

	/*************************************************
	 * Custom cell editor.
	 * 
	 * TODO: Currently not used. Remove if cell editing will never be allowed
	 * 
	 */
	public static class MyCellEditor extends DefaultCellEditor {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyCellEditor(MathTextField tf) {
			super(tf);
		}

		public MyCellEditor(AppD app) {
			this(new MathTextField(app));
		}

		@Override
		public boolean stopCellEditing() {

			// get the edit column while it is still available
			// int editColumn = sourceTable.getTable().getEditingColumn();

			// call super.stopCellEditing to update the table model
			boolean result = super.stopCellEditing();

			// now add the edit into the data source and exit
			// addEditedColumnToDataSource(editColumn);
			return result;
		}

	}

	private JPopupMenu getOptionsMenu() {

		JPopupMenu menu = new JPopupMenu();
		JMenu subMenu;
		final DataVariable var = dataSource.getSelectedDataVariable();

		if (mode == DataAnalysisModel.MODE_ONEVAR) {

			// ==========================
			// one var data type

			final JCheckBoxMenuItem itmNumeric = new JCheckBoxMenuItem(
					loc.getMenu("Number"));
			itmNumeric.setSelected(var.getGeoClass() == GeoClass.NUMERIC);
			itmNumeric.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					var.setGeoClass(GeoClass.NUMERIC);
					updatePanel(mode, false);
				}
			});

			final JCheckBoxMenuItem itemTypeText = new JCheckBoxMenuItem(
					loc.getMenu("Type.Text"));
			itemTypeText.setSelected(var.getGeoClass() == GeoClass.TEXT);
			itemTypeText.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					var.setGeoClass(GeoClass.TEXT);
					updatePanel(mode, false);
				}
			});

			ButtonGroup grp = new ButtonGroup();
			grp.add(itmNumeric);
			grp.add(itemTypeText);

			menu.add(itmNumeric);
			menu.add(itemTypeText);

			// ==========================
			// source type

			final JCheckBoxMenuItem itmRawData = new JCheckBoxMenuItem(
					loc.getMenu("RawData"));
			itmRawData.setSelected(var.getGroupType() == GroupType.RAWDATA);
			itmRawData.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (itmRawData.isSelected()
							&& var.getGroupType() != GroupType.RAWDATA) {
						var.setGroupType(GroupType.RAWDATA);
						updatePanel(mode, false);
					}
				}
			});

			final JCheckBoxMenuItem itmFrequency = new JCheckBoxMenuItem(
					loc.getMenu("DataWithFrequency"));
			itmFrequency.setSelected(var.getGroupType() == GroupType.FREQUENCY);
			itmFrequency.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (itmFrequency.isSelected()
							&& var.getGroupType() != GroupType.FREQUENCY) {
						var.setGroupType(GroupType.FREQUENCY);
						updatePanel(mode, false);
					}
				}
			});

			final JCheckBoxMenuItem itmClass = new JCheckBoxMenuItem(
					loc.getMenu("ClassWithFrequency"));
			itmClass.setSelected(var.getGroupType() == GroupType.CLASS);
			itmClass.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (itmClass.isSelected()
							&& var.getGroupType() != GroupType.CLASS) {
						var.setGroupType(GroupType.CLASS);
						updatePanel(mode, false);
					}
				}
			});

			ButtonGroup grp2 = new ButtonGroup();
			grp2.add(itmRawData);
			grp2.add(itmFrequency);
			grp2.add(itmClass);

			menu.addSeparator();
			menu.add(itmRawData);
			menu.add(itmFrequency);
			menu.add(itmClass);

		}

		if (mode == DataAnalysisModel.MODE_REGRESSION) {

			// ==========================
			// two var data type

			final JCheckBoxMenuItem itmNumeric = new JCheckBoxMenuItem(
					loc.getMenu("Number"));
			itmNumeric.setSelected(var.getGeoClass() == GeoClass.NUMERIC);
			itmNumeric.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ArrayList<DataItem> itemList = new ArrayList<>();
					itemList.add(new DataItem());
					itemList.add(new DataItem());
					var.setDataVariableAsRawData(GeoClass.NUMERIC, itemList);
					updatePanel(mode, false);
				}
			});

			final JCheckBoxMenuItem itmPoint = new JCheckBoxMenuItem(
					app.getLocalization().getMenu("Point"));
			itmPoint.setSelected(var.getGeoClass() == GeoClass.POINT);
			itmPoint.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ArrayList<DataItem> itemList = new ArrayList<>();
					itemList.add(new DataItem());
					var.setDataVariableAsRawData(GeoClass.POINT, itemList);
					updatePanel(mode, false);
				}
			});

			ButtonGroup grp = new ButtonGroup();
			grp.add(itmNumeric);
			grp.add(itmPoint);

			subMenu = new JMenu(loc.getMenu("DataType"));
			menu.add(subMenu);
			subMenu.add(itmNumeric);
			subMenu.add(itmPoint);

		}

		// ==========================
		// header as title

		final JCheckBoxMenuItem itmHeader = new JCheckBoxMenuItem(
				loc.getMenu("UseHeaderAsTitle"));
		itmHeader.setSelected(dataSource.enableHeader());
		itmHeader.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (dataSource.enableHeader() != itmHeader.isSelected()) {
					dataSource.setEnableHeader(itmHeader.isSelected());
					updatePanel(mode, false);
				}
			}
		});

		menu.addSeparator();
		menu.add(itmHeader);

		app.setComponentOrientation(menu);

		return menu;
	}

	private static class MyButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyButton(ImageIcon imageIcon) {
			super(imageIcon);
			setMargin(new Insets(0, 0, 0, 0));
			setBorderPainted(false);
			setContentAreaFilled(false);
			setFocusable(false);
		}
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/*
	 * private void updateClasses() {
	 * 
	 * int numClasses = 0;
	 * 
	 * if (dataSource.size() > 1) { DataItem frequencyData =
	 * dataSource.getItem(1);
	 * 
	 * if (frequencyData != null) { numClasses = frequencyData.getGeoCount(); }
	 * }
	 * 
	 * Double[] c = new Double[numClasses + 1]; c[0] = classStart; for (int i =
	 * 1; i < c.length; i++) { c[i] = c[i - 1] + classWidth; }
	 * 
	 * // System.out.println("========> classes:" + Arrays.toString(c) );
	 * dataSource.addItem(0, c, SourceType.CLASS); }
	 * 
	 * protected void addEditedColumnToDataSource(int colIndex) {
	 * 
	 * DefaultTableModel m = (DefaultTableModel) sourceTable.getTable()
	 * .getModel();
	 * 
	 * String[] s = new String[m.getRowCount()];
	 * 
	 * for (int i = 0; i < m.getRowCount(); i++) { s[i] = (String)
	 * m.getValueAt(i, colIndex); } // System.out.println(Arrays.toString(s));
	 * //dataSource.addItem(colIndex, s, SourceType.INTERNAL); //
	 * sourceTable.getTable().revalidate(); // sourceTable.getTable().repaint();
	 * 
	 * loadSourceTableFromDataSource();
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * /* private void updateSourceTableStructure() {
	 * 
	 * int columnCount = 1;
	 * 
	 * ArrayList<String> columnNameList = new ArrayList<String>();
	 * 
	 * switch (mode) {
	 * 
	 * case DataAnalysisModel.MODE_ONEVAR: if (dataSource.getGroupType() ==
	 * GroupType.RAWDATA) { columnCount = 1;
	 * columnNameList.add(loc.getMenu("Data")); } else if
	 * (dataSource.getGroupType() == GroupType.FREQUENCY) { columnCount = 2;
	 * columnNameList.add(loc.getMenu("Data"));
	 * columnNameList.add(loc.getMenu("Frequency")); } else if
	 * (dataSource.getGroupType() == GroupType.CLASS) { columnCount = 2;
	 * columnNameList.add(loc.getMenu("Classes"));
	 * columnNameList.add(loc.getMenu("Frequency")); } break;
	 * 
	 * case DataAnalysisModel.MODE_REGRESSION: if (dataSource.isPointList()) {
	 * columnCount = 1; columnNameList.add("(" + loc.getMenu("Column.X") + "," +
	 * loc.getMenu("Column.Y") + ")"); } else { columnCount = 2;
	 * columnNameList.add(loc.getMenu("Column.X"));
	 * columnNameList.add(loc.getMenu("Column.Y")); } break;
	 * 
	 * case DataAnalysisModel.MODE_MULTIVAR:
	 * 
	 * if (dataSource.size() > 2) { columnCount = dataSource.size(); } else {
	 * columnCount = 2; dataSource.ensureMinimumSize(2); }
	 * 
	 * for (int i = 1; i <= columnCount; i++) { columnNameList.add("# " + i); }
	 * 
	 * break; }
	 * 
	 * DefaultTableModel m = (DefaultTableModel) sourceTable.getTable()
	 * .getModel(); m.setColumnCount(columnCount);
	 * 
	 * for (int i = 0; i < columnCount; i++) {
	 * sourceTable.getTable().getColumnModel().getColumn(i)
	 * .setHeaderValue(columnNameList.get(i)); }
	 * 
	 * // sourceTable.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
	 * for (int i = 0; i < columnCount; i++) { //
	 * sourceTable.autoFitColumnWidth(i, 3); // } sourceTable.getTable()
	 * .setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	 * 
	 * sourceTable.getTable().setColumnSelectionInterval(0, 0);
	 * sourceTable.getTable().getTableHeader().setReorderingAllowed(false);
	 * 
	 * setColumnHeaders(sourceTable.getTable());
	 * 
	 * sourceTable.getTable().getTableHeader() .addMouseListener(new
	 * ColumnHeaderMouseListener()); sourceTable.getTable().getTableHeader()
	 * .addMouseMotionListener(new ColumnHeaderMouseMotionListener());
	 * 
	 * for (int i = 0; i < columnCount; i++) {
	 * sourceTable.getTable().getColumnModel().getColumn(i) .setCellEditor(new
	 * MyCellEditor(app)); } sourceTable.updateFonts(app.getPlainFont());
	 * 
	 * setTableDimension(sourceTable.getTable());
	 * 
	 * this.revalidate(); this.repaint();
	 * 
	 * }
	 */
	/*
	 * private void loadSourceTableFromDataSource() { sourceTable.clear(); int
	 * numColumns = Math.min(dataSource.size(), sourceTable.getTable()
	 * .getModel().getColumnCount()); for (int i = 0; i < numColumns; i++) {
	 * setTableColumn(i); }
	 * 
	 * }
	 */

}
