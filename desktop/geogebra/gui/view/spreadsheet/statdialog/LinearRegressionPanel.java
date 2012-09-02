package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.main.AppD;

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

public class LinearRegressionPanel extends JPanel implements StatPanelInterface{
	private static final long serialVersionUID = 1L;
	private AppD app;
	private Kernel kernel;
	private JList dataSourceList;
	private DefaultTableModel model;
	private DataAnalysisViewD statDialog;
	private DefaultListModel headerModel;
	

	public LinearRegressionPanel(AppD app, DataAnalysisViewD statDialog){

		this.app = app;
		kernel = app.getKernel();
		this.statDialog = statDialog;

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
		table.setGridColor(geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
		table.setShowGrid(true);

		// table row header
		JList rowHeader = new JList(headerModel);
		rowHeader.setFixedCellWidth(50);
		rowHeader.setFixedCellHeight(table.getRowHeight() + table.getRowMargin()); 
		rowHeader.setCellRenderer(new RowHeaderRenderer(table));

		// add table to scroll pane
		JScrollPane scroll = new JScrollPane(table);
		scroll.setRowHeaderView(rowHeader);
		
		
		
		this.add(scroll, BorderLayout.CENTER);

	}


	public void updateRegressionPanel(){
		
		String[] columnLabels = { 
				app.getMenu("Coefficient"),
				app.getMenu("StandardError.short"),
				app.getMenu("TStatistic"),
				app.getMenu("Pvalue"),
		};
		
		String[] rowLabels = { 
				app.getMenu("Intercept"),
				app.getMenu("Slope"),
		};
		
		
		model.setColumnCount(0);
		for(int i=0; i<columnLabels.length; i++)
			model.addColumn(columnLabels[i]);

		
		model.setRowCount(rowLabels.length);
		headerModel.setSize(0);
		for(int i=0; i<rowLabels.length; i++){
			headerModel.addElement(rowLabels[i]);
		}


	}


	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	

		return nv.getDouble();
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

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	public void updateFonts(Font font) {
		// TODO Auto-generated method stub
		
	}


	public void setLabels() {
		// TODO Auto-generated method stub
		
	}


	public void updatePanel() {
		// TODO Auto-generated method stub
		
	}
}
