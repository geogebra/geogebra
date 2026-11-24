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

import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.jspecify.annotations.NonNull;

/** Settings page showing app or object properties as tabbed pages. */
public final class PropertiesPage extends SettingsPage {
	private final State<PropertyView.TabbedPageSelector> tabbedPageSelectorState;
	private final Runnable onDispose;

	PropertiesPage(PropertyView.@NonNull TabbedPageSelector tabbedPageSelector) {
		tabbedPageSelectorState = new MutableState<>(tabbedPageSelector);
		onDispose = tabbedPageSelector::detach;
	}

	PropertiesPage(@NonNull ObjectSettingsSession session) {
		tabbedPageSelectorState = session.getTree();
		onDispose = session::dispose;
	}

	/** @return the current property tree, updated when its elements are replaced */
	public @NonNull State<PropertyView.TabbedPageSelector> getTabbedPageSelectorState() {
		return tabbedPageSelectorState;
	}

	@Override
	public @NonNull String getTitle() {
		return tabbedPageSelectorState.get().getTitle();
	}

	@Override
	void dispose() {
		onDispose.run();
	}
}
