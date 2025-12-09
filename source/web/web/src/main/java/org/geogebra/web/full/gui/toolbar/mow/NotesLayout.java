/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.toolbar.mow;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.ModeChangeListener;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.NotesToolbox;
import org.geogebra.web.full.main.AppWFull;
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
	public NotesLayout(AppWFull appW) {
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

	/**
	 * Add a custom tool with given properties
	 *
	 * @param iconUrl the URL of the tool icon.
	 * @param name The name of the tool.
	 * @param category to put the tool in.
	 * @param callback the action of the tool.
	 */
	public void addCustomTool(String iconUrl, String name, String category, Object callback) {
		if (toolbar != null) {
			toolbar.addCustomTool(iconUrl, name, category, callback);
		}
	}
}
