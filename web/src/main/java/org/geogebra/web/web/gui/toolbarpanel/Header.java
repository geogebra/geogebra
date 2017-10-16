package org.geogebra.web.web.gui.toolbarpanel;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.Persistable;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.GUITabs;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel.TabIds;
import org.geogebra.web.web.gui.util.PersistablePanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * header of toolbar
 *
 */
class Header extends FlowPanel implements KeyDownHandler {
	private PersistableToggleButton btnMenu;
	private MyToggleButton btnAlgebra;
	private MyToggleButton btnTools;
	private MyToggleButton btnClose;
	private boolean open = true;
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
	private List<ToggleButton> buttons = null;
	private class PersistableToggleButton extends ToggleButton
			implements Persistable {

		public PersistableToggleButton(Image image) {
			super(image);
		}

		@Override
		public void setTitle(String title) {
			if (app.has(Feature.TOOLTIP_DESIGN) && !Browser.isMobile()) {
				getElement().removeAttribute("title");
				getElement().setAttribute("data-title", title);
			} else {
				super.setTitle(title);
			}
		}

	}

	
	/**
	 * @param toolbarPanel
	 *            - panel containing the toolbar
	 * @param app
	 *            - application
	 */
	public Header(ToolbarPanel toolbarPanel, AppW app) {
		this.app = app;
		this.toolbarPanel = toolbarPanel;
		contents = new FlowPanel();
		contents.addStyleName("contents");
		add(contents);
		if (app.getArticleElement().getDataParamShowMenuBar(false)) {
			createMenuButton();
		}
		createRightSide();
		createCenter();
		addUndoRedoButtons();
		setLabels();
		ClickStartHandler.initDefaults(this, true, true);
		buttons = Arrays.asList(btnMenu, btnAlgebra, btnTools, btnClose,
				btnUndo, btnRedo);
		setTabIndexes();
	}

