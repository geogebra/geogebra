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

package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertySupplier;
import org.geogebra.common.properties.impl.AbstractProperty;
import org.geogebra.common.properties.impl.objects.FontProperty;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleUtil;

/**
 * Inserting symbols into text is implemented as a write-only property:
 * {@link #insertSymbol(SpecialSymbol)} changes the state of the text, but there is no meaningful
 * getter for this property.
 *
 * <p>All strings in this file are hard-coded in German, since these special symbols are only
 * available in special fonts used in German primary schools.</p>
 */
public final class SpecialSymbolProperty
		extends AbstractProperty {
	private final GeoElement geo;
	private final  String groupName;
	private final List<SpecialSymbol> values;

	private SpecialSymbolProperty(Localization loc, String groupName, String prefix,
			GeoElement geo) {
		super(loc, "Sonderzeichen");
		this.geo = geo;
		this.groupName = groupName;
		values = Stream.of(SpecialSymbol.values())
				.filter(s -> s.name().startsWith(prefix)).toList();
	}

	/**
	 * @return font family to be used for rendering this property
	 */
	public FontProperty.FontFamily getFontFamily() {
		HasTextFormat formatter = ((HasTextFormatter) geo).getFormatter();
		String fontFamily = "";
		if (formatter != null) {
			fontFamily = formatter.getFormat("font", "");
		}
		return FontProperty.FontFamily.getByCssName(fontFamily,
				FontProperty.FontFamily.BY_DRUCK_LINEATUR_TUERKIS_FARBBAND);
	}

	public String getGroupName() {
		return groupName;
	}

	public enum SpecialSymbol {
		BASIC1("[", "Haus links"),
		BASIC2("]", "Haus rechts"),
		BASIC3("|", "Zeilenendstrich"),
		BASIC4("\ue100", "Schmale Lineatur"),
		BASIC5("\ue000", "Leerzeichen"),

		DIFF1("\ue028", "Einfach"),
		DIFF2("\ue029", "Mittel"),
		DIFF3("\ue030", "Schwierig"),

		PUZZLE1("\ue001", "Teil 1"),
		PUZZLE2("\ue002", "Teil 2"),
		PUZZLE3("\ue003", "Teil 3"),
		PUZZLE4("\ue004", "Teil 4");
		public final String symbol;
		public final String description;

		SpecialSymbol(String symbol, String description) {
			this.symbol = symbol;
			this.description = description;
		}
	}

	/**
	 * Properties for given objects.
	 * @param loc localization
	 * @param activeGeoList geos
	 * @return properties
	 */
	public static PropertySupplier[] forGeos(Localization loc, List<GeoElement> activeGeoList) {
		GeoElement geo = activeGeoList.size() == 1 ? activeGeoList.get(0) : null;
		if (!FontStyleUtil.isInlineWithSymbols(geo)) {
			return new PropertySupplier[0];
		}
		SpecialSymbolProperty diffs = new SpecialSymbolProperty(
				loc, "Differenzierung", "DIFF", geo);
		if (FontStyleUtil.isFontStyleApplicable(geo)) {
			SpecialSymbolProperty basics = new SpecialSymbolProperty(
					loc, "Basiszeichen", "BASIC", geo);
			SpecialSymbolProperty puzzles = new SpecialSymbolProperty(
					loc, "Puzzle", "PUZZLE", geo);
			return new PropertySupplier[] { basics, diffs, puzzles };
		} else {
			return new PropertySupplier[] { diffs};
		}
	}

	/**
	 * @param value symbol to insert
	 */
	public void insertSymbol(SpecialSymbol value) {
		HasTextFormat formatter = ((HasTextFormatter) geo).getFormatter();
		if (formatter != null) {
			formatter.setSelectionText(value.symbol);
		}
	}

	public List<SpecialSymbol> getValues() {
		return values;
	}
}
