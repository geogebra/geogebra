package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.web.full.gui.dialog.ScriptInputPanelW;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Scripting editor for Web
 */
class ScriptEditPanel extends OptionPanel {

	private final ScriptManager scriptManager;
	private ScriptInputPanelW clickDialog;
	private ScriptInputPanelW updateDialog;
	private ScriptInputPanelW globalDialog;
	private TabPanel tabbedPane;
	private FlowPanel clickScriptPanel;
	private FlowPanel updateScriptPanel;
	private FlowPanel globalScriptPanel;
	private Localization loc;

	/**
	 * 
	 * @param model0
	 *            model
	 * @param app
	 *            application
	 */
	public ScriptEditPanel(ScriptEditorModel model0, final AppW app) {
		this.loc = app.getLocalization();
		this.scriptManager = app.getScriptManager();
		setModel(model0);
		model0.setListener(this);
		tabbedPane = new TabPanel();
		tabbedPane.setStyleName("scriptTabPanel");

		clickDialog = new ScriptInputPanelW(app,
				null, false, false);
		updateDialog = new ScriptInputPanelW(app,
				null, true, false);
		globalDialog = new ScriptInputPanelW(app, null, false,
				true);
		// add(td.getInputPanel(), BorderLayout.NORTH);
		// add(td2.getInputPanel(), BorderLayout.CENTER);
		clickScriptPanel = new FlowPanel();
		clickScriptPanel.add(clickDialog.getInputPanel());
		clickScriptPanel
		.add(clickDialog.getButtonPanel());

		updateScriptPanel = new FlowPanel();
		updateScriptPanel.add(
				updateDialog.getInputPanel());
		updateScriptPanel.add(updateDialog.getButtonPanel());

		globalScriptPanel = new FlowPanel();
		globalScriptPanel.add(globalDialog.getInputPanel());
		globalScriptPanel.add(globalDialog.getButtonPanel());
		setWidget(tabbedPane);

	}

	@Override
	public void setLabels() {
		// setBorder(BorderFactory.createTitledBorder(app.getPlain("JavaScript")));
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		if (geos.length != 1) {
			return null;
		}

		GeoElement geo = (GeoElement) geos[0];
		clickDialog.setGeo(geo);
		updateDialog.setGeo(geo);
		globalDialog.setGlobal();
		// remember selected tab
		int idx = tabbedPane.getTabBar().getSelectedTab();
		tabbedPane.clear();
		if (geo.canHaveClickScript()) {
			tabbedPane.add(clickScriptPanel, loc.getMenu("OnClick"));
		}
		if (geo.canHaveUpdateScript()) {
			tabbedPane.add(updateScriptPanel, loc.getMenu("OnUpdate"));
		}
		if (scriptManager.isJsEnabled()) {
			tabbedPane.add(globalScriptPanel, loc.getMenu("GlobalJavaScript"));
		}

		// select tab as before
		tabbedPane.selectTab(Math.max(0,	idx));
		return this;
	}

}