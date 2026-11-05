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
import java.util.Map;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoHistogram;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.NumericPropertyUtil;
import org.geogebra.common.properties.impl.objects.CommandData.Controller;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Creates the individual properties shown in the Data settings for a histogram.
 * <p>
 * The properties are created separately by {@link GeoElementPropertiesFactory}, but they must
 * operate on one coherent histogram model. This factory therefore keeps one
 * {@link Controller} per histogram for the lifetime of the surrounding property
 * collection. All properties for each histogram share its controller and
 * {@link HistogramData}, including the remembered raw-data settings while in heights mode.
 * <p>
 * The properties are applicable only when every selected element is a {@link GeoNumeric} produced
 * by an {@link AlgoHistogram} with one of the supported command layouts. Changing a property
 * redefines every selected histogram command. If redefinition replaces an output geo, its
 * controller updates its references and notifies every geo-dependent property view.
 */
final class HistogramDataPropertyFactory
		extends CommandData.Factory<HistogramDataPropertyFactory.HistogramData> {
	/** The two command families exposed by the histogram Data settings. */
	enum HistogramInputType {
		RAW_DATA,
		HEIGHTS
	}

	/** Initializes the factory for the given histogram selection. */
	HistogramDataPropertyFactory(
			@NonNull Localization localization, @NonNull List<GeoElement> elements) {
		super(localization);
		for (GeoElement element : elements) {
			if (element instanceof GeoNumeric histogram
					&& histogram.getParentAlgorithm() instanceof AlgoHistogram algoHistogram
					&& hasSupportedInput(algoHistogram)) {
				controllers.put(element, new Controller<>(histogram, new HistogramData()));
			}
		}
	}

	/** Constructs property to change the Histogram input type. */
	@NonNull NamedEnumeratedProperty<HistogramInputType> createInputTypeProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramInputTypeProperty(localization, getController(element));
	}

	/** Creates the property for editing class boundaries. */
	@NonNull StringProperty createClassBoundariesProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramClassBoundariesProperty(localization, getController(element));
	}

	/** Creates the property for editing histogram heights. */
	@NonNull StringProperty createHeightsProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramHeightsProperty(localization, getController(element));
	}

	/** Creates the property for editing raw histogram data. */
	@NonNull StringProperty createRawDataProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new HistogramRawDataProperty(localization, getController(element));
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
		return new HistogramDensityScaleFactorProperty(localization, getController(element));
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
				inputs[0].isGeoList() && inputs[1].isGeoList() && inputs[2].isGeoBoolean();
			case 4 ->
				// Histogram(classBoundaries, rawData, useDensity, densityFactor)
				(inputs[0].isGeoList()
								&& inputs[1].isGeoList()
								&& inputs[2].isGeoBoolean()
								&& inputs[3].isGeoNumeric())
						// Histogram(cumulative, classBoundaries, rawData, useDensity)
						|| (inputs[0].isGeoBoolean()
								&& inputs[1].isGeoList()
								&& inputs[2].isGeoList()
								&& inputs[3].isGeoBoolean());
			case 5 ->
				// Histogram(cumulative, classBoundaries, rawData, useDensity, densityFactor)
				inputs[0].isGeoBoolean()
						&& inputs[1].isGeoList()
						&& inputs[2].isGeoList()
						&& inputs[3].isGeoBoolean()
						&& inputs[4].isGeoNumeric();
			default -> false;
		};
	}

	/**
	 * Plain values that describe the editable histogram command.
	 * <p>
	 * The second list holds the heights or the raw data, depending on the input type, so its
	 * value is kept when switching modes.
	 */
	static final class HistogramData implements CommandData {
		private @NonNull HistogramInputType inputType = HistogramInputType.HEIGHTS;
		private @NonNull String classBoundaries = "";
		private @NonNull String rawDataOrHeights = "";
		private boolean cumulative = false;
		private boolean useDensity = false;
		private @NonNull String densityScaleFactor = "1";
		private @NonNull String commandName = "Histogram";

		@Override
		public void syncFromAlgorithm(@NonNull AlgoElement algorithm) {
			GeoElement[] inputs = algorithm.getInput();
			if (!hasSupportedInput(algorithm)) {
				return;
			}

			commandName = algorithm.getClassName().getCommand();
			boolean isRawData = inputs.length > 2;
			inputType = isRawData ? HistogramInputType.RAW_DATA : HistogramInputType.HEIGHTS;
			int classBoundariesIndex = inputs[0].isGeoBoolean() ? 1 : 0;
			classBoundaries = CommandRedefineHelper.getInputString(inputs[classBoundariesIndex]);
			rawDataOrHeights = CommandRedefineHelper.getInputString(inputs[classBoundariesIndex + 1]);
			if (!isRawData) {
				return;
			}

			cumulative = classBoundariesIndex == 1 && ((GeoBoolean) inputs[0]).getBoolean();
			int useDensityIndex = classBoundariesIndex + 2;
			useDensity = ((GeoBoolean) inputs[useDensityIndex]).getBoolean();
			int densityIndex = useDensityIndex + 1;
			densityScaleFactor = densityIndex < inputs.length
					? CommandRedefineHelper.getInputString(inputs[densityIndex])
					: "1";
		}

		@Override
		public @NonNull String getCommand() {
			return commandName + "(" + String.join(", ", getCommandParameters()) + ")";
		}

		private @NonNull List<String> getCommandParameters() {
			if (inputType == HistogramInputType.HEIGHTS) {
				return List.of(toListInput(classBoundaries), toListInput(rawDataOrHeights));
			}
			// Raw-data edits intentionally emit an explicit density factor;
			// omitted factors default to 1.
			if (cumulative) {
				return List.of(
						"true",
						toListInput(classBoundaries),
						toListInput(rawDataOrHeights),
						Boolean.toString(useDensity),
						densityScaleFactor);
			}
			return List.of(
					toListInput(classBoundaries),
					toListInput(rawDataOrHeights),
					Boolean.toString(useDensity),
					densityScaleFactor);
		}

		private static @NonNull String toListInput(@NonNull String list) {
			return list.isEmpty() ? "{}" : list;
		}
	}

	/** Base for math-formatted list properties with shared list validation. */
	private abstract static class AbstractHistogramListProperty
			extends CommandData.Property<HistogramData, String> implements StringProperty {

		private AbstractHistogramListProperty(
				@NonNull Localization localization,
				@NonNull String name,
				@NonNull Controller<HistogramData> controller) {
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

	private static final class HistogramInputTypeProperty
			extends AbstractNamedEnumeratedProperty<HistogramInputType>
			implements GeoElementDependentProperty {

		private final @NonNull Controller<HistogramData> controller;

		private HistogramInputTypeProperty(
				@NonNull Localization localization, @NonNull Controller<HistogramData> controller) {
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
			return controller.getGeoElement();
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.addRedefinitionObserver(observer);
		}
	}

	private static final class HistogramClassBoundariesProperty
			extends AbstractHistogramListProperty {

		private HistogramClassBoundariesProperty(
				@NonNull Localization localization, @NonNull Controller<HistogramData> controller) {
			super(localization, "ListOfClassBoundaries", controller);
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

		private HistogramHeightsProperty(
				@NonNull Localization localization, @NonNull Controller<HistogramData> controller) {
			super(localization, "ListOfHeights", controller);
		}

		@Override
		public @NonNull String getValue() {
			return controller.readData(data -> data.rawDataOrHeights);
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.rawDataOrHeights = value);
		}

		@Override
		public boolean isAvailable() {
			return controller.readData(data -> data.inputType == HistogramInputType.HEIGHTS);
		}
	}

	private static final class HistogramRawDataProperty extends AbstractHistogramListProperty {

		private HistogramRawDataProperty(
				@NonNull Localization localization, @NonNull Controller<HistogramData> controller) {
			super(localization, "ListOfRawData", controller);
		}

		@Override
		public @NonNull String getValue() {
			return controller.readData(data -> data.rawDataOrHeights);
		}

		@Override
		protected void doSetValue(@NonNull String value) {
			controller.updateData(data -> data.rawDataOrHeights = value);
		}

		@Override
		public boolean isAvailable() {
			return controller.readData(data -> data.inputType == HistogramInputType.RAW_DATA);
		}
	}

	private static final class HistogramCumulativeProperty
			extends CommandData.Property<HistogramData, Boolean> implements BooleanProperty {

		private HistogramCumulativeProperty(
				@NonNull Localization localization, @NonNull Controller<HistogramData> controller) {
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
			return controller.readData(data -> data.inputType == HistogramInputType.RAW_DATA);
		}
	}

	private static final class HistogramUseDensityProperty
			extends CommandData.Property<HistogramData, Boolean> implements BooleanProperty {

		private HistogramUseDensityProperty(
				@NonNull Localization localization, @NonNull Controller<HistogramData> controller) {
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
			return controller.readData(data -> data.inputType == HistogramInputType.RAW_DATA);
		}
	}

	private static final class HistogramDensityScaleFactorProperty
			extends CommandData.Property<HistogramData, String> implements StringProperty {

		private final @NonNull NumericPropertyUtil numericPropertyUtil;

		private HistogramDensityScaleFactorProperty(
				@NonNull Localization localization, @NonNull Controller<HistogramData> controller) {
			super(localization, "DensityScaleFactor", controller);
			numericPropertyUtil =
					new NumericPropertyUtil(getGeoElement().getKernel().getAlgebraProcessor());
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
			return numericPropertyUtil.isNumber(value)
					? null
					: getLocalization().getError("InvalidInput");
		}

		@Override
		public boolean isDisplayedInMathFormat() {
			return true;
		}

		@Override
		public boolean isAvailable() {
			return controller.readData(data -> data.inputType == HistogramInputType.RAW_DATA);
		}

		@Override
		public boolean isEnabled() {
			return controller.readData(
					data -> data.inputType == HistogramInputType.RAW_DATA && data.useDensity);
		}
	}
}
