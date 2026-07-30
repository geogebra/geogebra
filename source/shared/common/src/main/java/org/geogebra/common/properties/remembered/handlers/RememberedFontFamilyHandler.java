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

package org.geogebra.common.properties.remembered.handlers;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.impl.objects.FontProperty;
import org.geogebra.common.properties.remembered.RememberedPropertyHandler;

/**
 * Applies remembered font-family values to inline text, mind-map and inline table elements.
 */
public final class RememberedFontFamilyHandler
		extends RememberedPropertyHandler<FontProperty.FontFamily> {
	@Override
	public @Nonnull PropertyKey propertyKey() {
		return PropertyKey.of(FontProperty.class);
	}

	@Override
	public boolean supports(@Nonnull GeoElement geo) {
		return geo instanceof GeoInlineText || geo instanceof GeoMindMapNode
				|| geo instanceof GeoInlineTable;
	}

	@Override
	public boolean apply(@Nonnull GeoElement geo, @Nonnull FontProperty.FontFamily value) {
		if (!supports(geo)) {
			return false;
		}

		HasTextFormat formatter = ((HasTextFormatter) geo).getFormatter();
		if (formatter != null) {
			formatter.formatFont(value.cssName());
			return true;
		}
		return false;
	}
}
