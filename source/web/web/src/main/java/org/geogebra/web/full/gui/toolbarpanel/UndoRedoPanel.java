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

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.PersistablePanel;

/**
 * Undo/redo panel for graphics view
 */
public class UndoRedoPanel extends PersistablePanel {
	private final UndoRedoProvider undoRedoProvider;

	/**
	 * @param app application
	 */
	public UndoRedoPanel(AppW app) {
		undoRedoProvider = new UndoRedoProvider(app, AccessibilityGroup.UNDO_GRAPHICS,
				AccessibilityGroup.REDO_GRAPHICS);
		addStyleName("undoRedoPanel");
		buildPanel();
	}

	private void buildPanel() {
		add(undoRedoProvider.getUndoButton());
		add(undoRedoProvider.getRedoButton());
	}

	/**
	 * Enable/disable undo and redo buttons if undo/redo action is possible.
	 */
	public void updateUndoRedoActions() {
		undoRedoProvider.updateUndoRedoActions();
	}

	protected void setLabels() {
		undoRedoProvider.setLabels();
	}
}
