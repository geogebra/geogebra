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

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.AlgoPieChart;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

/**
 * {@code Property} responsible for setting the position of the center of a {@link GeoPieChart}.
 */
public final class PieChartCenterPositionProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions {

	private final GeoPieChart geoPieChart;
	private final AlgoPieChart algoPieChart;

	/**
	 * Constructs a pie chart center position property for the given element.
	 * @param localization localization
	 * @param element the element
	 * @throws NotApplicablePropertyException if the element is not a {@link GeoPieChart}
	 */
	public PieChartCenterPositionProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Center");
		if (!(element instanceof GeoPieChart geoPieChart
				&& element.getParentAlgorithm() instanceof AlgoPieChart algoPieChart)) {
			throw new NotApplicablePropertyException(element);
		}
		this.geoPieChart = geoPieChart;
		this.algoPieChart = algoPieChart;
	}

	@Override
	public String getValue() {
		return CommandRedefineHelper.getInputString(
				algoPieChart.getInput(algoPieChart.getCenterParamIndex()).toGeoElement());
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return PositionPropertyCollection.validatePointExpression(geoPieChart.getKernel()
				.getParser(), geoPieChart.getKernel().getLocalization(), value);
	}

	@Override
	protected void doSetValue(String value) {
		CommandRedefineHelper.redefineWithParam(geoPieChart, algoPieChart,
                algoPieChart.getCenterParamIndex(), value,
                geoPieChart.getKernel().getApplication());
	}

	@Override
	public List<String> getSuggestions() {
		return PositionPropertyCollection.getSuggestedPointLabels(geoPieChart.getConstruction());
	}
}
