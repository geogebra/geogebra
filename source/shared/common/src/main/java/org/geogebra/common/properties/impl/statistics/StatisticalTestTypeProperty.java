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

package org.geogebra.common.properties.impl.statistics;

import static java.util.Map.entry;

import java.util.stream.Collectors;

import org.geogebra.common.gui.view.probcalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for the statistical test type.
 */
public class StatisticalTestTypeProperty extends AbstractNamedEnumeratedProperty<Procedure> {
	private final StatisticsCollection statisticsCollection;

	/**
	 * Constructs an StatisticalTestTypeProperty
	 * @param localization the localization used
	 * @param statisticsCollection statistics collection
	 */
	public StatisticalTestTypeProperty(Localization localization,
			StatisticsCollection statisticsCollection) {
		super(localization, "StatisticsTab.StatisticalTest");
		this.statisticsCollection = statisticsCollection;
		setNamedValues(StatisticsCollection.statisticalTests.stream()
				.map(procedure -> entry(procedure, procedure.getName()))
				.collect(Collectors.toList()));
		setGroupDividerIndices(new int[]{ 6, 12 });
	}

	@Override
	public Procedure getValue() {
		return statisticsCollection.getSelectedProcedure();
	}

	@Override
	protected void doSetValue(Procedure value) {
		if (value != statisticsCollection.getSelectedProcedure()) {
			statisticsCollection.setSelectedProcedure(value);
		}
	}
}
