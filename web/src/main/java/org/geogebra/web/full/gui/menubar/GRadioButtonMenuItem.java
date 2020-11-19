package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Menu type for radiobutton things.
 *
 * @author gabor
 */
public class GRadioButtonMenuItem extends AriaMenuItem {
	/** wrapped radio button */
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
		super(gradio.getSafeHtmlString(), true, (ScheduledCommand) null);
	   base = gradio;
	}

	/**
	 * @param sel boolean
	 * 
	 * sets the radiobutton to selected
	 */
	public void setSelected(boolean sel) {
		base.radio.setValue(sel);
		setHTML(base.getSafeHtmlString());
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
		return base.getActionCommand();
	}
	
}
