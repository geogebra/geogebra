package org.geogebra.web.web.gui.toolbar.mow;


import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.util.PersistablePanel;

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
public class MOWToolbar extends FlowPanel implements FastClickHandler {
	/**
	 * application
	 */
	AppW app;
	private StandardButton closeButton;
	private StandardButton penButton;
	private StandardButton toolsButton;
	private StandardButton mediaButton;
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
		add(LayoutUtilW.panelRow(middlePanel, rightPanel));

		subMenuPanel = new FlowPanel();
		subMenuPanel.addStyleName("scrollPanel");
		subMenuPanel.add(penMenu);
		subMenuPanel.add(toolsMenu);
		subMenuPanel.add(mediaMenu);
		add(subMenuPanel);

		addStyleName("mowToolbar");
		// sets the horizontal position of the toolbar
		setResponsivePosition();
		updateUndoRedoPosition();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				setResponsivePosition();
				updateUndoRedoPosition();
			}
		});


	}

	private void createUndoRedoButtons() {
		undoRedoPanel = new PersistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
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
	 * update position of undo+redo panel
	 */
	public void updateUndoRedoPosition() {

		undoRedoPanel.getElement().getStyle().setLeft(0, Unit.PX);
		// toolbar max width = 700 + undoRedoPanel width = 120
		// 700+2*120 = 940
		if (app.getWidth() > 940) {
			undoRedoPanel.getElement().getStyle().setBottom(0, Unit.PX);
		} else {
			undoRedoPanel.getElement().getStyle().clearBottom();
			if (isSubmenuOpen) {
				undoRedoPanel.removeStyleName("hideSubmenu");
				undoRedoPanel.addStyleName("showSubmenu");
			} else {
				undoRedoPanel.removeStyleName("showSubmenu");
				undoRedoPanel.addStyleName("hideSubmenu");
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
		penButton = new StandardButton(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.mow_pen_panel()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		penButton.addFastClickHandler(this);
		penMenu = new PenSubMenu(app);
	}

	private void createToolsButton() {
		toolsButton = new StandardButton(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.mow_tools_panel()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		toolsButton.addFastClickHandler(this);
		toolsMenu = new ToolsSubMenu(app);
	}

	private void createMediaButton() {
		mediaButton = new StandardButton(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.mow_media_panel()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		mediaButton.addFastClickHandler(this);
		mediaMenu = new MediaSubMenu(app);
	}

	private void createCloseButton() {
		closeButton = new StandardButton(new ImageResourcePrototype(
				null, MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_black().getSafeUri(),
				0, 0, 24, 24, false, false), app);
		rightPanel.add(closeButton);
		closeButton.addFastClickHandler(this);
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
		StandardButton[] buttons = { penButton, toolsButton, mediaButton };
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
		updateUndoRedoPosition();
		updateUndoRedoActions();
	}



	@Override
	public void onClick(Widget source) {

		if (source == closeButton) {
			if (subMenuPanel.isVisible()) {
				setSubmenuVisible(false);
				toggleCloseButton(false);
			} else {
				setSubmenuVisible(true);
				toggleCloseButton(true);
			}

		} else if (source == penButton) {
			setCurrentMenu(penMenu);
			toggleCloseButton(true);
		} else if (source == toolsButton) {
			setCurrentMenu(toolsMenu);
			toggleCloseButton(true);
		} else if (source == mediaButton) {
			setCurrentMenu(mediaMenu);
			toggleCloseButton(true);
		}
		selectButton(source);
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
		if (mode == EuclidianConstants.MODE_PEN) {
			selectButton(penButton);
		}
		if (!(mode == EuclidianConstants.MODE_PEN
				&& currentMenu == penMenu)
				&& mode != EuclidianConstants.MODE_SELECTION_LISTENER
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
				setStyleName("showSubmenu");
			}
			isSubmenuOpen = true;
		} else {
			if (isSubmenuOpen) {
				setStyleName("hideSubmenu");
			}
			Timer timer = new Timer() {
				@Override
				public void run() {
					doShowSubmenu(visible);
				}
			};
			timer.schedule(250);
			isSubmenuOpen = false;
		}
		addStyleName("mowToolbar");
		setResponsivePosition();
		updateUndoRedoPosition();
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
	public void setResponsivePosition() {
		// small screen
		if (app.getWidth() < 700) {
			removeStyleName("BigScreen");
			addStyleName("SmallScreen");
			getElement().getStyle().setLeft(0, Unit.PX);
		} // big screen
		else {
			removeStyleName("SmallScreen");
			addStyleName("BigScreen");
			getElement().getStyle().setLeft((app.getWidth() - 700) / 2, Unit.PX);
		}
	}

}
