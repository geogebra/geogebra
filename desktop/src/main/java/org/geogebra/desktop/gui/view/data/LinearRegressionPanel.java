package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.main.AppD;

public class LinearRegressionPanel extends JPanel
		implements StatPanelInterface {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private DefaultTableModel model;
	private DefaultListModel headerModel;

	public LinearRegressionPanel(AppD app, DataAnalysisViewD statDialog) {

		this.app = app;

		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());

		// north panel with regression equation
		Box northPanel = Box.createVerticalBox();
		northPanel.add(new JLabel(" ---- regresion equation ----"));
		northPanel.add(new JLabel(" ----------------------------"));

		// south panel with additional statistics
		Box southPanel = Box.createVerticalBox();
		southPanel.add(new JLabel(" ---- regresion equation ----"));
		southPanel.add(new JLabel(" ----------------------------"));

		// set up table
		model = new DefaultTableModel();
		headerModel = new DefaultListModel();
		JTable table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setGridColor(
				GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
		table.setShowGrid(true);

		// table row header
		JList rowHeader = new JList(headerModel);
		rowHeader.setFixedCellWidth(50);
		rowHeader.setFixedCellHeight(
				table.getRowHeight() + table.getRowMargin());
		rowHeader.setCellRenderer(new RowHeaderRenderer(table));

		// add table to scroll pane
		JScrollPane scroll = new JScrollPane(table);
		scroll.setRowHeaderView(rowHeader);

		this.add(scroll, BorderLayout.CENTER);

	}

	public void updateRegressionPanel() {
		Localization loc = app.getLocalization();
		String[] columnLabels = { loc.getMenu("Coefficient"),
				loc.getMenu("StandardError.short"), loc.getMenu("TStatistic"),
				loc.getMenu("Pvalue"), };

		String[] rowLabels = { loc.getMenu("Intercept"),
				loc.getMenu("Slope"), };

		model.setColumnCount(0);
		for (int i = 0; i < columnLabels.length; i++) {
			model.addColumn(columnLabels[i]);
		}

		model.setRowCount(rowLabels.length);
		headerModel.setSize(0);
		for (int i = 0; i < rowLabels.length; i++) {
			headerModel.addElement(rowLabels[i]);
		}

	}

	class RowHeaderRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		RowHeaderRenderer(JTable table) {
			JTableHeader header = table.getTableHeader();
			setOpaque(true);
			setBorder(BorderFactory.createLineBorder(Color.black));
			setHorizontalAlignment(LEFT);
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setFont(app.getPlainFont());
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	@Override
	public void updateFonts(Font font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLabels() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePanel() {
		// TODO Auto-generated method stub

	}
}
