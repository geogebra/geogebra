package org.geogebra.web.full.gui.util;

import java.util.ArrayList;
import java.util.function.Function;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.undo.UpdateStyleActionStore;

public class UndoableSliderHandler
		implements SliderEventHandler {

	private final Function<ArrayList<GeoElement>, Boolean> parent;
	private final StyleBarW2 selection;
	private UpdateStyleActionStore undoStore;

	/**
	 * @param parent input listener
	 * @param selection provides selected (or default) geos
	 */
	public UndoableSliderHandler(Function<ArrayList<GeoElement>, Boolean> parent,
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
			undoStore = new UpdateStyleActionStore(selection.getTargetGeos());
		}
		parent.apply(selection.getTargetGeos());
	}
}
