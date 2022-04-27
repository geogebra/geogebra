package org.geogebra.test;

import java.util.ArrayList;

import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;

public class EventAcumulator implements EventListener {

	private ArrayList<String> evts = new ArrayList<>();

	@Override
	public void sendEvent(Event evt) {
		evts.add(evt.type + " " + (evt.argument == null ? evt.targets : evt.argument));
	}

	@Override
	public void reset() {
		// not needed
	}

	public ArrayList<String> getEvents() {
		return evts;
	}
}
