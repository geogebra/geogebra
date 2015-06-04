package org.geogebra.web.web.gui.util;

import org.geogebra.common.main.GWTKeycodes;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ButtonPopupMenu extends PopupPanel {
	
	FocusPanel container = null;
	VerticalPanel panel = null;
	
	public ButtonPopupMenu() {
		super();
		container = new FocusPanel();
		panel = new VerticalPanel();
		container.add(panel);
		container.addStyleName("ButtonPopupMenu");
		container.addKeyUpHandler(new KeyUpHandler() {
			
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
					hide();
				}
			}
		});
		add(container);
	}
	
	public VerticalPanel getPanel() {
		return panel;
	}

	public FocusPanel getFocusPanel() {
	   return container;
    }

}
