package org.geogebra.web.web.gui.toolbar.mow;


import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class MOWToolbar extends FlowPanel implements FastClickHandler {

	private static final int DEFAULT_SUBMENU_HEIGHT = 55;
	private static final int TOOLS_SUBMENU_HEIGHT = 120;
	private static final int MEDIA_SUBMENU_HEIGHT = 55;
	private static final int SUBMENU_ROW = 1;
	private AppW app;
	private StandardButton redoButton;
	private StandardButton undoButton;
	private StandardButton moveButton;
	private StandardButton penButton;
	private StandardButton toolsButton;
	private StandardButton mediaButton;
	private FlowPanel leftPanel;
	private FlowPanel middlePanel;
	private FlowPanel rightPanel;
	private SubMenuPanel currentMenu = null;
	private SubMenuPanel penMenu;
	private SubMenuPanel toolsMenu;
	private SubMenuPanel mediaMenu;
	private FlowPanel subMenuPanel;
	private int submenuHeight;
	private int lastSubmenuHeight;
	private ToolbarResources pr;

	public MOWToolbar(AppW app) {
		this.app = app;
		buildGUI();
	}

	protected void buildGUI() {
		pr = ((ImageFactory) GWT.create(ImageFactory.class)).getToolbarResources();
		// addStyleName("mowToolbar");
		leftPanel = new FlowPanel();
		leftPanel.addStyleName("left");

		middlePanel = new FlowPanel();
		middlePanel.addStyleName("middle");

		rightPanel = new FlowPanel();
		rightPanel.addStyleName("right");
		createUndoRedo();
		createMiddleButtons();
		createMoveButton();
		subMenuPanel = new FlowPanel();
		add(LayoutUtilW.panelRow(leftPanel, middlePanel, rightPanel));
		add(subMenuPanel);
		// hack
		submenuHeight = DEFAULT_SUBMENU_HEIGHT;
		lastSubmenuHeight = 0;
		addStyleName("mowToolbar");
		setResponsiveStyle();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				setResponsiveStyle();
			}
		});


	}

	private void createMiddleButtons() {
		createPenButton();
		createToolsButton();
		createMediaButton();
		middlePanel
				.add(LayoutUtilW.panelRow(penButton, toolsButton, mediaButton));
	}

	public static StandardButton createButton(String url,
			FastClickHandler handler) {
		NoDragImage im = new NoDragImage(url);
		StandardButton btn = new StandardButton(null, "", 32);
		btn.getUpFace().setImage(im);
		btn.addFastClickHandler(handler);
		// btn.addStyleName("mowPanelButton");
		return btn;
	}

	private void createPenButton() {
		penButton = createButton(GGWToolBar.getImageURL(EuclidianConstants.MODE_PEN_PANEL, app), this);
		penMenu = new PenSubMenu(app);
	}

	private void togglePenButton(boolean toggle) {
		NoDragImage upFace = getImage(pr.pen_panel_32(), 32);
		NoDragImage downFace = getImage(pr.pen_panel_active_32(), 32);
		if (!toggle) {
			penButton.getUpFace().setImage(upFace);
		} else {
			penButton.getUpFace().setImage(downFace);
		}
	}

	private void createToolsButton() {
		toolsButton = createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_TOOLS_PANEL,
						app),
				this);
		toolsMenu = new ToolsSubMenu(app);

	}

	private void toggleToolsButton(boolean toggle) {
		NoDragImage upFace = getImage(pr.tools_panel_32(), 32);
		NoDragImage downFace = getImage(pr.tools_panel_active_32(), 32);
		if (!toggle) {
			toolsButton.getUpFace().setImage(upFace);
		} else {
			toolsButton.getUpFace().setImage(downFace);
		}
	}

	private void createMediaButton() {
		mediaButton = createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_MEDIA_PANEL,
						app),
				this);
		mediaMenu = new MediaSubMenu(app);
	}

	private void toggleMediaButton(boolean toggle) {
		NoDragImage upFace = getImage(pr.media_panel_32(), 32);
		NoDragImage downFace = getImage(pr.media_panel_active_32(), 32);
		if (!toggle) {
			mediaButton.getUpFace().setImage(upFace);
		} else {
			mediaButton.getUpFace().setImage(downFace);
		}
	}

	private void setButtonActive(Widget button) {
		if (button == penButton) {
			togglePenButton(true);
			toggleToolsButton(false);
			toggleMediaButton(false);
			toggleMoveButton(false);
		}
		if (button == toolsButton) {
			togglePenButton(false);
			toggleToolsButton(true);
			toggleMediaButton(false);
			toggleMoveButton(false);
		}
		if (button == mediaButton) {
			togglePenButton(false);
			toggleToolsButton(false);
			toggleMediaButton(true);
			toggleMoveButton(false);
		}
	}

	private void createMoveButton() {
		moveButton = new StandardButton("");
		moveButton.getUpFace().setImage(getImage(pr.move_hand_32(), 32));
		rightPanel.add(moveButton);
		moveButton.addFastClickHandler(this);

	}

	public void toggleMoveButton(boolean toggle) {
		NoDragImage upFace = getImage(pr.move_hand_32(), 32);
		NoDragImage downFace = getImage(pr.move_hand_active_32(), 32);
		if (!toggle) {
			moveButton.getUpFace().setImage(upFace);
		} else {
			moveButton.getUpFace().setImage(downFace);
		}
	}


	public void update() {
		updateUndoActions();
	}

	private void createUndoRedo() {
		redoButton = new StandardButton(pr.redo_32(), null, 32);
		redoButton.getUpHoveringFace()
.setImage(getImage(pr.redo_32(), 32));

		redoButton.addFastClickHandler(this);

		redoButton.addStyleName("redoButton");
		redoButton.getElement().getStyle().setOverflow(Overflow.HIDDEN);

		undoButton = new StandardButton(pr.undo_32(), null, 32);
		undoButton.getUpHoveringFace()
.setImage(getImage(pr.undo_32(), 32));

		undoButton.addFastClickHandler(this);
		undoButton.addStyleName("undoButton");

		leftPanel.add(LayoutUtilW.panelRow(undoButton, redoButton));
		updateUndoActions();
	}

	/**
	 * @param uri
	 *            image URI
	 * @param width
	 *            size
	 * @return image wrapped in no-dragging widget
	 */
	public static NoDragImage getImage(ResourcePrototype uri, int width) {
		return new NoDragImage(ImgResourceHelper.safeURI(uri), width);
	}

	/**
	 * Update enabled/disabled for undo and redo
	 */
	public void updateUndoActions() {
		if (undoButton != null) {
			this.undoButton.setEnabled(app.getKernel().undoPossible());
		}
		if (this.redoButton != null) {
			this.redoButton.setEnabled(app.getKernel().redoPossible());
		}
	}

	public void onClick(Widget source) {
		if (source == redoButton) {
			app.getGuiManager().redo();
			app.hideKeyboard();
		} else if (source == undoButton) {
			app.getGuiManager().undo();
			app.hideKeyboard();
		} else if (source == moveButton) {
			app.setMoveMode();
			toggleMoveButton(true);
			if (currentMenu != null) {
				currentMenu.reset();
			}
		} else if (source == penButton) {
			Log.debug("source = penButton");
			submenuHeight = DEFAULT_SUBMENU_HEIGHT;
			setCurrentMenu(penMenu);
		} else if (source == toolsButton) {
			submenuHeight = TOOLS_SUBMENU_HEIGHT;
			setCurrentMenu(toolsMenu);
		} else if (source == mediaButton) {
			submenuHeight = MEDIA_SUBMENU_HEIGHT;
			setCurrentMenu(mediaMenu);
		}
		if (source != redoButton && source != undoButton) {
			setCSStoSelected(source);
		}
		setButtonActive(source);
		// lastSubmenuHeight = submenuHeight;
	}

	public void setMode(int mode) {
		if(mode == EuclidianConstants.MODE_MOVE){
			toggleMoveButton(true);
			if (currentMenu != null) {
				currentMenu.reset();
			}
			return;
		}
		toggleMoveButton(false);

		if (mode == EuclidianConstants.MODE_PEN || mode == EuclidianConstants.MODE_FREEHAND_SHAPE
				|| mode == EuclidianConstants.MODE_ERASER) {
			doSetCurrentMenu(penMenu);
			Log.debug("doSetCurrentMenu from setMode");
		} else {
			doSetCurrentMenu(toolsMenu);
		}

		if (currentMenu != null) {
			currentMenu.setMode(mode);
		}
	}

	public SubMenuPanel getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(SubMenuPanel submenu) {
		Log.debug("set current menu");
		if (submenu == null) {
			return;
		}

		if (currentMenu == submenu) {
			if (!subMenuPanel.isVisible()) {
				currentMenu.onOpen();
				// lastSubmenuHeight = 0;
			}

			setSubmenuVisible(!subMenuPanel.isVisible());
			return;
		}
		submenu.onOpen();
		doSetCurrentMenu(submenu);
		// lastSubmenuHeight = 0;
	}

	private void doSetCurrentMenu(SubMenuPanel submenu) {
		Log.debug("do set current menu");
		subMenuPanel.clear();
		this.currentMenu = submenu;
		subMenuPanel.add(currentMenu);
		setSubmenuVisible(true);

	}

	private void setSubmenuVisible(final boolean b) {
		Log.debug("set visible: " + b);
		Log.debug("last submenu height: " + lastSubmenuHeight);
		Log.debug("new submenu height: " + submenuHeight);

		// Log.printStacktrace("stacktrace");
		if (b) {
			subMenuPanel.setVisible(b);
			if (submenuHeight == 120) {
				if (lastSubmenuHeight == 0) {
					setStyleName("animate0to120");

				}
				if (lastSubmenuHeight == 55) {
					setStyleName("animate55to120");
				}
				lastSubmenuHeight = 120;
			}
			if (submenuHeight == 55) {
				if (lastSubmenuHeight == 0) {
					setStyleName("animate0to55");
				}
				if (lastSubmenuHeight == 120) {
					setStyleName("animate120to55");
				}
				lastSubmenuHeight = 55;
			}
		} else {
			if (submenuHeight == 120) {
				setStyleName("animate120to0");
			}
			if (submenuHeight == 55) {
				setStyleName("animate55to0");
			}
			Timer timer = new Timer() {
				public void run() {
					subMenuPanel.setVisible(b);
				}
			};
			timer.schedule(500);
			lastSubmenuHeight = 0;
		}
		addStyleName("mowToolbar");
		setResponsiveStyle();
	}

	public void setResponsiveStyle() {
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

	private void setCSStoSelected(Widget source) {
		FlowPanel parent = (FlowPanel) source.getParent();
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			Widget w = parent.getWidget(i);
			if (w == source) {
				w.addStyleName("mowActivePanelButton");
			} else {
				w.removeStyleName("mowActivePanelButton");
			}
		}
	}

}
