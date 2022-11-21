package org.geogebra.common.kernel.geos;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;

import com.himamis.retex.renderer.share.serialize.SerializationAdapter;

public class ScreenReaderSerializationAdapter implements SerializationAdapter {

	private final Localization loc;
	private final SymbolReader symbols;

	/**
	 *
	 * @param loc {@link Localization}
	 */
	public ScreenReaderSerializationAdapter(Localization loc) {
		this.loc = loc;
		symbols = new SymbolReader(loc);
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
			return "start absolute value " + base + " end absolute value";
		}
		if (base.isEmpty() && ScreenReader.getOpenParenthesis().equals(left)
				&& ScreenReader.getCloseParenthesis().equals(right)) {
			return "empty parentheses";
		}
		return readBracket(left) + base + readBracket(right);
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
		return "parenthesis";
	}

	@Override
	public String getLigature(String toString) {
		switch (toString) {
		case "``":
		case "''":
			return "\"";
		default: return toString;
		}
	}

	@Override
	public String convertToReadable(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char character = s.charAt(i);
			if (character == '_') {
				sb.append(" subscript ");
			} else {
				String str = convertCharacter(character);
				if (!"".equals(str)) {
					sb.append(str);
				}
			}
		}

		return sb.toString();
	}
}
