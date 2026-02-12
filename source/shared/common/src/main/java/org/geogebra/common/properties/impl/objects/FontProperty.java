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
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class FontProperty extends AbstractNamedEnumeratedProperty<FontProperty.FontFamily> {
	public enum FontFamily {
		TEST("Test font", "Druckshrift-1110-3-Regular, sans-serif"),
		TEST2("Test font 2", "BAyeishedruckschriftHasselt-OrangeL1L2, snas-serif"),
		ARIAL("Arial", "Arial, sans-serif"),
		CALIBRI("Calibri", "Calibri, sans-serif"),
		CENTURY_GOTHIC("Century Gothic", "Century Gothic, sans-serif"),
		COMIC_SANS("Comic Sans", "Comic Sans MS, sans-serif"),
		COURIER("Courier", "Courier, monospace"),
		GEORGIA("Georgia", "Georgia, serif"),
		DYSLEXIC("Open dyslexic mit Fibel a", "OpenDyslexicAlta"
				+ ", sans-serif"),
		PALATINO("Palatino", "Palatino Linotype, serif"),
		QUICKSAND("Quicksand", "Quicksand, sans-serif"),
		ROBOTO("Roboto", "Roboto, sans-serif"),
		SCHULBUCH_BAYERN("Schulbuch Bayern", "schulbuchbayerncomp-webfont, sans-serif"),
		SF_MONO("SF Mono", "SF Mono, monospace"),
		SF_PRO("SF Pro", "SF Pro, sans-serif"),
		SOURCE_SANS_PRO("Source Sans Pro", "SourceSansPro, sans-serif"),
		TIMES("Times", "Times, serif"),
		TITILLIUM("Titillium Web", "TitilliumWeb, sans-serif"),
		TREBUCHET("Trebuchet", "Trebuchet MS, sans-serif"),
		VERDANA("Verdana", "Verdana, sans-serif"),
		ABeZehBlueRedEDUBold("ABeZehBlueRedEDU-Bold", "ABeZehBlueRedEDU-Bold, sans-serif"),
		ABeZehBlueRedEDULight("ABeZehBlueRedEDU-Light", "ABeZehBlueRedEDU-Light, sans-serif"),
		ABeZehBlueRedEDURegular("ABeZehBlueRedEDU-Regular", "ABeZehBlueRedEDU-Regular,"
				+ " sans-serif"),
		ABeZehEDUBold("ABeZehEDU-Bold", "ABeZehEDU-Bold, sans-serif"),
		ABeZehEDUBoldItalic("ABeZehEDU-BoldItalic", "ABeZehEDU-BoldItalic, sans-serif"),
		ABeZehEDUItalic("ABeZehEDU-Italic", "ABeZehEDU-Italic, sans-serif"),
		ABeZehEDULight("ABeZehEDU-Light", "ABeZehEDU-Light, sans-serif"),
		ABeZehEDULightItalic("ABeZehEDU-LightItalic", "ABeZehEDU-LightItalic, sans-serif"),
		ABeZehEDURegular("ABeZehEDU-Regular", "ABeZehEDU-Regular, sans-serif"),
		ABeZehHokuspokusEDUDEBold("ABeZehHokuspokusEDUDE-Bold",
				"ABeZehHokuspokusEDUDE-Bold, sans-serif"),
		ABeZehHokuspokusEDUDERegular("ABeZehHokuspokusEDUDE-Regular",
				"ABeZehHokuspokusEDUDE-Regular, sans-serif"),
		ABeZehHokuspokusEDUENBold("ABeZehHokuspokusEDUEN-Bold",
				"ABeZehHokuspokusEDUEN-Bold, sans-serif"),
		ABeZehHokuspokusEDUENRegular("ABeZehHokuspokusEDUEN-Regular",
				"ABeZehHokuspokusEDUEN-Regular, sans-serif"),
		ABeZehIconsEDUDeutsch("ABeZehIconsEDU-Deutsch", "ABeZehIconsEDU-Deutsch, sans-serif"),
		ABeZehIconsEDUEnglish("ABeZehIconsEDU-English", "ABeZehIconsEDU-English, sans-serif"),
		ABeZehIconsEDUFrancais("ABeZehIconsEDU-Francais", "ABeZehIconsEDU-Francais, sans-serif"),
		ABeZehLinieEDULight("ABeZehLinieEDU-Light", "ABeZehLinieEDU-Light, sans-serif"),
		ABeZehLinieEDURegular("ABeZehLinieEDU-Regular", "ABeZehLinieEDU-Regular, sans-serif"),
		ABeZehPfeilEDULight("ABeZehPfeilEDU-Light", "ABeZehPfeilEDU-Light, sans-serif"),
		ABeZehPfeilEDURegular("ABeZehPfeilEDU-Regular", "ABeZehPfeilEDU-Regular, sans-serif"),
		ABeZehPfeilEDULINKSLight("ABeZehPfeilEDULINKS-Light", "ABeZehPfeilEDULINKS-Light"
				+ ", sans-serif"),
		ABeZehPunktEDULight("ABeZehPunktEDU-Light", "ABeZehPunktEDU-Light, sans-serif"),
		ABeZehPunktEDURegular("ABeZehPunktEDU-Regular", "ABeZehPunktEDU-Regular, sans-serif");

		private final String displayName;
		private final String cssName;

		FontFamily(String displayName, String cssName) {
			this.displayName = displayName;
			this.cssName = cssName;
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
			return isBycs ? Arrays.asList(FontFamily.values())
					: Arrays.asList(FontFamily.ARIAL, FontFamily.CALIBRI,
					FontFamily.CENTURY_GOTHIC, FontFamily.COMIC_SANS, FontFamily.COURIER,
					FontFamily.GEORGIA, FontFamily.ROBOTO, FontFamily.SF_MONO, FontFamily.SF_PRO,
					FontFamily.TIMES, FontFamily.TREBUCHET, FontFamily.VERDANA);
		}
	}

	private final HasTextFormatter geoElement;

	/** Font property for inline elements.
	 * @param localization localization
	 * @param geoElement geo element
	 */
	public FontProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "Font");
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
			formatter.format("font", value.cssName());
		}
	}

	@Override
	public FontFamily getValue() {
		HasTextFormat formatter = geoElement.getFormatter();
		if (formatter != null) {
			String font = formatter.getFormat("font", "");
			for (FontFamily family : FontFamily.values()) {
				if (font.equals(family.cssName())) {
					return family;
				}
			}
		}
		return FontFamily.TEST;
	}
}
