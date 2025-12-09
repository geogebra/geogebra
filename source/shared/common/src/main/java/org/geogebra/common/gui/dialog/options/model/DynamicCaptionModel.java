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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class DynamicCaptionModel extends CommonOptionsModel<String> {

	private final Construction construction;
	private final List<String> choices;
	private final Kernel kernel;

	public DynamicCaptionModel(App app) {
		super(app);
		kernel = app.getKernel();
		construction = kernel.getConstruction();
		choices = new ArrayList<>();
	}

	@Override
	public List<String> getChoices(Localization loc) {
		choices.clear();
		choices.add("");
		for (GeoElement geo: construction.getGeoSetConstructionOrder()) {
			if (geo.isGeoText()) {
				choices.add(geo.getLabelSimple());
			}
		}
		return choices;
	}

	@Override
	protected void apply(int index, String value) {
		GeoElementND geo = getGeoAt(index);
		if (StringUtil.empty(value)) {
			geo.clearDynamicCaption();
		} else {
			GeoText caption = (GeoText) kernel.lookupLabel(value);
			geo.setDynamicCaption(caption);
		}
		geo.updateRepaint();
	}

	@Override
	protected String getValueAt(int index) {
		return choices.get(index);
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return geo.hasDynamicCaption();
	}

	@Override
	public void updateProperties() {
		GeoText caption = getGeoAt(0).getDynamicCaption();
		if (caption == null) {
			return;
		}

		String textLabel = caption.getLabelSimple();
		int index = StringUtil.empty(textLabel) ? 0 :getChoices(app.getLocalization()).indexOf(textLabel);
		getListener().setSelectedIndex(index);
	}
}
