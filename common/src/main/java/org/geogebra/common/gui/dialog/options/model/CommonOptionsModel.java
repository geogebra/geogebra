package org.geogebra.common.gui.dialog.options.model;

import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public abstract class CommonOptionsModel<T> extends OptionsModel {
	private IComboListener listener;

	public CommonOptionsModel(App app) {
		super(app);
	}

	protected abstract void apply(int index, T value);

	protected abstract T getValueAt(int index);

	public boolean applyChanges(T value) {
		if (!hasGeos()) {
			return false;
		}

		for (int i = 0; i < getGeosLength(); i++) {
			apply(i, value);
		}
		storeUndoInfo();
		return true;
	}

	public abstract List<T> getChoices(Localization loc);


	@Override
	public void updateProperties() {
		T value0 = getValueAt(0);
		listener.setSelectedIndex(getChoices(app.getLocalization()).indexOf(value0));
	}


	@Override
	public IComboListener getListener() {
		return listener;
	}

	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

	public void fillModes(Localization loc) {
		if (listener == null) {
			return;
		}

		for (T item : getChoices(loc)) {
			listener.addItem(item.toString());
		}
	}


}
