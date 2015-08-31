package org.geogebra.web.web.gui.util;

import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.web.html5.gui.GPopupPanel;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ButtonPopupMenu extends GPopupPanel {
	
	FocusPanel container = null;
	VerticalPanel panel = null;
	
	public ButtonPopupMenu(Panel root) {
		super(root);
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
