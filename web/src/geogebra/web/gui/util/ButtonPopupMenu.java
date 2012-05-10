package geogebra.web.gui.util;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ButtonPopupMenu extends PopupPanel {
	
	VerticalPanel container = null;
	
	public ButtonPopupMenu() {
		super();
		container = new VerticalPanel();
		add(container);
	}
	
	VerticalPanel getPanel() {
		return container;
	}

}
