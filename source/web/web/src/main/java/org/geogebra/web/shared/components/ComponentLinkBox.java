/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.shared.components;

import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.user.client.ui.TextBox;

/**
 * material design link text field
 *
 * @author Csilla
 *
 */
public class ComponentLinkBox extends TextBox
		implements ClickHandler, BlurHandler {

	/** true if linkBox is focused */
	protected boolean isFocused = true;

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
		addClickHandler(this);
		addBlurHandler(this);
	}

	/**
	 * @return true if text field in focus
	 */
	public boolean isFocused() {
		return isFocused;
	}

	/**
	 * @param linkBoxFocused
	 *            true if set in focus text box
	 */
	public void setFocused(boolean linkBoxFocused) {
		this.isFocused = linkBoxFocused;
	}

	@Override
	public void setReadOnly(boolean isReadOnly) {
		super.setReadOnly(isReadOnly);
	}

	/**
	 * focus textBox and select text
	 */
	public void focus() {
		setFocus(true);
		setSelectionRange(0, 0);
		selectAll();
		setFocused(true);
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (isFocused()) {
			setFocus(true);
			setSelectionRange(0, 0);
		}
		setFocused(false);
	}

	@Override
	public void onClick(ClickEvent event) {
		focus();
	}
}
