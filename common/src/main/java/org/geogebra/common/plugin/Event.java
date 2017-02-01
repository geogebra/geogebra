package org.geogebra.common.plugin;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;

public class Event {
	public final EventType type;
	public final GeoElement target;
	public final String argument;
	public final ArrayList<GeoElement> targets;
	private boolean alwaysDispatched;

	/**
	 * @param type
	 *            type
	 * @param target
	 *            target
	 */
	public Event(EventType type, GeoElement target) {
		this(type, target, target == null ? null : target.getLabelSimple());
	}

	/**
	 * @param type
	 *            event type
	 * @param target
	 *            target
	 * @param argument
	 *            extra info
	 */
	public Event(EventType type, GeoElement target, String argument) {
		// this( type, target, argument);
		this.type = type;
		this.target = target;
		this.argument = argument;
		this.targets = null;
	}

	/**
	 * @param type
	 *            event type
	 * @param target
	 *            target
	 * @param argument
	 *            extra info
	 * @param targets
	 *            extra targets
	 */
	public Event(EventType type, GeoElement target, String argument,
			ArrayList<GeoElement> targets) {
		this.type = type;
		this.target = target;
		this.argument = argument;
		this.targets = targets;
	}

	/**
	 * @param type
	 *            event type
	 * @param target
	 *            target
	 * @param alwaysDispatch
	 *            whether to override scripting block
	 */
	public Event(EventType type, GeoElement target,
			boolean alwaysDispatch) {
		this(type, target);
		this.alwaysDispatched = alwaysDispatch;
	}

	/**
	 * @return whether to override blocked scripting
	 */
	public boolean isAlwaysDispatched() {
		return this.alwaysDispatched;
	}

	/**
	 * @return event type
	 */
	public EventType getType() {
		return type;
	}
}
