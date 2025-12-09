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

import org.geogebra.common.main.App;

public abstract class SliderOptionsModel extends NumberOptionsModel {
	private ISliderListener listener;

	public SliderOptionsModel(App app) {
		super(app);
	}

	@Override
	public void updateProperties() {
		getListener().setValue(getValueAt(0));

	}

	@Override
	public ISliderListener getListener() {
		return listener;
	}

	public void setListener(ISliderListener listener) {
		this.listener = listener;
	}

}