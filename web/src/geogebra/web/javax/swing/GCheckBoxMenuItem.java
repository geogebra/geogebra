package geogebra.web.javax.swing;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuItem;

public class GCheckBoxMenuItem{

	CheckBox checkBox;
	MenuItem menuItem;
	HorizontalPanel itemPanel;
	
	
//	public GCheckBoxMenuItem(SafeHtml html, final ScheduledCommand cmd) {
//	    super(html, cmd);
//	    checkBox = new CheckBox(html);
//	    checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
//			public void onValueChange(ValueChangeEvent<Boolean> event) {
//	            cmd.execute();
//            }});
//	    setHTML(checkBox.toString());
//    }

	public GCheckBoxMenuItem(String html, final ScheduledCommand cmd) {
//		ScheduledCommand cmd2 = new ScheduledCommand(){
//			public void execute() {
//				cmd.execute();
//				checkBox.setValue(!checkBox.getValue());
//            }	
//		};
	    
	    
		// It's didn't work, becase when I clicked on the label of the checkbox,
		// the command of menuitem didn't run, so I added the html-string for the MenuItem
	    // in an another way (see below)
	    //checkBox = new CheckBox(html);
	    
	    checkBox = new CheckBox();
	    itemPanel = new HorizontalPanel();
	    itemPanel.add(checkBox);
//	    App.debug("html: " + html);
//	    HTML htmlWidget = new HTML(html);
//	    App.debug("htmlWidget:");
//	    App.debug(htmlWidget.toString());
	    itemPanel.add(new HTML(html));
	    menuItem = new MenuItem(itemPanel.toString(), true, cmd);
    }

	
	public void setSelected(boolean sel){
		checkBox.setValue(sel);
		menuItem.setHTML(itemPanel.toString());
	}
	
	public MenuItem getMenuItem(){
		return menuItem;
	}

}
