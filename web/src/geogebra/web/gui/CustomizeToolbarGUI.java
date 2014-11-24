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

/**
 * A GUI to customize the toolbar
 *
 */
public class CustomizeToolbarGUI extends MyHeaderPanel implements
        CustomizeToolbarListener, SetLabels {
	private class ToolTreeResources implements Tree.Resources {

		public ToolTreeResources() {
		}

		public ImageResource treeClosed() {
			return GuiResources.INSTANCE.algebra_tree_closed();
		}

		public ImageResource treeLeaf() {
			return GuiResources.INSTANCE.algebra_tree_closed();
		}

		public ImageResource treeOpen() {
			return GuiResources.INSTANCE.algebra_tree_open();
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
		
		@Override
        public void insertItem(int before, TreeItem item){
			super.insertItem(before, item);
			toolbarChanged(toolTree.getToolbarString());
		}
		public int indexOfBranch(TreeItem item) {
			for (int i = 0; i < getItemCount(); i++) {
				if (getItem(i) == item) {
					return i;
				}
			}
			return -1;
		}

		public void removeBranchIfEmpty(TreeItem branch) {
			if (branch.getChildCount() == 0) {
				branch.remove();
			}
		}
		
		public TreeItem addBranchItem(final DraggableTool tool) {
			return setBranchItem(toolTree.addItem(tool), tool);

		} 

		native void toolbarChanged(String toolbarString) /*-{
	        if($wnd.onGgbToolbarChanged){
	        	$wnd.onGgbToolbarChanged(toolbarString);
	        }
	        
        }-*/;

		public TreeItem insertBranchItem(final DraggableTool tool, int idx) {
			DraggableTool branchTool = new DraggableTool(tool.getMode());
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
				
				@Override
				public void onDrop(DropEvent event) {
					App.debug("Drop on branch item!");
					event.preventDefault();
					if (dragging != null) {
						App.debug("Drop " + dragging.getTitle());
						if (dragging == tool) {
							App.debug("[CUSTOMIZE] Same tool");
							tool.removeStyleName("insertBeforeBranch");
							tool.removeStyleName("insertAfterBranch");
							return;
						}
	
						boolean fromAllTools = removeFromAllTools();

						int idx = toolTree.indexOfBranch(item);
			
						int y = event.getNativeEvent().getClientY();
						
						boolean insertBefore = tool.isTopHit(y); 
					
						if (!fromAllTools) {
							if (idx < toolTree.getItemCount()) {
								TreeItem parent = dragging.treeItem.getParentItem();
								toolTree.insertItem(idx, dragging.treeItem);
								if (dragging.treeItem.getChildCount() == 0) {
									addLeafItem(dragging.treeItem, dragging.duplicate());
									checkFirstLeaf(parent);
									removeBranchIfEmpty(parent);
								  }
								} else {
								
								toolTree.addBranchItem(dragging);
							
							}
						} else {
							DraggableTool dropped = new DraggableTool(dragging.getMode());
							if (insertBefore) {
								if (idx + 1 == toolTree.getItemCount()) {
									toolTree.addBranchItem(dropped);
								} else {
									toolTree.insertBranchItem(dropped, idx);
									
								}
							} else {
								toolTree.insertBranchItem(dropped, idx);
								
							}
							if (!fromAllTools) {
								checkEmptyBranch(dragging.treeItem);
								// dragging.treeItem.remove();
							}
						}
						
						if (dragging != null) {
							checkFirstLeaf(dragging.treeItem.getParentItem());
							dragging = null;
						}
						
						tool.removeStyleName("insertBeforBranch");
						tool.removeStyleName("insertAfterBranch");
					}
				}
				
				private void clearDragging() {
					dragging = null;
					clearIndicators(tool);
				}

				private void clearIndicators(DraggableTool branchTool) {
					branchTool.removeStyleName("insertBeforBranch");
					branchTool.removeStyleName("insertAfterBranch");
					branchTool.addStyleName("branch");
				}

				private boolean removeFromAllTools() {
					if (dragging.getParent() == allToolsPanelContent) {
						allTools.remove(allTools.indexOf(dragging.getMode()));
						allToolsPanelContent.remove(dragging);

						App.debug("[CUSTOMIZE] removing from allTools: "
								+ dragging.getTitle());
						return true;
					}
					return false;
				}
				private boolean isDropOnItself() {
					if (dragging == tool) {
						App.debug("[CUSTOMIZE] Same tool");
						clearDragging();
						return true;
					}
					return false;
				}
				
				private void reorderTree(int idx) {
					if (idx < toolTree.getItemCount()) {
						TreeItem parent = dragging.treeItem.getParentItem();
						toolTree.insertItem(idx, dragging.treeItem);
						if (dragging.treeItem.getChildCount() == 0) {
							addLeafItem(dragging.treeItem, dragging.duplicate());
							checkFirstLeaf(parent);
							removeBranchIfEmpty(parent);
						  }
						} else {
						
						toolTree.addBranchItem(dragging);
					
					}
				}

			}, DropEvent.getType());

			tool.addDomHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					event.preventDefault();
					event.stopPropagation();
					tool.onDragOver(event, "insertBeforeBranch", "insertAfterBranch");
					
				}
			}, DragOverEvent.getType());

			tool.addDomHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					event.preventDefault();
					event.stopPropagation();
					tool.removeStyleName("insertBeforeBranch");
					tool.removeStyleName("insertAfterBranch");
				}
			}, DragLeaveEvent.getType());
			toolbarChanged(toolTree.getToolbarString());
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
					
					int y = event.getNativeEvent().getClientY();
					
					dropToLeaf(branch, tool, tool.isTopHit(y));
					
					dragging = null;
				}
			}, DropEvent.getType());

			tool.addDomHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					tool.onDragOver(event, "insertBeforeLeaf", "insertAfterLeaf");
				}
			}, DragOverEvent.getType());

			tool.addDomHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					tool.removeStyleName("insertBeforeLeaf");
					tool.removeStyleName("insertAfterLeaf");
				}
			}, DragLeaveEvent.getType());
			toolbarChanged(toolTree.getToolbarString());
			return item;
		}
		
		protected void dropToLeaf(final TreeItem branch, final DraggableTool tool, boolean insertBefore) {
		
			int idx = branch.getChildIndex(tool.treeItem);

			if (dragging == tool) {
				tool.removeStyleName(insertBefore ? "insertBeforeLeaf": "insertAfterLeaf");
				return;
			}
			
			int childCount = dragging.treeItem == null ? 0 :dragging.treeItem.getChildCount();

			checkEmptyBranch(dragging.treeItem);
			
			if (childCount == 0) {
				reorderLeaf(branch, dragging, insertBefore ? idx:
					idx + 1);
			} else {
				for (int i = childCount - 1; i > -1; i--) {
					DraggableTool childTool = (DraggableTool)(dragging.treeItem.getChild(i).getUserObject());
					reorderLeaf(branch, childTool, idx);
				}
			}

			tool.removeStyleName("insertBeforeLeaf");
			tool.removeStyleName("insertAfterLeaf");
				
			checkFirstLeaf(branch);
		}

		private void reorderLeaf(final TreeItem branch, final DraggableTool tool, int idx) {
			if (idx < branch.getChildCount()){
				insertLeafItem(branch, tool, idx);
			} else {
				addLeafItem(branch, tool);
			}
				
			int idxMode = allTools.indexOf(dragging.getMode());

			if (idxMode != -1) {
				allTools.remove(idxMode);
			}
		}
	}

	private class DraggableTool extends FlowPanel {

		private Integer mode;
		TreeItem treeItem;
		private Image toolbarImg;
		private FlowPanel btn;

		public DraggableTool(Integer mode) {
			treeItem = null;
			btn = new FlowPanel();
			addStyleName("customizableToolbarItem");
			btn.addStyleName("toolbar_button");
			setMode(mode);
			int width = usedToolsPanelContent.getOffsetWidth() - DRAGABLE_TOOLS_PADDING;
			if (width > 0) {
				setWidth(width  + "px");
			}
		}
		
		public DraggableTool duplicate() {
			return new DraggableTool(mode);
        }

		private void initDrag() {
			addDomHandler(new DragStartHandler() {

				public void onDragStart(DragStartEvent event) {
					App.debug("!DRAG START!");
					dragging = DraggableTool.this;
					event.setData("text", "draggginggg");
					event.getDataTransfer().setDragImage(getElement(), 10, 10);
					event.stopPropagation();

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
			toolbarImg = new NoDragImage(
			        ((GGWToolBar) app.getToolbar()).getImageURL(mode),32);
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
		
		public boolean isTopHit(int y) {
			return (y > getAbsoluteTop() && 
					y < getAbsoluteTop() + getOffsetHeight()/2); 
		}
		
		public void onDragOver(DragOverEvent event, final String before,
				final String after) {
			int y = event.getNativeEvent().getClientY();
			boolean topHit = isTopHit(y);
			
			if (topHit) {
				addStyleName(before);
				removeStyleName(after);
			} else {
				if (treeItem.getState()) {
					return;
				} 
				addStyleName(after);
				removeStyleName(before);
			}
		}
	}

	private static final int PANEL_GAP = 30;
	private static final int MARGIN_Y = 21;
	private static final int ALLTOOLS_SCROLLPANEL_PADDING = 17;
	private static final int DRAGABLE_TOOLS_PADDING = 55;
	private static final int DRAGABLE_TOOLS_CHILD_PADDING = 16;
	
	/** application **/
	AppW app;
	private CustomizeToolbarHeaderPanel header;
	private Label lblAllTools, lblUsedTools;
	/** contains the {@link #toolTree} of the used tools **/
	ScrollPanel usedToolsPanelContent;
	/** flowPanel in a {@link #spAllTools scrollPanel} which contains all available tools **/
	FlowPanel allToolsPanelContent;
	/** contains the panel with the tools **/
	ScrollPanel spAllTools;
	private Vector<Integer> usedTools;
	/** all tools **/
	Vector<Integer> allTools;
	/** tree which contains the used tools **/
	ToolTree toolTree;
	/** element for dragging **/
	static DraggableTool dragging = null;
	private Button btDefalutToolbar;
	private Button btApply;
	private String oldToolbarString;
	private DockPanelW dockPanel;
	private int toolBarId;
	
	

	/**
	 * @param app {@link AppW}
	 */
	public CustomizeToolbarGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
		addFooter();
		toolBarId = -1;
		update();
	}

	private void addHeader() {
		header = new CustomizeToolbarHeaderPanel(app, this);
		setHeaderWidget(header);
	}
	
	private void addContent() {
		FlowPanel main = new FlowPanel();
		main.add(getLeftPanel());
		main.add(getRightPanel());
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
		btPanel.setStyleName("customizeToolbarButtonPanel");
		btPanel.add(btDefalutToolbar);
		btPanel.add(btApply);
		setFooterWidget(btPanel);
	}

	/**
	 * inits the right panel and returns it.
	 * @return FlowPanel
	 */
	private FlowPanel getRightPanel() {
		FlowPanel right = new FlowPanel();
		right.setStyleName("allToolsPanel");

		//caption of right panel
		lblAllTools = new Label(app.getMenu("Tools"));
		lblAllTools.setStyleName("panelTitle");
		
		//content of right panel 
		spAllTools = new ScrollPanel();
		spAllTools.addStyleName("allToolsScrollPanel");
		allToolsPanelContent = new FlowPanel();
		allToolsPanelContent.addStyleName("allToolsPanelContent");
		allToolsPanelContent.addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				if (dragging != null) {

					if (dragging.getParent() == allToolsPanelContent) {
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
					allToolsPanelContent.removeStyleName("toolBarDropping");
					spAllTools.scrollToBottom();
					toolTree.toolbarChanged(toolTree.getToolbarString());
				}
			}
		}, DropEvent.getType());

		allToolsPanelContent.addDomHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				allToolsPanelContent.addStyleName("toolBarDropping");
			}
		}, DragOverEvent.getType());

		allToolsPanelContent.addDomHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				allToolsPanelContent.removeStyleName("toolBarDropping");
			}
		}, DragLeaveEvent.getType());
		
		spAllTools.add(allToolsPanelContent);
		
		right.add(lblAllTools);
		right.add(spAllTools);
		
		return right;
    }
	
	/**
	 * inits the left panel and returns it
	 * @return FlowPanel
	 */
	private FlowPanel getLeftPanel() {
		FlowPanel left = new FlowPanel();
		left.setStyleName("usedToolsPanel");

		lblUsedTools = new Label(app.getMenu("Toolbar"));
		lblUsedTools.setStyleName("panelTitle");

		usedToolsPanelContent = new ScrollPanel();
		usedToolsPanelContent.setStyleName("usedToolsPanelContent");

		toolTree = new ToolTree(new ToolTreeResources());
		usedToolsPanelContent.add(toolTree);

		left.add(lblUsedTools);
		left.add(usedToolsPanelContent);
		
		return left;
    }

	/**
	 * @param item {@link TreeItem}
	 */
	static void checkEmptyBranch(TreeItem item) {
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

	/**
	 * @param branch {@link TreeItem}
	 */
	static void checkFirstLeaf(TreeItem branch) {
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

	/**
	 * 
	 * @param mode int
	 */
	void usedToolToAll(int mode) {
		if (usedTools.contains(mode)) {
			usedTools.remove(usedTools.indexOf(mode));
		}
		if (mode != -1) {
			allToolsPanelContent.add(new DraggableTool(mode));
		}
	}

	/**
	 * updates the the tools
	 */
	public void update() {
		update(toolBarId);
	}

	/**
	 * updates the tools
	 */
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

		allToolsPanelContent.clear();

		// allToolsPanel.add(buildItem());

		for (Integer mode : allTools) {
			if (!usedTools.contains(mode) && mode != ToolBar.SEPARATOR) {
				DraggableTool tool = new DraggableTool(mode);
				allToolsPanelContent.add(tool);
			}
		}
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

	/**
	 * @param toolbarDefinition String
	 */
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
				final DraggableTool tool = new DraggableTool(menu.get(0));

				TreeItem branch = toolTree.addBranchItem(tool);
				tool.treeItem = branch;

				for (int j = 0; j < menu.size(); j++) {
					Integer modeInt = menu.get(j);
					if (modeInt != ToolBar.SEPARATOR) {
						usedTools.add(modeInt);
						toolTree.addLeafItem(branch, new DraggableTool(modeInt));
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public void resetDefaultToolbar() {
		
		if (dockPanel != null) {
			buildUsedTools(dockPanel.getDefaultToolbarString());
		} else {
			String toolbarStr = ((GuiManagerW) app.getGuiManager()).getDefaultToolbarString();
			setGeneralToolbar(toolbarStr);
			buildUsedTools(toolbarStr);
		}

		update();
	}

	/**
	 * changes the toolbar-settings
	 */
	void apply() {
		String current = toolTree.getToolbarString();
		if (dockPanel != null) {
			dockPanel.setToolbarString(current);
			dockPanel.updatePanel(true);
		} else {
			setGeneralToolbar(current);
		}

		close();
	}

	private void setGeneralToolbar(String toolbarString) {
		GuiManagerW gm = ((GuiManagerW) app.getGuiManager());
		gm.setToolBarDefinition(toolbarString);
		gm.updateToolbar();
	}

	@Override
	public void onResize() {
		int w = (getOffsetWidth() / 2) - PANEL_GAP;
		int h = (getOffsetHeight()) - getHeaderWidget().getOffsetHeight()
		        - getFooterWidget().getOffsetHeight()
		        - lblUsedTools.getOffsetHeight() - MARGIN_Y;

		usedToolsPanelContent.setSize(w + "px", h + "px");
		spAllTools.setSize(w + "px", h + "px");

		allToolsPanelContent.getElement().setAttribute(
		        "style",
		        "min-height: " + (h - ALLTOOLS_SCROLLPANEL_PADDING)
		                + "px; width: " + (w - ALLTOOLS_SCROLLPANEL_PADDING)
		                + "px");

		// elements of usedTools
		for (int i = 0; i < toolTree.getItemCount(); i++) {
			final TreeItem branch = toolTree.getItem(i);
			((DraggableTool) (branch.getUserObject()))
			        .setWidth((w - DRAGABLE_TOOLS_PADDING) + "px");
			for (int j = 0; j < branch.getChildCount(); j++) {
				((DraggableTool) branch.getChild(j).getUserObject())
				        .setWidth((w - DRAGABLE_TOOLS_PADDING - DRAGABLE_TOOLS_CHILD_PADDING)
				                + "px");
			}
		}

		// elements of allTools
		for (int k = 0; k < allToolsPanelContent.getWidgetCount(); k++) {
			allToolsPanelContent.getWidget(k).setWidth(
			        (w - DRAGABLE_TOOLS_PADDING) + "px");
		}

		App.debug("[CUSTOMIZE] onResize");
	}
}