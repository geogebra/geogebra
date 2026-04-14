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

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;

/** {@code Property} responsible for setting the script for the given script language and event. */
public class ScriptProperty extends AbstractValuedProperty<String> implements StringProperty {
	private final GeoElement geoElement;
	private final App app;
	private final ScriptEvent scriptEvent;
	private final ScriptLanguageSelection scriptLanguageSelection;

	/**
	 * Constructs the property.
	 * @param localization localization for translating property names
	 * @param geoElement the element whose scripts are managed
	 * @param scriptEvent the script event for which to apply the script
	 * @param scriptLanguageSelection the script language selection for which to apply the script
	 */
	public ScriptProperty(Localization localization, GeoElement geoElement,
			ScriptEvent scriptEvent, ScriptLanguageSelection scriptLanguageSelection) {
		super(localization, "ScriptContent");
		this.geoElement = geoElement;
		this.app = geoElement.getApp();
		this.scriptEvent = scriptEvent;
		this.scriptLanguageSelection = scriptLanguageSelection;
	}

	@Override
	public String getValue() {
		if (scriptEvent == ScriptEvent.GlobalJavascript) {
			return app.getKernel().getLibraryJavaScript();
		}
		Script script = geoElement.getScript(scriptEvent.eventType);
		return script == null ? "" : script.getText();
	}

	@Override
	protected void doSetValue(String text) {
		if (scriptEvent == ScriptEvent.GlobalJavascript) {
			app.getKernel().setLibraryJavaScript(text);
			return;
		}
		Script script = app.createScript(scriptLanguageSelection.getSelection(), text, true);
		geoElement.setScript(script, scriptEvent.eventType);
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	public boolean isDisplayedAsTextArea() {
		return true;
	}
}
