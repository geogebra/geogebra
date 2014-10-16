package geogebra.web.gui;

import geogebra.common.gui.CustomizeToolbarModel;
import geogebra.common.main.App;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.main.AppW;
import geogebra.web.gui.CustomizeToolbarHeaderPanel.CustomizeToolbarListener;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.toolbar.ToolBarW;

import java.util.Vector;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class CustomizeToolbarGUI extends MyHeaderPanel
	implements CustomizeToolbarListener {

	private AppW app;
	private CustomizeToolbarHeaderPanel header;
	private FlowPanel usedToolsPanel;
	private FlowPanel allToolsPanel;
	public CustomizeToolbarGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
	}

	private void addContent() {
		FlowPanel main = new FlowPanel();
		
		usedToolsPanel = new FlowPanel();
		usedToolsPanel.setStyleName("usedToolsPanel");
		
		allToolsPanel = new FlowPanel();
		allToolsPanel.setStyleName("allToolsPanel");
		
		main.add(usedToolsPanel);
		main.add(allToolsPanel);
		setContentWidget(main);
		update(-1);
    }

	public void update(int id) {
		 updateUsedTools(id);
		 updateAllTools();
	}

	private void updateUsedTools(int id) {
		
		String toolbarDefinition = null;
		if (id == -1) {
			toolbarDefinition = ((GuiManagerW)app.getGuiManager()).getToolbarDefinition(); 
		} else  {
			DockPanelW panel = ((GuiManagerW)app.getGuiManager()).getLayout().getDockManager().getPanel(id);
			toolbarDefinition = panel.getDefaultToolbarString();
		}
		
		Vector<Integer> usedTools = CustomizeToolbarModel
				.generateToolsVector(toolbarDefinition);	    
		
		usedToolsPanel.clear();
		for (Integer mode: usedTools) {
			usedToolsPanel.add(buildItem(mode));
		}
		
		App.debug("[CUSTOMIZE] " + usedTools);
	}

	private void updateAllTools() {
		Vector<Integer> allTools = CustomizeToolbarModel
				.generateToolsVector(ToolBarW.getAllTools(app));
		 
		allToolsPanel.clear();
		
//		allToolsPanel.add(buildItem(ToolBar.SEPARATOR));
		
		for (Integer mode: allTools) {
			allToolsPanel.add(buildItem(mode));
		}
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

	private FlowPanel buildItem(int mode) {
		FlowPanel item = new FlowPanel();
		FlowPanel btn = new FlowPanel();


		item.addStyleName("customizableToolbarItem");
		btn.addStyleName("toolbar_button");
		Image toolbarImg = new Image(((GGWToolBar)app.getToolbar()).getImageURL(mode));
		toolbarImg.addStyleName("toolbar_icon");
		btn.add(toolbarImg);
		Label text = new Label(app.getMenu(app.getToolName(mode)));
	
		item.add(LayoutUtil.panelRow(btn, text));
		
		item.getElement().setAttribute("mode", mode + " ");
		return item;
	}
}
