package com.himamis.retex.editor.share.controller;

public interface ExpressionReader {

	String localize(String key, String... parameters);

	String mathExpression(String serialize);

	String power(String base, String exponent);

	String fraction(String numerator, String denominator);

	String squareRoot(String arg);

	String nroot(String radicand, String index);

	String inParentheses(String content);

}
