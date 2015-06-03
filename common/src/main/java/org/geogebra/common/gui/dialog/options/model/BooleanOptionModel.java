package org.geogebra.common.gui.dialog.options.model;





public abstract class BooleanOptionModel extends OptionsModel {
	public interface IBooleanOptionListener extends PropertyListener {
		void updateCheckbox(boolean isEqual);
	}

	private IBooleanOptionListener listener;
	
	public BooleanOptionModel(IBooleanOptionListener listener) {
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
		getListener().updateCheckbox(isEqual ? value0: false);

	}
	
	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			apply(i, value);
		}
	}	
	
	
	public IBooleanOptionListener getListener() {
		return listener;
	}

	public void setListener(IBooleanOptionListener listener) {
		this.listener = listener;
	}
}

