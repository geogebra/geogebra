package org.geogebra.web.html5.gui.textbox;

import org.geogebra.web.html5.gui.inputfield.HasSymbolPopup;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This class is created so that the bluetooth keyboard works in Safari iOS.
 * 
 * @author Balazs
 */
public class GTextBox extends TextBox implements NativePreviewHandler,
		HasSymbolPopup, MathKeyboardListener {
	// On iOS when using a bluetooth keyboard, the onkeyup event reports
	// the charcode to be 0. To solve this, we save the character code
	// in the onkeydown event, and we use that for the onkeyup

	protected int keyCode;
	protected boolean isControlKeyDown;
	protected boolean isAltKeyDown;
	protected boolean isShiftKeyDown;
	protected boolean isMetaKeyDown;

	public GTextBox() {
		Event.addNativePreviewHandler(this);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		final KeyUpHandler finalHandler = handler;
		return super.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == 0) {
					NativeEvent nativeEvent = Document.get().createKeyUpEvent(
					        isControlKeyDown, isAltKeyDown, isShiftKeyDown,
					        isMetaKeyDown, keyCode);
					event.setNativeEvent(nativeEvent);

				}
				finalHandler.onKeyUp(event);
			}
		});
	}

	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (event.getTypeInt() == Event.ONKEYDOWN) {
			NativeEvent nativeEvent = event.getNativeEvent();
			keyCode = nativeEvent.getKeyCode();
			isAltKeyDown = nativeEvent.getAltKey();
			isShiftKeyDown = nativeEvent.getShiftKey();
			isControlKeyDown = nativeEvent.getCtrlKey();
			isMetaKeyDown = nativeEvent.getMetaKey();
		}
	}

	private HasSymbolPopup showSymbolElement;

	public void setPopupCallback(HasSymbolPopup element) {
		this.showSymbolElement = element;
	}
	@Override
	public void showPopup(boolean show) {
		if (showSymbolElement != null) {
			showSymbolElement.showPopup(show);
		}
	}

	public void ensureEditing() {
		this.setFocus(true);

	}
}
