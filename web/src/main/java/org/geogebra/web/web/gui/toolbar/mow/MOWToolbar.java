package org.geogebra.web.web.gui.toolbar.mow;


import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.pagecontrolpanel.PageControlPanel;
import org.geogebra.web.web.gui.util.PersistablePanel;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * The toolbar for MOW.
 * 
 * @author Laszlo Gal, Alicia Hofstaetter
 *
 */
public class MOWToolbar extends FlowPanel {
	/**
	 * application
	 */
	AppW app;
	private MyToggleButton closeButton;
	private MyToggleButton penButton;
	private MyToggleButton toolsButton;
	private MyToggleButton mediaButton;
	private FlowPanel middlePanel;
	private FlowPanel rightPanel;
	private SubMenuPanel currentMenu = null;
	private SubMenuPanel penMenu;
	private SubMenuPanel toolsMenu;
	private SubMenuPanel mediaMenu;
	private FlowPanel subMenuPanel;
	private boolean isSubmenuOpen;
	private int currentMode = -1;
	/**
	 * panel containing undo and redo
	 */
	PersistablePanel undoRedoPanel;
	private MyToggleButton btnUndo;
	private MyToggleButton btnRedo;
	private StandardButton pageControlButton;
	private PageControlPanel pageControlPanel;
	private final static int MAX_TOOLBAR_WIDTH = 600;
	private final static int FLOATING_BTNS_WIDTH = 80;
	/**
	 *
	 * @param app
	 *            application
	 */
	public MOWToolbar(AppW app) {
		this.app = app;
		buildGUI();
	}

