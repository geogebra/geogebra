package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.io.layout.DockPanelData.TabIds;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.html5.gui.FastClickHandler;
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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * header of toolbar
 *
 */
class Header extends FlowPanel implements KeyDownHandler {
	private MenuToggleButton btnMenu;
	private MyToggleButton btnAlgebra;
	private MyToggleButton btnTools;
	private MyToggleButton btnTableView;
	private MyToggleButton btnClose;
	private Image imgClose;
	private Image imgOpen;
	private Image imgMenu;
	private FlowPanel contents;
	private FlowPanel center;
	private FlowPanel rightSide;
	/**
	 * panel containing undo and redo
	 */
	PersistablePanel undoRedoPanel;
	private MyToggleButton btnUndo;
	private MyToggleButton btnRedo;
	private boolean animating = false;
	private boolean lastOrientation;
	/**
	 * height in open state
	 */
	private static final int OPEN_HEIGHT = 56;
	/**
	 * application
	 */
	AppW app;
	/**
	 * Parent tool panel
	 */
	final ToolbarPanel toolbarPanel;
	private static final int PADDING = 12;
	private FocusableWidget focusableMenuButton;

	/**
	 * @param toolbarPanel
	 *            - panel containing the toolbar
	 */
	public Header(ToolbarPanel toolbarPanel) {
		this.app = toolbarPanel.getApp();
		this.toolbarPanel = toolbarPanel;
		contents = new FlowPanel();
		contents.addStyleName("contents");
		add(contents);
		if (app.getAppletParameters().getDataParamShowMenuBar(false)) {
			createMenuButton();
		}
		createRightSide();
		createCenter();
		maybeAddUndoRedoPanel();
		setLabels();
		ClickStartHandler.initDefaults(this, true, true);
		setTabIndexes();
		lastOrientation = app.isPortrait();
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

		int nrOfBtn = 1;
		createAlgebraButton();
		createToolsButton();
		createTableViewButton();

		center = new FlowPanel();
		center.addStyleName("center");
		center.addStyleName("indicatorLeft");

		center.add(btnAlgebra);

		boolean showToolPanel = app.getConfig().showToolsPanel();

		if (showToolPanel) {
			center.add(btnTools);
			nrOfBtn++;
		}
		if (app.getConfig().hasTableView()) {
			center.add(btnTableView);
			nrOfBtn++;
			if (showToolPanel) {
				center.addStyleName("threeTab");
			}
		}
		if (nrOfBtn > 1) {
			Element indicator = DOM.createDiv();
			indicator.addClassName("indicator");
			center.getElement().insertFirst(indicator);
		}
		contents.add(center);
	}

