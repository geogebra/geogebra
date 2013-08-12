package geogebra.web.cas.view;

import geogebra.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuItem;


public class RowHeaderPopupMenuW extends GPopupMenuW{
	
	public RowHeaderPopupMenuW(){
		super();
		addItem(new MenuItem("test", new ScheduledCommand(){

			public void execute() {
//	            App.debug("it's a test");
	            
            }
			
		}));
	}
}
