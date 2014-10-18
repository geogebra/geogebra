package geogebra.web.gui;

import geogebra.common.gui.CustomizeToolbarModel;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.gui.toolbar.ToolbarItem;
import geogebra.common.main.App;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.CustomizeToolbarHeaderPanel.CustomizeToolbarListener;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.toolbar.ToolBarW;

import java.util.Vector;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class CustomizeToolbarGUI extends MyHeaderPanel
implements CustomizeToolbarListener {

	private AppW app;
	private CustomizeToolbarHeaderPanel header;
	private FlowPanel usedToolsPanel;
	private FlowPanel allToolsPanel;
	private Vector<Integer> usedTools;
	private ToolTree toolTree;

	private class ToolTreeResources implements Tree.Resources {

		public ToolTreeResources() {
        }

		public ImageResource treeClosed() {
		//      return AppResources.INSTANCE.tree_open();
		      return GuiResources.INSTANCE.algebra_tree_closed();
	    }

		public ImageResource treeLeaf() {
            return GuiResources.INSTANCE.algebra_tree_closed();
//	        return AppResources.INSTANCE.tree_close();
        }

		public ImageResource treeOpen() {
            return GuiResources.INSTANCE.algebra_tree_open();
	        //return AppResources.INSTANCE.tree_close();
        }
		
	}
	private class ToolTree extends Tree {
		public ToolTree(Tree.Resources res) {
	        super(res);
        }

		public String getToolbarString() {
			// TODO: implement
			return null;
		}
	}

	public CustomizeToolbarGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
	}

	private void addContent() {
		FlowPanel main = new FlowPanel();

		usedToolsPanel = new FlowPanel();
		usedToolsPanel.setStyleName("usedToolsPanel");
		toolTree = new ToolTree(new ToolTreeResources());
		usedToolsPanel.add(toolTree);

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

		buildUsedTools(toolbarDefinition);
		//		usedToolsPanel.clear();
		//		
		//		for (Integer mode: usedTools) {
		//			usedToolsPanel.add(buildItem(mode));
		//		}
		//		
		App.debug("[CUSTOMIZE] " + usedTools);
	}

	private void updateAllTools() {
		Vector<Integer> allTools = CustomizeToolbarModel
				.generateToolsVector(ToolBarW.getAllTools(app));

		allToolsPanel.clear();

		//		allToolsPanel.add(buildItem(ToolBar.SEPARATOR));

		for (Integer mode: allTools) {
			if (true){//!usedTools.contains(mode)) {
				allToolsPanel.add(buildItem(mode));
			}
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

	private FlowPanel buildItem(Integer mode) {
		FlowPanel item = new FlowPanel();
		FlowPanel btn = new FlowPanel();


		item.addStyleName("customizableToolbarItem");
		btn.addStyleName("toolbar_button");
		Image toolbarImg = new Image(((GGWToolBar)app.getToolbar()).getImageURL(mode));
		toolbarImg.addStyleName("toolbar_icon");
		btn.add(toolbarImg);
		Label text = new Label(app.getMenu(mode == ToolBar.SEPARATOR ? "Separator": app.getToolName(mode)));

		item.add(LayoutUtil.panelRow(btn, text));

		item.getElement().setAttribute("mode", mode + " ");
		return item;
	}

	public void buildUsedTools(String toolbarDefinition) {
		toolTree.clear();
		if (usedTools == null) {
			usedTools = new Vector<Integer>();
		}
		// separator
		usedTools.add(ToolBar.SEPARATOR);
		
		// get default toolbar as nested vectors
		Vector<ToolbarItem> defTools = null;
		
		defTools = ToolBar.parseToolbarString(toolbarDefinition);
		for (int i = 0; i < defTools.size(); i++) {
			ToolbarItem element = defTools.get(i);
			Integer m = element.getMode();
		
			if (element.getMenu() != null) {
				Vector<Integer> menu = element.getMenu();
				
				TreeItem current = toolTree.addItem(buildItem(menu.get(0)));
				
	 			for (int j = 0; j < menu.size(); j++) {
					Integer modeInt = menu.get(j);
					int mode = modeInt.intValue();
					if (mode != -1)
						usedTools.add(modeInt);
						current.addItem(buildItem(modeInt));
					}
			}
//			else {
//				Integer modeInt = element.getMode();
//				int mode = modeInt.intValue();
//				if (mode != -1)
//					usedTools.add(modeInt);
//	     			//current = current.addItem(buildItem(modeInt));
//			}
		}
	}

}

