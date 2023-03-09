package org.geogebra.common.properties.impl.undo;

import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.DefaultPropertyObserver;

/**
 * Saves an undo point when a property value has changed.
 */
public class UndoSavingPropertyObserver extends DefaultPropertyObserver {

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

    @Override
    public void onEndChange(Property property) {
        if (property instanceof RangeProperty) {
            undoManager.storeUndoInfo();
        }
    }
}
