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

import static org.geogebra.web.full.gui.view.algebra.compositefocus.FocusPartAdder.addIfExists;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.FocusableCompositeW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItemFocusAccess;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.gwtproject.user.client.ui.Widget;

/**
 * Contributes both definition and value parts to composite focus for items
 * that require dual representation.
 *
 * <p>This contributor adds an input (definition) row and, when available,
 * a separate output (value) row to the given composite focus container.
 * Each added row is labeled for assistive technologies using accessible
 * text and a localized role description.</p>
 */
public final class AVDualRepresentationContributor implements FocusContributor {
	private static final String AV_INPUT_ROW_KEY = "av.input.row";
	private static final String AV_OUTPUT_ROW_KEY = "av.output.row";
	private final Supplier<String> accessibleLabel;
	private final String roleDescription;

	/**
	 * Creates a contributor for items with dual accessible representation.
	 *
	 * <p>The contentProvider supplies the localized content text and
	 * role description used for accessibility announcements when the
	 * focusable parts are added to the composite focus.</p>
	 *
	 * @param contentProvider provides accessible text and role description
	 */
	public AVDualRepresentationContributor(@Nonnull AccessibleContentProvider contentProvider) {
		this.accessibleLabel = contentProvider.getContentSupplier();
		this.roleDescription = contentProvider.getRoleDescription();
	}

	@Override
	public void contribute(RadioTreeItemFocusAccess item, FocusableCompositeW focus,
			AccessibilityManagerInterface am) {
		GeoElement geo = item.geo();
		if (geo == null) {
			return;
		}
		Widget definitionRow = addIfExists(focus, am, item.inputRow(), AV_INPUT_ROW_KEY,
				accessibleLabel);
		if (definitionRow != null) {
			AriaHelper.setRole(definitionRow, "status");
			AriaHelper.setRoleDescription(definitionRow, roleDescription);
		}

		if (item.hasTwoRows()) {
			Widget valueRow = addIfExists(focus, am, item.outputRow(), AV_OUTPUT_ROW_KEY, () ->
				geo.toValueString(StringTemplate.defaultTemplate));
			if (valueRow != null) {
				AriaHelper.setRole(valueRow, "status");
				AriaHelper.setRoleDescription(valueRow, "Value");
			}
		}
	}
}
