package org.geogebra.web.full.gui.dialog.text;

import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.gwtproject.dom.client.Element;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;

public class EditorTextField extends GTextBox implements KeyUpHandler {

	Element target;

	/**
	 * Create new textfield.
	 */
	public EditorTextField() {
		super();
		// TODO: use CSS style
		getStyleElement().setAttribute("spellcheck", "false");
		getStyleElement().setAttribute("oncontextmenu", "return false");

		addKeyUpHandler(this);
	}

	/** TODO: use CSS style */
	public void setFont(GFontW font) {
		int fontSize = font.getFontSize();
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

	@Override
	public void onKeyUp(KeyUpEvent e) {
		updateTarget();
	}

}
