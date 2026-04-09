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

package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;

public class FontStyleUtil {
	/**
	 * {@link org.geogebra.common.properties.impl.objects.FontRulingColorProperty} and
	 * {@link org.geogebra.common.properties.impl.objects.FontRulingProperty} is only applicable
	 * for BY_DRUCK_LINEATUR font family
	 * @param element geo element
	 * @return whether font ruling style is applicable for current font family
	 */
	public static boolean isFontStyleApplicable(GeoElement element) {
		return element instanceof HasTextFormatter hasTextFormatter
				&& hasTextFormatter.getFormatter() != null
				&& hasTextFormatter.getFormat("font", "").startsWith("ByLineatur");
	}

	/**
	 * {@link org.geogebra.common.properties.impl.objects.BoldProperty},
	 * {@link org.geogebra.common.properties.impl.objects.ItalicProperty} and
	 * {@link org.geogebra.common.properties.impl.objects.UnderlineProperty} cannot be applied on
	 * BY_DRUCK, BY_DRUCK_LINEATUR and BY_LESEN
	 * @param element geo element
	 * @return whether current font famility supports typographical emphasis
	 */
	public static boolean isInlineWithSupportedFont(GeoElement element) {
		return element instanceof HasTextFormatter formattedElement
				&& !formattedElement.getFormat("font", "").startsWith("By");
	}
}
