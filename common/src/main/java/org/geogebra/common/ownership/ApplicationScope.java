package org.geogebra.common.ownership;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;

/**
 * A container for application-scoped objects.
 * <p/>
 * This container serves as a home for objects with application lifetime that  don't have a
 * direct owner (i.e., no "parent"). Try to put as few objects as possible in here.
 * <p/>
 * Objects owned by this container are not necessarily <i>created</i> in here - they may be
 * created in different places (e.g., depending on the client platform), and ownership then
 * transferred here. This is a design decision you need to make if you plan to add a new
 * object in here.
 */
public final class ApplicationScope {

	public static ExamController examController;

	/**
	 * Prevent instantiation.
	 */
	private ApplicationScope() { }
}
