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

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.main.App;

public abstract class NumberOptionsModel extends OptionsModel {
	public NumberOptionsModel(App app) {
		super(app);
	}

	protected abstract void apply(int index, int value);

	protected abstract int getValueAt(int index);

	public boolean applyChanges(int value) {
		if (applyChangesNoUndo(value)) {
			storeUndoInfo();
			return true;
		}
		return false;
	}

	public boolean applyChangesNoUndo(int value) {
		if (!hasGeos()) {
			return false;
		}

		for (int i = 0; i < getGeosLength(); i++) {
			apply(i, value);
		}
		return true;
	}
}
