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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.jspecify.annotations.NonNull;

/** {@code Property} responsible for selecting the script language (GgbScript or JavaScript). */
public class ScriptLanguageSelectionProperty extends AbstractNamedEnumeratedProperty<ScriptType> {
	private final ElementSettingsData elementSettingsData;
	private final GeoElement geoElement;
	private final App app;
	private final ScriptEvent scriptEvent;

	/**
	 * Constructs the property.
	 * @param localization localization for translating property names
	 * @param geoElement the element whose scripts are managed
	 * @param scriptEvent the script event for which the language selection is managed
	 * @param elementSettingsData temporary data retained by the settings context
	 * @param jsEnabled whether JavaScript is enabled in the app
	 * @throws NotApplicablePropertyException if the script language selection is not applicable
	 * for the given event
	 */
	public ScriptLanguageSelectionProperty(
			Localization localization,
			GeoElement geoElement,
			ScriptEvent scriptEvent,
			ElementSettingsData elementSettingsData,
			boolean jsEnabled)
			throws NotApplicablePropertyException {
		super(localization, "ScriptType");
		if (scriptEvent == ScriptEvent.GlobalJavascript || !jsEnabled) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
		this.app = geoElement.getApp();
		this.elementSettingsData = elementSettingsData;
		this.scriptEvent = scriptEvent;
	}

	@Override
	public @NonNull List<ScriptType> getValues() {
		return List.of(ScriptType.GGBSCRIPT, ScriptType.JAVASCRIPT);
	}

	@Override
	public String[] getValueNames() {
		return getValues().stream()
				.map(scriptType -> app.getLocalization().getMenu(scriptType.getName()))
				.toArray(String[]::new);
	}

	@Override
	protected void doSetValue(ScriptType scriptType) {
		elementSettingsData.scriptLanguages.put(scriptEvent, scriptType);
		Script existingScript = geoElement.getScript(scriptEvent.eventType);
		if (existingScript != null) {
			Script newScript = app.createScript(scriptType, existingScript.getText(), true);
			geoElement.setScript(newScript, scriptEvent.eventType);
		}
	}

	@Override
	public ScriptType getValue() {
		return getScriptLanguage(geoElement, scriptEvent, elementSettingsData);
	}

	static @NonNull ScriptType getScriptLanguage(
			@NonNull GeoElement element,
			@NonNull ScriptEvent event,
			@NonNull ElementSettingsData elementSettingsData) {
		Script script = element.getScript(event.eventType);
		if (script != null) {
			elementSettingsData.scriptLanguages.put(event, script.getType());
		}
		return elementSettingsData.scriptLanguages.getOrDefault(event, ScriptType.GGBSCRIPT);
	}
}
