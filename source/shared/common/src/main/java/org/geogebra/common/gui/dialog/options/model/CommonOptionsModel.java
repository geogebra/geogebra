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

import java.util.List;

import org.geogebra.common.annotation.MissingDoc;
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

	@MissingDoc
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
