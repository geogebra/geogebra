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

import static org.geogebra.common.properties.impl.objects.BarChartDataPropertyFactory.BarChartInputType.FREQUENCIES;
import static org.geogebra.common.properties.impl.objects.BarChartDataPropertyFactory.BarChartInputType.HEIGHTS;
import static org.geogebra.common.properties.impl.objects.BarChartDataPropertyFactory.BarChartInputType.RAW_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.BarChartDataPropertyFactory.BarChartInputType;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

final class BarChartDataPropertyTests extends BaseAppTestSetup {
	@BeforeEach
	void setUpTest() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"BarChart({1, 2, 3}, 2)",
				"BarChart({1, 2, 3}, 2, 3)",
				"BarChart({1, 2, 3}, {2, 3, 4})",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2)",
				"BarChart(0, 6, {1, 2, 3})"
			})
	void initializesDataProperty(String command) throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		assertEquals("{1, 2, 3}", dataProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BarChart({1, 2, 3}, 2) -> RAW_DATA",
				"BarChart({1, 2, 3}, 2, 3) -> RAW_DATA",
				"BarChart({1, 2, 3}, {2, 3, 4}) -> FREQUENCIES",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2) -> FREQUENCIES",
				"BarChart(0, 6, {1, 2, 3}) -> HEIGHTS"
			})
	void initializesInputTypeProperty(String command, BarChartInputType expected)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				createPropertyFactory(barChart).createInputTypeProperty(barChart);

		assertEquals(expected, inputTypeProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BarChart({1, 2, 3}, 2) -> ListOfRawData",
				"BarChart({1, 2, 3}, 2, 3) -> ListOfRawData",
				"BarChart({1, 2, 3}, {2, 3, 4}) -> ListOfData",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2) -> ListOfData",
				"BarChart(0, 6, {1, 2, 3}) -> ListOfHeights"
			})
	void dataPropertyNameMatchesInputType(String command, String key)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		assertEquals(getLocalization().getMenu(key), dataProperty.getName());
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"BarChart({1, 2, 3}, 2);false;false",
				"BarChart({1, 2, 3}, 2, 3);false;false",
				"BarChart({1, 2, 3}, {2, 3, 4});true;false",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2);true;false",
				"BarChart(0, 6, {1, 2, 3});false;true"
			})
	void propertyAvailabilityMatchesInputType(
			String command, boolean frequenciesAvailable, boolean boundsAvailable)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		BarChartDataPropertyFactory factory = createPropertyFactory(barChart);
		StringProperty frequenciesProperty = factory.createFrequenciesProperty(barChart);
		StringProperty startProperty = factory.createBoundProperty(barChart, true);
		StringProperty endProperty = factory.createBoundProperty(barChart, false);

		assertEquals(frequenciesAvailable, frequenciesProperty.isAvailable());
		assertEquals(boundsAvailable, startProperty.isAvailable());
		assertEquals(boundsAvailable, endProperty.isAvailable());
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"BarChart(0, 4, k^2, k, 1, 4)",
				"BarChart(0, 4, k^2, k, 1, 4, 1)",
				"BarChart(0, 4, Sequence(k^2, k, 1, 4))"
			})
	void supportsSequenceBasedHeights(String command) throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		ChartDataPropertyCollection collection = new ChartDataPropertyCollection(
				new GeoElementPropertiesFactory(),
				getAlgebraProcessor(),
				getLocalization(),
				List.of(barChart));

		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				createPropertyFactory(barChart).createInputTypeProperty(barChart);

		assertEquals(HEIGHTS, inputTypeProperty.getValue());
		assertEquals(4, collection.getProperties().length);
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BarChart({1, 2, 3}, 2) -> BarChart({4, 5, 6}, 2)",
				"BarChart({1, 2, 3}, 2, 3) -> BarChart({4, 5, 6}, 2, 3)",
				"BarChart({1, 2, 3}, {2, 3, 4}) -> BarChart({4, 5, 6}, {2, 3, 4})",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2) -> BarChart({4, 5, 6}, {2, 3, 4}, 2)",
				"BarChart(0, 6, {1, 2, 3}) -> BarChart(0, 6, {4, 5, 6})"
			})
	void editingDataPreservesOtherArguments(String command, String expected)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		dataProperty.setValue("{4, 5, 6}");

		assertEquals(expected, definition(barChart.getLabelSimple()));
	}

	@Test
	void editingFrequenciesPreservesWidth() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4}, 2)");
		StringProperty frequenciesProperty =
				createPropertyFactory(barChart).createFrequenciesProperty(barChart);

		frequenciesProperty.setValue("{4, 5, 6}");

		assertEquals("BarChart({1, 2, 3}, {4, 5, 6}, 2)", definition(barChart.getLabelSimple()));
	}

	@Test
	void editingFrequenciesPreservesOmittedWidth() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty frequenciesProperty =
				createPropertyFactory(barChart).createFrequenciesProperty(barChart);

		frequenciesProperty.setValue("{4, 5, 6}");

		assertEquals("BarChart({1, 2, 3}, {4, 5, 6})", definition(barChart.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"BarChart({1, 2, 3}, 2);RAW_DATA;FREQUENCIES;BarChart({1, 2, 3}, 1)",
				"BarChart({1, 2, 3}, 2);RAW_DATA;HEIGHTS;BarChart({1, 2, 3}, 1)",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2);FREQUENCIES;RAW_DATA;BarChart({1, 2, 3}, {2, 3, 4}, 1)",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2);FREQUENCIES;HEIGHTS;BarChart({1, 2, 3}, {2, 3, 4}, 1)"
			})
	void switchingInputTypeDoesNotRememberWidth(
			String command, BarChartInputType original, BarChartInputType intermediate, String expected)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				createPropertyFactory(barChart).createInputTypeProperty(barChart);

		inputTypeProperty.setValue(intermediate);
		inputTypeProperty.setValue(original);

		assertEquals(expected, definition(barChart.getLabelSimple()));
	}

	@ParameterizedTest
	@EnumSource(
			value = BarChartInputType.class,
			names = {"FREQUENCIES", "HEIGHTS"})
	void switchingInputTypeDoesNotRememberScale(BarChartInputType intermediate)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, 1, 3)");
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				createPropertyFactory(barChart).createInputTypeProperty(barChart);

		inputTypeProperty.setValue(intermediate);
		inputTypeProperty.setValue(RAW_DATA);

		assertEquals("BarChart({1, 2, 3}, 1)", definition(barChart.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BarChart({1, 2, 3}, 2) -> RAW_DATA",
				"BarChart({1, 2, 3}, 2, 3) -> RAW_DATA",
				"BarChart({1, 2, 3}, {2, 3, 4}) -> FREQUENCIES",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2) -> FREQUENCIES",
				"BarChart(0, 6, {1, 2, 3}) -> HEIGHTS"
			})
	void selectingCurrentInputTypePreservesArguments(String command, BarChartInputType current)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				createPropertyFactory(barChart).createInputTypeProperty(barChart);

		inputTypeProperty.setValue(current);

		assertEquals(command, definition(barChart.getLabelSimple()));
	}

	@Test
	void editingStartPreservesOtherArguments() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart(0, 6, {1, 2, 3})");
		StringProperty startProperty =
				createPropertyFactory(barChart).createBoundProperty(barChart, true);

		startProperty.setValue("-2");

		assertEquals("BarChart(-2, 6, {1, 2, 3})", definition(barChart.getLabelSimple()));
	}

	@Test
	void editingEndPreservesOtherArguments() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart(0, 6, {1, 2, 3})");
		StringProperty endProperty =
				createPropertyFactory(barChart).createBoundProperty(barChart, false);

		endProperty.setValue("8");

		assertEquals("BarChart(0, 8, {1, 2, 3})", definition(barChart.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"BarChart({1, 2, 3}, 2);FREQUENCIES;BarChart({1, 2, 3}, {}, 1)",
				"BarChart({1, 2, 3}, {2, 3, 4}, 2);RAW_DATA;BarChart({1, 2, 3}, 1)",
				"BarChart(0, 6, {1, 2, 3});RAW_DATA;BarChart({1, 2, 3}, 1)",
				"BarChart(0, 6, {1, 2, 3});FREQUENCIES;BarChart({1, 2, 3}, {}, 1)"
			})
	void switchingInputTypeUsesDefaultWidth(String command, BarChartInputType target, String expected)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				createPropertyFactory(barChart).createInputTypeProperty(barChart);

		inputTypeProperty.setValue(target);

		assertEquals(expected, definition(barChart.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"BarChart({1, 2, 3}, 2);0;4",
				"BarChart({1, 4, 8}, {2, 3, 4}, 2);0;9",
				"BarChart({8, 1, 4}, {2, 3, 4}, 2);0;9",
				"BarChart({1, 2, 3}, {});0;1"
			})
	void switchingToHeightsCalculatesBounds(String command, String start, String end)
			throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement(command);
		BarChartDataPropertyFactory factory = createPropertyFactory(barChart);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				factory.createInputTypeProperty(barChart);

		inputTypeProperty.setValue(HEIGHTS);

		assertEquals(start, factory.createBoundProperty(barChart, true).getValue());
		assertEquals(end, factory.createBoundProperty(barChart, false).getValue());
	}

	@Test
	void switchingInputTypePreservesEditedData() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		BarChartDataPropertyFactory factory = createPropertyFactory(barChart);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				factory.createInputTypeProperty(barChart);
		StringProperty dataProperty = factory.createDataProperty(barChart);

		dataProperty.setValue("{4, 5, 6}");
		inputTypeProperty.setValue(RAW_DATA);
		assertEquals("{4, 5, 6}", dataProperty.getValue());
		inputTypeProperty.setValue(HEIGHTS);
		assertEquals("{4, 5, 6}", dataProperty.getValue());
		inputTypeProperty.setValue(FREQUENCIES);
		assertEquals("{4, 5, 6}", dataProperty.getValue());
	}

	@Test
	void frequenciesSurviveOtherModesAndDataEdits() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		BarChartDataPropertyFactory factory = createPropertyFactory(barChart);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				factory.createInputTypeProperty(barChart);
		StringProperty dataProperty = factory.createDataProperty(barChart);
		StringProperty frequenciesProperty = factory.createFrequenciesProperty(barChart);

		frequenciesProperty.setValue("{4, 5, 6}");

		inputTypeProperty.setValue(RAW_DATA);
		dataProperty.setValue("{2, 3, 4}");
		inputTypeProperty.setValue(HEIGHTS);
		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("{4, 5, 6}", frequenciesProperty.getValue());
		assertEquals("BarChart({2, 3, 4}, {4, 5, 6}, 1)", definition(barChart.getLabelSimple()));
	}

	@Test
	void editedBoundsSurviveOtherModes() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart(0, 6, {1, 2, 3})");
		BarChartDataPropertyFactory factory = createPropertyFactory(barChart);
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				factory.createInputTypeProperty(barChart);
		StringProperty startProperty = factory.createBoundProperty(barChart, true);
		StringProperty endProperty = factory.createBoundProperty(barChart, false);

		startProperty.setValue("-2");
		endProperty.setValue("8");

		inputTypeProperty.setValue(FREQUENCIES);
		inputTypeProperty.setValue(RAW_DATA);
		inputTypeProperty.setValue(HEIGHTS);

		assertEquals("BarChart(-2, 8, {1, 2, 3})", definition(barChart.getLabelSimple()));
	}

	@Test
	void editingPreservesNamedArguments() throws NotApplicablePropertyException {
		evaluateGeoElement("values={1, 2, 3}");
		evaluateGeoElement("width=2");
		evaluateGeoElement("scale=3");
		GeoElement barChart = evaluateGeoElement("BarChart(values, width, scale)");
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		dataProperty.setValue("{4, 5, 6}");

		assertEquals("BarChart({4, 5, 6}, width, scale)", definition(barChart.getLabelSimple()));
	}

	@Test
	void changingCommandUpdatesFrequenciesProperty() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty frequenciesProperty =
				createPropertyFactory(barChart).createFrequenciesProperty(barChart);

		editGeoElement(barChart, "BarChart({1, 2, 3}, {4, 5, 6})");

		assertEquals("{4, 5, 6}", frequenciesProperty.getValue());
	}

	@Test
	void changingCommandUpdatesBoundProperties() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart(0, 6, {1, 2, 3})");
		BarChartDataPropertyFactory factory = createPropertyFactory(barChart);
		StringProperty startProperty = factory.createBoundProperty(barChart, true);
		StringProperty endProperty = factory.createBoundProperty(barChart, false);

		editGeoElement(barChart, "BarChart(-2, 8, {1, 2, 3})");

		assertEquals("-2", startProperty.getValue());
		assertEquals("8", endProperty.getValue());
	}

	@ParameterizedTest
	@ValueSource(strings = {"{1, 2, 3}", "values", "Sequence(k, k, 1, 3)"})
	void dataPropertyAcceptsValidLists(String value) throws NotApplicablePropertyException {
		evaluateGeoElement("values={1, 2, 3}");
		GeoElement barChart = evaluateGeoElement("BarChart(values, {2, 3, 4})");
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		assertNull(dataProperty.validateValue(value));
	}

	@Test
	void dataPropertyAcceptsEmptyInput() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		assertNull(dataProperty.validateValue(""));
	}

	@ParameterizedTest
	@ValueSource(strings = {"x+1", "2"})
	void dataPropertyRejectsNonLists(String value) throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		assertNotNull(dataProperty.validateValue(value));
	}

	@Test
	void dataPropertyUsesMathFormat() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty dataProperty = createPropertyFactory(barChart).createDataProperty(barChart);

		assertTrue(dataProperty.isDisplayedInMathFormat());
	}

	@ParameterizedTest
	@ValueSource(strings = {"{1, 2, 3}", "values", "{}"})
	void frequenciesPropertyAcceptsValidLists(String value) throws NotApplicablePropertyException {
		evaluateGeoElement("values={1, 2, 3}");
		GeoElement barChart = evaluateGeoElement("BarChart(values, {2, 3, 4})");
		StringProperty frequenciesProperty =
				createPropertyFactory(barChart).createFrequenciesProperty(barChart);

		assertNull(frequenciesProperty.validateValue(value));
	}

	@Test
	void frequenciesPropertyAcceptsEmptyInput() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty frequenciesProperty =
				createPropertyFactory(barChart).createFrequenciesProperty(barChart);

		assertNull(frequenciesProperty.validateValue(""));
	}

	@ParameterizedTest
	@ValueSource(strings = {"x+1", "2"})
	void frequenciesPropertyRejectsNonLists(String value) throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty frequenciesProperty =
				createPropertyFactory(barChart).createFrequenciesProperty(barChart);

		assertNotNull(frequenciesProperty.validateValue(value));
	}

	@Test
	void frequenciesPropertyUsesMathFormat() throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		StringProperty frequenciesProperty =
				createPropertyFactory(barChart).createFrequenciesProperty(barChart);

		assertTrue(frequenciesProperty.isDisplayedInMathFormat());
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void boundPropertiesAcceptNumericExpressions(boolean start)
			throws NotApplicablePropertyException {
		evaluateGeoElement("bound=2");
		GeoElement barChart = evaluateGeoElement("BarChart(0, 6, {1, 2, 3})");
		StringProperty boundProperty =
				createPropertyFactory(barChart).createBoundProperty(barChart, start);

		assertNull(boundProperty.validateValue("bound"));
		assertNull(boundProperty.validateValue("1/2"));
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void boundPropertiesRejectNonNumbers(boolean start) throws NotApplicablePropertyException {
		GeoElement barChart = evaluateGeoElement("BarChart(0, 6, {1, 2, 3})");
		StringProperty boundProperty =
				createPropertyFactory(barChart).createBoundProperty(barChart, start);

		assertNotNull(boundProperty.validateValue("{1, 2}"));
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"(1, 2)",
				"StickGraph({1, 2}, {2, 3})",
				"StepGraph({1, 2}, {2, 3})",
				"BinomialDist(4, 0.5, false)"
			})
	void rejectsUnrelatedObjects(String command) {
		GeoElement geo = evaluateGeoElement(command);

		assertThrows(
				NotApplicablePropertyException.class,
				() -> createPropertyFactory(geo).createDataProperty(geo));
	}

	@Test
	@SuppressWarnings("unchecked")
	void selectedBarChartsRememberFrequenciesIndependently() throws NotApplicablePropertyException {
		GeoElement first = evaluateGeoElement("BarChart({1, 2, 3}, {2, 3, 4})");
		GeoElement second = evaluateGeoElement("BarChart({1, 2, 3}, {4, 3, 2})");
		ChartDataPropertyCollection collection = new ChartDataPropertyCollection(
				new GeoElementPropertiesFactory(),
				getAlgebraProcessor(),
				getLocalization(),
				List.of(first, second));
		NamedEnumeratedProperty<BarChartInputType> inputTypeProperty =
				(NamedEnumeratedProperty<BarChartInputType>) collection.getProperties()[0];

		inputTypeProperty.setValue(RAW_DATA);
		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("BarChart({1, 2, 3}, {2, 3, 4}, 1)", definition(first.getLabelSimple()));
		assertEquals("BarChart({1, 2, 3}, {4, 3, 2}, 1)", definition(second.getLabelSimple()));
	}

	private BarChartDataPropertyFactory createPropertyFactory(GeoElement... elements) {
		return new BarChartDataPropertyFactory(getLocalization(), List.of(elements));
	}

	private String definition(String label) {
		return lookup(label).getDefinition(StringTemplate.testTemplate);
	}
}
