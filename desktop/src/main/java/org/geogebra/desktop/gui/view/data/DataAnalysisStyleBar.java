package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.desktop.gui.inputfield.MyTextField;
import org.geogebra.desktop.gui.util.MyToggleButton;
import org.geogebra.desktop.main.AppD;

/**
 * @author G. Sturr
 * 
 */
public class DataAnalysisStyleBar extends JToolBar implements ActionListener {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private DataAnalysisViewD daView;
	protected int iconHeight = 18;
	private JButton btnRounding, btnPrint;
	private MyToggleButton btnShowStatistics, btnShowPlot2, btnShowData;
	private JPopupMenu roundingPopup;
	private MyTextField fldSource;
	private MyToggleButton btnDataSource;
	private MyTextField fldDataSource;
	private MyToggleButton btnExport;
	private MyToggleButton btnSwapXY;

	/**
	 * @param app
	 * @param statDialog
	 */
	public DataAnalysisStyleBar(AppD app, DataAnalysisViewD statDialog) {

		this.daView = statDialog;
		this.app = app;
		this.setFloatable(false);
		createGUI();
		updateGUI();
		setLabels();
	}

	private void createGUI() {
		this.removeAll();

		btnPrint = new JButton(app.getMenuIcon("document-print.png"));
		iconHeight = app.getScaledIconSize();

		btnPrint.addActionListener(this);
		btnPrint.setFocusPainted(false);
		btnPrint.setBorderPainted(false);
		btnPrint.setContentAreaFilled(false);
		btnPrint.setFocusable(false);

		btnShowStatistics = new MyToggleButton(
				app.getScaledIcon("stylingbar_variable_analysis_statistics.png"),
				iconHeight);
		btnShowStatistics.addActionListener(this);
		btnShowStatistics.setFocusPainted(false);
		btnShowStatistics.setFocusable(false);

		btnShowData = new MyToggleButton(
				app.getScaledIcon("stylingbar_variable_analysis_data.png"),
				iconHeight);
		btnShowData.addActionListener(this);
		btnShowData.setFocusPainted(false);
		btnShowData.setFocusable(false);

		btnShowPlot2 = new MyToggleButton(
				app.getScaledIcon("stylingbar_variable_analysis_2plot.png"),
				iconHeight);
		btnShowPlot2.addActionListener(this);
		btnShowPlot2.setFocusPainted(false);
		btnShowPlot2.setFocusable(false);

		// create export button
		btnExport = new MyToggleButton(app.getScaledIcon("export16.png"),
				iconHeight);
		btnExport.setFocusPainted(false);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		btnSwapXY = new MyToggleButton(iconHeight);
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
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
		// add(createDataSourcePanel());

	}

	public void reinit() {
		createGUI();
	}

	public void updateGUI() {

		DataAnalysisModel model = daView.getModel();
		btnShowStatistics.setSelected(model.showStatPanel());
		if (model.showStatPanel() && daView.getStatisticsPanel().isVisible()) {
			daView.getStatisticsPanel().updatePanel();
		}

		switch (model.getMode()) {
		case DataAnalysisModel.MODE_ONEVAR:
			if (daView.getDataSource() != null
					&& daView.groupType() == GroupType.RAWDATA) {
				btnShowData.setVisible(true);
			} else {
				btnShowData.setVisible(false);
			}
			break;
		case DataAnalysisModel.MODE_REGRESSION:
			btnShowData.setVisible(true);
			break;
		case DataAnalysisModel.MODE_MULTIVAR:
			btnShowData.setVisible(false);
			break;
		default:
			btnShowData.setVisible(false);
		}

		btnShowData.setSelected(model.showDataPanel());

		btnShowPlot2.setVisible(!model.isMultiVar());
		btnShowPlot2.setSelected(model.showDataDisplayPanel2());

		// fldDataSource.setText(statDialog.getStatDialogController()
		// .getSourceString());
		fldDataSource.revalidate();

		btnSwapXY.setVisible(model.isRegressionMode());
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
	}

	private JPanel createDataSourcePanel() {

		btnDataSource = new MyToggleButton(
				app.getScaledIcon("arrow_cursor_grabbing.png"), iconHeight); // app.getImageIcon("go-previous.png"));

		btnDataSource.addActionListener(this);
		fldDataSource = new MyTextField(app);

		JPanel dataSourcePanel = new JPanel(new BorderLayout(5, 0));
		// dataSourcePanel.add(btnDataSource, app.borderWest());
		// dataSourcePanel.add(fldDataSource, BorderLayout.CENTER);

		dataSourcePanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));

		return dataSourcePanel;
	}

	/**
	 * Builds popup button with options menu items
	 */
	private void buildRoundingButton() {

		btnRounding = new JButton(app.getScaledIcon("triangle-down.png"));
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
		DataAnalysisModel model = daView.getModel();
		Object source = e.getSource();
		if (source == btnShowStatistics) {
			model.setShowStatistics(btnShowStatistics.isSelected());
			updateGUI();
		} else if (source == btnShowData) {
			model.setShowDataPanel(btnShowData.isSelected());
			updateGUI();
		}

		else if (source == btnShowPlot2) {
			model.setShowComboPanel2(btnShowPlot2.isSelected());
			updateGUI();
		}

		else if (source == btnSwapXY) {
			daView.getController().swapXY();
			updateGUI();
		}

		else if (source == btnDataSource) {
			btnDataSource.setSelected(false);
			model.setShowDataOptionsDialog(true);
		}

		else if (source == btnExport) {
			JPopupMenu menu = daView.getExportMenu();
			menu.show(btnExport, 0, btnExport.getHeight());
			btnExport.setSelected(false);
		}

		else if (source == btnPrint) {
			daView.doPrint();
		}

	}

}
