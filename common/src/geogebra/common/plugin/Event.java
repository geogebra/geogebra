package geogebra.common.plugin;

import geogebra.common.kernel.geos.GeoElement;

public class Event {
	public final EventType type;
	public final GeoElement target;
	public final String argument;
	
	public Event(EventType type, GeoElement target) {
		this(type, target, null);
	}
	
	public Event(EventType type, GeoElement target, String argument) {
		this.type = type;
		this.target = target;
		this.argument = argument;
	}
}
