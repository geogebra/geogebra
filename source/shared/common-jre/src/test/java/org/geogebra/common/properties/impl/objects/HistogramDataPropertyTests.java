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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.facade.AbstractPropertyListFacade;
import org.geogebra.common.properties.impl.objects.HistogramDataPropertyFactory.HistogramInputType;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class HistogramDataPropertyTests extends BaseAppTestSetup {

	@BeforeEach
	void setUpTest() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void initializesHeightsMode() {
		HistogramProperties collection = createCollection(
				"Histogram({0,1,2,3},{2,3,1})");

		assertEquals(HistogramInputType.HEIGHTS, collection.getInputTypeProperty().getValue());
		assertEquals("{0, 1, 2, 3}", collection.getClassBoundariesProperty().getValue());
		assertEquals("{2, 3, 1}", collection.getHeightsProperty().getValue());
	}

	@Test
	void updatesAvailabilityAndEnabledState() {
		HistogramProperties collection = createCollection(
				"Histogram({0,1,2,3},{2,3,1})");

		assertTrue(collection.getHeightsProperty().isAvailable());
		assertFalse(collection.getRawDataProperty().isAvailable());
		assertFalse(collection.getCumulativeProperty().isAvailable());
		assertFalse(collection.getUseDensityProperty().isAvailable());
		assertFalse(collection.getDensityScaleFactorProperty().isAvailable());
		assertFalse(collection.getDensityScaleFactorProperty().isEnabled());

		collection.getRawDataProperty().setValue("{1,1,2,3}");
		collection.getInputTypeProperty().setValue(HistogramInputType.RAW_DATA);

		assertFalse(collection.getHeightsProperty().isAvailable());
		assertTrue(collection.getRawDataProperty().isAvailable());
		assertTrue(collection.getCumulativeProperty().isAvailable());
		assertTrue(collection.getUseDensityProperty().isAvailable());
		assertTrue(collection.getDensityScaleFactorProperty().isAvailable());
		assertFalse(collection.getDensityScaleFactorProperty().isEnabled());

		collection.getUseDensityProperty().setValue(true);
		assertTrue(collection.getDensityScaleFactorProperty().isEnabled());
	}

	@Test
	void initializesRawDataModes() {
		HistogramProperties nonCumulative = createCollection(
				"Histogram({0,1,2,3},{1,1,2,3,3},true,4)");
		assertEquals(HistogramInputType.RAW_DATA,
				nonCumulative.getInputTypeProperty().getValue());
		assertEquals("{1, 1, 2, 3, 3}", nonCumulative.getRawDataProperty().getValue());
		assertFalse(nonCumulative.getCumulativeProperty().getValue());
		assertTrue(nonCumulative.getUseDensityProperty().getValue());
		assertEquals("4", nonCumulative.getDensityScaleFactorProperty().getValue());

		HistogramProperties cumulative = createCollection(
				"Histogram(true,{0,1,2,3},{1,1,2,3,3},false,2)");
		assertTrue(cumulative.getCumulativeProperty().getValue());
		assertFalse(cumulative.getUseDensityProperty().getValue());
		assertEquals("2", cumulative.getDensityScaleFactorProperty().getValue());
	}

	@Test
	void rejectsUnsupportedSelectionsAndLegacyFrequencySyntax() {
		GeoElement point = evaluateGeoElement("(1,2)");
		assertThrows(NotApplicablePropertyException.class,
				() -> createChartData(List.of(point)));

		GeoElement legacy = evaluateGeoElement(
				"Histogram(true,{0,1,2},{1,2},{2,3},false)");
		assertThrows(NotApplicablePropertyException.class,
				() -> createChartData(List.of(legacy)));
	}

	@Test
	void updatesMultipleHistogramsAndKeepsTheirModeDataIndependent() {
		GeoElement first = evaluateGeoElement("Histogram({0,1,2},{2,1})");
		GeoElement second = evaluateGeoElement("Histogram({0,1,2},{1,2})");
		String firstLabel = first.getLabelSimple();
		String secondLabel = second.getLabelSimple();
		HistogramProperties collection = new HistogramProperties(assertDoesNotThrow(
				() -> createChartData(List.of(first, second))));

		collection.getRawDataProperty().setValue("{0.5,1.5}");
		collection.getInputTypeProperty().setValue(HistogramInputType.RAW_DATA);
		assertEquals("Histogram({0, 1, 2}, {0.5, 1.5}, false, 1)",
				getDefinition(firstLabel));
		assertEquals("Histogram({0, 1, 2}, {0.5, 1.5}, false, 1)",
				getDefinition(secondLabel));
		assertSame(lookup(firstLabel), collection.getProperty(0, 0).getGeoElement());
		assertSame(lookup(secondLabel), collection.getProperty(0, 1).getGeoElement());

		collection.getInputTypeProperty().setValue(HistogramInputType.HEIGHTS);
		assertEquals("Histogram({0, 1, 2}, {2, 1})", getDefinition(firstLabel));
		assertEquals("Histogram({0, 1, 2}, {1, 2})", getDefinition(secondLabel));
	}

	@Test
	void validatesMathInputs() {
		HistogramProperties collection = createCollection(
				"Histogram({0,1,2,3},{2,3,1})");

		assertNull(collection.getClassBoundariesProperty().validateValue("{0,2,4}"));
		assertNull(collection.getHeightsProperty().validateValue("{1,2}"));
		assertNotNull(collection.getRawDataProperty().validateValue("x + 1"));
		assertNull(collection.getDensityScaleFactorProperty().validateValue("sqrt(2)"));
		assertNotNull(collection.getDensityScaleFactorProperty().validateValue("{1,2}"));
	}

	@Test
	void rewritesCommandsAndRemembersBothInputModes() {
		GeoElement histogram = evaluateGeoElement("Histogram({0,1,2,3},{2,3,1})");
		HistogramProperties collection = createCollection(histogram);
		String label = histogram.getLabelSimple();

		collection.getRawDataProperty().setValue("{1,1,2,3,3}");
		collection.getInputTypeProperty().setValue(HistogramInputType.RAW_DATA);
		assertEquals("Histogram({0, 1, 2, 3}, {1, 1, 2, 3, 3}, false, 1)",
				getDefinition(label));
		assertFalse(collection.getCumulativeProperty().getValue());
		assertFalse(collection.getUseDensityProperty().getValue());
		assertFalse(collection.getDensityScaleFactorProperty().isEnabled());

		collection.getClassBoundariesProperty().setValue("{0,2,4,6}");
		collection.getUseDensityProperty().setValue(true);
		assertTrue(collection.getDensityScaleFactorProperty().isEnabled());
		collection.getDensityScaleFactorProperty().setValue("5");
		collection.getCumulativeProperty().setValue(true);
		assertEquals("Histogram(true, {0, 2, 4, 6}, {1, 1, 2, 3, 3}, true, 5)",
				getDefinition(label));

		collection.getInputTypeProperty().setValue(HistogramInputType.HEIGHTS);
		assertEquals("Histogram({0, 2, 4, 6}, {2, 3, 1})", getDefinition(label));
		collection.getHeightsProperty().setValue("{4,5,6}");

		collection.getInputTypeProperty().setValue(HistogramInputType.RAW_DATA);
		assertEquals("Histogram(true, {0, 2, 4, 6}, {1, 1, 2, 3, 3}, true, 5)",
				getDefinition(label));
		collection.getInputTypeProperty().setValue(HistogramInputType.HEIGHTS);
		assertEquals("Histogram({0, 2, 4, 6}, {4, 5, 6})", getDefinition(label));
	}

	@Test
	void preservesHistogramRightAndUpdatesGeoReferences() {
		GeoElement histogram = evaluateGeoElement("HistogramRight({0,1,2},{2,1})");
		HistogramProperties collection = createCollection(histogram);
		String label = histogram.getLabelSimple();

		collection.getRawDataProperty().setValue("{1,1,2}");
		collection.getInputTypeProperty().setValue(HistogramInputType.RAW_DATA);
		assertEquals("HistogramRight({0, 1, 2}, {1, 1, 2}, false, 1)",
				getDefinition(label));
		assertSame(lookup(label), collection.getFirstProperty(0).getGeoElement());
		assertSame(lookup(label), collection.getFirstProperty(3).getGeoElement());
	}

	private HistogramProperties createCollection(String definition) {
		GeoElement histogram = evaluateGeoElement(definition);
		return createCollection(histogram);
	}

	private HistogramProperties createCollection(GeoElement histogram) {
		return new HistogramProperties(assertDoesNotThrow(
				() -> createChartData(List.of(histogram))));
	}

	private ChartDataPropertyCollection createChartData(List<GeoElement> elements)
			throws NotApplicablePropertyException {
		return new ChartDataPropertyCollection(new GeoElementPropertiesFactory(),
				getAlgebraProcessor(), getLocalization(), elements);
	}

	private String getDefinition(String label) {
		return lookup(label).getDefinition(StringTemplate.testTemplate);
	}

	@SuppressWarnings("unchecked")
	private static final class HistogramProperties {
		private final ChartDataPropertyCollection collection;

		private HistogramProperties(ChartDataPropertyCollection collection) {
			this.collection = collection;
		}

		private NamedEnumeratedProperty<HistogramInputType> getInputTypeProperty() {
			return (NamedEnumeratedProperty<HistogramInputType>) collection.getProperties()[0];
		}

		private StringProperty getClassBoundariesProperty() {
			return (StringProperty) collection.getProperties()[1];
		}

		private StringProperty getHeightsProperty() {
			return (StringProperty) collection.getProperties()[2];
		}

		private StringProperty getRawDataProperty() {
			return (StringProperty) collection.getProperties()[3];
		}

		private BooleanProperty getCumulativeProperty() {
			return (BooleanProperty) collection.getProperties()[4];
		}

		private BooleanProperty getUseDensityProperty() {
			return (BooleanProperty) collection.getProperties()[5];
		}

		private StringProperty getDensityScaleFactorProperty() {
			return (StringProperty) collection.getProperties()[6];
		}

		private GeoElementDependentProperty getFirstProperty(int index) {
			return getProperty(index, 0);
		}

		private GeoElementDependentProperty getProperty(int index, int elementIndex) {
			AbstractPropertyListFacade<?> facade =
					(AbstractPropertyListFacade<?>) collection.getProperties()[index];
			return (GeoElementDependentProperty) facade.getPropertyList().get(elementIndex);
		}
	}
}
