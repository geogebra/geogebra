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
import org.geogebra.common.properties.impl.objects.FontProperty;

public class FontStyleUtil {

	/**
	 * {@link org.geogebra.common.properties.impl.objects.FontRulingColorProperty} and
	 * {@link org.geogebra.common.properties.impl.objects.FontRulingProperty} is only applicable
	 * for BY_DRUCK_LINEATUR font family
	 * @param element geo element
	 * @return whether font ruling style is applicable for current font family
	 */
	public static boolean isFontStyleApplicable(GeoElement element) {
		FontProperty.DropdownGroup group = getFontGroup(element);
		return group == FontProperty.DropdownGroup.BY_DS_LERNEN_1_2
				|| group == FontProperty.DropdownGroup.BY_DS_LERNEN_3
				|| group == FontProperty.DropdownGroup.BY_DS_LERNEN_4;
	}

	/**
	 * Similar to {@link #isFontStyleApplicable(GeoElement)}.
	 * Needed to restrict some of the basic symbols for certain fonts
	 * ({@link FontProperty.DropdownGroup#BY_DS_LERNEN_3}
	 * and {@link FontProperty.DropdownGroup#BY_DS_LERNEN_4}).
	 * @param element GeoElement
	 * @return Whether font ruling style is applicable in a restricted way for the
	 * current font family.
	 */
	public static boolean isFontStyleApplicableWithLimitation(GeoElement element) {
		FontProperty.DropdownGroup group = getFontGroup(element);
		return group == FontProperty.DropdownGroup.BY_DS_LERNEN_3
				|| group == FontProperty.DropdownGroup.BY_DS_LERNEN_4;
	}

	/**
	 * {@link org.geogebra.common.properties.impl.objects.BoldProperty},
	 * {@link org.geogebra.common.properties.impl.objects.ItalicProperty} and
	 * {@link org.geogebra.common.properties.impl.objects.UnderlineProperty} cannot be applied on
	 * BY_DRUCK, BY_DRUCK_LINEATUR and BY_LESEN
	 * @param element geo element
	 * @return whether current font family supports typographical emphasis
	 */
	public static boolean isInlineWithSupportedFont(GeoElement element) {
		FontProperty.FontFamily fontFamily = getFontFamily(element);
		return fontFamily != null && !isByDSFont(fontFamily);
	}

	/**
	 * @param element element
	 * @return whether element is editable and supporting special symbols
	 */
	public static boolean isInlineWithSymbols(GeoElement element) {
		return isByDSFont(getFontFamily(element));
	}

	/**
	 * @param element element
	 * @return whether element is editable and supporting wurm symbol
	 */
	public static boolean isInlineWithWurm(GeoElement element) {
		return getFontFamily(element) == FontProperty.FontFamily.BY_DS_LERNEN_WURM;
	}

	/**
	 * @param fontFamily FontFamily
	 * @return Whether the given font is one of the 'ByDS' fonts.
	 */
	private static boolean isByDSFont(FontProperty.FontFamily fontFamily) {
		return fontFamily != null && fontFamily.name().startsWith("BY_DS");
	}

	/**
	 * @param element GeoElement
	 * @return The FontFamily associated with the given font.
	 */
	private static FontProperty.FontFamily getFontFamily(GeoElement element) {
		if (!(element instanceof HasTextFormatter hasTextFormatter)) {
			return null;
		}
		String fontFamilyName = hasTextFormatter.getFormat("font", "");
		return FontProperty.FontFamily.getByCssName(fontFamilyName, FontProperty.FontFamily.ARIAL);
	}

	/**
	 * @param element GeoElement
	 * @return The DropdownGroup associated with the given font.
	 */
	private static FontProperty.DropdownGroup getFontGroup(GeoElement element) {
		FontProperty.FontFamily fontFamily = getFontFamily(element);
		return fontFamily != null ? fontFamily.dropdownGroup() : null;
	}
}
