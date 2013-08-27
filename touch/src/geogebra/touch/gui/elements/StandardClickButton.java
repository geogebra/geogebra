package geogebra.touch.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

public class StandardClickButton extends StandardButton {

	public StandardClickButton(SVGResource icon) {
		super(icon);
	}

	public StandardClickButton(String label) {
		super(label);
	}

	public StandardClickButton(SVGResource icon, String label) {
		super(icon, label);
	}

	@Override
	public void onBrowserEvent(Event event) {
		// ignore every event except for clickEvents
		if (DOM.eventGetType(event) == Event.ONCLICK) {
			fireFastClickEvent();
		}
	}
}
