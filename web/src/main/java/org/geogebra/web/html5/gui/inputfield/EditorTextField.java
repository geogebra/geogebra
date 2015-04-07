package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.gui.textbox.GTextBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class EditorTextField extends GTextBox implements KeyUpHandler {

	Element target;

	public EditorTextField() {
		super();

		// TODO: use CSS style
		getStyleElement().setAttribute("spellcheck", "false");
		getStyleElement().setAttribute("oncontextmenu", "return false");

		addKeyUpHandler(this);
	}

	// TODO: use CSS style
	public void setFont(GFontW font) {
		String fontSize = font.getFontSize();
		String fontFamily = font.getFontFamily();

		getStyleElement().setAttribute("style",
		        "font-family:" + fontFamily + "; font-size:" + fontSize + "pt");
	}

	protected void updateTarget() {
		if (target != null) {
			target.setPropertyString("value", getText());
		}
	}

	public void setTarget(Element target) {
		this.target = target;
	}

	public void onKeyUp(KeyUpEvent e) {
		updateTarget();
	}

}
