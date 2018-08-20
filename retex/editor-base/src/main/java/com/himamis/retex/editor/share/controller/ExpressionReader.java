package com.himamis.retex.editor.share.controller;

public interface ExpressionReader {

	String localize(String key, String... parameters);

	String mathExpression(String serialize);

	String power(String serialize, String serialize2);

	String fraction(String numerator, String denominator);

	String squareRoot(String arg);

}
