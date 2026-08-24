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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoHistogram;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.NumericPropertyUtil;
import org.geogebra.common.properties.impl.objects.GeoElementDependentProperty.RedefinitionObserver;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Creates the individual properties shown in the Data settings for a histogram.
 * <p>
 * The properties are created separately by {@link GeoElementPropertiesFactory}, but they must
 * operate on one coherent histogram model. This factory therefore keeps one
 * {@link HistogramDataController} per histogram for the lifetime of the surrounding property
 * collection. All properties for each histogram share its controller and
 * {@link HistogramData}, including the remembered values for the inactive input mode.
 * <p>
 * The properties are applicable only when every selected element is a {@link GeoNumeric} produced
 * by an {@link AlgoHistogram} with one of the supported command layouts. Changing a property
 * redefines every selected histogram command. If redefinition replaces an output geo, its
 * controller updates its references and notifies every geo-dependent property view.
 */
final class HistogramDataPropertyFactory {

	/** The two command families exposed by the histogram Data settings. */
	enum HistogramInputType {
		RAW_DATA,
		HEIGHTS
	}

	private final @NonNull AlgebraProcessor algebraProcessor;
	private final @NonNull Localization localization;
	private final @NonNull Map<GeoElement, HistogramDataController> controllers =
			new HashMap<>();

	/** Initializes the factory for the given histogram selection. */
	HistogramDataPropertyFactory(@NonNull AlgebraProcessor algebraProcessor,
			@NonNull Localization localization, @NonNull List<GeoElement> elements) {
		this.algebraProcessor = algebraProcessor;
		this.localization = localization;
		for (GeoElement element : elements) {
			if (element instanceof GeoNumeric histogram
					&& histogram.getParentAlgorithm() instanceof AlgoHistogram algoHistogram
					&& hasSupportedInput(algoHistogram)) {
				controllers.put(element, new HistogramDataController(histogram, algoHistogram));
			}
		}
	}

	/** Constructs property to change the Histogram input type. */
	@NonNull NamedEnumeratedProperty<HistogramInputType> createInputTypeProperty(
			@NonNull GeoElement element) throws NotApplicablePropertyException {
		return new HistogramInputTypeProperty(localization, getController(element));
	}

