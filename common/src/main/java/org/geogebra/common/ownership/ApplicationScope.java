package org.geogebra.common.ownership;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.kernel.Kernel;

/**
 * A container for application-scoped objects.
 *
 * @apiNote Note that this class is not instantiated automatically, each
 * client platform can decide when to set it up, and where to store (the
 * reference to) it.
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

	public void createExamController(Kernel kernel) {
		examController = new ExamController(kernel);
	}

	public ExamController getExamController() {
		return examController;
	}
}
