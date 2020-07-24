package org.geogebra.web.richtext;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.Widget;

/** The interface to the Carota editor */
public interface Editor {

	void reload();

	interface EditorChangeListener {

		/**
		 * Called 0.5s after the last change in the editor state
		 * @param content the JSON encoded content of the editor
		 */
		void onContentChanged(String content);

		/**
		 * Called instantly on editor state change
		 *
		 * @param minHeight
		 *            minimum height in pixels
		 */
		void onSizeChanged(int minHeight);

		/**
		 * Called on selection change
		 */
		void onSelectionChanged();
	}

	/**
	 * Return the GWT widget that represents the editor.
	 *
	 * @return a GWT widget
	 */
	Widget getWidget();

	/**
	 * Focuses the editor.
	 */
	void focus(int x, int y);

	/**
	 * Sets the editor change listener
	 */
	void setListener(EditorChangeListener listener);

	/**
	 * Set the content of the editor
	 * @param content JSON encoded string in Carota format
	 */
	void setContent(String content);

	/**
	 * Deselect all text
	 */
	void deselect();

	/**
	 * Format selection or (if nothing selected) whole document.
	 *
	 * @param key
	 *            property name
	 * @param val
	 *            property value (double, bool or color string)
	 */
	void format(String key, Object val);

	/**
	 * @param <T>
	 *            parameter type (bool, string or double)
	 * @param key
	 *            property name
	 * @param fallback
	 *            fallback to use when format not found
	 * @return format property value
	 */
	<T> T getFormat(String key, T fallback);

	/**
	 * @return if part is selected, then hyperlink of selection,
	 * 		otherwise hyperlink at the end of text
	 */
	String getHyperLinkURL();

	void insertHyperlink(String url, String text);

	/**
	 * @return JSON encoded editor content
	 */
	String getContent();

	void draw(Context2d canvasElement);

	void setWidth(int width);

	String getHyperlinkRangeText();

	String getSelectionRangeText();

	void setSelection(String text);

	void setHyperlinkUrl(String input);

	String urlByCoordinate(int x, int y);

	void switchListTo(String listType);

	String getListStyle();
}
