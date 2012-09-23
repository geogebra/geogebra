package geogebra.gui.view.data;

import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.MyToggleButton;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * @author G. Sturr
 * 
 */
public class DataAnalysisStyleBar extends JToolBar implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private AppD app;
	private DataAnalysisViewD statDialog;
	protected int iconHeight = 18;
	private JButton btnRounding, btnPrint;
	private MyToggleButton btnShowStatistics, btnShowPlot2, btnShowData;
	private JPopupMenu roundingPopup;
	private MyTextField fldSource;
	private MyToggleButton btnDataSource;
	private MyTextField fldDataSource;
	private MyToggleButton btnExport;
	private MyToggleButton btnSwapXY;

	public DataAnalysisStyleBar(AppD app, DataAnalysisViewD statDialog) {

		this.statDialog = statDialog;
		this.app = app;
		this.setFloatable(false);
		createGUI();
		updateGUI();
		setLabels();
	}

	private void createGUI() {
		this.removeAll();

		btnPrint = new JButton(app.getImageIcon("document-print.png"));
		btnPrint.addActionListener(this);
		btnPrint.setFocusPainted(false);
		btnPrint.setBorderPainted(false);
		btnPrint.setContentAreaFilled(false);
		btnPrint.setFocusable(false);

		btnShowStatistics = new MyToggleButton(
				app.getImageIcon("dataview-showstatistics.png"), iconHeight);
		btnShowStatistics.addActionListener(this);
		btnShowStatistics.setFocusPainted(false);
		btnShowStatistics.setFocusable(false);

		btnShowData = new MyToggleButton(
				app.getImageIcon("dataview-showdata.png"), iconHeight);
		btnShowData.addActionListener(this);
		btnShowData.setFocusPainted(false);
		btnShowData.setFocusable(false);

		btnShowPlot2 = new MyToggleButton(
				app.getImageIcon("dataview-showplot2.png"), iconHeight);
		btnShowPlot2.addActionListener(this);
		btnShowPlot2.setFocusPainted(false);
		btnShowPlot2.setFocusable(false);

		// create export button
		btnExport = new MyToggleButton(app.getImageIcon("export16.png"),
				iconHeight);
		btnExport.setFocusPainted(false);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		btnSwapXY = new MyToggleButton(iconHeight);
		btnSwapXY.setSelected(!statDialog.getController().isLeftToRight());
		btnSwapXY.setMaximumSize(btnSwapXY.getPreferredSize());
		btnSwapXY.addActionListener(this);
		btnSwapXY.setFocusable(false);
		
		
		buildRoundingButton();
		createDataSourcePanel();
		
		// add(btnRounding);
		add(btnDataSource);
		addSeparator();
		add(btnShowStatistics);
		add(btnShowData);
		add(btnShowPlot2);
		add(btnSwapXY);
		//add(createDataSourcePanel());

	}

	public void updateGUI() {

		btnShowStatistics.setSelected(statDialog.showStatPanel());
		
		btnShowData.setVisible(statDialog.getMode() != DataAnalysisViewD.MODE_MULTIVAR);
		btnShowData.setSelected(statDialog.showDataPanel());
		
		btnShowPlot2.setVisible(statDialog.getMode() != DataAnalysisViewD.MODE_MULTIVAR);
		btnShowPlot2.setSelected(statDialog.showComboPanel2());
	
		//	fldDataSource.setText(statDialog.getStatDialogController()
		//		.getSourceString());
		fldDataSource.revalidate();
		
		btnSwapXY.setVisible(statDialog.getMode() == DataAnalysisViewD.MODE_REGRESSION);
		btnSwapXY.setSelected(!statDialog.getController().isLeftToRight());
	}

	private JPanel createDataSourcePanel() {

		btnDataSource = new MyToggleButton(app.getImageIcon("cursor_grabbing.gif"), iconHeight);   //app.getImageIcon("go-previous.png"));
		
		btnDataSource.addActionListener(this);
		fldDataSource = new MyTextField(app);

		JPanel dataSourcePanel = new JPanel(new BorderLayout(5, 0));
		//dataSourcePanel.add(btnDataSource, BorderLayout.WEST);
		//dataSourcePanel.add(fldDataSource, BorderLayout.CENTER);

		dataSourcePanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));

		return dataSourcePanel;
	}

	/**
	 * Builds popup button with options menu items
	 */
	private void buildRoundingButton() {

		btnRounding = new JButton(app.getImageIcon("triangle-down.png"));
		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT);
		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);

		/*
		 * roundingPopup = createRoundingPopup();
		 * 
		 * btnRounding.addActionListener(new ActionListener(){ public void
		 * actionPerformed(ActionEvent e) { // popup appears below the button
		 * roundingPopup.show(getParent(),
		 * btnRounding.getLocation().x,btnRounding.getLocation().y +
		 * btnRounding.getHeight()); } });
		 * 
		 * updateMenuDecimalPlaces(roundingPopup);
		 */

	}

	public void setLabels() {
		btnRounding.setText(app.getMenu(".xx"));
		btnShowStatistics.setToolTipText(app.getMenu("ShowStatistics"));
		btnShowData.setToolTipText(app.getMenu("ShowData"));
		btnShowPlot2.setToolTipText(app.getMenu("ShowPlot2"));
		btnPrint.setToolTipText(app.getMenu("Print"));
		btnDataSource.setToolTipText(app.getPlain("ShowDataSource"));
		
		String swapString = app.getMenu("Column.X") + " \u21C6 "
				+ app.getMenu("Column.Y");
		btnSwapXY.setFont(app.getPlainFont());
		btnSwapXY.setText(swapString);

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnShowStatistics) {
			statDialog.setShowStatistics(btnShowStatistics.isSelected());
			updateGUI();
		}
		else if (source == btnShowData) {
			statDialog.setShowDataPanel(btnShowData.isSelected());
			updateGUI();
		}

		else if (source == btnShowPlot2) {
			statDialog.setShowComboPanel2(btnShowPlot2.isSelected());
			updateGUI();
		}

		else if (source == btnSwapXY) {
			statDialog.getController().swapXY();
			updateGUI();
		}
		
		else if (source == btnDataSource) {
			btnDataSource.setSelected(false);
			statDialog.setShowDataOptionsDialog(true);
		}
		
		else if (source == btnExport) {
			JPopupMenu menu = statDialog.getExportMenu();
			menu.show(btnExport, 0, btnExport.getHeight());
			btnExport.setSelected(false);
		}

		else if (source == btnPrint) {
			statDialog.doPrint();
		}
		

	}

}
