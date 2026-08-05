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
import org.jspecify.annotations.Nullable;

public class FontProperty extends AbstractNamedEnumeratedProperty<FontProperty.FontFamily>
		implements StyledItemProperty {

	/**
	 * Groups internal font variants that should be represented by a single item
	 * in the font dropdown.
	 */
	public enum DropdownGroup {
		BY_DS_SCHREIBEN_1_2("BY_DS_SCHREIBEN_1_2_TUERKIS_FARBBAND"),
		BY_DS_SCHREIBEN_3("BY_DS_SCHREIBEN_3_TUERKIS_FARBBAND"),
		BY_DS_SCHREIBEN_4("BY_DS_SCHREIBEN_4_TUERKIS_FARBBAND");

		private final String fontFamilyName;

		DropdownGroup(String fontFamilyName) {
			this.fontFamilyName = fontFamilyName;
		}

		private FontFamily fontFamily() {
			return FontFamily.valueOf(fontFamilyName);
		}
	}

	public enum FontFamily {
		ARIAL("Arial", "Arial, sans-serif"),
		BY_DS_SCHREIBEN_1_2_SCHWARZ("ByDS Schreiben 1+2", "ByLineatur-schwarz, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_SCHWARZ_FARBBAND("ByDS Schreiben 1+2",
				"ByLineatur-schwarz-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_TUERKIS("ByDS Schreiben 1+2", "ByLineatur-tuerkis, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_TUERKIS_FARBBAND("ByDS Schreiben 1+2",
				"ByLineatur-tuerkis-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_ORANGE_FARBBAND("ByDS Schreiben 1+2",
				"ByLineatur-orange-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_ORANGE("ByDS Schreiben 1+2", "ByLineatur-orange, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_GRUEN_FARBBAND("ByDS Schreiben 1+2",
				"ByLineatur-gruen-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_GRUEN("ByDS Schreiben 1+2", "ByLineatur-gruen, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_1_2),
		BY_DS_SCHREIBEN_1_2_OHNE_LINEATUR("ByDS Schreiben 1+2 (ohne Lineatur)",
				"ByDruck, sans-serif"),
		BY_DS_SCHREIBEN_3_SCHWARZ("ByDS Schreiben 3", "ByLineatur3-schwarz, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_SCHWARZ_FARBBAND("ByDS Schreiben 3",
				"ByLineatur3-schwarz-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_TUERKIS("ByDS Schreiben 3", "ByLineatur3-tuerkis, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_TUERKIS_FARBBAND("ByDS Schreiben 3",
				"ByLineatur3-tuerkis-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_ORANGE("ByDS Schreiben 3", "ByLineatur3-orange, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_ORANGE_FARBBAND("ByDS Schreiben 3",
				"ByLineatur3-orange-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_GRUEN("ByDS Schreiben 3", "ByLineatur3-gruen, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_GRUEN_FARBBAND("ByDS Schreiben 3",
				"ByLineatur3-gruen-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_3),
		BY_DS_SCHREIBEN_3_4_OHNE_LINEATUR("ByDS Schreiben 3+4 (ohne Lineatur)",
				"ByDruck2, sans-serif"),
		BY_DS_SCHREIBEN_4_SCHWARZ("ByDS Schreiben 4", "ByLineatur4-schwarz, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_4_SCHWARZ_FARBBAND("ByDS Schreiben 4",
				"ByLineatur4-schwarz-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_4_TUERKIS("ByDS Schreiben 4", "ByLineatur4-tuerkis, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_4_TUERKIS_FARBBAND("ByDS Schreiben 4",
				"ByLineatur4-tuerkis-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_4_ORANGE("ByDS Schreiben 4", "ByLineatur4-orange, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_4_ORANGE_FARBBAND("ByDS Schreiben 4",
				"ByLineatur4-orange-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_4_GRUEN("ByDS Schreiben 4", "ByLineatur4-gruen, sans-serif",
				DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_4_GRUEN_FARBBAND("ByDS Schreiben 4",
				"ByLineatur4-gruen-Farbband, sans-serif", DropdownGroup.BY_DS_SCHREIBEN_4),
		BY_DS_SCHREIBEN_KONTUR("ByDS Schreiben Kontur", "ByDS Schreiben Kontur, sans-serif"),
		BY_DS_SCHREIBEN_WURM("ByDS Schreiben Wurm", "ByDS Schreiben Wurm, sans-serif"),
		BY_DS_LESEN("ByDS Lesen", "ByLesen, sans-serif"),
		CALIBRI("Calibri", "Calibri, sans-serif"),
		COMIC_SANS("Comic Sans", "Comic Sans MS, sans-serif"),
		COURIER("Courier", "Courier, monospace"),
		GEORGIA("Georgia", "Georgia, serif"),
		DYSLEXIC("Open Dyslexic mit Fibel-a", "OpenDyslexicAlta, sans-serif"),
		TIMES("Times", "Times, serif"),
		TREBUCHET("Trebuchet", "Trebuchet MS, sans-serif"),
		VERDANA("Verdana", "Verdana, sans-serif");

		private final String displayName;
		private final String cssName;
		private final @Nullable DropdownGroup dropdownGroup;

		FontFamily(String displayName, String cssName) {
			this(displayName, cssName, null);
		}

		FontFamily(String displayName, String cssName, @Nullable DropdownGroup dropdownGroup) {
			this.displayName = displayName;
			this.cssName = cssName;
			this.dropdownGroup = dropdownGroup;
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

		/**
		 * @return DropdownGroup
		 */
		public DropdownGroup dropdownGroup() {
			return dropdownGroup;
		}

		/** Provides available fonts (application dependent).
		 * @param isBycs to distinguish between bycs and notes
		 * @return list of available fonts
		 */
		public static List<FontFamily> getAvailableFonts(boolean isBycs) {
			return isBycs ? Arrays.asList(FontFamily.ARIAL,
					FontFamily.BY_DS_SCHREIBEN_1_2_TUERKIS_FARBBAND,
					FontFamily.BY_DS_SCHREIBEN_1_2_OHNE_LINEATUR,
					FontFamily.BY_DS_SCHREIBEN_3_TUERKIS_FARBBAND,
					FontFamily.BY_DS_SCHREIBEN_3_4_OHNE_LINEATUR,
					FontFamily.BY_DS_SCHREIBEN_4_TUERKIS_FARBBAND,
					FontFamily.BY_DS_SCHREIBEN_KONTUR,
					FontFamily.BY_DS_SCHREIBEN_WURM, FontFamily.BY_DS_LESEN,
					FontFamily.CALIBRI, FontFamily.COMIC_SANS, FontFamily.COURIER,
					FontFamily.GEORGIA, FontFamily.DYSLEXIC, FontFamily.TIMES,
					FontFamily.TREBUCHET, FontFamily.VERDANA)
					: Arrays.asList(FontFamily.ARIAL, FontFamily.CALIBRI,
					FontFamily.COMIC_SANS, FontFamily.COURIER,
					FontFamily.GEORGIA, FontFamily.TIMES,
					FontFamily.TREBUCHET, FontFamily.VERDANA);
		}

		private FontFamily getFontFamilyForDropdown() {
			if (dropdownGroup == null) {
				return this;
			}
			return dropdownGroup.fontFamily();
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
			return FontFamily.getByCssName(font, FontFamily.ARIAL).getFontFamilyForDropdown();
		}
		return FontFamily.ARIAL;
	}

	@Override
	public Map<Integer, FontFamily> getFontFamilies() {
		Map<Integer, FontFamily> fontFamilies = new HashMap<>();
		int i = 0;
		for (FontFamily family : getValues()) {
			fontFamilies.put(i++, family);
		}
		return fontFamilies;
	}

	/**
	 * @return Whether the font cannot be determined. This is the case if a selection contains
	 * more than one font.
	 */
	public boolean hasIndeterminableFont() {
		HasTextFormat formatter = geoElement.getFormatter();
		return formatter != null && formatter.hasIndeterminableFont();
	}
}
