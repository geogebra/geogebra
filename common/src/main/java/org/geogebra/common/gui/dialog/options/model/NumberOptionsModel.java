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
