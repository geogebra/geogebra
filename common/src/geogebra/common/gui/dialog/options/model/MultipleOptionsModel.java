package geogebra.common.gui.dialog.options.model;

import geogebra.common.main.Localization;

import java.util.List;


public abstract class MultipleOptionsModel extends OptionsModel {
	private IComboListener listener;
	public MultipleOptionsModel(IComboListener listener) {
		this.listener = listener;
	}

	public abstract int getValueAt(int index);
	
	@Override
	public void updateProperties() {
		int value0 = getValueAt(0);
		boolean isEqual = true;

		for (int i = 0; i < getGeosLength(); i++) {
			if (value0 != getValueAt(i)) {
				isEqual = false;
			}
		}

		listener.setSelectedIndex(isEqual ? value0 : -1);

	}
	
	public abstract List<String> getChoiches(Localization loc);
	
	public void fillModes(Localization loc) {
		for (String item: getChoiches(loc)) {
			getListener().addItem(item);
		}
	}

	protected abstract void apply(int index, int value);

	public void applyChanges(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			apply(i, value);
		}
	
	}

	public IComboListener getListener() {
		return listener;
	}

	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

}
