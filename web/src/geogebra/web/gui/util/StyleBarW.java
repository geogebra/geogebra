package geogebra.web.gui.util;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author G. Sturr
 * 
 */
public class StyleBarW extends HorizontalPanel {

	/**
	 * Constructor
	 */
	public StyleBarW() {
		setStyleName("StyleBar");
		setSpacing(2);
	}

	
	protected void addSeparator(){
		VerticalSeparator s = new VerticalSeparator(10,25);
		add(s);
	}
	
	
}
