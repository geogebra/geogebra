package geogebra.touch.gui.elements;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RadioButton;

public class StandardRadioButton extends RadioButton {
	private LookAndFeel laf = TouchEntryPoint.getLookAndFeel();

	public StandardRadioButton(String name, String label) {
		super(name, label);
		
		this.getElement().getStyle().setBackgroundImage(this.laf.getIcons().radioButtonInactive().getSafeUri().asString());
		this.getElement().getStyle().setPaddingLeft(25, Unit.PX);
		this.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		
		//this.getElement().setInnerHTML("<input type=\"button\" style=\"width: 0px; height: 0px;\" /><img src=\"" + this.laf.getIcons().radioButtonInactive().getSafeUri().asString() +"\"<label>testlabel</label>");
	}

}
