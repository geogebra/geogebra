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
import java.util.Objects;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;

/**
 * {@code PropertyCollection} holding script related properties for a fixed script event. It
 * contains a {@link ScriptLanguageSelectionProperty} (if applicable) and a {@link ScriptProperty}.
 */
public class ScriptEventPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs the property collection for a script event.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param geoElements the elements whose scripts are managed
	 * @param scriptEvent the script event this collection belongs to
	 * @param jsEnabled whether JavaScript is enabled in the app
	 */
	public ScriptEventPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, List<GeoElement> geoElements, ScriptEvent scriptEvent,
			boolean jsEnabled) {
		super(localization, "");
		ScriptLanguageSelection scriptLanguageSelection =
				new ScriptLanguageSelection(initialScriptLanguage(geoElements.get(0), scriptEvent));
		setProperties(Stream.<Property>of(
				propertiesFactory.createOptionalPropertyFacade(geoElements, geoElement ->
						new ScriptLanguageSelectionProperty(localization, geoElement, scriptEvent,
								scriptLanguageSelection, jsEnabled),
						NamedEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(geoElements, geoElement ->
						new ScriptProperty(localization, geoElement, scriptEvent,
								scriptLanguageSelection), StringPropertyListFacade::new)
		).filter(Objects::nonNull).toArray(Property[]::new));
	}

	private static ScriptType initialScriptLanguage(GeoElement geoElement,
			ScriptEvent scriptEvent) {
		if (scriptEvent == ScriptEvent.GlobalJavascript) {
			return ScriptType.JAVASCRIPT;
		}
		Script existingScript = geoElement.getScript(scriptEvent.eventType);
		return existingScript != null ? existingScript.getType() : ScriptType.GGBSCRIPT;
	}
}
