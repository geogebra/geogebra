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

public class CustomizeToolbarGUI extends MyHeaderPanel implements
        CustomizeToolbarListener, SetLabels {
	private class ToolTreeResources implements Tree.Resources {

		public ToolTreeResources() {
		}

		public ImageResource treeClosed() {
			// return AppResources.INSTANCE.tree_open();
			return GuiResources.INSTANCE.algebra_tree_closed();
		}

		public ImageResource treeLeaf() {
			return GuiResources.INSTANCE.algebra_tree_closed();
			// return AppResources.INSTANCE.tree_close();
		}

		public ImageResource treeOpen() {
			return GuiResources.INSTANCE.algebra_tree_open();
			// return AppResources.INSTANCE.tree_close();
		}

	}

	private class ToolTree extends Tree {
		public ToolTree(Tree.Resources res) {
			super(res);
		}

		public String getToolbarString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < getItemCount(); i++) {
				TreeItem branch = getItem(i);
				DraggableTool branchTool = (DraggableTool) (branch
				        .getUserObject());

				int childCount = branch.getChildCount();
				if (childCount == 0) { // new menu with separator
					sb.append("|| ");
				} else

				if (i > 0 && !sb.toString().endsWith("|| ")) {
					sb.append("| ");
				}

				for (int j = 0; j < childCount; j++) {
					TreeItem ti = branch.getChild(j);
					DraggableTool tool = (DraggableTool) (ti.getUserObject());
					int mode = tool.getMode() == null ? -1 : tool.getMode();
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
			return setBranchItem(toolTree.addItem(tool), tool);

		}

		public TreeItem insertBranchItem(final DraggableTool tool, int idx) {
			DraggableTool branchTool = new DraggableTool(tool.getMode(), null);
			TreeItem branch = setBranchItem(
			        toolTree.insertItem(idx, branchTool), branchTool);
			addLeafItem(branch, tool);
			return branch;
		}

		public TreeItem setBranchItem(final TreeItem item,
		        final DraggableTool tool) {

			item.setUserObject(tool);
			tool.treeItem = item;
			tool.addDomHandler(new DropHandler() {
				private boolean removeFromAllTools() {
					if (dragging.getParent() == allToolsPanel) {
						allTools.remove(allTools.indexOf(dragging.getMode()));
						allToolsPanel.remove(dragging);

						App.debug("[CUSTOMIZE] removing from allTools: "
						        + dragging.getTitle());
						return true;
					}
					return false;
				}

				@Override
				public void onDrop(DropEvent event) {
					App.debug("Drop on branch item!");
					event.preventDefault();
					if (dragging != null) {
						App.debug("Drop " + dragging.getTitle());
						if (dragging == tool) {
							App.debug("[CUSTOMIZE] Same tool");
							tool.removeStyleName("branchDropping");
							return;
						}
						boolean fromAllTools = removeFromAllTools();

						int i = 0;
						int idx = 0;
						boolean found = false;
						while (i < toolTree.getItemCount() && !found) {
							if (getItem(i) == item) {
								found = true;
								idx = i;
							}
							i++;
						}

						if (!fromAllTools
						        && dragging.treeItem.getChildCount() > 1) {
							toolTree.insertItem(idx, dragging.treeItem);
						} else {
							App.debug("------------------------------");
							DraggableTool dropped = new DraggableTool(dragging
							        .getMode(), item);
							toolTree.insertBranchItem(dropped, idx);
							if (!fromAllTools) {
								checkEmptyBranch(dragging.treeItem);
								// dragging.treeItem.remove();
							}
						}
						

						App.debug("checking first leaf");
						checkFirstLeaf(dragging.treeItem.getParentItem());
						

						dragging = null;
						tool.removeStyleName("branchDropping");

					}

				}
			}, DropEvent.getType());

			tool.addDomHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					tool.addStyleName("branchDropping");
					event.preventDefault();
					event.stopPropagation();
				}
			}, DragOverEvent.getType());

			tool.addDomHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					tool.removeStyleName("branchDropping");
					event.preventDefault();
					event.stopPropagation();
				}
			}, DragLeaveEvent.getType());

			return item;
		}

		public TreeItem addLeafItem(final TreeItem branch,
		        final DraggableTool tool) {
			return setLeafItem(branch, branch.addItem(tool), tool);
		}

		public TreeItem insertLeafItem(final TreeItem branch,
		        final DraggableTool tool, int idx) {
			return setLeafItem(branch, branch.insertItem(idx, tool), tool);
		}

		public TreeItem setLeafItem(final TreeItem branch, final TreeItem item,
		        final DraggableTool tool) {
			item.setUserObject(tool);
			tool.treeItem = item;
			tool.addDomHandler(new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					event.preventDefault();
					event.stopPropagation();
					if (dragging == null) {
						return;
					}
					
					dropToLeaf(branch, tool);
					
					dragging = null;
				}
			}, DropEvent.getType());

			tool.addDomHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					tool.addStyleName("leafDropping");
				}
			}, DragOverEvent.getType());

			tool.addDomHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					tool.removeStyleName("leafDropping");
				}
			}, DragLeaveEvent.getType());

			return item;
		}
		
		protected void dropToLeaf(final TreeItem branch, final DraggableTool tool) {
		
			int idx = branch.getChildIndex(tool.treeItem);

			if (dragging == tool) {
				tool.removeStyleName("leafDropping");
				return;
			}
			
			int childCount = dragging.treeItem == null ? 0 :dragging.treeItem.getChildCount();

			checkEmptyBranch(dragging.treeItem);
			
			if (childCount == 0) {
				reorderLeaf(branch, dragging, idx);
			} else {
				for (int i = childCount - 1; i > -1; i--) {
					DraggableTool childTool = (DraggableTool)(dragging.treeItem.getChild(i).getUserObject());
					reorderLeaf(branch, childTool, idx);
				}
			}
			
			
			
			tool.removeStyleName("leafDropping");
			
			checkFirstLeaf(branch);
		}

		private void reorderLeaf(final TreeItem branch, final DraggableTool tool, int idx) {
			insertLeafItem(branch, tool, idx);
			int idxMode = allTools.indexOf(dragging.getMode());

			if (idxMode != -1) {
				allTools.remove(idxMode);
			}

		}
		
		protected void dropToBranch() {
			
		}
		
	}

	private class DraggableTool extends FlowPanel {

		private Integer mode;
		private TreeItem treeItem;
		private Image toolbarImg;
		private FlowPanel btn;

		public DraggableTool(Integer mode, TreeItem parent) {
			treeItem = null;
			btn = new FlowPanel();
			addStyleName("customizableToolbarItem");
			btn.addStyleName("toolbar_button");
			setMode(mode);
		}

		public int getSubToolCount() {
			return treeItem == null ? 0 : treeItem.getChildCount();
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

		public boolean isLeaf() {
			return treeItem.getChildCount() == 0;
		}

		public Integer getMode() {
			return mode;
		}

		public void setMode(Integer mode) {
			this.mode = mode;
			clear();
			toolbarImg = new Image(
			        ((GGWToolBar) app.getToolbar()).getImageURL(mode));
			toolbarImg.addStyleName("toolbar_icon");
			toolbarImg.setWidth("32px");
			btn.clear();
			btn.add(toolbarImg);
			String str = app.getMenu(app.getToolName(mode));
			setTitle(str);
			Label text = new Label(str);
			add(LayoutUtil.panelRow(btn, text));
			getElement().setAttribute("mode", mode + " ");
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
			initDrag();
		}
	}

	private static final int PANEL_GAP = 20;

	private static final int MARGIN_Y = 10;

	private AppW app;
	private CustomizeToolbarHeaderPanel header;
	private Label lblAllTools, lblUsedTools;
	private ScrollPanel usedToolsPanel;
	private FlowPanel allToolsPanel;
	private ScrollPanel spAllTools;
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

		spAllTools = new ScrollPanel();

		allToolsPanel = new FlowPanel();
		spAllTools.add(allToolsPanel);
		
		lblAllTools = new Label(app.getMenu("Tools"));
		lblAllTools.setStyleName("panelTitle");

		FlowPanel right = new FlowPanel();
		right.setStyleName("allToolsPanel");

		right.add(lblAllTools);
		right.add(spAllTools);

		allToolsPanel.addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				if (dragging != null) {

					if (dragging.getParent() == allToolsPanel) {
						return;
					}

					App.debug("Drop " + dragging.getTitle());
					TreeItem parent = null;
					if (dragging.isLeaf()) {
						App.debug("[DROP] leaf");
						usedToolToAll(dragging.getMode());
						parent = dragging.treeItem.getParentItem();
						checkEmptyBranch(dragging.treeItem);

					} else {
						App.debug("[DROP] branch");
						if (dragging.treeItem == null) {
							App.debug("[DROP] dragging.treeItem == null");

						}
						for (int i = 0; i < dragging.treeItem.getChildCount(); i++) {
							DraggableTool tool = (DraggableTool) (dragging.treeItem
							        .getChild(i).getUserObject());
							App.debug("Dropping branch");
							usedToolToAll(tool.getMode());
						}

						dragging.treeItem.remove();

					}
					checkFirstLeaf(parent);
					dragging = null;
					allToolsPanel.removeStyleName("toolBarDropping");
					spAllTools.scrollToBottom();
				}
			}
		}, DropEvent.getType());

		allToolsPanel.addDomHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				allToolsPanel.addStyleName("toolBarDropping");
			}
		}, DragOverEvent.getType());

		allToolsPanel.addDomHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				allToolsPanel.removeStyleName("toolBarDropping");
			}
		}, DragLeaveEvent.getType());

		main.add(left);
		main.add(right);
		setContentWidget(main);

	}

	private static void checkEmptyBranch(TreeItem item) {
		if (item == null) {
			return;
		}

		TreeItem branch = item.getParentItem();
		item.remove();
		if (branch != null) {
			int n = branch.getChildCount();
			if (n == 0) {
				branch.remove();
			} else {
				checkFirstLeaf(branch);
			}
		}

	}

	private static void checkFirstLeaf(TreeItem branch) {
		if (branch == null) {
			return;
		}

		TreeItem leaf = branch.getChild(0);
		if (leaf == null) {
			App.debug("[CUSTOMIZE] no leafs, should never happen!");
			return;
		}

		DraggableTool branchTool = (DraggableTool) branch.getUserObject();
		DraggableTool firstTool = (DraggableTool) leaf.getUserObject();
		App.debug("[CUSTOMIZE] branch: " + branchTool.getTitle());
		App.debug("[CUSTOMIZE] first: " + firstTool.getTitle());
		
		if (branchTool.getMode() != firstTool.getMode()) {
			App.debug("[CUSTOMIZE] branch and first tool does not match");
			branchTool.setMode(firstTool.getMode());
		}
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
			oldToolbarString = ((GuiManagerW) app.getGuiManager())
			        .getToolbarDefinition();
			dockPanel = null;
		} else {
			dockPanel = ((GuiManagerW) app.getGuiManager()).getLayout()
			        .getDockManager().getPanel(id);
			oldToolbarString = dockPanel.getDefaultToolbarString();
		}

		buildUsedTools(oldToolbarString);
		// usedToolsPanel.clear();
		//
		// for (Integer mode: usedTools) {
		// usedToolsPanel.add(buildItem(mode));
		// }
		//
		App.debug("[CUSTOMIZE] " + usedTools);
	}

	private void updateAllTools() {
		allTools = CustomizeToolbarModel.generateToolsVector(ToolBarW
		        .getAllTools(app));

		allToolsPanel.clear();

		// allToolsPanel.add(buildItem());

		for (Integer mode : allTools) {
			if (!usedTools.contains(mode) && mode != ToolBar.SEPARATOR) {
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

			if (m != ToolBar.SEPARATOR && element.getMenu() != null) {
				Vector<Integer> menu = element.getMenu();
				final DraggableTool tool = new DraggableTool(menu.get(0), null);

				TreeItem branch = toolTree.addBranchItem(tool);
				tool.treeItem = branch;

				for (int j = 0; j < menu.size(); j++) {
					Integer modeInt = menu.get(j);
					if (modeInt != ToolBar.SEPARATOR) {
						usedTools.add(modeInt);
						toolTree.addLeafItem(branch, new DraggableTool(modeInt,
						        branch));
					}
				}
			}
		}
	}

	public void resetDefaultToolbar() {

		if (dockPanel != null) {
			buildUsedTools(oldToolbarString);
		} else {
			((GuiManagerW) app.getGuiManager()).getToolbarDefinition();
		}

		update();
	}

	private void apply() {
		String current = toolTree.getToolbarString();
		if (dockPanel != null) {
			dockPanel.setToolbarString(current);
			dockPanel.updatePanel(true);
		} else {
			GuiManagerW gm = ((GuiManagerW) app.getGuiManager());
			gm.setToolBarDefinition(current);
			gm.updateToolbar();
		}

		close();
	}

	@Override
	public void onResize() {
		int w = (getOffsetWidth() / 2) - PANEL_GAP;
		int h = (getOffsetHeight()) - getHeaderWidget().getOffsetHeight()
		        - getFooterWidget().getOffsetHeight()
		        - lblUsedTools.getOffsetHeight() - MARGIN_Y;
		usedToolsPanel.setSize(w + "px", h + "px");
		allToolsPanel.setSize(w + "px", h + "px");
		App.debug("[CUSTOMIZE] onResize");
	}
}
