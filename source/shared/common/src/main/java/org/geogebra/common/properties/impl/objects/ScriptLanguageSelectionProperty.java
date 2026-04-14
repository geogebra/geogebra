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

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/** {@code Property} responsible for selecting the script language (GgbScript or JavaScript). */
public class ScriptLanguageSelectionProperty extends AbstractNamedEnumeratedProperty<ScriptType> {
	private final ScriptLanguageSelection scriptLanguageSelection;
	private final GeoElement geoElement;
	private final App app;
	private final ScriptEvent scriptEvent;

	/**
	 * Constructs the property.
	 * @param localization localization for translating property names
	 * @param geoElement the element whose scripts are managed
	 * @param scriptEvent the script event for which the language selection is managed
	 * @param scriptLanguageSelection the script language selection state for this event
	 * @param jsEnabled whether JavaScript is enabled in the app
	 * @throws NotApplicablePropertyException if the script language selection is not applicable
	 * for the given event
	 */
	public ScriptLanguageSelectionProperty(Localization localization, GeoElement geoElement,
			ScriptEvent scriptEvent, ScriptLanguageSelection scriptLanguageSelection,
			boolean jsEnabled) throws NotApplicablePropertyException {
		super(localization, "ScriptType");
		if (scriptEvent == ScriptEvent.GlobalJavascript || !jsEnabled) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
		this.app = geoElement.getApp();
		this.scriptLanguageSelection = scriptLanguageSelection;
		this.scriptEvent = scriptEvent;
	}

	@Override
	public @Nonnull List<ScriptType> getValues() {
		return List.of(ScriptType.GGBSCRIPT, ScriptType.JAVASCRIPT);
	}

	@Override
	public String[] getValueNames() {
		return getValues().stream()
				.map(scriptType -> app.getLocalization().getMenu(scriptType.getName()))
				.toArray(String[]::new);
	}

	@Override
	public boolean isAvailable() {
		return getValues().size() > 1;
	}

	@Override
	protected void doSetValue(ScriptType scriptType) {
		scriptLanguageSelection.setSelection(scriptType);
		Script existingScript = geoElement.getScript(scriptEvent.eventType);
		if (existingScript != null) {
			Script newScript = app.createScript(scriptType, existingScript.getText(), true);
			geoElement.setScript(newScript, scriptEvent.eventType);
		}
	}

	@Override
	public ScriptType getValue() {
		return scriptLanguageSelection.getSelection();
	}
}
