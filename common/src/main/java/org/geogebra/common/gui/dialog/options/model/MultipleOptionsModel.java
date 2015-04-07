package org.geogebra.common.gui.dialog.options.model;

import java.util.List;

import org.geogebra.common.main.Localization;


public abstract class MultipleOptionsModel extends NumberOptionsModel {
	public static final int MAX_CHOICES = 200;
	private IComboListener listener;
	public MultipleOptionsModel(IComboListener listener) {
		this.listener = listener;
	}
	
	public abstract List<String> getChoiches(Localization loc);
	
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
		
	public void fillModes(Localization loc) {
		for (String item: getChoiches(loc)) {
			getListener().addItem(item);
		}
	}

	public IComboListener getListener() {
		return listener;
	}

	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

}
