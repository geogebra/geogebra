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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public abstract class MultipleOptionsModel extends NumberOptionsModel {
	public static final int MAX_CHOICES = 200;
	private IComboListener listener;

	public MultipleOptionsModel(App app) {
		super(app);
	}

	@MissingDoc
	public abstract List<String> getChoices(Localization loc);

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
		if (getListener() == null) {
			return;
		}

		if (getListener() instanceof GeoComboListener
				&& this instanceof MultipleGeosModel) {
			for (GeoElement geo : ((MultipleGeosModel) this)
					.getGeoChoices(loc)) {
				((GeoComboListener) getListener()).addItem(geo);
			}
			return;
		}
		for (String item : getChoices(loc)) {
			getListener().addItem(item);
		}
	}

	@Override
	public IComboListener getListener() {
		return listener;
	}

	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

	public abstract String getTitle();
}
