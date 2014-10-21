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
			// TODO: implement
			return "Howdy!";
		}

		public TreeItem addBranchItem(final DraggableTool tool) {
			final TreeItem current = toolTree.addItem(tool);
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
						TreeItem parent = dragging.parent;

						allTools.remove(allTools.indexOf(dragging.mode));
						allToolsPanel.remove(dragging);
						DraggableTool dropped = new DraggableTool(dragging.mode, current);
						dropped.treeItem = current.addItem(dropped);
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

			return current;
		}

		public TreeItem addLeafItem(final TreeItem branch, final DraggableTool leaf) {
	        TreeItem item = branch.addItem(leaf);
	        
	        leaf.addDomHandler(new DropHandler()
			{
				@Override
				public void onDrop(DropEvent event)
				{
					App.debug("Drop on leaf item!");
					event.preventDefault();
					if (dragging != null)
					{
						int idx = branch.getChildIndex(leaf.treeItem);
						
						branch.insertItem(idx, dragging);
						leaf.removeStyleName("leafDropping");
								
					}
				}
			}, DropEvent.getType());

			leaf.addDomHandler(new DragOverHandler()
			{
				@Override
				public void onDragOver(DragOverEvent event)
				{
					leaf.addStyleName("leafDropping");
				}
			}, DragOverEvent.getType());

			leaf.addDomHandler(new DragLeaveHandler()
			{
				@Override
				public void onDragLeave(DragLeaveEvent event)
				{
					leaf.removeStyleName("leafDropping");
				}
			}, DragLeaveEvent.getType());

		       
	        return item;
        }
	}

	private class DraggableTool extends FlowPanel {

		private Integer mode;
		private TreeItem parent;
		private TreeItem treeItem;
		private Vector<Integer> children;
		public DraggableTool(Integer mode, TreeItem parent) {
			this.mode = mode;
			this.parent = parent;
			treeItem = null; 
			children = null;

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

		private void initDrag() {
			addDomHandler(new DragStartHandler() {

				public void onDragStart(DragStartEvent event) {
					App.debug("!DRAG START!");
					dragging = DraggableTool.this;
					event.setData("text", "draggginggg");
					event.getDataTransfer().setDragImage(getElement(), 10, 10);

				}
			}, DragStartEvent.getType());
		}

		public void addChild(Integer mode) {
			if (children == null) {
				children = new Vector<Integer>();
			}

			children.add(mode);
		}

		public boolean isLeaf() {
			return children == null;
		}
	}

	private AppW app;
	private CustomizeToolbarHeaderPanel header;
	private Label lblAllTools, lblUsedTools;
	private ScrollPanel usedToolsPanel;
	private FlowPanel allToolsPanel;
	private Vector<Integer> usedTools;
	private Vector<Integer> allTools;
	private ToolTree toolTree;
	private static DraggableTool dragging = null;
	private static TreeItem allToolsRoot = new TreeItem();
	private Button btDefalutToolbar;
	private Button btApply;

	public CustomizeToolbarGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
		addFooter();
		update(-1);
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

						for (Integer mode: dragging.children) {
							App.debug("Dropping branch");
							usedToolToAll(mode);
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
					
			}
		});
				
		btApply = new Button();

		btApply.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				App.debug("[Customize] " + toolTree.getToolbarString());
			}
		});
		
		
		FlowPanel btPanel = new FlowPanel();
		btPanel.setStyleName("DialogButtonPanel");

		btPanel.add(btDefalutToolbar);
		btPanel.add(btApply);
		setFooterWidget(btPanel);
	}
	
	private void usedToolToAll(int mode) {
		if (mode != ToolBar.SEPARATOR && usedTools.contains(mode)) {
			usedTools.remove(usedTools.indexOf(mode));
			allToolsPanel.add(new DraggableTool(mode, allToolsRoot));
		}


	}

	public void update(int id) {
		updateUsedTools(id);
		updateAllTools();
		setLabels();
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
					DraggableTool leaf = new DraggableTool(modeInt, branch);
					TreeItem t = toolTree.addLeafItem(branch, leaf);
					leaf.treeItem = t;
					tool.addChild(modeInt);
				}
			}
		}
	}
	
}



