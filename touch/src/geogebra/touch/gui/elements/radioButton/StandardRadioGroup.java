package geogebra.touch.gui.elements.radioButton;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlowPanel;

public class StandardRadioGroup extends FlowPanel {
	private ArrayList<StandardRadioButton> radioButtons;

	private ArrayList<RadioChangeHandler> radioChangeHandlerList;

	public StandardRadioGroup() {
		this.radioButtons = new ArrayList<StandardRadioButton>();
		this.radioChangeHandlerList = new ArrayList<RadioChangeHandler>();
	}

	void addRadioButton(StandardRadioButton button) {
		this.radioButtons.add(button);
	}

	void deselectAll() {
		for (StandardRadioButton rB : this.radioButtons) {
			rB.setActive(false);
		}
	}

	public void addRadioChangeHandler(RadioChangeHandler handler) {
		this.radioChangeHandlerList.add(handler);
	}

	void fireRadioChanged(StandardRadioButton standardRadioButton) {
		int index = this.radioButtons.indexOf(standardRadioButton);
		if (index == -1) {
			return;
		}
		RadioChangeEvent event = new RadioChangeEvent(index);
		for (RadioChangeHandler rch : this.radioChangeHandlerList) {
			rch.onRadioChange(event);
		}
	}

}
