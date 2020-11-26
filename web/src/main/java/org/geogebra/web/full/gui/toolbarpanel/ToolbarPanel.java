package org.geogebra.web.full.gui.toolbarpanel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MyModeChangedListener;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.layout.DockPanelData.TabIds;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.toolbarpanel.tableview.TableTab;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.accessibility.AccessibilityManagerW;
import org.geogebra.web.html5.gui.accessibility.SideBarAccessibilityAdapter;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.EventListener;

/**
 * @author Laszlo Gal
 */
public class ToolbarPanel extends FlowPanel
		implements MyModeChangedListener, SideBarAccessibilityAdapter {

	/** Closed width of header in landscape mode */
	public static final int CLOSED_WIDTH_LANDSCAPE = 72;
	public static final int CLOSED_WIDTH_LANDSCAPE_COMPACT = 56;
	/** Min width of open header in landscape mode */
	public static final int OPEN_MIN_WIDTH_LANDSCAPE = 160;
	/** Closed height of header in portrait mode */
	public static final int CLOSED_HEIGHT_PORTRAIT = 56;
	private static final int HDRAGGER_WIDTH = 8;
	private static final int OPEN_ANIM_TIME = 200;
	/** Header of the panel with buttons and tabs */
	NavigationRail navRail;
	/** Application */
	private final AppW app;
	private EventDispatcher eventDispatcher;
	private FlowPanel main;
	private StandardButton moveBtn;
	private Integer lastOpenWidth;
	private AlgebraTab tabAlgebra;
	private @CheckForNull TableTab tabTable;
	private @CheckForNull ToolsTab tabTools;
	private ShowableTab tabContainer;
	private boolean isOpen;
	private final ScheduledCommand deferredOnRes = this::resize;
	private UndoRedoPanel undoRedoPanel;

	/**
	 * @param app application
	 */
	public ToolbarPanel(AppW app) {
		this.app = app;
		eventDispatcher = app.getEventDispatcher();
		app.getActiveEuclidianView().getEuclidianController()
				.setModeChangeListener(this);
		initGUI();
		initClickStartHandler();
		((AccessibilityManagerW) app.getAccessibilityManager())
				.setMenuContainer(this);
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		if (tabTools != null) {
			tabTools.setMoveMode();
		}
	}

	/**
	 * Changes visual settings of selected mode.
	 * @param mode the mode will be selected
	 */
	public void setMode(int mode) {
		if (tabTools != null) {
			tabTools.setMode(mode);
		}
	}

	/**
	 * Updates the style of undo and redo buttons accordingly of they are active
	 * or inactive
	 */
	public void updateUndoRedoActions() {
		if (undoRedoPanel == null) {
			boolean panelAdded = maybeAddUndoRedoPanel();
			if (!panelAdded) {
				return;
			}
		}
		undoRedoPanel.updateUndoActions();
	}

	/**
	 * update position of undo+redo panel
	 */
	public void updateUndoRedoPosition() {
		final EuclidianView ev = app.getActiveEuclidianView();
		if (ev != null && undoRedoPanel != null) {
			double evTop = (ev.getAbsoluteTop() - (int) app.getAbsTop())
					/ app.getGeoGebraElement().getScaleY();
			double evLeft = (ev.getAbsoluteLeft() - (int) app.getAbsLeft())
					/ app.getGeoGebraElement().getScaleX();
			if ((evLeft <= 0) && !app.isPortrait()) {
				return;
			}
			int move = app.isPortrait() && app.showMenuBar() && !navRail.needsHeader() ? 48 : 0;
			undoRedoPanel.getElement().getStyle().setTop(evTop, Style.Unit.PX);
			undoRedoPanel.getElement().getStyle().setLeft(evLeft + move,
					Style.Unit.PX);
		}
	}

	/**
	 * Show the undo/redo panel.
	 */
	public void showUndoRedoPanel() {
		if (undoRedoPanel != null) {
			undoRedoPanel.removeStyleName("hidden");
		}
	}

	/**
	 * Hide the entire undo/redo panel (eg. during animation).
	 */
	public void hideUndoRedoPanel() {
		if (undoRedoPanel != null) {
			undoRedoPanel.addStyleName("hidden");
		}
	}

	private boolean maybeAddUndoRedoPanel() {
		boolean isAllowed = app.isUndoRedoEnabled() && app.isUndoRedoPanelAllowed();
		if (isAllowed) {
			addUndoRedoButtons();
		}
		return isAllowed;
	}

	private void addUndoRedoButtons() {
		if (undoRedoPanel == null) {
			undoRedoPanel = new UndoRedoPanel(app);
		}
		getFrame().add(undoRedoPanel);
	}

	/**
	 * This setter is for tests only.
	 * @param eventDispatcher event dispatcher
	 */
	void setEventDispatcher(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	void onResize() {
		DockSplitPaneW dockParent = getDockParent();
		if (dockParent != null) {
			dockParent.onResize();
		}
	}

	private void addTab(ToolbarTab tab, boolean active) {
		tab.addStyleName(active ? "tab" : "tab-hidden");
		main.add(tab);
	}

	/**
	 * @return width of one tab.
	 */
	public int getTabWidth() {
		int w = this.getOffsetWidth() - getNavigationRailWidth();
		if (isAnimating() && !app.isPortrait() && lastOpenWidth != null) {
			w = lastOpenWidth - getNavigationRailWidth() - HDRAGGER_WIDTH;
		}
		return Math.max(w, 0);
	}

	/**
	 * @return the height of one tab
	 */
	public int getTabHeight() {
		return getOffsetHeight() - getNavigationRailHeight();
	}

	private int getNavigationRailHeight() {
		if (!app.showToolBar() || isKeyboardShowing()) {
			return 0;
		}
		return app.isPortrait() ? ToolbarPanel.CLOSED_HEIGHT_PORTRAIT : 0;
	}

	private void initClickStartHandler() {
		ClickStartHandler.init(this, new ClickStartHandler() {
			@Override
			public void onClickStart(final int x, final int y,
					PointerEventType type) {
				getApp().getActiveEuclidianView().getEuclidianController()
						.closePopups(x, y, type);
			}
		});
	}

	/**
	 * Init gui, don't open any panels
	 */
	public void initGUI() {
		clear();

		addStyleName("toolbar");
		maybeAddUndoRedoPanel();
		navRail = new NavigationRail(this);
		if (app.showToolBar()) {
			add(navRail);
		}
		main = new FlowPanel();
		sinkEvents(Event.ONCLICK);
		main.addStyleName("main");
		tabAlgebra = new AlgebraTab(this);
		tabContainer = new TabContainer(this);

		addTab(tabAlgebra, true);
		if (isToolsTabExpected()) {
			tabTools = new ToolsTab(this);
			addTab(tabTools, false);
		} else {
			tabTools = null;
		}
		if (isTableTabExpected()) {
			tabTable = new TableTab(this);
			addTab(tabTable, false);
		} else {
			tabTable = null;
		}
		addMoveBtn();
		add(main);
		ClickStartHandler.initDefaults(main, false, true);
		hideDragger();
		doOpen();
	}

	private boolean isToolsTabExpected() {
		return app.getConfig().showToolsPanel() && app.showToolBar();
	}

	private boolean isTableTabExpected() {
		return app.getConfig().hasTableView() && app.showToolBar();
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONCLICK) {
			app.hideMenu();
		}
		super.onBrowserEvent(event);
	}

	/**
	 * @return the height of open toolbar in portrait mode.
	 */
	int getOpenHeightInPortrait() {
		double h = app.getHeight();
		int kh = 0;
		if (app.isUnbundledGraphing() || app.isUnbundled3D()) {
			return (int) (Math
					.round(h * PerspectiveDecoder.portraitRatio(h, true))) + kh;
		}
		return (int) (Math
				.round(h * PerspectiveDecoder.portraitRatio(h, false)));
	}

	/**
	 * resets toolbar
	 */
	public void reset() {
		lastOpenWidth = null;
		hideDragger();
		navRail.reset();
		resizeTabs();
	}

	private void addMoveBtn() {
		moveBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.mode_move(), null, 24);
		AriaHelper.hide(moveBtn);
		String altText = app.getLocalization().getMenu(
				EuclidianConstants.getModeText(EuclidianConstants.MODE_MOVE))
				+ ". " + app.getToolHelp(EuclidianConstants.MODE_MOVE);
		moveBtn.setTitle(altText);
		moveBtn.setAltText(altText);
		moveBtn.setStyleName("moveFloatingBtn");
		if (tabTable != null) {
			moveBtn.addStyleName("moveBtnMiddleTab");
		}
		// moveMoveBtnDown style added for moveBtn to fix the position on tablet
		// too
		moveBtn.addStyleName("moveMoveBtnDown");
		main.add(moveBtn);
		setMoveFloatingButtonVisible(false);
		FastClickHandler moveBtnHandler = source -> moveBtnClicked();
		moveBtn.addFastClickHandler(moveBtnHandler);
	}

	/**
	 * Handler for move floating button
	 */
	protected void moveBtnClicked() {
		setMoveMode();
		if (tabTools != null) {
			tabTools.showTooltip(EuclidianConstants.MODE_MOVE);
		}
	}

	private void hideDragger() {
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		final DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockParent != null) {
			final Widget opposite = dockParent.getOpposite(dockPanel);
			updateDraggerStyle();
			if (opposite != null) {
				Dom.toggleClass(opposite, "hiddenHDraggerRightPanel", dockParent
						.getOrientation() == SwingConstants.HORIZONTAL_SPLIT);
			}
		}
	}

	/**
	 * Opens the toolbar.
	 */
	private void doOpen() {
		isOpen = true;
		updateDraggerStyle();
		updateSizes();
		updateKeyboardVisibility();
		updatePanelVisibility(isOpen);
	}

	/**
	 * Closes the toolbar.
	 */
	public void close() {
		if (!isOpen) {
			return;
		}
		isOpen = false;
		updateDraggerStyle();
		updateSizes();
		updateKeyboardVisibility();
		dispatchEvent(EventType.SIDE_PANEL_CLOSED);
		updatePanelVisibility(isOpen);
	}

	private void updateDraggerStyle() {
		DockSplitPaneW dockParent = getDockParent();
		if (dockParent != null) {
			dockParent.setStyleName("matDragger", isOpen);
			dockParent.setStyleName("moveUpDragger", !isOpen && app.isPortrait());
			dockParent.setStyleName("hideDragger", !isOpen && !app.isPortrait());
		}
	}

	private DockSplitPaneW getDockParent() {
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		return dockPanel != null ? dockPanel.getParentSplitPane() : null;
	}

	private void updateSizes() {
		if (app.isPortrait()) {
			updateHeight();
		} else {
			updateWidth();
		}
	}

	private void updateKeyboardVisibility() {
		getFrame().showKeyboardButton(isOpen() && getSelectedTabId() != TabIds.TOOLS);
	}

	/**
	 * This method is package-private for tests only.
	 * @param type event type
	 */
	void dispatchEvent(EventType type) {
		org.geogebra.common.plugin.Event event = new org.geogebra.common.plugin.Event(type);
		eventDispatcher.dispatchEvent(event);
	}

	/**
	 * updates panel width according to its state in landscape mode.
	 */
	public void updateWidth() {
		if (app.isPortrait()) {
			return;
		}
		final ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		final DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockParent != null) {
			final Widget opposite = dockParent.getOpposite(dockPanel);
			AnimationCallback animCallback = null;
			updateDraggerStyle();
			opposite.addStyleName("hiddenHDraggerRightPanel");
			if (isOpen()) {
				navRail.removeCloseOrientationStyles();
				if (lastOpenWidth != null) {
					updateWidthForOpening(dockPanel, dockParent);
					animCallback = new LandscapeAnimationCallback(navRail);
				}
			} else {
				updateWidthForClosing(dockPanel, dockParent);
				animCallback = new LandscapeAnimationCallback(navRail) {

					@Override
					public void onEnd() {
						super.onEnd();
						dockParent.addStyleName("hide-HDragger");
						opposite.addStyleName("hiddenHDraggerRightPanel");
					}
				};
			}
			dockParent.animate(OPEN_ANIM_TIME, animCallback);
		}
	}

	private void updateWidthForOpening(ToolbarDockPanelW dockPanel, DockSplitPaneW dockParent) {
		dockParent.setWidgetSize(dockPanel, lastOpenWidth);
	}

	private void updateWidthForClosing(ToolbarDockPanelW dockPanel, DockSplitPaneW dockParent) {
		lastOpenWidth = getOffsetWidth();
		dockParent.setWidgetMinSize(dockPanel, getNavigationRailWidth());
		dockParent.setWidgetSize(dockPanel, getNavigationRailWidth());
	}

	private void setMinimumSize() {
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockParent != null) {
			dockParent.setWidgetMinSize(dockPanel,
					isOpen() ? OPEN_MIN_WIDTH_LANDSCAPE
							: getNavigationRailWidth());
		}
	}

	/**
	 * updates panel height according to its state in portrait mode.
	 */
	public void updateHeight() {
		if (!app.isPortrait()) {
			return;
		}
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		final DockSplitPaneW dockParent = dockPanel != null ? dockPanel.getParentSplitPane() : null;
		Widget evPanel = dockParent != null ? dockParent.getOpposite(dockPanel) : null;
		if (evPanel != null) {
			if (isOpen()) {
				updateHeightForOpening(dockParent, evPanel);
			} else {
				updateHeightForClosing(dockParent, evPanel);
			}

			dockParent.animate(OPEN_ANIM_TIME,
					new PortraitAnimationCallback(navRail, app, dockParent));
		}
	}

	private void updateHeightForOpening(DockSplitPaneW dockParent, Widget evPanel) {
		dockParent.setWidgetSize(evPanel, getOpenHeightInPortrait());
		dockParent.removeStyleName("hide-VDragger");
	}

	private void updateHeightForClosing(DockSplitPaneW dockParent, Widget evPanel) {
		dockParent.setWidgetSize(evPanel,
				app.getHeight() - navRail.getOffsetHeight()
						- app.getAppletParameters().getBorderThickness());
		dockParent.addStyleName("hide-VDragger");
	}

	/**
	 * @return algebra dock panel
	 */
	ToolbarDockPanelW getToolbarDockPanel() {
		return (ToolbarDockPanelW) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA);
	}

	/**
	 * @return mode floating action button
	 */
	public StandardButton getMoveBtn() {
		return moveBtn;
	}

	@Override
	public void onModeChange(int mode) {
		updateMoveButton(mode);
	}

	/**
	 * show or hide move btn according to selected tool
	 */
	public void updateMoveButton() {
		updateMoveButton(app.getMode());
	}

	private void updateMoveButton(int mode) {
		setMoveFloatingButtonVisible(mode != EuclidianConstants.MODE_MOVE
			&& getSelectedTabId() == TabIds.TOOLS);
	}

	/**
	 * Hide move floating action button
	 */
	public void setMoveFloatingButtonVisible(boolean visible) {
		if (moveBtn == null) {
			return;
		}
		Dom.toggleClass(moveBtn, "showMoveBtn", "hideMoveBtn", visible);
	}

	/**
	 * @param ttLeft - tooltip left
	 * @param width - width
	 * @param isSmall - is small tooltip
	 * @return true if was moved
	 */
	public boolean moveMoveFloatingButtonUpWithTooltip(int ttLeft, int width,
			boolean isSmall) {
		if (moveBtn != null) {
			int mLeft = moveBtn.getAbsoluteLeft()
					- (int) app.getAbsLeft();
			int mRight = mLeft + 48;
			int ttRight = ttLeft + width;
			if ((ttLeft < mRight && ttRight > mRight)
					|| (ttRight > mLeft && ttLeft < mLeft)) {
				if (isSmall) {
					moveBtn.removeStyleName("moveMoveBtnDownSmall");
					moveBtn.addStyleName("moveMoveBtnUpSmall");
				} else {
					moveBtn.removeStyleName("moveMoveBtnDown");
					moveBtn.addStyleName("moveMoveBtnUp");
				}
				return true; // button was moved
			}
		}
		return false; // button was not moved
	}

	/**
	 * @param isSmall - is small tooltip
	 * @param wasMoved - true if was moved
	 */
	public void moveMoveFloatingButtonDownWithTooltip(boolean isSmall,
			boolean wasMoved) {
		if (moveBtn != null && wasMoved) {

			if (isSmall) {
				moveBtn.addStyleName("moveMoveBtnDownSmall");
				moveBtn.removeStyleName("moveMoveBtnUpSmall");
			} else {
				moveBtn.addStyleName("moveMoveBtnDown");
				moveBtn.removeStyleName("moveMoveBtnUp");
			}
		}
	}

	/**
	 * @return if toolbar is open or not.
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Just for convince.
	 * @return if toolbar is closed or not.
	 */
	public boolean isClosed() {
		return !isOpen;
	}

	/**
	 * @return the frame with casting.
	 */
	GeoGebraFrameFull getFrame() {
		return (((AppWFull) app).getAppletFrame());
	}

	/**
	 * @param expanded whether menu is open
	 */
	public void markMenuAsExpanded(boolean expanded) {
		navRail.markMenuAsExpanded(expanded);
	}

	/**
	 * @param value to set.
	 */
	void setLastOpenWidth(Integer value) {
		this.lastOpenWidth = value;
	}

	/**
	 * Opens algebra tab.
	 * @param fade decides if tab should fade during animation.
	 */
	public void openAlgebra(boolean fade) {
		switchTab(TabIds.ALGEBRA, fade);
		setMoveMode();
		dispatchEvent(EventType.ALGEBRA_PANEL_SELECTED);
	}

	private void switchTab(TabIds tab, boolean fade) {
		ToolTipManagerW.hideAllToolTips();
		navRail.selectTab(tab);
		open();
		setFadeTabs(fade);
		app.invokeLater(() -> {
			tabAlgebra.setActive(tab == TabIds.ALGEBRA);
			if (tabTools != null) {
				tabTools.setActive(tab == TabIds.TOOLS);
			}
			if (tabTable != null) {
				tabTable.setActive(tab == TabIds.TABLE);
			}
		});
		updateMoveButton();
	}

	/**
	 * Opens tools tab.
	 * @param fade decides if tab should fade during animation.
	 */
	public void openTools(boolean fade) {
		if (!app.showToolBar()) {
			openAlgebra(fade);
			return;
		}
		if (tabTools != null) {
			tabTools.setVisible(true);
		}
		ToolTipManagerW.hideAllToolTips();

		switchTab(TabIds.TOOLS, fade);
		dispatchEvent(EventType.TOOLS_PANEL_SELECTED);
	}

	public void openTableView(boolean fade) {
		openTableView(null, fade);
	}

	/**
	 * Opens tools tab.
	 * @param geo to ensure to be visible.
	 * @param fade decides if tab should fade during animation.
	 */
	public void openTableView(@Nullable GeoEvaluatable geo, boolean fade) {
		if (!app.showToolBar() || !app.getConfig().hasTableView()) {
			openAlgebra(fade);
			return;
		}

		switchTab(TabIds.TABLE, fade);
		if (tabTable != null) {
			tabTable.scrollTo(geo);
		}
		dispatchEvent(EventType.TABLE_PANEL_SELECTED);
	}

	/**
	 * This getter is public only for testing.
	 * @return tool tab
	 */
	public @CheckForNull ToolsTab getToolsTab() {
		return tabTools;
	}

	/**
	 * Opens the toolbar, sends event through the EventDispatcher.
	 */
	public void open() {
		if (!isOpen()) {
			doOpen();
			dispatchEvent(EventType.SIDE_PANEL_OPENED);
		}
		onOpen();
	}

	/**
	 * Called after open.
	 */
	protected void onOpen() {
		resizeTabs();
	}

	/**
	 * Resize tabs.
	 */
	public void resize() {
		if (getOffsetWidth() == 0) {
			return;
		}

		navRail.resize();
		resizeTabs();
	}

	/**
	 * Update the size of tab container and all tabs
	 */
	public void resizeTabs() {
		main.getElement().getStyle().setProperty("left",
				getNavigationRailWidth() + "px");
		main.getElement().getStyle().setProperty("height",
				"calc(100% - " + getNavigationRailHeight() + "px)");
		main.getElement().getStyle().setProperty("width", "calc(100% - "
				+ getNavigationRailWidth() + "px)");

		navRail.setVisible(!app.isPortrait() || !isKeyboardShowing());

		if (tabAlgebra != null) {
			tabAlgebra.onResize();
		}

		if (tabTools != null) {
			tabTools.onResize();
		}

		if (tabTable != null) {
			tabTable.onResize();
		}
	}

	private boolean isKeyboardShowing() {
		return app.getAppletFrame().isKeyboardShowing();
	}

	/**
	 * Shows/hides full toolbar.
	 */
	void updateStyle() {
		setMinimumSize();
		if (isOpen()) {
			main.removeStyleName("hidden");
		} else {
			main.addStyleName("hidden");
		}
	}

	/**
	 * @return true if AV is selected and ready to use.
	 */
	public boolean isAlgebraViewActive() {
		return tabAlgebra != null && getSelectedTabId() == TabIds.ALGEBRA;
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
	 * @return the selected tab id.
	 */
	public TabIds getSelectedTabId() {
		return getToolbarDockPanel().getTabId();
	}

	/**
	 * @param tabId to set.
	 */
	public void setSelectedTabId(TabIds tabId) {
		this.getToolbarDockPanel().doSetTabId(tabId);
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
	 */
	public MathKeyboardListener getKeyboardListener() {
		if (tabAlgebra == null
				|| app.getInputPosition() != InputPosition.algebraView) {
			return null;
		}
		return ((AlgebraViewW) app.getAlgebraView()).getActiveTreeItem();
	}

	/**
	 * @param ml to update.
	 * @return the updated listener.
	 */
	public MathKeyboardListener updateKeyboardListener(
			MathKeyboardListener ml) {
		return AlgebraDockPanelW
				.updateKeyboardListenerForView(this.tabAlgebra.aview, ml);
	}

	/**
	 * @return if toolbar is animating or not.
	 */
	public boolean isAnimating() {
		return navRail.isAnimating();
	}

	/**
	 * Resize in a deferred way.
	 */
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	/**
	 * update header style
	 */
	public void updateHeader() {
		navRail.updateStyle();
	}

	/**
	 * @param style style to change color of header (teal -> ok, red -> cheating)
	 */
	public void setHeaderStyle(String style) {
		resetHeaderClasses();
		navRail.addStyleName(style);
		navRail.updateIcons(true);
		ExamUtil.makeRed(navRail.getElement(), "examCheat".equals(style));
	}

	/**
	 *
	 */
	public void initInfoBtnAction() {
		navRail.initInfoBtnAction();
	}

	/**
	 * remove exam style
	 */
	public void resetHeaderStyle() {
		resetHeaderClasses();
		navRail.updateIcons(false);
	}

	private void resetHeaderClasses() {
		ExamUtil.makeRed(navRail.getElement(), false);
		navRail.removeStyleName("examOk");
		navRail.removeStyleName("examCheat");
	}

	/**
	 * Called when app changes orientation.
	 */
	public void onOrientationChange() {
		navRail.onOrientationChange();
		hideDragger();
	}

	/**
	 * set labels of gui elements
	 */
	public void setLabels() {
		navRail.setLabels();
		if (undoRedoPanel != null) {
			undoRedoPanel.setLabels();
		}
		if (tabTools != null && !tabTools.isCustomToolbar) {
			tabTools.toolsPanel.setLabels();
			tabTools.moreBtn
					.setText(app.getLocalization().getMenu("Tools.More"));
			tabTools.lessBtn
					.setText(app.getLocalization().getMenu("Tools.Less"));
		}
		if (moveBtn != null) {
			String altText = app.getLocalization()
					.getMenu(EuclidianConstants
							.getModeText(EuclidianConstants.MODE_MOVE))
					+ ". " + app.getToolHelp(EuclidianConstants.MODE_MOVE);
			moveBtn.setTitle(altText);
			moveBtn.setAltText(altText);
		}
		if (tabTable != null) {
			tabTable.setLabels();
		}
	}

	/**
	 * close portrait
	 */
	public void doCloseInPortrait() {
		DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
				.getDockManager();
		dm.closePortrait();
		updatePanelVisibility(false);
	}

	/**
	 * sets icons tab-able.
	 */
	public void setTabIndexes() {
		navRail.setTabIndexes();
		if (undoRedoPanel != null) {
			undoRedoPanel.setTabIndexes();
		}
	}

	/**
	 * Sets if current tab should animate or not.
	 * @param fade to set.
	 */
	private void setFadeTabs(boolean fade) {
		tabAlgebra.setFade(fade);
		if (tabTools != null) {
			tabTools.setFade(fade);
		}
		if (tabTable != null) {
			tabTable.setFade(fade);
		}
	}

	@Override
	public void focusMenu() {
		navRail.focusMenu();
	}

	@Override
	public boolean focusInput(boolean force) {
		if (force) {
			openAlgebra(true);
		}
		return isOpen() && isAlgebraViewActive()
				&& tabAlgebra.focusInput();
	}

	/**
	 * Update toolbar content
	 */
	public void updateContent() {
		ToolsTab toolsTab = getToolsTab();
		if (toolsTab != null) {
			toolsTab.updateContent();
		}
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * Returns the tab associated with the tabIdentifier
	 * @param tabIdentifier one of the App.VIEW_ int constants
	 * @return the tab identified by the parameter, or null if no related tab is found
	 */
	@Nullable
	public ShowableTab getTab(int tabIdentifier) {
		switch (tabIdentifier) {
		case App.VIEW_ALGEBRA:
			return getAlgebraTab();
		case App.VIEW_TOOLS:
			return getToolsTab();
		case App.VIEW_TABLE:
			return getTableTab();
		case App.VIEW_SIDE_PANEL:
			return getTabContainer();
		}
		return null;
	}

	/**
	 * This getter is public for testing only.
	 * @return algebra tab
	 */
	public AlgebraTab getAlgebraTab() {
		return tabAlgebra;
	}

	/**
	 * This getter is public for testing only.
	 * @return table of values tab
	 */
	public TableTab getTableTab() {
		return tabTable;
	}

	/**
	 * This getter is public for testing only.
	 * @return the representation of the side panel containing all the tabs
	 */
	public ShowableTab getTabContainer() {
		return tabContainer;
	}

	private void updatePanelVisibility(boolean isVisible) {
		app.getGuiManager().onToolbarVisibilityChanged(App.VIEW_ALGEBRA, isVisible);
	}

	/**
	 * @return navigation rail width
	 */
	public int getNavigationRailWidth() {
		if (!app.showToolBar() || app.isPortrait()) {
			return 0;
		}
		return app.getAppletFrame().hasCompactNavigationRail()
				? CLOSED_WIDTH_LANDSCAPE_COMPACT : CLOSED_WIDTH_LANDSCAPE;
	}

	protected void setMenuButton(FocusableWidget focusableMenuButton) {
		if (undoRedoPanel != null) {
			undoRedoPanel.redoAnchor = focusableMenuButton;
		}
	}

	/**
	 * Base class for Toolbar Tabs-
	 * @author Laszlo
	 */
	public abstract static class ToolbarTab extends ScrollPanel implements ShowableTab {
		/** Constructor */
		public ToolbarTab(ToolbarPanel parent) {
			setSize("100%", "100%");
			setAlwaysShowScrollBars(false);

			EventListener onTransitionEnd = evt -> parent.setFadeTabs(false);
			Dom.addEventListener(this.getElement(), "transitionend",
					onTransitionEnd);
		}

		@Override
		public void onResize() {
			setHeight("100%");
		}

		/**
		 * Set tab the active one.
		 * @param active to set.
		 */
		public void setActive(boolean active) {
			Dom.toggleClass(this, "tab", "tab-hidden", active);
			if (active) {
				onActive();
			}
		}

		/**
		 * Sets if tab should fade during animation or not.
		 * @param fade to set.
		 */
		public void setFade(boolean fade) {
			setStyleName("tabFade", fade);
		}

		/**
		 * Called when tab is activated.
		 */
		protected abstract void onActive();
	}
}
