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

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class FontProperty extends AbstractNamedEnumeratedProperty<FontProperty.FontFamily>
		implements StyledItemProperty {
	public enum FontFamily {
		ARIAL("Arial", "Arial, sans-serif"),
		BY_DRUCK("By Druck", "ByDruck, sans-serif"),
		BY_DRUCK_LINEATUR_SCHWARZ("By Druck Lineatur 1+2", "ByLineatur-schwarz, sans-serif"),
		BY_DRUCK_LINEATUR_SCHWARZ_FARBBAND("By Druck Lineatur 1+2",
				"ByLineatur-schwarz-Farbband, sans-serif"),
		BY_DRUCK_LINEATUR_TUERKIS("By Druck Lineatur 1+2", "ByLineatur-tuerkis, sans-serif"),
		BY_DRUCK_LINEATUR_TUERKIS_FARBBAND("By Druck Lineatur 1+2",
				"ByLineatur-tuerkis-Farbband, sans-serif"),
		BY_DRUCK_LINEATUR_ORANGE_FARBBAND("By Druck Lineatur 1+2",
				"ByLineatur-orange-Farbband, sans-serif"),
		BY_DRUCK_LINEATUR_ORANGE("By Druck Lineatur 1+2", "ByLineatur-orange, sans-serif"),
		BY_DRUCK_LINEATUR_GRUEN_FARBBAND("By Druck Lineatur 1+2",
				"ByLineatur-gruen-Farbband, sans-serif"),
		BY_DRUCK_LINEATUR_GRUEN("By Druck Lineatur 1+2", "ByLineatur-gruen, sans-serif"),
		BY_LESEN("By Lesen", "ByLesen, sans-serif"),
		CALIBRI("Calibri", "Calibri, sans-serif"),
		COMIC_SANS("Comic Sans", "Comic Sans MS, sans-serif"),
		COURIER("Courier", "Courier, monospace"),
		GEORGIA("Georgia", "Georgia, serif"),
		DYSLEXIC("Open Dyslexic mit Fibel-a", "OpenDyslexicAlta"
				+ ", sans-serif"),
		TIMES("Times", "Times, serif"),
		TREBUCHET("Trebuchet", "Trebuchet MS, sans-serif"),
		VERDANA("Verdana", "Verdana, sans-serif");

		private final String displayName;
		private final String cssName;

		FontFamily(String displayName, String cssName) {
			this.displayName = displayName;
			this.cssName = cssName;
		}

		/**
		 * @param font font family stack
		 * @param fallback fallback font
		 * @return font family that exactly matches the CSS font family stack,
		 *         {@code fallback} if not found
		 */
		public static FontFamily getByCssName(String font, FontFamily fallback) {
			for (FontFamily family : FontFamily.values()) {
				if (font.equals(family.cssName())) {
					return family;
				}
			}
			return fallback;
		}

		/**
		 * @return display name
		 */
		public String displayName() {
			return displayName;
		}

		/**
		 * @return css name
		 */
		public String cssName() {
			return cssName;
		}

		/** Provides available fonts (application dependent).
		 * @param isBycs to distinguish between bycs and notes
		 * @return list of available fonts
		 */
		public static List<FontFamily> getAvailableFonts(boolean isBycs) {
			return isBycs ? Arrays.asList(FontFamily.ARIAL, FontFamily.BY_DRUCK,
					FontFamily.BY_DRUCK_LINEATUR_TUERKIS_FARBBAND, FontFamily.BY_LESEN,
					FontFamily.CALIBRI, FontFamily.COMIC_SANS, FontFamily.COURIER,
					FontFamily.GEORGIA, FontFamily.DYSLEXIC, FontFamily.TIMES,
					FontFamily.TREBUCHET, FontFamily.VERDANA)
					: Arrays.asList(FontFamily.ARIAL, FontFamily.CALIBRI,
					FontFamily.COMIC_SANS, FontFamily.COURIER,
					FontFamily.GEORGIA, FontFamily.TIMES,
					FontFamily.TREBUCHET, FontFamily.VERDANA);
		}
	}

	private final HasTextFormatter geoElement;

	/** Font property for inline elements.
	 * @param localization localization
	 * @param geoElement geo element
	 */
	public FontProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "ContextMenu.Font");
		if (!(geoElement instanceof HasTextFormatter)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = (HasTextFormatter) geoElement;
		List<FontFamily> availableFonts
				= FontFamily.getAvailableFonts(geoElement.getApp().isByCS());
		setValues(availableFonts);
		setNamedValues(availableFonts.stream()
				.map(fontFamily -> entry(fontFamily, fontFamily.displayName()))
				.collect(Collectors.toList()));
	}

	@Override
	protected void doSetValue(FontFamily value) {
		HasTextFormat formatter = geoElement.getFormatter();
		if (formatter != null) {
			formatter.formatFont(value.cssName());
		}
	}

	@Override
	public FontFamily getValue() {
		HasTextFormat formatter = geoElement.getFormatter();
		if (formatter != null) {
			String font = formatter.getFormat("font", "");
			return FontFamily.getByCssName(font, FontFamily.ARIAL);
		}
		return FontFamily.ARIAL;
	}

	@Override
	public Map<Integer, FontFamily> getFontFamilies() {
		Map<Integer, FontFamily> fontFamilies = new HashMap<>();
		int i = 0;
		for (FontFamily family : FontFamily.values()) {
			fontFamilies.put(i++, family);
		}
		return fontFamilies;
	}
}
