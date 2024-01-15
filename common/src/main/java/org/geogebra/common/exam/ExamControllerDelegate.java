package org.geogebra.common.exam;

/**
 * The idea of this delegate is to "externalize" any functionality that does not fit into
 * the {@link ExamController} itself (either because it's platform-specific behaviour,
 * or is functionality outside the responsibility of the ExamController).
 */
public interface ExamControllerDelegate {

	void requestAction(ExamAction action);
}
