package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.io.layout.DockPanelData.TabIds;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.GCustomButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.PersistablePanel;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.GlobalHeader;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Navigation rail or bottom bar
 */
class NavigationRail extends FlowPanel implements KeyDownHandler {
	private MenuToggleButton btnMenu;
	private StandardButton btnAlgebra;
	private StandardButton btnTools;
	private StandardButton btnTableView;
	private final Image imgMenu;
	private final FlowPanel contents;
	private FlowPanel center;
	/**
	 * panel containing undo and redo
	 */
	PersistablePanel undoRedoPanel;
	private MyToggleButton btnUndo;
	private MyToggleButton btnRedo;
	private boolean animating = false;
	private boolean lastOrientation;

	/**
	 * application
	 */
	AppW app;
	/**
	 * Parent tool panel
	 */
	final ToolbarPanel toolbarPanel;
	private FocusableWidget focusableMenuButton;

	/**
	 * @param toolbarPanel
	 *            - panel containing the toolbar
	 */
	public NavigationRail(ToolbarPanel toolbarPanel) {
		this.app = toolbarPanel.getApp();
		this.toolbarPanel = toolbarPanel;
		contents = new FlowPanel();
		contents.addStyleName("contents");
		add(contents);
		if (app.getAppletParameters().getDataParamShowMenuBar(false)) {
			createMenuButton();
		}
		imgMenu = new NoDragImage(MaterialDesignResources.INSTANCE.toolbar_menu_black(), 24);
		createCenter();
		maybeAddUndoRedoPanel();
		setLabels();
		ClickStartHandler.initDefaults(this, true, true);
		setTabIndexes();
		lastOrientation = app.isPortrait();
		setStyleName("header");
	}

	private boolean maybeAddUndoRedoPanel() {
		boolean isAllowed = app.isUndoRedoEnabled() && app.isUndoRedoPanelAllowed();
		if (isAllowed) {
			addUndoRedoButtons();
		}
		return isAllowed;
	}

	/**
	 * Remove the undo-redo panel from the frame
	 */
	public void removeUndoRedoPanel() {
		if (undoRedoPanel != null) {
			toolbarPanel.getFrame().remove(undoRedoPanel);
		}
	}

	private void createCenter() {
		if (!app.showToolBar() || !app.enableGraphing()) {
			return;
		}

		createAlgebraButton();
		createToolsButton();
		createTableViewButton();

		center = new FlowPanel();
		center.addStyleName("center");

		center.add(btnAlgebra);

		boolean showToolPanel = app.getConfig().showToolsPanel();

		if (showToolPanel) {
			center.add(btnTools);
		}
		if (app.getConfig().hasTableView()) {
			center.add(btnTableView);
			if (showToolPanel) {
				center.addStyleName("threeTab");
			}
		}
		if (btnMenu != null && !isHeaderExternal()) {
			center.addStyleName("withMenu");
		}
		contents.add(center);
	}

	private void createAlgebraButton() {
		btnAlgebra = new StandardButton(
				MaterialDesignResources.INSTANCE.toolbar_algebra_graphing(),
				"Algebra", 24,	app);
		btnAlgebra.addStyleName("tabButton");
		btnAlgebra.addFastClickHandler(source -> onAlgebraPressed());
		btnAlgebra.addKeyDownHandler(this);
		AriaHelper.hide(btnAlgebra);
	}

	private void createToolsButton() {
		btnTools = new StandardButton(
						MaterialDesignResources.INSTANCE.toolbar_tools(),
				"Tools", 24,	app);
		btnTools.addStyleName("tabButton");
		btnTools.addFastClickHandler(source -> onToolsPressed());
		btnTools.addKeyDownHandler(this);
		AriaHelper.hide(btnTools);
	}

	private void createTableViewButton() {
		btnTableView = new StandardButton(
				MaterialDesignResources.INSTANCE.toolbar_table_view_black(),
				"Table",	24,	app);
		btnTableView.addStyleName("tabButton");
		btnTableView.addFastClickHandler(source -> onTableViewPressed());

		btnTableView.addKeyDownHandler(this);
		AriaHelper.hide(btnTableView);
	}

