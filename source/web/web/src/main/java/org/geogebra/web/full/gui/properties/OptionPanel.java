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

package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PropertyListener;
import org.gwtproject.user.client.ui.Widget;

/**
 * Panel for properties view
 *
 */
public abstract class OptionPanel implements IOptionPanel, PropertyListener {
	private OptionsModel model;
	private Widget widget;

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		getModel().setGeos(geos);
		boolean geosOK = getModel().checkGeos();
		if (widget != null) {
			widget.setVisible(geosOK);
		}

		if (!geosOK || widget == null) {
			return null;
		}
		getModel().updateProperties();
		setLabels();
		return this;
	}

	@Override
	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	@Override
	public OptionsModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            options model
	 */
	public void setModel(OptionsModel model) {
		this.model = model;
	}

	@Override
	public abstract void setLabels();
}
