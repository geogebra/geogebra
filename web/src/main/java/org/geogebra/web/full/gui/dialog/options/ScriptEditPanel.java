package org.geogebra.web.full.gui.dialog.options;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.dialog.options.model.ScriptInputModel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.dialog.ScriptInputPanelW;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AsyncManager;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.TabPanel;

/**
 * Scripting editor for Web
 */
class ScriptEditPanel extends OptionPanel {

	private ScriptInputModel[] scriptModels;
	private TabPanel tabbedPane;
	private List<FlowPanel> panelWrappers = new ArrayList<>();
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
		setModel(model0);
		model0.setListener(this);
		tabbedPane = new TabPanel();
		tabbedPane.setStyleName("scriptTabPanel");
		scriptModels = ScriptInputModel.getModels(app);
		AsyncManager manager = app.getAsyncManager();

		manager.runOrSchedule(() -> {
			final CommandDispatcher cmdDispatcher = app.getKernel()
					.getAlgebraProcessor().getCmdDispatcher();
			// preload scripting
			cmdDispatcher.getScriptingCommandProcessorFactory();
		});

		for (ScriptInputModel scriptModel : scriptModels) {
			ScriptInputPanelW panel = new ScriptInputPanelW(app, scriptModel);
			scriptModel.setListener(panel);
			panelWrappers.add(panel);
		}
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
		for (ScriptInputModel model: scriptModels) {
			model.setGeos(geos);
			model.updatePanel();
		}
		// remember selected tab
		int idx = tabbedPane.getTabBar().getSelectedTab();
		tabbedPane.clear();
		for (int i = 0; i < scriptModels.length; i++) {
			if (scriptModels[i].checkGeos()) {
				tabbedPane.add(panelWrappers.get(i), loc.getMenu(scriptModels[i].getTitle()));
			}
		}
		// select tab as before
		tabbedPane.selectTab(Math.max(0, idx));
		return this;
	}

}