	/**
	 * Builds main GUI parts: left (undo/redo), middle (pen/tools/media) and
	 * right (move) panels
	 */
	protected void buildGUI() {
		middlePanel = new FlowPanel();
		middlePanel.addStyleName("middle");
		rightPanel = new FlowPanel();
		rightPanel.addStyleName("right");

		createUndoRedoButtons();
		// midddle buttons open submenus
		createMiddleButtons();
		createCloseButton();
		if (app.has(Feature.MOW_MULTI_PAGE)) {
			createPageControlButton();
		}
		add(LayoutUtilW.panelRow(middlePanel, rightPanel));

		subMenuPanel = new FlowPanel();
		subMenuPanel.addStyleName("scrollPanel");
		subMenuPanel.add(penMenu);
		subMenuPanel.add(toolsMenu);
		subMenuPanel.add(mediaMenu);
		add(subMenuPanel);
		addStyleName("mowToolbar");

		ClickStartHandler.initDefaults(this, true, true);
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateToolbarPosition();
			}
		});
	}

	private void createUndoRedoButtons() {
		undoRedoPanel = new PersistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
		undoRedoPanel.addStyleName("undoRedoPositionMow");
		addUndoButton(undoRedoPanel);
		addRedoButton(undoRedoPanel);
	}

	/**
	 * get the undo/redo panel
	 * 
	 * @return undo/redo panel
	 */
	public PersistablePanel getUndoRedoButtons() {
		return undoRedoPanel;
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

		ClickStartHandler.init(btnUndo, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (app.isMenuShowing()) {
					app.toggleMenu();
				}
				app.getGuiManager().undo();
			}
		});
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

		ClickStartHandler.init(btnRedo, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (app.isMenuShowing()) {
					app.toggleMenu();
				}
				app.getGuiManager().redo();
			}
		});
		panel.add(btnRedo);
	}

	/**
	 * updates position of pageControlButton and zoomPanel
	 */
	public void updateFloatingButtonsPosition() {
		EuclidianDockPanelW dp = (EuclidianDockPanelW) (app.getGuiManager()
				.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN));

		if (!app.has(Feature.MOW_MULTI_PAGE)) {
			if (app.getWidth() > MAX_TOOLBAR_WIDTH + FLOATING_BTNS_WIDTH) {
				dp.setZoomPanelBottom(true);
			} else {
				dp.setZoomPanelBottom(false);
				if (isSubmenuOpen) {
					dp.moveZoomPanelUpOrDown(true);
				} else {
					dp.moveZoomPanelUpOrDown(false);
				}
			}
		} else {
			if (app.getWidth() > MAX_TOOLBAR_WIDTH + FLOATING_BTNS_WIDTH) {
				pageControlButton.getElement().getStyle().setBottom(0, Unit.PX);
				dp.setZoomPanelBottom(true);
			} else {
				pageControlButton.getElement().getStyle().clearBottom();
				dp.setZoomPanelBottom(false);
				if (isSubmenuOpen) {
					pageControlButton.removeStyleName("hideMowSubmenu");
					pageControlButton.addStyleName("showMowSubmenu");
					dp.moveZoomPanelUpOrDown(true);
				} else {
					pageControlButton.removeStyleName("showMowSubmenu");
					pageControlButton.addStyleName("hideMowSubmenu");
					dp.moveZoomPanelUpOrDown(false);
				}
			}
		}
	}

	/**
	 * update style of undo+redo buttons
	 */
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

	private void createMiddleButtons() {
		createPenButton();
		createToolsButton();
		createMediaButton();

		middlePanel.addStyleName("indicatorLeft");
		middlePanel.getElement()
				.setInnerHTML(middlePanel.getElement().getInnerHTML()
						+ "<div class=\"indicator\"></div>");
		middlePanel.add(penButton);
		middlePanel.add(toolsButton);
		middlePanel.add(mediaButton);
	}

	private void createPenButton() {
		penButton = new MyToggleButton(new Image(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.mow_pen_panel()
								.getSafeUri(),
						0, 0, 24, 24, false, false)),
				app);
		ClickStartHandler.init(penButton, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setPenMenu();
			}
		});
		penMenu = new PenSubMenu(app);
	}

	private void createToolsButton() {
		toolsButton = new MyToggleButton(new Image(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.mow_tools_panel()
								.getSafeUri(),
						0, 0, 24, 24, false, false)),
				app);
		ClickStartHandler.init(toolsButton, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setToolsMenu();
			}
		});
		toolsMenu = new ToolsSubMenu(app);
	}

	private void createMediaButton() {
		mediaButton = new MyToggleButton(new Image(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.mow_media_panel()
								.getSafeUri(),
						0, 0, 24, 24, false, false)),
				app);
		ClickStartHandler.init(mediaButton, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setMediaMenu();
			}
		});
		mediaMenu = new MediaSubMenu(app);
	}

	private void createCloseButton() {
		closeButton = new MyToggleButton(new Image(new ImageResourcePrototype(
				null, MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_black().getSafeUri(),
				0, 0, 24, 24, false, false)), app);
		rightPanel.add(closeButton);
		ClickStartHandler.init(closeButton, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toggleSubmenu();
			}
		});
	}

	private void createPageControlButton() {
		pageControlButton = new StandardButton(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.mow_page_control()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		pageControlButton.setStyleName("pageControlButton");
		pageControlButton.addFastClickHandler(new FastClickHandler() {
			public void onClick(Widget source) {
				// TODO open Page Control Panel
				openPageControlPanel();
				// TODO show floating + button
			}
		});
	}

	/**
	 * Opens the page control panel
	 */
	public void openPageControlPanel(){
		if(pageControlPanel == null){
			pageControlPanel = ((AppWapplet) app).getAppletFrame()
					.getPageControlPanel();
		}
		pageControlPanel.open();
	}

	/**
	 * Hides floating page control button
	 */
	public void hidePageControlButton() {
		if (pageControlButton == null) {
			return;
		}
		pageControlButton.addStyleName("hidePageControlButton");
		pageControlButton.removeStyleName("showPageControlButton");
	}

	/**
	 * Shows floating page control button
	 */
	public void showPageControlButton() {
		if (pageControlButton == null) {
			return;
		}
		pageControlButton.addStyleName("showPageControlButton");
		pageControlButton.removeStyleName("hidePageControlButton");
	}

	/**
	 * @return pageControlButton
	 */
	public StandardButton getPageControlButton() {
		return pageControlButton;
	}

	/**
	 * set current Menu to Pen Menu, toggle Close Button and set CSS to selected
	 */
	void setPenMenu() {
		setCurrentMenu(penMenu);
		selectButton(penButton);
	}

	/**
	 * set current Menu to Tools Menu, toggle Close Button and set CSS to
	 * selected
	 */
	void setToolsMenu() {
		setCurrentMenu(toolsMenu);
		selectButton(toolsButton);
	}

	/**
	 * set current Menu to Media Menu, toggle Close Button and set CSS to
	 * selected
	 */
	void setMediaMenu() {
		setCurrentMenu(mediaMenu);
		selectButton(mediaButton);
	}
	
	/**
	 * set submenu open/closed
	 */
	void toggleSubmenu() {
		setSubmenuVisible(!subMenuPanel.isVisible());
	}

	/**
	 * selects a tab and sets the indicator
	 * 
	 * @param source
	 *            the tab to be selected
	 */
	private void selectButton(Widget source) {
		if (source == penButton || source == toolsButton
				|| source == mediaButton) {
			MyToggleButton[] buttons = { penButton, toolsButton, mediaButton };
		String[] indicatorPosition = { "indicatorLeft", "indicatorCenter",
				"indicatorRight" };
		for (int i = 0; i < buttons.length; i++) {
				if (buttons[i] == source) {
					middlePanel.addStyleName(indicatorPosition[i]);
				buttons[i].addStyleName("selected");
			} else {
					middlePanel.removeStyleName(indicatorPosition[i]);
				buttons[i].removeStyleName("selected");
			}
		}
		}
	}

	/**
	 * Toggles the open/close toolbar icon
	 * 
	 * @param toggle
	 *            true if open action happend
	 * 
	 */
	public void toggleCloseButton(boolean toggle) {
		Image upFace = new Image(new ImageResourcePrototype(
				null, MaterialDesignResources.INSTANCE
						.toolbar_open_portrait_black().getSafeUri(),
				0, 0, 24, 24, false, false));
		upFace.getElement().setAttribute("draggable", "false");
		Image downFace = new Image(new ImageResourcePrototype(
				null, MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_black().getSafeUri(),
				0, 0, 24, 24, false, false));
		downFace.getElement().setAttribute("draggable", "false");
		if (toggle) {
			closeButton.getUpFace().setImage(downFace);
		} else {
			closeButton.getUpFace().setImage(upFace);
		}
	}

	/**
	 * Updates the toolbar ie. undo/redo button states
	 */
	public void update() {
		updateUndoRedoActions();
	}

	/**
	 * Updates the positions of undo/redo, floating buttons (page control, zoom
	 * panel) and toolbar
	 */
	public void updatePositions() {
		updateToolbarPosition();
		updateFloatingButtonsPosition();
	}

	private SubMenuPanel getSubMenuForMode(int mode) {
		if (mode == EuclidianConstants.MODE_TEXT
				|| mode == EuclidianConstants.MODE_IMAGE
				|| mode == EuclidianConstants.MODE_VIDEO
				|| mode == EuclidianConstants.MODE_AUDIO
				|| mode == EuclidianConstants.MODE_GEOGEBRA) {
			return mediaMenu;
		} else if (mode == EuclidianConstants.MODE_PEN
				|| mode == EuclidianConstants.MODE_ERASER) {
			return penMenu;
		} else if (mode == EuclidianConstants.MODE_MOVE
				|| mode == EuclidianConstants.MODE_SELECT
				|| mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			if (currentMenu != null) {
				return currentMenu;
			}
			return penMenu;
		} else {
			return toolsMenu;
		}
	}

	/**
	 * Set the toolbar state for the selected mode
	 * 
	 * @param mode
	 *            the mode to set.
	 */
	public void setMode(int mode) {
		if (mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			return;
		}
		if (mode == EuclidianConstants.MODE_PEN) {
			selectButton(penButton);
		}
		if (!(mode == EuclidianConstants.MODE_PEN
				&& currentMenu == penMenu)
				&& !((mode == EuclidianConstants.MODE_TEXT
						|| mode == EuclidianConstants.MODE_MOVE)
						&& currentMenu == mediaMenu)) {
			doSetCurrentMenu(getSubMenuForMode(mode));
		}
		if (currentMenu != null) {
			currentMenu.setMode(mode);
		}
		currentMode = mode;
	}

	/**
	 * 
	 * @return The current submenu panel that is visible or last used.
	 */
	public SubMenuPanel getCurrentMenu() {
		return currentMenu;
	}

	/**
	 * @return more of current selected tool
	 */
	public int getCurrentMode() {
		return currentMode;
	}

	/**
	 * Sets the actual submenu, and opens it if it is different than the last
	 * one, toggles its visibility otherwise.
	 * 
	 * @param submenu
	 *            The submenu panel to set.
	 */
	public void setCurrentMenu(SubMenuPanel submenu) {
		if (submenu == null) {
			setSubmenuVisible(false);
			currentMenu = null;
			return;
		}
		if (currentMenu == submenu) {
			if (!subMenuPanel.isVisible()) {
				currentMenu.onOpen();
				setSubmenuVisible(true);
			}
			return;
		}
		// this will call setMode => submenu is open
		app.setMode(submenu.getFirstMode());
	}

	private void doSetCurrentMenu(SubMenuPanel submenu) {
		setSubmenuVisible(true);
		if (submenu != currentMenu) {
			subMenuPanel.removeStyleName("slideLeft");
			subMenuPanel.removeStyleName("slideCenter");
			subMenuPanel.removeStyleName("slideRight");
			if (submenu == penMenu) {
				subMenuPanel.addStyleName("slideLeft");
			} else if (submenu == toolsMenu) {
				subMenuPanel.addStyleName("slideCenter");
			} else if (submenu == mediaMenu) {
				subMenuPanel.addStyleName("slideRight");
			}
			currentMenu = submenu;
		}
	}

	/**
	 * Set the submenu visible / invisible and adds the animation
	 * 
	 * @param visible
	 *            true if submenu should be visible
	 */
	private void setSubmenuVisible(final boolean visible) {
		if (visible) {
			subMenuPanel.setVisible(visible);
			if (!isSubmenuOpen) {
				setStyleName("showMowSubmenu");
				toggleCloseButton(true);
			}
			isSubmenuOpen = true;
		} else {
			if (isSubmenuOpen) {
				setStyleName("hideMowSubmenu");
				toggleCloseButton(false);
			}
			Timer timer = new Timer() {
				@Override
				public void run() {
					doShowSubmenu(visible);
				}
			};
			timer.schedule(200);
			isSubmenuOpen = false;
		}
		addStyleName("mowToolbar");
		updatePositions();
	}

	/**
	 * @param visible
	 *            whether to show the subpanel
	 */
	protected void doShowSubmenu(boolean visible) {
		subMenuPanel.setVisible(visible);
	}

	/**
	 * Sets the horizontal position of the toolbar depending on screen size
	 */
	public void updateToolbarPosition() {
		// small screen
		if ((app.getWidth() - MAX_TOOLBAR_WIDTH) / 2 < FLOATING_BTNS_WIDTH) {
			removeStyleName("SmallScreen");
			addStyleName("BigScreen");
			// floating buttons push toolbar to the left
			getElement().getStyle().setLeft(Math.max(
					app.getWidth() - MAX_TOOLBAR_WIDTH - FLOATING_BTNS_WIDTH,
					0),
					Unit.PX);
			if (app.getWidth() < MAX_TOOLBAR_WIDTH) {
				// toolbar gets 100% of app width, floating buttons move above
				// toolbar
				removeStyleName("BigScreen");
				addStyleName("SmallScreen");
				getElement().getStyle().setLeft(0, Unit.PX);
			}
		} // big screen
		else {
			removeStyleName("SmallScreen");
			addStyleName("BigScreen");
			getElement().getStyle()
					.setLeft((app.getWidth() - MAX_TOOLBAR_WIDTH) / 2,
					Unit.PX);
		}
	}
}