	/** Creates the property for editing class boundaries. */
	@NonNull StringProperty createClassBoundariesProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramClassBoundariesProperty(algebraProcessor, localization,
				getController(element));
	}

	/** Creates the property for editing histogram heights. */
	@NonNull StringProperty createHeightsProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramHeightsProperty(algebraProcessor, localization, getController(element));
	}

	/** Creates the property for editing raw histogram data. */
	@NonNull StringProperty createRawDataProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramRawDataProperty(algebraProcessor, localization, getController(element));
	}

	/** Creates the property for controlling cumulative mode. */
	@NonNull BooleanProperty createCumulativeProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramCumulativeProperty(localization, getController(element));
	}

	/** Creates the property controlling density calculation. */
	@NonNull BooleanProperty createUseDensityProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramUseDensityProperty(localization, getController(element));
	}

	/** Creates the property for editing the density scale factor. */
	@NonNull StringProperty createDensityScaleFactorProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramDensityScaleFactorProperty(algebraProcessor, localization,
				getController(element));
	}

	/**
	 * Tests whether the algorithm uses a command layout that this editor can reproduce without
	 * losing an argument.
	 * @param algorithm histogram algorithm
	 * @return whether all inputs match a supported heights or raw-data layout
	 */
	private static boolean hasSupportedInput(@NonNull AlgoElement algorithm) {
		GeoElement[] inputs = algorithm.getInput();
		return switch (inputs.length) {
			case 2 ->
					// Histogram(classBoundaries, heights)
					inputs[0].isGeoList() && inputs[1].isGeoList();
			case 3 ->
					// Histogram(classBoundaries, rawData, useDensity)
					inputs[0].isGeoList() && inputs[1].isGeoList()
					&& inputs[2].isGeoBoolean();
			case 4 ->
					// Histogram(classBoundaries, rawData, useDensity, densityFactor)
					(inputs[0].isGeoList() && inputs[1].isGeoList()
					&& inputs[2].isGeoBoolean() && inputs[3].isGeoNumeric())
					// Histogram(cumulative, classBoundaries, rawData, useDensity)
					|| (inputs[0].isGeoBoolean() && inputs[1].isGeoList()
					&& inputs[2].isGeoList() && inputs[3].isGeoBoolean());
			case 5 ->
					// Histogram(cumulative, classBoundaries, rawData, useDensity, densityFactor)
					inputs[0].isGeoBoolean() && inputs[1].isGeoList()
					&& inputs[2].isGeoList() && inputs[3].isGeoBoolean()
					&& inputs[4].isGeoNumeric();
			default -> false;
		};
	}

	private @NonNull HistogramDataController getController(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		HistogramDataController controller = controllers.get(element);
		if (controller == null) {
			throw new NotApplicablePropertyException(element);
		}
		return controller;
	}

	/**
	 * Plain values that describe the editable histogram command.
	 * <p>
	 * The two mode-specific list values are nullable until their mode is initialized. They
	 * remain independent after initialization so switching modes can restore the previous value.
	 */
	private static final class HistogramData {
		private @NonNull HistogramInputType inputType = HistogramInputType.HEIGHTS;
		private @NonNull String classBoundaries = "";
		private @Nullable String heights = null;
		private @Nullable String rawData = null;
		private boolean cumulative = false;
		private boolean useDensity = false;
		private @NonNull String densityScaleFactor = "1";
	}

	/**
	 * Panel-lifetime controller shared by all properties belonging to one histogram.
	 * <p>
	 * It synchronizes the active values from the live algorithm before reads and edits while
	 * retaining the inactive mode's second-list value in {@link HistogramData}. It also generates
	 * histogram commands, manages redefinition, updates references when the output geo is replaced,
	 * and notifies the registered redefinition observers.
	 */
	private static final class HistogramDataController {
		private @NonNull GeoNumeric histogram;
		private @NonNull AlgoHistogram algoHistogram;
		private final @NonNull Set<RedefinitionObserver> redefinitionObservers = new HashSet<>();
		private final @NonNull HistogramData data = new HistogramData();
		private boolean redefinitionPending;

		private HistogramDataController(@NonNull GeoNumeric histogram,
				@NonNull AlgoHistogram algoHistogram) {
			this.histogram = histogram;
			this.algoHistogram = algoHistogram;
		}

		private void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			redefinitionObservers.add(observer);
		}

		/** Reads a value after synchronizing with the histogram algorithm. */
		private <T> T readData(Function<HistogramData, T> getter) {
			syncFromAlgorithm();
			return getter.apply(data);
		}

		private boolean isHeightsMode() {
			return readData(data -> data.inputType) == HistogramInputType.HEIGHTS;
		}

		private boolean isRawDataMode() {
			return readData(data -> data.inputType) == HistogramInputType.RAW_DATA;
		}

		private boolean isDensityScaleFactorEnabled() {
			return isRawDataMode() && data.useDensity;
		}

		/** Synchronizes, mutates the data and rebuilds the histogram command. */
		private void updateData(@NonNull Consumer<HistogramData> mutation) {
			syncFromAlgorithm();
			mutation.accept(data);
			redefineFromData();
		}

		private void syncFromAlgorithm() {
			if (redefinitionPending) {
				return;
			}
			GeoElement[] inputs = algoHistogram.getInput();
			if (!hasSupportedInput(algoHistogram)) {
				return;
			}

			boolean isRawData = inputs.length > 2;
			data.inputType = isRawData
					? HistogramInputType.RAW_DATA : HistogramInputType.HEIGHTS;
			int classBoundariesIndex = inputs[0].isGeoBoolean() ? 1 : 0;
			data.classBoundaries =
					CommandRedefineHelper.getInputString(inputs[classBoundariesIndex]);
			String secondList =
					CommandRedefineHelper.getInputString(inputs[classBoundariesIndex + 1]);
			if (!isRawData) {
				data.heights = secondList;
				return;
			}

			data.rawData = secondList;
			data.cumulative = classBoundariesIndex == 1
					&& ((GeoBoolean) inputs[0]).getBoolean();
			int useDensityIndex = classBoundariesIndex + 2;
			data.useDensity = ((GeoBoolean) inputs[useDensityIndex]).getBoolean();
			int densityIndex = useDensityIndex + 1;
			data.densityScaleFactor = densityIndex < inputs.length
					? CommandRedefineHelper.getInputString(inputs[densityIndex]) : "1";
		}

		private void redefineFromData() {
			String command = algoHistogram.getClassName().getCommand() + "("
					+ String.join(", ", getCommandParameters()) + ")";
			GeoNumeric previousHistogram = histogram;
			RedefineInputHandler handler = new RedefineInputHandler(histogram.getApp(), histogram,
					histogram.getRedefineString(false, true));
			redefinitionPending = true;
			handler.processInput(command, ErrorHelper.silent(), success -> {
				redefinitionPending = false;
				GeoElement updatedElement = handler.getGeoElement().toGeoElement();
				if (!success
						|| !(updatedElement instanceof GeoNumeric updatedHistogram)
						|| !(updatedHistogram.getParentAlgorithm()
								instanceof AlgoHistogram updatedAlgo)) {
					syncFromAlgorithm();
					return;
				}
				histogram = updatedHistogram;
				algoHistogram = updatedAlgo;
				if (previousHistogram != updatedHistogram) {
					redefinitionObservers.forEach(observer -> observer.onGeoElementRedefined(
							previousHistogram, updatedHistogram));
					updatedHistogram.getApp().getSelectionManager().clearSelectedGeos(false, false);
					updatedHistogram.getApp().getSelectionManager()
							.addSelectedGeo(updatedHistogram);
				}
			});
		}

		private @NonNull List<String> getCommandParameters() {
			if (data.inputType == HistogramInputType.HEIGHTS) {
				return List.of(data.classBoundaries,
						data.heights == null ? "" : data.heights);
			}
			// Raw-data edits intentionally emit an explicit density factor;
			// omitted factors default to 1.
			if (data.cumulative) {
				return List.of("true", data.classBoundaries,
						data.rawData == null ? "" : data.rawData,
						Boolean.toString(data.useDensity), data.densityScaleFactor);
			}
			return List.of(data.classBoundaries,
					data.rawData == null ? "" : data.rawData,
					Boolean.toString(data.useDensity), data.densityScaleFactor);
		}
	}

	/**
	 * Base class that connects a histogram property to the geo and redefinition observers held by
	 * its shared controller.
	 * @param <T> property value type
	 */
	private static abstract class AbstractHistogramProperty<T> extends AbstractValuedProperty<T>
			implements GeoElementDependentProperty {

		protected final @NonNull HistogramDataController controller;

		private AbstractHistogramProperty(@NonNull Localization localization,
				@NonNull String name,
				@NonNull HistogramDataController controller) {
			super(localization, name);
			this.controller = controller;
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.histogram;
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.addRedefinitionObserver(observer);
		}
	}

	/** Base for math-formatted list properties with shared list validation. */
	private static abstract class AbstractHistogramListProperty
			extends AbstractHistogramProperty<String> implements StringProperty {

		private final @NonNull AlgebraProcessor algebraProcessor;

		private AbstractHistogramListProperty(@NonNull AlgebraProcessor algebraProcessor,
				@NonNull Localization localization, @NonNull String name,
				@NonNull HistogramDataController controller) {
			super(localization, name, controller);
			this.algebraProcessor = algebraProcessor;
		}

		@Override
		public @Nullable String validateValue(@NonNull String value) {
			return value.isEmpty() || algebraProcessor.evaluateToList(value) != null
					? null : getLocalization().getError("InvalidInput");
		}

		@Override
		public boolean isDisplayedInMathFormat() {
			return true;
		}
	}

	private static final class HistogramInputTypeProperty
			extends AbstractNamedEnumeratedProperty<HistogramInputType>
			implements GeoElementDependentProperty {

		private final @NonNull HistogramDataController controller;

		private HistogramInputTypeProperty(@NonNull Localization localization,
				@NonNull HistogramDataController controller) {
			super(localization, "InputType");
			this.controller = controller;
			setNamedValues(List.of(
					Map.entry(HistogramInputType.RAW_DATA, "ListOfRawData"),
					Map.entry(HistogramInputType.HEIGHTS, "ListOfHeights")));
		}

		@Override
		public @NonNull HistogramInputType getValue() {
			return controller.readData(data -> data.inputType);
		}

		@Override
		protected void doSetValue(@NonNull HistogramInputType value) {
			controller.updateData(data -> data.inputType = value);
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.histogram;
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.addRedefinitionObserver(observer);
		}
	}

	private static final class HistogramClassBoundariesProperty
			extends AbstractHistogramListProperty {

		private HistogramClassBoundariesProperty(@NonNull AlgebraProcessor algebraProcessor,
				@NonNull Localization localization,
				@NonNull HistogramDataController controller) {
			super(algebraProcessor, localization, "ListOfClassBoundaries", controller);
		}

		@Override
		public @NonNull String getValue() {
			return controller.readData(data -> data.classBoundaries);
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.classBoundaries = value);
		}
	}

	private static final class HistogramHeightsProperty extends AbstractHistogramListProperty {

		private HistogramHeightsProperty(@NonNull AlgebraProcessor algebraProcessor,
				@NonNull Localization localization, @NonNull HistogramDataController controller) {
			super(algebraProcessor, localization, "ListOfHeights", controller);
		}

		@Override
		public @NonNull String getValue() {
			String heights = controller.readData(data -> data.heights);
			return heights != null ? heights : "";
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.heights = value);
		}

		@Override
		public boolean isAvailable() {
			return controller.isHeightsMode();
		}
	}

	private static final class HistogramRawDataProperty extends AbstractHistogramListProperty {

		private HistogramRawDataProperty(@NonNull AlgebraProcessor algebraProcessor,
				@NonNull Localization localization, @NonNull HistogramDataController controller) {
			super(algebraProcessor, localization, "ListOfRawData", controller);
		}

		@Override
		public @NonNull String getValue() {
			String rawData = controller.readData(data -> data.rawData);
			return rawData != null ? rawData : "";
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.rawData = value);
		}

		@Override
		public boolean isAvailable() {
			return controller.isRawDataMode();
		}
	}

	private static final class HistogramCumulativeProperty
			extends AbstractHistogramProperty<Boolean> implements BooleanProperty {

		private HistogramCumulativeProperty(@NonNull Localization localization,
				@NonNull HistogramDataController controller) {
			super(localization, "Cumulative", controller);
		}

		@Override
		public @NonNull Boolean getValue() {
			return controller.readData(data -> data.cumulative);
		}

		@Override
		protected void doSetValue(@NonNull Boolean value) {
			controller.updateData(data -> data.cumulative = value);
		}

		@Override
		public boolean isAvailable() {
			return controller.isRawDataMode();
		}
	}

	private static final class HistogramUseDensityProperty
			extends AbstractHistogramProperty<Boolean> implements BooleanProperty {

		private HistogramUseDensityProperty(@NonNull Localization localization,
				@NonNull HistogramDataController controller) {
			super(localization, "UseDensity", controller);
		}

		@Override
		public @NonNull Boolean getValue() {
			return controller.readData(data -> data.useDensity);
		}

		@Override
		protected void doSetValue(@NonNull Boolean value) {
			controller.updateData(data -> data.useDensity = value);
		}

		@Override
		public boolean isAvailable() {
			return controller.isRawDataMode();
		}
	}

	private static final class HistogramDensityScaleFactorProperty
			extends AbstractHistogramProperty<String> implements StringProperty {

		private final @NonNull NumericPropertyUtil numericPropertyUtil;

		private HistogramDensityScaleFactorProperty(@NonNull AlgebraProcessor algebraProcessor,
				@NonNull Localization localization, @NonNull HistogramDataController controller) {
			super(localization, "DensityScaleFactor", controller);
			numericPropertyUtil = new NumericPropertyUtil(algebraProcessor);
		}

		@Override
		public @NonNull String getValue() {
			return controller.readData(data -> data.densityScaleFactor);
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.densityScaleFactor = value);
		}

		@Override
		public @Nullable String validateValue(@NonNull String value) {
			return numericPropertyUtil.isNumber(value) ? null
					: getLocalization().getError("InvalidInput");
		}

		@Override
		public boolean isDisplayedInMathFormat() {
			return true;
		}

		@Override
		public boolean isAvailable() {
			return controller.isRawDataMode();
		}

		@Override
		public boolean isEnabled() {
			return controller.isDensityScaleFactorEnabled();
		}
	}
}
