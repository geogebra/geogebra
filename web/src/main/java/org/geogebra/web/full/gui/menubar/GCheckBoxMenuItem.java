package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author gabor
 * 
 * Menuitem that uses checkbox
 *
 */
public class GCheckBoxMenuItem extends AriaMenuItem {

	private FlowPanel panel;
	/**
	 * CheckBox to store
	 */
	CheckBox checkbox;
	/**
	 * stored valuechangehandler (can be only one with this implementation)
	 */
	ValueChangeHandler<Boolean> valueChangeHandler;

	/**
	 * @param html
	 *            creates a new GCheckbox menuitem
	 */
	public GCheckBoxMenuItem(SafeHtml html) {
		super(html.asString(), true, (ScheduledCommand) null);
		initGui(html, true);

	}
	
	private void initGui(SafeHtml html, boolean showCheckbox) {
		addStyleName("GCheckBoxMenuItem");
		checkbox = new CheckBox();
		checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
	           if (valueChangeHandler != null) {
	        	   valueChangeHandler.onValueChange(event);
	           }
	           setSelected(event.getValue());
            }
		});
		this.setScheduledCommand(new ScheduledCommand() {

			@Override
			public void execute() {
				if (valueChangeHandler != null) {
					checkbox.setValue(!checkbox.getValue());
					valueChangeHandler.onValueChange(
							new MyValueChangeEvent(checkbox.getValue()));
				}
				setSelected(checkbox.getValue());
			}
		});
	    panel = new FlowPanel();
	    if (showCheckbox) {
	    	panel.add(checkbox);
	    }
	    panel.add(new HTML(html));
	    this.setHTML(panel.getElement().getInnerHTML());
	}

	/**
	 * @param html
	 *            menuItems html
	 * @param showCheckbox
	 *            whether to show checkbox
	 */
	public GCheckBoxMenuItem(SafeHtml html, boolean showCheckbox) {
		super(html.asString(), true, (ScheduledCommand) null);
		initGui(html, showCheckbox);
	}

	/**
	 * @param selected
	 *            whether the checkbox is selected or not
	 */
	public void setSelected(boolean selected) {
		Log.debug("setselected called");
	    checkbox.setValue(selected);
	    this.setHTML(panel.getElement().getInnerHTML());
	    setSelectedClass(checkbox.getValue());
    }
	
	private void setSelectedClass(Boolean selected) {
		if (selected) {
	    	this.addStyleName("selected");
		} else {
			this.removeStyleName("selected");
		}
    }

	/**
	 * @param handler the valuechangehandler to add
	 * @return new handlerregistration object
	 */
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		valueChangeHandler = handler;
		return new HandlerRegistration() {
			
			@Override
			public void removeHandler() {
				valueChangeHandler = null;
			}
		};
	}

	/**
	 * @return whether the checkbox is selected or not
	 */
	public boolean isSelected() {
	    return checkbox.getValue();
    }
	
	private class MyValueChangeEvent extends ValueChangeEvent<Boolean> {

		protected MyValueChangeEvent(Boolean value) {
	        super(value);
	        setSource(GCheckBoxMenuItem.this);
        }
		
	}

}
