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

package org.geogebra.common.properties.impl.algebra;

import static java.util.Map.entry;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.DEPENDENCY;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.LAYER;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.ORDER;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.TYPE;

import java.util.List;

import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for setting sort by in Algebra view.
 */
public class SortByProperty extends AbstractNamedEnumeratedProperty<AlgebraView.SortMode> {

	private AlgebraSettings algebraSettings;

	/**
	 * Constructs a sort by property object.
	 * @param algebraSettings algebra view
	 * @param localization localization
	 */
	public SortByProperty(AlgebraSettings algebraSettings, Localization localization) {
		super(localization, "SortBy");
		this.algebraSettings = algebraSettings;
		setNamedValues(List.of(
				entry(DEPENDENCY, DEPENDENCY.toString()),
				entry(TYPE, TYPE.toString()),
				entry(ORDER, ORDER.toString()),
				entry(LAYER, LAYER.toString())
		));
	}

	@Override
	public AlgebraView.SortMode getValue() {
		return algebraSettings.getTreeMode();
	}

	@Override
	protected void doSetValue(AlgebraView.SortMode value) {
		algebraSettings.setTreeMode(value);
	}
}
