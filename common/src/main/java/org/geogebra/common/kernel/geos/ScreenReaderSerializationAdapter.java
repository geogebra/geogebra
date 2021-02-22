package org.geogebra.common.kernel.geos;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;

import com.himamis.retex.renderer.share.serialize.SerializationAdapter;

public class ScreenReaderSerializationAdapter implements SerializationAdapter {

	private final Localization loc;

	public ScreenReaderSerializationAdapter(Localization loc) {
		this.loc = loc;
	}

	@Override
	public String subscriptContent(String base, String sub, String sup) {
		StringBuilder ret = new StringBuilder(base);
		if (sub != null) {
			ret.append(" start subscript ").append(sub).append(" end subscript ");
		}
		if (sup != null) {
			ret.append(' ');
			ScreenReader.appendPower(ret, sup, loc);
		}
		return ret.toString();
	}

	@Override
	public String transformBrackets(String left, String base, String right) {
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
		switch (character) {
		case '+': return " plus ";
		case '-': return " minus ";
		case '=': return " equals ";
		case ',':
			return ScreenReader.getComma();
		case '(':
			return ScreenReader.getOpenParenthesis();
		case ')':
			return ScreenReader.getCloseParenthesis();
		case '{':
			return " open brace ";
		case '}':
			return " close brace ";
		case '[':
			return " open bracket ";
		case ']':
			return " close bracket ";
		}
		return character + "";
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
}
