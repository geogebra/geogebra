package org.geogebra.common.exam;

public interface ExamListener {
	void examStateChanged(ExamState newState);

	void examActionRequired(ExamAction action);
}
