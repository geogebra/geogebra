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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoPieChart;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.Nullable;

/**
 * Property for the list of frequencies for a pie chart.
 */
public class PieChartFrequenciesProperty extends AbstractValuedProperty<String>
		implements StringProperty {

	private final GeoPieChart pieChart;
	private final AlgoPieChart algoPieChart;

	/**
	 * @param localization localization
	 * @param geoElement An element
	 * @throws NotApplicablePropertyException If {@code element} is not a {@link GeoPieChart}
	 * created from an {@link AlgoPieChart}.
	 */
	public PieChartFrequenciesProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "ListOfFrequencies");
		if (!(geoElement instanceof GeoPieChart pieChart
				&& pieChart.getParentAlgorithm() instanceof AlgoPieChart algoPieChart)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.pieChart = pieChart;
		this.algoPieChart = algoPieChart;
	}

	@Override
	public @Nullable String getValue() {
		return algoPieChart.getInput(algoPieChart.getFrequenciesParamIndex()).toGeoElement()
				.getLabel(StringTemplate.editorTemplate);
	}

	@Override
	public @Nullable String validateValue(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		GeoList list = algoPieChart.getKernel().getAlgebraProcessor().evaluateToList(value);
		if (list != null) {
			return null;
		}
		return algoPieChart.getKernel().getLocalization().getError("InvalidInput");
	}

	@Override
	protected void doSetValue(String value) {
		CommandRedefineHelper.redefineWithParam(pieChart, algoPieChart,
				algoPieChart.getFrequenciesParamIndex(),
				value, algoPieChart.getKernel().getApplication());
	}

	@Override
	public boolean isDisplayedInMathFormat() {
		return true;
	}
}
