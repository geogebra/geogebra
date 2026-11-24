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

package org.geogebra.common.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.impl.objects.ElementSettingsData;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.jspecify.annotations.NonNull;

import com.google.j2objc.annotations.Weak;

/** Owns the property tree and retained data for one object settings presentation. */
public final class ObjectSettingsSession implements Construction.GeoElementReplacementListener {
	@Weak
	private final App app;

	private final Context context = new Context();
	private final List<GeoElement> elements;
	private final MutableState<PropertyView.TabbedPageSelector> tree;

	/**
	 * @param app application whose element replacements are observed
	 * @param elements nonempty selection whose settings are shown
	 */
	public ObjectSettingsSession(@NonNull App app, @NonNull List<GeoElement> elements) {
		this.app = app;
		this.elements = new ArrayList<>(elements);
		tree = new MutableState<>(Objects.requireNonNull(
				PropertyViewFactory.propertyViewOfObjectSettings(app, this.elements, context)));
		app.getKernel().getConstruction().addGeoElementReplacementListener(this);
	}

	/** @return an unmodifiable view of the current elements, updated on replacement */
	public @NonNull List<GeoElement> getElements() {
		return Collections.unmodifiableList(elements);
	}

	/** @return the current property tree, updated when shown elements are replaced */
	public @NonNull State<PropertyView.TabbedPageSelector> getTree() {
		return tree;
	}

	/** Reconstructs the tree while retaining the selected tab and expanded groups. */
	public void rebuild() {
		PropertyView.TabbedPageSelector previous = tree.get();
		PropertyView.TabbedPageSelector replacement = Objects.requireNonNull(
				PropertyViewFactory.propertyViewOfObjectSettings(app, elements, context));
		PropertyViewFactory.restorePresentation(previous, replacement);
		previous.detach();
		tree.set(replacement);
	}

	@Override
	public void onGeoElementsReplaced(@NonNull Map<GeoElement, GeoElement> replacements) {
		replacements.forEach(context::replace);
		if (elements.stream().anyMatch(replacements::containsKey)) {
			elements.replaceAll(element -> replacements.getOrDefault(element, element));
			rebuild();
		}
	}

	/** Releases the tree, temporary data and references when the settings presentation ends. */
	public void dispose() {
		app.getKernel().getConstruction().removeGeoElementReplacementListener(this);
		tree.get().detach();
		context.elements.clear();
		elements.clear();
	}

	/** Temporary element data retained while an object settings session is open. */
	public static final class Context {
		private final Map<GeoElement, ElementSettingsData> elements = new IdentityHashMap<>();

		/**
		 * @param element element whose temporary data is needed
		 * @return retained data, creating it on first access
		 */
		public @NonNull ElementSettingsData forElement(@NonNull GeoElement element) {
			return elements.computeIfAbsent(element, key -> new ElementSettingsData());
		}

		/**
		 * Transfers the retained data of a replaced element to its replacement.
		 * @param original element that was replaced
		 * @param replacement element that replaced it
		 */
		public void replace(@NonNull GeoElement original, @NonNull GeoElement replacement) {
			ElementSettingsData elementSettingsData = elements.remove(original);
			if (elementSettingsData != null) {
				elements.put(replacement, elementSettingsData);
			}
		}
	}
}
