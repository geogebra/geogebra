package org.geogebra.web.web.gui.toolbar.mow;


import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class MOWToolbar extends FlowPanel implements FastClickHandler {

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

	public MOWToolbar(AppW app) {
		this.app = app;
		buildGUI();
	}

	protected void buildGUI() {
		addStyleName("mowToolbar");
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
		submenuHeight = 65;

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
		return btn;
	}

	private void createPenButton() {
		penButton = createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_PEN, app), this);
		penMenu = new PenSubMenu(app);
	}

	private void createToolsButton() {
		toolsButton = createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_TEXT, app),
				this);
		toolsMenu = new ToolsSubMenu(app);

	}

	private void createMediaButton() {
		mediaButton = createButton(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_IMAGE, app),
				this);
		mediaMenu = new MediaSubMenu(app);
	}

	private void createMoveButton() {
		NoDragImage im = new NoDragImage(
				GGWToolBar.getImageURL(EuclidianConstants.MODE_MOVE, app));
		moveButton = new StandardButton(null, "", 32);
		moveButton.getUpFace().setImage(im);
		rightPanel.add(moveButton);
		moveButton.addFastClickHandler(this);

	}

	public void update() {
		updateUndoActions();
	}

	private void createUndoRedo() {
		PerspectiveResources pr = ((ImageFactory) GWT
				.create(ImageFactory.class)).getPerspectiveResources();

		redoButton = new StandardButton(pr.menu_header_redo(), null, 32);
		redoButton.getUpHoveringFace()
				.setImage(getImage(pr.menu_header_redo_hover(), 32));

		redoButton.addFastClickHandler(this);

		redoButton.addStyleName("redoButton");
		redoButton.getElement().getStyle().setOverflow(Overflow.HIDDEN);

		undoButton = new StandardButton(pr.menu_header_undo(), null, 32);
		undoButton.getUpHoveringFace()
				.setImage(getImage(pr.menu_header_undo_hover(), 32));

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
		} else if (source == penButton) {
			setCurrentMenu(penMenu);
		} else if (source == toolsButton) {
			setCurrentMenu(toolsMenu);
		} else if (source == mediaButton) {
			setCurrentMenu(mediaMenu);
		}
	}

	public SubMenuPanel getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(SubMenuPanel submenu) {
		if (submenu == null) {
			return;
		}

		if (currentMenu == submenu) {
			setSubmenuVisible(!subMenuPanel.isVisible());
			return;
		}

		subMenuPanel.clear();
		this.currentMenu = submenu;
		subMenuPanel.add(currentMenu);
		setSubmenuVisible(true);
	}

	private void setSubmenuVisible(final boolean b) {
		subMenuPanel.setVisible(b);
		getElement().getStyle().setTop((b ? -submenuHeight : 0), Unit.PX);



	}

}