	/**
	 * Handler for Algebra button.
	 */
	protected void onAlgebraPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.ALGEBRA) {
			onClosePressed();
			return;
		}
		toolbarPanel.openAlgebra(isOpen());
		app.setKeyboardNeeded(true);
		toolbarPanel.getFrame().keyBoardNeeded(false, null);
		toolbarPanel.getFrame().showKeyboardButton(true);
	}

	/**
	 * Handler for tools button.
	 */
	protected void onToolsPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.TOOLS) {
			onClosePressed();
			return;
		}
		app.setKeyboardNeeded(false);
		toolbarPanel.getFrame().keyBoardNeeded(false, null);
		toolbarPanel.getFrame().showKeyboardButton(false);
		toolbarPanel.openTools(isOpen());
	}

	/**
	 * Handler for table view button.
	 */
	protected void onTableViewPressed() {
		if (isOpen() && toolbarPanel.getSelectedTabId() == TabIds.TABLE) {
			onClosePressed();
			return;
		}
		app.setKeyboardNeeded(false);
		toolbarPanel.getFrame().keyBoardNeeded(false, null);
		toolbarPanel.getFrame().showKeyboardButton(false);
		toolbarPanel.openTableView(null, isOpen());
	}

	/**
	 * Handler for Close button.
	 */
	protected void onClosePressed() {
		app.hideMenu();
		onClose();
		toolbarPanel.getFrame().showKeyBoard(false, null, true);
	}

	private void onClose() {
		setAnimating(true);
		updateIcons(null, app.isExamStarted());
		removeOrientationStyles();
		Widget headerParent = toolbarPanel.navRail.getParent().getParent()
				.getParent();
		if (app.isPortrait()) {
			headerParent.addStyleName("closePortrait");
		} else {
			headerParent.addStyleName("closeLandscape");
			toolbarPanel.setLastOpenWidth(getOffsetWidth());
		}
		toolbarPanel.setMoveMode();
		toolbarPanel.close();
		app.getAccessibilityManager().focusAnchorOrMenu();
	}

	private void removeOrientationStyles() {
		Widget headerParent = toolbarPanel.navRail.getParent().getParent()
				.getParent();
		headerParent.removeStyleName("closePortrait");
		headerParent.removeStyleName("closeLandscape");
	}

	/**
	 * Handler for Undo button.
	 */
	protected void onUndoPressed() {
		app.closeMenuHideKeyboard();
		app.getGuiManager().undo();
	}

	/**
	 * Handler for Redo button.
	 */
	protected void onRedoPressed() {
		app.closeMenuHideKeyboard();
		app.getAccessibilityManager().setAnchor(focusableMenuButton);
		app.getGuiManager().redo();
		app.getAccessibilityManager().cancelAnchor();
	}

	/**
	 * set labels
	 */
	void setLabels() {
		setTitle(btnUndo, "Undo");
		setTitle(btnRedo, "Redo");

		setAltTexts();
	}

	private void setTitle(Widget btn, String avTitle) {
		if (btn != null) {
			btn.setTitle(app.getLocalization().getMenu(avTitle));
			TestHarness.setAttr(btn, "btn_" + avTitle);
		}
	}

	private void setAltTexts() {
		imgMenu.setAltText(app.getLocalization().getMenu("Menu"));
		setButtonText(btnAlgebra, app.getConfig().getAVTitle());
		setButtonText(btnTools, "Tools");
		setButtonText(btnTableView, "Table");
		setAltText(btnUndo, "Undo");
		setAltText(btnRedo, "Redo");
	}

	private void setButtonText(StandardButton btnTools, String key) {
		if (btnTools != null) {
			btnTools.setText(app.getLocalization().getMenu(key));
		}
	}

	private void setAltText(MyToggleButton btn, String string) {
		if (btn != null) {
			btn.setAltText(app.getLocalization().getMenu(string));
		}
	}

	/**
	 * @param tabId
	 *            tab id
	 */
	void selectTab(TabIds tabId) {
		if (center == null) {
			return;
		}
		updateIcons(tabId, app.isExamStarted());
		toolbarPanel.setSelectedTabId(tabId);
	}

	private void setSelected(StandardButton btn, boolean selected, boolean exam) {
		GColor color = GColor.WHITE;
		if (!exam) {
			color = selected ? app.getVendorSettings().getPrimaryColor() : GColor.BLACK;
		}
		btn.setIcon(((SVGResource) btn.getIcon()).withFill(color.toString()));
		Dom.toggleClass(btn, "selected", selected);
	}

	/**
	 * @param expanded
	 *            whether menu is expanded
	 */
	public void markMenuAsExpanded(boolean expanded) {
		if (btnMenu != null) {
			btnMenu.getElement().setAttribute("aria-expanded",
					String.valueOf(expanded));
			btnMenu.getElement().removeAttribute("aria-pressed");
			Dom.toggleClass(btnMenu, "selected", expanded);
		}
	}

	private void createMenuButton() {
		btnMenu = new MenuToggleButton(app);
		focusableMenuButton = new FocusableWidget(AccessibilityGroup.MENU, null, btnMenu);
		updateMenuPosition();
		markMenuAsExpanded(false);
	}

	private void updateMenuPosition() {
		if (btnMenu == null) {
			return;
		}
		boolean external = isHeaderExternal();
		btnMenu.setExternal(external);
		if (external) {
			btnMenu.addToGlobalHeader();
			addShareButton();
		} else {
			toolbarPanel.getFrame().add(btnMenu);
		}
	}

	private boolean isHeaderExternal() {
		return needsHeader() && GlobalHeader.isInDOM();
	}

	private boolean needsHeader() {
		return !app.getAppletFrame().shouldHideHeader();
	}

	private void addShareButton() {
		GlobalHeader.INSTANCE.initShareButton(share -> {
			if (app.isMenuShowing()) {
				app.toggleMenu();
			}
			FileMenuW.share(app, share);
		});
	}

	private void addUndoRedoButtons() {
		undoRedoPanel = new PersistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
		addUndoButton(undoRedoPanel);
		addRedoButton(undoRedoPanel);
		toolbarPanel.getFrame().add(undoRedoPanel);
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
			int move = app.isPortrait() && app.showMenuBar() && !needsHeader() ? 48 : 0;
			undoRedoPanel.getElement().getStyle().setTop(evTop, Unit.PX);
			undoRedoPanel.getElement().getStyle().setLeft(evLeft + move,
					Unit.PX);
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

	/**
	 * update style of undo+redo buttons
	 */
	public void updateUndoRedoActions() {
		if (undoRedoPanel == null) {
			boolean panelAdded = maybeAddUndoRedoPanel();
			if (!panelAdded) {
				return;
			}
		}
		Dom.toggleClass(btnUndo, "buttonActive", "buttonInactive",
				app.getKernel().undoPossible());

		if (app.getKernel().redoPossible()) {
			btnRedo.removeStyleName("hideButton");
		} else {
			if (!btnRedo.getElement().hasClassName("hideButton")) {
				app.getAccessibilityManager().focusAnchor();
			}
			btnRedo.addStyleName("hideButton");
		}
	}

	private void addUndoButton(final FlowPanel panel) {
		btnUndo = new MyToggleButton(
				new NoDragImage(MaterialDesignResources.INSTANCE.undo_border(),
						24),
				app);
		btnUndo.setTitle(app.getLocalization().getMenu("Undo"));
		btnUndo.addStyleName("flatButton");
		btnUndo.addStyleName("undo");

		ClickStartHandler.init(btnUndo, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onUndoPressed();
			}
		});

		btnUndo.addKeyDownHandler(this);

		panel.add(btnUndo);
	}

	private void addRedoButton(final FlowPanel panel) {
		btnRedo = new MyToggleButton(
				new NoDragImage(MaterialDesignResources.INSTANCE.redo_border(),
						24),
				app);
		btnRedo.setTitle(app.getLocalization().getMenu("Redo"));
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");
		btnRedo.addStyleName("redo");

		ClickStartHandler.init(btnRedo, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onRedoPressed();
			}
		});

		btnRedo.addKeyDownHandler(this);
		panel.add(btnRedo);
	}

	/**
	 * @return - true if toolbar is open
	 */
	public boolean isOpen() {
		return toolbarPanel.isOpen();
	}

	private void removeOpenStyles() {
		removeStyleName("header-open-portrait");
		removeStyleName("header-open-landscape");
	}

	private void removeCloseStyles() {
		removeStyleName("header-close-portrait");
		removeStyleName("header-close-landscape");
	}

	/**
	 * update style of toolbar
	 */
	public void updateStyle() {
		if (isAnimating()) {
			return;
		}

		String orientation = app.isPortrait() ? "portrait" : "landscape";
		Dom.toggleClass(this, "compact",
				app.getAppletFrame().hasCompactNavigationRail());
		if (isOpen()) {
			removeCloseStyles();
			addStyleName("header-open-" + orientation);
			if (!app.isPortrait()) {
				clearHeight();
				clearWidth();
			}
		} else {
			removeOpenStyles();
			addStyleName("header-close-" + orientation);
		}

		updateMenuButtonStyle();

		updateUndoRedoPosition();
		updateUndoRedoActions();
		toolbarPanel.updateStyle();
	}

	private void updateMenuButtonStyle() {
		if (btnMenu == null) {
			return;
		}
		Dom.toggleClass(btnMenu, "portraitMenuBtn",
				"landscapeMenuBtn", app.isPortrait());
		btnMenu.getUpFace().setImage(imgMenu);
	}

	/**
	 * handle resize of toolbar
	 */
	public void resize() {
		if (isAnimating()) {
			return;
		}
		updateMenuPosition();
		updateStyle();
	}

	/**
	 * @return true if animating
	 */
	public boolean isAnimating() {
		return animating;
	}

	/**
	 * @param b
	 *            - set if animating
	 */
	public void setAnimating(boolean b) {
		this.animating = b;
	}

	/**
	 * Resets toolbar.
	 */
	public void reset() {
		resize();
		updateUndoRedoPosition();
	}

	/**
	 * Called when app changes orientation.
	 */
	public void onOrientationChange() {
		if (lastOrientation != app.isPortrait()) {
			removeOpenStyles();
			removeCloseStyles();
		} else if (isOpen()) {
			removeCloseStyles();
		} else {
			removeOpenStyles();
		}
		updateStyle();

		lastOrientation = app.isPortrait();

		if (app.isPortrait()) {
			clearWidth();
			clearHeight();
			updateStyle();
		} else {
			if (!isOpen()) {
				int width = app.getAppletParameters().getDataParamWidth();
				if (app.getAppletParameters().getDataParamFitToScreen()) {
					width = Window.getClientWidth();
				}
				toolbarPanel.setLastOpenWidth((int) (width
						* PerspectiveDecoder.landscapeRatio(app, width)));
			}
		}
	}

	private void clearWidth() {
		getElement().getStyle().clearWidth();
	}

	private void clearHeight() {
		getElement().getStyle().clearHeight();
	}

	/**
	 * Sets tab order for header buttons.
	 */
	public void setTabIndexes() {
		tabIndex(btnMenu, AccessibilityGroup.MENU);
		if (focusableMenuButton != null) {
			focusableMenuButton.attachTo(app);
		}
		tabIndex(btnUndo, AccessibilityGroup.UNDO);
		tabIndex(btnRedo, AccessibilityGroup.REDO);
		setAltTexts();
	}

	private void tabIndex(GCustomButton btn, AccessibilityGroup group) {
		if (btn != null) {
			new FocusableWidget(group, null, btn).attachTo(app);
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int key = event.getNativeKeyCode();
		if (key != GWTKeycodes.KEY_ENTER && key != GWTKeycodes.KEY_SPACE) {
			return;
		}
		Object source = event.getSource();
		if (source == null) {
			return;
		}
		if (source == btnAlgebra) {
			onAlgebraPressed();
		} else if (source == btnTools) {
			onToolsPressed();
		} else if (source == btnUndo) {
			onUndoPressed();
		} else if (source == btnRedo) {
			onRedoPressed();
		}
	}

	/** Sets focus to Burger menu */
	public void focusMenu() {
		if (btnMenu != null) {
			btnMenu.getElement().focus();
		}
	}

	/**
	 * After visibility changed in landscape
	 */
	public void onLandscapeAnimationEnd() {
		if (!isOpen()) {
			getElement().getStyle().clearWidth();
			toolbarPanel.updateUndoRedoPosition();
		} else {
			toolbarPanel.onOpen();
		}
		toolbarPanel.onResize();

		Scheduler.get().scheduleDeferred(() -> {
			showUndoRedoPanel();
			updateUndoRedoPosition();
			resize();
		});
	}

	/**
	 * Exam info button.
	 */
	public void initInfoBtnAction() {
		final StandardButton examInfoBtn = GlobalHeader.INSTANCE
				.getExamInfoBtn();
		if (examInfoBtn == null) {
			return;
		}
		examInfoBtn.addFastClickHandler(source -> showExamDialog(examInfoBtn));
	}

	private void showExamDialog(StandardButton examInfoBtn) {
		new ExamLogAndExitDialog(app, true, null, examInfoBtn).show();
	}

	public void updateIcons(boolean exam) {
		updateIcons(toolbarPanel.getSelectedTabId(), exam);
	}

	private void updateIcons(TabIds tabId, boolean exam) {
		setSelected(btnAlgebra, tabId == TabIds.ALGEBRA, exam);
		setSelected(btnTools, tabId == TabIds.TOOLS, exam);
		setSelected(btnTableView, tabId == TabIds.TABLE, exam);
	}
}
