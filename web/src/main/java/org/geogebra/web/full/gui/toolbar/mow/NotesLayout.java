package org.geogebra.web.full.gui.toolbar.mow;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.ModeChangeListener;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.NotesToolbox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.mow.header.NotesTopBar;
import org.gwtproject.user.client.ui.Widget;

public class NotesLayout implements SetLabels, ModeChangeListener {
	private final AppW appW;
	private final @CheckForNull NotesToolbox toolbar;
	private final @CheckForNull NotesTopBar topBar;

	private static final int TOP_BAR_HEIGHT = 48;

	/**
	 * @param appW application
	 */
	public NotesLayout(AppW appW) {
		this.appW = appW;
		topBar = new NotesTopBar(appW);
		this.toolbar = appW.showToolBar() ? new NotesToolbox(appW, topBar.wasAttached()) : null;
		appW.getActiveEuclidianView().getEuclidianController()
				.setModeChangeListener(this);
		setLabels();
	}

	@Override
	public void setLabels() {
		if (toolbar != null) {
			toolbar.setLabels();
		}
		if (topBar != null) {
			topBar.setLabels();
		}
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

	public int getTopBarHeight() {
		return topBar != null && topBar.wasAttached() ? TOP_BAR_HEIGHT : 0;
	}
}
