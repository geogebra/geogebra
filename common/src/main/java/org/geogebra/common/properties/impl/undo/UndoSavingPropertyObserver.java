package org.geogebra.common.properties.impl.undo;

import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.RangeProperty;

/**
 * Saves an undo point when a property value has changed.
 * Ignores changes to range properties, they should call
 * directly into {@link UndoSavingPropertyObserver#sliderMoveEnded()}.
 */
public class UndoSavingPropertyObserver implements PropertyObserver {

	private final UndoManager undoManager;

	public UndoSavingPropertyObserver(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	@Override
	public void onChange(Property property) {
		if (!(property instanceof RangeProperty)) {
			undoManager.storeUndoInfo();
		}
	}

	public void sliderMoveEnded() {
		undoManager.storeUndoInfo();
	}
}
