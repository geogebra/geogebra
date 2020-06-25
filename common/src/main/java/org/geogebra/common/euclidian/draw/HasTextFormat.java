package org.geogebra.common.euclidian.draw;

public interface HasTextFormat {

	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	void format(String key, Object val);

	/**
	 * @param key formatting option name
	 * @param fallback fallback when not set / indeterminate
	 * @param <T> option type
	 * @return formatting option value or fallback
	 */
	<T> T getFormat(String key, T fallback);

	/**
	 * @return hyperlink of selected part, or at the end of text element if no selection
	 */
	String getHyperLinkURL();

	/**
	 * @param url
	 *         (absolute) link URL
	 */
	void setHyperlinkUrl(String url);

	/**
	 * @return the plaintext representation of the hyperlink range
	 */
	String getHyperlinkRangeText();

	/**
	 * Inserts formatted hyperlink at the current selection
	 */
	void insertHyperlink(String url, String text);

	/**
	 * Returns the style of selected text
	 * @return "number" or "bullet"
	 */
	String getListStyle();

	/**
	 * Switch the list type of selected text
	 * @param listType - numbered or bullet list
	 */
	void switchListTo(String listType);

	boolean copySelection();

	void setSelectionText(String text);
}
