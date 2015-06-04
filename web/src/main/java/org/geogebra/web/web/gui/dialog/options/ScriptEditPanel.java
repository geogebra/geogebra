package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.ScriptInputPanelW;
import org.geogebra.web.web.gui.properties.OptionPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

class ScriptEditPanel extends OptionPanel {


	/**
	 * 
	 */
	private ScriptInputPanelW clickDialog, updateDialog, globalDialog;
	private TabPanel tabbedPane;
	private FlowPanel clickScriptPanel, updateScriptPanel, globalScriptPanel;
	private Localization loc;

	public ScriptEditPanel(ScriptEditorModel model0, final AppW app) {
		this.loc = app.getLocalization();
		int row = 35;
		int column = 15;
		setModel(model0);
		model0.setListener(this);
		tabbedPane = new TabPanel();
		tabbedPane.setStyleName("scriptTabPanel");

		clickDialog = new ScriptInputPanelW(app, app.getPlain("Script"),
				null, row, column, false, false);
		updateDialog = new ScriptInputPanelW(app, app.getPlain("JavaScript"),
				null, row, column, true, false);
		globalDialog = new ScriptInputPanelW(app,
				app.getPlain("GlobalJavaScript"), null, row, column, false,
				true);
		// add(td.getInputPanel(), BorderLayout.NORTH);
		// add(td2.getInputPanel(), BorderLayout.CENTER);
		clickScriptPanel = new FlowPanel();
		clickScriptPanel.add(clickDialog.getInputPanel(row, column, true));
		clickScriptPanel
		.add(clickDialog.getButtonPanel());

		updateScriptPanel = new FlowPanel();
		updateScriptPanel.add(
				updateDialog.getInputPanel(row, column, true));
		updateScriptPanel.add(updateDialog.getButtonPanel());

		globalScriptPanel = new FlowPanel();
		globalScriptPanel.add(globalDialog.getInputPanel(row, column, true));
		globalScriptPanel.add(globalDialog.getButtonPanel());
		setWidget(tabbedPane);

		

		setLabels();
	}

	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		clickDialog.applyModifications();
		updateDialog.applyModifications();
		globalDialog.applyModifications();
	}

	@Override
	public void setLabels() {
		// setBorder(BorderFactory.createTitledBorder(app.getPlain("JavaScript")));
		String ok = loc.getMenu("OK");
		String cancel = loc.getMenu("Cancel");
		
		clickDialog.setLabels(ok, cancel);
		updateDialog.setLabels(ok, cancel);
		globalDialog.setLabels(ok, cancel);
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		if (geos.length != 1){
			return null;
		}

		// remember selected tab
		int idx = tabbedPane.getTabBar().getSelectedTab();

		GeoElement geo = (GeoElement) geos[0];
		clickDialog.setGeo(geo);
		updateDialog.setGeo(geo);
		globalDialog.setGlobal();
		tabbedPane.clear();
		if (geo.canHaveClickScript())
			tabbedPane.add(clickScriptPanel, loc.getMenu("OnClick"));
		if (geo.canHaveUpdateScript())
			tabbedPane.add(updateScriptPanel, loc.getMenu("OnUpdate"));
		tabbedPane.add(globalScriptPanel, loc.getMenu("GlobalJavaScript"));

		// select tab as before
		tabbedPane.selectTab(Math.max(0,	idx));
		return this;
	}

}