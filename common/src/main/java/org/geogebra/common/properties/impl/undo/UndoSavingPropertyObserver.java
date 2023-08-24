package org.geogebra.common.properties.impl.undo;

import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.ValuedProperty;

/**
 * Saves an undo point when a property value has changed.
 */
public class UndoSavingPropertyObserver implements PropertyValueObserver {

	private final UndoManager undoManager;

	public UndoSavingPropertyObserver(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	@Override
	public void onBeginSetValue(ValuedProperty property) {
		// Ignore
	}

	@Override
	public void onEndSetValue(ValuedProperty property) {
		if (property instanceof RangeProperty) {
			undoManager.storeUndoInfo();
		}
	}

	@Override
	public void onDidSetValue(ValuedProperty property) {
		if (!(property instanceof RangeProperty)) {
			undoManager.storeUndoInfo();
		}
	}
}
