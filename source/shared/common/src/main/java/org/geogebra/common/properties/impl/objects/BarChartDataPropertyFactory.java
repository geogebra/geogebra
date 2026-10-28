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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.geos.BarChartGeoNumeric;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.NumericPropertyUtil;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.GeoElementDependentProperty.RedefinitionObserver;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Creates properties for BarChart Data settings with state shared for the collection's lifetime.
 * Inactive frequencies and bounds are remembered while another mode is active.
 */
final class BarChartDataPropertyFactory {
	/** Input types supported by the BarChart Data properties. */
	enum BarChartInputType {
		RAW_DATA,
		FREQUENCIES,
		HEIGHTS
	}

	private final @NonNull Localization localization;
	private final @NonNull Map<GeoElement, BarChartDataController> controllers = new HashMap<>();

	BarChartDataPropertyFactory(
			@NonNull Localization localization, @NonNull List<GeoElement> elements) {
		this.localization = localization;
		for (GeoElement element : elements) {
			if (element instanceof GeoNumeric barChart
					&& barChart.getParentAlgorithm() instanceof AlgoBarChart algorithm
					&& hasSupportedInput(algorithm)) {
				controllers.put(element, new BarChartDataController(barChart));
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

	private @NonNull BarChartDataController getController(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		BarChartDataController controller = controllers.get(element);
		if (controller == null) {
			throw new NotApplicablePropertyException(element);
		}
		return controller;
	}

	/** Editable BarChart state, retaining inactive frequencies and bounds. */
	private static final class BarChartData {
		private @NonNull BarChartInputType inputType = RAW_DATA;
		private @NonNull String data = "{}";
		private @NonNull String frequencies = "{}";
		private @Nullable String start = null;
		private @Nullable String end = null;
		private @Nullable String width = null;
		private @Nullable String scale = null;

		private void syncFromAlgorithm(@NonNull AlgoBarChart algorithm) {
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

		private @NonNull String getCommand() {
			return switch (inputType) {
				case HEIGHTS -> "BarChart(" + start + ", " + end + ", " + data + ")";
				case FREQUENCIES ->
					"BarChart(" + data + ", " + frequencies + (width == null ? "" : ", " + width) + ")";
				case RAW_DATA ->
					"BarChart(" + data + ", " + width + (scale == null ? "" : ", " + scale) + ")";
			};
		}
	}

	/** Shared controller for reading and redefining a BarChart from its Data properties. */
	private static final class BarChartDataController {
		private @NonNull GeoNumeric barChart;
		private final @NonNull Set<RedefinitionObserver> redefinitionObservers = new HashSet<>();
		private final @NonNull BarChartData data = new BarChartData();

		private BarChartDataController(@NonNull GeoNumeric barChart) {
			this.barChart = barChart;
			syncFromAlgorithm();
		}

		private <T> T readData(@NonNull Function<BarChartData, T> getter) {
			syncFromAlgorithm();
			return getter.apply(data);
		}

		private void updateData(@NonNull Consumer<BarChartData> mutation) {
			syncFromAlgorithm();
			mutation.accept(data);
			redefine();
		}

		private void syncFromAlgorithm() {
			data.syncFromAlgorithm((AlgoBarChart) barChart.getParentAlgorithm());
		}

		private void initializeBounds() {
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
			data.start = Double.isFinite(start)
					? barChart.getKernel().format(start, StringTemplate.maxPrecision)
					: "0";
			data.end = Double.isFinite(end)
					? barChart.getKernel().format(end, StringTemplate.maxPrecision)
					: "1";
		}

		private void redefine() {
			String command = data.getCommand();
			GeoNumeric previousBarChart = barChart;
			RedefineInputHandler handler = new RedefineInputHandler(
					barChart.getApp(), barChart, barChart.getRedefineString(false, true));
			handler.processInput(command, ErrorHelper.silent(), success -> {
				GeoElement updatedElement = handler.getGeoElement().toGeoElement();
				if (!success
						|| !(updatedElement instanceof GeoNumeric updatedBarChart)
						|| !(updatedBarChart.getParentAlgorithm() instanceof AlgoBarChart)) {
					return;
				}
				barChart = updatedBarChart;
				if (previousBarChart != updatedBarChart) {
					redefinitionObservers.forEach(
							observer -> observer.onGeoElementRedefined(previousBarChart, updatedBarChart));
					updatedBarChart.getApp().getSelectionManager().clearSelectedGeos(false, false);
					updatedBarChart.getApp().getSelectionManager().addSelectedGeo(updatedBarChart);
				}
				updatedBarChart.notifyUpdate();
			});
		}
	}

	/** Property for the BarChart input type. */
	private static final class BarChartInputTypeProperty
			extends AbstractNamedEnumeratedProperty<BarChartInputType>
			implements GeoElementDependentProperty {
		private final @NonNull BarChartDataController controller;

		private BarChartInputTypeProperty(
				@NonNull Localization localization, @NonNull BarChartDataController controller) {
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
					controller.initializeBounds();
				}
				data.inputType = inputType;
				data.width = "1";
				data.scale = null;
			});
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.barChart;
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.redefinitionObservers.add(observer);
		}
	}

	/** Base class for math-formatted BarChart fields. */
	private abstract static class AbstractBarChartStringProperty
			extends AbstractValuedProperty<String>
			implements StringProperty, GeoElementDependentProperty {
		protected final @NonNull BarChartDataController controller;

		private AbstractBarChartStringProperty(
				@NonNull Localization localization,
				@NonNull String name,
				@NonNull BarChartDataController controller) {
			super(localization, name);
			this.controller = controller;
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.barChart;
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.redefinitionObservers.add(observer);
		}

		@Override
		public @Nullable String validateValue(@NonNull String value) {
			return value.isEmpty()
							|| controller.barChart.getKernel().getAlgebraProcessor().evaluateToList(value) != null
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
				@NonNull Localization localization, @NonNull BarChartDataController controller) {
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
				@NonNull Localization localization, @NonNull BarChartDataController controller) {
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
				@NonNull BarChartDataController controller,
				boolean start) {
			super(localization, start ? "StartValue" : "EndValue", controller);
			this.start = start;
			numericPropertyUtil =
					new NumericPropertyUtil(controller.barChart.getKernel().getAlgebraProcessor());
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
