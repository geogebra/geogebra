package geogebra.web.gui.util;

import geogebra.common.main.App;

import java.util.ArrayList;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 * ButtonGroupPanel that acts as a button group for togglebuttons
 *
 */
public class ButtonGroupPanel extends FlowPanel implements ValueChangeHandler<Boolean> {
		
	
	
	private ArrayList<MyToggleButton2> buttons = null;
	
	/**
	 * creates new buttonGroupPanel
	 */
	public ButtonGroupPanel() {
		super();
		buttons = new ArrayList<MyToggleButton2>();
	}
	
	/**
	 * @param button MytoggleButton2
	 */
	public void add(MyToggleButton2 button) {
		button.addValueChangeHandler(this);
		buttons.add(button);
		super.add(button);
	}

	public void onValueChange(ValueChangeEvent<Boolean> event) {
	    for (MyToggleButton2 button : buttons) {
	    		App.debug(button.getValue() + "");
	    }
	    App.debug(((MyToggleButton2) event.getSource()).getValue() + "");
	}
	
	
}
