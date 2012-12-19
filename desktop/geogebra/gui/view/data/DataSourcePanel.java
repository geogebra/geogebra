package geogebra.gui.view.data;

import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.inputfield.MathTextField;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.LayoutUtil;
import geogebra.main.AppD;
import geogebra.util.Validation;

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
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Panel to manage data sources for the DataAnalysisView
 * 
 * @author G. Sturr
 * 
 */

public class DataSourcePanel extends JPanel implements ActionListener,
		FocusListener {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private DataAnalysisViewD dataView;

	private int mode;
	protected int sourceType = DataSource.SOURCE_RAWDATA;

	// data source and table
	private DataSource dataSource;
	protected StatTable sourceTable;

	// GUI elements
	private JPanel sourcePanel, dataTypePanel, classesPanel,
			sourceControlPanel;
	private JComboBox cbSourceType, cbDataType;
	private JLabel lblStart, lblWidth, lblSourceType, lblDataType;
	private JCheckBox ckHeader;
	private JRadioButton btnNumeric, btnCategorical, btnNumeric2, btnPoints;
	private MyButton btnAdd, btnClear, btnDelete, btnOptions;

	private MyTextField fldStart, fldWidth;

	// flags and other fields
	private double classStart = 0, classWidth = 1;
	private boolean isNumericData = true;
	private boolean isPointData = false;

	private JLabel lblTitle;

	private JPanel controlPanel;

	private JDialog invoker;

	private int btnHoverColumn = -1;

	private JPopupMenu optionsPopup;

	private boolean showOptionsPanel = false;

	/*************************************************
	 * Constructor
	 * 
	 * @param app
	 * @param mode
	 */
	public DataSourcePanel(AppD app, JDialog invoker, int mode) {

		this.app = app;
		this.invoker = invoker;
		this.mode = mode;
		dataSource = new DataSource(app);

		createGUIElements();
		updatePanel(mode, true);
		setLabels();
		addFocusListener(this);

	}

	// ====================================================
	// GUI
	// ====================================================

	public void updatePanel(int mode, boolean doAutoLoadSelectedGeos) {
		this.mode = mode;

		if (doAutoLoadSelectedGeos) {
			dataSource.setDataSourceAutomatically(mode, sourceType);
		}
		buildGUI();
		updateGUI();
		loadSourceTableFromDataSource();
		revalidate();

		// TODO: test code, remove later ?
		invoker.pack();

	}

	private void buildGUI() {

		buildSourcePanel();
		buildControlPanel();
		buildOptionsPopup();

		removeAll();
		setLayout(new BorderLayout(2, 2));
		setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

		add(sourcePanel, BorderLayout.CENTER);
		// add(controlPanel, BorderLayout.SOUTH);

	}

	private void createGUIElements() {

		lblTitle = new JLabel();

		btnAdd = new MyButton(app.getImageIcon("list-add.png"));
		btnAdd.addActionListener(this);

		btnClear = new MyButton(app.getImageIcon("edit-clear.png"));
		btnClear.addActionListener(this);

		btnDelete = new MyButton(app.getImageIcon("list-remove.png"));
		btnDelete.addActionListener(this);

		btnOptions = new MyButton(app.getImageIcon("view-properties16.png"));
		btnOptions.addActionListener(this);

		ckHeader = new JCheckBox();
		ckHeader.addActionListener(this);

		lblSourceType = new JLabel();
		cbSourceType = new JComboBox();

		lblDataType = new JLabel();
		cbDataType = new JComboBox();

		btnNumeric = new JRadioButton();
		btnNumeric.addActionListener(this);
		btnCategorical = new JRadioButton();
		btnCategorical.addActionListener(this);
		ButtonGroup group2 = new ButtonGroup();
		group2.add(btnNumeric);
		group2.add(btnCategorical);

		btnNumeric2 = new JRadioButton();
		btnNumeric2.addActionListener(this);
		btnPoints = new JRadioButton();
		btnPoints.addActionListener(this);
		ButtonGroup group3 = new ButtonGroup();
		group3.add(btnNumeric2);
		group3.add(btnPoints);

		lblStart = new JLabel();
		lblWidth = new JLabel();

		fldStart = new MyTextField(app, 4);
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldStart.addActionListener(this);
		fldStart.setText("" + classStart);
		fldStart.addFocusListener(this);

		fldWidth = new MyTextField(app, 4);
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.addActionListener(this);
		fldWidth.setText("" + classWidth);
		fldWidth.addFocusListener(this);

		createSourceTable();

		buildOptionsPopup();

	}

	private void buildSourcePanel() {

		buildSourceControlPanel();

		if (sourcePanel == null) {
			sourcePanel = new JPanel(new BorderLayout(0, 5));
		}

		sourcePanel.removeAll();

		buildSourceControlPanel();
		buildClassesPanel();

		sourcePanel.add(sourceControlPanel, BorderLayout.NORTH);
		sourcePanel.add(sourceTable, BorderLayout.CENTER);
		sourcePanel.add(classesPanel, BorderLayout.SOUTH);

	}

	private void buildSourceControlPanel() {

		if (sourceControlPanel == null) {
			sourceControlPanel = new JPanel();
			sourceControlPanel.setLayout(new BorderLayout(0, 0));
		}

		sourceControlPanel.removeAll();
		if (mode == DataAnalysisViewD.MODE_MULTIVAR) {
			sourceControlPanel.add(
					LayoutUtil.flowPanel(0, 0, 0, btnAdd, btnDelete),
					app.borderWest());
		}

		sourceControlPanel.add(
				LayoutUtil.flowPanel(0, 0, 0, btnClear, btnOptions),
				app.borderEast());

	}

	private void buildControlPanel() {

		controlPanel = new JPanel(new BorderLayout());
		controlPanel.setBorder(BorderFactory.createEtchedBorder());
		if (dataTypePanel == null) {
			dataTypePanel = new JPanel();
			dataTypePanel.setLayout(new BoxLayout(dataTypePanel,
					BoxLayout.Y_AXIS));
		}
		dataTypePanel.removeAll();
		int tab = 15;

		if (mode == DataAnalysisViewD.MODE_ONEVAR) {

			dataTypePanel.add(LayoutUtil.flowPanel(tab, cbDataType));
			dataTypePanel.add(LayoutUtil.flowPanel(tab, cbSourceType));

		} else if (mode == DataAnalysisViewD.MODE_REGRESSION) {
			dataTypePanel.add(LayoutUtil.flowPanel(tab, cbDataType));
		} else {
			dataTypePanel.add(LayoutUtil.flowPanel(tab, cbSourceType));
		}

		dataTypePanel.add(LayoutUtil.flowPanel(tab, ckHeader));
		// dataTypePanel.add(OptionsUtil.flowPanel(tab, btnOptions));

		// controlPanel.add(OptionsUtil.flowPanel(lblTitle), app.borderWest());
		// controlPanel.add(OptionsUtil.flowPanel(btnOptions),
		// BorderLayout.NORTH);
		if (showOptionsPanel) {
			controlPanel.add(dataTypePanel, BorderLayout.SOUTH);
		}

	}

	private void buildClassesPanel() {
		classesPanel = LayoutUtil.flowPanel(4, 2, 0, lblStart, fldStart,
				lblWidth, fldWidth);
	}

	// ====================================================
	// Updates
	// ====================================================

	public void setLabels() {

		// ckHeader.setText(app.getMenu("Title Header"));

		// lblSourceType.setText(app.getMenu("Source Type:"));
		// lblDataType.setText(app.getMenu("Data Type:"));
		// String[] sourceTypeLabels = { app.getMenu("Raw Data"),
		// app.getMenu("Data with Frequency"),
		// app.getMenu("Class with Frequency") };
		// cbSourceType.removeActionListener(this);
		// cbSourceType.removeAllItems();
		// for (int i = 0; i < sourceTypeLabels.length; i++) {
		// cbSourceType.addItem(sourceTypeLabels[i]);
		// }
		// cbSourceType.addActionListener(this);

		// String[] dataTypeLabels = getDataTypeLabels();
		// if (dataTypeLabels != null) {
		// cbDataType.removeActionListener(this);
		// cbDataType.removeAllItems();
		// for (int i = 0; i < dataTypeLabels.length; i++) {
		// cbDataType.addItem(dataTypeLabels[i]);
		// }
		// cbDataType.addActionListener(this);
		// }

		// btnNumeric.setText(app.getMenu("Number"));
		// btnCategorical.setText(app.getMenu("Text"));
		// btnNumeric2.setText(app.getMenu("Number"));
		// btnPoints.setText(app.getMenu("Point"));

		// dataTypePanel.setBorder(BorderFactory.createTitledBorder(app
		// .getMenu("Data Type")));

		// sourcePanel.setBorder(BorderFactory.createTitledBorder(app
		// .getMenu("Source")));

		// classesPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Classes")));

		lblStart.setText(app.getMenu("Start") + ":");
		lblWidth.setText(app.getMenu("Width") + ":");

		btnOptions.setToolTipText(app.getMenu("Options"));
		btnClear.setToolTipText(app.getMenu("ClearColumns"));
		btnDelete.setToolTipText(app.getPlain("fncInspector.removeColumn"));
		btnAdd.setToolTipText(app.getPlain("fncInspector.addColumn"));

	}

	private String[] getDataTypeLabels() {
		if (mode == DataAnalysisViewD.MODE_ONEVAR) {
			String[] dataTypeLabels = { app.getMenu("Number"),
					app.getMenu("Text") };
			return dataTypeLabels;
		} else if (mode == DataAnalysisViewD.MODE_REGRESSION) {
			String[] dataTypeLabels = { app.getMenu("Numbers"),
					app.getMenu("Points") };
			return dataTypeLabels;
		}
		return null;
	}

	protected void updateGUI() {

		lblTitle.setIcon(app.getModeIcon(mode));

		btnNumeric.setSelected(isNumericData);

		ckHeader.setSelected(dataSource.enableHeader());

		classesPanel
				.setVisible(sourceType == DataSource.SOURCE_CLASS_FREQUENCY);

		updateSourceTableStructure();
		this.revalidate();
		this.repaint();
	}

	private void createSourceTable() {

		int rowCount = 8;
		int columnCount = 4;

		String[] columnNames = new String[columnCount];

		sourceTable = new StatTable(app);
		sourceTable.setHorizontalAlignment(SwingConstants.CENTER);
		sourceTable.setBorder(BorderFactory.createEmptyBorder());

		sourceTable.setStatTable(rowCount, null, columnCount, columnNames);
		sourceTable.getTable().setColumnSelectionAllowed(true);
		sourceTable.getTable().setRowSelectionAllowed(true);
		sourceTable.setAllowCellEdit(true);

		// sourceTable.getTable().setShowHorizontalLines(false);

		sourceTable.clear();

		// sourceTable.getTable().setCellEditor(new MyCellEditor(app));

		sourceTable.getTable().getModel()
				.addTableModelListener(new TableModelListener() {

					public void tableChanged(TableModelEvent e) {
						if (e.getType() == TableModelEvent.UPDATE) {
							// updateTableEdit(e.getColumn());
						}

					}
				});

		setColumnHeaders(sourceTable.getTable());

	}

	protected void addEditedColumnToDataSource(int colIndex) {
		// System.out.println("ADD EDITED COLUMN!!!!");
		DefaultTableModel m = (DefaultTableModel) sourceTable.getTable()
				.getModel();

		String[] s = new String[m.getRowCount()];

		for (int i = 0; i < m.getRowCount(); i++) {
			s[i] = (String) m.getValueAt(i, colIndex);
		}
		// System.out.println(Arrays.toString(s));
		dataSource.addItem(colIndex, s, DataSource.ITEM_INTERNAL);
		// sourceTable.getTable().revalidate();
		// sourceTable.getTable().repaint();

		loadSourceTableFromDataSource();

	}

	private void updateSourceTableStructure() {

		int columnCount = 1;

		ArrayList<String> columnNameList = new ArrayList<String>();

		switch (mode) {

		case DataAnalysisViewD.MODE_ONEVAR:
			if (sourceType == DataSource.SOURCE_RAWDATA) {
				columnCount = 1;
				columnNameList.add(app.getMenu("Data"));
			} else if (sourceType == DataSource.SOURCE_VALUE_FREQUENCY) {
				columnCount = 2;
				columnNameList.add(app.getMenu("Data"));
				columnNameList.add(app.getMenu("Frequency"));
			} else if (sourceType == DataSource.SOURCE_CLASS_FREQUENCY) {
				columnCount = 2;
				columnNameList.add(app.getMenu("Classes"));
				columnNameList.add(app.getMenu("Frequency"));
			}
			break;

		case DataAnalysisViewD.MODE_REGRESSION:
			columnCount = 2;
			columnNameList.add(app.getMenu("Column.X"));
			columnNameList.add(app.getMenu("Column.Y"));
			break;

		case DataAnalysisViewD.MODE_MULTIVAR:

			if (dataSource.size() > 2) {
				columnCount = dataSource.size();
			} else {
				columnCount = 2;
				dataSource.ensureSize(2);
			}

			for (int i = 1; i <= columnCount; i++) {
				columnNameList.add("# " + i);
			}

			break;
		}

		DefaultTableModel m = (DefaultTableModel) sourceTable.getTable()
				.getModel();
		m.setColumnCount(columnCount);

		for (int i = 0; i < columnCount; i++) {
			sourceTable.getTable().getColumnModel().getColumn(i)
					.setHeaderValue(columnNameList.get(i));
		}

		// sourceTable.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// for (int i = 0; i < columnCount; i++) {
		// sourceTable.autoFitColumnWidth(i, 3);
		// }
		sourceTable.getTable()
				.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		sourceTable.getTable().setColumnSelectionInterval(0, 0);
		sourceTable.getTable().getTableHeader().setReorderingAllowed(false);

		Dimension d = sourceTable.getPreferredSize();
		d.width = 250;
		d.height = 8 * sourceTable.getTable().getRowHeight();
		// d.height = 200;
		// sourceTable.setPreferredSize(d);
		// sourceTable.getTable().setPreferredSize(d);
		sourceTable.getTable().setPreferredScrollableViewportSize(d);

		setColumnHeaders(sourceTable.getTable());

		sourceTable.getTable().getTableHeader()
				.addMouseListener(new ColumnHeaderMouseListener());
		sourceTable.getTable().getTableHeader()
				.addMouseMotionListener(new ColumnHeaderMouseMotionListener());

		for (int i = 0; i < columnCount; i++) {
			sourceTable.getTable().getColumnModel().getColumn(i)
					.setCellEditor(new MyCellEditor(app));
		}

		this.revalidate();
		this.repaint();

	}

	private void setColumnNames() {

	}

	private void loadSourceTableFromDataSource() {
		sourceTable.clear();
		int numColumns = Math.min(dataSource.size(), sourceTable.getTable()
				.getModel().getColumnCount());
		for (int i = 0; i < numColumns; i++) {
			//.out.println("seting column # " + i );
			setTableColumn(i);
		}

	}

	/**
	 * Loads data from the dataSource list at the given index position into the
	 * corresponding source table column.
	 * 
	 * @param colIndex
	 */
	private void setTableColumn(int colIndex) {

		if (dataSource.get(colIndex) == null) {
			return;
		}

		DefaultTableModel model = sourceTable.getModel();
		//System.out.println("column type: " + dataSource.get(colIndex).getType() );
		try {
			if (dataSource.get(colIndex).getType() == dataSource.ITEM_LIST) {

				GeoList geoList = (GeoList) dataSource.get(colIndex).getItem();

				// ensure the table has enough rows
				if (model.getRowCount() < geoList.size()) {
					model.setRowCount(geoList.size());
				}

				for (int i = 0; i < model.getRowCount(); i++) {

					if (i < geoList.size() && geoList.get(i) != null
							&& geoList.get(i).isDefined()
							&& isValidDataType(geoList.get(i))) {
						model.setValueAt(geoList.get(i).getValueForInputBar(),
								i, colIndex);
					} else {
						model.setValueAt(" ", i, colIndex);
					}
				}
			}

			else if (dataSource.get(colIndex).getType() == dataSource.ITEM_SPREADSHEET) {

				ArrayList<CellRange> rangeList = (ArrayList<CellRange>) dataSource
						.get(colIndex).getItem();
				int maxRow = 0;
				int row = 0;
				boolean skipFirstCell = dataSource.enableHeader();

				for (CellRange cr : rangeList) {

					ArrayList<GeoElement> list = cr.toGeoList();
					maxRow += list.size();

					// ensure the table has enough rows
					if (model.getRowCount() < maxRow) {
						model.setRowCount(maxRow);
					}

					// iterate through the list and set the row values
					for (int i = 0; i < list.size(); i++) {
						if (skipFirstCell) {
							skipFirstCell = false;
							continue;
						}
						if (list.get(i) != null && list.get(i).isDefined()
								&& isValidDataType(list.get(i))) {
							model.setValueAt(list.get(i).getValueForInputBar(),
									row, colIndex);
						} else {
							model.setValueAt(" ", row, colIndex);
						}
						row++;
					}
				}

			} else if (dataSource.get(colIndex).getType() == dataSource.ITEM_INTERNAL) {

				String[] str = (String[]) dataSource.get(colIndex).getItem();

				// ensure the table has enough rows
				if (model.getRowCount() < str.length) {
					model.setRowCount(str.length);
				}

				// load the array into the column
				for (int i = 0; i < model.getRowCount(); i++) {
					if (i < str.length && str[i] != null) {
						model.setValueAt(str[i], i, colIndex);
					} else {
						model.setValueAt(" ", i, colIndex);
					}
				}

			}

			else if (dataSource.get(colIndex).getType() == dataSource.ITEM_CLASSES) {

				Double[] leftBorder = (Double[]) dataSource.get(colIndex)
						.getItem();
				//System.out.println("=====> " + Arrays.toString(leftBorder));
				// ensure the table has enough rows
				if (model.getRowCount() < leftBorder.length - 1) {
					model.setRowCount(leftBorder.length - 1);
				}

				// load the array into the column
				for (int i = 0; i < model.getRowCount(); i++) {
					if (i < leftBorder.length && leftBorder[i] != null) {
						String interval = leftBorder[i] + " - "
								+ leftBorder[i + 1];
						model.setValueAt(interval, i, colIndex);
					} else {
						model.setValueAt(" ", i, colIndex);
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// sourceTable.getTable().setColumnSelectionInterval(colIndex,
		// colIndex);
		sourceTable.getTable().revalidate();

	}

	private boolean isValidDataType(GeoElement geo) {
		//System.out.println("isnumeric: " + dataSource.isNumericData() + "  "
		//		+ geo.getValueForInputBar());
		if (dataSource.isNumericData()) {
			return geo.isGeoNumeric();
		} else {
			return geo.isGeoText();
		}
	}

	/**
	 * Sets the dataSource field at the given index to refer to the currently
	 * selected geos and fills the corresponding column in the data table with
	 * data from these geos.
	 * 
	 */
	void addDataToColumn(int colIndex) {
		// App.error("add data at position: " + colIndex);

		// ignore attempts to load the Classes column
		if (sourceType == DataSource.SOURCE_CLASS_FREQUENCY && colIndex == 0) {
			return;
		}
		dataSource.addCurrentGeoSelection(colIndex);
		loadSourceTableFromDataSource();
		updateGUI();

	}

	private void updateClasses(int index, double classStart, double classWidth,
			int numClasses) {

		Double[] c = new Double[numClasses + 1];
		c[0] = classStart;
		for (int i = 1; i < numClasses; i++) {
			c[i] = c[i - 1] + classWidth;
		}
		
		//System.out.println("========> classes:" + Arrays.toString(c) );
		dataSource.addItem(index, c, DataSource.ITEM_CLASSES);
	}

	// ====================================================
	// Event handlers
	// ====================================================

	/**
	 * Handles button clicks
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);

		} else if (source == cbSourceType) {

			if (cbSourceType.getSelectedItem().equals(app.getMenu("Raw Data"))) {
				sourceType = DataSource.SOURCE_RAWDATA;
			} else if (cbSourceType.getSelectedItem().equals(
					app.getMenu("Data with Frequency"))) {
				sourceType = DataSource.SOURCE_VALUE_FREQUENCY;
			} else if (cbSourceType.getSelectedItem().equals(
					app.getMenu("Class with Frequency"))) {
				sourceType = DataSource.SOURCE_CLASS_FREQUENCY;
			}

		} else if (source == btnNumeric || source == btnCategorical) {
			isNumericData = btnNumeric.isSelected();

		} else if (source == btnNumeric2 || source == btnPoints) {
			isPointData = btnPoints.isSelected();

		} else if (source == ckHeader) {
			dataSource.setEnableHeader(ckHeader.isSelected());
			updatePanel(mode, false);

		} else if (source == btnAdd) {

			dataSource.addEmpty();
			updatePanel(DataAnalysisViewD.MODE_MULTIVAR, false);
		} else if (source == btnClear) {
			int n = dataSource.size();
			dataSource.clear();
			dataSource.ensureSize(n);
			loadSourceTableFromDataSource();
		}

		else if (source == btnDelete) {
			if (dataSource.size() > 2) {
				dataSource.removeLast();
				loadSourceTableFromDataSource();
			}
		}

		else if (source == btnOptions) {
			optionsPopup.show(btnOptions, 0, btnOptions.getHeight());
			// showOptionsPanel = !showOptionsPanel;
			// updatePanel(mode,false);
		}

		updateGUI();
		revalidate();

	}

	private void doTextFieldActionPerformed(Object source) {

		//System.out.println("========> do textfield 1" );
		if (!(source instanceof JTextField)) {
			return;
		}
		((JTextField) source).getText().trim();

		if (source == fldStart) {
			classStart = Validation.validateDouble(fldStart, classStart);
			updateClasses(0, classStart, classWidth, 5);
			updatePanel(mode, false);

		} else if (source == fldWidth) {
			//System.out.println("========> do textfield 2" );
			classWidth = Validation
					.validateDoublePositive(fldWidth, classWidth);
			//System.out.println("========> do textfield 3" + classWidth );
			updateClasses(0, classStart, classWidth, 5);
			updatePanel(mode, false);
		}
	}

	public void focusGained(FocusEvent e) {
		// do nothing
	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed(e.getSource());
	}

	public void updateFonts(Font font) {
		setFont(font);
		sourceTable.updateFonts(font);

	}

	public void applySettings() {

		if (dataView == null) {
			dataView = (DataAnalysisViewD) app.getGuiManager()
					.getDataAnalysisView();
		}

		// TODO: this should be set in the action listeners for source type:
		dataSource.setSourceType(sourceType);

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

	public class ColumnHeaderMouseMotionListener implements MouseMotionListener {

		JTableHeader header = table().getTableHeader();

		public void mouseDragged(MouseEvent arg0) {

		}

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

		public void mouseClicked(java.awt.event.MouseEvent evt) {

			JTable table = sourceTable.getTable();
			TableColumnModel colModel = table.getColumnModel();

			// The index of the column whose header was clicked
			int vColIndex = colModel.getColumnIndexAtX(evt.getX());
			table.convertColumnIndexToModel(vColIndex);

			// Return if not clicked on the column header button
			if (vColIndex != btnHoverColumn) {
				return;
			}

			// Determine if mouse was clicked between column heads
			Rectangle headerRect = table.getTableHeader().getHeaderRect(
					vColIndex);
			if (vColIndex == 0) {
				headerRect.width -= 3; // Hard-coded constant
			} else {
				headerRect.grow(-3, 0); // Hard-coded constant
			}
			if (!headerRect.contains(evt.getX(), evt.getY())) {
				// Mouse was clicked between column heads
				// vColIndex is the column head closest to the click

				if (evt.getX() < headerRect.x) {
				}
			}

			addDataToColumn(vColIndex);
		}

		public void mouseExited(java.awt.event.MouseEvent evt) {

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

	public class MyTableHeaderRenderer extends JPanel implements
			TableCellRenderer {

		private JLabel lblTitle, lblSource;
		private JButton btnSelect;

		protected Border headerBorder = UIManager
				.getBorder("TableHeader.cellBorder");

		protected Font font = UIManager.getFont("TableHeader.font");
		private ImageIcon selectIcon, rolloverSelectIcon;

		public MyTableHeaderRenderer() {
			setLayout(new BorderLayout());
			setOpaque(true);
			setBorder(headerBorder);

			lblTitle = new JLabel("", SwingConstants.CENTER);
			lblTitle.setForeground(Color.WHITE);
			lblTitle.setBackground(Color.LIGHT_GRAY);
			lblTitle.setOpaque(true);

			lblSource = new JLabel("", SwingConstants.LEFT);
			lblSource.setForeground(Color.BLACK);

			btnSelect = new JButton();
			btnSelect.setBorderPainted(false);
			btnSelect.setContentAreaFilled(false);

			selectIcon = app.getImageIcon("arrow_cursor_grabbing.png");
			rolloverSelectIcon = app
					.getImageIcon("arrow_cursor_grabbing_rollover.png");

		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int vColIndex) {

			removeAll();
			invalidate();

			if (value == null) {
				// Do nothing if no value
				return this;
			}

			// set lblTitle text to the given column header text (e.g. "Data")
			lblTitle.setText(value.toString());
			lblTitle.setFont(app.getPlainFont());

			// set lblSource text to the source string for this column
			lblSource.setText(dataSource.getDataTitle(vColIndex));
			lblSource.setFont(app.getItalicFont());

			// layout the header
			JPanel p = new JPanel(new BorderLayout(10, 0));
			p.add(lblTitle, BorderLayout.NORTH);
			p.add(lblSource, BorderLayout.CENTER);

			if (!lblTitle.getText().equals(app.getMenu("Classes"))) {
				p.add(btnSelect, app.borderWest());

				if (btnHoverColumn == vColIndex) {
					btnSelect.setIcon(rolloverSelectIcon);
					setToolTipText(app.getMenuTooltip("AddSelection"));
				} else {
					btnSelect.setIcon(selectIcon);
					setToolTipText(null);
				}
			}

			add(p, BorderLayout.CENTER);

			if (isSelected) {
				setBackground(geogebra.awt.GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER));
			} else {
				setBackground(geogebra.awt.GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
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
		public boolean isOverTraceButton(int colIndex, Point loc, Object value) {

			try {
				return loc.x < btnSelect.getWidth();
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return false;
		}

	}

	/**
	 * @author gsturr
	 * 
	 */
	public class MyCellEditor extends DefaultCellEditor {

		public MyCellEditor(MathTextField tf) {
			super(tf);
		}

		public MyCellEditor(AppD app) {
			this(new MathTextField(app));
		}

		@Override
		public boolean stopCellEditing() {

			// get the edit column while it is still available
			int editColumn = sourceTable.getTable().getEditingColumn();

			// call super.stopCellEditing to update the table model
			boolean result = super.stopCellEditing();

			// now add the edit into the data source and exit
			addEditedColumnToDataSource(editColumn);
			return result;
		}

	}

	private void buildOptionsPopup() {

		JMenu subMenu;

		if (optionsPopup == null) {
			optionsPopup = new JPopupMenu();
		}
		optionsPopup.removeAll();

		if (mode == DataAnalysisViewD.MODE_ONEVAR) {

			// ==========================
			// one var data type

			final JCheckBoxMenuItem itemTypeNum = new JCheckBoxMenuItem(
					app.getMenu("Number"));
			itemTypeNum.setSelected(dataSource.isNumericData());
			itemTypeNum.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dataSource.setNumericData(itemTypeNum.isSelected());
					updatePanel(mode, false);
				}
			});

			final JCheckBoxMenuItem itemTypeText = new JCheckBoxMenuItem(
					app.getMenu("Type.Text"));
			itemTypeText.setSelected(!dataSource.isNumericData());
			itemTypeText.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dataSource.setNumericData(itemTypeNum.isSelected());
					updatePanel(mode, false);
				}
			});

			ButtonGroup grp = new ButtonGroup();
			grp.add(itemTypeNum);
			grp.add(itemTypeText);

			// subMenu = new JMenu(app.getMenu("DataType"));
			// optionsPopup.add(subMenu);
			// subMenu.add(itemTypeNum);
			// subMenu.add(itemTypeText);
			optionsPopup.add(itemTypeNum);
			optionsPopup.add(itemTypeText);

			// ==========================
			// source type

			final JCheckBoxMenuItem itmSourceRawData = new JCheckBoxMenuItem(
					app.getMenu("RawData"));
			itmSourceRawData
					.setSelected(sourceType == DataSource.SOURCE_RAWDATA);
			itmSourceRawData.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (itmSourceRawData.isSelected()
							&& sourceType != DataSource.SOURCE_RAWDATA) {
						sourceType = DataSource.SOURCE_RAWDATA;
						updateGUI();
					}
				}
			});

			final JCheckBoxMenuItem itmSourceDataFrequency = new JCheckBoxMenuItem(
					app.getMenu("DataWithFrequency"));
			itmSourceDataFrequency
					.setSelected(sourceType == DataSource.SOURCE_VALUE_FREQUENCY);
			itmSourceDataFrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (itmSourceDataFrequency.isSelected()
							&& sourceType != DataSource.SOURCE_VALUE_FREQUENCY) {
						sourceType = DataSource.SOURCE_VALUE_FREQUENCY;
						updateGUI();
					}
				}
			});

			final JCheckBoxMenuItem itmSourceClassFrequency = new JCheckBoxMenuItem(
					app.getMenu("ClassWithFrequency"));
			itmSourceClassFrequency
					.setSelected(sourceType == DataSource.SOURCE_CLASS_FREQUENCY);
			itmSourceClassFrequency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (itmSourceClassFrequency.isSelected()
							&& sourceType != DataSource.SOURCE_CLASS_FREQUENCY) {
						sourceType = DataSource.SOURCE_CLASS_FREQUENCY;
						updateGUI();
					}
				}
			});

			ButtonGroup grp2 = new ButtonGroup();
			grp2.add(itmSourceRawData);
			grp2.add(itmSourceDataFrequency);
			grp2.add(itmSourceClassFrequency);

			optionsPopup.addSeparator();
			optionsPopup.add(itmSourceRawData);
			optionsPopup.add(itmSourceDataFrequency);
			optionsPopup.add(itmSourceClassFrequency);

		}

		if (mode == DataAnalysisViewD.MODE_REGRESSION) {

			// ==========================
			// two var data type

			final JCheckBoxMenuItem itemTypeNum = new JCheckBoxMenuItem(
					app.getMenu("Number"));
			itemTypeNum.setSelected(dataSource.isNumericData());
			itemTypeNum.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dataSource.setNumericData(itemTypeNum.isSelected());
					// updatePanel(mode, false);
				}
			});

			final JCheckBoxMenuItem itemTypePoint = new JCheckBoxMenuItem(
					app.getPlain("Point"));
			itemTypePoint.setSelected(!dataSource.isNumericData());
			itemTypePoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dataSource.setNumericData(itemTypePoint.isSelected());
					// TODO: update action
				}
			});

			ButtonGroup grp = new ButtonGroup();
			grp.add(itemTypeNum);
			grp.add(itemTypePoint);

			subMenu = new JMenu(app.getMenu("DataType"));
			optionsPopup.add(subMenu);
			subMenu.add(itemTypeNum);
			subMenu.add(itemTypePoint);

		}

		// ==========================
		// header as title

		final JCheckBoxMenuItem itmHeader = new JCheckBoxMenuItem(
				app.getMenu("UseHeaderAsTitle"));
		itmHeader.setSelected(dataSource.enableHeader());
		itmHeader.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (dataSource.enableHeader() != itmHeader.isSelected()) {
					dataSource.setEnableHeader(itmHeader.isSelected());
					updatePanel(mode, false);
				}
			}
		});

		optionsPopup.addSeparator();
		optionsPopup.add(itmHeader);
		
		app.setComponentOrientation(optionsPopup);


	}

	private class MyButton extends JButton {

		public MyButton(ImageIcon imageIcon) {
			super(imageIcon);
			setMargin(new Insets(0, 0, 0, 0));
			setBorderPainted(false);
			setContentAreaFilled(false);
			setFocusable(false);
		}
	}

}
