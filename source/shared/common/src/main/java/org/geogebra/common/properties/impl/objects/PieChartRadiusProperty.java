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
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.statistics.AlgoPieChart;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for controlling the radius of a {@link GeoPieChart}.
 */
public final class PieChartRadiusProperty extends AbstractNumericProperty {

	private final GeoPieChart geoPieChart;
	private final AlgoPieChart algoPieChart;

	/**
	 * @param algebraProcessor the algebra processor
	 * @param localization localization
	 * @param element a {@link GeoPieChart} element
	 * @throws NotApplicablePropertyException if the element is not a {@link GeoPieChart}
	 */
	public PieChartRadiusProperty(AlgebraProcessor algebraProcessor, Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(algebraProcessor, localization, "Radius");
		if (!(element instanceof GeoPieChart geoPieChart
				&& element.getParentAlgorithm() instanceof AlgoPieChart algoPieChart)) {
			throw new NotApplicablePropertyException(element);
		}
		this.geoPieChart = geoPieChart;
		this.algoPieChart = algoPieChart;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		String newRadiusStr = value.getLabel(StringTemplate.editTemplate);
		CommandRedefineHelper.redefineWithParam(geoPieChart, algoPieChart,
                algoPieChart.getRadiusParamIndex(), newRadiusStr,
                geoPieChart.getKernel().getApplication());
	}

	@Override
	protected NumberValue getNumberValue() {
        int radiusParamIndex = algoPieChart.getRadiusParamIndex();
        if (algoPieChart.getInputLength() >= radiusParamIndex + 1
                && algoPieChart.getInput(radiusParamIndex) instanceof NumberValue radiusParameter) {
            return radiusParameter;
        } else {
            return new MyDouble(geoPieChart.getKernel(), geoPieChart.getRadius());
        }
	}
}
