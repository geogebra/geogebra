package org.geogebra.common.exam;

/**
 * Listener interface for changes in the {@link ExamController}.
 */
public interface ExamListener {
	void examStateChanged(ExamState newState);
}

