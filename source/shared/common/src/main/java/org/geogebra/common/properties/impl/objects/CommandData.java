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
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.GeoElementDependentProperty.RedefinitionObserver;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.NonNull;

/** Editable command parameters and syntax, including remembered values for inactive input modes. */
interface CommandData {
	/** Refreshes active values without discarding remembered inactive values. */
	void syncFromAlgorithm(@NonNull AlgoElement algorithm);

	/** Returns the command representing the current editable state. */
	@NonNull String getCommand();

	/**
	 * Holds shared controllers for one property collection's lifetime.
	 * Concrete factories populate the map for supported elements and create their own properties.
	 * @param <D> editable command state
	 */
	abstract class Factory<D extends CommandData> {
		protected final @NonNull Localization localization;
		protected final @NonNull Map<GeoElement, Controller<D>> controllers = new HashMap<>();

		protected Factory(@NonNull Localization localization) {
			this.localization = localization;
		}

		protected @NonNull Controller<D> getController(@NonNull GeoElement element)
				throws NotApplicablePropertyException {
			Controller<D> controller = controllers.get(element);
			if (controller == null) {
				throw new NotApplicablePropertyException(element);
			}
			return controller;
		}
	}

	/**
	 * Shares command state and redefinition handling between independent properties.
	 * @param <D> editable command state
	 */
	final class Controller<D extends CommandData> {
		private @NonNull GeoElement element;
		private final @NonNull D data;
		private final @NonNull Set<RedefinitionObserver> redefinitionObservers = new HashSet<>();

		Controller(@NonNull GeoElement element, @NonNull D data) {
			this.element = element;
			this.data = data;
		}

		/** Reads command state after refreshing active values from the algorithm. */
		<T> T readData(@NonNull Function<D, T> getter) {
			data.syncFromAlgorithm(element.getParentAlgorithm());
			return getter.apply(data);
		}

		/** Refreshes active values, applies an edit, and requests command redefinition. */
		void updateData(@NonNull Consumer<D> mutation) {
			data.syncFromAlgorithm(element.getParentAlgorithm());
			mutation.accept(data);
			redefine(data.getCommand());
		}

		/** Returns the current element. */
		@NonNull GeoElement getGeoElement() {
			return element;
		}

		/** Registers an observer for element replacement. */
		void addRedefinitionObserver(@NonNull RedefinitionObserver observer) {
			redefinitionObservers.add(observer);
		}

		private void redefine(@NonNull String command) {
			GeoElement previousElement = element;
			RedefineInputHandler handler = new RedefineInputHandler(
					element.getApp(), element, element.getRedefineString(false, true));
			handler.processInput(command, ErrorHelper.silent(), success -> {
				if (!success) {
					return;
				}
				GeoElement updatedElement = handler.getGeoElement().toGeoElement();
				element = updatedElement;
				if (previousElement != updatedElement) {
					new HashSet<>(redefinitionObservers)
							.forEach(observer -> observer.onGeoElementRedefined(previousElement, updatedElement));
					updatedElement.getApp().getSelectionManager().clearSelectedGeos(false, false);
					updatedElement.getApp().getSelectionManager().addSelectedGeo(updatedElement);
				}
				updatedElement.notifyUpdate();
			});
		}
	}
	/**
	 * Base for properties backed by shared editable command state.
	 * @param <D> command state type
	 * @param <T> property value type
	 */
	abstract class Property<D extends CommandData, T> extends AbstractValuedProperty<T>
			implements GeoElementDependentProperty {
		protected final @NonNull Controller<D> controller;

		protected Property(
				@NonNull Localization localization,
				@NonNull String name,
				@NonNull Controller<D> controller) {
			super(localization, name);
			this.controller = controller;
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
}
