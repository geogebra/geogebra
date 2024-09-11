package org.geogebra.web.html5.gui.menu;

import javax.annotation.CheckForNull;

import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.client.Document;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.Text;
import jsinterop.base.Js;

/**
 * Accessible menu item: use &lt;li&gt; instead of &lt;td&gt; as a tag
 *
 */
public class AriaMenuItem extends SimplePanel implements HasResource {
	private ScheduledCommand cmd;
	private AriaMenuBar submenu;
	private boolean enabled = true;
	private boolean focusable = true;
	private Widget submenuHeading;
	private Text textNode;
	private HTMLElement img;

	/**
	 * @param text
	 *            content
	 * @param icon
	 *            icon
	 * @param cmd
	 *            command to run when clicked
	 */
	public AriaMenuItem(String text, ResourcePrototype icon, ScheduledCommand cmd) {
		this();
		setContent(text, icon);
		this.cmd = cmd;
	}

	/**
	 * @param text
	 *            content
	 * @param icon
	 *            icon
	 * @param submenu
	 *            submenu to open when clicked
	 */
	public AriaMenuItem(String text, ResourcePrototype icon, AriaMenuBar submenu) {
		this();
		setContent(text, icon);
		this.submenu = submenu;
	}

	/**
	 * @param renderer contained widget
	 * @param command action
	 */
	public AriaMenuItem(Widget renderer, ScheduledCommand command) {
		this();
		setWidget(renderer);
		this.cmd = command;
	}

	/**
	 * @param renderer contained widget
	 * @param submenu submenu
	 */
	public AriaMenuItem(Widget renderer, AriaMenuBar submenu) {
		this();
		setWidget(renderer);
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
	 * @param icon
	 *            icon
	 */
	private void setContent(String text, @CheckForNull ResourcePrototype icon) {
		getElement().removeAllChildren();
		this.textNode = DomGlobal.document.createTextNode(text);
		try {
			elemental2.dom.Element el = Js.uncheckedCast(getElement());
			if (icon != null) {
				img = Js.uncheckedCast(DomGlobal.document.createElement("img"));
				img.setAttribute("src", NoDragImage.safeURI(icon));
				img.setAttribute("draggable", "false");
				img.classList.add("menuImg");
				el.appendChild(img);
			}
			el.appendChild(textNode);
		} catch (ClassCastException ex) {
			// mockito
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

	public String getText() {
		return getElement().getInnerText();
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

	/**
	 * @param text new text content
	 */
	public void setTextContent(String text) {
		if (textNode != null) {
			textNode.textContent = text;
		}
	}

	@Override
	public void setResource(ResourcePrototype icon) {
		img.setAttribute("src", NoDragImage.safeURI(icon));
	}
}
