package org.geogebra.web.web.gui.properties;

import org.geogebra.common.main.App;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class PropertiesButton extends MenuItem {

	public PropertiesButton(String text) {
	    super(text, true, new Command() {
			
			public void execute() {
				App.debug("dummy");
			}
		});
    }

}
