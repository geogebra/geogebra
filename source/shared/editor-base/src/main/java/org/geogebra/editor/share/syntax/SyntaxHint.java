/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.syntax;

/**
 * Syntax hint.
 */
public interface SyntaxHint {

	/**
	 * @return parts before the active placeholder
	 */
	String getPrefix();

	/**
	 * @return active placeholder
	 */
	String getActivePlaceholder();

	/**
	 * @return parts after the active placeholder
	 */
	String getSuffix();

	/**
	 * Tests if the syntax hint is empty.
	 * The prefix, placeholder and suffix will return empty string in this case.
	 * @return true if there is no hint.
	 */
	boolean isEmpty();
}
