package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.Presistable;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Laszlo Gal
 *
 */
public class ToolbarPanel extends FlowPanel {
	private static final int CLOSED_WIDTH_LANDSCAPE = 56;
	private static final int CLOSED_HEIGHT_PORTRAIT = 56;

	/** Application */
	App app;

	enum TabIds {
		ALGEBRA, TOOLS
	};
	private Header header;
	private FlowPanel main;
	private Integer lastOpenWidth = null;
	private Integer lastOpenHeight = null;
	private AlgebraTab tabAlgebra = null;
	private ToolsTab tabTools = null;
	private TabIds selectedTab;
	private class Header extends FlowPanel {

		private static final int PADDING = 12;

		private class PresistableToggleButton extends ToggleButton
				implements Presistable {

			public PresistableToggleButton(Image image) {
				super(image);
			}

		}

		private class PresistablePanel extends FlowPanel
				implements Presistable {

			public PresistablePanel() {
				super();
			}
		}

		private PresistableToggleButton btnMenu;
		private ToggleButton btnAlgebra;
		private ToggleButton btnTools;
		private ToggleButton btnClose;
		private boolean open = true;
		private Image imgClose;
		private Image imgOpen;

		private Image imgMenu;
		private FlowPanel contents;
		private FlowPanel center;
		PresistablePanel undoRedoPanel;
		private ToggleButton btnUndo;
		private ToggleButton btnRedo;
		public Header() {
			contents = new FlowPanel();
			contents.addStyleName("contents");
			add(contents);

			createMenuButton();
			createCloseButton();
			createCenter();
			addUndoRedoButtons();

		}

		private void createCenter() {
			btnAlgebra = new ToggleButton(new Image(
					MaterialDesignResources.INSTANCE.toolbar_algebra()));
			btnAlgebra.addStyleName("flatButton");

			ClickStartHandler.init(btnAlgebra, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					openAlgebra();
				}
			});

