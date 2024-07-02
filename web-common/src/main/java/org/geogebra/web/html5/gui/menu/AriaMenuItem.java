package org.geogebra.web.html5.gui.menu;

import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.client.Document;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Accessible menu item: use &lt;li&gt; instead of &lt;td&gt; as a tag
 *
 */
public class AriaMenuItem extends SimplePanel {
	private ScheduledCommand cmd;
	private AriaMenuBar submenu;
	private boolean enabled = true;
	private boolean focusable = true;
	private Widget submenuHeading;

	/**
	 * @param text
	 *            content
	 * @param asHTML
	 *            whether to use it as raw HTML
	 * @param cmd
	 *            command to run when clicked
	 */
	public AriaMenuItem(String text, boolean asHTML, ScheduledCommand cmd) {
		this();
		setContent(text, asHTML);
		this.cmd = cmd;
	}

	/**
	 * @param text
	 *            content
	 * @param asHTML
	 *            whether to use it as raw HTML
	 * @param submenu
	 *            submenu to open when clicked
	 */
	public AriaMenuItem(String text, boolean asHTML, AriaMenuBar submenu) {
		this();
		setContent(text, asHTML);
		this.submenu = submenu;
	}

	/**
	 * Constructor
	 */
	public AriaMenuItem() {
		super(Document.get().createLIElement());
		getElement().setClassName("gwt-MenuItem listMenuItem keyboardFocus");
		getElement().setAttribute("role", "menuitem");
		getElement().setTabIndex(0);
	}

	/**
	 * @param text
	 *            content
	 * @param asHTML
	 *            whether to parse it as HTML
	 */
	public void setContent(String text, boolean asHTML) {
		if (asHTML) {
			getElement().setInnerHTML(text);
		} else {
			getElement().setInnerText(text);
		}
	}

	/**
	 * @return command
	 */
	public ScheduledCommand getScheduledCommand() {
		return cmd;
	}

	/**
	 * @param enabled
	 *            whether this button is active
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			removeStyleName("gwt-MenuItem-disabled");
		} else {
			addStyleName("gwt-MenuItem-disabled");
		}
		this.enabled = enabled;
	}

	/**
	 * @param cmd
	 *            command to run when clicked
	 */
	public void setScheduledCommand(ScheduledCommand cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return submenu
	 */
	public AriaMenuBar getSubMenu() {
		return submenu;
	}

	/**
	 * @return content as HTML
	 */
	public String getHTML() {
		return getElement().getInnerHTML();
	}

	public String getText() {
		return getElement().getInnerText();
	}

	/**
	 * @param string
	 *            content as HTML
	 */
	public void setHTML(String string) {
		setContent(string, true);
	}

	/**
	 * @return whether the item is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	public boolean isFocusable() {
		return focusable;
	}

	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
	}

	public void setSubmenuHeading(Widget label) {
		this.submenuHeading = label;
	}

	public Widget getSubmenuHeading() {
		return submenuHeading;
	}
}
