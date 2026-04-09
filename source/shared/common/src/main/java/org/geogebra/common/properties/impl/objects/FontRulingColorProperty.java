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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleUtil;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class FontRulingColorProperty extends ElementColorProperty
		implements ColorProperty {
	private final HasTextFormatter element;

	public enum FontStyle {
		BLACK("schwarz", GColor.newColorRGB(0x5C6470)),
		BLUE("tuerkis", GColor.newColorRGB(0x90CBC9)),
		GREEN("gruen", GColor.newColorRGB(0x6CB672)),
		ORANGE("orange", GColor.newColorRGB(0xDC7B3A));

		private final String fontName;
		private final GColor fontColor;

		FontStyle(String fontName, GColor fontColor) {
			this.fontName = fontName;
			this.fontColor = fontColor;
		}

		public String getFontName() {
			return fontName;
		}

		public GColor getFontColor() {
			return fontColor;
		}
	}

	private static final List<FontStyle> fontStyles = Arrays.stream(FontStyle.values()).toList();

	/**
	 * @param localization - localization
	 * @param element - element
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public FontRulingColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new FontStyleDelegate(element), "Lineaturfarbe");
		if (!FontStyleUtil.isFontStyleApplicable(element)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (HasTextFormatter) element;
		setValues(fontStyles.stream().map(FontStyle::getFontColor).collect(Collectors.toList()));
	}

	@Override
	public void doSetValue(GColor value) {
		HasTextFormat formatter = element.getFormatter();
		if (formatter != null) {
			String oldFont = formatter.getFormat("font", "");
			String newFont = getNewFont(getStringKeyFromColor(value));
			if (oldFont.startsWith("ByLineatur") && newFont != null) {
				formatter.format("font", newFont);
			}
		}
	}

	@Override
	public GColor getValue() {
		HasTextFormat formatter = element.getFormatter();
		if (formatter != null) {
			String font = formatter.getFormat("font", "");
			if (font.startsWith("ByLineatur")) {
				for (FontStyle entry : fontStyles) {
					String fontName = entry.getFontName();
					if (fontName != null && font.contains(entry.getFontName())) {
						return entry.getFontColor();
					}
				}
			}
		}
		return fontStyles.get(0).getFontColor();
	}

	private String getNewFont(String newColor) {
		HasTextFormat formatter = element.getFormatter();
		if (formatter != null) {
			String font = formatter.getFormat("font", "");
			if (font.startsWith("ByLineatur")) {
				for (FontStyle entry : fontStyles) {
					if (font.contains(entry.getFontName())) {
						return font.replace(entry.getFontName(), newColor);
					}
				}
			}
		}
		return "";
	}

	private static String getStringKeyFromColor(GColor color) {
		return fontStyles.stream().filter(font -> font.getFontColor().equals(color))
				.findAny().map(FontStyle::getFontName).get();
	}
}
