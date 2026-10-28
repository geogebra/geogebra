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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
import org.geogebra.common.kernel.algos.AlgoBoxPlot;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.CommandRedefineHelper;
import org.geogebra.common.properties.impl.objects.GeoElementDependentProperty.RedefinitionObserver;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Creates properties for BoxPlot Data settings with state shared for the collection's lifetime.
 * Frequencies are remembered while raw data mode is active.
 */
final class BoxPlotDataPropertyFactory {
	/** Input types supported by the BoxPlot Data properties. */
	enum BoxPlotInputType {
		RAW_DATA,
		FREQUENCIES
	}

	private final @NonNull Localization localization;
	private final @NonNull Map<GeoElement, BoxPlotDataController> controllers = new HashMap<>();

	BoxPlotDataPropertyFactory(
			@NonNull Localization localization, @NonNull List<GeoElement> elements) {
		this.localization = localization;
		for (GeoElement element : elements) {
			if (element instanceof GeoNumeric boxPlot
					&& boxPlot.getParentAlgorithm() instanceof AlgoBoxPlot algorithm
					&& hasSupportedInput(algorithm)) {
				controllers.put(element, new BoxPlotDataController(boxPlot));
			}
		}
	}

	/** Creates the BoxPlot input type property. */
	@NonNull NamedEnumeratedProperty<BoxPlotInputType> createInputTypeProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new BoxPlotInputTypeProperty(localization, getController(element));
	}

	/** Creates the BoxPlot data list property. */
	@NonNull StringProperty createDataProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new BoxPlotDataProperty(localization, getController(element));
	}

	/** Creates the BoxPlot frequencies list property. */
	@NonNull StringProperty createFrequenciesProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new BoxPlotFrequenciesProperty(localization, getController(element));
	}

	/** Creates the BoxPlot outlier display property. */
	@NonNull BooleanProperty createShowOutliersProperty(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		return new BoxPlotShowOutliersProperty(localization, getController(element));
	}

	private static boolean hasSupportedInput(@NonNull AlgoBoxPlot algorithm) {
		GeoElement[] inputs = algorithm.getInput();
		return switch (inputs.length) {
			// BoxPlot(<yOffset>, <yScale>, <List of Raw Data>)
			case 3 -> inputs[2].isGeoList();
			// BoxPlot(<yOffset>, <yScale>, <List of Raw Data>, <Show Outliers>)
			case 4 -> inputs[2].isGeoList() && inputs[3].isGeoBoolean();
			// BoxPlot(<yOffset>, <yScale>, <List of Data>, <List of Frequencies>, <Show Outliers>)
			case 5 -> inputs[2].isGeoList() && inputs[3].isGeoList() && inputs[4].isGeoBoolean();
			default -> false;
		};
	}

	private @NonNull BoxPlotDataController getController(@NonNull GeoElement element)
			throws NotApplicablePropertyException {
		BoxPlotDataController controller = controllers.get(element);
		if (controller == null) {
			throw new NotApplicablePropertyException(element);
		}
		return controller;
	}

	/** Editable BoxPlot state, retaining frequencies while raw-data mode is active. */
	private static final class BoxPlotData {
		private @NonNull BoxPlotInputType inputType = RAW_DATA;
		private @NonNull String offset = "0";
		private @NonNull String scale = "1";
		private @NonNull String data = "{}";
		private @Nullable String frequencies = null;
		private @Nullable String outliersArgument = null;
		private boolean outliers = false;

		private void syncFromAlgorithm(@NonNull GeoElement[] parameters) {
			offset = CommandRedefineHelper.getInputString(parameters[0]);
			scale = CommandRedefineHelper.getInputString(parameters[1]);
			data = CommandRedefineHelper.getInputString(parameters[2]);
			inputType = parameters.length == 5 ? FREQUENCIES : RAW_DATA;
			if (parameters.length == 5) {
				frequencies = CommandRedefineHelper.getInputString(parameters[3]);
			}
			outliersArgument = parameters.length == 4 || parameters.length == 5
					? CommandRedefineHelper.getInputString(parameters[parameters.length - 1])
					: null;
			outliers =
					outliersArgument != null && ((GeoBoolean) parameters[parameters.length - 1]).getBoolean();
		}

		private @NonNull String getCommand() {
			String command = "BoxPlot(" + offset + ", " + scale + ", " + data;
			if (inputType == FREQUENCIES) {
				String frequenciesArgument = frequencies != null ? frequencies : "{}";
				String outliersParameter = outliersArgument != null ? outliersArgument : "false";
				command += ", " + frequenciesArgument + ", " + outliersParameter;
			} else if (outliersArgument != null) {
				command += ", " + outliersArgument;
			}
			return command + ")";
		}
	}

	/** Shared controller for reading and redefining a BoxPlot from its Data properties. */
	private static final class BoxPlotDataController {
		private @NonNull GeoNumeric boxPlot;
		private final @NonNull Set<RedefinitionObserver> redefinitionObservers = new HashSet<>();
		private final @NonNull BoxPlotData data = new BoxPlotData();

		private BoxPlotDataController(@NonNull GeoNumeric boxPlot) {
			this.boxPlot = boxPlot;
		}

		private <T> T readData(@NonNull Function<BoxPlotData, T> getter) {
			syncFromAlgorithm();
			return getter.apply(data);
		}

		private void updateData(@NonNull Consumer<BoxPlotData> mutation) {
			syncFromAlgorithm();
			mutation.accept(data);
			redefine();
		}

		private void syncFromAlgorithm() {
			data.syncFromAlgorithm(boxPlot.getParentAlgorithm().getInput());
		}

		private void redefine() {
			String frequencies = data.frequencies != null ? data.frequencies : "{}";
			String command = data.getCommand();
			GeoNumeric previousBoxPlot = boxPlot;
			RedefineInputHandler handler = new RedefineInputHandler(
					boxPlot.getApp(), boxPlot, boxPlot.getRedefineString(false, true));
			handler.processInput(command, ErrorHelper.silent(), success -> {
				GeoElement updatedElement = handler.getGeoElement().toGeoElement();
				if (!success
						|| !(updatedElement instanceof GeoNumeric updatedBoxPlot)
						|| !(updatedBoxPlot.getParentAlgorithm() instanceof AlgoBoxPlot)) {
					return;
				}
				boxPlot = updatedBoxPlot;
				data.frequencies = frequencies;
				if (previousBoxPlot != updatedBoxPlot) {
					redefinitionObservers.forEach(
							observer -> observer.onGeoElementRedefined(previousBoxPlot, updatedBoxPlot));
					updatedBoxPlot.getApp().getSelectionManager().clearSelectedGeos(false, false);
					updatedBoxPlot.getApp().getSelectionManager().addSelectedGeo(updatedBoxPlot);
				}
				updatedBoxPlot.notifyUpdate();
			});
		}
	}

	/** Property for the BoxPlot input type. */
	private static final class BoxPlotInputTypeProperty
			extends AbstractNamedEnumeratedProperty<BoxPlotInputType>
			implements GeoElementDependentProperty {
		private final @NonNull BoxPlotDataController controller;

		private BoxPlotInputTypeProperty(
				@NonNull Localization localization, @NonNull BoxPlotDataController controller) {
			super(localization, "InputType");
			this.controller = controller;
			setNamedValues(List.of(
					Map.entry(RAW_DATA, "ListOfRawData"),
					Map.entry(FREQUENCIES, "ListOfDataAndFrequencies")));
		}

		@Override
		public @NonNull BoxPlotInputType getValue() {
			return controller.readData(data -> data.inputType);
		}

		@Override
		protected void doSetValue(@NonNull BoxPlotInputType value) {
			controller.updateData(data -> data.inputType = value);
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.boxPlot;
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.redefinitionObservers.add(observer);
		}
	}

	/** Base class for BoxPlot list properties. */
	private abstract static class AbstractBoxPlotListProperty extends AbstractValuedProperty<String>
			implements StringProperty, GeoElementDependentProperty {
		protected final @NonNull BoxPlotDataController controller;

		private AbstractBoxPlotListProperty(
				@NonNull Localization localization,
				@NonNull String name,
				@NonNull BoxPlotDataController controller) {
			super(localization, name);
			this.controller = controller;
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.boxPlot;
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.redefinitionObservers.add(observer);
		}

		@Override
		public @Nullable String validateValue(@NonNull String value) {
			return value.isEmpty()
							|| controller.boxPlot.getKernel().getAlgebraProcessor().evaluateToList(value) != null
					? null
					: getLocalization().getError("InvalidInput");
		}

		@Override
		public boolean isDisplayedInMathFormat() {
			return true;
		}
	}

	/** Property for the BoxPlot data list in both input modes. */
	private static final class BoxPlotDataProperty extends AbstractBoxPlotListProperty {
		private BoxPlotDataProperty(
				@NonNull Localization localization, @NonNull BoxPlotDataController controller) {
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
			String name =
					controller.readData(data -> data.inputType == RAW_DATA ? "ListOfRawData" : "ListOfData");
			return getLocalization().getMenu(name);
		}
	}

	/** Property for the BoxPlot frequencies list, available only in frequency mode. */
	private static final class BoxPlotFrequenciesProperty extends AbstractBoxPlotListProperty {
		private BoxPlotFrequenciesProperty(
				@NonNull Localization localization, @NonNull BoxPlotDataController controller) {
			super(localization, "ListOfFrequencies", controller);
		}

		@Override
		public @NonNull String getValue() {
			String frequencies = controller.readData(data -> data.frequencies);
			return frequencies != null ? frequencies : "{}";
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

	/** Property for displaying BoxPlot outliers separately. */
	private static final class BoxPlotShowOutliersProperty extends AbstractValuedProperty<Boolean>
			implements BooleanProperty, GeoElementDependentProperty {
		private final @NonNull BoxPlotDataController controller;

		private BoxPlotShowOutliersProperty(
				@NonNull Localization localization, @NonNull BoxPlotDataController controller) {
			super(localization, "ShowOutliers");
			this.controller = controller;
		}

		@Override
		public @NonNull GeoElement getGeoElement() {
			return controller.boxPlot;
		}

		@Override
		public void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			controller.redefinitionObservers.add(observer);
		}

		@Override
		public @NonNull Boolean getValue() {
			return controller.readData(data -> data.outliers);
		}

		@Override
		protected void doSetValue(@NonNull Boolean value) {
			controller.updateData(data -> {
				data.outliers = value;
				data.outliersArgument = value.toString();
			});
		}
	}
}
