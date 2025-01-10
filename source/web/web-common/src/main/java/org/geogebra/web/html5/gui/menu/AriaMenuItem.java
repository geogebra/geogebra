package org.geogebra.web.html5.gui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.geogebra.common.util.AttributedString;
import org.geogebra.common.util.Range;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.client.Document;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
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
	private Node textNode;
	private HTMLElement img;

	/**
	 * @param text content
	 * @param icon icon
	 * @param cmd command to run when clicked
	 */
	public AriaMenuItem(String text, ResourcePrototype icon, ScheduledCommand cmd) {
		this();
		setContent(text, icon);
		this.cmd = cmd;
	}

	/**
	 * @param text formatted text
	 * @param icon icon
	 * @param cmd item action
	 */
	public AriaMenuItem(AttributedString text, @Nullable ResourcePrototype icon,
			ScheduledCommand cmd) {
		this();
		Set<Range> attribute = text.getAttribute(AttributedString.Attribute.Subscript);
		if (attribute == null) {
			setContent(text.getRawValue(), icon);
		} else {
			setHTMLContent(addSubscript(text.getRawValue(), attribute), icon);
		}
		this.cmd = cmd;
	}

	private Node addSubscript(String rawValue, Set<Range> attribute) {
		List<Integer> splits = new ArrayList<>();
		splits.add(0);
		for (Range range: attribute) {
			splits.add(range.getStart());
			splits.add(range.getEnd());
		}
		Node span = DomGlobal.document.createElement("span");
		for (int i = 0; i + 2 < splits.size(); i += 2) {
			addPlainText(span, rawValue.substring(splits.get(i), splits.get(i + 1)));
			Element subscript = DomGlobal.document.createElement("sub");
			subscript.textContent = rawValue.substring(splits.get(i + 1), splits.get(i + 2));
			span.appendChild(subscript);
		}
		addPlainText(span, rawValue.substring(splits.get(splits.size() - 1)));
		return span;
	}

	private void addPlainText(Node span, String s) {
		Text plain = DomGlobal.document.createTextNode(s);
		span.appendChild(plain);
	}

	/**
	 * @param text content
	 * @param cmd command to run when clicked
	 * @param icon icon
	 */
	public AriaMenuItem(String text, ScheduledCommand cmd, IconSpec icon) {
		this();
		setContent(text, icon);
		this.cmd = cmd;
	}

	/**
	 * @param text content
	 * @param icon icon
	 * @param submenu submenu to open when clicked
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
	 * @param text content
	 * @param icon icon
	 */
	private void setContent(String text, @CheckForNull ResourcePrototype icon) {
		this.textNode = DomGlobal.document.createTextNode(text == null ? "" : text);
		setHTMLContent(textNode, icon);
	}

	private void setHTMLContent(Node textNode, ResourcePrototype icon) {
		getElement().removeAllChildren();
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
	 * @param text content
	 * @param icon icon
	 */
	private void setContent(String text, IconSpec icon) {
		getElement().removeAllChildren();
		this.textNode = DomGlobal.document.createTextNode(text);
		try {
			elemental2.dom.Element el = Js.uncheckedCast(getElement());
			if (icon != null) {
				img = Js.uncheckedCast(DomGlobal.document.createElement("img"));
				if (icon instanceof ImageIconSpec) {
					img.setAttribute("src", NoDragImage.safeURI(((ImageIconSpec) icon).getImage()));
					img.setAttribute("draggable", "false");
					img.classList.add("menuImg");
					el.appendChild(img);
				} else {
					elemental2.dom.Element iconElem = Js.uncheckedCast(icon.toElement());
					el.insertAdjacentElement("afterbegin", iconElem);
				}

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
	 * @param enabled whether this button is active
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
	 * @param cmd command to run when clicked
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
