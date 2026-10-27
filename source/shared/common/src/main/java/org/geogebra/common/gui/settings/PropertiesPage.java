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

package org.geogebra.common.gui.settings;

import org.geogebra.common.properties.PropertyView;
import org.jspecify.annotations.NonNull;

/** Settings page showing app or object properties as tabbed pages. */
public final class PropertiesPage extends SettingsPage {

	private final PropertyView.TabbedPageSelector tabbedPageSelector;

	PropertiesPage(PropertyView.@NonNull TabbedPageSelector tabbedPageSelector) {
		this.tabbedPageSelector = tabbedPageSelector;
	}

	/** @return the tabbed property views to render */
	public PropertyView.@NonNull TabbedPageSelector getTabbedPageSelector() {
		return tabbedPageSelector;
	}

	@Override
	public @NonNull String getTitle() {
		return tabbedPageSelector.getTitle();
	}

	@Override
	void dispose() {
		tabbedPageSelector.detach();
	}
}
