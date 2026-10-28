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

import static org.geogebra.common.properties.impl.objects.BoxPlotDataPropertyFactory.BoxPlotInputType.FREQUENCIES;
import static org.geogebra.common.properties.impl.objects.BoxPlotDataPropertyFactory.BoxPlotInputType.RAW_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.BoxPlotDataPropertyFactory.BoxPlotInputType;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

final class BoxPlotDataPropertyTests extends BaseAppTestSetup {
	@BeforeEach
	void setUpTest() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@ParameterizedTest
	@ValueSource(strings = {"BoxPlot(1, 2, {1, 2, 3})", "BoxPlot(1, 2, {1, 2, 3}, true)"})
	void initializesRawDataProperty(String command) throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(boxPlot).createDataProperty(boxPlot);

		assertEquals("{1, 2, 3}", dataProperty.getValue());
	}

	@Test
	void initializesFrequenciesProperty() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		StringProperty frequenciesProperty =
				createPropertyFactory(boxPlot).createFrequenciesProperty(boxPlot);

		assertEquals("{2, 3, 4}", frequenciesProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}) -> RAW_DATA",
				"BoxPlot(1, 2, {1, 2, 3}, true) -> RAW_DATA",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false) -> FREQUENCIES"
			})
	void initializesInputTypeProperty(String command, BoxPlotInputType expected)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				createPropertyFactory(boxPlot).createInputTypeProperty(boxPlot);

		assertEquals(expected, inputTypeProperty.getValue());
	}

	@Test
	void defaultsShowOutliersToFalse() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3})");
		BooleanProperty showOutliersProperty =
				createPropertyFactory(boxPlot).createShowOutliersProperty(boxPlot);

		assertFalse(showOutliersProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}, true) -> true",
				"BoxPlot(1, 2, {1, 2, 3}, false) -> false",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true) -> true",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false) -> false"
			})
	void initializesShowOutliersProperty(String command, boolean expected)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		BooleanProperty showOutliersProperty =
				createPropertyFactory(boxPlot).createShowOutliersProperty(boxPlot);

		assertEquals(expected, showOutliersProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}) -> BoxPlot(1, 2, {4, 5, 6})",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true) -> BoxPlot(1, 2, {4, 5, 6}, {2, 3, 4}, true)"
			})
	void changingDataUpdatesCommand(String command, String expected)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(boxPlot).createDataProperty(boxPlot);

		dataProperty.setValue("{4, 5, 6}");

		assertEquals(expected, definition(boxPlot.getLabelSimple()));
	}

	@Test
	void changingFrequenciesUpdatesCommand() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		StringProperty frequenciesProperty =
				createPropertyFactory(boxPlot).createFrequenciesProperty(boxPlot);

		frequenciesProperty.setValue("{4, 5, 6}");

		assertEquals(
				"BoxPlot(1, 2, {1, 2, 3}, {4, 5, 6}, false)", definition(boxPlot.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"BoxPlot(1, 2, {1, 2, 3}, false);true;BoxPlot(1, 2, {1, 2, 3}, true)",
				"BoxPlot(1, 2, {1, 2, 3}, true);false;BoxPlot(1, 2, {1, 2, 3}, false)",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false);true;BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true)",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true);false;BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)"
			})
	void changingShowOutliersUpdatesCommand(String command, boolean value, String expected)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		BooleanProperty showOutliersProperty =
				createPropertyFactory(boxPlot).createShowOutliersProperty(boxPlot);

		showOutliersProperty.setValue(value);

		assertEquals(expected, definition(boxPlot.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}) -> BoxPlot(1, 2, {4, 5, 6})",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true) -> BoxPlot(1, 2, {4, 5, 6}, {2, 3, 4}, true)"
			})
	void changingCommandUpdatesDataProperty(String command, String updatedCommand)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(boxPlot).createDataProperty(boxPlot);

		editGeoElement(boxPlot, updatedCommand);

		assertEquals("{4, 5, 6}", dataProperty.getValue());
	}

	@Test
	void changingCommandUpdatesFrequenciesProperty() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		StringProperty frequenciesProperty =
				createPropertyFactory(boxPlot).createFrequenciesProperty(boxPlot);

		editGeoElement(boxPlot, "BoxPlot(1, 2, {1, 2, 3}, {4, 5, 6}, false)");

		assertEquals("{4, 5, 6}", frequenciesProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"BoxPlot(1, 2, {1, 2, 3}, false);BoxPlot(1, 2, {1, 2, 3}, true);true",
				"BoxPlot(1, 2, {1, 2, 3}, true);BoxPlot(1, 2, {1, 2, 3}, false);false",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false);BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true);true",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true);BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false);false"
			})
	void changingCommandUpdatesShowOutliersProperty(
			String command, String updatedCommand, boolean expected)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		BooleanProperty showOutliersProperty =
				createPropertyFactory(boxPlot).createShowOutliersProperty(boxPlot);

		editGeoElement(boxPlot, updatedCommand);

		assertEquals(expected, showOutliersProperty.getValue());
	}

	@Test
	void switchingToFrequenciesAddsEmptyFrequencies() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3})");
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				createPropertyFactory(boxPlot).createInputTypeProperty(boxPlot);

		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("BoxPlot(1, 2, {1, 2, 3}, {}, false)", definition(boxPlot.getLabelSimple()));
		assertFalse(lookup(boxPlot.getLabelSimple()).isDefined());
	}

	@Test
	void switchingToRawDataRemovesFrequenciesArgument() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true)");
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				createPropertyFactory(boxPlot).createInputTypeProperty(boxPlot);

		inputTypeProperty.setValue(RAW_DATA);

		assertEquals("BoxPlot(1, 2, {1, 2, 3}, true)", definition(boxPlot.getLabelSimple()));
	}

	@Test
	void settingFrequenciesDefinesInitiallyUndefinedPlot() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {}, false)");
		StringProperty frequenciesProperty =
				createPropertyFactory(boxPlot).createFrequenciesProperty(boxPlot);

		frequenciesProperty.setValue("{2, 3, 4}");

		assertTrue(lookup(boxPlot.getLabelSimple()).isDefined());
		assertEquals(
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)", definition(boxPlot.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}) -> FREQUENCIES",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false) -> RAW_DATA"
			})
	void switchingInputTypePreservesData(String command, BoxPlotInputType target)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		StringProperty dataProperty = factory.createDataProperty(boxPlot);

		factory.createInputTypeProperty(boxPlot).setValue(target);

		assertEquals("{1, 2, 3}", dataProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"BoxPlot(1, 2, {1, 2, 3}, true);FREQUENCIES;true",
				"BoxPlot(1, 2, {1, 2, 3}, false);FREQUENCIES;false",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, true);RAW_DATA;true",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false);RAW_DATA;false"
			})
	void switchingInputTypePreservesShowOutliers(
			String command, BoxPlotInputType target, boolean expected)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		BooleanProperty showOutliersProperty = factory.createShowOutliersProperty(boxPlot);

		factory.createInputTypeProperty(boxPlot).setValue(target);

		assertEquals(expected, showOutliersProperty.getValue());
	}

	@Test
	void frequenciesSurviveRawDataMode() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				factory.createInputTypeProperty(boxPlot);
		StringProperty frequenciesProperty = factory.createFrequenciesProperty(boxPlot);

		inputTypeProperty.setValue(RAW_DATA);
		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("{2, 3, 4}", frequenciesProperty.getValue());
	}

	@Test
	void frequenciesSurviveExternalSwitchToRawDataMode() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		StringProperty frequenciesProperty = factory.createFrequenciesProperty(boxPlot);
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				factory.createInputTypeProperty(boxPlot);
		assertEquals("{2, 3, 4}", frequenciesProperty.getValue());

		editGeoElement(boxPlot, "BoxPlot(1, 2, {1, 2, 3}, false)");
		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("{2, 3, 4}", frequenciesProperty.getValue());
	}

	@Test
	void editedFrequenciesSurviveRawDataMode() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		StringProperty frequenciesProperty = factory.createFrequenciesProperty(boxPlot);
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				factory.createInputTypeProperty(boxPlot);
		frequenciesProperty.setValue("{4, 5, 6}");

		inputTypeProperty.setValue(RAW_DATA);
		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("{4, 5, 6}", frequenciesProperty.getValue());
	}

	@Test
	void editingRawDataPreservesRememberedFrequencies() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				factory.createInputTypeProperty(boxPlot);
		StringProperty frequenciesProperty = factory.createFrequenciesProperty(boxPlot);
		inputTypeProperty.setValue(RAW_DATA);

		factory.createDataProperty(boxPlot).setValue("{4, 5, 6}");
		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("{2, 3, 4}", frequenciesProperty.getValue());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}) -> ListOfRawData",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false) -> ListOfData"
			})
	void dataPropertyNameMatchesInputType(String command, String key)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(boxPlot).createDataProperty(boxPlot);

		assertEquals(getLocalization().getMenu(key), dataProperty.getName());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}) -> false",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false) -> true"
			})
	void frequenciesAvailabilityMatchesInputType(String command, boolean expected)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		StringProperty frequenciesProperty =
				createPropertyFactory(boxPlot).createFrequenciesProperty(boxPlot);

		assertEquals(expected, frequenciesProperty.isAvailable());
	}

	@ParameterizedTest
	@ValueSource(strings = {"BoxPlot(1, 2, {1, 2, 3})", "BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)"})
	void dataPropertyIsAvailableInBothModes(String command) throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		StringProperty dataProperty = createPropertyFactory(boxPlot).createDataProperty(boxPlot);

		assertTrue(dataProperty.isAvailable());
	}

	@ParameterizedTest
	@ValueSource(strings = {"BoxPlot(1, 2, {1, 2, 3})", "BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)"})
	void showOutliersPropertyIsAvailableInBothModes(String command)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		BooleanProperty showOutliersProperty =
				createPropertyFactory(boxPlot).createShowOutliersProperty(boxPlot);

		assertTrue(showOutliersProperty.isAvailable());
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {
				"false;{1, 2, 3}", "true;{1, 2, 3}", "false;values", "true;values",
				"false;Sequence(k, k, 1, 3)", "true;Sequence(k, k, 1, 3)", "false;{}", "true;{}"
			})
	void listPropertiesAcceptValidLists(boolean frequencies, String value)
			throws NotApplicablePropertyException {
		evaluateGeoElement("values={1, 2, 3}");
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, values, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		StringProperty property = frequencies
				? factory.createFrequenciesProperty(boxPlot)
				: factory.createDataProperty(boxPlot);

		assertNull(property.validateValue(value));
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void listPropertiesAcceptEmptyInput(boolean frequencies) throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		StringProperty property = frequencies
				? factory.createFrequenciesProperty(boxPlot)
				: factory.createDataProperty(boxPlot);

		assertNull(property.validateValue(""));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = ';',
			value = {"false;2", "true;2", "false;x+1", "true;x+1"})
	void listPropertiesRejectNonLists(boolean frequencies, String value)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		StringProperty property = frequencies
				? factory.createFrequenciesProperty(boxPlot)
				: factory.createDataProperty(boxPlot);

		assertNotNull(property.validateValue(value));
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void listPropertiesUseMathFormat(boolean frequencies) throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		StringProperty property = frequencies
				? factory.createFrequenciesProperty(boxPlot)
				: factory.createDataProperty(boxPlot);

		assertTrue(property.isDisplayedInMathFormat());
	}

	@Test
	void editingSettingsPreservesOffsetAndScale() throws NotApplicablePropertyException {
		evaluateGeoElement("offset=1");
		evaluateGeoElement("scale=2");
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(offset, scale, {1, 2, 3})");
		StringProperty dataProperty = createPropertyFactory(boxPlot).createDataProperty(boxPlot);

		dataProperty.setValue("{4, 5, 6}");

		assertEquals("BoxPlot(offset, scale, {4, 5, 6})", definition(boxPlot.getLabelSimple()));
	}

	@Test
	void editingSettingsPreservesUntouchedArguments() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(3, 4, {1, 2, 3}, {2, 3, 4}, true)");
		StringProperty frequenciesProperty =
				createPropertyFactory(boxPlot).createFrequenciesProperty(boxPlot);

		frequenciesProperty.setValue("{4, 5, 6}");

		assertEquals("BoxPlot(3, 4, {1, 2, 3}, {4, 5, 6}, true)", definition(boxPlot.getLabelSimple()));
	}

	@Test
	void editingDataPreservesDynamicShowOutliers() throws NotApplicablePropertyException {
		GeoBoolean outliers = evaluateGeoElement("outliers = true");
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, outliers)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		BooleanProperty showOutliersProperty = factory.createShowOutliersProperty(boxPlot);

		factory.createDataProperty(boxPlot).setValue("{4, 5, 6}");

		assertEquals("BoxPlot(1, 2, {4, 5, 6}, outliers)", definition(boxPlot.getLabelSimple()));
		assertTrue(showOutliersProperty.getValue());
		outliers.setValue(false);
		outliers.updateRepaint();
		assertFalse(showOutliersProperty.getValue());
	}

	@Test
	void editingSettingsPreservesNamedListReferences() throws NotApplicablePropertyException {
		evaluateGeoElement("values={1, 2, 3}");
		evaluateGeoElement("counts={2, 3, 4}");
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, values, counts, false)");
		BooleanProperty showOutliersProperty =
				createPropertyFactory(boxPlot).createShowOutliersProperty(boxPlot);

		showOutliersProperty.setValue(true);

		assertEquals("BoxPlot(1, 2, values, counts, true)", definition(boxPlot.getLabelSimple()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"BoxPlot(1, 2, {1, 2, 3}) -> FREQUENCIES",
				"BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false) -> RAW_DATA"
			})
	void propertiesFollowSettingsDrivenRedefinition(String command, BoxPlotInputType target)
			throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement(command);
		String label = boxPlot.getLabelSimple();
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				factory.createInputTypeProperty(boxPlot);
		List<GeoElementDependentProperty> properties = List.of(
				(GeoElementDependentProperty) inputTypeProperty,
				(GeoElementDependentProperty) factory.createDataProperty(boxPlot),
				(GeoElementDependentProperty) factory.createFrequenciesProperty(boxPlot),
				(GeoElementDependentProperty) factory.createShowOutliersProperty(boxPlot));

		inputTypeProperty.setValue(target);

		properties.forEach(property -> assertSame(lookup(label), property.getGeoElement()));
	}

	@Test
	void rejectsQuartileBoxPlots() {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, 0, 1, 2, 3, 4)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(boxPlot);

		assertThrows(NotApplicablePropertyException.class, () -> factory.createDataProperty(boxPlot));
	}

	@Test
	void rejectsUnrelatedObjects() {
		GeoElement point = evaluateGeoElement("(1, 2)");
		BoxPlotDataPropertyFactory factory = createPropertyFactory(point);

		assertThrows(NotApplicablePropertyException.class, () -> factory.createDataProperty(point));
	}

	@Test
	void registersBoxPlotDataProperties() throws NotApplicablePropertyException {
		GeoElement boxPlot = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3})");
		ChartDataPropertyCollection collection = new ChartDataPropertyCollection(
				new GeoElementPropertiesFactory(),
				getAlgebraProcessor(),
				getLocalization(),
				List.of(boxPlot));

		assertEquals(4, collection.getProperties().length);
		assertInstanceOf(NamedEnumeratedProperty.class, collection.getProperties()[0]);
		assertInstanceOf(StringProperty.class, collection.getProperties()[1]);
		assertInstanceOf(StringProperty.class, collection.getProperties()[2]);
		assertInstanceOf(BooleanProperty.class, collection.getProperties()[3]);
	}

	@Test
	void changingSettingUpdatesAllSelectedBoxPlots() throws NotApplicablePropertyException {
		GeoElement first = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, false)");
		GeoElement second = evaluateGeoElement("BoxPlot(3, 4, {4, 5, 6}, false)");
		ChartDataPropertyCollection collection = new ChartDataPropertyCollection(
				new GeoElementPropertiesFactory(),
				getAlgebraProcessor(),
				getLocalization(),
				List.of(first, second));
		BooleanProperty showOutliersProperty = (BooleanProperty) collection.getProperties()[3];

		showOutliersProperty.setValue(true);

		assertEquals("BoxPlot(1, 2, {1, 2, 3}, true)", definition(first.getLabelSimple()));
		assertEquals("BoxPlot(3, 4, {4, 5, 6}, true)", definition(second.getLabelSimple()));
	}

	@Test
	@SuppressWarnings("unchecked")
	void selectedBoxPlotsRememberFrequenciesIndependently() throws NotApplicablePropertyException {
		GeoElement first = evaluateGeoElement("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)");
		GeoElement second = evaluateGeoElement("BoxPlot(3, 4, {1, 2, 3}, {4, 3, 2}, false)");
		ChartDataPropertyCollection collection = new ChartDataPropertyCollection(
				new GeoElementPropertiesFactory(),
				getAlgebraProcessor(),
				getLocalization(),
				List.of(first, second));
		NamedEnumeratedProperty<BoxPlotInputType> inputTypeProperty =
				(NamedEnumeratedProperty<BoxPlotInputType>) collection.getProperties()[0];

		inputTypeProperty.setValue(RAW_DATA);
		inputTypeProperty.setValue(FREQUENCIES);

		assertEquals("BoxPlot(1, 2, {1, 2, 3}, {2, 3, 4}, false)", definition(first.getLabelSimple()));
		assertEquals("BoxPlot(3, 4, {1, 2, 3}, {4, 3, 2}, false)", definition(second.getLabelSimple()));
	}

	private BoxPlotDataPropertyFactory createPropertyFactory(GeoElement... elements) {
		return new BoxPlotDataPropertyFactory(getLocalization(), List.of(elements));
	}

	private String definition(String label) {
		return lookup(label).getDefinition(StringTemplate.testTemplate);
	}
}
