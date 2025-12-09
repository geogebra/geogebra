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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;

public class GroupModel extends OptionsModel {
	private List<OptionsModel> models = new ArrayList<>();
	private PropertyListener listener;

	public GroupModel(App app) {
		super(app);
		listener = new PropertyListener() {

			@Override
			public Object updatePanel(Object[] geos2) {
				boolean enabled = false;
				for (OptionsModel model : models) {
					enabled = model.updateMPanel(geos2) || enabled;
				}
				return enabled ? this : null;
			}
		};
	}

	@Override
	protected boolean isValidAt(int index) {
		for (OptionsModel model : models) {
			if (model.isValidAt(index)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateProperties() {
		for (OptionsModel model : models) {
			model.updateProperties();
		}

	}

	public void add(OptionsModel model) {
		models.add(model);
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

}
