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

package org.geogebra.web.full.gui.view.algebra.compositefocus;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.web.full.gui.view.algebra.FocusableCompositeW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItemFocusAccess;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.gwtproject.user.client.ui.Widget;

/**
 * Contributes a single content row to composite focus for an algebra view item.
 *
 * <p>This contributor adds the main content part of an item that is rendered
 * using a single row, and applies appropriate accessibility metadata
 * for screen readers.</p>
 */
public final class AVSingleRepresentationContributor implements FocusContributor {
	private static final String AV_DEFINITION_VALUE_KEY = "av.definition.value";
	private static final String AV_CHECKBOX_KEY = "av.checkbox";
	private final Supplier<String> accessibleLabel;
	private final String roleDescription;

	/**
	 * Creates a contributor for items with a single accessible representation.
	 *
	 * <p>The contentProvider supplies the localized content text and
	 * role description used for accessibility announcements when the
	 * focusable parts are added to the composite focus.</p>
	 *
	 * @param contentProvider provides accessible text and role description
	 */
	public AVSingleRepresentationContributor(@Nonnull AccessibleContentProvider contentProvider) {
		this.accessibleLabel = contentProvider.getContentSupplier();
		this.roleDescription = contentProvider.getRoleDescription();
	}

	@Override
	public void contribute(RadioTreeItemFocusAccess item, FocusableCompositeW focus,
			AccessibilityManagerInterface am) {
			FocusPartAdder.addIfExists(focus, am, item.checkbox(), AV_CHECKBOX_KEY, () ->
					"checkbox");
		Widget widget = item.inputRow();
		Widget row = FocusPartAdder.addIfExists(focus, am,
				widget,
				AV_DEFINITION_VALUE_KEY,
				accessibleLabel);
		if (row != null) {
			AriaHelper.setRole(row, "status");
			AriaHelper.setRoleDescription(row, roleDescription);
		}
	}
}