			btnTools = new ToggleButton(new Image(
					MaterialDesignResources.INSTANCE.toolbar_tools()));
			btnTools.addStyleName("flatButton");
			ClickStartHandler.init(btnTools, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					openTools();
				}
			});

			center = new FlowPanel();
			center.addStyleName("center");
			center.add(btnAlgebra);
			center.add(btnTools);
			contents.add(center);

		}

		void selectAlgebra() {
			btnAlgebra.addStyleName("selected");
			btnTools.removeStyleName("selected");
			setSelectedTab(TabIds.ALGEBRA);
		}

		void selectTools() {
			btnAlgebra.removeStyleName("selected");
			btnTools.addStyleName("selected");
			setSelectedTab(TabIds.TOOLS);
		}

		private void createCloseButton() {
			imgClose = new Image();
			imgOpen = new Image();
			imgMenu = new Image();
			updateButtonImages();
			btnClose = new ToggleButton();
			btnClose.addStyleName("flatButton");
			btnClose.addStyleName("close");
			contents.add(btnClose);

			ClickStartHandler.init(btnClose, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					if (isOpen()) {
						if (isPortrait()) {
							setLastOpenHeight(
									app.getActiveEuclidianView().getHeight());
						} else {
							setLastOpenWidth(getOffsetWidth());
						}
					}

					setOpen(!isOpen());
					getFrame().showKeyBoard(false, null, true);

				}
			});
		}

		private void updateButtonImages() {
			if (isPortrait()) {
				imgOpen.setResource(MaterialDesignResources.INSTANCE
						.toolbar_open_portrait_white());
				imgClose.setResource(MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_white());
				imgMenu.setResource(
						MaterialDesignResources.INSTANCE.toolbar_menu_black());
			} else {
				imgOpen.setResource(MaterialDesignResources.INSTANCE
						.toolbar_open_landscape_white());
				imgClose.setResource(MaterialDesignResources.INSTANCE
						.toolbar_close_landscape_white());
				imgMenu.setResource(
						MaterialDesignResources.INSTANCE.toolbar_menu_white());
			}
		}

		private void createMenuButton() {
			btnMenu = new PresistableToggleButton(new Image(
					MaterialDesignResources.INSTANCE.toolbar_menu_black()));
			btnMenu.addStyleName("flatButton");
			btnMenu.addStyleName("menu");

			getFrame().add(btnMenu);

			ClickStartHandler.init(btnMenu, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					toggleMenu();
				}
			});
		}

		private void addUndoRedoButtons() {
			undoRedoPanel = new PresistablePanel();
			undoRedoPanel.addStyleName("undoRedoPanel");
			addUndoButton(undoRedoPanel);
			addRedoButton(undoRedoPanel);
			getFrame().add(undoRedoPanel);
		}

		public void updateUndoRedoPosition() {
			final EuclidianView ev = ((AppW) app).getEuclidianView1();
			if (ev != null) {
				int evTop = ev.getAbsoluteTop();
				int evLeft = ev.getAbsoluteLeft();
				if ((evLeft == 0) && !isPortrait()) {
					return;
				}
				int move = isPortrait() ? 48 : 0;
				undoRedoPanel.getElement().getStyle().setTop(evTop, Unit.PX);
				undoRedoPanel.getElement().getStyle().setLeft(evLeft + move,
						Unit.PX);
			}
		}

		public void updateUndoRedoActions() {
			if (app.getKernel().undoPossible()) {
				btnUndo.addStyleName("buttonActive");
				btnUndo.removeStyleName("buttonInactive");
			} else {
				btnUndo.removeStyleName("buttonActive");
				btnUndo.addStyleName("buttonInactive");
			}

			if (app.getKernel().redoPossible()) {
				btnRedo.removeStyleName("hideButton");
			} else {
				btnRedo.addStyleName("hideButton");
			}
		}

		private void addUndoButton(final FlowPanel panel) {
			btnUndo = new ToggleButton(
					new Image(MaterialDesignResources.INSTANCE.undo_black()));
			btnUndo.addStyleName("flatButton");

			ClickStartHandler.init(btnUndo, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					app.getGuiManager().undo();
				}
			});

			panel.add(btnUndo);
		}

		private void addRedoButton(final FlowPanel panel) {
			btnRedo = new ToggleButton(
					new Image(MaterialDesignResources.INSTANCE.redo_black()));
			btnRedo.addStyleName("flatButton");
			btnRedo.addStyleName("buttonActive");

			ClickStartHandler.init(btnRedo, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					app.getGuiManager().redo();
				}
			});

			panel.add(btnRedo);
		}
		
		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean value) {
			this.open = value;
			updateStyle();
			
			if (isPortrait()) {
				updateHeight();
			} else {

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						updateCenterSize();
					}
				});
				updateWidth();

			}
			

			showKeyboardButtonDeferred(isOpen());

		}

		void updateStyle() {
			ToolbarPanel.this.updateStyle();
			removeStyleName("header-open-portrait");
			removeStyleName("header-close-portrait");
			removeStyleName("header-open-landscape");
			removeStyleName("header-close-landscape");
			updateButtonImages();
			String orientation = isPortrait() ? "portrait" : "landscape";
			if (open) {
				addStyleName("header-open-" + orientation);
				btnClose.getUpFace().setImage(imgClose);
			} else {
				addStyleName("header-close-" + orientation);
				btnClose.getUpFace().setImage(imgOpen);
			}

			btnMenu.getUpFace().setImage(imgMenu);
			updateUndoRedoPosition();
			updateUndoRedoActions();

		}

		void updateCenterSize() {
			if (open) {
				return;
			}

			int h = getOffsetHeight() - btnMenu.getOffsetHeight()
					- btnClose.getOffsetHeight() - 2 * PADDING;

			if (h > 0) {
				center.setHeight(h + "px");
			}

		}

		public void resize() {
			updateStyle();
		}
	}

	/**
	 * Updates the style of undo and redo buttons accordingly of they are active
	 * or inactive
	 */
	public void updateUndoRedoActions() {
		header.updateUndoRedoActions();
	}

	/**
	 * Updates the position of undo and redo buttons
	 */
	public void updateUndoRedoPosition() {
		header.updateUndoRedoPosition();
	}

	private class ToolbarTab extends ScrollPanel {
		public ToolbarTab() {
			setSize("100%", "100%");
			setAlwaysShowScrollBars(false);

		}

		@Override
		public void onResize() {
			setPixelSize(ToolbarPanel.this.getOffsetWidth(),
					ToolbarPanel.this.getOffsetHeight());
		}
	}

	private class AlgebraTab extends ToolbarTab {
		SimplePanel simplep;
		AlgebraViewW aview = null;

		private int savedScrollPosition;

		public AlgebraTab() {
			if (app != null) {
				setAlgebraView((AlgebraViewW) app.getAlgebraView());
				aview.setInputPanel();
			}
		}

		public void setAlgebraView(final AlgebraViewW av) {
			if (av != aview) {
				if (aview != null && simplep != null) {
					simplep.remove(aview);
					remove(simplep);
				}

				simplep = new SimplePanel(aview = av);
				add(simplep);
				simplep.addStyleName("algebraSimpleP");
				addStyleName("algebraPanel");
				addDomHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						int bt = simplep.getAbsoluteTop()
								+ simplep.getOffsetHeight();
						if (event.getClientY() > bt) {
							app.getSelectionManager().clearSelectedGeos();
							av.resetItems(true);
						}
					}
				}, ClickEvent.getType());
			}
		}

		@Override
		public void onResize() {
			setPixelSize(ToolbarPanel.this.getOffsetWidth(),
					ToolbarPanel.this.getOffsetHeight());

			if (aview != null) {
				aview.resize();
			}
		}

		public void scrollToActiveItem() {
			final RadioTreeItem item = aview.getActiveTreeItem();

			int spH = getOffsetHeight();

			int top = item.getElement().getOffsetTop();

			int relTop = top - savedScrollPosition;

			if (spH < relTop + item.getOffsetHeight()) {

				int pos = top + item.getOffsetHeight() - spH;
				setVerticalScrollPosition(pos);
			} 
		}
	}

	private class ToolsTab extends ToolbarTab {
	
		public ToolsTab() {
			createContents();
		}

		private void createContents() {
			add(new Tools((AppW) ToolbarPanel.this.app));
		}
	}
	/**
	 * 
	 * @param app
	 *            .
	 */
	public ToolbarPanel(App app) {
		this.app = app;
		initGUI();

		initClickStartHandler();
	}

	private void initClickStartHandler() {
		ClickStartHandler.init(this, new ClickStartHandler() {
			@Override
			public void onClickStart(final int x, final int y,
					PointerEventType type) {

				app.getActiveEuclidianView().getEuclidianController()
						.closePopups(x, y, type);

			}
		});
	}

	private void initGUI() {
		clear();
		addStyleName("toolbar");
		header = new Header();
		add(header);
		main = new FlowPanel();
		add(main);
		openAlgebra();

	}

	/**
	 * Sets last height and width
	 */
	void setLastSize() {
		if (isPortrait()) {
			if (lastOpenHeight == null) {
				lastOpenHeight = app.getActiveEuclidianView().getViewHeight();
			}
		} else {
			if (lastOpenWidth == null) {
				lastOpenWidth = getOffsetWidth();
			}
		}

	}
	/**
	 * Opens the toolbar.
	 */
	public void open() {
		if (header.isOpen()) {
			return;
		}
		header.setOpen(true);
		if (lastOpenWidth == null) {
			setLastSize();
		}

	}

	/**
	 * Closes the toolbar.
	 */
	public void close() {
		if (!header.isOpen()) {
			return;
		}
		
		header.setOpen(false);
	}

	/**
	 * updates panel width according to its state in landscape mode.
	 */
	public void updateWidth() {
		if (isPortrait()) {
			return;
		}

		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null ? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null && getLastOpenWidth() != null) {
			Widget opposite = dockParent.getOpposite(dockPanel);
			if (header.isOpen()) {
				dockParent.setWidgetSize(dockPanel,
						getLastOpenWidth().intValue());
				dockParent.removeStyleName("hide-HDragger");
				opposite.removeStyleName("hiddenHDraggerRightPanel");
			} else {

				dockParent.setWidgetMinSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);
				dockParent.setWidgetSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);
				dockParent.addStyleName("hide-HDragger");
				opposite.addStyleName("hiddenHDraggerRightPanel");
			}
			dockPanel.deferredOnResize();
		}

	}

	private void setMinimumSize() {
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null) {
			dockParent.setWidgetMinSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);

		}

	}

	/**
	 * updates panel height according to its state in portrait mode.
	 */
	public void updateHeight() {
		if (!isPortrait()) {
			return;
		}

		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null && getLastOpenHeight() != null) {
			Widget opposite = dockParent.getOpposite(dockPanel);
			if (header.isOpen()) {
				dockPanel.getOffsetHeight();
				dockParent.setWidgetSize(opposite, getLastOpenHeight());
				dockParent.removeStyleName("hide-VDragger");
			} else {
				int h = dockPanel.getOffsetHeight() - CLOSED_HEIGHT_PORTRAIT
						+ 8;
				if (h > 0) {
					dockParent.setWidgetSize(opposite,
							opposite.getOffsetHeight() + h);
					dockParent.addStyleName("hide-VDragger");

				}

			}
			// dockPanel.deferredOnResize();
		}

	}

	/**
	 * @return algebra dock panel
	 */
	ToolbarDockPanelW getToolbarDockPanel() {
		return (ToolbarDockPanelW) app.getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA);
	}

	/**
	 * @return if toolbar is open or not.
	 */
	public boolean isOpen() {
		return header.isOpen();
	}

	/**
	 * @return the frame with casting.
	 */
	GeoGebraFrameBoth getFrame() {
		return ((GeoGebraFrameBoth) ((AppWFull) app).getAppletFrame());
	}

	/**
	 * @param b
	 *            To show or hide keyboard button.
	 */
	void showKeyboardButtonDeferred(final boolean b) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getFrame().showKeyboardButton(b);

			}
		});
	}

	/**
	 * 
	 * @return the last width when toolbar was open.
	 */
	Integer getLastOpenWidth() {
		return lastOpenWidth;
	}

	/**
	 * 
	 * @param value
	 *            to set.
	 */
	void setLastOpenWidth(Integer value) {
		this.lastOpenWidth = value;
	}

	/**
	 * Opens and closes Burger Menu
	 */
	void toggleMenu() {
		((AppW) app).toggleMenu();
	}

	/**
	 * Opens algebra tab.
	 */
	void openAlgebra() {
		if (tabAlgebra == null) {
			tabAlgebra = new AlgebraTab();
		}
		header.selectAlgebra();
		open(tabAlgebra);
	}

	/**
	 * Opens tools tab.
	 */
	void openTools() {
		if (tabTools == null) {
			tabTools = new ToolsTab();
		}

		header.selectTools();

		open(tabTools);
	}

	private void open(ToolbarTab tab) {

		if (!isOpen()) {
			open();
		}
		main.clear();
		main.add(tab);
		tab.onResize();
	}

	/**
	 * Resize tabs.
	 */
	public void resize() {
		header.resize();
		if (tabAlgebra != null) {
			tabAlgebra.onResize();
		}

		if (tabTools != null) {
			tabTools.onResize();
		}


	}

	/**
	 * Shows/hides full toolbar.
	 */
	void updateStyle() {
		setMinimumSize();
		if (header.isOpen()) {
			main.removeStyleName("hidden");
		} else {
			main.addStyleName("hidden");

		}
	}

	/**
	 * 
	 * @return if app is in portrait mode.
	 */
	public boolean isPortrait() {
		return ((DockManagerW) (app.getGuiManager().getLayout()
				.getDockManager())).isPortrait();
	}

	Integer getLastOpenHeight() {
		return lastOpenHeight;
	}

	void setLastOpenHeight(Integer lastOpenHeight) {
		this.lastOpenHeight = lastOpenHeight;
	}

	/**
	 * Scrolls to currently edited item, if AV is active.
	 */
	public void scrollToActiveItem() {
		if (tabAlgebra != null && selectedTab == TabIds.ALGEBRA) {
			tabAlgebra.scrollToActiveItem();
		}
	}

	/**
	 * 
	 * @return the selected tab id.
	 */
	public TabIds getSelectedTab() {
		return selectedTab;
	}

	/**
	 * 
	 * @param selectedTab
	 *            to set.
	 */
	public void setSelectedTab(TabIds selectedTab) {
		this.selectedTab = selectedTab;
	}

	/**
	 * 
	 * @return The height that AV should have minimally in portrait mode.
	 */
	public double getMinVHeight() {
		return 3 * header.getOffsetHeight();
				
	}
}
