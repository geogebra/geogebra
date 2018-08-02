package com.himamis.retex.editor.share.controller;

public interface ExpressionReader {

	String localize(String key, String... parameters);

	String mathExpression(String serialize);

}
