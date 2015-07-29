package org.geogebra.web.web.gui.view.data;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.StyleBarW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author G. Sturr
 * 
 */
public class DataAnalysisStyleBarW extends StyleBarW implements ClickHandler {

	private static final long serialVersionUID = 1L;

	private AppW app;
	private DataAnalysisViewW daView;
	private Button btnRounding, btnPrint;
	private MyToggleButton2 btnShowStatistics, btnShowPlot2, btnShowData;
	private AutoCompleteTextFieldW fldSource;
	private MyToggleButton2 btnDataSource;
	private AutoCompleteTextFieldW fldDataSource;
	private MyToggleButton2 btnExport;
	private MyToggleButton2 btnSwapXY;

	/**
	 * @param app
	 * @param statDialog
	 */
	public DataAnalysisStyleBarW(AppW app, DataAnalysisViewW statDialog) {
		super(app, AppW.VIEW_DATA_ANALYSIS);
		this.daView = statDialog;
		this.app = app;
	//	this.setFloatable(false);
		createGUI();
		updateGUI();
		setLabels();
		addViewButton();
	}

	private void createGUI() {
		this.clear();

		btnPrint = new Button("P");
//		btnPrint.app.getImageIcon("document-print.png"));
		btnPrint.addClickHandler(this);
		
		btnShowStatistics = new MyToggleButton2(AppResources.INSTANCE.dataview_showstatistics());
		btnShowStatistics.addClickHandler(this);
		
		btnShowData = new MyToggleButton2(AppResources.INSTANCE.dataview_showdata());
		btnShowData.addClickHandler(this);
	
		btnShowPlot2 = new MyToggleButton2(AppResources.INSTANCE.dataview_showplot2());
		btnShowPlot2.addClickHandler(this);
		
		// create export button
		btnExport = new MyToggleButton2(AppResources.INSTANCE.export());
		btnExport.addClickHandler(this);

		btnSwapXY = new MyToggleButton2(getSwapXYString());
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
		btnSwapXY.addClickHandler(this);
		btnSwapXY.getElement().addClassName("daSwapXYButton");
		buildRoundingButton();
		createDataSourcePanel();

		// add(btnRounding);
//		add(btnDataSource);
		// addSeparator();
		add(btnShowStatistics);
		add(btnShowData);
		add(btnShowPlot2);
		add(btnSwapXY);
		// add(createDataSourcePanel());

	}

	public void updateGUI() {

		DataAnalysisModel model = daView.getModel();
		btnShowStatistics.setSelected(model.showStatPanel());
		if (model.showStatPanel() && daView.getStatisticsPanel().isVisible()) {
			daView.getStatisticsPanel().updatePanel();
		}

		switch (model.getMode()) {
		case DataAnalysisModel.MODE_ONEVAR:
			// if (true){//daView.groupType() == GroupType.RAWDATA) {
			btnShowData.setVisible(true);
			// } else {
			// btnShowData.setVisible(false);
			// }
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
		btnSwapXY.setVisible(model.isRegressionMode());
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
		
	} 

	private FlowPanel createDataSourcePanel() {

//		btnDataSource = new MyToggleButton2(AppResources.INSTANCE.a);
//		btnDataSource.addClickHandler(this);
//		//fldDataSource = new MyTextField(app);
//
		FlowPanel dataSourcePanel = new FlowPanel();
		// dataSourcePanel.add(btnDataSource, app.borderWest());
		// dataSourcePanel.add(fldDataSource, BorderLayout.CENTER);


		return dataSourcePanel;
	}

	/**
	 * Builds popup button with options menu items
	 */
	private void buildRoundingButton() {

//		btnRounding = new JButton(app.getImageIcon("triangle-down.png"));
//		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT);
//		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);

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

	@Override
	public void setLabels() {
		super.setLabels();
		btnShowStatistics.setToolTipText(app.getMenu("ShowStatistics"));
		btnShowData.setToolTipText(app.getMenu("ShowData"));
		btnShowPlot2.setToolTipText(app.getMenu("ShowPlot2"));
		btnSwapXY.setText(getSwapXYString());
	}

	private String getSwapXYString() {
		return app.getMenu("Column.X") + " \u21C6 "
				+ app.getMenu("Column.Y");
	}

	public void actionPerformed(Object source) {
		DataAnalysisModel model = daView.getModel();
	
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
//			JPopupMenu menu = daView.getExportMenu();
//			menu.show(btnExport, 0, btnExport.getHeight());
			btnExport.setSelected(false);
		}

		else if (source == btnPrint) {
			daView.doPrint();
		}

	}

	@Override
    public void setOpen(boolean showStyleBar) {
	    // TODO Auto-generated method stub
	    
    }

	public void onClick(ClickEvent event) {
	    actionPerformed(event.getSource());
    }

}
