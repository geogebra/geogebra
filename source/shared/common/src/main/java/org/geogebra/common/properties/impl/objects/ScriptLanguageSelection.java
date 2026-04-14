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

import javax.annotation.Nonnull;

import org.geogebra.common.plugin.ScriptType;

/**
 * Script language selection owner, holding the state of the selected script language, enabling
 * passing between {@code Property}s, updating via {@link ScriptLanguageSelectionProperty}, reading
 * by other related {@code Property}s.
 */
public final class ScriptLanguageSelection {
	private ScriptType selectedScriptLanguage;

	/**
	 * Constructs with the given initial selection.
	 * @param initialScriptLanguage the initial script language
	 */
	public ScriptLanguageSelection(ScriptType initialScriptLanguage) {
		this.selectedScriptLanguage = initialScriptLanguage;
	}

	void setSelection(@Nonnull ScriptType scriptLanguage) {
		this.selectedScriptLanguage = scriptLanguage;
	}

	/**
	 * @return the selected script language
	 */
	public @Nonnull ScriptType getSelection() {
		return selectedScriptLanguage;
	}
}