	private void createAlgebraButton() {
		btnAlgebra = new MyToggleButton(new NoDragImage(
				MaterialDesignResources.INSTANCE.toolbar_algebra_graphing(), 24, 24),
				app);
		btnAlgebra.addStyleName("tabButton");
		ClickStartHandler.init(btnAlgebra, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onAlgebraPressed();
			}
		});
		btnAlgebra.addKeyDownHandler(this);
		AriaHelper.hide(btnAlgebra);
	}

	private void createToolsButton() {
		btnTools = new MyToggleButton(
				new NoDragImage(
						MaterialDesignResources.INSTANCE.toolbar_tools(), 24),
				app);

		btnTools.addStyleName("tabButton");
		ClickStartHandler.init(btnTools,
				new ClickStartHandler(false, true) {

					@Override
					public void onClickStart(int x, int y,
							PointerEventType type) {
						onToolsPressed();
					}
				});

		btnTools.addKeyDownHandler(this);
		AriaHelper.hide(btnTools);
	}

	private void createTableViewButton() {
		btnTableView = new MyToggleButton(new NoDragImage(
				MaterialDesignResources.INSTANCE.toolbar_table_view_white(),
				24),
				app);
		btnTableView.addStyleName("tabButton");
		ClickStartHandler.init(btnTableView,
				new ClickStartHandler(false, true) {

					@Override
					public void onClickStart(int x, int y,
							PointerEventType type) {
						onTableViewPressed();
					}
				});

		btnTableView.addKeyDownHandler(this);
		AriaHelper.hide(btnTableView);
	}

	/**
	 * Handler for Algebra button.
	 */
	protected void onAlgebraPressed() {
		if (!isOpen()) {
			toolbarPanel.setFadeTabs(false);
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
		if (!isOpen()) {
			toolbarPanel.setFadeTabs(false);
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
		if (!isOpen()) {
			toolbarPanel.setFadeTabs(false);
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
		if (isOpen()) {
			onClose();
		} else {
			onOpen();
		}
		toolbarPanel.getFrame().showKeyBoard(false, null, true);
	}

	private void onClose() {
		setAnimating(true);
		removeOrientationStyles();
		Widget headerParent = toolbarPanel.header.getParent().getParent()
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

	private void onOpen() {
		removeOrientationStyles();
		TabIds tab = toolbarPanel.getSelectedTabId();
		if (tab == TabIds.ALGEBRA) {
			onAlgebraPressed();
		} else if (tab == TabIds.TABLE) {
			onTableViewPressed();
		} else {
			// tools or null
			onToolsPressed();
		}
		toolbarPanel.open();
		updateStyle();
	}

	private void removeOrientationStyles() {
		Widget headerParent = toolbarPanel.header.getParent().getParent()
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
		setTitle(btnMenu, "Menu");
		setTitle(btnTools, "Tools");
		if (btnTableView != null) {
			setTitle(btnTableView, "Table");
		}
		setTitle(btnAlgebra, app.getConfig().getAVTitle());
		setTitle(btnClose, isOpen() ? "Close" : "Open");
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
		setAltText(btnAlgebra, app.getConfig().getAVTitle());
		setAltText(btnTools, "Tools");
		if (btnTableView != null) {
			setAltText(btnTableView, "Table");
		}
		setAltText(btnUndo, "Undo");
		setAltText(btnRedo, "Redo");
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
		center.removeStyleName("indicatorLeft");
		center.removeStyleName("indicatorCenter");
		center.removeStyleName("indicatorRight");
		btnAlgebra.removeStyleName("selected");
		btnTools.removeStyleName("selected");
		btnTableView.removeStyleName("selected");
		switch (tabId) {
		case ALGEBRA:
			center.addStyleName("indicatorLeft");
			btnAlgebra.addStyleName("selected");
			break;
		case TOOLS:
			center.addStyleName(app.getConfig().hasTableView()
					? "indicatorCenter"
					: "indicatorRight");
			btnTools.addStyleName("selected");
			break;
		case TABLE:
			center.addStyleName("indicatorRight");
			btnTableView.addStyleName("selected");
			break;
		default:
			break;
		}
		toolbarPanel.setSelectedTabId(tabId);
	}

	private void createRightSide() {
		imgClose = new Image();
		imgOpen = new Image();
		imgMenu = new Image();
		updateButtonImages();
		btnClose = new MyToggleButton(app);
		btnClose.addStyleName("flatButton");
		btnClose.addStyleName("close");

		ClickStartHandler.init(btnClose, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onClosePressed();
			}
		});

		btnClose.addKeyDownHandler(this);

		rightSide = new FlowPanel();
		rightSide.add(btnClose);
		rightSide.addStyleName("rightSide");
		contents.add(rightSide);
	}

	private void updateButtonImages() {
		if (app.isPortrait()) {
			setResource(imgOpen,
					MaterialDesignResources.INSTANCE
							.toolbar_open_portrait_white());
			setResource(imgClose,
					MaterialDesignResources.INSTANCE
							.toolbar_close_portrait_white());
			if (!needsHeader()) {
				setResource(imgMenu,
						MaterialDesignResources.INSTANCE.toolbar_menu_black());
			}
		} else {
			setResource(imgOpen,
					MaterialDesignResources.INSTANCE
							.toolbar_open_landscape_white());
			setResource(imgClose,
					MaterialDesignResources.INSTANCE
							.toolbar_close_landscape_white());
			if (!needsHeader()) {
				setResource(imgMenu,
					MaterialDesignResources.INSTANCE.toolbar_menu_white());
			}
		}
		if (needsHeader()) {
			setResource(imgMenu,
					MaterialDesignResources.INSTANCE.toolbar_menu_black());
		}

		imgOpen.setAltText(app.getLocalization().getMenu("Open"));
		imgClose.setAltText(app.getLocalization().getMenu("Close"));
	}

	private static void setResource(Image img, SVGResource svg) {
		if (img != null) {
			img.setResource(new ImageResourcePrototype(
				null, svg.getSafeUri(),
				0, 0, 24, 24, false, false));
		}
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
		boolean external = needsHeader() && GlobalHeader.isInDOM();
		btnMenu.setExternal(external);
		if (external) {
			btnMenu.addToGlobalHeader();
			addShareButton();
		} else {
			toolbarPanel.getFrame().add(btnMenu);
		}
	}

	private boolean needsHeader() {
		return !app.getAppletFrame().shouldHideHeader();
	}

	private void addShareButton() {
		GlobalHeader.INSTANCE.initShareButton(new AsyncOperation<Widget>() {

			@Override
			public void callback(Widget share) {
				if (app.isMenuShowing()) {
					app.toggleMenu();
				}
				FileMenuW.share(app, share);
			}
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
	 * Show buttons (tabs, close) of the header.
	 */
	public void showButtons() {
		if (center != null) {
			center.removeStyleName("hidden");
		}
		rightSide.removeStyleName("hidden");
	}

	/**
	 * Hide buttons (eg. during animation).
	 */
	public void hideButons() {
		if (center != null) {
			center.addStyleName("hidden");
		}
		rightSide.addStyleName("hidden");
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

		updateButtonImages();
		String orientation = app.isPortrait() ? "portrait" : "landscape";
		if (isOpen()) {
			removeCloseStyles();
			addStyleName("header-open-" + orientation);
			btnClose.getUpFace().setImage(imgClose);
			btnClose.setTitle(app.getLocalization().getMenu("Close"));
			if (!app.isPortrait()) {
				clearHeight();
				clearWidth();
			}
		} else {
			removeOpenStyles();
			addStyleName("header-close-" + orientation);
			btnClose.getUpFace().setImage(imgOpen);
			btnClose.setTitle(app.getLocalization().getMenu("Open"));
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
		if (isOpen()) {
			btnMenu.removeStyleName("landscapeMenuBtn");
		} else {
			if (!app.isPortrait()) {
				btnMenu.addStyleName("landscapeMenuBtn");
			} else {
				btnMenu.removeStyleName("landscapeMenuBtn");
			}
		}
		if (app.isPortrait()) {
			btnMenu.addStyleName("portraitMenuBtn");
		} else {
			btnMenu.removeStyleName("portraitMenuBtn");
		}
		btnMenu.getUpFace().setImage(imgMenu);
	}

	/**
	 * update center posiotion by resize
	 */
	void updateCenterSize() {
		int h = 0;
		if (isOpen()) {
			h = OPEN_HEIGHT;
		} else {
			h = getOffsetHeight() - getMenuButtonHeight()
					- btnClose.getOffsetHeight() - 2 * PADDING;
		}

		if (h > 0 && center != null) {
			center.setHeight(h + "px");
		}
	}

	private int getMenuButtonHeight() {
		return btnMenu == null ? 0 : btnMenu.getOffsetHeight();
	}

	/**
	 * handle resize of toolbar
	 */
	public void resize() {
		if (isAnimating()) {
			return;
		}
		updateMenuPosition();
		updateCenterSize();
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
	 * Shrinks header width by dx.
	 *
	 * @param dx
	 *            the step of shinking.
	 */
	public void expandWidth(double dx) {
		getElement().getStyle().setWidth(dx, Unit.PX);
	}

	/**
	 * Resets toolbar.
	 */
	public void reset() {
		resize();
		updateUndoRedoPosition();
		getElement().getStyle().setHeight(OPEN_HEIGHT, Unit.PX);
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
		tabIndex(btnClose, AccessibilityGroup.ALGEBRA_CLOSE);

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
		} else if (source == btnClose) {
			onClosePressed();
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
	 * @param expandFrom
	 *            collapsed width
	 * @param expandTo
	 *            expanded width
	 */
	public void onLandscapeAnimationEnd(double expandFrom, double expandTo) {
		if (!isOpen()) {
			getElement().getStyle().clearWidth();
			setHeight("100%");
			toolbarPanel.updateUndoRedoPosition();
		} else {
			expandWidth(expandTo);
			toolbarPanel.onOpen();
		}
		toolbarPanel.onResize();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateCenterSize();
				showUndoRedoPanel();
				updateUndoRedoPosition();
				showButtons();
				resize();
			}
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
		examInfoBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				new ExamLogAndExitDialog(app, true, null, examInfoBtn).show();
			}
		});
	}
}
