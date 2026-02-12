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

package org.geogebra.common.properties.impl.objects;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.properties.TextRotation;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class TextRotationProperty extends AbstractEnumeratedProperty<TextRotation>
		implements IconsEnumeratedProperty<TextRotation> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_TEXT_ROTATION_NONE, PropertyResource.ICON_TEXT_ROTATION_UP,
			PropertyResource.ICON_TEXT_ROTATION_DOWN
	};
	private final GeoInlineTable geoElement;
	private static final String[] rawLabels = {"ContextMenu.rotateNone", "ContextMenu.rotateUp",
		"ContextMenu.rotateDown"};

	/**
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param element the construction element
	 */
	public TextRotationProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "ContextMenu.textRotation");
		if (!(element instanceof GeoInlineTable)) {
			throw new NotApplicablePropertyException(element);
		}
		geoElement = (GeoInlineTable) element;
		setValues(List.of(TextRotation.NONE, TextRotation.UP, TextRotation.DOWN));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return rawLabels;
	}

	@Override
	protected void doSetValue(TextRotation value) {
		InlineTableController formatter = (InlineTableController) geoElement.getFormatter();
		if (getLocalization() != null && formatter != null
				&& !value.equals(TextRotation.fromString(formatter.getRotation()))) {
			formatter.setRotation(value.toString());
		}
		geoElement.updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public TextRotation getValue() {
		InlineTableController formatter = (InlineTableController) geoElement.getFormatter();
		if (formatter != null) {
			return TextRotation.fromString(formatter.getRotation());
		}
		return TextRotation.NONE;
	}
}
