package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.MyToggleButton;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * @author gsturr
 * 
 */
public class DataAnalysisStyleBar extends JToolBar implements ActionListener {

	private AppD app;
	private StatDialog statDialog;
	protected int iconHeight = 18;
	private JButton btnRounding, btnPrint;
	private MyToggleButton btnShowStatistics, btnShowPlot2, btnShowData;
	private JPopupMenu roundingPopup;
	private MyTextField fldSource;
	private JLabel lblDataSource;
	private MyTextField fldDataSource;
	private MyToggleButton btnExport;

	public DataAnalysisStyleBar(AppD app, StatDialog statDialog) {

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

		buildRoundingButton();

		// add(btnRounding);
		add(btnShowStatistics);
		add(btnShowData);
		add(btnShowPlot2);
		add(createDataSourcePanel());

	}

	public void updateGUI() {

		btnShowStatistics.setSelected(statDialog.showStatPanel());
		btnShowData.setSelected(statDialog.showDataPanel());
		btnShowPlot2.setSelected(statDialog.showComboPanel2());
		fldDataSource.setText(statDialog.getStatDialogController()
				.getSourceString());
		fldDataSource.revalidate();

	}

	private JPanel createDataSourcePanel() {

		lblDataSource = new JLabel();
		fldDataSource = new MyTextField(app);

		JPanel dataSourcePanel = new JPanel(new BorderLayout(5, 0));
		dataSourcePanel.add(lblDataSource, BorderLayout.WEST);
		dataSourcePanel.add(fldDataSource, BorderLayout.CENTER);

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

		lblDataSource.setText("   " + app.getMenu("Data") + ": ");

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnShowStatistics) {
			statDialog.setShowStatistics(btnShowStatistics.isSelected());
			updateGUI();
		}
		if (source == btnShowData) {
			statDialog.setShowDataPanel(btnShowData.isSelected());
			updateGUI();
		}

		if (source == btnShowPlot2) {
			statDialog.setShowComboPanel2(btnShowPlot2.isSelected());
			updateGUI();
		}

		if (source == btnExport) {
			JPopupMenu menu = statDialog.getExportMenu();
			menu.show(btnExport, 0, btnExport.getHeight());
			btnExport.setSelected(false);
		}

		if (source == btnPrint) {
			statDialog.doPrint();
		}

	}

}
