package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.gui.util.PersistablePanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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
	/** application */
	AppW app;
	/** opens/closes toolbar */
	protected MyToggleButton closeButton;
	/** opens pen submenu */
	protected MyToggleButton penButton;
	/** opens tool submenu */
	protected MyToggleButton toolsButton;
	/** opens media submenu */
	protected MyToggleButton mediaButton;
	private FlowPanel middlePanel;
	private FlowPanel rightPanel;
	private SubMenuPanel currentMenu = null;
	/** pen submenu */
	protected SubMenuPanel penMenu;
	/** tools submenu */
	protected SubMenuPanel toolsMenu;
	/** media submenu */
	protected SubMenuPanel mediaMenu;
	private FlowPanel subMenuPanel;
	private boolean isSubmenuOpen;
	private int currentMode = -1;
	/** panel containing undo and redo */
	private PersistablePanel undoRedoPanel;
	/** undo button */
	protected MyToggleButton btnUndo;
	/** redo button */
	protected MyToggleButton btnRedo;
	private MyToggleButton pageControlButton;
	private PageListPanel pageControlPanel;
	private final static int MAX_TOOLBAR_WIDTH = 600;
	private final static int FLOATING_BTNS_WIDTH = 80;

	private enum ButtonType {
		UNDO, REDO, CLOSE, PEN, TOOLS, MEDIA, PAGECONTROL, DEFAULT
	}

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
		createToolbarButtons();
		if (app.has(Feature.MOW_MULTI_PAGE)) {
			createPageControlButton();
		}
		createSubmenus();
		add(LayoutUtilW.panelRow(middlePanel, rightPanel));
		addStyleName("mowToolbar");

		ClickStartHandler.initDefaults(this, true, false);
		ClickStartHandler.init(this, getClickStartHandler(ButtonType.DEFAULT));
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateToolbarPosition();
			}
		});
	}

	private void createSubmenus() {
		subMenuPanel = new FlowPanel();
		subMenuPanel.addStyleName("mowSubmenuScrollPanel");
		penMenu = new PenSubMenu(app);
		toolsMenu = new ToolsSubMenu(app);
		mediaMenu = new MediaSubMenu(app);
		subMenuPanel.add(penMenu);
		subMenuPanel.add(toolsMenu);
		subMenuPanel.add(mediaMenu);
		add(subMenuPanel);
	}

	private static ImageResourcePrototype getIcon(SVGResource resource) {
		return new ImageResourcePrototype(null, resource.getSafeUri(), 0, 0, 24,
				24, false, false);
	}

	private ClickStartHandler getClickStartHandler(final ButtonType b) {
		ClickStartHandler handler = new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (b != ButtonType.PAGECONTROL) {
					closeFloatingMenus();
				}
				switch (b) {
				case UNDO:
					app.getGuiManager().undo();
					break;
				case REDO:
					app.getGuiManager().redo();
					break;
				case CLOSE:
					toggleSubmenu();
					break;
				case PEN:
					setMenu(penButton, penMenu);
					break;
				case TOOLS:
					setMenu(toolsButton, toolsMenu);
					break;
				case MEDIA:
					setMenu(mediaButton, mediaMenu);
					break;
				case PAGECONTROL:
					openPageControlPanel();
					break;
				case DEFAULT:
					// only close menus
					break;
				}
			}
		};
		return handler;
	}

	private MyToggleButton createButton(SVGResource resource, ButtonType type,
			String tooltip, FlowPanel parent) {
		MyToggleButton button = new MyToggleButton(new Image(getIcon(resource)),
				app);
		ClickStartHandler.init(button, getClickStartHandler(type));
		button.setTitle(app.getLocalization().getMenu(tooltip));
		if (parent != null) {
			parent.add(button);
		}
		return button;
	}

	private void createUndoRedoButtons() {
		undoRedoPanel = new PersistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
		undoRedoPanel.addStyleName("undoRedoPositionMow");

		btnUndo = createButton(MaterialDesignResources.INSTANCE.undo_border(),
				ButtonType.UNDO, "Undo", undoRedoPanel);
		btnUndo.addStyleName("flatButton");

		btnRedo = createButton(MaterialDesignResources.INSTANCE.redo_border(),
				ButtonType.REDO, "Redo", undoRedoPanel);
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");
	}

	private void createToolbarButtons() {
		middlePanel.addStyleName("indicatorLeft");
		middlePanel.getElement()
				.setInnerHTML(middlePanel.getElement().getInnerHTML()
						+ "<div class=\"indicator\"></div>");

		penButton = createButton(
				MaterialDesignResources.INSTANCE.mow_pen_panel(),
				ButtonType.PEN, "Pen", middlePanel);

		toolsButton = createButton(
				MaterialDesignResources.INSTANCE.toolbar_tools(),
				ButtonType.TOOLS, "Tools", middlePanel);

		mediaButton = createButton(
				MaterialDesignResources.INSTANCE.mow_media_panel(),
				ButtonType.MEDIA, "ToolCategory.Media", middlePanel);

		closeButton = createButton(
				MaterialDesignResources.INSTANCE.toolbar_close_portrait_white(),
				ButtonType.CLOSE, "Open", rightPanel);
	}

	private void createPageControlButton() {
		pageControlButton = createButton(
				MaterialDesignResources.INSTANCE.mow_page_control(),
				ButtonType.PAGECONTROL, "PageControl", null);
		pageControlButton.setStyleName("mowFloatingButton");
		showPageControlButton(true);

		pageControlButton.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				setTouchStyleForCards();
			}
		});
	}

	/**
	 * make sure style is touch also on whiteboard
	 */
	protected void setTouchStyleForCards() {
		pageControlPanel.setIsTouch();
	}
	/**
	 * Opens the page control panel
	 */
	public void openPageControlPanel() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		if (pageControlPanel == null) {
			pageControlPanel = ((AppWFull) app).getAppletFrame()
					.getPageControlPanel();
		}
		pageControlPanel.open();
		app.getPageController().updatePreviewImage();
	}

	/**
	 * @param doShow
	 *            - true if page control button should be visible, false
	 *            otherwise
	 */
	public void showPageControlButton(boolean doShow) {
		if (pageControlButton == null) {
			return;
		}
		pageControlButton.addStyleName(
				doShow ? "showMowFloatingButton" : "hideMowFloatingButton");
		pageControlButton.removeStyleName(
				doShow ? "hideMowFloatingButton" : "showMowFloatingButton");
	}

	/**
	 * @return pageControlButton
	 */
	public MyToggleButton getPageControlButton() {
		return pageControlButton;
	}

	/**
	 * Closes burger menu and page control panel
	 */
	protected void closeFloatingMenus() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		if (app.has(Feature.MOW_MULTI_PAGE) && pageControlPanel != null) {
			pageControlPanel.close();
		}
	}

	/**
	 * set current Menu, toggle Close Button and set CSS to selected
	 * 
	 * @param button
	 *            the button that was pressed
	 * @param submenu
	 *            submenu associated with the button
	 */
	protected void setMenu(MyToggleButton button, SubMenuPanel submenu) {
		setCurrentMenu(submenu);
		selectButton(button);
	}

	/**
	 * set submenu open/closed
	 */
	protected void toggleSubmenu() {
		setSubmenuVisible(!subMenuPanel.isVisible());
	}

	/**
	 * get the undo/redo panel
	 * 
	 * @return undo/redo panel
	 */
	public PersistablePanel getUndoRedoButtons() {
		return undoRedoPanel;
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
		Image upFace = new Image(getIcon(MaterialDesignResources.INSTANCE
				.toolbar_open_portrait_white()));
		upFace.getElement().setAttribute("draggable", "false");
		Image downFace = new Image(getIcon(MaterialDesignResources.INSTANCE
				.toolbar_close_portrait_white()));
		downFace.getElement().setAttribute("draggable", "false");
		if (toggle) {
			closeButton.getUpFace().setImage(downFace);
			closeButton.setTitle(app.getLocalization().getMenu("Close"));
		} else {
			closeButton.getUpFace().setImage(upFace);
			closeButton.setTitle(app.getLocalization().getMenu("Open"));
		}
	}

	private SubMenuPanel getSubMenuForMode(int mode) {
		if (mode == EuclidianConstants.MODE_TEXT
				|| mode == EuclidianConstants.MODE_IMAGE
				|| mode == EuclidianConstants.MODE_VIDEO
				|| mode == EuclidianConstants.MODE_CAMERA
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
		if (!(mode == EuclidianConstants.MODE_PEN && currentMenu == penMenu)
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
					0), Unit.PX);
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
					.setLeft((app.getWidth() - MAX_TOOLBAR_WIDTH) / 2, Unit.PX);
		}
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
}