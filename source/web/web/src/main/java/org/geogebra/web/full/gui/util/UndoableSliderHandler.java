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
