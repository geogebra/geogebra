package org.geogebra.common.exam;

/**
 * The states the {@link ExamController} can be in.
 */
public enum ExamState {
	/** No exam is currently active. */
	IDLE,

	/** The exam is about to start (e.g., the app is showing a Start Exam dialog, the browser
	 * is going into fullscreen, etc.). */
	PREPARING,

	/** An exam is active. */
	ACTIVE,

	/** The exam has ended and is in the wrap-up phase (e.g., the app is showing the exam
	 * summary dialog). */
	FINISHED
}
