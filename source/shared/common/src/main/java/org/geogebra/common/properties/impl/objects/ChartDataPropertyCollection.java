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
import java.util.Objects;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * A property collection containing the data coordinate properties (x- and y-lists) for chart
 * elements such as `LineGraph`.
 */
public class ChartDataPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates chart data properties using session-owned temporary data.
	 * @param propertiesFactory property facade factory
	 * @param localization localization
	 * @param elements selected elements
	 * @param context temporary data owned by the settings session
	 * @throws NotApplicablePropertyException if no data properties apply
	 */
	public ChartDataPropertyCollection(
			GeoElementPropertiesFactory propertiesFactory,
			Localization localization,
			List<GeoElement> elements,
			ObjectSettingsSession.Context context)
			throws NotApplicablePropertyException {
		super(localization, "Data");
		HistogramDataPropertyFactory histogramFactory =
				new HistogramDataPropertyFactory(localization, elements, context);
		BoxPlotDataPropertyFactory boxPlotFactory =
				new BoxPlotDataPropertyFactory(localization, elements, context);
		BarChartDataPropertyFactory barChartFactory =
				new BarChartDataPropertyFactory(localization, elements, context);
		setProperties(Stream.of(
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								boxPlotFactory::createInputTypeProperty,
								NamedEnumeratedPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements, boxPlotFactory::createDataProperty, StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements, boxPlotFactory::createFrequenciesProperty, StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								boxPlotFactory::createShowOutliersProperty,
								BooleanPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								barChartFactory::createInputTypeProperty,
								NamedEnumeratedPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements, barChartFactory::createDataProperty, StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								barChartFactory::createFrequenciesProperty,
								StringPropertyListFacade::new),
						barChartFactory.createBoundsPropertyCollection(propertiesFactory, elements),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								histogramFactory::createInputTypeProperty,
								NamedEnumeratedPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								histogramFactory::createClassBoundariesProperty,
								StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements, histogramFactory::createHeightsProperty, StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements, histogramFactory::createRawDataProperty, StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								histogramFactory::createCumulativeProperty,
								BooleanPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								histogramFactory::createUseDensityProperty,
								BooleanPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								histogramFactory::createDensityScaleFactorProperty,
								StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new LineGraphCoordinatesProperty(
										localization, element, LineGraphCoordinatesProperty.Axis.X),
								StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new LineGraphCoordinatesProperty(
										localization, element, LineGraphCoordinatesProperty.Axis.Y),
								StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new PieChartFrequenciesProperty(localization, element),
								StringPropertyListFacade::new))
				.filter(Objects::nonNull)
				.toArray(Property[]::new));
		if (getProperties().length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
	}
}
