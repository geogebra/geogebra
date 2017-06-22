package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.algebra.LatexTreeItemController;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Laszlo Gal
 *
 */
public class ToolbarPanel extends FlowPanel {
	private static final int CLOSED_WIDTH_LANDSCAPE = 56;
	private static final int CLOSED_HEIGHT_PORTRAIT = 56;
	static final int OPEN_HEIGHT = 56;
	private static final int HEIGHT_CLOSED = 57;
	private static final int WIDTH_AUTO_CLOSE = 56;
	private static final int HEIGHT_AUTO_CLOSE = 86;

	/** Application */
	App app;

	/**
	 * Tab ids.
	 */
	enum TabIds {
		/** tab one */
		ALGEBRA,

		/** tab two */
		TOOLS
	}

	/** Header of the panel with buttons and tabs */
	Header header;

	private FlowPanel main;
	private Integer lastOpenWidth = null;
	private Integer lastOpenHeight = null;
	private AlgebraTab tabAlgebra = null;
	private ToolsTab tabTools = null;
	private TabIds selectedTab;
	private boolean closedByUser = false;
	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	void setMoveMode() {
		tabTools.setMoveMode();
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
					ToolbarPanel.this.getOffsetHeight() - 56);
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
				addStyleName("matDesign");
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
			super.onResize();
			if (aview != null) {
				aview.resize();
			}
		}

		public void scrollToActiveItem() {
			final RadioTreeItem item = aview.getActiveTreeItem();

			int spH = getOffsetHeight();

			int top = header.getOffsetHeight()
					+ item.getElement().getOffsetTop();

			int relTop = top - savedScrollPosition;

			if (spH < relTop + item.getOffsetHeight()) {

				int pos = top + item.getOffsetHeight() - spH;
				setVerticalScrollPosition(pos);
			} 
		}

		public void saveScrollPosition() {
			savedScrollPosition = getVerticalScrollPosition();
		}


	}

	class ToolsTab extends ToolbarTab {
	
		private Tools toolsPanel;

		public ToolsTab() {
			createContents();
		}

		private void createContents() {
			toolsPanel = new Tools((AppW) ToolbarPanel.this.app);
			add(toolsPanel);
		}
		
		public void updateContent() {
			toolsPanel.removeFromParent();
			toolsPanel = new Tools((AppW) ToolbarPanel.this.app);
			add(toolsPanel);
		}

		/**
		 * Selects MODE_MOVE as mode and changes visual settings accordingly of
		 * this.
		 */
		void setMoveMode() {
			toolsPanel.setMoveMode();
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

	/**
	 * Init gui, don't open any panels
	 */
	private void initGUI() {
		clear();
		addStyleName("toolbar");
		header = new Header(this);
		add(header);
		main = new FlowPanel();
		main.addStyleName("main");
		tabAlgebra = new AlgebraTab();
		tabTools = new ToolsTab();
		main.add(tabAlgebra);
		main.add(tabTools);
		add(main);
	}

	/**
	 * Sets last height and width
	 * 
	 * @param force
	 *            override values even they are not null.
	 */
	void setLastSize(boolean force) {
		if (isPortrait()) {
			if (force || lastOpenHeight == null) {
				lastOpenHeight = app.getActiveEuclidianView().getViewHeight();
			}
		} else {
			if (force || lastOpenWidth == null) {
				lastOpenWidth = getOffsetWidth();
			}
		}

	}
	/**
	 * Opens the toolbar.
	 */
	public void doOpen() {
		if (header.isOpen()) {
			return;
		}
		setClosedByUser(false);
		header.setOpen(true);
		setLastSize(false);

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
		final DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null && getLastOpenWidth() != null) {
			final Widget opposite = dockParent.getOpposite(dockPanel);
			AnimationCallback animCallback = null;
			if (header.isOpen()) {
				dockParent.setWidgetSize(dockPanel,
						getLastOpenWidth().intValue());
				dockParent.removeStyleName("hide-HDragger");
				opposite.removeStyleName("hiddenHDraggerRightPanel");
			} else {
				dockParent.setWidgetMinSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);
				dockParent.setWidgetSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);
				animCallback = new Layout.AnimationCallback() {

					@Override
					public void onLayout(Layer layer, double progress) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationComplete() {
						dockParent.addStyleName("hide-HDragger");
						opposite.addStyleName("hiddenHDraggerRightPanel");
					}
				};

			}
			dockParent.animate(500, animCallback);
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
	public void openAlgebra() {
		header.selectAlgebra();
		open();
		main.addStyleName("algebra");
		main.removeStyleName("tools");

	}

	/**
	 * Opens tools tab.
	 */
	public void openTools() {

		header.selectTools();

		open();
		main.removeStyleName("algebra");
		main.addStyleName("tools");

	}

	public ToolsTab getTabTools() {
		return tabTools;
	}

	private void open() {

		if (!isOpen()) {
			doOpen();
		}
		// main.clear();
		resize();
		main.getElement().getStyle().setProperty("height", "calc(100% - 56px)");
	}

	/**
	 * Resize tabs.
	 */
	public void resize() {
		header.resize();
		main.setWidth(getOffsetWidth() * 2 + "px");
		if (tabAlgebra != null) {
			tabAlgebra.onResize();
		}

		if (tabTools != null) {
			tabTools.onResize();
		}

		if (isPortrait()) {
			int h = getOffsetHeight();
			if (h > HEIGHT_CLOSED) {
				if (h < HEIGHT_AUTO_CLOSE) {
					close();
				} else {
					doOpen();
				}
			}
		} else {
			if (getOffsetWidth() < WIDTH_AUTO_CLOSE) {
				close();
			} else if (!isClosedByUser()) {
				doOpen();
			}
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

	/**
	 * 
	 * @return last opened height in portrait mode.
	 */
	Integer getLastOpenHeight() {
		return lastOpenHeight;
	}

	/**
	 * Sets the last opened height in portrait mode.
	 * 
	 * @param value
	 *            to set.
	 */
	void setLastOpenHeight(Integer value) {
		this.lastOpenHeight = value;
	}

	/**
	 * 
	 * @return true if AV is selected and ready to use.
	 */
	public boolean isAlgebraViewActive() {
		return tabAlgebra != null && selectedTab == TabIds.ALGEBRA;
	}

	/**
	 * Scrolls to currently edited item, if AV is active.
	 */
	public void scrollToActiveItem() {
		if (isAlgebraViewActive()) {
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

	/**
	 * Saves the scroll position of algebra view
	 */
	public void saveAVScrollPosition() {
		tabAlgebra.saveScrollPosition();
	}

	/**
	 * Scrolls to the bottom of AV.
	 */
	public void scrollAVToBottom() {
		if (tabAlgebra != null) {
			tabAlgebra.scrollToBottom();
		}
	}

	/**
	 * @return keyboard listener of AV.
	 * 
	 */
	public MathKeyboardListener getKeyboardListener() {
		if (tabAlgebra == null
				|| app.getInputPosition() != InputPosition.algebraView) {
			return null;
		}
		return ((AlgebraViewW) app.getAlgebraView()).getActiveTreeItem();
	}

	/**
	 * @param ml
	 *            to update.
	 * @return the updated listener.
	 */
	public MathKeyboardListener updateKeyboardListener(
			MathKeyboardListener ml) {
		if (tabAlgebra.aview.getInputTreeItem() != ml) {
			return ml;
		}

		// if no retex editor yet
		if (!(ml instanceof RadioTreeItem)) {
			return ml;
		}
		LatexTreeItemController itemController = ((RadioTreeItem) ml)
				.getLatexController();
		itemController.initAndShowKeyboard(false);
		return itemController.getRetexListener();
	}

	/**
	 * 
	 * @return true if toolbar is closed by user with close button, and not by
	 *         code.
	 */
	boolean isClosedByUser() {
		return closedByUser;
	}

	/**
	 * Sets if user closed the toolbar.
	 * 
	 * @param value
	 *            to set
	 */
	void setClosedByUser(boolean value) {
		this.closedByUser = value;
	}

}
