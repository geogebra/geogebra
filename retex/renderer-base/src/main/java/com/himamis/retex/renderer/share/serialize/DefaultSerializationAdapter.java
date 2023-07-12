package com.himamis.retex.renderer.share.serialize;

public class DefaultSerializationAdapter implements SerializationAdapter {

	@Override
	public String subscriptContent(String base, String sub, String sup) {
		StringBuilder sb = new StringBuilder(base);
		if (sub != null && !sub.isEmpty()) {
			if (sub.length() > 1) {
				sb.append("_{").append(sub).append("}");
			} else {
				sb.append("_").append(sub);
			}
		}
		if (sup != null && !sup.isEmpty()) {
			if (sup.equals("\u2218")) { // convert ^[circle] to degree symbol
				sb.append("\u00b0");
			} else {
				sb.append("^(").append(sup).append(')');
			}
		}
		return sb.toString();
	}

	@Override
	public String transformBrackets(String left, String base, String right) {
		return left + base + right;
	}

	public String sqrt(String base) {
		return "sqrt(" + base + ")";
	}

	@Override
	public String convertCharacter(char character) {
		if ('\u00b7' == character) {
			return "\u00d7";
		}
		return String.valueOf(character);
	}

	@Override
	public String fraction(String numerator, String denominator) {
		return "(" + numerator + ")/("
				+ denominator + ")";
	}

	@Override
	public String nroot(String base, String root) {
		return "nroot(" + base + ","
				+ root + ")";
	}

	@Override
	public String parenthesis(String paren) {
		return paren;
	}

	@Override
	public String getLigature(String toString) {
		return toString;
	}

	@Override
	public String convertToReadable(String s) {
		return s;
	}
}
