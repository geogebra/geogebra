package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.main.App;

public abstract class BooleanOptionModel extends OptionsModel {
	private IBooleanOptionListener listener;

	public interface IBooleanOptionListener extends PropertyListener {
		void updateCheckbox(boolean isEqual);
	}

	public BooleanOptionModel(IBooleanOptionListener listener, App app) {
		super(app);
		this.setListener(listener);
	}

	public abstract boolean getValueAt(int index);

	public abstract void apply(int index, boolean value);

	@Override
	public void updateProperties() {
		boolean value0 = getValueAt(0);
		boolean isEqual = true;

		for (int i = 0; i < getGeosLength(); i++) {
			if (value0 != getValueAt(i)) {
				isEqual = false;
			}
		}
		getListener().updateCheckbox(isEqual && value0);

	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			apply(i, value);
		}
		storeUndoInfo();
	}

	@Override
	public IBooleanOptionListener getListener() {
		return listener;
	}

	public void setListener(IBooleanOptionListener listener) {
		this.listener = listener;
	}

	public abstract String getTitle();
}
