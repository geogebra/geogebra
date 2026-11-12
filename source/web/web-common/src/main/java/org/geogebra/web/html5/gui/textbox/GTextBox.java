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

package org.geogebra.web.html5.gui.textbox;

import org.geogebra.common.util.TextObject;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.dom.client.BrowserEvents;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.Event.NativePreviewEvent;
import org.gwtproject.user.client.Event.NativePreviewHandler;
import org.gwtproject.user.client.ui.TextBox;

import elemental2.dom.KeyboardEvent;
import elemental2.dom.KeyboardEventInit;
import jsinterop.base.Js;

/**
 * This class is created so that the bluetooth keyboard works in Safari iOS.
 *
 * @author Balazs
 */
public class GTextBox extends TextBox implements NativePreviewHandler, TextObject {
	// On iOS when using a bluetooth keyboard, the onkeyup event reports
	// the charcode to be 0. To solve this, we save the character code
	// in the onkeydown event, and we use that for the onkeyup

	protected int keyCode;
	protected boolean isControlKeyDown;
	protected boolean isAltKeyDown;
	protected boolean isShiftKeyDown;
	protected boolean isMetaKeyDown;
	private boolean isFocused = false;

	public GTextBox() {
		this(false, null);
	}

	/**
	 * @param autocomplete allow browser autocomplete ?
	 */
	public GTextBox(boolean autocomplete, GlobalHandlerRegistry globalHandlers) {
		HandlerRegistration handler = Event.addNativePreviewHandler(this);
		if (globalHandlers != null) {
			globalHandlers.add(handler);
		}

		if (!autocomplete) {
			// suggestion from here to disable autocomplete
			// https://code.google.com/p/google-web-toolkit/issues/detail?id=6065
			//
			// #3878
			getElement().setAttribute("autocomplete", "off");
			getElement().setAttribute("autocapitalize", "off");
		}
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		final KeyUpHandler finalHandler = handler;
		return super.addKeyUpHandler(event -> {
			if (event.getNativeKeyCode() == 0) {
				KeyboardEventInit init = KeyboardEventInit.create();
				init.setCtrlKey(isControlKeyDown);
				init.setAltKey(isAltKeyDown);
				init.setShiftKey(isShiftKeyDown);
				init.setMetaKey(isMetaKeyDown);
				init.setBubbles(true);
				Js.asPropertyMap(init).set("keyCode", keyCode);
				event.setNativeEvent(new KeyboardEvent(BrowserEvents.KEYUP, init));
			}
			finalHandler.onKeyUp(event);
		});
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (BrowserEvents.KEYDOWN.equals(event.getNativeEvent().type)) {
			KeyboardEvent nativeEvent = (KeyboardEvent) event.getNativeEvent();
			keyCode = DOM.getKeyCode(nativeEvent);
			isAltKeyDown = nativeEvent.altKey;
			isShiftKeyDown = nativeEvent.shiftKey;
			isControlKeyDown = nativeEvent.ctrlKey;
			isMetaKeyDown = nativeEvent.metaKey;
			if (GlobalKeyDispatcherW.isLeftAltDown()) {
				nativeEvent.preventDefault();
			}
		}
	}

	@Override
	public void setFocus(boolean b) {
		super.setFocus(b);
		isFocused = b;
	}

	/**
	 * @return whether this is focused (baseson a flag)
	 */
	public boolean hasFocus() {
		return isFocused;
	}

	@Override
	public void setEditable(boolean editable) {
		this.setReadOnly(!editable);
	}

	@Override
	public void updateLabel(String labelTextKey) {
		// not needed
	}
}
