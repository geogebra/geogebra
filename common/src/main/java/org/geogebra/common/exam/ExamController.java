package org.geogebra.common.exam;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.exam.restriction.ExamRegion;

/**
 * Coordinates exam mode.
 */
public final class ExamController {

	private Kernel kernel;
	private ExamState state;
	private Date startDate, endDate;
	private Set<ExamListener> listeners = new HashSet<ExamListener>();

	public ExamController(Kernel kernel/* TODO more dependencies? */) {
		this.kernel = kernel;
	}

	/**
	 * @return The current exam state.
	 */
	public ExamState getState() {
		return state;
	}

	/**
	 * Starts a new exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the INACTIVE state.
	 */
	public void startExam(ExamRegion region) {
		if (state != ExamState.INACTIVE) {
			throw new IllegalStateException();
		}
		sendActionRequired(ExamAction.CLEAR_CLIPBOARD);
		sendActionRequired(ExamAction.CLEAR_APPS);
		applyRestrictions(region);
		state = ExamState.ACTIVE;
		startDate = new Date();
	}

	/**
	 * Stops the current exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the ACTIVE state.
	 */
	public void stopExam() {
		if (state != ExamState.ACTIVE) {
			throw new IllegalStateException();
		}
		state = ExamState.SUMMARY;
		endDate = new Date();
	}

	/**
	 * Finishes the current exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the SUMMARY state.
	 */
	public void finishExam() {
		if (state != ExamState.SUMMARY) {
			throw new IllegalStateException();
		}
		state = ExamState.INACTIVE;
		startDate = endDate = null;
		sendActionRequired(ExamAction.CLEAR_CLIPBOARD);
		sendActionRequired(ExamAction.CLEAR_APPS);
	}

	/**
	 * Adds an `ExamListener.
	 * @param listener The listener to add.
	 * Trying to add a listener that is already registered will have no effect.
	 */
	public void addListener(ExamListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes an `ExamListener`.
	 * @param listener The listener to remove.
	 * Trying to remove a listener that is not registered will have no effect.
	 */
	public void removeListener(ExamListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners(ExamState newState) {
		for (ExamListener listener : listeners) {
			listener.examStateChanged(newState);
		}
	}

	private void sendActionRequired(ExamAction action) {
		for (ExamListener listener : listeners) {
			listener.examActionRequired(action);
		}
	}

	private void applyRestrictions(ExamRegion region) {
		ExamRestrictions restrictions = ExamRestrictions.forRegion(region);
		if (restrictions == null) {
			return;
		}
	}
}
