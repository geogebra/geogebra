package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.ModeChangeListener;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.io.layout.DockPanelData.TabIds;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.ViewCounter;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.toolbarpanel.spreadsheet.SpreadsheetTab;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyProbabilityTable;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyValuesTable;
import org.geogebra.web.full.gui.toolbarpanel.tableview.TableTab;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.accessibility.AccessibilityManagerW;
import org.geogebra.web.html5.gui.accessibility.SideBarAccessibilityAdapter;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Float;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.layout.client.Layout;
import org.gwtproject.layout.client.Layout.AnimationCallback;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;

/**
 * @author Laszlo Gal
 */
public class ToolbarPanel extends FlowPanel
		implements ModeChangeListener, SideBarAccessibilityAdapter {

	/** Closed width of header in landscape mode */
	public static final int CLOSED_WIDTH_LANDSCAPE = 72;
	public static final int CLOSED_WIDTH_LANDSCAPE_COMPACT = 56;
	/** Loading width of open header in landscape mode */
	public static final int OPEN_START_WIDTH_LANDSCAPE = 380;
	/** Closed height of header in portrait mode */
	public static final int CLOSED_HEIGHT_PORTRAIT = 56;
	public static final int OPEN_ANIM_TIME = 200;
	public static final int HEADING_HEIGHT = 48;
	/** Header of the panel with buttons and tabs */
	NavigationRail navRail;
	/** Application */
	private final AppWFull app;
	private EventDispatcher eventDispatcher;
	private FlowPanel main;
	private StandardButton moveBtn;
	private Integer lastOpenWidth;
	private AlgebraTab tabAlgebra;
	private final List<ToolbarTab> tabs = new ArrayList<>();
	private @CheckForNull TableTab tabTable;
	private @CheckForNull ToolsTab tabTools;
	private ShowableTab tabContainer;
	private boolean isOpen;
	private final ScheduledCommand deferredOnRes = this::resize;
	private @CheckForNull UndoRedoPanel undoRedoPanel;
	private FlowPanel heading;
	private DockPanelDecorator decorator;
	private final ExamController examController = GlobalScope.examController;

	/**
	 * @param app application
	 */
	public ToolbarPanel(AppW app, DockPanelDecorator decorator) {
		this.app = (AppWFull) app;
		this.decorator = decorator;
		eventDispatcher = app.getEventDispatcher();
		app.getActiveEuclidianView().getEuclidianController()
				.setModeChangeListener(this);
		initGUI();
		doOpen(); // should not be part of initGUI to allow app switching with closed AV
		initClickStartHandler();
		((AccessibilityManagerW) app.getAccessibilityManager())
				.setMenuContainer(this);
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		app.setMoveMode();
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
	 * or inactiveAlgebraDockPanelW
	 */
	public void updateUndoRedoActions() {
		if (undoRedoPanel == null) {
			maybeAddUndoRedoPanel();
		}
		if (undoRedoPanel != null) {
			undoRedoPanel.updateUndoActions();
		}
	}

	/**
	 * update position of undo+redo panel
	 */
	public void updateUndoRedoPosition() {
		DockSplitPaneW dockParent = getDockParent();
		if (dockParent == null) {
			return;
		}
		if (getToolbarDockPanel().isAlone() && undoRedoPanel != null) {
			setUndoPosition(0, getNavigationRailWidth());
			return;
		}
		Widget evPanel = dockParent.getOpposite(getToolbarDockPanel());
		if (evPanel != null && undoRedoPanel != null) {
			double evTop = (evPanel.getAbsoluteTop() - (int) app.getAbsTop())
					/ app.getGeoGebraElement().getScaleY();
			double evLeft = (evPanel.getAbsoluteLeft() - (int) app.getAbsLeft())
					/ app.getGeoGebraElement().getScaleX();
			if ((evLeft <= 0) && !app.isPortrait()) {
				return;
			}
			int move = app.isPortrait() && app.showMenuBar() && !navRail.needsHeader() ? 48 : 0;
			setUndoPosition(evTop, evLeft + move);
		}
	}

	private void setUndoPosition(double top, double left) {
		assert undoRedoPanel != null;
		undoRedoPanel.setVisible(true);
		undoRedoPanel.getElement().getStyle().setTop(top, Unit.PX);
		undoRedoPanel.getElement().getStyle().setLeft(left, Unit.PX);
	}

	/**
	 * show or hide the undo/redo panel
	 * @param show true if show, false otherwise
	 */
	public void showHideUndoRedoPanel(boolean show) {
		if (undoRedoPanel != null) {
			Dom.toggleClass(undoRedoPanel, "hidden", !show);
		}
	}

	private void maybeAddUndoRedoPanel() {
		boolean isAllowed = app.getUndoRedoMode() == UndoRedoMode.GUI
				&& app.getConfig().getVersion() != GeoGebraConstants.Version.SCIENTIFIC
				&& app.getConfig().getVersion() != GeoGebraConstants.Version.PROBABILITY;
		if (isAllowed) {
			addUndoRedoButtons();
		} else {
			removeUndoRedoPanel();
		}
	}

	/**
	 * remove undo/redo from frame
	 */
	public void removeUndoRedoPanel() {
		if (undoRedoPanel != null) {
			undoRedoPanel.removeFromParent();
			undoRedoPanel = null;
		}
	}

	private void addUndoRedoButtons() {
		getFrame().add(getUndoRedoPanel());
		getUndoRedoPanel().setVisible(false);
	}

	private Widget getUndoRedoPanel() {
		if (undoRedoPanel == null) {
			undoRedoPanel = new UndoRedoPanel(app);
		}
		return undoRedoPanel;
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
		tabs.add(tab);
		tab.addStyleName(active ? "tab" : "tab-hidden");
		main.add(tab);
	}

	/**
	 * @return width of one tab.
	 */
	public int getTabWidth() {
		int w = this.getOffsetWidth() - getNavigationRailWidth();
		if (isAnimating() && !app.isPortrait() && lastOpenWidth != null) {
			w = Math.max(lastOpenWidth, this.getOffsetWidth())
					- getNavigationRailWidth();
		}
		return Math.max(w, 0);
	}

	/**
	 * @return the height of one tab
	 */
	public int getTabHeight() {
		return getOffsetHeight() - getNavigationRailHeight() - getHeadingHeight();
	}

	private int getHeadingHeight() {
		return heading == null ? 0 : heading.getOffsetHeight();
	}

	private int getNavigationRailHeight() {
		if (!needsNavRail() || isKeyboardShowing()) {
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
		if (needsNavRail()) {
			add(navRail);
		}
		main = new FlowPanel();
		sinkEvents(Event.ONCLICK);
		main.addStyleName("main");
		tabAlgebra = new AlgebraTab(this);
		tabs.add(tabAlgebra);
		tabContainer = new TabContainer(this);

		addTab(tabAlgebra, true);
		if (isToolsTabExpected()) {
			tabTools = new ToolsTab(this);
			addTab(tabTools, false);
		} else {
			removeTab(TabIds.TOOLS);
		}
		// reset tool even if toolbar not available (needed on app switch)
		app.setMoveMode();

		StickyProbabilityTable table = null;
		DistributionTab tabDist;
		if (app.getConfig().hasDistributionView()) {
			table = new StickyProbabilityTable();
			tabDist = new DistributionTab(this, table);
			addTab(tabDist, false);
		} else {
			removeTab(TabIds.DISTRIBUTION);
		}
		if (isTableTabExpected()) {
			tabTable = new TableTab(this,
					table == null ? new StickyValuesTable(app,
							(TableValuesView) app.getGuiManager().getTableValuesView()) : table);
			addTab(tabTable, false);
		} else {
			removeTab(TabIds.TABLE);
		}
		SpreadsheetTab spreadsheetTab;
		if (app.getConfig().hasSpreadsheetView()) {
			spreadsheetTab = new SpreadsheetTab(this);
			addTab(spreadsheetTab, false);
		} else {
			removeTab(TabIds.SPREADSHEET);
		}
		addMoveBtn();
		heading = new FlowPanel();
		heading.setVisible(getToolbarDockPanel().isAlone());
		createCloseButton();
		heading.setStyleName("toolPanelHeading");
		if (app.getConfig().getVersion() != GeoGebraConstants.Version.SCIENTIFIC) {
			add(heading);
		}
		add(main);
		hideDragger();
		if (examController.isExamActive()) {
			navRail.resetExamStyle();
		}
	}

	private void removeTab(TabIds tabID) {
		tabs.removeIf(tab -> tab.getID() == tabID);
	}

	protected boolean needsNavRail() {
		return app.showToolBar() || app.getConfig().hasDistributionView();
	}

	public DockPanelDecorator getDecorator() {
		return decorator;
	}

	private void createCloseButton() {
		SVGResource icon = app.isPortrait() ? MaterialDesignResources.INSTANCE
				.toolbar_close_portrait_black() : MaterialDesignResources.INSTANCE
				.toolbar_close_landscape_black();
		StandardButton close = new StandardButton(icon, null, 24, 24);
		close.addStyleName("flatButton");
		close.getElement().getStyle().setFloat(Float.RIGHT);
		close.addFastClickHandler(source -> {
			navRail.setAnimating(true);
			showOppositeView();
			resizeTabs();
			app.invokeLater(this::closeAnimation);
		});
		heading.add(close);
	}

	/**
	 * closes toolbar with animation
	 */
	public void closeAnimation() {
		DockSplitPaneW dockParent = getDockParent();
		if (dockParent != null) {
			dockParent.addStyleName("singlePanel");
			int parentOffsetWidth = dockParent.getMaxWidgetSize();
			dockParent.setWidgetSize(getToolbarDockPanel(), parentOffsetWidth - 1);
			double targetSize = 2 * parentOffsetWidth / 3.0;
			setLastOpenWidth((int) targetSize);
			dockParent.forceLayout();
			updateDraggerStyle();
			if (undoRedoPanel != null) {
				undoRedoPanel.addStyleName("withTransition");
			}
			dockParent.setWidgetSize(getToolbarDockPanel(), targetSize);
			dockParent.animate(OPEN_ANIM_TIME, fullscreenClose(dockParent));
		}
	}

	private AnimationCallback fullscreenClose(final DockSplitPaneW parent) {
		return new AnimationCallback() {
			@Override
			public void onAnimationComplete() {
				navRail.setAnimating(false);
				if (undoRedoPanel != null) {
					undoRedoPanel.removeStyleName("withTransition");
				}
				setLastOpenWidth(getOffsetWidth());
				updateUndoRedoPosition();
				heading.setVisible(false);
				parent.forceLayout();
				parent.removeStyleName("singlePanel");
				resetFullscreenButton();
			}

			@Override
			public void onLayout(Layout.Layer layer, double progress) {
				updateUndoRedoPosition();
			}
		};
	}

	private void resetFullscreenButton() {
		ZoomPanel fullscreenBtn = app.getZoomPanel();
		if (fullscreenBtn != null) {
			removeStyleNamesFromFullscreenBtn();
			fullscreenBtn.addStyleName("zoomPanelPosition");
		}
	}

	/**
	 * shows the opposite view
	 */
	public void showOppositeView() {
		animateHeadingHeight(HEADING_HEIGHT, 0);
		int viewId = App.VIEW_EUCLIDIAN;
		if ((Perspective.GRAPHER_3D + "").equals(
				app.getConfig().getForcedPerspective())) {
			viewId = App.VIEW_EUCLIDIAN3D;
		} else if ((Perspective.PROBABILITY + "").equals(
				app.getConfig().getForcedPerspective())) {
			viewId = App.VIEW_PROBABILITY_CALCULATOR;
		}
		DockPanelW opposite =
				app.getGuiManager().getLayout().getDockManager().getPanel(viewId);
		DockSplitPaneW dockParent = getDockParent();
		if (dockParent == null) {
			return;
		}
		if (app.isPortrait()) {
			opposite.setEmbeddedDef("0");
			getToolbarDockPanel().setEmbeddedDef("2");
			dockParent.setComponentOrder(null, getToolbarDockPanel());
		} else {
			opposite.setEmbeddedDef("1");
			getToolbarDockPanel().setEmbeddedDef("3");
			dockParent.setComponentOrder(getToolbarDockPanel(), null);
		}
		app.getGuiManager().setShowView(true, viewId);
	}

	private boolean isToolsTabExpected() {
		return app.getConfig().showToolsPanel() && needsNavRail();
	}

	private boolean isTableTabExpected() {
		return app.getConfig().hasTableView() && needsNavRail();
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
		setHeadingHeight(0);
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
		moveBtn.addStyleName("floatingActionButton");
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
		updateSizes(null, OPEN_ANIM_TIME);
		updateKeyboardVisibility();
		updatePanelVisibility(isOpen);
	}

	public void close(boolean snap) {
		close(snap, OPEN_ANIM_TIME);
	}

	/**
	 * Closes the toolbar.
	 */
	public void close(boolean snap, int time) {
		if (!isOpen) {
			return;
		}
		isOpen = false;
		final Integer finalWidth = snap && !app.isPortrait()
				? (Integer) OPEN_START_WIDTH_LANDSCAPE
				: getPreferredWidth();
		if (getToolbarDockPanel().isAlone()) {
			showOppositeView();
		}
		updateDraggerStyle();
		app.invokeLater(() -> {
			updateSizes(() -> setLastOpenWidth(finalWidth), time);
			updateKeyboardVisibility();
			dispatchEvent(EventType.SIDE_PANEL_CLOSED);
			updatePanelVisibility(isOpen);
		});
	}

	private Integer getPreferredWidth() {
		if (getToolbarDockPanel().isAlone()) {
			if (!app.isPortrait()) {
				double ratio = PerspectiveDecoder.landscapeRatio(app,
						app.getWidth());
				return (int) (app.getWidth() * ratio);
			}
		} else {
			if (getOffsetWidth() > 0) {
				return getOffsetWidth();
			}
		}
		return null;
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

	private void updateSizes(Runnable callback, int time) {
		if (app.isPortrait()) {
			updateHeight();
		} else {
			updateWidth(callback, time);
		}
	}

	private void updateKeyboardVisibility() {
		getFrame().showKeyboardButton((isOpen() && getSelectedTabId() != TabIds.TOOLS)
			|| app.getGuiManager().showView(App.VIEW_PROBABILITY_CALCULATOR));
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
	public void updateWidth(Runnable callback, int time) {
		if (app.isPortrait()) {
			return;
		}
		final ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		final DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockParent != null) {
			final Widget opposite = dockParent.getOpposite(dockPanel);
			if (opposite == null) {
				return;
			}
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
						if (callback != null) {
							callback.run();
						}
					}
				};
			}
			dockParent.animate(time, animCallback);
		}
	}

	private void updateWidthForOpening(ToolbarDockPanelW dockPanel, DockSplitPaneW dockParent) {
		dockParent.setWidgetSize(dockPanel, lastOpenWidth);
	}

	private void updateWidthForClosing(ToolbarDockPanelW dockPanel, DockSplitPaneW dockParent) {
		setLastOpenWidth(getOffsetWidth());
		dockParent.setWidgetMinSize(dockPanel, getNavigationRailWidth());
		dockParent.setWidgetSize(dockPanel, getNavigationRailWidth());
	}

	private void setMinimumSize() {
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockParent != null) {
			dockParent.setWidgetMinSize(dockPanel,
					getNavigationRailWidth());
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
		if (evPanel != null && dockParent.getOrientation() == SwingConstants.VERTICAL_SPLIT) {
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
	 * @return move FAB top if it is covering the snackbar, 0 otherwise
	 */
	public int getMoveTopBelowSnackbar(int snackbarRight) {
		//keep the 8px distance between FAB and snackbar
		if (moveBtn != null && !moveBtn.getStyleName().contains("hideMoveBtn")
				&& moveBtn.getAbsoluteLeft() - 8 <=  snackbarRight) {
			return app.isPortrait() ? 124 : 60;
		}
		return 0;
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

	private void moveFullScreenButtonUpOrDown(String withMoveBtn, String noMoveBtn) {
		if (!app.isPortrait()) {
			ZoomPanel fullscreenBtn = app.getZoomPanel();
			removeStyleNamesFromFullscreenBtn();
			if (app.getMode() != EuclidianConstants.MODE_MOVE
					&& getSelectedTabId() == TabIds.TOOLS) {
				fullscreenBtn.addStyleName(withMoveBtn);
			} else {
				fullscreenBtn.addStyleName(noMoveBtn);
			}
		}
	}

	private void removeStyleNamesFromFullscreenBtn() {
		ZoomPanel fullscreenBtn = app.getZoomPanel();
		fullscreenBtn.removeStyleName("zoomPanelPosition");
		fullscreenBtn.removeStyleName("zoomPanelForFullscreenAVMoveUp");
		fullscreenBtn.removeStyleName("zoomPanelForFullscreenAV");
		fullscreenBtn.removeStyleName("zoomPanelForFullscreenAVMoveUpNoMoveBtn");
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
		return app.getAppletFrame();
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
	public void setLastOpenWidth(Integer value) {
		this.lastOpenWidth = value;
	}

	/**
	 * Opens algebra tab.
	 * @param fade decides if tab should fade during animation.
	 */
	public void openAlgebra(boolean fade) {
		if (!app.getConfig().hasAlgebraView()) {
			// maybe SetPerspective was called through API
			return;
		}
		switchTab(TabIds.ALGEBRA, fade);
		setMoveMode();
		dispatchEvent(EventType.ALGEBRA_PANEL_SELECTED);
	}

	private void switchTab(TabIds tab, boolean fade) {
		app.getToolTipManager().hideTooltip();
		navRail.selectTab(tab);
		openNoResize();
		setFadeTabs(fade);
		app.invokeLater(() -> {
			for (ToolbarTab tabUI : tabs) {
				tabUI.setActive(tabUI.getID() == tab);
			}
			resizeTabs();
		});
		updateMoveButton();
		if (tab != TabIds.TOOLS) {
			resetFullscreenButton();
		}
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
		app.getToolTipManager().hideTooltip();

		switchTab(TabIds.TOOLS, fade);
		dispatchEvent(EventType.TOOLS_PANEL_SELECTED);
	}

	public void openTableView(boolean fade) {
		openTableView(null, fade);
	}

	/**
	 * If table view is active, hide the whole toolbar. If not, open toolbar and focus TV.
	 */
	public void toggleTableView() {
		boolean isScientific = app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC;
		if (isTableOfValuesViewActive() && isScientific) {
			navRail.onAlgebraPressed();
		} else {
			navRail.onTableViewPressed();
		}

		if (!navRail.isOpen()) {
			app.getActiveEuclidianView().requestFocus();
		}
	}

	private boolean isTableOfValuesViewActive() {
		return tabTable != null && getSelectedTabId() == TabIds.TABLE;
	}

	/**
	 * Opens tools tab.
	 * @param geo to ensure to be visible.
	 * @param fade decides if tab should fade during animation.
	 */
	public void openTableView(@Nullable GeoEvaluatable geo, boolean fade) {
		if (!needsNavRail() || !app.getConfig().hasTableView()) {
			openAlgebra(fade);
			return;
		}

		switchTab(TabIds.TABLE, fade);
		setMoveMode();
		if (tabTable != null) {
			tabTable.scrollTo(geo);
		}

		dispatchEvent(EventType.TABLE_PANEL_SELECTED);
	}

	/**
	 * Open distribution tab.
	 * @param fade decides if tab should fade during animation.
	 */
	public void openDistributionView(boolean fade) {
		if (!app.getConfig().hasDistributionView()) {
			return;
		}
		switchTab(TabIds.DISTRIBUTION, fade);
		setMoveMode();
	}

	/**
	 * Open spreadsheet tab.
	 * @param fade decides if tab should fade during animation.
	 */
	public void openSpreadsheetView(boolean fade) {
		if (!app.getConfig().hasSpreadsheetView()) {
			return;
		}
		setMoveMode();
		switchTab(TabIds.SPREADSHEET, fade);
		dispatchEvent(EventType.SPREADSHEET_PANEL_SELECTED);
	}

	/**
	 * Opens the toolbar, sends event through the EventDispatcher.
	 */
	public void open() {
		openNoResize();
		resizeTabs();
	}

	private void openNoResize() {
		if (!isOpen()) {
			doOpen();
			dispatchEvent(EventType.SIDE_PANEL_OPENED);
		}
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
				"calc(100% - " + (getNavigationRailHeight() + getHeadingHeight()) + "px)");
		main.getElement().getStyle().setProperty("width", "calc(100% - "
				+ getNavigationRailWidth() + "px)");

		navRail.setVisible(!app.isPortrait() || !isKeyboardShowing());
		for (ToolbarTab tab: tabs) {
			tab.onResize();
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
		for (ToolbarTab tab: tabs) {
			if (getSelectedTabId() == tab.getID()) {
				return tab.getKeyboardListener();
			}
		}
		return null;
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
	 *
	 */
	public void initInfoBtnAction() {
		navRail.initInfoBtnAction();
	}

	/**
	 * remove exam style
	 */
	public void resetHeaderStyle() {
		navRail.resetExamStyle();
	}

	/**
	 * Called when app changes orientation.
	 */
	public void onOrientationChange(boolean isAlone) {
		navRail.onOrientationChange();
		hideDragger();
		heading.clear();
		createCloseButton();
		updateHeadingStyle(isAlone);
	}

	/**
	 * set labels of gui elements
	 */
	public void setLabels() {
		navRail.setLabels();
		if (undoRedoPanel != null) {
			undoRedoPanel.setLabels();
		}
		if (moveBtn != null) {
			String altText = app.getLocalization()
					.getMenu(EuclidianConstants
							.getModeText(EuclidianConstants.MODE_MOVE))
					+ ". " + app.getToolHelp(EuclidianConstants.MODE_MOVE);
			moveBtn.setTitle(altText);
			moveBtn.setAltText(altText);
		}
		for (ToolbarTab tabUI : tabs) {
			tabUI.setLabels();
		}
	}

	/**
	 * close portrait
	 */
	public void doCloseInPortrait() {
		DockManagerW dm = app.getGuiManager().getLayout()
				.getDockManager();
		dm.closePortrait();
		updatePanelVisibility(false);
	}

	/**
	 * Sets if current tab should animate or not.
	 * @param fade to set.
	 */
	void setFadeTabs(boolean fade) {
		for (ToolbarTab tab: tabs) {
			tab.setFade(fade);
		}
	}

	@Override
	public void focusMenu() {
		navRail.focusMenu();
	}

	@Override
	public boolean focusInput(boolean force, boolean forceFade) {
		if (force) {
			openAlgebra(forceFade);
		}
		return isOpen() && isAlgebraViewActive()
				&& tabAlgebra.focusInput();
	}

	/**
	 * Update toolbar content
	 */
	public void updateContent() {
		if (tabTools != null) {
			tabTools.updateContent();
		}
	}

	/**
	 * @return application
	 */
	public AppWFull getApp() {
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
			return getTab(TabIds.ALGEBRA);
		case App.VIEW_TOOLS:
			return getTab(TabIds.TOOLS);
		case App.VIEW_TABLE:
			return getTab(TabIds.TABLE);
		case App.VIEW_SIDE_PANEL:
			return getTabContainer();
		}
		return null;
	}

	/**
	 * This getter is public for testing only.
	 * @return table of values tab
	 */
	public ToolbarTab getTab(TabIds tabID) {
		for (ToolbarTab tab: tabs) {
			if (tab.getID() == tabID) {
				return tab;
			}
		}
		return null;
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
		if (!needsNavRail() || app.isPortrait()) {
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
	 * Update state depending on whether the opposite panel is visible.
	 * @param alone whether toolbar panel is the only open one
	 */
	public void setAlone(boolean alone) {
		if (heading != null) {
			updateHeadingStyle(alone);
			if (alone) { // not animated (e.g. from setPerspective API)
				setHeadingHeight(HEADING_HEIGHT);
			}
		}
	}

	private void updateHeadingStyle(boolean alone) {
		Element globalHeader = Dom.querySelector(".GeoGebraHeader");
		boolean localShadow = app.isPortrait() && alone;
		if (globalHeader != null) {
			if (localShadow) {
				globalHeader.addClassName("noShadow");
			} else {
				globalHeader.removeClassName("noShadow");
			}
			heading.setStyleName("withShadow", localShadow);
		}
	}

	/**
	 * Hide the view opposite to the toolbar panel
	 */
	public void hideOppositeView() {
		DockSplitPaneW dockParent = getDockParent();
		animateHeadingHeight(0, HEADING_HEIGHT);
		if (dockParent != null) {
			dockParent.addStyleName("singlePanel");
			DockPanelW opposite = (DockPanelW) dockParent.getOpposite(getToolbarDockPanel());
			navRail.setAnimating(true);
			setLastOpenWidth(getOffsetWidth());
			dockParent.setWidgetSize(opposite, 0);
			dockParent.animate(OPEN_ANIM_TIME, new AnimationCallback() {
				@Override
				public void onAnimationComplete() {
					dockParent.removeStyleName("singlePanel");
					app.getGuiManager().setShowView(false, opposite.getViewId());
					navRail.setAnimating(false);
					dockParent.forceLayout();
					if (app.getMode() != EuclidianConstants.MODE_MOVE
							&& getSelectedTabId() == TabIds.TOOLS) {
						moveFullScreenButtonUpOrDown("zoomPanelForFullscreenAV",
								"zoomPanelPosition");
					}
				}

				@Override
				public void onLayout(Layout.Layer layer, double progress) {
					// nothing to do
				}
			});
		}
	}

	public void hideToolbar() {
		navRail.onClosePressed(true);
	}

	public void hideToolbarImmediate() {
		navRail.onClose(true, 0);
	}

	private void animateHeadingHeight(int from, int to) {
		if (!app.isPortrait()) {
			setHeadingHeight(from);
			app.invokeLater(() -> setHeadingHeight(to));
		} else {
			setHeadingHeight(to);
		}
	}

	private void setHeadingHeight(int to) {
		heading.setVisible(to > 0);
		heading.setHeight(to + "px");
	}

	/**
	 * Removes tool tab. Used to avoid updating tool tab during perspective reset if we're
	 * going to rebuild it using custom toolbar from a file.
	 */
	public void removeToolsTab() {
		if (tabTools != null) {
			tabTools.removeFromParent();
		}
		tabTools = null;
	}

	/**
	 * Paint this on canvas
	 * @param context2d context
	 * @param counter decrease after painting is done
	 * @param left distance from left canvas edge
	 * @param top distance from top canvas edge
	 */
	public void paintToCanvas(CanvasRenderingContext2D context2d,
			ViewCounter counter, int left, int top) {
		navRail.paintToCanvas(context2d, left, top);
		// if tool tabs is active, still paint algebra
		ToolbarTab active = getSelectedTabId() == TabIds.TABLE
				? getTab(TabIds.TABLE) : getTab(TabIds.ALGEBRA);
		active.paintToCanvas(context2d, counter, left + 72, top);
	}

	public void setAVIconNonSelect(boolean exam) {
		navRail.setAVIconNonSelect(exam);
	}

	/**
	 * Open function define dialog for table if table is empty
	 */
	public void openTableFunctionDialogIfEmpty() {
		if (tabTable != null) {
			tabTable.openDialogIfEmpty();
		}
	}
}
