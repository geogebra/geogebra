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