	private void createCenter() {
		if (!app.isUnbundledGeometry()) {
			btnAlgebra = new MyToggleButton(
					new Image(new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE
							.toolbar_algebra_graphing().getSafeUri(),
					0, 0, 24, 24, false, false)),
					app);
		} else {
			btnAlgebra = new MyToggleButton(
					new Image(new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE
									.toolbar_algebra_geometry().getSafeUri(),
							0, 0, 24, 24, false, false)),
				app);
		}
		btnAlgebra.addStyleName("tabButton");
		ClickStartHandler.init(btnAlgebra, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onAlgebraPressed();
			}
		});

		btnTools = new MyToggleButton(
				new Image(new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE
						.toolbar_tools().getSafeUri(),
				0, 0, 24, 24, false, false)),
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

		if (app.has(Feature.TAB_ON_GUI)) {
			btnAlgebra.addKeyDownHandler(this);
			btnTools.addKeyDownHandler(this);

		}
		center = new FlowPanel();
		center.addStyleName("center");
		center.addStyleName("indicatorLeft");
		center.getElement().setInnerHTML(center.getElement().getInnerHTML()
				+ "<div class=\"indicator\"></div>");
		center.add(btnAlgebra);
		center.add(btnTools);
		contents.add(center);
	}

	/**
	 * Handler for Algebra button.
	 */
	protected void onAlgebraPressed() {
		if (!open) {
			toolbarPanel.setFadeTabs(false);
		}

		toolbarPanel.openAlgebra(open);
		toolbarPanel.setMoveMode();
		app.setKeyboardNeeded(true);
		toolbarPanel.getFrame().keyBoardNeeded(false, null);
		toolbarPanel.getFrame().showKeyboardButton(true);
	}

	/**
	 * Handler for button.
	 */
	protected void onToolsPressed() {
		if (!open) {
			toolbarPanel.setFadeTabs(false);
		}

		app.setKeyboardNeeded(false);
		toolbarPanel.getFrame().keyBoardNeeded(false, null);
		toolbarPanel.getFrame().showKeyboardButton(false);
		toolbarPanel.openTools(open);
	}

	/**
	 * Handler for Close button.
	 */
	protected void onClosePressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}

		if (isOpen()) {
			onClose();
		} else {
			onOpen();
		}

		toolbarPanel.getFrame().showKeyBoard(false, null, true);
	}
	
	private void onClose() {
		setAnimating(true);
		Widget headerParent = toolbarPanel.header.getParent().getParent()
				.getParent();

		if (app.isPortrait()) {
			headerParent.addStyleName("closePortrait");
		} else {
			headerParent.addStyleName("closeLandscape");
			toolbarPanel.setLastOpenWidth(getOffsetWidth());
		}
		toolbarPanel.setMoveMode();
		toolbarPanel.setClosedByUser(true);
		setOpen(false);
	}

	private void onOpen() {
		if (toolbarPanel.isAlgebraViewActive()) {
			onAlgebraPressed();
		} else {
			onToolsPressed();
		}
		updateStyle();
		toolbarPanel.setClosedByUser(false);
	}
		
	/**
	 * Handler for Undo button.
	 */
	protected void onUndoPressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		toolbarPanel.app.getGuiManager().undo();
	}

	/**
	 * Handler for Redo button.
	 */
	protected void onRedoPressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		toolbarPanel.app.getGuiManager().redo();
	}

	/**
	 * set labels
	 */
	void setLabels() {
		setTitle(btnMenu, "Menu");
		setTitle(btnTools,"Tools");
		setTitle(btnAlgebra,app.getConfig().getAVTitle());
		setTitle(btnClose, isOpen() ? "Close" : "Open");
		setTitle(btnUndo, "Undo");
		setTitle(btnRedo, "Redo");

		setAltTexts();

	}

	private void setTitle(Widget btn, String avTitle) {
		if (btn != null) {
			btn.setTitle(app.getLocalization().getMenu(avTitle));
		}

	}

	private void setAltTexts() {
		if (!app.has(Feature.TAB_ON_GUI)) {
			return;
		}

		imgMenu.setAltText(app.getLocalization().getMenu("Menu"));
		btnAlgebra.setAltText(
				app.getLocalization().getMenu(app.getConfig().getAVTitle()));
		btnTools.setAltText(app.getLocalization().getMenu("Tools"));
		btnUndo.setAltText(app.getLocalization().getMenu("Undo"));
		btnRedo.setAltText(app.getLocalization().getMenu("Redo"));
	}

	/**
	 * Switch to algebra panel
	 */
	void selectAlgebra() {
		center.removeStyleName("indicatorRight");
		center.addStyleName("indicatorLeft");
		btnAlgebra.addStyleName("selected");
		btnTools.removeStyleName("selected");
		toolbarPanel.setSelectedTabId(TabIds.ALGEBRA);
	}

	/**
	 * Switch to tools panel
	 */
	void selectTools() {
		center.removeStyleName("indicatorLeft");
		center.addStyleName("indicatorRight");
		btnAlgebra.removeStyleName("selected");
		btnTools.addStyleName("selected");
		toolbarPanel.setSelectedTabId(TabIds.TOOLS);
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
		
		if (app.has(Feature.TAB_ON_GUI)) {
			btnClose.addKeyDownHandler(this);
		}

		rightSide = new FlowPanel();
		rightSide.add(btnClose);
		rightSide.addStyleName("rightSide");
		contents.add(rightSide);
	}

	private void updateButtonImages() {
		if (app.isPortrait()) {
			imgOpen.setResource(new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE
							.toolbar_open_portrait_white().getSafeUri(),
					0, 0, 24, 24, false, false));
			imgClose.setResource(new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE
							.toolbar_close_portrait_white().getSafeUri(),
					0, 0, 24, 24, false, false));
			imgMenu.setResource(new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE.menu_black_border()
					.getSafeUri(),
					0, 0, 24, 24, false, false));
		} else {
			imgOpen.setResource(new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE
							.toolbar_open_landscape_white().getSafeUri(),
					0, 0, 24, 24, false, false));
			imgClose.setResource(new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE
							.toolbar_close_landscape_white().getSafeUri(),
					0, 0, 24, 24, false, false));
			ImageResource menuImgRec = new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE.toolbar_menu_white()
							.getSafeUri(),
					0, 0, 24, 24, false, false);
			imgMenu.setResource(
					menuImgRec);
		}

		imgOpen.setAltText(app.getLocalization().getMenu("Open"));
		imgClose.setAltText(app.getLocalization().getMenu("Close"));
	}

	private void createMenuButton() {
		ImageResource menuImgRec = new ImageResourcePrototype(null,
				MaterialDesignResources.INSTANCE.toolbar_menu_black()
						.getSafeUri(),
				0, 0, 24, 24, false, false);
		btnMenu = new PersistableToggleButton(new Image(menuImgRec));
		btnMenu.addStyleName("flatButton");
		btnMenu.addStyleName("menu");

		toolbarPanel.getFrame().add(btnMenu);

		ClickStartHandler.init(btnMenu, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toolbarPanel.toggleMenu();
			}
		});

		if (app.has(Feature.TAB_ON_GUI)) {
			btnMenu.addKeyDownHandler(this);
		}
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
		final EuclidianView ev = ((AppW) toolbarPanel.app)
				.getActiveEuclidianView();
		if (ev != null) {
			int evTop = ev.getAbsoluteTop() - (int) app.getAbsTop();
			int evLeft = ev.getAbsoluteLeft() - (int) app.getAbsLeft();
			if ((evLeft <= 0) && !app.isPortrait()) {
				return;
			}
			int move = app.isPortrait() ? 48 : 0;
			undoRedoPanel.getElement().getStyle().setTop(evTop, Unit.PX);
			undoRedoPanel.getElement().getStyle().setLeft(evLeft + move,
					Unit.PX);
		}
	}

	/**
	 * Show the undo/redo panel.
	 */
	public void showUndoRedoPanel() {
		undoRedoPanel.removeStyleName("hidden");
	}


	/**
	 * Hide the entire undo/redo panel (eg. during animation).
	 */
	public void hideUndoRedoPanel() {
		undoRedoPanel.addStyleName("hidden");
	}

	/**
	 * Show center panel.
	 */
	public void showCenter() {
		center.removeStyleName("hidden");
	}

	/**
	 * Hide center buttons (eg. during animation).
	 */
	public void hideCenter() {
		center.addStyleName("hidden");
	}

	/**
	 * update style of undo+redo buttons
	 */
	public void updateUndoRedoActions() {
		if (toolbarPanel.app.getKernel().undoPossible()) {
			btnUndo.addStyleName("buttonActive");
			btnUndo.removeStyleName("buttonInactive");
		} else {
			btnUndo.removeStyleName("buttonActive");
			btnUndo.addStyleName("buttonInactive");
		}

		if (toolbarPanel.app.getKernel().redoPossible()) {
			btnRedo.removeStyleName("hideButton");
		} else {
			btnRedo.addStyleName("hideButton");
		}
	}

	private void addUndoButton(final FlowPanel panel) {
		btnUndo = new MyToggleButton(
				new Image(new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.undo_border()
								.getSafeUri(),
						0, 0, 24, 24, false, false)),
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

		if (app.has(Feature.TAB_ON_GUI)) {
			btnUndo.addKeyDownHandler(this);
		}

		panel.add(btnUndo);
	}

	private void addRedoButton(final FlowPanel panel) {
		btnRedo = new MyToggleButton(
				new Image(new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.redo_border()
								.getSafeUri(),
						0, 0, 24, 24, false, false)),
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

		if (app.has(Feature.TAB_ON_GUI)) {
			btnRedo.addKeyDownHandler(this);
		}

		panel.add(btnRedo);
	}
	
	/**
	 * @return - true if toolbar is open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param value
	 *            - true if toolbar should be open
	 */
	public void setOpen(boolean value) {
		this.open = value;
		updateDraggerStyle(value);
		
		if (app.isPortrait()) {
			toolbarPanel.updateHeight();
		} else {
			toolbarPanel.updateWidth();

		}

		toolbarPanel.showKeyboardButtonDeferred(
				isOpen() && toolbarPanel.getSelectedTabId() != TabIds.TOOLS);
	}

	private void updateDraggerStyle(boolean close) {
		DockSplitPaneW dockParent = getDockParent();
		if (dockParent != null) {
			if (app.isPortrait() && !close) {
				dockParent.removeStyleName("hide-Dragger");
				dockParent.addStyleName("moveUpDragger");
			} else {
				dockParent.removeStyleName("moveUpDragger");
				dockParent.addStyleName("hide-Dragger");
			}
		}
	}

	private DockSplitPaneW getDockParent() {
		ToolbarDockPanelW dockPanel = toolbarPanel.getToolbarDockPanel();
		return dockPanel != null ? dockPanel.getParentSplitPane() : null;
	}

	/**
	 * update style of toolbar
	 */
	public void updateStyle() {
		if (isAnimating()) {
			return;
		}
		toolbarPanel.updateStyle();
		removeStyleName("header-open-portrait");
		removeStyleName("header-close-portrait");
		removeStyleName("header-open-landscape");
		removeStyleName("header-close-landscape");
		updateButtonImages();
		String orientation = app.isPortrait() ? "portrait" : "landscape";
		if (open) {
			addStyleName("header-open-" + orientation);
			btnClose.getUpFace().setImage(imgClose);
			btnClose.setTitle(app.getLocalization().getMenu("Close"));
			if (!app.isPortrait()) {
				clearHeight();
				clearWidth();
			}
		} else {
			addStyleName("header-close-" + orientation);
			btnClose.getUpFace().setImage(imgOpen);
			btnClose.setTitle(app.getLocalization().getMenu("Open"));

		}

		updateMenuButtonStyle();

		updateUndoRedoPosition();
		updateUndoRedoActions();
	}

	private void updateMenuButtonStyle() {
		if (btnMenu == null) {
			return;
		}
		if (open) {
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
		if (open) {
			h = OPEN_HEIGHT;
		} else {
			h = getOffsetHeight() - getMenuButtonHeight()
					- btnClose.getOffsetHeight() - 2 * PADDING;
		}

		if (h > 0) {
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
		Log.debug("ORIENTATION: "
				+ (app.isPortrait() ? "portrait" : "landscape"));
		if (app.isPortrait()) {
			clearWidth();
			clearHeight();
			updateStyle();
		} else {
			if (!isOpen()) {
				int width = app.getArticleElement().getDataParamWidth();
				if (app.getArticleElement().getDataParamFitToScreen()) {
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
		int tabIndex = GUITabs.HEADER_TAB_START;
		for (ToggleButton btn : buttons) {
			if (btn != null) {
				btn.setTabIndex(tabIndex);

				tabIndex++;
			}
		}

		setAltTexts();
	}

	public void onKeyDown(KeyDownEvent event) {
		int key = event.getNativeKeyCode();
		if (key != GWTKeycodes.KEY_ENTER && key != GWTKeycodes.KEY_SPACE) {
			return;
		}
		Object source = event.getSource();
		if (source == null) {
			return;
		}
		if (source == btnMenu) {
			toolbarPanel.toggleMenu();
		} else if (source == btnAlgebra) {
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
			expandWidth(expandFrom);
			setHeight("100%");
			toolbarPanel.updateUndoRedoPosition();
		} else {
			expandWidth(expandTo);
			toolbarPanel.onOpen();
		}
		if (getDockParent() != null) {
			getDockParent().onResize();
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				updateCenterSize();
				showUndoRedoPanel();
				updateUndoRedoPosition();
				showCenter();
			}
		});

	}
}