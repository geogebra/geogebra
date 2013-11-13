package geogebra.common.gui.dialog.options.model;

public abstract class NumberOptionsModel extends OptionsModel {
	protected abstract void apply(int index, int value);
	protected abstract int getValueAt(int index);

	public void applyChanges(int value) {
		if (!hasGeos()) {
			return;
		}
		
		for (int i = 0; i < getGeosLength(); i++) {
			apply(i, value);
		}
	}
}
