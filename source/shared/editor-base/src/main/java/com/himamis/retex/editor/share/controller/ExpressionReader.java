package com.himamis.retex.editor.share.controller;

import com.himamis.retex.renderer.share.serialize.SerializationAdapter;

public interface ExpressionReader {

	String localize(String key, String... parameters);

	String power(String base, String exponent);

	void debug(String label);

	SerializationAdapter getAdapter();
}
