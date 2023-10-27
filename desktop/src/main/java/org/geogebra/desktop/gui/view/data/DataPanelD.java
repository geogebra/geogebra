package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

public class DataPanelD extends JPanel
		implements ActionListener, StatPanelInterface {
	private static final long serialVersionUID = 1L;

	private final AppD app;
	private final DataAnalysisViewD daView;
	private final DataAnalysisControllerD statController;

	private JTable dataTable;
	private JCheckBox btnEnableAll;
	private MyRowHeader rowHeader;
	private JScrollPane scrollPane;

	private Boolean[] selectionList;

	private JLabel lblHeader;
	public int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	private final LocalizationD loc;

	private static final Color DISABLED_BACKGROUND_COLOR = Color.LIGHT_GRAY;
	private static final Color TABLE_GRID_COLOR = DataAnalysisViewD.TABLE_GRID_COLOR;
	private static final Color TABLE_HEADER_COLOR = DataAnalysisViewD.TABLE_HEADER_COLOR;

	/*************************************************
	 * Construct a DataPanel
	 */
	public DataPanelD(AppD app, DataAnalysisViewD statDialog) {
		this.app = app;
		this.loc = app.getLocalization();
		this.daView = statDialog;
		this.statController = statDialog.getController();

		buildDataTable();
		populateDataTable(statController.getDataArray());
		createGUI();
	}

	private void buildDataTable() {
		dataTable = new JTable() {
			private static final long serialVersionUID = 1L;

			// disable cell edits (for now)
			@Override
			public boolean isCellEditable(int rowIndex, int vColIndex) {
				return false;
			}

			@Override
			protected void configureEnclosingScrollPane() {
				super.configureEnclosingScrollPane();
				Container p = getParent();
				if (p instanceof JViewport) {
					((JViewport) p).setBackground(getBackground());
				}
			}
		};
	}

	private void createGUI() {
		// set table and column renderers
		dataTable.setDefaultRenderer(Object.class, new DataPanelCellRenderer());
		ColumnHeaderRenderer columnHeader = new ColumnHeaderRenderer();
		columnHeader.setPreferredSize(new Dimension(preferredColumnWidth,
				SpreadsheetSettings.TABLE_CELL_HEIGHT));
		for (int i = 0; i < dataTable.getColumnCount(); ++i) {
			dataTable.getColumnModel().getColumn(i)
					.setHeaderRenderer(columnHeader);
			dataTable.getColumnModel().getColumn(i)
					.setPreferredWidth(preferredColumnWidth);
		}

		// disable row selection (for now)
		dataTable.setColumnSelectionAllowed(false);
		dataTable.setRowSelectionAllowed(false);

		// dataTable.setAutoResizeMode(JTable.);
		dataTable.setPreferredScrollableViewportSize(
				dataTable.getPreferredSize());
		dataTable.setMinimumSize(new Dimension(100, 50));
		// dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		dataTable.setAutoCreateColumnsFromModel(false);
		dataTable.setGridColor(TABLE_GRID_COLOR);

		// create a scrollPane for the table
		scrollPane = new JScrollPane(dataTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		// create row header
		rowHeader = new MyRowHeader(this, dataTable);
		scrollPane.setRowHeaderView(rowHeader);

		// create enableAll button and put it in the upper left corner

		btnEnableAll = new JCheckBox();
		btnEnableAll.setEnabled(false);
		btnEnableAll.setBorderPainted(false);
		btnEnableAll.setBackground(GColorD.getAwtColor(
				GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
		btnEnableAll.setContentAreaFilled(false);
		btnEnableAll.setHorizontalAlignment(SwingConstants.LEFT);
		btnEnableAll.addActionListener(this);

		Corner upperLeftCorner = new Corner();
		upperLeftCorner.setLayout(new BorderLayout());
		upperLeftCorner.add(btnEnableAll, loc.borderWest());

		upperLeftCorner.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 1, TABLE_GRID_COLOR),
				BorderFactory.createEmptyBorder(0, 5, 0, 2)));

		// set the other corners
		scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,
				upperLeftCorner);
		scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,
				new Corner());
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
				new Corner());

		lblHeader = new JLabel();
		lblHeader.setHorizontalAlignment(SwingConstants.LEFT);
		lblHeader.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(2, 5, 2, 2)));

		// finally, load up our JPanel
		this.setLayout(new BorderLayout());
		this.add(lblHeader, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		this.setMinimumSize(dataTable.getPreferredSize());

	}

	@Override
	public void setLabels() {
		lblHeader.setText(loc.getMenu("Data"));
	}

	@Override
	public void updatePanel() {
		setRowHeight();
	}

	@Override
	public void updateFonts(Font font) {
		// TODO Auto-generated method stub

	}

	private Boolean[] updateSelectionList(ArrayList<GeoElement> dataArray) {

		selectionList = new Boolean[dataArray.size()];
		for (int i = 0; i < selectionList.length; ++i) {
			selectionList[i] = true;
		}

		return selectionList;
	}

	private void populateDataTable(ArrayList<GeoElement> dataArray) {

		if (dataArray == null || dataArray.size() < 1) {
			return;
		}

		TableModel dataModel = null;
		String[] titles = daView.getDataTitles();

		switch (daView.getModel().getMode()) {

		default:
		case DataAnalysisModel.MODE_ONEVAR:

			dataModel = new DefaultTableModel(dataArray.size(), 1);
			for (int row = 0; row < dataArray.size(); ++row) {
				dataModel
						.setValueAt(
								dataArray.get(row).toDefinedValueString(
										StringTemplate.defaultTemplate),
								row, 0);
			}

			dataTable.setModel(dataModel);
			dataTable.getColumnModel().getColumn(0).setHeaderValue(titles[0]);

			updateSelectionList(dataArray);

			break;

		case DataAnalysisModel.MODE_REGRESSION:

			// a data source may be a list of points with a single title
			// so we must create a title for the y column
			String titleX = titles[0];
			String titleY = titles.length == 1 ? titleX : titles[1];

			dataModel = new DefaultTableModel(dataArray.size(), 2);
			for (int row = 0; row < dataArray.size(); ++row) {
				dataModel.setValueAt(
						((GeoPoint) (dataArray.get(row))).getInhomX(), row, 0);
				dataModel.setValueAt(
						((GeoPoint) (dataArray.get(row))).getInhomY(), row, 1);
			}

			dataTable.setModel(dataModel);

			// handle x,y titles
			if (daView.getDataSource().isPointData()) {

				dataTable.getColumnModel().getColumn(0)
						.setHeaderValue(loc.getMenu("Column.X"));

				// quick fix for GGB-1392
				if (dataTable.getColumnModel().getColumnCount() > 1) {
					dataTable.getColumnModel().getColumn(1)
							.setHeaderValue(loc.getMenu("Column.Y"));
				} else {
					Log.error("problem setting title for 2nd column");
				}
			} else {
				dataTable.getColumnModel().getColumn(0).setHeaderValue(
						loc.getMenu("Column.X") + ": " + titleX);

				// quick fix for GGB-1392
				if (dataTable.getColumnModel().getColumnCount() > 1) {
					dataTable.getColumnModel().getColumn(1).setHeaderValue(
							loc.getMenu("Column.Y") + ": " + titleY);
				} else {
					Log.error("problem setting title for 2nd column");
				}
			}

			updateSelectionList(dataArray);

			break;
		}

	}

	/**
	 * Loads the data table. Called on data set changes.
	 */
	public void loadDataTable(ArrayList<GeoElement> dataArray) {

		// load the data model
		populateDataTable(dataArray);

		// prepare boolean selection list for the checkboxes
		selectionList = new Boolean[dataArray.size()];
		for (int i = 0; i < dataArray.size(); ++i) {
			selectionList[i] = true;
		}

		// create a new header
		rowHeader = new MyRowHeader(this, dataTable);
		scrollPane.setRowHeaderView(rowHeader);
		updateFonts(getFont());

		// repaint
		dataTable.repaint();
		rowHeader.repaint();
	}

	private static class Corner extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(TABLE_HEADER_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);

		if (dataTable != null && dataTable.getRowCount() > 0
				&& dataTable.getColumnCount() > 0) {

			// set the font for each component
			dataTable.setFont(font);
			if (dataTable.getTableHeader() != null) {
				dataTable.getTableHeader().setFont(font);
			}
			rowHeader.setFont(font);

			setRowHeight();

			// set the column width
			int size = font.getSize();
			if (size < 12) {
				size = 12; // minimum size
			}
			double multiplier = size / 12.0;
			preferredColumnWidth = (int) (SpreadsheetSettings.TABLE_CELL_WIDTH
					* multiplier);

			// columnHeader.setPreferredSize(new Dimension(preferredColumnWidth,
			// (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
			// this.validate();
			// dataTable.repaint();
		}

		if (dataTable != null) {
			dataTable.setPreferredScrollableViewportSize(
					dataTable.getPreferredSize());
		}
	}

	private void setRowHeight() {
		// get row height needed to draw an "X" character
		int h = dataTable.getCellRenderer(0, 0).getTableCellRendererComponent(
				dataTable, "X", false, false, 0, 0).getPreferredSize().height;

		// use this height to set the table and row header heights
		dataTable.setRowHeight(h);
		rowHeader.setFixedCellHeight(h);
	}

	public MyRowHeader getRowHeader() {
		return rowHeader;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnEnableAll) {
			rowHeader.enableAll();
			btnEnableAll.setEnabled(false);
		}
	}

	// =================================================
	// Column Header Renderer
	// =================================================

	protected class ColumnHeaderRenderer extends JLabel
			implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public ColumnHeaderRenderer() {
			super("", SwingConstants.CENTER);
			setOpaque(true);
			setBackground(TABLE_HEADER_COLOR);
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
					TABLE_GRID_COLOR));
			setFont(app.getPlainFont());
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int colIndex) {
			setFont(app.getPlainFont());
			setText(value.toString());

			return this;
		}

	}

	// ======================================================
	// Table Cell Renderer
	// ======================================================

	class DataPanelCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public DataPanelCellRenderer() {
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value == null) {
				setText("");
				return this;
			}
			setFont(app.getPlainFont());
			String text = value.toString();

			// if (isSelected)
			// setBackground(SELECTED_BACKGROUND_COLOR_HEADER);
			// setBackground(table.getBackground());

			if (!selectionList[row]) {
				setBackground(DISABLED_BACKGROUND_COLOR);
			} else {
				setBackground(table.getBackground());
			}

			setText(text);
			return this;
		}

	}

	// ======================================================
	// Row Header
	// ======================================================

	public class MyRowHeader extends JList<Boolean> implements MouseListener {
		private static final long serialVersionUID = 1L;

		// DefaultListModel model;
		JTable table;
		DataPanelD dataPanel;

		protected MyRowHeader(DataPanelD dataPanel, JTable table) {
			super(selectionList);
			this.table = table;
			this.dataPanel = dataPanel;

			setCellRenderer(new RowHeaderRenderer(table));
			setSelectionModel(table.getSelectionModel());
			this.addMouseListener(this);

		}

		class RowHeaderRenderer extends JLabel implements ListCellRenderer {
			private static final long serialVersionUID = 1L;
			private final JPanel panel = new JPanel();

			private final JCheckBox jCheckBox = new JCheckBox();

			RowHeaderRenderer(JTable table) {
				setOpaque(false);
				setHorizontalAlignment(LEFT);
				setFont(table.getFont());
				jCheckBox.setBorder(
						BorderFactory.createMatteBorder(0, 0, 1, 0,
								GColorD.getAwtColor(
										GeoGebraColorConstants.TABLE_GRID_COLOR)));
				panel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 1, 1,
								GColorD.getAwtColor(
										GeoGebraColorConstants.TABLE_GRID_COLOR)),
						BorderFactory.createEmptyBorder(0, 0, 1, 0)));
				panel.add(jCheckBox);
				panel.add(this);
			}

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				setText("" + (index + 1));
				if (!(Boolean) value) {
					panel.setBackground(DISABLED_BACKGROUND_COLOR);
				} else {
					panel.setBackground(TABLE_HEADER_COLOR);
				}
				jCheckBox.setSelected((Boolean) value);
				return panel;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// only handle mouse pressed
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// only handle mouse pressed
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// only handle mouse pressed
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// check if we clicked in checkbox icon area
			int index = this.locationToIndex(e.getPoint());
			Rectangle rect = getCellBounds(index, index);
			boolean iconClicked = rect != null && e.getX() - rect.x < 16; // distance
																			// from
																			// left
																			// border
			if (iconClicked) {
				// icon clicked: toggle enable/disable data
				selectionList[this.getSelectedIndex()] = !selectionList[this
						.getSelectedIndex()];
				statController.updateSelectedDataList(this.getSelectedIndex(),
						selectionList[this.getSelectedIndex()]);
				btnEnableAll.setEnabled(!isAllEnabled());

				table.repaint();
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// only handle mouse pressed
		}

		/**
		 * Enable all
		 */
		public void enableAll() {
			for (int i = 0; i < selectionList.length; ++i) {
				if (!selectionList[i]) {
					statController.updateSelectedDataList(i, true);
					selectionList[i] = true;
				}
			}
			rowHeader.repaint();
			table.repaint();
		}

		protected boolean isAllEnabled() {
			for (Boolean selected : selectionList) {
				if (!selected) {
					return false;
				}
			}
			return true;
		}

	}

}
