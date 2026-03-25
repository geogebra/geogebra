/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.exam;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.TempStorage;
import org.geogebra.common.main.exam.event.CheatingEvents;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.ownership.NonOwning;
import org.geogebra.common.restrictions.Restrictions;
import org.geogebra.common.restrictions.Restrictions.ContextDependencies;
import org.geogebra.common.restrictions.RestrictionsController;
import org.geogebra.common.util.TimeFormatAdapter;

import com.google.j2objc.annotations.Property;
import com.google.j2objc.annotations.Weak;

/**
 * A controller for coordinating the core aspects of exam mode.
 *
 * <h2>Responsibilities</h2>
 * Here's the list of responsibilities of this controller:
 * <ul>
 *     <li><b>Exam state</b>: Starting, stopping, and finishing up the exam, and making the
 *     current exam state available (both as a getter, and as a change notification to registered
 *     listeners).
 *     <li><b>Start/end date</b>: Setting the exam start and end date, and providing date and
 *     time formatting for these.
 *     <li><b>Restrictions</b>: Applying restrictions on certain components (e.g.,  the
 *     {@link CommandDispatcher}) at the start of an exam, and reverting those restrictions
 *     when the exam ends. The bulk of the restrictions is now handled by
 *     {@link RestrictionsController}, however.
 *     <li><b>Events</b>: Collect relevant events (e.g., cheating attempts).
 * </ul>
 * <h2>NOT Responsibilities</h2>
 * Conversely, here's what's not in the responsibility of this controller:
 * <ul>
 *     <li><b>UI</b>: Each client platform will need to implement UI or permissions flows
 *     separately and independently.</li>
 *     <li><b>Clipboard</b>: The delegate is asked to clear the clipboard at the appropriate
 *     points. Why? Because I didn't want to introduce a clipboard abstraction (type) just for this
 *     single action.
 *     <li><b>Apps</b>: The delegate is asked to clear out the (other) apps at the appropriate
 *     points. Why? Because the App instances are managed (and owned) differently on the different
 *     platforms.
 * </ul>
 *
 *  @implNote This class is not designed to be thread-safe.
 */
public final class ExamController {

	@Weak
	@NonOwning
	@Property
	public @CheckForNull ExamControllerDelegate delegate;

	private @NonOwning RestrictionsController restrictionsController;
	private Function<ExamType, Restrictions> examRestrictionsFactory =
			ExamType::createRestrictions;

	private ContextDependencies activeDependencies;
	private ExamType examType;
	private ExamOptions options;

	private ExamState state = ExamState.IDLE;
	private Date startDate;
	private Date finishDate;
	private final Set<ExamListener> listeners = new HashSet<>();
	private final TempStorage tempStorage = new TempStorage();
	private CheatingEvents cheatingEvents = new CheatingEvents();
	private TimeFormatAdapter timeFormatter;

	private final CommandFilter noCASFilter = CommandFilterFactory.createNoCasCommandFilter();

	/**
	 * Creates a new ExamController.
	 * @param restrictionsController The controller responsible for applying restrictions.
	 */
	public ExamController(@Nonnull RestrictionsController restrictionsController) {
		this.restrictionsController = restrictionsController;
	}

	/**
	 * Set the active context and associated dependencies.
	 * <p>
	 * When any of the dependencies (e.g., the command dispatcher or algebra processor) change,
	 * this should be communicated to the exam controller by calling this method.
	 * </p>
	 * This method needs to be called before an exam starts, and also when the active app
	 * changes during an exam, so what we can remove the restrictions from the current dependencies,
	 * and apply the restrictions on the new dependencies.
	 */
	public void setActiveContext(@Nonnull ContextDependencies contextDependencies) {
		if (restrictionsController.getRestrictions() != null && activeDependencies != null) {
			removeExtraRestrictionsFromDependencies();
		}
		restrictionsController.setActiveContext(contextDependencies);
		this.activeDependencies = contextDependencies;
		if (restrictionsController.getRestrictions() != null) {
			applyExtraRestrictionsToDependencies();
			resetDelegateBeforeExam();
		}
	}

