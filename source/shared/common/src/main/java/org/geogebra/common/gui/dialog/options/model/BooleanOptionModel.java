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

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.main.App;

public abstract class BooleanOptionModel extends OptionsModel {
	private IBooleanOptionListener listener;

	public interface IBooleanOptionListener extends PropertyListener {
		@MissingDoc
		void updateCheckbox(boolean isEqual);
	}

	public BooleanOptionModel(IBooleanOptionListener listener, App app) {
		super(app);
		this.setListener(listener);
	}

	@MissingDoc
	public abstract boolean getValueAt(int index);

	@MissingDoc
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

	@MissingDoc
	public abstract String getTitle();
}
