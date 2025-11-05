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

import javax.annotation.Nonnull;

/**
 * Callback that notifies when the syntax hint changes.
 */
public interface SyntaxTooltipUpdater {

	/**
	 * Callback to notify when to update the syntax hint.
	 * @param hint syntax hint
	 */
	void updateSyntaxTooltip(@Nonnull SyntaxHint hint);
}
