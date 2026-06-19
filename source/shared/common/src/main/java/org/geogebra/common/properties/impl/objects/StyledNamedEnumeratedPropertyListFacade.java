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
import java.util.Map;

import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;

public class StyledNamedEnumeratedPropertyListFacade
		extends NamedEnumeratedPropertyListFacade<FontProperty, FontProperty.FontFamily>
		implements StyledItemProperty {

	/**
	 * @param properties properties to handle
	 */
	public StyledNamedEnumeratedPropertyListFacade(List<FontProperty> properties) {
		super(properties);
	}

	@Override
	public Map<Integer, FontProperty.FontFamily> getFontFamilies() {
		return getFirstProperty().getFontFamilies();
	}

	/**
	 * @return The selected font display name. If a selection contains more than one font,
	 * or multiple text elements with different fonts selected, returns an empty string.
	 */
	public String getSelectedFontDisplayName() {
		if (getIndex() == -1 || getFirstProperty().hasIndeterminableFont()) {
			return "";
		}
		FontProperty.FontFamily value = getValue();
		return value == null ? "" : value.displayName();
	}
}
