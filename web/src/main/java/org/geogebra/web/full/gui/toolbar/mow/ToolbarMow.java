package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.MyModeChangedListener;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.PersistablePanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Toolbar for mow
 * 
 * @author csilla
 *
 */
public class ToolbarMow extends FlowPanel
		implements MyModeChangedListener, FastClickHandler {
	private AppW appW;
	private HeaderMow header;
	private FlowPanel toolbarPanel;
	private StandardButton pageControlButton;
	private PageListPanel pageControlPanel;
	private boolean isOpen = true;
	/** panel containing undo and redo */
	private PersistablePanel undoRedoPanel;
	/** undo button */
	protected StandardButton btnUndo;
	/** redo button */
	protected StandardButton btnRedo;
	private final static int MAX_TOOLBAR_WIDTH = 600;
	private final static int FLOATING_BTNS_WIDTH = 80;

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
	}

	private void createPageControlButton() {
		pageControlButton = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_page_control(), null, 24,
				appW);
		pageControlButton.setStyleName("mowFloatingButton");
		showPageControlButton(true);

		pageControlButton.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				setTouchStyleForCards();
			}
		});
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
		pageControlButton.addStyleName(
				doShow ? "showMowFloatingButton" : "hideMowFloatingButton");
		pageControlButton.removeStyleName(
				doShow ? "hideMowFloatingButton" : "showMowFloatingButton");
	}
	
	/**
	 * updates position of pageControlButton and zoomPanel
	 */
	public void updateFloatingButtonsPosition() {
		EuclidianDockPanelW dp = (EuclidianDockPanelW) (appW.getGuiManager()
				.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN));
		if (!appW.has(Feature.MOW_MULTI_PAGE)) {
			if (appW.getWidth() > MAX_TOOLBAR_WIDTH + FLOATING_BTNS_WIDTH) {
				dp.setZoomPanelBottom(true);
			} else {
				dp.setZoomPanelBottom(false);
				if (isOpen) {
					dp.moveZoomPanelUpOrDown(true);
				} else {
					dp.moveZoomPanelUpOrDown(false);
				}
			}
		} else {
			if (appW.getWidth() > MAX_TOOLBAR_WIDTH + FLOATING_BTNS_WIDTH) {
				pageControlButton.getElement().getStyle().setBottom(0, Unit.PX);
				dp.setZoomPanelBottom(true);
			} else {
				pageControlButton.getElement().getStyle().clearBottom();
				dp.setZoomPanelBottom(false);
				if (isOpen) {
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
		// TODO switch tab and toolbar panel
	}

	/**
	 * @param mode
	 *            tool id
	 */
	public void onModeChange(int mode) {
		// TODO
	}

	/**
	 * click handler
	 */
	public void onClick(Widget source) {
		if (source == pageControlButton) {
			openPagePanel();
		} else if (source == btnUndo) {
			appW.getGuiManager().undo();
		} else if (source == btnRedo) {
			appW.getGuiManager().redo();
		}
	}

	/**
	 * Opens the page control panel
	 */
	public void openPagePanel() {
		if (appW.isMenuShowing()) {
			appW.toggleMenu();
		}
		appW.getActiveEuclidianView().getEuclidianController()
				.widgetsToBackground();
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
		undoRedoPanel.addStyleName("undoRedoPositionMow");
		// create buttons
		btnUndo = new StandardButton(
				MaterialDesignResources.INSTANCE.undo_border(), null,24,appW);
		btnUndo.addStyleName("flatButton");
		btnUndo.addFastClickHandler(this);
		btnRedo = new StandardButton(
				MaterialDesignResources.INSTANCE.redo_border(), null, 24,
				appW);
		btnRedo.addFastClickHandler(this);
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");
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
}
