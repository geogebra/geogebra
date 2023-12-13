package org.geogebra.common.exam;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ExamCommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.main.exam.TempStorage;
import org.geogebra.common.main.exam.restriction.ExamRegion;
import org.geogebra.common.ownership.NonOwning;

/**
 * A controller that coordinates exam mode.
 * <p/>
 * <h3>Side Effects</h3>
 * emanating from this controller:
 * <ul>
 * <li> The exam controller will potentially (depending on the exam region) call
 * 	 <ul>
 * 	     <li>{@link CommandDispatcher#addCommandFilter(CommandFilter)}
 *   	 <li>{@link CommandDispatcher#addCommandArgumentFilter(CommandArgumentFilter)}
 *   </ul>
 *   on exam start (in this order), and the corresponding
 *   <ul>
 *       <li>{@link CommandDispatcher#removeCommandFilter(CommandFilter)}
 *   	 <li>{@link CommandDispatcher#removeCommandArgumentFilter(CommandArgumentFilter)}
 *   </ul>
 *   on exam end (in this order).
 * </ul>
 */
public final class ExamController {

	private ExamState state;
	private Date startDate, endDate;
	private Set<ExamListener> listeners = new HashSet<ExamListener>();

	private final TempStorage tempStorage = new TempStorage();
	@NonOwning
	private CommandDispatcher commandDispatcher;
	private CommandFilter examCommandFilter;
	private final CommandArgumentFilter examCommandArgumentFilter = new ExamCommandArgumentFilter();

	// filter for apps with no CAS
//	private final CommandFilter noCASFilter = CommandFilterFactory.createNoCasCommandFilter();

	public ExamController(@NonOwning CommandDispatcher commandDispatcher/* TODO more dependencies? */) {
		this.commandDispatcher = commandDispatcher;
	}

	/**
	 * @return The current exam state.
	 * <p/>
	 * Also observable through {@link ExamListener#examStateChanged(ExamState)}.
	 */
	public ExamState getState() {
		return state;
	}

	private void setExamState(ExamState newState) {
		if (newState == state) {
			return;
		}
		state = newState;
		notifyListeners(newState);
	}

	/**
	 * Starts a new exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the INACTIVE state.
	 */
	public void startExam(ExamRegion region, boolean enableCAS) {
		if (state != ExamState.INACTIVE) {
			throw new IllegalStateException();
		}
		sendActionRequired(ExamAction.CLEAR_CLIPBOARD);
		sendActionRequired(ExamAction.CLEAR_APPS);
		tempStorage.clearTempMaterials();
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
		unapplyRestrictions();
		tempStorage.clearTempMaterials();
		sendActionRequired(ExamAction.CLEAR_CLIPBOARD);
		sendActionRequired(ExamAction.CLEAR_APPS);
//		setShowSyntax(true);

		// TODO
//		https://geogebra-jira.atlassian.net/browse/GGB-1306
//		after ending the exam, CAS and 3D need to be enabled again (unless specified by data-param)
//		when there is no data-param for CAS, start Exam should not jump to CAS
	}

	/**
	 * Adds an `ExamListener.
	 * @param listener The listener to add.
	 * Trying to add a listener that is already registered will have no effect.
	 */
	public void addListener(@NonOwning ExamListener listener) {
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
		if (restrictions != null) {
			examCommandFilter = restrictions.getCommandFilter();
			if (examCommandFilter != null) {
				commandDispatcher.addCommandFilter(examCommandFilter);
			}
		}
		commandDispatcher.addCommandArgumentFilter(examCommandArgumentFilter);
	}

	private void unapplyRestrictions() {
		if (examCommandFilter != null) {
			commandDispatcher.removeCommandFilter(examCommandFilter);
		}
		examCommandFilter = null;
		commandDispatcher.removeCommandArgumentFilter(examCommandArgumentFilter);
	}

//	private void setShowSyntax(boolean showSyntax) {
//		CommandErrorMessageBuilder builder = localization.getCommandErrorMessageBuilder();
//		builder.setShowingSyntax(showSyntax);
//	}
}
