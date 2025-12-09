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
import java.util.TreeSet;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public abstract class MultipleGeosModel extends MultipleOptionsModel {
	private List<String> choices;

	public MultipleGeosModel(App app) {
		super(app);
		choices = new ArrayList<>();
	}

	/**
	 * @param loc
	 *            localization
	 * @return list of points
	 */
	public List<GeoElement> getGeoChoices(Localization loc) {
		TreeSet<GeoElement> points = app.getKernel().getPointSet();
		List<GeoElement> choices2 = new ArrayList<>();
		choices2.add(null);
		int count = 0;
		for (GeoElement p: points) {
			if (++count > MAX_CHOICES) {
				break;
			}
			choices2.add(p);
		}
		return choices2;
	}

	@Override
	public List<String> getChoices(Localization loc) {
		TreeSet<GeoElement> points = app.getKernel().getPointSet();
		choices.clear();
		choices.add("");
		int count = 0;
		for (GeoElement p: points) {
			if (++count > MAX_CHOICES) {
				break;
			}
			choices.add(p.getLabel(StringTemplate.editTemplate));
		}
		return choices;
	}

}
