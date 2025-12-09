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

package org.geogebra.editor.share.catalog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Map of symbol templates indexed by name and unicode.
 */
public class SymbolTemplateMap implements Iterable<SymbolTemplate> {

	private final Map<String, SymbolTemplate> templates;

	SymbolTemplateMap() {
		templates = new HashMap<>();
	}

	/**
	 * Adds a template to the map, with the name and unicode String as keys.
	 * @param template the template to be added
	 */
	void addSymbol(SymbolTemplate template) {
		templates.put(template.getTag().toString(), template);
		templates.put(template.getUnicodeString(), template);
	}

	/**
	 * @param templateName the name of the template
	 * @return the template with name, otherwise null
	 */

	@CheckForNull public SymbolTemplate getSymbol(String templateName) {
		return templates.get(templateName);
	}

	@Override
	@Nonnull public Iterator<SymbolTemplate> iterator() {
		return templates.values().iterator();
	}
}
