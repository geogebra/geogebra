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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.main.undo.UpdateStyleActionStore;

public class UndoableSliderHandler
		implements SliderEventHandler {

	private final ElementPropertySetter parent;
	private final StyleBarW2 selection;
	private UpdateStyleActionStore undoStore;

	/**
	 * @param parent input listener
	 * @param selection provides selected (or default) geos
	 */
	public UndoableSliderHandler(ElementPropertySetter parent,
			StyleBarW2 selection) {
		this.parent = parent;
		this.selection = selection;
	}

	@Override
	public void onValueChange() {
		parent.apply(selection.getTargetGeos());
		if (undoStore != null && undoStore.needUndo()) {
			undoStore.storeUndo();
			undoStore = null;
		}
	}

	@Override
	public void onSliderInput() {
		if (undoStore == null && !selection.getTargetGeos().isEmpty()) {
			UndoManager undoManager = selection.getTargetGeos().get(0)
					.getConstruction().getUndoManager();
			undoStore = new UpdateStyleActionStore(selection.getTargetGeos(), undoManager);
		}
		parent.apply(selection.getTargetGeos());
	}
}
