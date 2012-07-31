package geogebra.web.gui;

import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;

/*
 * Michael Borcherds
 * 
 * Implements KeyListener
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class GeoGebraKeys implements KeyDownHandler, KeyUpHandler {

	private static StringBuilder altCodes = new StringBuilder();

	private boolean altPressed;
	
	AppW app;

	public GeoGebraKeys(AppW app) {
		this.app = app;
	}
	
	public void onKeyDown(KeyDownEvent e) {  
		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		//AG dont worry about this yet if (Application.MAC_OS && e.isControlDown())
		//AG	e.consume();
		//Application.debug("keyPressed");
	}

	public void onKeyUp(KeyUpEvent e) {
		
		//Application.debug("keyTyped"+e.getKeyChar());
		if (e.isAltKeyDown()) {
			if (!altPressed) {
				altCodes.setLength(0);
				//Application.debug("alt pressed");
			}
			altPressed = true;
		} else {
			if (altCodes.length() > 0) {
				
				// intercept wrong character and replace with correct Alt-code
				char insertStr = (char) Integer.parseInt(altCodes.toString());
				TextBox comp = (TextBox) e.getSource();
				int pos = comp.getCursorPos();
				String oldText = comp.getText();
				StringBuilder sb = new StringBuilder();
				sb.append(oldText.substring(0, pos));
				sb.append(insertStr);
				sb.append(oldText.substring(pos));            
				comp.setText(sb.toString());

				comp.setCursorPos(pos + 1);
				e.stopPropagation();
				
			}
			
			altPressed = false;
			altCodes.setLength(0);
		}

		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltKeyDown() && e.isControlKeyDown())
			return;

		
		

	}   

}
