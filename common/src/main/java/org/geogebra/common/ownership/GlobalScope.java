package org.geogebra.common.ownership;

import org.geogebra.common.exam.ExamController;

/**
 * A container for objects with global lifetime, i.e., objects that may live from
 * app launch until app termination.
 *
 * Note: By "app", we here mean the iOS/Android/Web app, not the
 * `org.geogebra.common.main.App` instance.
 * <p/>
 * This container serves as a home for objects with global lifetime that don't have a
 * direct owner (i.e., no "parent"). Try to put as few objects as possible in here.
 * <p/>
 * Objects owned by this container are not necessarily <i>created</i> in here - they may be
 * created in different places (e.g., depending on the client platform), and ownership then
 * transferred here. This is a design decision you need to make if you plan to add a new
 * object in here.
 */
public final class GlobalScope {

	public static ExamController examController;

	/**
	 * Prevent instantiation.
	 */
	private GlobalScope() { }
}
