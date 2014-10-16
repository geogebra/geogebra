package geogebra.web.gui;

import geogebra.common.gui.CustomizeToolbarModel;
import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.CustomizeToolbarHeaderPanel.CustomizeToolbarListener;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.toolbar.ToolBarW;

import java.util.Vector;

public class CustomizeToolbarGUI extends MyHeaderPanel
	implements CustomizeToolbarListener {

	private AppW app;
	private CustomizeToolbarHeaderPanel header;

	public CustomizeToolbarGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
	}

	private void addContent() {
		update(-1);
    }

	public void update(int id) {
		 updateUsedTools(id);
		 updateAllTools();
	}

	private void updateUsedTools(int id) {
		
		String toolbarDefinition = null;
		if (id == -1) {
			toolbarDefinition = app.getGuiManager().getToolbarDefinition(); 
		} else  {
			DockPanelW panel = (DockPanelW) app.getGuiManager().getLayout().getDockManager().getPanel(id);
			toolbarDefinition = panel.getDefaultToolbarString();
		}
		
		App.debug("[CUSTOMIZE] "); 
		Vector<Integer> usedTools = CustomizeToolbarModel
				.generateToolsVector(toolbarDefinition);	    
		App.debug("[CUSTOMIZE] " + usedTools);
	}

	private void updateAllTools() {
		Vector<Integer> allTools = CustomizeToolbarModel
				.generateToolsVector(ToolBarW.getAllTools(app));
		 
		
	}

	private void addHeader() {
		header = new CustomizeToolbarHeaderPanel(app, this);
		setHeaderWidget(header);
		
	}

	public void setLabels() {
		if (header != null) {
			header.setLabels();
		}
	}

}
