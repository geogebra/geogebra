package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.PersistablePanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Toolbar for mow
 * 
 * @author csilla
 *
 */
public class ToolbarMow extends FlowPanel
		implements FastClickHandler, SetLabels {
	private AppW appW;
	private HeaderMow header;
	private FlowPanel toolbarPanel;
	private FlowPanel toolbarPanelContent;
	private StandardButton pageControlButton;
	private PageListPanel pageControlPanel;
	private boolean isOpen = true;
	/** panel containing undo and redo */
	private PersistablePanel undoRedoPanel;
	/** undo button */
	protected StandardButton btnUndo;
	/** redo button */
	protected StandardButton btnRedo;
	private PenSubMenu penPanel;
	private ToolsSubMenu toolsPanel;
	private MediaSubMenu mediaPanel;
	private TabIds currentTab;

	private final static int MAX_TOOLBAR_WIDTH = 600;
	private final static int FLOATING_BTNS_WIDTH = 48;
	private final static int FLOATING_BTNS_MARGIN_RIGHT = 16;

	/**
	 * Tab ids.
	 */
	enum TabIds {
		/** tab one */
		PEN,

		/** tab two */
		TOOLS,

		/** tab three */
		MEDIA
	}

	/**
	 * constructor
	 * 
	 * @param app
	 *            see {@link AppW}
	 */
	public ToolbarMow(AppW app) {
		this.appW = app;
		header = new HeaderMow(this, appW);
		add(header);
		initGui();
	}

	/**
	 * @return true if toolbar is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            true if toolbar is open
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	private void initGui() {
		addStyleName("toolbarMow");
		toolbarPanel = new FlowPanel();
		toolbarPanel.addStyleName("toolbarMowPanel");
		add(toolbarPanel);
		createUndoRedoButtons();
		createPageControlButton();
		createPanels();
		// setMode(appW.getMode());
		setLabels();
	}

	private void createPanels() {
		toolbarPanelContent = new FlowPanel();
		toolbarPanelContent.addStyleName("mowSubmenuScrollPanel");
		toolbarPanelContent.addStyleName("slideLeft");
		currentTab = TabIds.PEN;
		penPanel = new PenSubMenu(appW);
		toolsPanel = new ToolsSubMenu(appW);
		mediaPanel = new MediaSubMenu(appW);
		toolbarPanelContent.add(penPanel);
		toolbarPanelContent.add(toolsPanel);
		toolbarPanelContent.add(mediaPanel);
		toolbarPanel.add(toolbarPanelContent);
		updateAriaHidden();
	}

	private void createPageControlButton() {
		pageControlButton = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_page_control(), null, 24,
				appW);
		new FocusableWidget(AccessibilityGroup.PAGE_LIST_OPEN, null, pageControlButton)
				.attachTo(appW);
		pageControlButton.setStyleName("mowFloatingButton");
		showPageControlButton(true);

		pageControlButton.addBitlessDomHandler(event -> setTouchStyleForCards(),
				TouchStartEvent.getType());
		pageControlButton.addFastClickHandler(this);
		updateFloatingButtonsPosition();
	}

	/**
	 * make sure style is touch also on whiteboard
	 */
	protected void setTouchStyleForCards() {
		pageControlPanel.setIsTouch();
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

	private void movePageControlButtonDown() {
		pageControlButton.getElement().getStyle().setBottom(0, Unit.PX);
		pageControlButton.removeStyleName("narrowscreen");
	}

	private void movePageControlButtonAboveToolbar() {
		pageControlButton.getElement().getStyle().clearBottom();
		Dom.toggleClass(
				pageControlButton,
				"showMowSubmenu", "hideMowSubmenu",
				isOpen);
		pageControlButton.addStyleName("narrowscreen");
	}

	private void moveZoomPanelDown() {
		getDockPanel().moveZoomPanelToBottom();
	}

	private void moveZoomPanelAboveToolbar() {
		EuclidianDockPanelW dockPanel = getDockPanel();
		dockPanel.moveZoomPanelAboveToolbar();
		dockPanel.moveZoomPanelUpOrDown(isOpen);
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
	 * @param tab
	 *            id of tab
	 */
	public void tabSwitch(TabIds tab) {
		if (tab != currentTab) {
			currentTab = tab;
			toolbarPanelContent.removeStyleName("slideLeft");
			toolbarPanelContent.removeStyleName("slideCenter");
			toolbarPanelContent.removeStyleName("slideRight");
			switch (tab) {
			case PEN:
				toolbarPanelContent.addStyleName("slideLeft");
				break;
			case TOOLS:
				toolbarPanelContent.addStyleName("slideCenter");
				break;
			case MEDIA:
				toolbarPanelContent.addStyleName("slideRight");
				break;
			default:
				toolbarPanelContent.addStyleName("slideLeft");
				break;
			}
			appW.setMode(getCurrentPanel().getFirstMode());
			updateAriaHidden();
		}
	}

	private void updateAriaHidden() {
		penPanel.setAriaHidden(currentTab != TabIds.PEN);
		toolsPanel.setAriaHidden(currentTab != TabIds.TOOLS);
		mediaPanel.setAriaHidden(currentTab != TabIds.MEDIA);
	}

	@Override
	public void onClick(Widget source) {
		if (source == pageControlButton) {
			openPagePanel();
		} else if (source == btnUndo) {
			appW.getGuiManager().undo();
		} else if (source == btnRedo) {
			appW.getGuiManager().redo();
		}
		getFrame().deselectDragBtn();
	}

	/**
	 * @return the frame with casting.
	 */
	GeoGebraFrameFull getFrame() {
		return (((AppWFull) appW).getAppletFrame());
	}

	/**
	 * Opens the page control panel
	 */
	public void openPagePanel() {
		appW.hideMenu();
		EuclidianController ec = appW.getActiveEuclidianView().getEuclidianController();
		ec.widgetsToBackground();

		if (pageControlPanel == null) {
			pageControlPanel = ((AppWFull) appW).getAppletFrame()
					.getPageControlPanel();
		}
		pageControlPanel.open();
		appW.getPageController().updatePreviewImage();
	}

	private void createUndoRedoButtons() {
		undoRedoPanel = new PersistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
		undoRedoPanel.addStyleName(appW.getVendorSettings().getStyleName("undoRedoPosition"));
		// create buttons
		btnUndo = new StandardButton(
				MaterialDesignResources.INSTANCE.undo_border(), null, 24, appW);
		btnUndo.addStyleName("flatButton");
		btnUndo.addFastClickHandler(this);
		new FocusableWidget(AccessibilityGroup.UNDO, null, btnUndo).attachTo(appW);
		btnRedo = new StandardButton(
				MaterialDesignResources.INSTANCE.redo_border(), null, 24, appW);
		btnRedo.addFastClickHandler(this);
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

	@Override
	public void setLabels() {
		pageControlButton
				.setTitle(appW.getLocalization().getMenu("PageControl"));
		btnUndo.setTitle(appW.getLocalization().getMenu("Undo"));
		btnRedo.setTitle(appW.getLocalization().getMenu("Redo"));
		header.setLabels();
		penPanel.setLabels();
		toolsPanel.setLabels();
		mediaPanel.setLabels();
	}

	private SubMenuPanel getCurrentPanel() {
		switch (currentTab) {
		case PEN:
			return penPanel;
		case TOOLS:
			return toolsPanel;
		case MEDIA:
			return mediaPanel;
		default:
			return penPanel;
		}
	}

	/**
	 * @param mode
	 *            id of tool
	 */
	public void setMode(int mode) {
		if (((AppWFull) appW).getZoomPanelMow() != null
				&& mode != EuclidianConstants.MODE_TRANSLATEVIEW) {
			((AppWFull) appW).getZoomPanelMow().getDragPadBtn()
					.removeStyleName("selected");
		}
		getCurrentPanel().setMode(mode);
	}
}
