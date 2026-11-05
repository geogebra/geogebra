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

import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.BarChartGeoNumeric;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.NumericPropertyUtil;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.CommandData.Controller;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Creates properties for BarChart Data settings with state shared for the collection's lifetime.
 * Inactive frequencies and bounds are remembered while another mode is active.
 */
final class BarChartDataPropertyFactory
		extends CommandData.Factory<BarChartDataPropertyFactory.BarChartData> {
	/** Input types supported by the BarChart Data properties. */
	enum BarChartInputType {
		RAW_DATA,
		FREQUENCIES,
		HEIGHTS
	}

	BarChartDataPropertyFactory(
			@NonNull Localization localization, @NonNull List<GeoElement> elements) {
		super(localization);
		for (GeoElement element : elements) {
			if (element instanceof GeoNumeric barChart
					&& barChart.getParentAlgorithm() instanceof AlgoBarChart algorithm
					&& hasSupportedInput(algorithm)) {
				BarChartData data = new BarChartData();
				data.syncFromAlgorithm(algorithm);
				controllers.put(element, new Controller<>(barChart, data));
			}
		}
	}

	/** Creates the BarChart input type property. */
	@NonNull NamedEnumeratedProperty<BarChartInputType> createInputTypeProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new BarChartInputTypeProperty(localization, getController(element));
	}

	/** Creates the BarChart data list property. */
	@NonNull StringProperty createDataProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new BarChartDataProperty(localization, getController(element));
	}

	/** Creates the BarChart frequencies list property. */
	@NonNull StringProperty createFrequenciesProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new BarChartFrequenciesProperty(localization, getController(element));
	}

	/** Creates the row containing the start and end values. */
	@Nullable BarChartBoundsPropertyCollection createBoundsPropertyCollection(
			@NonNull GeoElementPropertiesFactory propertiesFactory, @NonNull List<GeoElement> elements) {
		StringProperty start = propertiesFactory.createOptionalPropertyFacade(
				elements, element -> createBoundProperty(element, true), StringPropertyListFacade::new);
		StringProperty end = propertiesFactory.createOptionalPropertyFacade(
				elements, element -> createBoundProperty(element, false), StringPropertyListFacade::new);
		return start == null || end == null
				? null
				: new BarChartBoundsPropertyCollection(localization, start, end);
	}

	/** Creates a start or end value property. */
	@NonNull StringProperty createBoundProperty(@NonNull GeoElement element, boolean start)
			throws NotApplicablePropertyException {
		return new BarChartBoundProperty(localization, getController(element), start);
	}

	private static boolean hasSupportedInput(@NonNull AlgoBarChart algorithm) {
		return switch (algorithm.getType()) {
			case AlgoBarChart.TYPE_BARCHART_RAWDATA,
					AlgoBarChart.TYPE_BARCHART_FREQUENCY_TABLE,
					AlgoBarChart.TYPE_BARCHART_FREQUENCY_TABLE_WIDTH,
					AlgoBarChart.TYPE_BARCHART_EXPRESSION -> true;
			default -> false;
		};
	}

	/** Editable BarChart state, retaining inactive frequencies and bounds. */
	static final class BarChartData implements CommandData {
		private @NonNull BarChartInputType inputType = RAW_DATA;
		private @NonNull String data = "{}";
		private @NonNull String frequencies = "{}";
		private @Nullable String start = null;
		private @Nullable String end = null;
		private @Nullable String width = null;
		private @Nullable String scale = null;

		@Override
		public void syncFromAlgorithm(@NonNull AlgoElement algoElement) {
			AlgoBarChart algorithm = (AlgoBarChart) algoElement;
			GeoElement[] parameters = algorithm.getInput();
			if (algorithm.getType() == AlgoBarChart.TYPE_BARCHART_EXPRESSION) {
				inputType = HEIGHTS;
				start = CommandRedefineHelper.getInputString(parameters[0]);
				end = CommandRedefineHelper.getInputString(parameters[1]);
				data = CommandRedefineHelper.getInputString(parameters[2]);
			} else {
				inputType = parameters[1].isGeoList() ? FREQUENCIES : RAW_DATA;
				data = CommandRedefineHelper.getInputString(parameters[0]);
				if (inputType == FREQUENCIES) {
					frequencies = CommandRedefineHelper.getInputString(parameters[1]);
					width =
							parameters.length == 3 ? CommandRedefineHelper.getInputString(parameters[2]) : null;
				} else {
					width = CommandRedefineHelper.getInputString(parameters[1]);
					scale =
							parameters.length == 3 ? CommandRedefineHelper.getInputString(parameters[2]) : null;
				}
			}
		}

		private void initializeBounds(@NonNull GeoElement barChart) {
			AlgoBarChart algorithm = (AlgoBarChart) barChart.getParentAlgorithm();
			double[] borders = algorithm.getLeftBorder();
			int count = ((BarChartGeoNumeric) barChart).getIntervals();
			double start = Double.POSITIVE_INFINITY;
			double end = Double.NEGATIVE_INFINITY;
			if (barChart.isDefined() && borders != null) {
				for (int i = 0; i < count; i++) {
					start = Math.min(start, borders[i]);
					end = Math.max(end, borders[i] + algorithm.getWidth());
				}
			}
			this.start = Double.isFinite(start)
					? barChart.getKernel().format(start, StringTemplate.maxPrecision)
					: "0";
			this.end = Double.isFinite(end)
					? barChart.getKernel().format(end, StringTemplate.maxPrecision)
					: "1";
		}

		@Override
		public @NonNull String getCommand() {
			return switch (inputType) {
				case HEIGHTS -> "BarChart(" + start + ", " + end + ", " + data + ")";
				case FREQUENCIES ->
					"BarChart(" + data + ", " + frequencies + (width == null ? "" : ", " + width) + ")";
				case RAW_DATA ->
					"BarChart(" + data + ", " + width + (scale == null ? "" : ", " + scale) + ")";
			};
		}
	}

	/** Property for the BarChart input type. */
	private static final class BarChartInputTypeProperty
			extends AbstractNamedEnumeratedProperty<BarChartInputType>
			implements GeoElementDependentProperty {
		private final @NonNull Controller<BarChartData> controller;

		private BarChartInputTypeProperty(
				@NonNull Localization localization, @NonNull Controller<BarChartData> controller) {
			super(localization, "InputType");
			this.controller = controller;
			setNamedValues(List.of(
					Map.entry(RAW_DATA, "ListOfRawData"),
					Map.entry(FREQUENCIES, "ListOfDataAndFrequencies"),
					Map.entry(HEIGHTS, "ListOfHeights")));
		}

		@Override
		public @NonNull BarChartInputType getValue() {
			return controller.readData(data -> data.inputType);
		}

		@Override
		protected void doSetValue(@NonNull BarChartInputType inputType) {
			controller.updateData(data -> {
				if (data.inputType == inputType) {
					return;
				}
				if (inputType == HEIGHTS && data.start == null) {
					data.initializeBounds(getGeoElement());
				}
				data.inputType = inputType;
				data.width = "1";
				data.scale = null;
			});
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.getGeoElement();
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.addRedefinitionObserver(observer);
		}
	}

	/** Base class for math-formatted BarChart fields. */
	private abstract static class AbstractBarChartStringProperty
			extends CommandData.Property<BarChartData, String> implements StringProperty {

		private AbstractBarChartStringProperty(
				@NonNull Localization localization,
				@NonNull String name,
				@NonNull Controller<BarChartData> controller) {
			super(localization, name, controller);
		}

		@Override
		public @Nullable String validateValue(@NonNull String value) {
			return value.isEmpty()
							|| getGeoElement().getKernel().getAlgebraProcessor().evaluateToList(value) != null
					? null
					: getLocalization().getError("InvalidInput");
		}

		@Override
		public boolean isDisplayedInMathFormat() {
			return true;
		}
	}

	/** Property for the BarChart data list in all input modes. */
	private static final class BarChartDataProperty extends AbstractBarChartStringProperty {
		private BarChartDataProperty(
				@NonNull Localization localization, @NonNull Controller<BarChartData> controller) {
			super(localization, "ListOfData", controller);
		}

		@Override
		public @NonNull String getValue() {
			return controller.readData(data -> data.data);
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.data = value);
		}

		@Override
		public @NonNull String getName() {
			String name = controller.readData(data -> switch (data.inputType) {
				case RAW_DATA -> "ListOfRawData";
				case FREQUENCIES -> "ListOfData";
				case HEIGHTS -> "ListOfHeights";
			});
			return getLocalization().getMenu(name);
		}
	}

	/** Property for the BarChart frequencies list, available only in frequency mode. */
	private static final class BarChartFrequenciesProperty extends AbstractBarChartStringProperty {
		private BarChartFrequenciesProperty(
				@NonNull Localization localization, @NonNull Controller<BarChartData> controller) {
			super(localization, "ListOfFrequencies", controller);
		}

		@Override
		public @NonNull String getValue() {
			return controller.readData(data -> data.frequencies);
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.frequencies = value);
		}

		@Override
		public boolean isAvailable() {
			return controller.readData(data -> data.inputType == FREQUENCIES);
		}
	}

	/** Numeric start/end field, available only in heights mode. */
	private static final class BarChartBoundProperty extends AbstractBarChartStringProperty {
		private final boolean start;
		private final @NonNull NumericPropertyUtil numericPropertyUtil;

		private BarChartBoundProperty(
				@NonNull Localization localization,
				@NonNull Controller<BarChartData> controller,
				boolean start) {
			super(localization, start ? "StartValue" : "EndValue", controller);
			this.start = start;
			numericPropertyUtil =
					new NumericPropertyUtil(getGeoElement().getKernel().getAlgebraProcessor());
		}

		@Override
		public @NonNull String getValue() {
			String value = controller.readData(data -> start ? data.start : data.end);
			return value != null ? value : "";
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> {
				if (start) {
					data.start = value;
				} else {
					data.end = value;
				}
			});
		}

		@Override
		public @Nullable String validateValue(@NonNull String value) {
			return numericPropertyUtil.isNumber(value)
					? null
					: getLocalization().getError("InvalidInput");
		}

		@Override
		public boolean isAvailable() {
			return controller.readData(data -> data.inputType == HEIGHTS);
		}
	}
}
