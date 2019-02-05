package org.geogebra.web.shared;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * material design link text field
 * 
 * @author Csilla
 *
 */
public class ComponentLinkBox extends TextBox {

	/** true if linkBox is focused */
	protected boolean linkBoxFocused = true;

	/**
	 * @param isReadOnly
	 *            true if text field not editable
	 * @param urlString
	 *            content string
	 * @param style
	 *            style name
	 */
	public ComponentLinkBox(boolean isReadOnly, String urlString,
			String style) {
		setReadOnly(isReadOnly);
		setText(urlString);
		setStyleName(style);
		addLinkBoxHandlers();
	}

	/**
	 * @return true if text field in focus
	 */
	public boolean isLinkBoxFocused() {
		return linkBoxFocused;
	}

	/**
	 * @param linkBoxFocused
	 *            true if set in focus text box
	 */
	public void setLinkBoxFocused(boolean linkBoxFocused) {
		this.linkBoxFocused = linkBoxFocused;
	}

	@Override
	public void setReadOnly(boolean isReadOnly) {
		super.setReadOnly(isReadOnly);
	}

	private void addLinkBoxHandlers() {
		this.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				focusLinkBox();
			}
		});
		this.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (getLinkBox().isLinkBoxFocused()) {
					getLinkBox().setFocus(true);
					getLinkBox().setSelectionRange(0, 0);
				}
				getLinkBox().setLinkBoxFocused(false);
			}
		});
	}

	/**
	 * @return link text box
	 */
	public ComponentLinkBox getLinkBox() {
		return this;
	}

	/**
	 * focus textBox and select text
	 */
	protected void focusLinkBox() {
		setFocus(true);
		setSelectionRange(0, 0);
		selectAll();
		setLinkBoxFocused(true);
	}
}
