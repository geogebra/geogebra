/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
