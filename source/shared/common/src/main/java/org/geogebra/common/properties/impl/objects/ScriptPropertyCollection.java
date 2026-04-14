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
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Property collection for script editing. Contains a {@link ScriptEventSelectionProperty} as the
 * first property, followed by one {@link ScriptEventPropertyCollection} per available event.
 */
public class ScriptPropertyCollection extends AbstractPropertyCollection<Property> {
	private final NamedEnumeratedProperty<ScriptEvent> scriptEventSelectionProperty;
	private final List<ScriptEventPropertyCollection> scriptEventPropertyCollections;

	/**
	 * Constructs the property.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param geoElements the elements whose scripts are managed
	 * @param jsEnabled whether JavaScript is enabled in the app
	 */
	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	public ScriptPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, List<GeoElement> geoElements, boolean jsEnabled) {
		super(localization, "Script");
		this.scriptEventSelectionProperty = propertiesFactory.createOptionalPropertyFacade(
				geoElements, element -> new ScriptEventSelectionProperty(localization, element,
						jsEnabled), NamedEnumeratedPropertyListFacade::new);
		if (scriptEventSelectionProperty.getIndex() == -1) {
			scriptEventSelectionProperty.setIndex(0);
		}
		this.scriptEventPropertyCollections = scriptEventSelectionProperty.getValues().stream()
				.map(scriptEvent -> new ScriptEventPropertyCollection(propertiesFactory,
						localization, geoElements, scriptEvent, jsEnabled)).toList();
		setProperties(Stream.concat(
				Stream.of(scriptEventSelectionProperty),
				scriptEventPropertyCollections.stream()
		).toArray(Property[]::new));
	}

	/**
	 * @return the {@link ScriptEventSelectionProperty}
	 */
	public NamedEnumeratedProperty<ScriptEvent> getScriptEventSelectionProperty() {
		return scriptEventSelectionProperty;
	}

	/**
	 * @return the list of {@link ScriptEventPropertyCollection}s
	 */
	public List<ScriptEventPropertyCollection> getScriptEventPropertyCollections() {
		return scriptEventPropertyCollections;
	}
}
