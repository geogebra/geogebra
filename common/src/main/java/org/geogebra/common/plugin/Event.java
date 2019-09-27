package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.Map;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Event to be handled by EventListener
 * 
 * @author Arnaud
 */
public class Event {

	/** event type */
	public final EventType type;

	/** generic argument, e.g. macro name */
	public String argument;
	/** argument formatted as a JSON string */
	public Map<String, Object> jsonArgument;

	/** primary target */
	public GeoElement target;
	/** secondary target */
	public ArrayList<GeoElement> targets;

	private boolean alwaysDispatched;

	/**
	 * @param type
	 *            event type
	 */
	public Event(EventType type) {
		this(type, null);
	}

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
		this(type, target, argument, null);
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
	public Event(EventType type, GeoElement target,
			 String argument, ArrayList<GeoElement> targets) {
		this.type = type;
		this.target = target;
		this.argument = argument;
		this.targets = targets;
	}

	/**
	 * @param jsonArgument
	 *            JSON encoded additional properties
	 * @return this
	 */
	public Event setJsonArgument(Map<String, Object> jsonArgument) {
		this.jsonArgument = jsonArgument;
		return this;
	}

	/**
	 * @param alwaysDispatched
	 *            whether to force dispatching while an update is running
	 * @return this
	 */
	public Event setAlwaysDispatched(boolean alwaysDispatched) {
		this.alwaysDispatched = alwaysDispatched;
		return this;
	}

	/**
	 * @return whether to override blocked scripting
	 */
	boolean isAlwaysDispatched() {
		return this.alwaysDispatched;
	}

	/**
	 * @return event type
	 */
	public EventType getType() {
		return type;
	}

	/**
	 * 
	 * @return primary target
	 */
	public GeoElement getTarget() {
		return target;
	}

	/**
	 * 
	 * @return secondary target
	 */
	public ArrayList<GeoElement> getTargets() {
		return targets;
	}

	/**
	 * 
	 * @return argument
	 */
	public String getArgument() {
		return argument;
	}

	public Map<String, Object> getJsonArgument() {
		return jsonArgument;
	}
}
