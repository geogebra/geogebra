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
import org.geogebra.common.main.exam.event.CheatingEvents;
import org.geogebra.common.main.exam.ExamRegion;
import org.geogebra.common.ownership.NonOwning;

import com.google.j2objc.annotations.Weak;

/**
 * A controller for coordinating exam mode.
 * <p/>
 * <h3>Responsibilities</h3>
 * Here's the list of responsibilites of this controller:
 * <ul>
 *     <li><b>Exam state</b>: Starting, stopping, and finishing up the exam, and making the
 *     current exam state available (both as a getter, and as a change notification to registered
 *     listeners).
 *     <li><b>Start/end date</b>: Setting the exam start and end date, and providing date and
 *     time formatting for these.
 *     <li><b>Restrictions</b>: Applying restrictions to the {@link CommandDispatcher} during the
 *     exam.
 *     <li><b>Events</b>: Collect relevant events (e.g., cheating attempts).
 * </ul>
 * <h3>NOT Responsibilities</h3>
 * Conversely, here's what's not in the responsibility of this controller:
 * <ul>
 *     <li><b>Clipboard</b>: The delegate is asked to clear the clipboard at the appropriate
 *     points. Why? Because I didn't want to introduce a clipboard abstraction (type) just for this
 *     single action.
 *     <li><b>Apps</b>: The delegate is asked to clear out the (other) apps at the appropriate
 *     points. Why? Because the App instances are managed (and owned) differently on the different
 *     platforms.
 * </ul>
 * <h3>Side Effects</h3>
 * The ExamController tries to be as side effect-free as possible, so that it's easier to
 * understand and maintain. For this reason, certain behavior is not implemented in the
 * controller itself, but signalled to observers via actions emitted by the controller.
 * <p/>
 * Any side effects emanating from the ExamController are documented here:
 * <ul>
 * <li> The ExamController will potentially (depending on the exam region) call
 * 	 <ul>
 * 	     <li>{@link CommandDispatcher#addCommandFilter(CommandFilter) addCommandFilter()}
 *   	 <li>{@link CommandDispatcher#addCommandArgumentFilter(CommandArgumentFilter) addCommandArgumentFilter()}
 *   </ul>
 *   on exam start (in this order), and the corresponding
 *   <ul>
 *       <li>{@link CommandDispatcher#removeCommandFilter(CommandFilter) removeCommandFilter()}
 *   	 <li>{@link CommandDispatcher#removeCommandArgumentFilter(CommandArgumentFilter) removeCommandArgumentFilter()}
 *   </ul>
 *   on exam end (in this order).
 * </ul>
 *
 *  @implNote This class is not designed to be thread-safe.
 */
public final class ExamController {

	@Weak
	@NonOwning
	private ExamControllerDelegate delegate;

	@NonOwning
	private CommandDispatcher commandDispatcher;
	@NonOwning
	private ExamRegion examRegion;

	private ExamConfiguration configuration;
	private ExamState state = ExamState.INACTIVE;
	private Date startDate, endDate;
	private final Set<ExamListener> listeners = new HashSet<ExamListener>();
	private final TempStorage tempStorage = new TempStorage();
	private CheatingEvents cheatingEvents = new CheatingEvents();
	private CommandFilter examCommandFilter;
	private final CommandArgumentFilter examCommandArgumentFilter = new ExamCommandArgumentFilter();

	// filter for apps with no CAS
//	private final CommandFilter noCASFilter = CommandFilterFactory.createNoCasCommandFilter();

	public ExamController() {
	}

	/**
	 * Sets the delegate.
	 *
	 * @apiNote It is assumed that the delegate is set before attempting to start an exam.
	 *
	 * @param delegate The delegate.
	 */
	public void setDelegate(@NonOwning ExamControllerDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Sets the command dispatcher.
	 * @param commandDispatcher The command dispatcher.
	 */
	public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
		this.commandDispatcher = commandDispatcher;
	}
	/**
	 * @return The current exam state.
	 * <p/>
	 * Also observable through {@link ExamListener#examStateChanged(ExamState) examStateChanged()}.
	 */
	public ExamState getState() {
		return state;
	}

	/**
	 * Changes state and notifies listeners about the state change.
	 *
	 * Note: If newState is equal to the current state, nothing happens.
	 *
	 * @param newState The new state to change to.
	 */
	private void setState(ExamState newState) {
		if (newState == state) {
			return;
		}
		state = newState;
		notifyListeners(newState);
	}

	/**
	 * @return The exam start date, if an exam is currently active, or null otherwise.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return The exam end date, if the exam has been stopped, or null otherwise.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Get ready for a new exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the {@link ExamState#INACTIVE INACTIVE}
	 * state.
	 */
	public void prepareExam() {
		if (state != ExamState.INACTIVE) {
			throw new IllegalStateException();
		}
		setState(ExamState.PREPARING);
	}

	public void startExam(ExamRegion region) {
		startExam(region, null);
	}

	/**
	 * Starts the exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the
	 * {@link ExamState#PREPARING PREPARING} state.
	 */
	public void startExam(ExamRegion region, ExamConfiguration configuration) {
		if (state != ExamState.PREPARING) {
			throw new IllegalStateException();
		}
		this.configuration = configuration;
		applyConfiguration(configuration);
		applyRestrictions(region);
		requestAction(ExamAction.CLEAR_CLIPBOARD);
		requestAction(ExamAction.CLEAR_APPS);
		requestAction(ExamAction.HIDE_SYNTAX_IN_COMMAND_ERROR_MESSAGES);
		tempStorage.clearTempMaterials();
		cheatingEvents = new CheatingEvents();

		startDate = new Date();
		setState(ExamState.ACTIVE);
	}

	/**
	 * Stops the exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the {@link ExamState#ACTIVE ACTIVE}
	 * 	state.
	 */
	public void stopExam() {
		if (state != ExamState.ACTIVE) {
			throw new IllegalStateException();
		}
		endDate = new Date();
		setState(ExamState.WRAPPING_UP);
	}

	/**
	 * Finishes the current exam.
	 *
	 * @throws IllegalStateException if the exam controller is not in the
	 * {@link ExamState#WRAPPING_UP WRAPPING_UP} state.
	 */
	public void finishExam() {
		if (state != ExamState.WRAPPING_UP) {
			throw new IllegalStateException();
		}
		unapplyRestrictions();
		tempStorage.clearTempMaterials();
		requestAction(ExamAction.CLEAR_CLIPBOARD);
		requestAction(ExamAction.CLEAR_APPS);
		requestAction(ExamAction.SHOW_SYNTAX_IN_COMMAND_ERROR_MESSAGES);
		startDate = endDate = null;
		setState(ExamState.INACTIVE);
	}

	// TODO remove - unclear semantics
	public boolean isExamActive() {
		return state == ExamState.ACTIVE;
	}

	// TODO remove - unclear semantics
	public boolean isExamInPreparationOrActiveOrWrappingUp() {
		return state != ExamState.INACTIVE;
	}

	public boolean isIdle() {
		return state == ExamState.INACTIVE;
	}

	/**
	 * Adds an {@link ExamListener}.
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

	private void requestAction(ExamAction action) {
		if (delegate != null) {
			delegate.requestAction(action);
		}
	}

	private void applyConfiguration(ExamConfiguration configuration) {
		// TODO apply configuration / disable subapps (CAS)
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

	// TODO try to remove
	public TempStorage getTempStorage() {
		return tempStorage;
	}
}
