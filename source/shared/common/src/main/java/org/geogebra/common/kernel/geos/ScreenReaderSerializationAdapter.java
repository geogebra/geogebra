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

package org.geogebra.common.kernel.geos;

import java.util.Locale;

import org.geogebra.common.io.ScreenReaderTableAdapter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;

import com.himamis.retex.renderer.share.serialize.SerializationAdapter;
import com.himamis.retex.renderer.share.serialize.TableAdapter;

public class ScreenReaderSerializationAdapter implements SerializationAdapter {

	private final Localization loc;
	private final SymbolReader symbols;
	private final TableAdapter tableAdapter;

	/**
	 *
	 * @param loc {@link Localization}
	 */
	public ScreenReaderSerializationAdapter(Localization loc) {
		this.loc = loc;
		symbols = new SymbolReader(loc);
		tableAdapter = new ScreenReaderTableAdapter(loc);
	}

	@Override
	public String subscriptContent(String base, String sub, String sup) {
		StringBuilder ret = new StringBuilder(base);
		if (sub != null) {
			ret.append(" start subscript ").append(sub).append(" end subscript ");
		}

		if (sup != null) {
			ret.append(' ');
			if (isDegrees(sup)) {
				ret.append("1".equals(base) ? ScreenReader.getDegree(loc)
						: ScreenReader.getDegrees(loc));
			} else {
				ScreenReader.appendPower(ret, sup, loc);
			}
		}
		return ret.toString();
	}

	private boolean isDegrees(String sup) {
		return ScreenReader.getDegrees(loc).equals(sup)
				|| ScreenReader.getDegree(loc).equals(sup);
	}

	@Override
	public String transformBrackets(String left, String base, String right) {
		if ("|".equals(left) && "|".equals(right)) {
			return ScreenReader.getStartAbs(loc) + base + ScreenReader.getEndAbs(loc);
		}
		if (base.isEmpty() && ScreenReader.getOpenParenthesis(loc).equals(left)
				&& ScreenReader.getCloseParenthesis(loc).equals(right)) {
			return localize("EmptyParentheses", "empty parentheses");
		}
		return readBracket(left) + base + readBracket(right);
	}

	private String localize(String key, String defaultValue) {
		return loc.getMenuDefault("ScreenReader." + key, defaultValue);
	}

	private String readBracket(String left) {
		if (left.length() == 1) {
			return convertCharacter(left.charAt(0));
		}
		return left;
	}

	@Override
	public String sqrt(String base) {
		return ScreenReader.nroot(base, "2", loc);
	}

	@Override
	public String convertCharacter(char character) {
		return symbols.get(character);
	}

	@Override
	public String fraction(String numerator, String denominator) {
		StringBuilder sb = new StringBuilder();
		ScreenReader.fraction(sb, numerator, denominator, loc);
		return sb.toString();
	}

	@Override
	public String nroot(String base, String root) {
		return ScreenReader.nroot(base, root, loc);
	}

	@Override
	public String parenthesis(String paren) {
		return localize("Parenthesis", "parenthesis");
	}

	@Override
	public String getLigature(String toString) {
		switch (toString) {
		case "``":
		case "''":
			return "\"";
		case "\u0338=":
			return "\u2260";
		case "\u0338\u2208":
			return "\u2209";
		default:
			return null;
		}
	}

	@Override
	public String convertToReadable(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char character = s.charAt(i);
			if (character == '_') {
				sb.append(" ").append(localize("Subscript", "subscript"))
						.append(" ");
			} else {
				String str = convertCharacter(character);
				if (!"".equals(str)) {
					sb.append(str);
				}
			}
		}

		return sb.toString();
	}

	@Override
	public TableAdapter getTableAdapter() {
		return this.tableAdapter;
	}

	@Override
	public String segment(String base) {
		// order hardcoded, same in e.g. properties view
		return loc.getMenu("Segment").toLowerCase(Locale.ROOT) + " " + base;
	}

	@Override
	public String vector(String content) {
		// order hardcoded, same in e.g. properties view
		return loc.getMenu("Vector").toLowerCase(Locale.ROOT) + " " + content;
	}

	@Override
	public String circled(String serialize) {
		return loc.getPlainDefault("ScreenReader.Circled", "circled %0", serialize);
	}

	@Override
	public String under(String decoration, String base) {
		return loc.getPlainDefault("ScreenReader.AUnderB", "%0 under %1", decoration, base);
	}

	@Override
	public String over(String decoration, String base) {
		return loc.getPlainDefault("ScreenReader.AOverB", "%0 over %1", decoration, base);
	}

	@Override
	public String blank() {
		return localize("Blank", "blank");
	}

	@Override
	public String operatorFromTo(String operator, String from, String to) {
		return loc.getPlainDefault("ScreenReader.AFromBToC", "%0 from %1 to %2",
				operator, from, to) + " ";
	}

	@Override
	public String hyperbolic(String baseName) {
		return " " + localize("Hyperbolic", "hyperbolic") + " " + baseName;
	}
}
