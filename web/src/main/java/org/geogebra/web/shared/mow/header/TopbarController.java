package org.geogebra.web.shared.mow.header;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.main.AppW;

public class TopbarController {
	private final AppW appW;
	private ZoomController zoomController;
	private final EuclidianView view;
	private final Runnable deselectDragBtn;

	/**
	 * Controller
	 * @param appW - application
	 * @param deselectDragBtn
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

	public void onZoomIn() {
		zoomController.onZoomInPressed();
		deselectDragBtn.run();
	}

	public void onZoomOut() {
		zoomController.onZoomOutPressed();
		deselectDragBtn.run();
	}

	public void onHome() {
		zoomController.onHomePressed();
		deselectDragBtn.run();
	}

	public void onDrag() {
		appW.setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
		appW.hideMenu();
	}

	public AppW getApp() {
		return appW;
	}

	public void updateHomeButtonVisibility(IconButton homeBtn) {
		if (view == null) {
			return;
		}
		if (view.isCoordSystemTranslatedByAnimation()) {
			return;
		}
		homeBtn.setDisabled(view.isStandardView());
	}
}
