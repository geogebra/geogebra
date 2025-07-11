package org.geogebra.common.exam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ScheduledPreviewFromInputBar;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.TempStorage;
import org.geogebra.common.main.exam.event.CheatingEvents;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.ownership.NonOwning;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.util.TimeFormatAdapter;

import com.google.j2objc.annotations.Weak;

/**
 * A controller for coordinating the core aspects of exam mode.
 *
 * <h3>Responsibilities</h3>
 * Here's the list of responsibilities of this controller:
 * <ul>
 *     <li><b>Exam state</b>: Starting, stopping, and finishing up the exam, and making the
 *     current exam state available (both as a getter, and as a change notification to registered
 *     listeners).
 *     <li><b>Start/end date</b>: Setting the exam start and end date, and providing date and
 *     time formatting for these.
 *     <li><b>Restrictions</b>: Applying restrictions on certain components (e.g.,  the
 *     {@link CommandDispatcher}) at the start of an exam, and reverting those restrictions
 *     when the exam ends.
 *     <li><b>Events</b>: Collect relevant events (e.g., cheating attempts).
 * </ul>
 * <h3>NOT Responsibilities</h3>
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
	public ExamControllerDelegate delegate;

	private List<ExamControllerDelegate> delegates = new ArrayList<>();

	private Function<ExamType, ExamRestrictions> examRestrictionsFactory =
			ExamRestrictions::forExamType;
	private @NonOwning PropertiesRegistry propertiesRegistry;
	private @NonOwning GeoElementPropertiesFactory geoElementPropertiesFactory;
	private @NonOwning ContextMenuFactory contextMenuFactory;

	private Set<ExamRestrictable> restrictables = new HashSet<>();
	/** this is only for the mobile use case (1 Suite app instance) */
	private ContextDependencies activeDependencies;
	/** this is only for the Web use case (multiple Suite app instances) */
	private @CheckForNull List<ContextDependencies> registeredDependencies = null;

	private ExamType examType;
	private ExamRestrictions examRestrictions;
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
	 * @param propertiesRegistry The properties registry.
	 * @param geoElementPropertiesFactory The properties factory for geo elements.
	 * @param contextMenuFactory The context menu factory.
	 * @implNote The ExamController will register itself as a listener on the properties registry.
	 */
	public ExamController(
			@Nonnull PropertiesRegistry propertiesRegistry,
			@Nonnull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nonnull ContextMenuFactory contextMenuFactory) {
		this.propertiesRegistry = propertiesRegistry;
		this.geoElementPropertiesFactory = geoElementPropertiesFactory;
		this.contextMenuFactory = contextMenuFactory;
	}

	/**
	 * Sets the delegate.
	 * @param delegate The delegate.
	 * @apiNote It is assumed that the delegate is set before attempting to start an exam.
	 * Used in the mobile case where only one Suite app can exist at a time.
	 * @implNote This method is provided for J2ObjC.
	 */
	public void setDelegate(@NonOwning ExamControllerDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Adds a delegate.
	 * @param delegate The delegate
	 * @apiNote to be used in web together with registerContext
	 */
	public void registerDelegate(@NonOwning ExamControllerDelegate delegate) {
		this.delegates.add(delegate);
	}

	/**
	 * Register additional app context (Web use case).
	 * Same parameter semantics as {@link #setActiveContext}
	 * @apiNote This method is intended for the Web use case, and must be called once on app
	 * initialization, before any attempts to start an exam.
	 */
	public void registerContext(ContextDependencies contextDependencies) {
		if (activeDependencies != null) {
			throw new IllegalStateException(
					"registerContexts() must not be mixed with calls to setActiveContext()");
		}
		if (registeredDependencies == null) {
			registeredDependencies = new ArrayList<>();
		}
		registeredDependencies.add(contextDependencies);
		if (state == ExamState.ACTIVE) {
			applyRestrictionsToContextDependencies(contextDependencies);
			applyRestrictionsToRestrictables();
		}
	}

	/**
	 * Set the active context and associated dependencies.
	 * <p>
	 * The context can be <i>any object</i>, but it should correspond to or identify the current
	 * app, or, in Suite, the currently active sub-app (Graphing, Geometry, etc). The only
	 * requirement here is that when any of the dependencies (e.g., the command dispatcher or
	 * algebra processor) change, this should also mean a change in current context and be
	 * communicated to the exam controller by calling this method.
	 * </p>
	 * This method needs to be called before an exam starts, and also when the active app
	 * changes during an exam, so what we can remove the restrictions on the current dependencies,
	 * and apply the restrictions on the new dependencies.
	 * @apiNote This method is intended for the mobile use case, and must not be mixed with
	 * calls to {@link #registerContext}.
	 */
	public void setActiveContext(
			@Nonnull Object context,
			@Nonnull AlgoDispatcher algoDispatcher,
			@Nonnull CommandDispatcher commandDispatcher,
			@Nonnull AlgebraProcessor algebraProcessor,
			@Nonnull Localization localization,
			@Nonnull Settings settings,
			@Nonnull StatisticGroupsBuilder statisticGroupsBuilder,
			@CheckForNull AutocompleteProvider autocompleteProvider,
			@CheckForNull ToolsProvider toolsProvider,
			@CheckForNull ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
			@CheckForNull Construction construction) {
		if (registeredDependencies != null) {
			throw new IllegalStateException(
					"setActiveContext() must not be mixed with calls to registerContexts()");
		}
		// remove restrictions for current dependencies, if exam is active
		if (examRestrictions != null && activeDependencies != null) {
			removeRestrictionsFromContextDependencies(activeDependencies);
		}
		ContextDependencies contextDependencies = new ContextDependencies(context,
				algoDispatcher,
				commandDispatcher,
				algebraProcessor,
				localization,
				settings,
				statisticGroupsBuilder,
				autocompleteProvider,
				toolsProvider,
				scheduledPreviewFromInputBar,
				construction);
		activeDependencies = contextDependencies;
		// apply restrictions to new dependencies, if exam is active
		if (examRestrictions != null) {
			applyRestrictionsToDelegates(examRestrictions);
			applyRestrictionsToContextDependencies(contextDependencies);
		}
	}

	/**
	 * Remove a context. Called when the context is no longer needed,
	 * does not remove any restrictions.
	 * @param context exam context
	 */
	public void unregisterContext(Object context) {
		if (registeredDependencies != null) {
			registeredDependencies.removeIf(deps -> deps.context == context);
		}
	}

	/**
	 * Register an object that may need to apply additional restrictions/customization
	 * for certain types of exams.
	 * @param restrictable An object that may need to perform additional customization
	 * when an exam is started.
	 * @apiNote When an exam is currently active, the {@link ExamRestrictable} is asked
	 * to apply the current {@link ExamRestrictions} immediately.
	 */
	public void registerRestrictable(@Nonnull ExamRestrictable restrictable) {
		restrictables.add(restrictable);
		if (examRestrictions != null) {
			restrictable.applyRestrictions(examRestrictions.getFeatureRestrictions(), examType);
		}
	}

	/**
	 * Unregister an {@link ExamRestrictable}.
	 * @param restrictable An object that that was previously registered with
	 * {@link #registerRestrictable(ExamRestrictable)}..
	 * @apiNote When an exam is currently active, the {@link ExamRestrictable} is asked
	 * to remove the current {@link ExamRestrictions} immediately.
	 */
	public void unregisterRestrictable(@Nonnull ExamRestrictable restrictable) {
		if (examRestrictions != null) {
			restrictable.removeRestrictions(examRestrictions.getFeatureRestrictions(), examType);
		}
		restrictables.remove(restrictable);
	}

	/**
	 * Adds an {@link ExamListener}.
	 * @param listener The listener to add.
	 * @apiNote Trying to add a listener that is already registered will have no effect.
	 */
	public void addListener(@NonOwning @Nonnull ExamListener listener) {
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
	 * Get the list of disabled subapps, if any.
	 * @return The set of disabled (restricted) sub-apps, or null if there are no
	 * restrictions on sub-apps currently.
	 */
	public @CheckForNull Set<SuiteSubApp> getDisabledSubApps() {
		return examRestrictions != null ? examRestrictions.getDisabledSubApps() : null;
	}

	/**
	 * Get the list of disabled subapps, if any.
	 * @return The set of disabled (restricted) sub-app codes, or null if there are no
	 * restrictions on sub-apps currently.
	 */
	public @CheckForNull Set<String> getDisabledSubAppCodes() {
		Set<SuiteSubApp> disabledSubApps = getDisabledSubApps();
		if (disabledSubApps == null) {
			return null;
		}
		return disabledSubApps.stream().map(subApp -> subApp.appCode).collect(Collectors.toSet());
	}

	/**
	 * Check for disabled subapps.
	 * @param appCode A sub-app code (e.g.
	 * {@link org.geogebra.common.GeoGebraConstants#GRAPHING_APPCODE}).
	 * @return True if the sub-app corresponding to appCode is currently disabled, false otherwise.
	 */
	public boolean isDisabledSubApp(String appCode) {
		return isDisabledSubApp(SuiteSubApp.forCode(appCode));
	}

	/**
	 * Check for disabled subapps.
	 * @param subApp A sub-app
	 * @return True if the sub-app corresponding to appCode is currently disabled, false otherwise.
	 */
	public boolean isDisabledSubApp(SuiteSubApp subApp) {
		Set<SuiteSubApp> disabledSubApps = getDisabledSubApps();
		if (disabledSubApps == null) {
			return false;
		}
		return disabledSubApps.contains(subApp);
	}

	/**
	 * Check for disabled features.
	 * @param featureRestriction A feature restriction.
	 * @return True if the exam is currently active and the feature is restricted, false
	 * otherwise.
	 */
	public boolean isFeatureRestricted(ExamFeatureRestriction featureRestriction) {
		return examRestrictions != null && examRestrictions
				.getFeatureRestrictions().contains(featureRestriction);
	}

	/**
	 * Get the exam short display name.
	 * @param appConfig The current app config.
	 * @param localization The localization.
	 * @return The current exam's short display name (see
	 * {@link ExamType#getShortDisplayName(Localization, AppConfig)}.
	 */
	public @CheckForNull String getExamName(AppConfig appConfig, Localization localization) {
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
	public String getDurationFormatted(Localization localization) {
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
	public @CheckForNull ExamSummary getExamSummary(AppConfig appConfig,
			Localization localization) {
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
		if (activeDependencies == null && registeredDependencies == null) {
			throw new IllegalStateException("no active context(s)");
		}

		this.examType = examType;
		this.options = options;

		forEachDelegate(delegate -> {
			delegate.examClearClipboard();
			delegate.examClearApps();
		});
		tempStorage.clearTempMaterials();
		createNewTempMaterial();

		ExamRestrictions restrictions = examRestrictionsFactory.apply(examType);
		propertiesRegistry.addListener(restrictions);
		applyRestrictionsToDelegates(restrictions);
		// delay setting the examRestrictions field until after delegates have been notified
		// (see https://git.geogebra.org/ggb/geogebra/-/merge_requests/9370#note_76206)
		examRestrictions = restrictions;

		if (activeDependencies != null) {
			applyRestrictionsToContextDependencies(activeDependencies);
		} else if (registeredDependencies != null) {
			registeredDependencies.forEach(this::applyRestrictionsToContextDependencies);
		}
		applyRestrictionsToRestrictables();

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
		propertiesRegistry.removeListener(examRestrictions);
		if (activeDependencies != null) {
			removeRestrictionsFromContextDependencies(activeDependencies);
		} else if (registeredDependencies != null) {
			registeredDependencies.forEach(this::removeRestrictionsFromContextDependencies);
		}
		removeRestrictionsFromRestrictables();
		tempStorage.clearTempMaterials();
		forEachDelegate(delegate -> {
			delegate.examClearClipboard();
			delegate.examClearApps();
		});
		startDate = finishDate = null;
		examType = null;
		examRestrictions = null;
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

	private void applyRestrictionsToDelegates(ExamRestrictions restrictions) {
		forEachDelegate(delegate -> {
			// switching away from restricted subapp only possible in mobile use case;
			// in Web, there may be several Suite instances, so there's no "current subapp"
			SuiteSubApp currentSubApp = delegate.examGetCurrentSubApp();
			Set<SuiteSubApp> disabledSubApps = restrictions.getDisabledSubApps();
			if (currentSubApp == null
					|| (disabledSubApps != null && disabledSubApps.contains(currentSubApp))) {
				delegate.examSwitchSubApp(restrictions.getDefaultSubApp());
			}
			if (delegate.examGetActiveMaterial() == null) {
				delegate.examSetActiveMaterial(tempStorage.newMaterial());
			}
		});
	}

	private void applyRestrictionsToContextDependencies(ContextDependencies dependencies) {
		if (examRestrictions == null) {
			return; // log/throw?
		}
		if (dependencies != null) {
			examRestrictions.applyTo(dependencies,
					propertiesRegistry,
					geoElementPropertiesFactory,
					contextMenuFactory);
			if (options != null && !options.casEnabled) {
				dependencies.commandDispatcher.addCommandFilter(noCASFilter);
			}
		}
	}

	private void removeRestrictionsFromContextDependencies(ContextDependencies dependencies) {
		if (examRestrictions == null) {
			return;
		}
		if (dependencies != null) {
			examRestrictions.removeFrom(dependencies,
					propertiesRegistry,
					geoElementPropertiesFactory,
					contextMenuFactory);
			if (options != null && !options.casEnabled) {
				dependencies.commandDispatcher.removeCommandFilter(noCASFilter);
			}
		}
	}

	/**
	 * Re-applies the {@link ExamRestrictions} by removing, and subsequently adding them again
	 * to the {@link #restrictables}
	 */
	public void reapplyRestrictionsToRestrictables() {
		removeRestrictionsFromRestrictables();
		applyRestrictionsToRestrictables();
	}

	private void applyRestrictionsToRestrictables() {
		for (ExamRestrictable restrictable : restrictables) {
			restrictable.applyRestrictions(examRestrictions.getFeatureRestrictions(), examType);
		}
	}

	private void removeRestrictionsFromRestrictables() {
		for (ExamRestrictable restrictable : restrictables) {
			restrictable.removeRestrictions(examRestrictions.getFeatureRestrictions(), examType);
		}
	}

	/**
	 * Re-apply settings restrictions for ClearAll during exam.
	 */
	public void reapplySettingsRestrictions() {
		if (examRestrictions != null) {
			examRestrictions.reapplySettingsRestrictions();
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
		forEachDelegate(delegate -> {
			Material material = tempStorage.newMaterial();
			if (delegate != null) {
				delegate.examSetActiveMaterial(material);
			}
		});
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

	void setExamRestrictionsFactory(Function<ExamType, ExamRestrictions> examRestrictionsFactory) {
		this.examRestrictionsFactory = examRestrictionsFactory;
	}

	private void forEachDelegate(Consumer<ExamControllerDelegate> consumer) {
		if (delegate != null) {
			consumer.accept(delegate);
		} else {
			delegates.forEach(consumer);
		}
	}

	public static class ContextDependencies {
		@NonOwning
		public final @Nonnull Object context;
		@NonOwning
		public final @Nonnull AlgoDispatcher algoDispatcher;
		@NonOwning
		public final @Nonnull CommandDispatcher commandDispatcher;
		@NonOwning
		public final @Nonnull AlgebraProcessor algebraProcessor;
		@NonOwning
		public final @Nonnull Localization localization;
		@NonOwning
		public final @CheckForNull Settings settings;
		@NonOwning
		public final @Nonnull StatisticGroupsBuilder statisticGroupsBuilder;
		@NonOwning
		public final @CheckForNull AutocompleteProvider autoCompleteProvider;
		@NonOwning
		public final @CheckForNull ToolsProvider toolsProvider;
		@NonOwning
		public final @CheckForNull ScheduledPreviewFromInputBar scheduledPreviewFromInputBar;
		@NonOwning
		public final @CheckForNull Construction construction;

		/**
		 * @param context context (e.g. App instance)
		 * @param algoDispatcher algorithm dispatcher
		 * @param commandDispatcher command dispatcher
		 * @param algebraProcessor algebra processor
		 * @param localization localization
		 * @param settings settings
		 * @param autoCompleteProvider autocomplete provider
		 * @param toolsProvider tools provider
		 * @param scheduledPreviewFromInputBar scheduled preview provider
		 * @param construction construction
		 */
		public ContextDependencies(@Nonnull Object context,
				@Nonnull AlgoDispatcher algoDispatcher,
				@Nonnull CommandDispatcher commandDispatcher,
				@Nonnull AlgebraProcessor algebraProcessor,
				@Nonnull Localization localization,
				@Nonnull Settings settings,
				@Nonnull StatisticGroupsBuilder statisticGroupsBuilder,
				@CheckForNull AutocompleteProvider autoCompleteProvider,
				@CheckForNull ToolsProvider toolsProvider,
				@CheckForNull ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
				@CheckForNull Construction construction) {
			this.context = context;
			this.algoDispatcher = algoDispatcher;
			this.commandDispatcher = commandDispatcher;
			this.algebraProcessor = algebraProcessor;
			this.localization = localization;
			this.settings = settings;
			this.statisticGroupsBuilder = statisticGroupsBuilder;
			this.autoCompleteProvider = autoCompleteProvider;
			this.toolsProvider = toolsProvider;
			this.scheduledPreviewFromInputBar = scheduledPreviewFromInputBar;
			this.construction = construction;
		}
	}
}
