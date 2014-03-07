package geogebra.touch.gui.elements;

import geogebra.html5.gui.StandardButton;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

public class StandardClickButton extends StandardButton {

	public StandardClickButton(ImageResource icon) {
		super(icon);
	}

	public StandardClickButton(String label) {
		super(label);
	}

	public StandardClickButton(ImageResource icon, String label) {
		super(icon, label);
	}

	@Override
	public void onBrowserEvent(Event event) {
		// ignore every event except for clickEvents
		if (DOM.eventGetType(event) == Event.ONCLICK) {
			fireFastClickEvent();
			event.stopPropagation();
		}
	}
}
