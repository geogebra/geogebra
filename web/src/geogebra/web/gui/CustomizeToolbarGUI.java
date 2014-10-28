package geogebra.web.gui;

import geogebra.common.gui.CustomizeToolbarModel;
import geogebra.common.gui.SetLabels;
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

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class CustomizeToolbarGUI extends MyHeaderPanel
implements CustomizeToolbarListener, SetLabels {
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
			StringBuilder sb = new StringBuilder();
			for (int i =0; i < getItemCount(); i++) {
				TreeItem branch = getItem(i);
				DraggableTool branchTool = (DraggableTool)(branch.getUserObject());
            	
				int childCount = branch.getChildCount();
      			if (childCount == 0) { // new menu with separator
					sb.append("|| ");
				} else 
					
				if (i > 0 && !sb.toString().endsWith("|| ")) {
					sb.append("| ");
				}
				
				
            	for (int j=0; j < childCount; j++) {
					TreeItem ti = branch.getChild(j);
					DraggableTool tool = (DraggableTool)(ti.getUserObject());
	            	int mode = tool.mode == null ? -1: tool.mode;
					if (mode < 0) {
	            		sb.append(", "); // separator
	            	} else { // mode number
						sb.append(mode);
						sb.append(" ");
					}
	            }
		            
		        }
			return sb.toString().trim();
		}

		public TreeItem addBranchItem(final DraggableTool tool) {
			final TreeItem item = toolTree.addItem(tool);
			item.setUserObject(tool);
			
			tool.addDomHandler(new DropHandler()
			{
				@Override
				public void onDrop(DropEvent event)
				{
					App.debug("Drop on branch item!");
					event.preventDefault();
					if (dragging != null)
					{
						App.debug("Drop " + dragging.getTitle());
		
				
						allTools.remove(allTools.indexOf(dragging.mode));
				
						DraggableTool dropped = new DraggableTool(dragging.mode, item);
						dropped.treeItem = item.addItem(dropped);
						item.setUserObject(dropped);
						dragging = null;
						tool.removeStyleName("toolBarDropping");
					}
				}
			}, DropEvent.getType());

			tool.addDomHandler(new DragOverHandler()
			{
				@Override
				public void onDragOver(DragOverEvent event)
				{
					tool.addStyleName("toolBarDropping");
				}
			}, DragOverEvent.getType());

			tool.addDomHandler(new DragLeaveHandler()
			{
				@Override
				public void onDragLeave(DragLeaveEvent event)
				{
					tool.removeStyleName("toolBarDropping");
				}
			}, DragLeaveEvent.getType());

			return item;
		}

		public TreeItem addLeafItem(final TreeItem branch, final DraggableTool tool) {
			return setLeafItem(branch, branch.addItem(tool), tool);
		}
		
		public TreeItem insertLeafItem(final TreeItem branch, final DraggableTool tool, int idx) {
	        return setLeafItem(branch, branch.insertItem(idx, tool), tool);
		}
		
		public TreeItem setLeafItem(final TreeItem branch, final TreeItem item, final DraggableTool tool) {
			item.setUserObject(tool);
			tool.treeItem = item;
	        tool.addDomHandler(new DropHandler()
			{
				@Override
				public void onDrop(DropEvent event)
				{
					App.debug("Drop on leaf item!");
					event.preventDefault();
					if (dragging != null)
					{
						int idx = branch.getChildIndex(tool.treeItem);
						insertLeafItem(branch, dragging, idx);
						int idxMode = allTools.indexOf(dragging.mode);
						
						if (idxMode != -1 )  {
							allTools.remove(idxMode);
						}
						
						tool.removeStyleName("leafDropping");
								
					}
				}
			}, DropEvent.getType());

			tool.addDomHandler(new DragOverHandler()
			{
				@Override
				public void onDragOver(DragOverEvent event)
				{
					tool.addStyleName("leafDropping");
				}
			}, DragOverEvent.getType());

			tool.addDomHandler(new DragLeaveHandler()
			{
				@Override
				public void onDragLeave(DragLeaveEvent event)
				{
					tool.removeStyleName("leafDropping");
				}
			}, DragLeaveEvent.getType());

	        return item;
        }
	}

	private class DraggableTool extends FlowPanel {

		private Integer mode;
		private TreeItem treeItem;
		public DraggableTool(Integer mode, TreeItem parent) {
			this.mode = mode;
			treeItem = null; 

			FlowPanel btn = new FlowPanel();
			addStyleName("customizableToolbarItem");
			btn.addStyleName("toolbar_button");
			Image toolbarImg = new Image(((GGWToolBar)app.getToolbar()).getImageURL(mode));
			toolbarImg.addStyleName("toolbar_icon");
			btn.add(toolbarImg);
			String str = app.getMenu(mode == ToolBar.SEPARATOR ? "Separator": app.getToolName(mode));
			setTitle(str);
			Label text = new Label(str);
			add(LayoutUtil.panelRow(btn, text));
			getElement().setAttribute("mode", mode + " ");
			getElement().setDraggable(Element.DRAGGABLE_TRUE);

			initDrag();
		}

		public boolean isSeparator() {
	        return mode == ToolBar.SEPARATOR;
        }

		private void initDrag() {
			addDomHandler(new DragStartHandler() {

				public void onDragStart(DragStartEvent event) {
					App.debug("!DRAG START!");
					
					if (isSeparator() && getParent() == allToolsPanel) {
						// infinite number of separators can be dragged from Tools Panel.
						dragging = new DraggableTool(ToolBar.SEPARATOR, null);
					
					} else {
						dragging = DraggableTool.this;
					}
					
					event.setData("text", "draggginggg");
					event.getDataTransfer().setDragImage(getElement(), 10, 10);

				}
			}, DragStartEvent.getType());
		}

		public boolean isLeaf() {
			return treeItem.getChildCount() == 0;
		}
	}

	private AppW app;
	private CustomizeToolbarHeaderPanel header;
	private Label lblAllTools, lblUsedTools;
	private ScrollPanel usedToolsPanel;
	FlowPanel allToolsPanel;
	private Vector<Integer> usedTools;
	private Vector<Integer> allTools;
	private ToolTree toolTree;
	static DraggableTool dragging = null;
	private static TreeItem allToolsRoot = new TreeItem();
	private Button btDefalutToolbar;
	private Button btApply;
	private String oldToolbarString;
	private DockPanelW dockPanel;
	private int toolBarId;

	public CustomizeToolbarGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
		addFooter();
		toolBarId = -1;
		update();
	}

	private void addContent() {
		FlowPanel main = new FlowPanel();

		FlowPanel left = new FlowPanel();
		left.setStyleName("usedToolsPanel");
		
		lblUsedTools = new Label(app.getMenu("Toolbar"));
		lblUsedTools.setStyleName("panelTitle");
		
		usedToolsPanel = new ScrollPanel();
		usedToolsPanel.setStyleName("usedToolsPanel");
		
		
		toolTree = new ToolTree(new ToolTreeResources());
		usedToolsPanel.add(toolTree);

		left.add(lblUsedTools);
		left.add(usedToolsPanel);
		
		allToolsPanel = new FlowPanel();

		lblAllTools = new Label(app.getMenu("Tools"));
		lblAllTools.setStyleName("panelTitle");
		
		FlowPanel right = new FlowPanel();
		right.setStyleName("allToolsPanel");
		
		right.add(lblAllTools);
		right.add(allToolsPanel);
	
		
		allToolsPanel.addDomHandler(new DropHandler()
		{
			@Override
			public void onDrop(DropEvent event)
			{
				event.preventDefault();
				if (dragging != null)
				{
					App.debug("Drop " + dragging.getTitle());

					if (dragging.isLeaf()) {
						usedToolToAll(dragging.mode);
						if (dragging.treeItem != null) {
							dragging.treeItem.remove();
						}
					} else {

						for (int i=0; i < dragging.treeItem.getChildCount(); i++) {
							DraggableTool tool = (DraggableTool)(dragging.treeItem.getChild(i).getUserObject());
							App.debug("Dropping branch");
							usedToolToAll(tool.mode);
						}

						dragging.treeItem.remove();
					}

					dragging = null;
					allToolsPanel.removeStyleName("toolBarDropping");
				}
			}
		}, DropEvent.getType());

		allToolsPanel.addDomHandler(new DragOverHandler()
		{
			@Override
			public void onDragOver(DragOverEvent event)
			{
				allToolsPanel.addStyleName("toolBarDropping");
			}
		}, DragOverEvent.getType());

		allToolsPanel.addDomHandler(new DragLeaveHandler()
		{
			@Override
			public void onDragLeave(DragLeaveEvent event)
			{
				allToolsPanel.removeStyleName("toolBarDropping");
			}
		}, DragLeaveEvent.getType());

		
		main.add(left);
		main.add(right);
		setContentWidget(main);
		
	}

	private void addFooter() {
		btDefalutToolbar = new Button();
		
		btDefalutToolbar.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				App.debug("[Customize] reset");
				resetDefaultToolbar();
				
			}
		});
				
		btApply = new Button();

		btApply.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				App.debug("[Customize] apply");
				apply();
			}
			});
		
		
		FlowPanel btPanel = new FlowPanel();
		btPanel.setStyleName("DialogButtonPanel");

		btPanel.add(btDefalutToolbar);
		btPanel.add(btApply);
		setFooterWidget(btPanel);
	}
	
	private void usedToolToAll(int mode) {
		if (usedTools.contains(mode)) {
			usedTools.remove(usedTools.indexOf(mode));
		}
		if (mode != -1) {
			allToolsPanel.add(new DraggableTool(mode, allToolsRoot));
		}


	}

	public void update() {
		update(toolBarId);
	}
	
	public void update(int id) {
		toolBarId = id;
		updateUsedTools(id);
		updateAllTools();
		setLabels();
	}

	private void updateUsedTools(int id) {

		oldToolbarString = null;
		if (id == -1) {
			oldToolbarString = ((GuiManagerW)app.getGuiManager()).getToolbarDefinition();
			dockPanel = null;
		} else  {
			dockPanel = ((GuiManagerW)app.getGuiManager()).getLayout().getDockManager().getPanel(id);
			oldToolbarString = dockPanel.getDefaultToolbarString();
		}

		buildUsedTools(oldToolbarString);
		//		usedToolsPanel.clear();
		//		
		//		for (Integer mode: usedTools) {
		//			usedToolsPanel.add(buildItem(mode));
		//		}
		//		
		App.debug("[CUSTOMIZE] " + usedTools);
	}

	private void updateAllTools() {
		allTools = CustomizeToolbarModel
				.generateToolsVector(ToolBarW.getAllTools(app));

		allToolsPanel.clear();

		//		allToolsPanel.add(buildItem(ToolBar.SEPARATOR));

		for (Integer mode: allTools) {
			if (!usedTools.contains(mode)) {
				DraggableTool tool = new DraggableTool(mode, allToolsRoot);
				allToolsPanel.add(tool);
			}
		}
	}

	private void addHeader() {
		header = new CustomizeToolbarHeaderPanel(app, this);
		setHeaderWidget(header);

	}

	@Override
	public void setLabels() {
		if (header != null) {
			header.setLabels();
		}
		
		lblUsedTools.setText(app.getMenu("Toolbar"));
		lblAllTools.setText(app.getMenu("Tools"));
		btDefalutToolbar.setText(app.getMenu("Toolbar.ResetDefault"));
		btApply.setText(app.getPlain("Apply"));
	}

	public void buildUsedTools(String toolbarDefinition) {
		toolTree.clear();
		if (usedTools == null) {
			usedTools = new Vector<Integer>();
		}
		usedTools.clear();

		// get default toolbar as nested vectors
		Vector<ToolbarItem> defTools = null;

		defTools = ToolBar.parseToolbarString(toolbarDefinition);
		for (int i = 0; i < defTools.size(); i++) {
			ToolbarItem element = defTools.get(i);
			Integer m = element.getMode();
			if (element.getMenu() != null) {
				Vector<Integer> menu = element.getMenu();
				final DraggableTool tool = new DraggableTool(menu.get(0), null);

				TreeItem branch = toolTree.addBranchItem(tool);

				for (int j = 0; j < menu.size(); j++) {
					Integer modeInt = menu.get(j);
					int mode = modeInt.intValue();
					if (mode != -1)
						usedTools.add(modeInt);
						toolTree.addLeafItem(branch, new DraggableTool(modeInt, branch));
					}
			}
		}
	}
	
	public void resetDefaultToolbar() {
		
		if (dockPanel != null) {
			buildUsedTools(oldToolbarString);
		} else {
			((GuiManagerW)app.getGuiManager()).getToolbarDefinition();
		}
		
		update();
	}
	
	private void apply() {
		String current = toolTree.getToolbarString();
		
//		App.debug("[CUSTOMIZE] original toolbar is: " + oldToolbarString);
//		App.debug("[CUSTOMIZE] setting  toolbar to: " + current);
//		App.debug("[CUSTOMIZE] equal? " + current.equals(oldToolbarString));
		
		if (dockPanel != null) {
			dockPanel.setToolbarString(current);
			dockPanel.updatePanel(true);
		} else {
			GuiManagerW gm = ((GuiManagerW)app.getGuiManager());
			gm.setToolBarDefinition(current);
			gm.updateToolbar();
		}
		
		close();
	}
}




