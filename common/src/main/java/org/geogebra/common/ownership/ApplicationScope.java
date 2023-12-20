package org.geogebra.common.ownership;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;

/**
 * A container for application-scoped objects.
 * <p/>
 * Try to put as few objects as possible in here. This container serves
 * as a home for object with application lifetime that don't have a
 * direct owner.
 * <p/>
 */
public final class ApplicationScope {

	public static ExamController examController;

	/**
	 * Prevent instantiation.
	 */
	private ApplicationScope() { }

//	/**
//	 * Accessor for Objective-C.
//	 */
//	public static ExamController getExamController() {
//		return examController;
//	}
//
//	public static void setExamController(ExamController examController) {
//		ApplicationScope.examController = examController;
//	}
}
