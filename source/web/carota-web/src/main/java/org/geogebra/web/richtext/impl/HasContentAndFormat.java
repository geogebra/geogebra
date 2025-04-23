package org.geogebra.web.richtext.impl;

import jsinterop.annotations.JsType;

/**
 * Methods for Murok table.
 * TODO either let CarotaDocument also implement this, or just remove this.
 */
@JsType(isNative = true)
public interface HasContentAndFormat extends HasContent {

	void setFormatting(String key, Object val);

	<T> T getFormatting(String key, T fallback);

	void init(int width, int height);

	void stopEditing();

	void setHyperlinkUrl(String url);

	void insertHyperlink(String url, String text);

	void switchListTo(String listType);

	void insert(String text);
}
