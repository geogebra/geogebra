package org.geogebra.common.move.events;

public interface GenericEvent<T> {

	void fire(T view);
}
