package org.geogebra.web.shared.mow.header;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;

import java.util.function.Consumer;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.full.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.main.AppW;

public class TopbarController {
	private final AppW appW;
	private ZoomController zoomController;
	private final EuclidianView view;
	private final Runnable deselectDragBtn;
	private ContextMenuGraphicsWindowW settingsContextMenu;

	/**
	 * Controller
	 * @param appW - application
	 * @param deselectDragBtn - deselect drag button callback
	 */
	public TopbarController(AppW appW, Runnable deselectDragBtn) {
		this.appW = appW;
		this.deselectDragBtn = deselectDragBtn;
		this.view = appW.getActiveEuclidianView();
		zoomController = new ZoomController(appW, view);
	}

	/**
	 * on menu pressed
	 */
	public void onMenuToggle() {
		appW.hideKeyboard();
		appW.toggleMenu();
		deselectDragBtn.run();
	}

	/**
	 * on undo pressed
	 */
	public void onUndo() {
		appW.getGuiManager().undo();
		deselectDragBtn.run();
	}

	/**
	 * on redo pressed
	 */
	public void onRedo() {
		appW.getGuiManager().redo();
		deselectDragBtn.run();
	}

	/**
	 * on zoom in press
	 */
	public void onZoomIn() {
		setSelectMode();
		zoomController.onZoomInPressed();
		deselectDragBtn.run();
	}

	/**
	 * on zoom out press
	 */
	public void onZoomOut() {
		setSelectMode();
		zoomController.onZoomOutPressed();
		deselectDragBtn.run();
	}

	private void setSelectMode() {
		if (appW.getMode() != MODE_SELECT_MOW) {
			appW.setMode(MODE_SELECT_MOW);
		}
	}

	/**
	 * on home press
	 */
	public void onHome() {
		zoomController.onHomePressed();
		deselectDragBtn.run();
	}

	/**
	 * on drag button press
	 */
	public void onDrag() {
		appW.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
		appW.hideMenu();
	}

	public AppW getApp() {
		return appW;
	}

	/**
	 * update home button state based on whether view is standard view
	 * @param homeBtn - home button
	 */
	public void updateHomeButtonVisibility(IconButton homeBtn) {
		if (view == null) {
			return;
		}
		if (view.isCoordSystemTranslatedByAnimation()) {
			return;
		}
		if (homeBtn != null) {
			homeBtn.setDisabled(view.isStandardView());
		}
	}

	/**
	 * @return whether fullscreen button is allowed or not
	 */
	public boolean needsFullscreenButton() {
		return ZoomController.needsFullscreenButton(appW);
	}

	/**
	 * on fullscreen press
	 * @param fullscreenBtn - fullscreen button
	 */
	public void onFullscreenOn(IconButton fullscreenBtn) {
		zoomController.onFullscreenPressed(null, getFullscreenBtnSelectCB(fullscreenBtn));
	}

	private Consumer<Boolean> getFullscreenBtnSelectCB(final IconButton fullscreenBtn) {
		return fullScreenActive -> {
			if (fullscreenBtn != null) {
				fullscreenBtn.setIcon(fullScreenActive
						? ZoomPanelResources.INSTANCE.fullscreen_exit_black18()
						: ZoomPanelResources.INSTANCE.fullscreen_black18());
			}
		};
	}

	/**
	 * on settings press
	 * @param anchor - settings button
	 */
	public void onSettingsOpen(IconButton anchor) {
		initSettingsContextMenu(anchor);
		toggleSettingsContextMenu(anchor);
	}

	private void initSettingsContextMenu(IconButton anchor) {
		if (settingsContextMenu == null) {
			settingsContextMenu = new ContextMenuGraphicsWindowW(appW, 0, 0, false);
			getSettingsContextMenu().setAutoHideEnabled(false);
			getSettingsContextMenu().addCloseHandler(event -> {
				anchor.setActive(false);
			});
		}
	}

	private void toggleSettingsContextMenu(IconButton anchor) {
		boolean settingsShowing  = getSettingsContextMenu().isShowing();
		if (settingsShowing) {
			settingsContextMenu.getWrappedPopup().getPopupPanel().hide();
		} else {
			appW.registerPopup(getSettingsContextMenu());
			settingsContextMenu.getWrappedPopup().showAtPoint((int) (anchor.getAbsoluteLeft()
					- appW.getAbsLeft()), (int) (anchor.getAbsoluteTop()
					+ anchor.getOffsetHeight() - appW.getAbsTop()));
		}
		anchor.setActive(!settingsShowing);
	}

	private final GPopupPanel getSettingsContextMenu() {
		return settingsContextMenu.getWrappedPopup().getPopupPanel();
	}
}
