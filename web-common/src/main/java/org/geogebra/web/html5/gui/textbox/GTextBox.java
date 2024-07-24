package org.geogebra.web.html5.gui.textbox;

import org.geogebra.common.util.TextObject;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.Event.NativePreviewEvent;
import org.gwtproject.user.client.Event.NativePreviewHandler;
import org.gwtproject.user.client.ui.TextBox;

/**
 * This class is created so that the bluetooth keyboard works in Safari iOS.
 * 
 * @author Balazs
 */
public class GTextBox extends TextBox
		implements NativePreviewHandler, TextObject {
	// On iOS when using a bluetooth keyboard, the onkeyup event reports
	// the charcode to be 0. To solve this, we save the character code
	// in the onkeydown event, and we use that for the onkeyup

	protected int keyCode;
	protected boolean isControlKeyDown;
	protected boolean isAltKeyDown;
	protected boolean isShiftKeyDown;
	protected boolean isMetaKeyDown;
	private  boolean isFocused = false;

	public GTextBox() {
		this(false, null);
	}

	/**
	 * @param autocomplete
	 *            allow browser autocomplete ?
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
				NativeEvent nativeEvent = Document.get().createKeyUpEvent(
						isControlKeyDown, isAltKeyDown, isShiftKeyDown,
						isMetaKeyDown, keyCode);
				event.setNativeEvent(nativeEvent);

			}
			finalHandler.onKeyUp(event);
		});
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (event.getTypeInt() == Event.ONKEYDOWN) {
			NativeEvent nativeEvent = event.getNativeEvent();
			keyCode = nativeEvent.getKeyCode();
			isAltKeyDown = nativeEvent.getAltKey();
			isShiftKeyDown = nativeEvent.getShiftKey();
			isControlKeyDown = nativeEvent.getCtrlKey();
			isMetaKeyDown = nativeEvent.getMetaKey();
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

	public boolean hasFocus() {
		return isFocused;
	}

	@Override
	public void setEditable(boolean editable) {
		this.setReadOnly(!editable);
	}

}
