package geogebra.gui.view.data;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.main.settings.SpreadsheetSettings;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

public class DataPanel extends JPanel implements ActionListener,
		StatPanelInterface {
	private static final long serialVersionUID = 1L;

	private AppD app;
	private DataAnalysisViewD statDialog;
	private DataAnalysisControllerD statController;

	private JTable dataTable;
	private JButton btnEnableAll;
	private MyRowHeader rowHeader;
	private MyColumnHeaderRenderer columnHeader;
	private JScrollPane scrollPane;

	private Boolean[] selectionList;

	private JLabel lblHeader;
	public int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	private static final Color DISABLED_BACKGROUND_COLOR = Color.LIGHT_GRAY;
	private static final Color SELECTED_BACKGROUND_COLOR_HEADER = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER);
	private static final Color TABLE_GRID_COLOR = DataAnalysisViewD.TABLE_GRID_COLOR;
	private static final Color TABLE_HEADER_COLOR = DataAnalysisViewD.TABLE_HEADER_COLOR;

	/*************************************************
	 * Construct a DataPanel
	 */
	public DataPanel(AppD app, DataAnalysisViewD statDialog) {
		this.app = app;
		this.statDialog = statDialog;
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
		dataTable.setDefaultRenderer(Object.class, new MyCellRenderer());
		columnHeader = new MyColumnHeaderRenderer();
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
		dataTable.setPreferredScrollableViewportSize(dataTable
				.getPreferredSize());
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
		CheckBoxIcon cbIcon = new CheckBoxIcon(13);
		ImageIcon iconUnChecked = cbIcon.createCheckBoxImageIcon(false, false);
		ImageIcon iconChecked = cbIcon.createCheckBoxImageIcon(true, false);

		btnEnableAll = new JButton();
		btnEnableAll.setIcon(iconUnChecked);
		btnEnableAll.setDisabledIcon(iconChecked);
		btnEnableAll.setEnabled(false);
		btnEnableAll.setBorderPainted(false);
		btnEnableAll
				.setBackground(geogebra.awt.GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
		btnEnableAll.setContentAreaFilled(false);
		btnEnableAll.setHorizontalAlignment(SwingConstants.LEFT);
		btnEnableAll.addActionListener(this);

		Corner upperLeftCorner = new Corner();
		upperLeftCorner.setLayout(new BorderLayout());
		upperLeftCorner.add(btnEnableAll, app.borderWest());

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

	public void removeGeos() {

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
		GeoPoint geo = null;
		String[] titles = statDialog.getDataTitles();

		switch (statDialog.getMode()) {

		case DataAnalysisViewD.MODE_ONEVAR:

			dataModel = new DefaultTableModel(dataArray.size(), 1);
			for (int row = 0; row < dataArray.size(); ++row) {
				dataModel.setValueAt(
						dataArray.get(row).toDefinedValueString(
								StringTemplate.defaultTemplate), row, 0);
			}

			dataTable.setModel(dataModel);
			dataTable.getColumnModel().getColumn(0).setHeaderValue(titles[0]);

			updateSelectionList(dataArray);

			break;

		case DataAnalysisViewD.MODE_REGRESSION:

			dataModel = new DefaultTableModel(dataArray.size(), 2);
			for (int row = 0; row < dataArray.size(); ++row) {
				dataModel.setValueAt(
						((GeoPoint) (dataArray.get(row))).getInhomX(), row, 0);
				dataModel.setValueAt(
						((GeoPoint) (dataArray.get(row))).getInhomY(), row, 1);
			}

			dataTable.setModel(dataModel);
			dataTable.getColumnModel().getColumn(0)
					.setHeaderValue(app.getMenu("Column.X") + ": " + titles[0]);
			dataTable.getColumnModel().getColumn(1)
					.setHeaderValue(app.getMenu("Column.Y") + ": " + titles[1]);

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

	public void ensureTableFill() {
		Container p = getParent();
		DefaultTableModel dataModel = (DefaultTableModel) dataTable.getModel();
		if (dataTable.getHeight() < p.getHeight()) {
			int newRows = (p.getHeight() - dataTable.getHeight())
					/ dataTable.getRowHeight();
			dataModel.setRowCount(dataTable.getRowCount() + newRows);
			for (int i = 0; i <= dataTable.getRowCount(); ++i) {
				if (rowHeader.getModel().getElementAt(i) != null)
					((DefaultListModel) rowHeader.getModel()).add(i, true);
			}
		}

	}

	private void notifySelectionChange(int index, boolean isSelected) {
		// statDialog.handleDataPanelSelectionChange(selectionList);
	}

	private class Corner extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(TABLE_HEADER_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	public void updateFonts(Font font) {

	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);

		if (dataTable != null && dataTable.getRowCount() > 0
				&& dataTable.getColumnCount() > 0) {

			// set the font for each component
			dataTable.setFont(font);
			if (dataTable.getTableHeader() != null)
				dataTable.getTableHeader().setFont(font);
			rowHeader.setFont(font);

			// get row height needed to draw an "X" character
			int h = dataTable
					.getCellRenderer(0, 0)
					.getTableCellRendererComponent(dataTable, "X", false,
							false, 0, 0).getPreferredSize().height;

			// use this height to set the table and row header heights
			dataTable.setRowHeight(h);
			rowHeader.setFixedCellHeight(h);

			// set the column width
			int size = font.getSize();
			if (size < 12)
				size = 12; // minimum size
			double multiplier = (size) / 12.0;
			preferredColumnWidth = (int) (SpreadsheetSettings.TABLE_CELL_WIDTH * multiplier);

			// columnHeader.setPreferredSize(new Dimension(preferredColumnWidth,
			// (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
			// this.validate();
			// dataTable.repaint();
		}

		if (dataTable != null) {
			dataTable.setPreferredScrollableViewportSize(dataTable
					.getPreferredSize());
		}
	}

	public MyRowHeader getRowHeader() {
		return rowHeader;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnEnableAll) {
			rowHeader.enableAll();
			btnEnableAll.setEnabled(false);

		}
	}

	// =================================================
	// Column Header Renderer
	// =================================================

	protected class MyColumnHeaderRenderer extends JLabel implements
			TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public MyColumnHeaderRenderer() {
			super("", SwingConstants.CENTER);
			setOpaque(true);
			setBackground(TABLE_HEADER_COLOR);
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
					TABLE_GRID_COLOR));
			setFont(app.getPlainFont());
		}

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

	class MyCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public MyCellRenderer() {
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

			if (!selectionList[row])
				setBackground(DISABLED_BACKGROUND_COLOR);
			else
				setBackground(table.getBackground());

			setText(text);
			return this;
		}

	}

	// ======================================================
	// Row Header
	// ======================================================

	public class MyRowHeader extends JList implements MouseListener {
		private static final long serialVersionUID = 1L;

		// DefaultListModel model;
		JTable table;
		DataPanel dataPanel;

		public MyRowHeader(DataPanel dataPanel, JTable table) {
			super(selectionList);
			this.table = table;
			this.dataPanel = dataPanel;

			setCellRenderer(new RowHeaderRenderer(table));
			setSelectionModel(table.getSelectionModel());
			this.addMouseListener(this);

		}

		class RowHeaderRenderer extends JLabel implements ListCellRenderer {
			private static final long serialVersionUID = 1L;
			private ImageIcon iconChecked, iconUnChecked;

			RowHeaderRenderer(JTable table) {

				CheckBoxIcon cbIcon = new CheckBoxIcon(13);
				iconUnChecked = cbIcon.createCheckBoxImageIcon(false, false);
				iconChecked = cbIcon.createCheckBoxImageIcon(true, false);

				setOpaque(true);

				setBorder(BorderFactory
						.createCompoundBorder(
								BorderFactory
										.createMatteBorder(
												0,
												0,
												1,
												1,
												geogebra.awt.GColorD
														.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)),
								BorderFactory.createEmptyBorder(0, 5, 0, 2)));

				setHorizontalAlignment(LEFT);
				setFont(table.getFont());
			}

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				setText("" + (index + 1));

				// add/remove icons
				if ((Boolean) value) {
					setIcon(iconChecked);
				} else {
					setIcon(iconUnChecked);
				}

				if (!(Boolean) value)
					setBackground(DISABLED_BACKGROUND_COLOR);
				else
					setBackground(TABLE_HEADER_COLOR);

				return this;
			}
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

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
				return;
			}
		}

		public void mouseReleased(MouseEvent arg0) {
		}

		public void enableAll() {

			for (int i = 0; i < selectionList.length; ++i) {
				if (selectionList[i] == false) {
					statController.updateSelectedDataList(i, true);
					selectionList[i] = true;
				}
			}
			rowHeader.repaint();
			table.repaint();
		}

		public boolean isAllEnabled() {
			for (int i = 0; i < selectionList.length; ++i) {
				if (selectionList[i] == false)
					return false;
			}
			return true;
		}

	}

	public static class CheckBoxIcon {

		// Michael Borcherds 2008-05-11
		// adapted from
		// http://www.java2s.com/Open-Source/Java-Document/6.0-JDK-Modules-com.sun.java/swing/com/sun/java/swing/plaf/windows/WindowsIconFactory.java.htm
		// references to XPStyle removed
		// option for double-size added
		// replaced UIManager.getColor() with numbers from:
		// http://www.java2s.com/Tutorial/Java/0240__Swing/ListingUIDefaultProperties.htm

		/*
		 * Copyright 1998-2006 Sun Microsystems, Inc. All Rights Reserved. DO
		 * NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
		 * 
		 * This code is free software; you can redistribute it and/or modify it
		 * under the terms of the GNU General Public License version 2 only, as
		 * published by the Free Software Foundation. Sun designates this
		 * particular file as subject to the "Classpath" exception as provided
		 * by Sun in the LICENSE file that accompanied this code.
		 * 
		 * This code is distributed in the hope that it will be useful, but
		 * WITHOUT ANY WARRANTY; without even the implied warranty of
		 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
		 * General Public License version 2 for more details (a copy is included
		 * in the LICENSE file that accompanied this code).
		 * 
		 * You should have received a copy of the GNU General Public License
		 * version 2 along with this work; if not, write to the Free Software
		 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
		 * USA.
		 * 
		 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
		 * Clara, CA 95054 USA or visit www.sun.com if you need additional
		 * information or have any questions.
		 */
		// int csize = 13;

		// EuclidianView ev;

		public static Color highlightBackground = new Color(230, 230, 230);
		public int csize;

		public CheckBoxIcon(int csize) {
			this.csize = csize;
		}

		public void paintIcon(boolean checked, boolean highlighted, Graphics g,
				int x, int y) {

			{
				// Outer top/left
				// g.setColor(UIManager.getColor("CheckBox.shadow"));
				g.setColor(new Color(128, 128, 128));
				g.drawLine(x, y, x + (csize - 2), y);
				g.drawLine(x, y + 1, x, y + (csize - 2));

				// Outer bottom/right
				// g.setColor(UIManager.getColor("CheckBox.highlight"));
				g.setColor(Color.white);
				g.drawLine(x + (csize - 1), y, x + (csize - 1), y + (csize - 1));
				g.drawLine(x, y + (csize - 1), x + (csize - 2), y + (csize - 1));

				// Inner top.left
				// g.setColor(UIManager.getColor("CheckBox.darkShadow"));
				g.setColor(new Color(64, 64, 64));
				g.drawLine(x + 1, y + 1, x + (csize - 3), y + 1);
				g.drawLine(x + 1, y + 2, x + 1, y + (csize - 3));

				// Inner bottom/right
				// g.setColor(UIManager.getColor("CheckBox.light"));
				g.setColor(new Color(212, 208, 200));
				g.drawLine(x + 1, y + (csize - 2), x + (csize - 2), y
						+ (csize - 2));
				g.drawLine(x + (csize - 2), y + 1, x + (csize - 2), y
						+ (csize - 3));

				// inside box
				if (highlighted) {
					// g.setColor(UIManager.getColor("CheckBox.background"));
					g.setColor(highlightBackground);
				} else {
					// g.setColor(UIManager.getColor("CheckBox.interiorBackground"));
					g.setColor(Color.white);
				}
				g.fillRect(x + 2, y + 2, csize - 4, csize - 4);

				// g.setColor(UIManager.getColor("CheckBox.foreground"));
				g.setColor(new Color(0, 0, 0));

				// paint check

				if (checked) {
					if (csize == 13) {

						for (int i = 5; i <= 9; i++)
							g.drawLine(x + i, y + 12 - i, x + i, y + 14 - i);

						for (int i = 3; i <= 4; i++)
							g.drawLine(x + i, y + i + 2, x + i, y + i + 4);

					} else { // csize == 26

						for (int i = 10; i <= 18; i++)
							g.drawLine(x + i, y + 24 - i, x + i, y + 29 - i);

						for (int i = 5; i <= 9; i++)
							g.drawLine(x + i, y + i + 4, x + i, y + i + 9);

					}
				}
			}
		}

		public ImageIcon createCheckBoxImageIcon(boolean checked,
				boolean highlighted) {

			CheckBoxIcon cbIcon = new CheckBoxIcon(13);
			BufferedImage image = new BufferedImage(13, 13,
					(BufferedImage.TYPE_INT_ARGB));
			ImageIcon icon = new ImageIcon(image);
			Graphics2D g2d = image.createGraphics();

			cbIcon.paintIcon(checked, highlighted, g2d, 0, 0);

			return icon;
		}

	}

	public void setLabels() {
		lblHeader.setText(app.getMenu("Data"));
	}

	public void updatePanel() {
		// TODO Auto-generated method stub
	}

}
