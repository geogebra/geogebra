package org.geogebra.web.web.gui.menubar;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 * Menu type for radiobutton things.
 *
 */
public class GRadioButtonMenuItem extends MenuItem {
	
	GradioButtonBase base;
	
	/**
	 * @param html HTML to set
	 * @param cmd Command to execute
	 * @param groupName Groupname for the radiobuttons
	 */
	public GRadioButtonMenuItem(String html, String cmd, String groupName) {
		this(new GradioButtonBase(html, cmd, groupName));
	}
	
	private GRadioButtonMenuItem(GradioButtonBase gradio) {
	   super(gradio.getSafeHtml());
	   base = gradio;
	}

	/**
	 * @param sel boolean
	 * 
	 * sets the radiobutton to selected
	 */
	public void setSelected(boolean sel){
		base.radio.setValue(sel);
		setHTML(base.getSafeHtml());
	}

	/**
	 * @param al ValueChange listener
	 * 
	 * Adds a valuechange listener to the radiobutton
	 */
	public void addValueChangeHandler(ValueChangeHandler<Boolean> al) {
	    base.radio.addValueChangeHandler(al);
    }
	
	/**
	 * @return the action command
	 */
	public String getActionCommand() {
		return base.radio.getElement().getAttribute("data-command");
	}
	
}
