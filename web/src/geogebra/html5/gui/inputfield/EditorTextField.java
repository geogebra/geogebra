package geogebra.html5.gui.inputfield;

import geogebra.html5.awt.GFontW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;


public class EditorTextField extends TextBox implements KeyUpHandler {

	Element target;

	public EditorTextField() {
		super();
		
		//TODO: use CSS style		
		getStyleElement().setAttribute("spellcheck", "false");
		getStyleElement().setAttribute("oncontextmenu", "return false");

		addKeyUpHandler(this);
	}

	//TODO: use CSS style
	public void setFont(GFontW font){
		String fontSize = font.getFontSize();
		String fontFamily = font.getFontFamily();

		getStyleElement().setAttribute(
		        "style",
		        "font-family:" + fontFamily + "; font-size:" + fontSize
		                + "pt");
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
