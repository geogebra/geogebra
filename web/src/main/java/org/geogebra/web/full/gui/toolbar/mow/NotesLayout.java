package org.geogebra.web.full.gui.toolbar.mow;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.ModeChangeListener;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.NotesToolbox;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.mow.header.NotesTopBar;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.Widget;

public class NotesLayout implements SetLabels, ModeChangeListener {
	private final AppW appW;
	private final @CheckForNull NotesToolbox toolbar;
	private final @CheckForNull NotesTopBar topBar;
	private StandardButton pageControlButton;
	private @CheckForNull PageListPanel pageControlPanel;

	/**
	 * @param appW application
	 */
	public NotesLayout(AppW appW) {
		this.appW = appW;
		topBar = new NotesTopBar(appW);
		this.toolbar = appW.showToolBar() ? new NotesToolbox(appW, topBar.wasAttached()) : null;
		appW.getActiveEuclidianView().getEuclidianController()
				.setModeChangeListener(this);
		createPageControlButton();
		setLabels();
	}

	private void createPageControlButton() {
		pageControlButton = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_page_control(), null, 24);
		new FocusableWidget(AccessibilityGroup.PAGE_LIST_OPEN, null, pageControlButton)
				.attachTo(appW);
		pageControlButton.setStyleName("mowFloatingButton");
		pageControlButton.addStyleName("floatingActionButton");
		showPageControlButton(true);

		pageControlButton.addBitlessDomHandler(event -> setTouchStyleForCards(),
				TouchStartEvent.getType());
		pageControlButton.addFastClickHandler(this::openPagePanel);
	}

	/**
	 * make sure style is touch also on whiteboard
	 */
	protected void setTouchStyleForCards() {
		getPageControlPanel().setIsTouch();
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
	 * Opens the page control panel
	 */
	public void openPagePanel(Widget trigger) {
		appW.hideMenu();
		appW.closePopups();
		EuclidianController ec = appW.getActiveEuclidianView().getEuclidianController();
		ec.widgetsToBackground();

		getPageControlPanel().open();
		appW.getPageController().updatePreviewImage();
		appW.setMode(MODE_SELECT_MOW);
	}

	private PageListPanel getPageControlPanel() {
		if (pageControlPanel == null) {
			pageControlPanel = ((AppWFull) appW).getAppletFrame()
					.getPageControlPanel();
		}
		return pageControlPanel;
	}

	@Override
	public void setLabels() {
		if (toolbar != null) {
			toolbar.setLabels();
		}
		if (topBar != null) {
			topBar.setLabels();
		}
		pageControlButton
				.setTitle(appW.getLocalization().getMenu("PageControl"));
	}

	/**
	 * update style of undo+redo buttons
	 */
	public void updateUndoRedoActions() {
		if (topBar != null) {
			topBar.updateUndoRedoActions(appW.getKernel());
		}
	}

	public Widget getToolbar() {
		return toolbar;
	}

	public NotesTopBar getTopBar() {
		return topBar;
	}

	@Override
	public void onModeChange(int mode) {
		if (topBar != null) {
			topBar.onModeChange(mode);
		}
		if (toolbar != null) {
			toolbar.onModeChange(mode);
		}
	}
}
