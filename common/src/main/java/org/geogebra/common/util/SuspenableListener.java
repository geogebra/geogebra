package org.geogebra.common.util;

/**
 * A listener that can be temporarily suspended.
 */
public interface SuspenableListener {

	/**
	 * Suspend listening to notifications.
	 */
	void suspendListening();

	/**
	 * Resume listening to notifications.
	 */
	void resumeListening();
}
