package geogebra.web.gui.menubar;

import geogebra.web.main.Application;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class RadioButtonMenuBar extends MenuBar{
	
	public RadioButtonMenuBar(){
		super(true);
	}
	
	private Application app;

	public RadioButtonMenuBar(int selected, MenuItem... items){
		super(true);
		for(int i=0; i<items.length; i++) addItem(items[i]);
		setSelected(selected);
	}

	public void setSelected(int itemIndex) {	    
	    for(int i=0; i<getItems().size(); i++){
	    	if (i==itemIndex)
	    		getItems().get(i).getElement().setAttribute("radioMenuItemSelected", "true");
	    	else
	    		getItems().get(i).getElement().setAttribute("radioMenuItemSelected", "false");
	    }	    
	}
}
