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

package org.geogebra.common.contextmenu;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;

/**
 * Items in input context menu.
 */
public enum InputContextMenuItem implements ContextMenuItem {
	Expression("Expression", ContextMenuIcon.Expression),
	Text("Text", ContextMenuIcon.Text),
	Help("Help", ContextMenuIcon.Help),
	Image("Image", ContextMenuIcon.Image);

	private final String translationKey;
	private final ContextMenuIcon icon;

	InputContextMenuItem(String translationKey, ContextMenuIcon icon) {
		this.translationKey = translationKey;
		this.icon = icon;
	}

	@Override
	public @Nonnull AttributedString getLocalizedTitle(@Nonnull Localization localization) {
		return new AttributedString(localization.getMenu(translationKey));
	}

	@Override
	public ContextMenuIcon getIcon() {
		return icon;
	}
}
