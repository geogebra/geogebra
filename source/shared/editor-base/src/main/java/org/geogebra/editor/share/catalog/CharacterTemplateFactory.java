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

import javax.annotation.CheckForNull;

/**
 * Factory for creating single character templates.
 */
class CharacterTemplateFactory {

	@CheckForNull CharacterTemplate createCharacter(String name) {
		if (name == null || name.length() != 1) {
			return null;
		}
		char code = name.charAt(0);
		return new CharacterTemplate(name, code, CharacterTemplate.TYPE_CHARACTER);
	}

}
