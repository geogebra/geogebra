package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
	private Header header;
	private FlowPanel main;
	private Integer lastOpenWidth = null;
	private Integer lastOpenHeight = null;
	private AlgebraTab tabAlgebra = null;
	private ToolsTab tabTools = null;

	private class Header extends FlowPanel {
		private static final int PADDING = 12;
		private ToggleButton btnMenu;
		private ToggleButton btnAlgebra;
		private ToggleButton btnTools;
		private ToggleButton btnClose;
		private boolean open = true;
		private Image imgClose;
		private Image imgOpen;
		private FlowPanel contents;
		private FlowPanel center;
		public Header() {
			contents = new FlowPanel();
			contents.addStyleName("contents");
			add(contents);


			createMenuButton();
			createCloseButton();
			createCenter();
			if (app.has(Feature.NEW_UNDO_REDO_BUTTONS)) {
				addUndoRedoButtons();
			}

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
		}

		void selectTools() {
			btnAlgebra.removeStyleName("selected");
			btnTools.addStyleName("selected");
		}

		private void createCloseButton() {
			imgClose = new Image();
			imgOpen = new Image();
			updateCloseImages();
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

		private void updateCloseImages() {
			if (isPortrait()) {
				imgOpen.setResource(MaterialDesignResources.INSTANCE
						.toolbar_open_portrait_white());
				imgClose.setResource(MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_white());
			} else {
				imgOpen.setResource(MaterialDesignResources.INSTANCE
						.toolbar_open_landscape_white());
				imgClose.setResource(MaterialDesignResources.INSTANCE
						.toolbar_close_landscape_white());
			}
		}

		private void createMenuButton() {
			btnMenu = new ToggleButton(new Image(MaterialDesignResources.INSTANCE.toolbar_menu_white()));
			btnMenu.addStyleName("flatButton");
			btnMenu.addStyleName("menu");
			contents.add(btnMenu);

			ClickStartHandler.init(btnMenu, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					toggleMenu();
				}
			});
		}

		private void addUndoRedoButtons() {
			FlowPanel undoRedoPanel = new FlowPanel();
			undoRedoPanel.addStyleName("undoRedoPanel");
			addUndoButton(undoRedoPanel);
			addRedoButton(undoRedoPanel);
			addForEV(undoRedoPanel);
		}

		void addForEV(FlowPanel panel) {
			if (((AppW) app).getEuclidianViewpanel() != null) {
				((AppW) app).getEuclidianViewpanel().getAbsolutePanel()
						.add(panel);
			}
		}

		private void addUndoButton(final FlowPanel panel) {
			ToggleButton btnUndo = new ToggleButton(
					new Image(MaterialDesignResources.INSTANCE.undo_black()));
			btnUndo.addStyleName("flatButton");

			ClickStartHandler.init(btnUndo, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					app.getGuiManager().undo();
					addForEV(panel);
				}
			});

			panel.add(btnUndo);
		}

		private void addRedoButton(final FlowPanel panel) {
			ToggleButton btnRedo = new ToggleButton(
					new Image(MaterialDesignResources.INSTANCE.redo_black()));
			btnRedo.addStyleName("flatButton");

			ClickStartHandler.init(btnRedo, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					app.getGuiManager().redo();
					addForEV(panel);
				}
			});

			panel.add(btnRedo);
		}
		
		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean value) {
			this.open = value;
			styleHeader();
			
			if (isPortrait()) {
				updateHeight();
			} else {

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						updateCenterSize();
					}
				});
				updateWidth();

			}
			

			showKeyboardButtonDeferred(isOpen());
		}

		private void styleHeader() {
			removeStyleName("header-open-portrait");
			removeStyleName("header-close-portrait");
			removeStyleName("header-open-landscape");
			removeStyleName("header-close-landscape");
			updateCloseImages();
			String orientation = isPortrait() ? "portrait" : "landscape";
			if (open) {
				addStyleName("header-open-" + orientation);
				btnClose.getUpFace().setImage(imgClose);
			} else {
				addStyleName("header-close-" + orientation);
				btnClose.getUpFace().setImage(imgOpen);
			}
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
			styleHeader();
		}
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

		// TODO
		// private int savedScrollPosition;

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
			if (isPortrait()) {
				return;
			}
			setPixelSize(ToolbarPanel.this.getOffsetWidth(),
					ToolbarPanel.this.getOffsetHeight());

			if (aview != null) {
				aview.resize();
			}
		}

	}

	private class ToolsTab extends ToolbarTab {
	
		public ToolsTab() {
			createContents();
		}

		private void createContents() {
			add(new Label("Here comes the tools..."));
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
	 * Opens the toolbar.
	 */
	public void open() {
		if (header.isOpen()) {
			return;
		}
		header.setOpen(true);
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
			if (header.isOpen()) {
				dockParent.setWidgetSize(dockPanel,
						getLastOpenWidth().intValue());
				dockParent.removeStyleName("hide-HDragger");
			} else {
				dockParent.setWidgetSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);
				dockParent.addStyleName("hide-HDragger");
			}
			dockPanel.deferredOnResize();
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
			Widget d = dockParent.getOpposite(dockPanel);
			if (header.isOpen()) {
				int h = dockPanel.getOffsetHeight();
				dockParent.setWidgetSize(d, getLastOpenHeight());
				dockParent.removeStyleName("hide-VDragger");
			} else {
				int h = dockPanel.getOffsetHeight() - CLOSED_HEIGHT_PORTRAIT;
				if (h > 0) {
					dockParent.setWidgetSize(d, d.getOffsetHeight() + h);
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
}
