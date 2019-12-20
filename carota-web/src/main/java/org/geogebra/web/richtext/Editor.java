package org.geogebra.web.richtext;

import com.google.gwt.user.client.ui.Widget;

/** The interface to the Carota editor */
public interface Editor {

	interface EditorChangeListener {

		/**
		 * Called 0.5s after the last change in the editor state
		 * @param content the JSON encoded content of the editor
		 */
		void onContentChanged(String content);

		/**
		 * Called instantly on editor state change
		 */
		void onSizeChanged();
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
	void addListener(EditorChangeListener listener);

	/**
	 * Set the content of the editor
	 * @param content JSON encoded string in Carota format
	 */
	void setContent(String content);

	void deselect();

	void format(String key, Object val);
}
