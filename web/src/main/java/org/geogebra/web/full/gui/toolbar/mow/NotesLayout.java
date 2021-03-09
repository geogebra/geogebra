package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.PersistablePanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Widget;

public class NotesLayout implements SetLabels {
	private final static int MAX_TOOLBAR_WIDTH = 600;
	private final static int FLOATING_BTNS_WIDTH = 48;
	private final static int FLOATING_BTNS_MARGIN_RIGHT = 16;
	private final AppW appW;
	private final ToolbarMow toolbar;
	private StandardButton pageControlButton;
	private PageListPanel pageControlPanel;
	/** panel containing undo and redo */
	private PersistablePanel undoRedoPanel;
	/** undo button */
	protected StandardButton btnUndo;
	/** redo button */
	protected StandardButton btnRedo;

	/**
	 * @param appW application
	 */
	public NotesLayout(AppW appW) {
		this.appW = appW;
		this.toolbar = new ToolbarMow(appW, this);
		createUndoRedoButtons();
		createPageControlButton();
		setLabels();
	}

	private void createPageControlButton() {
		pageControlButton = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_page_control(), null, 24);
		new FocusableWidget(AccessibilityGroup.PAGE_LIST_OPEN, null, pageControlButton)
				.attachTo(appW);
		pageControlButton.setStyleName("mowFloatingButton");
		showPageControlButton(true);

		pageControlButton.addBitlessDomHandler(event -> setTouchStyleForCards(),
				TouchStartEvent.getType());
		pageControlButton.addFastClickHandler(this::openPagePanel);
		updateFloatingButtonsPosition();
	}

	/**
	 * make sure style is touch also on whiteboard
	 */
	protected void setTouchStyleForCards() {
		pageControlPanel.setIsTouch();
	}

	private void movePageControlButtonDown() {
		pageControlButton.getElement().getStyle().setBottom(0, Style.Unit.PX);
		pageControlButton.removeStyleName("narrowscreen");
	}

	private void movePageControlButtonAboveToolbar() {
		pageControlButton.getElement().getStyle().clearBottom();
		Dom.toggleClass(
				pageControlButton,
				"showMowSubmenu", "hideMowSubmenu",
				toolbar.isOpen());
		pageControlButton.addStyleName("narrowscreen");
	}

	private void moveZoomPanelDown() {
		getDockPanel().moveZoomPanelToBottom();
	}

	private void moveZoomPanelAboveToolbar() {
		EuclidianDockPanelW dockPanel = getDockPanel();
		dockPanel.moveZoomPanelAboveToolbar();
		dockPanel.moveZoomPanelUpOrDown(toolbar.isOpen());
	}

	private EuclidianDockPanelW getDockPanel() {
		return (EuclidianDockPanelW) appW
				.getGuiManager()
				.getLayout()
				.getDockManager()
				.getPanel(App.VIEW_EUCLIDIAN);
	}

	private boolean isEnoughSpaceForFloatingButtonBesideToolbar() {
		int spaceNeededForFloatingButton = (FLOATING_BTNS_WIDTH + FLOATING_BTNS_MARGIN_RIGHT) * 2;
		int toolbarWithFloatingButtonWidth = MAX_TOOLBAR_WIDTH + spaceNeededForFloatingButton;
		return appW.getWidth() > toolbarWithFloatingButtonWidth;
	}

	/**
	 * @return button to open/close the page side panel
	 */
	public StandardButton getPageControlButton() {
		return pageControlButton;
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
		Dom.toggleClass(pageControlButton, "showMowFloatingButton",
				"hideMowFloatingButton", doShow);
	}

	/**
	 * updates position of pageControlButton and zoomPanel
	 */
	public void updateFloatingButtonsPosition() {
		if (isEnoughSpaceForFloatingButtonBesideToolbar()) {
			moveZoomPanelDown();
			movePageControlButtonDown();
		} else {
			moveZoomPanelAboveToolbar();
			movePageControlButtonAboveToolbar();
		}
	}

	/**
	 * Opens the page control panel
	 */
	public void openPagePanel(Widget trigger) {
		appW.hideMenu();
		EuclidianController ec = appW.getActiveEuclidianView().getEuclidianController();
		ec.widgetsToBackground();

		if (pageControlPanel == null) {
			pageControlPanel = ((AppWFull) appW).getAppletFrame()
					.getPageControlPanel();
		}
		pageControlPanel.open();
		appW.getPageController().updatePreviewImage();
		deselectDragButton();
	}

	protected void deselectDragButton() {
		(((AppWFull) appW).getAppletFrame()).deselectDragBtn();
	}

	@Override
	public void setLabels() {
		toolbar.setLabels();
		pageControlButton
				.setTitle(appW.getLocalization().getMenu("PageControl"));
		btnUndo.setTitle(appW.getLocalization().getMenu("Undo"));
		btnRedo.setTitle(appW.getLocalization().getMenu("Redo"));
	}

	private void createUndoRedoButtons() {
		undoRedoPanel = new PersistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
		undoRedoPanel.addStyleName(appW.getVendorSettings().getStyleName("undoRedoPosition"));
		// create buttons
		btnUndo = new StandardButton(
				MaterialDesignResources.INSTANCE.undo_border(), null, 24);
		btnUndo.addStyleName("flatButton");
		btnUndo.addFastClickHandler(widget -> {
			appW.getGuiManager().undo();
			deselectDragButton();
		});
		new FocusableWidget(AccessibilityGroup.UNDO, null, btnUndo).attachTo(appW);
		btnRedo = new StandardButton(
				MaterialDesignResources.INSTANCE.redo_border(), null, 24);
		btnRedo.addFastClickHandler(widget -> {
			appW.getGuiManager().redo();
			deselectDragButton();
		});
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");
		new FocusableWidget(AccessibilityGroup.REDO, null, btnRedo).attachTo(appW);
		undoRedoPanel.add(btnUndo);
		undoRedoPanel.add(btnRedo);
	}

	/**
	 * update style of undo+redo buttons
	 */
	public void updateUndoRedoActions() {
		if (appW.getKernel().undoPossible()) {
			btnUndo.addStyleName("buttonActive");
			btnUndo.removeStyleName("buttonInactive");
		} else {
			btnUndo.removeStyleName("buttonActive");
			btnUndo.addStyleName("buttonInactive");
		}
		if (appW.getKernel().redoPossible()) {
			btnRedo.removeStyleName("hideButton");
		} else {
			btnRedo.addStyleName("hideButton");
		}
	}

	/**
	 * @return undo/redo panel
	 */
	public PersistablePanel getUndoRedoButtons() {
		return undoRedoPanel;
	}

	public void setMode(int mode) {
		toolbar.setMode(mode);
	}

	public Widget getToolbar() {
		return toolbar;
	}

	/**
	 * @return true if toolbar open, false otherwise
	 */
	public boolean isNotesToolbarOpen() {
		return toolbar.isOpen();
	}

	/**
	 * @param open true if should open notes toolbar
	 */
	public void setToolbarOpen(boolean open) {
		toolbar.openCloseNotesToolbar(open);
	}
}
