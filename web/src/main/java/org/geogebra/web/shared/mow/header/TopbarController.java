package org.geogebra.web.shared.mow.header;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;

import java.util.function.Consumer;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.main.AppW;

public class TopbarController {
	private final AppW appW;
	private final ZoomController zoomController;
	private final EuclidianView view;
	private ContextMenuGraphicsWindowW settingsContextMenu;

	/**
	 * Controller
	 * @param appW - application
	 */
	public TopbarController(AppW appW) {
		this.appW = appW;
		this.view = appW.getActiveEuclidianView();
		zoomController = new ZoomController(appW, view);
	}

	/**
	 * on menu pressed
	 */
	public void onMenuToggle() {
		appW.hideKeyboard();
		appW.toggleMenu();
	}

	/**
	 * on undo pressed
	 */
	public void onUndo() {
		appW.getGuiManager().undo();
	}

	/**
	 * on redo pressed
	 */
	public void onRedo() {
		appW.getGuiManager().redo();
	}

	/**
	 * on zoom in press
	 */
	public void onZoomIn() {
		setSelectMode();
		zoomController.onZoomInPressed();
	}

	/**
	 * on zoom out press
	 */
	public void onZoomOut() {
		setSelectMode();
		zoomController.onZoomOutPressed();
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
	}

	/**
	 * on drag button press
	 * @param isDragButtonActive whether drag is on or off
	 */
	public void onDrag(boolean isDragButtonActive) {
		if (isDragButtonActive) {
			appW.setMode(EuclidianConstants.MODE_SELECT_MOW);
		} else {
			appW.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
			appW.hideMenu();
			appW.closePopups();
		}
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

	/**
	 * on fullscreen exit
	 * @param fullscreenBtn - fullscreen button
	 */
	public void onFullscreenExit(IconButton fullscreenBtn) {
		zoomController.onExitFullscreen(null, getFullscreenBtnSelectCB(fullscreenBtn));
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
	public void onSettingsOpen(IconButton anchor, FocusableWidget focusableAnchor) {
		appW.getAccessibilityManager().setAnchor(focusableAnchor);
		initSettingsContextMenu(anchor);
		toggleSettingsContextMenu(anchor);
	}

	private void initSettingsContextMenu(IconButton anchor) {
		if (settingsContextMenu == null) {
			settingsContextMenu = new ContextMenuGraphicsWindowW(appW, 0, 0, false);
			getSettingsContextMenu().setAutoHideEnabled(false);
			getSettingsContextMenu().addCloseHandler(event -> anchor.setActive(false));
		}
	}

	private void toggleSettingsContextMenu(IconButton anchor) {
		boolean settingsShowing  = getSettingsContextMenu().isShowing();
		if (settingsShowing) {
			settingsContextMenu.getWrappedPopup().hide();
		} else {
			showAndFocusMenuRelativeTo(anchor);
			appW.closePopups();
			appW.registerPopup(getSettingsContextMenu());
		}
		anchor.setActive(!settingsShowing);
	}

	private void showAndFocusMenuRelativeTo(IconButton anchor) {
		settingsContextMenu.getWrappedPopup().showAtPoint((int) (anchor.getAbsoluteLeft()
				- appW.getAbsLeft()), (int) (anchor.getAbsoluteTop()
				+ anchor.getOffsetHeight() - appW.getAbsTop()));
		settingsContextMenu.getWrappedPopup().getPopupMenu().getItemAt(0).getElement().focus();
	}

	private GPopupPanel getSettingsContextMenu() {
		return settingsContextMenu.getWrappedPopup().getPopupPanel();
	}

	/**
	 * register focusable widget
	 * @param button - focusable widget
	 * @param group - accessibility group
	 */
	public void registerFocusable(IconButton button, AccessibilityGroup group) {
		new FocusableWidget(group, null, button).attachTo(appW);
	}

	/**
	 * register focusable widget
	 * @param group - accessibility group
	 * @param button - focusable widget
	 * @return focusable widget
	 */
	public FocusableWidget getRegisteredFocusable(AccessibilityGroup group,
			IconButton button) {
		FocusableWidget focusableWidget = new FocusableWidget(group, null, button);
		focusableWidget.attachTo(appW);
		return focusableWidget;
	}
}
