package geogebra.web.gui.menubar;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * @author gabor
 * 
 * Menu type for radiobutton things.
 *
 */
public class GRadioButtonMenuItem {
	
	RadioButton radio;
	MenuItem menuItem;
	FlowPanel itemPanel;
	private String actionCommand;
	
	/**
	 * @param html HTML to set
	 * @param cmd Command to execute
	 * @param RadioGroupName Groupname for the radiobuttons
	 */
	public GRadioButtonMenuItem(String html, String cmd, String groupName) {
		this.radio = new RadioButton(groupName);
		this.itemPanel = new FlowPanel();
		itemPanel.add(radio);
		itemPanel.add(new HTML(html));
		this.actionCommand = cmd;
		menuItem = new MenuItem(itemPanel.toString(), true, new ScheduledCommand() {
			
			public void execute() {
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), radio);
				//Maybe createClickevent?????
			}
		});	
	}
	
	public void setSelected(boolean sel){
		radio.setValue(sel);
		menuItem.setHTML(itemPanel.toString());
	}
	
	public MenuItem getMenuItem(){
		return menuItem;
	}

	/**
	 * @param al ValueChange listener
	 * 
	 * Adds a valuechange listener to the radiobutton
	 */
	public void addValueChangeHandler(ValueChangeHandler<Boolean> al) {
	    radio.addValueChangeHandler(al);
    }
	
}
