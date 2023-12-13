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
 * @apiNote Note that this class is not instantiated automatically, each
 * client platform can decide when to set it up, and where to store it.
 *
 * @implNote This class is not designed to be thread-safe.
 */
public final class ApplicationScope {

	private ExamController examController;

	/**
	 * Create and initialize eager objects.
	 */
	public void initialize() {
	}

	/**
	 * Create the exam controller.
	 * @param kernel
	 * @param commandDispatcher
	 */
	public void createExamController(CommandDispatcher commandDispatcher) {
		examController = new ExamController(commandDispatcher);
	}

	public ExamController getExamController() {
		return examController;
	}
}
