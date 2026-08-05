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
import java.util.function.Predicate;
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

	private SpecialSymbolProperty(Localization loc, String groupName,
			Predicate<SpecialSymbol> filter, GeoElement geo) {
		super(loc, "Sonderzeichen");
		this.geo = geo;
		this.groupName = groupName;
		values = Stream.of(SpecialSymbol.values())
				.filter(filter).toList();
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
				FontProperty.FontFamily.BY_DS_SCHREIBEN_1_2_BLUE_FARBBAND);
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
		PUZZLE4("\ue004", "Teil 4"),

		WURM("\ue031", "Kopf");
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
				loc, "Differenzierung", byPrefix("DIFF"), geo);
		boolean limitBasicSymbols = FontStyleUtil.isFontStyleApplicableWithLimitation(geo);
		SpecialSymbolProperty basics = getBasicSpecialSymbolProperty(loc, geo, limitBasicSymbols);

		if (limitBasicSymbols) {
			return new PropertySupplier[] { basics, diffs };
		} else if (FontStyleUtil.isFontStyleApplicable(geo)) {
			SpecialSymbolProperty puzzles = new SpecialSymbolProperty(
					loc, "Puzzle", byPrefix("PUZZLE"), geo);
			return new PropertySupplier[] { basics, diffs, puzzles };
		} else if (FontStyleUtil.isInlineWithWurm(geo)) {
			SpecialSymbolProperty wurm = new SpecialSymbolProperty(
					loc, "Wurm", byPrefix("WURM"), geo);
			return new PropertySupplier[] { wurm, diffs };
		} else {
			return new PropertySupplier[] { diffs };
		}
	}

	private static Predicate<SpecialSymbol> byPrefix(String prefix) {
		return specialSymbol -> specialSymbol.name().startsWith(prefix);
	}

	/**
	 * @param localization Localization
	 * @param geo GeoElement
	 * @param limitBasicSymbols Whether the basic symbols are limited to the ones listed below.
	 * @return The property for the basic symbols used for the 'ByDS' fonts.
	 * @implNote 'ByDS Schreiben 3' and 'ByDS Schreiben 4' must contain only the basic symbols
	 * {@link SpecialSymbol#BASIC3}, {@link SpecialSymbol#BASIC4}, and {@link SpecialSymbol#BASIC5}
	 */
	private static SpecialSymbolProperty getBasicSpecialSymbolProperty(
			Localization localization, GeoElement geo, boolean limitBasicSymbols) {
		if (limitBasicSymbols) {
			return new SpecialSymbolProperty(localization, "Basiszeichen",
					List.of(SpecialSymbol.BASIC3, SpecialSymbol.BASIC4,
							SpecialSymbol.BASIC5)::contains, geo);
		}
		return new SpecialSymbolProperty(localization, "Basiszeichen", byPrefix("BASIC"), geo);
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
