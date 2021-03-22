package com.himamis.retex.renderer.share.serialize;

public class DefaultSerializationAdapter implements SerializationAdapter {

	@Override
	public String subscriptContent(String base, String sub, String sup) {
		StringBuilder sb = new StringBuilder(base);
		if (sub != null) {
			if (sub.length() > 1) {
				sb.append("_{" + sub + "}");
			} else {
				sb.append("_" + sub);
			}
		}
		if (sup != null) {
			sb.append("^(").append(sup).append(')');
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

	public String convertCharacter(char character) {
		return character + "";
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
}
