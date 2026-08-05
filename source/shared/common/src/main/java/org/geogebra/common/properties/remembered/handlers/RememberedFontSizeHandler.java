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

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.impl.objects.NotesFontSizeProperty;
import org.geogebra.common.properties.remembered.RememberedPropertyHandler;
import org.jspecify.annotations.NonNull;

/**
 * Applies remembered font-size values to inline text, mind-map and inline table elements.
 */
public final class RememberedFontSizeHandler extends RememberedPropertyHandler<String> {
	@Override
	public @NonNull PropertyKey propertyKey() {
		return PropertyKey.of(NotesFontSizeProperty.class);
	}

	@Override
	public boolean supports(@NonNull GeoElement geo) {
		return geo instanceof GeoInlineText || geo instanceof GeoMindMapNode
				|| geo instanceof GeoInlineTable;
	}

	@Override
	public boolean apply(@NonNull GeoElement geo, @NonNull String value) {
		if (!supports(geo)) {
			return false;
		}
		Integer fontSize = NotesFontSizeProperty.parse(value);
		if (fontSize == null) {
			return false;
		}

		HasTextFormat formatter = ((HasTextFormatter) geo).getFormatter();
		if (formatter == null) {
			return false;
		}

		formatter.format("size", fontSize.doubleValue());
		return true;
	}
}
