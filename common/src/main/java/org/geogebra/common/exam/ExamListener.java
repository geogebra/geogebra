package org.geogebra.common.exam;

/**
 * Listener interface for changes in the {@link ExamController}.
 */
public interface ExamListener {

	/**
	 * The exam state did change in the {@link ExamController}.
	 *
	 * @param newState The new exam state.
	 */
	void examStateChanged(ExamState newState);

	default void cheatingStarted() {
	}
}

