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
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.MyToggleButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

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
	private MyToggleButtonD btnShowStatistics, btnShowPlot2, btnShowData;
	private MyToggleButtonD btnDataSource;
	private MyTextFieldD fldDataSource;
	private MyToggleButtonD btnExport;
	private MyToggleButtonD btnSwapXY;

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

		btnPrint = new JButton(app.getMenuIcon(GuiResourcesD.DOCUMENT_PRINT));
		iconHeight = app.getScaledIconSize();

		btnPrint.addActionListener(this);
		btnPrint.setFocusPainted(false);
		btnPrint.setBorderPainted(false);
		btnPrint.setContentAreaFilled(false);
		btnPrint.setFocusable(false);

		btnShowStatistics = new MyToggleButtonD(
				app.getScaledIcon(
						GuiResourcesD.STYLINGBAR_VARIABLE_ANALYSIS_STATISTICS),
				iconHeight);
		btnShowStatistics.addActionListener(this);
		btnShowStatistics.setFocusPainted(false);
		btnShowStatistics.setFocusable(false);

		btnShowData = new MyToggleButtonD(
				app.getScaledIcon(
						GuiResourcesD.STYLINGBAR_VARIABLE_ANALYSIS_DATA),
				iconHeight);
		btnShowData.addActionListener(this);
		btnShowData.setFocusPainted(false);
		btnShowData.setFocusable(false);

		btnShowPlot2 = new MyToggleButtonD(
				app.getScaledIcon(
						GuiResourcesD.STYLINGBAR_VARIABLE_ANALYSIS_2PLOT),
				iconHeight);
		btnShowPlot2.addActionListener(this);
		btnShowPlot2.setFocusPainted(false);
		btnShowPlot2.setFocusable(false);

		// create export button
		btnExport = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.EXPORT16), iconHeight);
		btnExport.setFocusPainted(false);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		btnSwapXY = new MyToggleButtonD(iconHeight);
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

		btnDataSource = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.ARROW_CURSOR_GRABBING),
				iconHeight);

		btnDataSource.addActionListener(this);
		fldDataSource = new MyTextFieldD(app);

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

		btnRounding = new JButton(
				app.getScaledIcon(GuiResourcesD.TRIANGLE_DOWN));
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
		Localization loc = app.getLocalization();
		btnRounding.setText(loc.getMenu(".xx"));
		btnShowStatistics.setToolTipText(loc.getMenu("ShowStatistics"));
		btnShowData.setToolTipText(loc.getMenu("ShowData"));
		btnShowPlot2.setToolTipText(loc.getMenu("ShowPlot2"));
		btnPrint.setToolTipText(loc.getMenu("Print"));
		btnDataSource.setToolTipText(loc.getMenu("ShowDataSource"));

		String swapString = loc.getMenu("Column.X") + " \u21C6 "
				+ loc.getMenu("Column.Y");
		btnSwapXY.setFont(app.getPlainFont());
		btnSwapXY.setText(swapString);

	}

	@Override
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
			app.getDialogManager().showDataSourceDialog(model.getMode(), false);
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
