package geogebra.web.gui.properties;

import geogebra.common.main.App;

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
