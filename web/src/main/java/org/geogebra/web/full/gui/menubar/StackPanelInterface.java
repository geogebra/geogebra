package org.geogebra.web.full.gui.menubar;

import org.gwtproject.event.dom.client.DomEvent.Type;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.safehtml.shared.annotations.IsSafeHtml;
import org.gwtproject.safehtml.shared.annotations.SuppressIsSafeHtmlCastCheck;
import org.gwtproject.user.client.EventListener;
import org.gwtproject.user.client.ui.InsertPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Interface for stack panels (accessible or otherwise)
 * 
 * @author Laszlo
 */
public interface StackPanelInterface extends InsertPanel, EventListener {
	/**
	 * Adds a new child with the given widget and header.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackText
	 *            the header text associated with this widget
	 */

	@SuppressIsSafeHtmlCastCheck
	void add(Widget w, String stackText);

	/**
	   * Adds a new child with the given widget and header, optionally interpreting
	   * the header as HTML.
	   *
	   * @param w the widget to be added
	   * @param stackHtml the header html associated with this widget
	   */
	void add(Widget w, SafeHtml stackHtml);

	/**
	 * Adds a new child with the given widget and header, optionally
	 * interpreting the header as HTML.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackText
	 *            the header text associated with this widget
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as HTML
	 */
	void add(Widget w, @IsSafeHtml String stackText, boolean asHTML);

	/**
	   * Gets the currently selected child index.
	   *
	   * @return selected child
	   */
	int getSelectedIndex();

	/**
	   * Sets the text associated with a child by its index.
	   *
	   * @param index the index of the child whose text is to be set
	   * @param text the text to be associated with it
	   */
	@SuppressIsSafeHtmlCastCheck
	void setStackText(int index, String text);

	/**
	   * Sets the html associated with a child by its index.
	   *
	   * @param index the index of the child whose text is to be set
	   * @param html the html to be associated with it
	   */
	void setStackText(int index, SafeHtml html);

	/**
	   * Sets the text associated with a child by its index.
	   *
	   * @param index the index of the child whose text is to be set
	   * @param text the text to be associated with it
	   * @param asHTML <code>true</code> to treat the specified text as HTML
	   */
	void setStackText(int index, @IsSafeHtml String text,
			boolean asHTML);

	/**
	   * Shows the widget at the specified child index.
	   *
	   * @param index the index of the child to be shown
	   */
	void showStack(int index);

	/**
	   * Adds the {@code styleName} on the {@code <tr>} for the header specified by {@code index}.
	   *
	   * @param index the index of the header row to apply to the style to
	   * @param styleName the name of the class to add
	   */
	void addHeaderStyleName(int index, String styleName);

	/**
	 * Removes the {@code styleName} off the {@code 
	 * <tr>
	 * } for the header specified by {@code index}.
	 *
	 * @param index
	 *            the index of the header row to remove the style from
	 * @param styleName
	 *            the name of the class to remove
	 */
	void removeHeaderStyleName(int index, String styleName);

	/**
	 * @param handler
	 *            handler
	 * @param type
	 *            event type
	 * @return event registration
	 */
	<H>
			HandlerRegistration addDomHandler(H handler,
			Type<H> type);

	/**
	 * @param string
	 *            style name
	 */
	void addStyleName(String string);

	/**
	 * @param w
	 *            widget
	 * @return whether it was removed
	 */
	boolean remove(Widget w);

	/**
	 * @return whether all stacks are collapsed
	 */
	boolean isCollapsed();

	/**
	 * @return selected index if visible; last selected if all stacks are
	 *         collapsed
	 */
	int getLastSelectedIndex();
}
