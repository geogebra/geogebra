package org.geogebra.common.plugin;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;

public class Event {
	public final EventType type;
	public final GeoElement target;
	public final String argument;
	public final ArrayList<GeoElement> targets;
	private boolean alwaysDispatched;

	public Event(EventType type, GeoElement target) {
		this(type, target, null);
	}

	public Event(EventType type, GeoElement target, String argument) {
		// this( type, target, argument);
		this.type = type;
		this.target = target;
		this.argument = argument;
		this.targets = null;
	}

	public Event(EventType type, GeoElement target, String argument,
			ArrayList<GeoElement> targets) {
		this.type = type;
		this.target = target;
		this.argument = argument;
		this.targets = targets;
	}

	public Event(EventType type, GeoElement geoElement, boolean alwaysDispatch) {
		this(type, geoElement);
		this.alwaysDispatched = alwaysDispatch;
	}

	public boolean isAlwaysDispatched() {
		return this.alwaysDispatched;
	}
}
