package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * global settings tab
 * 
 * @author csilla
 *
 */
public class OptionsGlobalW implements OptionPanelW {
	private AppW app;
	private FlowPanel optionsPanel;
	private Label lblGlobal;
	private Label lblRounding;
	private ListBox roundingList;
	private Label lblLabeling;
	private ListBox labelingList;


	/**
	 * @param app
	 *            - application
	 */
	public OptionsGlobalW(AppW app) {
		this.app = app;
		createGUI();
		updateGUI();
		// app.getSettings().getAlgebra().addListener(this);
	}
	
	private void createGUI() {
		optionsPanel = new FlowPanel();
		optionsPanel.setStyleName("algebraOptions");
		lblGlobal = new Label(app.getLocalization().getMenu("Global"));
		lblGlobal.addStyleName("panelTitle");
		optionsPanel.add(lblGlobal);
		lblRounding = new Label(
				app.getLocalization().getMenu("Rounding") + ":");
		roundingList = new ListBox();
		optionsPanel.add(LayoutUtilW.panelRowIndent(lblRounding, roundingList));
		lblLabeling = new Label(
				app.getLocalization().getMenu("Labeling") + ":");
		labelingList = new ListBox();
		optionsPanel.add(LayoutUtilW.panelRowIndent(lblLabeling, labelingList));
	}

	@Override
	public void updateGUI() {
		updateRoundingList();
		updateLabelingList();
	}

	private void updateLabelingList() {
		String[] labelingStrs = { "Labeling.automatic", "Labeling.on",
				"Labeling.off", "Labeling.pointsOnly" };
		for (String str : labelingStrs) {
			labelingList.addItem(app.getLocalization().getMenu(str));
		}
	}

	private void updateRoundingList() {
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenuWithoutSeparator();
		for (String str : strDecimalSpaces) {
			roundingList.addItem(str);
		}
	}

	@Override
	public Widget getWrappedPanel() {
		return optionsPanel;
	}

	@Override
	public void onResize(int height, int width) {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
