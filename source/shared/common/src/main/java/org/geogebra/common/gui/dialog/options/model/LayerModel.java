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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;

public class LayerModel extends MultipleOptionsModel {

	public LayerModel(App app) {
		super(app);
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isDrawable();
	}

	@Override
	public List<String> getChoices(Localization loc) {
		List<String> choices = new ArrayList<>();
		for (int layer = 0; layer <= EuclidianStyleConstants.MAX_LAYERS; ++layer) {
			choices.add(" " + layer);
		}
		return choices;
	}

	@Override
	protected void apply(int index, int value) {
		GeoElement geo = getGeoAt(index);
		geo.setLayer(value);
		geo.updateVisualStyleRepaint(GProperty.LAYER);
	}

	@Override
	public int getValueAt(int index) {
		return getGeoAt(index).getLayer();
	}

	@Override
	public String getTitle() {
		return "Layer";
	}
}
