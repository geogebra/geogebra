package org.geogebra.web.html5.gui.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedFocusPanel extends SimplePanel {

	TextAreaElement focusTextarea;

	public AdvancedFocusPanel() {
		// Here it is not the getContainerElement()
		// that shall get the focus, but another element!

		// In theory, getContainerElement() will play the role
		// of parent element of any widget added to this panel;
		// which should be the same as getElement(), the
		// DOM representation of AdvancedFocusPanel when added
		// to other panels... so it makes no sense changing
		// any of these to textarea, but instead, we shall add
		// the textarea as a child element of:
		// getContainerElement() == getElement(),
		// so the textarea will be a sibling of Widget.getElement
		super();

		// now this focusTextarea shall get focus,
		// which will make it possible for us to add "paste"
		// events to the AdvancedFocusPanel, not just "keydown",
		// "keypress", "keyup", etc events.
		focusTextarea = DOM.createTextArea().cast();

		// the only problem with focusTextarea seems to be its style:
		// so it is still visible on the page, unless we hide it!
		focusTextarea.addClassName("AdvancedFocusPanelsTextarea");

		// as the setWidget call happens later, we shall accept
		// that focusTextarea will actually be the first child
		// of the AdvancedFocusPanel... but it's not trivial
		// that it will work together well with SimplePanel...
		// but it turned out that it's Okay (testing)
		DOM.appendChild(getContainerElement(), focusTextarea);

		// moreover, it's not trivial that focus features will
		// work well, what about browser natural click-focus feature?
		// so maybe we will also need to make getElement() focusable,
		// and redirect its focus to the textarea, just like
		// MathQuillGGB does it... but it turned out we don't need this!

		// about methods like FocusHandler, etc... use addDomHandler!
	}

	public AdvancedFocusPanel(Widget w) {
		this();
		// "widget" constructor is just adding a call
		// of setWidget, there is no better idea!
		setWidget(w);
	}

	/**
	 * In theory, we only use GWT's functionality of setFocus,
	 * and don't mind changing focus by tabIndex features
	 * @param focus true focus false blur
	 */
	public void setFocus(boolean focus) {
		if (focus) {
			focusTextarea.focus();
		} else {
			focusTextarea.blur();
		}
	}

	public Element getTextarea() {
		return (Element)focusTextarea;
	}

	/**
	 * In order for the copy/cut events to work naturally, the focusTextarea
	 * should contain a selection which contains the string to be copied. So as
	 * to work properly, this string should be continuously updated as the
	 * spreadsheet view has selected cells
	 * 
	 * TODO the focus should only be called when we want to copy
	 * 
	 * @param str
	 *            string for copying/cutting
	 */
	public void setSelectedContent(String str) {
		if (str == null || str.equals(focusTextarea.getValue())) {
			return;
		}
		focusTextarea.setValue(str);
		focusTextarea.select();
	}
}
