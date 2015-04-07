package org.geogebra.web.web.gui;

import java.util.Vector;

import org.geogebra.common.gui.CustomizeToolbarModel;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.LayoutUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.CustomizeToolbarHeaderPanel.CustomizeToolbarListener;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.toolbar.ToolBarW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
	protected static final String PREFIX = "[Customize] ";

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

		/**
		 * @param tool
		 *            The tool at the top of the branch
		 * @param defaultLeaf
		 *            tells if a leaf of the same tool should be created
		 */
		public void addTool(DraggableTool tool, boolean defaultLeaf) {
			TreeItem item = null;

			if (tool.isInTree()) {
				// reordering the tree
				if (tool.isLeaf()) {
					TreeItem srcBranch = tool.treeItem.getParentItem();
					tool.treeItem.remove();

					item = addItem(tool);
					addDefaultLeaf(item, tool);

					checkBranch(srcBranch);

				} else {
					// branch move
					addItem(tool.treeItem);
					// no setup needed for existing branch
					return;
				}

			} else {
				// comes from outside
				item = addItem(tool);
				if (defaultLeaf) {
					addDefaultLeaf(item, tool);
				}
			}

			setupItem(item, tool);
		}

		protected void addDefaultLeaf(TreeItem item, DraggableTool tool) {
			setupItem(item, tool);
			tool.addTool(tool.duplicate());

		}

		public void insertTool(int idxBefore, DraggableTool tool) {
			TreeItem item = null;
			if (tool.isInTree()) {
				// reordering the tree
				if (tool.isLeaf()) {
					TreeItem srcBranch = tool.treeItem.getParentItem();
					tool.treeItem.remove();
					item = insertItem(idxBefore, tool);
					addDefaultLeaf(item, tool);
					checkBranch(srcBranch);
				} else {
					// branch move
					insertItem(idxBefore, tool.treeItem);
					return;
				}

			} else {
				// comes from outside
				item = insertItem(idxBefore, tool);
				addDefaultLeaf(item, tool);
			}

		}

		@Override
		public void insertItem(int before, TreeItem item) {
			super.insertItem(before, item);
			toolbarChanged(toolTree.getToolbarString());
		}

		public int indexOf(TreeItem item) {
			for (int i = 0; i < getItemCount(); i++) {
				if (getItem(i) == item) {
					return i;
				}
			}
			return -1;
		}

		public boolean removeBranchIfEmpty(TreeItem branch) {
			if (branch.getChildCount() == 0) {
				branch.remove();
				return true;
			}
			return false;
		}

		public void checkBranch(TreeItem branch) {
			if (removeBranchIfEmpty(branch)) {
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

		public TreeItem getLastItem() {
			return getItem(getItemCount() - 1);
		}

		public DraggableTool getLastTool() {
			return (DraggableTool) getLastItem().getUserObject();
		}

		public boolean hitLastItem(int y) {
			TreeItem last = getLastItem();
			return (y > last.getAbsoluteTop() + last.getOffsetHeight());
		}

		native void toolbarChanged(String toolbarString) /*-{
			if ($wnd.onGgbToolbarChanged) {
				$wnd.onGgbToolbarChanged(toolbarString);
			}

		}-*/;

		public TreeItem setupItem(final TreeItem item, final DraggableTool tool) {
			item.setUserObject(tool);
			tool.treeItem = item;
			// tool.addStyleName("insertBeforeBorder");
			// tool.addStyleName("insertAfterBorder");

			tool.addDropHandler(new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					event.preventDefault();
					event.stopPropagation();
					if (draggingTool == tool) {
						App.debug("Dropping tool to itself");
						return;
					}
					int idx = indexOf(item);
					App.debug(PREFIX + "drop on item " + idx);
					insertTool(idx, draggingTool);
					tool.removeStyleName("insertAfterBranch");
					tool.removeStyleName("insertBeforeBranch");
				}

			});

			tool.addDragEnterHandler(new DragEnterHandler() {
				@Override
				public void onDragEnter(DragEnterEvent event) {
					event.preventDefault();
					event.stopPropagation();
					App.debug("dragEnter");
					tool.addStyleName("insertBeforeBranch");
				}
			});

			tool.addDragLeaveHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					event.preventDefault();
					event.stopPropagation();
					App.debug("dragLeave");
					tool.removeStyleName("insertAfterBranch");
					tool.removeStyleName("insertBeforeBranch");
				}

			});
			toolbarChanged(toolTree.getToolbarString());
			return item;
		}
	}

	private class DraggableTool extends FlowPanel {

		private Integer mode;
		TreeItem treeItem;
		private Image toolbarImg;
		private FlowPanel btn;
		private HandlerRegistration hrDrop;
		private HandlerRegistration hrDragEnter;
		private HandlerRegistration hrDragOver;
		private HandlerRegistration hrDragLeave;

		public DraggableTool(Integer mode) {
			treeItem = null;

			hrDrop = null;
			hrDragOver = null;
			hrDragLeave = null;

			btn = new FlowPanel();
			addStyleName("customizableToolbarItem");
			btn.addStyleName("toolbar_button");
			setMode(mode);
			int width = usedToolsPanelContent.getOffsetWidth()
			        - DRAGABLE_TOOLS_PADDING;
			if (width > 0) {
				setWidth(width + "px");
			}

			addStyleName("insertBeforeBorder");
			addStyleName("insertAfterBorder");
		}

		public boolean isHit(int x, int y) {
			int left = getAbsoluteLeft();
			int top = getAbsoluteTop();
			return (x >= left && x <= left + getOffsetWidth() && y >= top && y <= top
			        + getOffsetHeight());
		}

		public boolean isInTree() {
			return treeItem != null;
		}

		public DraggableTool duplicate() {
			return new DraggableTool(mode);
		}

		public TreeItem addTool(DraggableTool tool) {
			if (treeItem == null) {
				return null;
			}

			TreeItem item = null;

			if (tool.isInTree()) {
				if (tool.isLeaf()) {
					tool.removeFromTree();
					item = setupItem(treeItem.addItem(tool), tool);
				} else {
					// dropping a branch into another
					for (int i = 0; i < tool.treeItem.getChildCount(); i++) {
						TreeItem leaf = tool.treeItem.getChild(i);
						DraggableTool leafTool = (DraggableTool) leaf
						        .getUserObject();
						setupItem(treeItem.addItem(leafTool), leafTool);
					}
					tool.removeFromTree();
				}

			} else {
				// comes from allTools list
				item = setupItem(treeItem.addItem(tool), tool);

			}

			toolTree.checkBranch(treeItem);

			return item;

		}

		public TreeItem insertTool(int idxBefore, DraggableTool tool) {
			if (treeItem == null) {
				return null;
			}

			// inserts before this item;
			TreeItem item = null;

			if (tool.isInTree()) {
				if (tool.isLeaf()) {
					tool.removeFromTree();
					item = setupItem(treeItem.insertItem(idxBefore, tool), tool);
				} else {
					// dropping a branch into another
					for (int i = 0; i < tool.treeItem.getChildCount(); i++) {
						TreeItem leaf = tool.treeItem.getChild(i);
						DraggableTool leafTool = (DraggableTool) leaf
						        .getUserObject();
						setupItem(treeItem.insertItem(idxBefore + i, leafTool),
						        leafTool);
					}
					tool.removeFromTree();
				}

			} else {
				// comes from allTools list
				item = setupItem(treeItem.insertItem(idxBefore, tool), tool);

			}

			toolTree.checkBranch(treeItem);

			return item;

		}

		private void removeFromTree() {
			if (!isInTree()) {
				return;
			}
			TreeItem branch = treeItem.getParentItem();
			treeItem.remove();
			if (branch != null) {
				toolTree.checkBranch(branch);
			}
		}

		TreeItem setupItem(TreeItem leaf, final DraggableTool tool) {
			leaf.setUserObject(tool);
			tool.treeItem = leaf;

			// TODO: check steffi
			tool.addStyleName("insertBeforeBorder");
			tool.addStyleName("insertAfterBorder");

			tool.removeDragNDrop();

			tool.addDropHandler(new DropHandler() {

				public void onDrop(DropEvent event) {
					if (draggingTool == tool
					        || draggingTool.treeItem == treeItem) {
						App.debug("Dropping tool to itself");
						tool.removeHighligts();
						return;
					}

					if (tool.afterLastLeaf(event.getNativeEvent().getClientY())) {
						App.debug("Adding as last leaf!");
						addTool(draggingTool);
					} else {
						int idx = treeItem.getChildIndex(tool.treeItem);
						insertTool(idx, draggingTool);
					}

					tool.removeHighligts();
				}
			});

			tool.addDragOverHandler(new DragOverHandler() {

				public void onDragOver(DragOverEvent event) {
					if (tool.afterLastLeaf(event.getNativeEvent().getClientY())) {
						tool.addStyleName("insertAfterLeaf");
					} else {
						tool.addStyleName("insertBeforeLeaf");

					}
				}
			});

			tool.addDragLeaveHandler(new DragLeaveHandler() {

				public void onDragLeave(DragLeaveEvent event) {
					tool.removeHighligts();

				}
			});
			return leaf;
		}

		boolean afterLastLeaf(int y) {
			if (treeItem == null) {
				return false;
			}

			TreeItem branch = treeItem.getParentItem();
			if (branch == null) {
				return false;
			}

			return (branch.getChildIndex(treeItem) == branch.getChildCount() - 1 && !isTopHit(y));

		}

		void removeHighligts() {
			removeStyleName("insertBeforeLeaf");
			removeStyleName("insertAfterLeaf");

		}

		private void initDrag() {
			addDomHandler(new DragStartHandler() {

				public void onDragStart(DragStartEvent event) {
					App.debug("!DRAG START!");
					draggingTool = DraggableTool.this;
					event.setData("text", "draggginggg");
					event.getDataTransfer().setDragImage(getElement(), 10, 10);
					event.stopPropagation();

				}
			}, DragStartEvent.getType());
		}

		public boolean isLeaf() {
			return isInTree() && treeItem.getChildCount() == 0;
		}

		public Integer getMode() {
			return mode;
		}

		public void setMode(Integer mode) {
			this.mode = mode;
			clear();
			toolbarImg = new NoDragImage(
			        ((GGWToolBar) app.getToolbar()).getImageURL(mode), 32);
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
			return (y > getAbsoluteTop() && y < getAbsoluteTop()
			        + getOffsetHeight() / 2);
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

		public void addDropHandler(DropHandler handler) {
			hrDrop = addDomHandler(handler, DropEvent.getType());
		}

		public void addDragOverHandler(DragOverHandler handler) {
			hrDragOver = addDomHandler(handler, DragOverEvent.getType());
		}

		public void addDragLeaveHandler(DragLeaveHandler handler) {
			hrDragLeave = addDomHandler(handler, DragLeaveEvent.getType());
		}

		public void addDragEnterHandler(DragEnterHandler handler) {
			hrDragEnter = addDomHandler(handler, DragEnterEvent.getType());
		}

		public void removeDragNDrop() {
			if (hrDrop != null) {
				hrDrop.removeHandler();
			}

			if (hrDragOver != null) {
				hrDragOver.removeHandler();
			}

			if (hrDragLeave != null) {
				hrDragLeave.removeHandler();
			}

			if (hrDragEnter != null) {
				hrDragEnter.removeHandler();
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
	/**
	 * flowPanel in a {@link #spAllTools scrollPanel} which contains all
	 * available tools
	 **/
	FlowPanel allToolsPanelContent;
	/** contains the panel with the tools **/
	ScrollPanel spAllTools;
	private Vector<Integer> usedTools;
	/** all tools **/
	Vector<Integer> allTools;
	/** tree which contains the used tools **/
	ToolTree toolTree;
	/** element for dragging **/
	static DraggableTool draggingTool = null;
	private Button btDefalutToolbar;
	private Button btApply;
	private String oldToolbarString;
	private DockPanelW dockPanel;
	private int toolBarId;

	/**
	 * @param app
	 *            {@link AppW}
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
	 * 
	 * @return FlowPanel
	 */
	private FlowPanel getRightPanel() {
		FlowPanel right = new FlowPanel();
		right.setStyleName("allToolsPanel");

		// caption of right panel
		lblAllTools = new Label(app.getMenu("Tools"));
		lblAllTools.setStyleName("panelTitle");

		// content of right panel
		spAllTools = new ScrollPanel();
		spAllTools.addStyleName("allToolsScrollPanel");
		allToolsPanelContent = new FlowPanel();
		allToolsPanelContent.addStyleName("allToolsPanelContent");
		allToolsPanelContent.addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				if (draggingTool != null) {

					if (draggingTool.getParent() == allToolsPanelContent) {
						return;
					}

					App.debug("Drop " + draggingTool.getTitle());
					if (draggingTool.isLeaf()) {
						App.debug("[DROP] leaf");
						usedToolToAll(draggingTool.getMode());
						draggingTool.removeFromTree();

					} else {
						App.debug("[DROP] branch");
						if (draggingTool.treeItem == null) {
							App.debug("[DROP] dragging.treeItem == null");

						}
						for (int i = 0; i < draggingTool.treeItem
						        .getChildCount(); i++) {
							DraggableTool tool = (DraggableTool) (draggingTool.treeItem
							        .getChild(i).getUserObject());
							App.debug("Dropping branch");
							usedToolToAll(tool.getMode());
						}

						draggingTool.treeItem.remove();

					}
					draggingTool = null;
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
	 * 
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

		// dragging the item under the tree adds it to the end.
		usedToolsPanelContent.addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				boolean emptyTree = toolTree.getItemCount() == 0;
				if (emptyTree
				        || toolTree.hitLastItem(event.getNativeEvent()
				                .getClientY())) {
					toolTree.addTool(draggingTool, true);

					if (!emptyTree) {
						toolTree.getLastTool().removeStyleName(
						        "insertAfterBranch");
					}
				}
			}

		}, DropEvent.getType());

		usedToolsPanelContent.addDomHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				if (toolTree.hitLastItem(event.getNativeEvent().getClientY())) {
					toolTree.getLastTool().addStyleName("insertAfterBranch");
				}
			}
		}, DragOverEvent.getType());

		usedToolsPanelContent.addDomHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				toolTree.getLastTool().removeStyleName("insertAfterBranch");
			}
		}, DragLeaveEvent.getType());

		return left;
	}

	/**
	 * 
	 * @param mode
	 *            int
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
	 * @param toolbarDefinition
	 *            String
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

				toolTree.addTool(tool, false);

				for (int j = 0; j < menu.size(); j++) {
					Integer modeInt = menu.get(j);
					if (modeInt != ToolBar.SEPARATOR) {
						usedTools.add(modeInt);
						tool.addTool(new DraggableTool(modeInt));
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
			String toolbarStr = ((GuiManagerW) app.getGuiManager())
			        .getDefaultToolbarString();
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
		gm.setGeneralToolBarDefinition(toolbarString);
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

	@Override
	public AppW getApp() {
		return app;
	}
}