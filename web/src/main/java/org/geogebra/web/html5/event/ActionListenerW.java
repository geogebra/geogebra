package org.geogebra.web.html5.event;

import org.geogebra.common.euclidian.event.ActionListener;
import org.geogebra.common.euclidian.event.ActionListenerI;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class ActionListenerW extends ActionListener implements EventListener,
        ChangeHandler {

	public ActionListenerW(ActionListenerI listener) {
		setListenerClass(listener);
	}

	public void onBrowserEvent(Event e) {
		ActionEventW event = ActionEventW.wrapEvent(e);
		// this would make too much calls on e.g.
		// mouseMove, mouseOver, mouseOut events!
		// at least exclude those, and think what's more:
		if (e.getTypeInt() == Event.ONMOUSEMOVE
				|| e.getTypeInt() == Event.ONMOUSEOVER
				|| e.getTypeInt() == Event.ONMOUSEOUT) {
			return;
		}
		wrapActionPerformed(event);
	}

	public void onChange(ChangeEvent e) {
		ChangeEventW event = ChangeEventW.wrapEvent(e);
		wrapActionPerformed(event);

	}

}
