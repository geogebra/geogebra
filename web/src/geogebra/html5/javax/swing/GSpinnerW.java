package geogebra.html5.javax.swing;

import com.google.gwt.user.client.ui.TextBox;

public class GSpinnerW extends TextBox{

	public GSpinnerW(){
		super();
		this.setValue("2");
		this.getElement().setAttribute("type", "number");
		this.getElement().setAttribute("min", "0.25");
		this.getElement().setAttribute("max", "10");
		this.getElement().setAttribute("step", "0.25");
	}
	
}