	/**
	 * Adds an {@link ExamListener}.
	 * @param listener The listener to add.
	 * @apiNote Trying to add a listener that is already registered will have no effect.
	 */
	public void addListener(@Nonnull ExamListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes an {@link ExamListener}.
	 * @param listener The listener to remove.
	 * @apiNote Trying to remove a listener that is not registered will have no effect.
	 */
	public void removeListener(@Nonnull ExamListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return The current exam state.
	 * <p>
	 * Observable through {@link ExamListener#examStateChanged(ExamState) examStateChanged()}.
	 * </p>
	 */
	public ExamState getState() {
		return state;
	}

	/**
	 * Changes state and notifies listeners about the state change.
	 * @param newState The new state to change to.
	 * @apiNote If newState is equal to the current state, nothing happens.
	 */
	private void setState(ExamState newState) {
		if (newState == state) {
			return;
		}
		state = newState;
		notifyListeners(newState);
	}

	/**
	 * @return The cheating events.
	 */
	public CheatingEvents getCheatingEvents() {
		return cheatingEvents;
	}

	/**
	 * @return The ExamType if an exam is currently active, or null otherwise.
	 */
	public @CheckForNull ExamType getExamType() {
		return examType;
	}

	/**
	 * Get the exam short display name.
	 * @param appConfig The current app config.
	 * @param localization The localization.
	 * @return The current exam's short display name (see
	 * {@link ExamType#getShortDisplayName(Localization, AppConfig)}.
	 */
	public @CheckForNull String getExamName(@Nonnull AppConfig appConfig,
			@Nonnull Localization localization) {
		return examType == null ? null : examType.getShortDisplayName(localization, appConfig);
	}

	/**
	 * @return The exam start date, if an exam is currently active, or null otherwise.
	 */
	public @CheckForNull Date getStartDate() {
		return startDate;
	}

	/**
	 * @return The exam end date, if the exam has been stopped, or null otherwise.
	 */
	public @CheckForNull Date getFinishDate() {
		return finishDate;
	}

	/**
	 * @param localization A localization.
	 * @return The formatted duration since the start of the exam, if an exam is currently
	 * active, or zero (0:00) otherwise.
	 */
	public String getDurationFormatted(@Nonnull Localization localization) {
		if (timeFormatter == null) {
			timeFormatter = FormatFactory.getPrototype().getTimeFormat();
		}
		if (startDate == null) {
			return timeFormatter.format(localization.getLanguageTag(), 0);
		}
		return timeFormatter.format(localization.getLanguageTag(),
				System.currentTimeMillis() - startDate.getTime());
	}

	/**
	 * @return true if cheating events have been recorded during the exam, or false otherwise.
	 */
	public boolean isCheating() {
		return !cheatingEvents.isEmpty();
	}

	/**
	 * @return A summary of the exam if the exam is in the {@link ExamState#ACTIVE} or
	 * {@link ExamState#FINISHED} state, or null otherwise.
	 */
	public @CheckForNull ExamSummary getExamSummary(@Nonnull AppConfig appConfig,
			@Nonnull Localization localization) {
		if (state == ExamState.IDLE || state == ExamState.PREPARING) {
			return null;
		}
		if (timeFormatter == null) {
			timeFormatter = FormatFactory.getPrototype().getTimeFormat();
		}
		return new ExamSummary(examType, startDate, finishDate, cheatingEvents,
				appConfig, timeFormatter, localization);
	}

	/**
	 * Get ready for a new exam.
	 *
	 * Note: This step is optional (can be skipped during crash recovery, for example).
	 * @throws IllegalStateException if the exam controller is not in the {@link ExamState#IDLE IDLE}
	 * state.
	 */
	public void prepareExam() {
		if (state != ExamState.IDLE && state != ExamState.PREPARING) {
			throw new IllegalStateException("expected to be in IDLE or PREPARING state, but is "
					+ state); // allow prepareExam() call also in PREPARING state (APPS-5536)
		}
		setState(ExamState.PREPARING);
		// save current material and restore after exit?
	}

	/**
	 * Cancel the exam.
	 *
	 * If the exam is currently in the {@link ExamState#PREPARING} state, this will change the state
	 * back to {@link ExamState#IDLE}. Otherwise, this method will have no effect.
	 */
	public void cancelExam() {
		if (state == ExamState.PREPARING) {
			setState(ExamState.IDLE);
		}
	}

	/**
	 * Start the exam.
	 * @param examType The exam type.
	 * @param options Additional options (optional).
	 * @throws IllegalStateException if the exam controller is not in either the
	 * {@link ExamState#IDLE} or {@link ExamState#PREPARING PREPARING} state.
	 * @apiNote Make sure to call {@link #setActiveContext} before attempting to start an exam.
	 */
	public void startExam(@Nonnull ExamType examType, @CheckForNull ExamOptions options) {
		if (state != ExamState.IDLE && state != ExamState.PREPARING) {
			throw new IllegalStateException("expected to be in IDLE or PREPARING state, "
					+ "but is " + state);
		}
		if (activeDependencies == null) {
			throw new IllegalStateException("no active context");
		}
		this.examType = examType;
		this.options = options;

		if (delegate != null) {
			delegate.examClearClipboard();
			delegate.examClearApps();
		}
		tempStorage.clearTempMaterials();

		Restrictions examRestrictions = examRestrictionsFactory.apply(examType);
		restrictionsController.applyRestrictions(examRestrictions);

		createNewTempMaterial();

		cheatingEvents = new CheatingEvents();
		cheatingEvents.delegate = (cheatingEvent) -> {
			if (cheatingEvents.size() == 1) {
				notifyListenersCheatingStarted();
			}
		};

		startDate = new Date();
		setState(ExamState.ACTIVE);
	}

	/**
	 * Finish the current exam.
	 * @throws IllegalStateException if the exam controller is not in the {@link ExamState#ACTIVE ACTIVE}
	 * state.
	 */
	public void finishExam() {
		if (state != ExamState.ACTIVE) {
			throw new IllegalStateException("expected to be in ACTIVE state, but is " + state);
		}
		finishDate = new Date();
		setState(ExamState.FINISHED);
	}

	/**
	 * Exit the finished exam.
	 * @throws IllegalStateException if the exam controller is not in the
	 * {@link ExamState#FINISHED FINISHED} state.
	 */
	public void exitExam() {
		if (state != ExamState.FINISHED) {
			throw new IllegalStateException("expected to be in FINISHED state, but is " + state);
		}
		restrictionsController.removeRestrictions();
		resetDelegateAfterExam();
		removeExtraRestrictionsFromDependencies();
		restrictionsController.resetRestrictions();

		tempStorage.clearTempMaterials();
		if (delegate != null) {
			delegate.examClearClipboard();
			delegate.examClearApps();
		}

		startDate = finishDate = null;
		examType = null;
		setState(ExamState.IDLE);
	}

	/**
	 * @return true if the exam is in the {@link ExamState#ACTIVE} state.
	 */
	public boolean isExamActive() {
		return state == ExamState.ACTIVE;
	}

	/**
	 * @return true if the exam is in the {@link ExamState#IDLE} state.
	 */
	public boolean isIdle() {
		return state == ExamState.IDLE;
	}

	private void notifyListeners(ExamState newState) {
		for (ExamListener listener : listeners) {
			listener.examStateChanged(newState);
		}
	}

	private void notifyListenersCheatingStarted() {
		for (ExamListener listener : listeners) {
			listener.cheatingStarted();
		}
	}

	private void resetDelegateBeforeExam() {
		if (delegate == null) {
			return;
		}
		if (delegate.examGetActiveMaterial() == null) {
			delegate.examSetActiveMaterial(tempStorage.newMaterial());
		}
	}

	private void resetDelegateAfterExam() {
		if (delegate == null) {
			return;
		}
		delegate.examClearApps();
		if (delegate.examGetActiveMaterial() == null) {
			delegate.examSetActiveMaterial(tempStorage.newMaterial());
		}
	}

	private void applyExtraRestrictionsToDependencies() {
		if (activeDependencies == null) {
			return;
		}
		if (options != null && !options.casEnabled) {
			activeDependencies.commandDispatcher().addCommandFilter(noCASFilter);
		}
	}

	private void removeExtraRestrictionsFromDependencies() {
		if (activeDependencies == null) {
			return;
		}
		if (options != null && !options.casEnabled) {
			activeDependencies.commandDispatcher().removeCommandFilter(noCASFilter);
		}
	}

	/**
	 * Re-apply settings restrictions for ClearAll during exam.
	 */
	public void reapplySettingsRestrictions() {
		Restrictions restrictions = restrictionsController.getRestrictions();
		if (restrictions != null) {
			restrictions.reapplySettingsRestrictions();
		}
	}

	/**
	 * Unfortunately we need to expose this - some (iOS) client code currently needs access.
	 * @return The temporary material storage for exams.
	 */
	@Deprecated // don't use this (see doc)
	public TempStorage getTempStorage() {
		return tempStorage;
	}

	/**
	 * Creates a new temporary material. Also calls the delegate's
	 * {@link ExamControllerDelegate#examSetActiveMaterial(Material)} method.
	 */
	public void createNewTempMaterial() {
		Material material = tempStorage.newMaterial();
		if (delegate != null) {
			delegate.examSetActiveMaterial(material);
		}
	}

	/**
	 * Creates a new temporary material, does not notify any delegates.
	 * @return the created material
	 */
	public Material getNewTempMaterial() {
		return tempStorage.newMaterial();
	}

	/**
	 * Saves the material in temp storage.
	 * @param material An exam material.
	 */
	public void saveTempMaterial(Material material) {
		tempStorage.saveTempMaterial(material);
	}

	// Test support API

	void setExamRestrictionsFactory(Function<ExamType, Restrictions> examRestrictionsFactory) {
		this.examRestrictionsFactory = examRestrictionsFactory;
	}
}
