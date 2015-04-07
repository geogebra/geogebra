package org.geogebra.common.gui.dialog.options.model;

public abstract class NumberOptionsModel extends OptionsModel {
	protected abstract void apply(int index, int value);
	protected abstract int getValueAt(int index);

	public boolean applyChanges(int value) {
		if (!hasGeos()) {
			return false;
		}
		
		for (int i = 0; i < getGeosLength(); i++) {
			apply(i, value);
		}
		
		return true;
	}
}
