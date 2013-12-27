package geogebra.web.gui.util;

import geogebra.common.main.App;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 * ButtonGroupPanel that acts as a button group for togglebuttons
 *
 */
public class ButtonGroupPanel extends FlowPanel implements MouseUpHandler {
		
	
	
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
		//button.addMouseUpHandler(this);
		buttons.add(button);
		super.add(button);
	}

	public void onValueChange(ValueChangeEvent<Boolean> event) {
		MyToggleButton2 source = (MyToggleButton2) event.getSource();
	    for (MyToggleButton2 button : buttons) {
	    		if (event.getValue())
	    			
	    App.debug(((MyToggleButton2) event.getSource()).getValue() + "");
	}
	
	
}

	@Override
    public void onMouseUp(MouseUpEvent event) {
		MyToggleButton2 source = (MyToggleButton2) event.getSource();
		if (source.getValue()) {
			for (MyToggleButton2 button : buttons) {
				if (button != source) {
					button.setValue(false);
				}
			}
		} else {
			source.setValue(true);
			for (MyToggleButton2 button : buttons) {
				if (button != source) {
					button.setValue(false);
				}
			}
		}
		}
	}
