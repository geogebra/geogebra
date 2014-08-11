package geogebra.web.gui.view.data;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.main.settings.SpreadsheetSettings;
import geogebra.html5.main.LocalizationW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class DataPanelW extends FlowPanel implements StatPanelInterfaceW
{
	private static final long serialVersionUID = 1L;

	private AppW app;
	private DataAnalysisViewW daView;
	private DataAnalysisControllerW statController;

//	private JTable dataTable;
	private Button btnEnableAll;
//	private MyRowHeader rowHeader;
//	private MyColumnHeaderRenderer columnHeader;
//	private JScrollPane scrollPane;

	private Boolean[] selectionList;

	private Label lblHeader;
	public int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	private LocalizationW loc;

	private static final GColor DISABLED_BACKGROUND_COLOR = GColor.LIGHT_GRAY;
	private static final GColor SELECTED_BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER;
	private static final GColor TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
//	private static final GColor TABLE_HEADER_COLOR = GeoGebraColorConstants.TABLE_HEADER_COLOR;
	
	/*************************************************
	 * Construct a DataPanel
	 */
	public DataPanelW(AppW app, DataAnalysisViewW statDialog) {
		this.app = app;
		this.loc = (LocalizationW) app.getLocalization();
		this.daView = statDialog;
		this.statController = statDialog.getController();

		buildDataTable();
//		populateDataTable(statController.getDataArray());
		createGUI();
		add(new Label("o lenne az!"));
	}

	private void buildDataTable() {
//		dataTable = new JTable() {
//			private static final long serialVersionUID = 1L;
//
//			// disable cell edits (for now)
//			@Override
//			public boolean isCellEditable(int rowIndex, int vColIndex) {
//				return false;
//			}
//
//			@Override
//			protected void configureEnclosingScrollPane() {
//				super.configureEnclosingScrollPane();
//				Container p = getParent();
//				if (p instanceof JViewport) {
//					((JViewport) p).setBackground(getBackground());
//				}
//			}
//		};
	}

	private void createGUI() {
		// set table and column renderers
//		dataTable.setDefaultRenderer(Object.class, new MyCellRenderer());
//		columnHeader = new MyColumnHeaderRenderer();
//		columnHeader.setPreferredSize(new Dimension(preferredColumnWidth,
//				SpreadsheetSettings.TABLE_CELL_HEIGHT));
//		for (int i = 0; i < dataTable.getColumnCount(); ++i) {
//			dataTable.getColumnModel().getColumn(i)
//					.setHeaderRenderer(columnHeader);
//			dataTable.getColumnModel().getColumn(i)
//					.setPreferredWidth(preferredColumnWidth);
//		}
//
//		// disable row selection (for now)
//		dataTable.setColumnSelectionAllowed(false);
//		dataTable.setRowSelectionAllowed(false);
//
//		// dataTable.setAutoResizeMode(JTable.);
//		dataTable.setPreferredScrollableViewportSize(dataTable
//				.getPreferredSize());
//		dataTable.setMinimumSize(new Dimension(100, 50));
//		// dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//		dataTable.setAutoCreateColumnsFromModel(false);
//		dataTable.setGridColor(TABLE_GRID_COLOR);
//
//		// create a scrollPane for the table
//		scrollPane = new JScrollPane(dataTable);
//		scrollPane.setBorder(BorderFactory.createEmptyBorder());
//
//		// create row header
//		rowHeader = new MyRowHeader(this, dataTable);
//		scrollPane.setRowHeaderView(rowHeader);
//
//		// create enableAll button and put it in the upper left corner
//		CheckBoxIcon cbIcon = new CheckBoxIcon(13);
//		ImageIcon iconUnChecked = cbIcon.createCheckBoxImageIcon(false, false);
//		ImageIcon iconChecked = cbIcon.createCheckBoxImageIcon(true, false);
//
//		btnEnableAll = new JButton();
//		btnEnableAll.setIcon(iconUnChecked);
//		btnEnableAll.setDisabledIcon(iconChecked);
//		btnEnableAll.setEnabled(false);
//		btnEnableAll.setBorderPainted(false);
//		btnEnableAll
//				.setBackground(geogebra.awt.GColorD
//						.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
//		btnEnableAll.setContentAreaFilled(false);
//		btnEnableAll.setHorizontalAlignment(SwingConstants.LEFT);
//		btnEnableAll.addActionListener(this);
//
//		Corner upperLeftCorner = new Corner();
//		upperLeftCorner.setLayout(new BorderLayout());
//		upperLeftCorner.add(btnEnableAll, loc.borderWest());
//
//		upperLeftCorner.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createMatteBorder(0, 0, 1, 1, TABLE_GRID_COLOR),
//				BorderFactory.createEmptyBorder(0, 5, 0, 2)));
//
//		// set the other corners
//		scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,
//				upperLeftCorner);
//		scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,
//				new Corner());
//		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
//				new Corner());
//
//		lblHeader = new JLabel();
//		lblHeader.setHorizontalAlignment(SwingConstants.LEFT);
//		lblHeader.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createEtchedBorder(),
//				BorderFactory.createEmptyBorder(2, 5, 2, 2)));
//
//		// finally, load up our JPanel
//		this.setLayout(new BorderLayout());
//		this.add(lblHeader, BorderLayout.NORTH);
//		this.add(scrollPane, BorderLayout.CENTER);
//		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
//		this.setMinimumSize(dataTable.getPreferredSize());
//
	}

	public void removeGeos() {

	}

	public void setLabels() {
		lblHeader.setText(loc.getMenu("Data"));
	}

	public void updatePanel() {
//		setRowHeight();
	}
//
//	public void updateFonts(Font font) {
//		// TODO Auto-generated method stub
//
//	}

	private Boolean[] updateSelectionList(ArrayList<GeoElement> dataArray) {

		selectionList = new Boolean[dataArray.size()];
		for (int i = 0; i < selectionList.length; ++i) {
			selectionList[i] = true;
		}

		return selectionList;
	}

//	private void populateDataTable(ArrayList<GeoElement> dataArray) {
//
//		if (dataArray == null || dataArray.size() < 1) {
//			return;
//		}
//
//		TableModel dataModel = null;
//		GeoPoint geo = null;
//		String[] titles = daView.getDataTitles();
//
//		switch (daView.getModel().getMode()) {
//
//		case DataAnalysisModel.MODE_ONEVAR:
//
//			dataModel = new DefaultTableModel(dataArray.size(), 1);
//			for (int row = 0; row < dataArray.size(); ++row) {
//				dataModel.setValueAt(
//						dataArray.get(row).toDefinedValueString(
//								StringTemplate.defaultTemplate), row, 0);
//			}
//
//			dataTable.setModel(dataModel);
//			dataTable.getColumnModel().getColumn(0).setHeaderValue(titles[0]);
//
//			updateSelectionList(dataArray);
//
//			break;
//
//		case DataAnalysisModel.MODE_REGRESSION:
//
//			// a data source may be a list of points with a single title
//			// so we must create a title for the y column
//			String titleX = titles[0];
//			String titleY = titles.length == 1 ? titleX : titles[1];
//
//			dataModel = new DefaultTableModel(dataArray.size(), 2);
//			for (int row = 0; row < dataArray.size(); ++row) {
//				dataModel.setValueAt(
//						((GeoPoint) (dataArray.get(row))).getInhomX(), row, 0);
//				dataModel.setValueAt(
//						((GeoPoint) (dataArray.get(row))).getInhomY(), row, 1);
//			}
//
//			dataTable.setModel(dataModel);
//
//			// handle x,y titles
//			if (daView.getDataSource().isPointData()) {
//
//				dataTable.getColumnModel().getColumn(0)
//						.setHeaderValue(loc.getMenu("Column.X"));
//				dataTable.getColumnModel().getColumn(1)
//						.setHeaderValue(loc.getMenu("Column.Y"));
//			} else {
//				dataTable
//						.getColumnModel()
//						.getColumn(0)
//						.setHeaderValue(loc.getMenu("Column.X") + ": " + titleX);
//				dataTable
//						.getColumnModel()
//						.getColumn(1)
//						.setHeaderValue(loc.getMenu("Column.Y") + ": " + titleY);
//			}
//
//			updateSelectionList(dataArray);
//
//			break;
//		}
//
//	}
//
//	/**
//	 * Loads the data table. Called on data set changes.
//	 */
//	public void loadDataTable(ArrayList<GeoElement> dataArray) {
//
//		// load the data model
//		populateDataTable(dataArray);
//
//		// prepare boolean selection list for the checkboxes
//		selectionList = new Boolean[dataArray.size()];
//		for (int i = 0; i < dataArray.size(); ++i) {
//			selectionList[i] = true;
//		}
//
//		// create a new header
//		rowHeader = new MyRowHeader(this, dataTable);
//		scrollPane.setRowHeaderView(rowHeader);
//		updateFonts(getFont());
//
//		// repaint
//		dataTable.repaint();
//		rowHeader.repaint();
//
//	}
//
//	public void ensureTableFill() {
//		Container p = getParent();
//		DefaultTableModel dataModel = (DefaultTableModel) dataTable.getModel();
//		if (dataTable.getHeight() < p.getHeight()) {
//			int newRows = (p.getHeight() - dataTable.getHeight())
//					/ dataTable.getRowHeight();
//			dataModel.setRowCount(dataTable.getRowCount() + newRows);
//			for (int i = 0; i <= dataTable.getRowCount(); ++i) {
//				if (rowHeader.getModel().getElementAt(i) != null)
//					((DefaultListModel) rowHeader.getModel()).add(i, true);
//			}
//		}
//
//	}
//
//	private void notifySelectionChange(int index, boolean isSelected) {
//		// statDialog.handleDataPanelSelectionChange(selectionList);
//	}
//
//	private class Corner extends JPanel {
//		private static final long serialVersionUID = 1L;
//
//		@Override
//		protected void paintComponent(Graphics g) {
//			g.setColor(TABLE_HEADER_COLOR);
//			g.fillRect(0, 0, getWidth(), getHeight());
//		}
//	}
//
//	@Override
//	public void setFont(Font font) {
//		super.setFont(font);
//
//		if (dataTable != null && dataTable.getRowCount() > 0
//				&& dataTable.getColumnCount() > 0) {
//
//			// set the font for each component
//			dataTable.setFont(font);
//			if (dataTable.getTableHeader() != null)
//				dataTable.getTableHeader().setFont(font);
//			rowHeader.setFont(font);
//
//			setRowHeight();
//
//			// set the column width
//			int size = font.getSize();
//			if (size < 12)
//				size = 12; // minimum size
//			double multiplier = (size) / 12.0;
//			preferredColumnWidth = (int) (SpreadsheetSettings.TABLE_CELL_WIDTH * multiplier);
//
//			// columnHeader.setPreferredSize(new Dimension(preferredColumnWidth,
//			// (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
//			// this.validate();
//			// dataTable.repaint();
//		}
//
//		if (dataTable != null) {
//			dataTable.setPreferredScrollableViewportSize(dataTable
//					.getPreferredSize());
//		}
//	}
//
//	private void setRowHeight() {
//		// get row height needed to draw an "X" character
//		int h = dataTable
//				.getCellRenderer(0, 0)
//				.getTableCellRendererComponent(dataTable, "X", false, false, 0,
//						0).getPreferredSize().height;
//
//		// use this height to set the table and row header heights
//		dataTable.setRowHeight(h);
//		rowHeader.setFixedCellHeight(h);
//	}
//
//	public MyRowHeader getRowHeader() {
//		return rowHeader;
//	}
//
//	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == btnEnableAll) {
//			rowHeader.enableAll();
//			btnEnableAll.setEnabled(false);
//
//		}
//	}
}
