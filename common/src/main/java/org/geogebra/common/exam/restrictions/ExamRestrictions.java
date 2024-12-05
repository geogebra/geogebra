package org.geogebra.common.exam.restrictions;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ScheduledPreviewFromInputBar;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ExamCommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.ConstructionElementSetup;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;

/**
 * Represents restrictions that apply during exams.
 * <p/>
 * Restrictions that are specific to the different exam types are represented as subclasses
 * of this class.
 * Restrictions that apply to all exam types should be implemented in this class
 * (in {@link #applyTo(CommandDispatcher, AlgebraProcessor, PropertiesRegistry, Object,
 * Localization, Settings, AutocompleteProvider, ToolsProvider, GeoElementPropertiesFactory,
 * Construction, ScheduledPreviewFromInputBar, ContextMenuFactory)}).
 * <p/>
 * Any restrictions to be applied during exams should be implemented in here (so that
 * everything is one place):
 * <ul>
 * <li>disabled subapps,</li>
 * <li>restricted commands,</li>
 * <li>restricted expressions,</li>
 * <li>frozen properties,</li>
 * <li>etc.</li>
 * </ul>
 */
public class ExamRestrictions implements PropertiesRegistryListener {

	private final ExamType examType;
	private final Set<SuiteSubApp> disabledSubApps;
	private final SuiteSubApp defaultSubApp;
	private final Set<ExamFeatureRestriction> featureRestrictions;
	private final Set<ExpressionFilter> inputExpressionFilters;
	private final Set<ExpressionFilter> outputExpressionFilters;
	private final Set<CommandFilter> commandFilters;
	private final Set<Operation> filteredOperations;
	private final Set<CommandArgumentFilter> commandArgumentFilters;
	private final Set<ContextMenuItemFilter> contextMenuItemFilters;
	// filter independent of exam region
	private final CommandArgumentFilter examCommandArgumentFilter =
			new ExamCommandArgumentFilter();
	private final SyntaxFilter syntaxFilter;
	private final ToolCollectionFilter toolsFilter;
	private final Map<String, PropertyRestriction> propertyRestrictions;
	private final Set<GeoElementPropertyFilter> geoElementPropertyFilters;
	private final Set<ConstructionElementSetup> constructionElementSetups;
	private RestorableSettings savedSettings;
	private Settings restrictedSettings = null;

	/**
	 * Factory for ExamRestrictions.
	 * @param examType The exam type.
	 * @return An {@link ExamRestrictions} subclass that contains all the restrictions for
	 * the given exam type.
	 */
	public static ExamRestrictions forExamType(ExamType examType) {
		switch (examType) {
		case BAYERN_CAS:
			return new BayernCasExamRestrictions();
		case CVTE:
			return new CvteExamRestrictions();
		case IB:
			return new IBExamRestrictions();
		case NIEDERSACHSEN:
			return new NiedersachsenExamRestrictions();
		case REALSCHULE:
			return new RealschuleExamRestrictions();
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		case MMS:
			return new MmsExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
	}

	/**
	 * Prevent instantiation, except by subclasses.
	 * @param examType The exam type.
	 * @param disabledSubApps An optional set of disabled subapps for this exam type. Passing in
	 * null is equivalent to passing in an empty set.
	 * @param defaultSubApp An optional subapp to activate at the start of an exam, if the
	 * current subapp is in the list of restricted subapps. If null, Graphing will be used as the
	 * default subapp.
	 * @param featureRestrictions An optional set of features to disable during the exam.
	 * @param inputExpressionFilters An optional set of expression filters (e.g., ||) to apply during
	 * exams to the algebra inputs.
	 * @param outputExpressionFilters An optional set of expression filters (e.g., ||) to apply during
	 * exams to the algebra outputs.
	 * @param commandFilters An optional command filter to apply during exams.
	 * @param commandArgumentFilters An optional command argument filter to apply during exams.
	 * @param filteredOperations An optional set of operations to filter out during exams.
	 * @param syntaxFilter An optional syntax filter to apply during exams.
	 * @param toolsFilter An optional filter for tools that should be unavailable during the exam.
	 * If this argument is null, the Image tool will still be filtered out (APPS-5214). When
	 * providing a non-null filter here, it should include the Image tool.
	 * @param propertyRestrictions An optional map of properties and restrictions
	 * to be applied to them during the exam.
	 */
	// TODO APPS-5867: add EquationBehaviour to exam
	protected ExamRestrictions(
			@Nonnull ExamType examType,
			@Nullable Set<SuiteSubApp> disabledSubApps,
			@Nullable SuiteSubApp defaultSubApp,
			@Nullable Set<ExamFeatureRestriction> featureRestrictions,
			@Nullable Set<ExpressionFilter> inputExpressionFilters,
			@Nullable Set<ExpressionFilter> outputExpressionFilters,
			@Nullable Set<CommandFilter> commandFilters,
			@Nullable Set<CommandArgumentFilter> commandArgumentFilters,
			@Nullable Set<Operation> filteredOperations,
			@Nullable Set<ContextMenuItemFilter> contextMenuItemFilters,
			@Nullable SyntaxFilter syntaxFilter,
			@Nullable ToolCollectionFilter toolsFilter,
			@Nullable Map<String, PropertyRestriction> propertyRestrictions,
			@Nullable Set<GeoElementPropertyFilter> geoElementPropertyFilters,
			@Nullable Set<ConstructionElementSetup> constructionElementSetups) {
		this.examType = examType;
		this.disabledSubApps = disabledSubApps != null ? disabledSubApps : Set.of();
		this.defaultSubApp = defaultSubApp != null ? defaultSubApp : SuiteSubApp.GRAPHING;
		this.featureRestrictions = featureRestrictions != null ? featureRestrictions : Set.of();
		this.inputExpressionFilters =
				inputExpressionFilters != null ? inputExpressionFilters : Set.of();
		this.outputExpressionFilters =
				outputExpressionFilters != null ? outputExpressionFilters : Set.of();
		this.commandFilters = commandFilters != null ? commandFilters : Set.of();
		this.commandArgumentFilters = commandArgumentFilters != null
				? commandArgumentFilters : Set.of();
		this.filteredOperations = filteredOperations != null ? filteredOperations : Set.of();
		this.contextMenuItemFilters =
				contextMenuItemFilters != null ? contextMenuItemFilters : Set.of();
		this.syntaxFilter = syntaxFilter;
		this.toolsFilter = toolsFilter != null ? toolsFilter
				: new ToolCollectionSetFilter(EuclidianConstants.MODE_IMAGE);
		this.propertyRestrictions = propertyRestrictions != null ? propertyRestrictions : Map.of();
		this.geoElementPropertyFilters = geoElementPropertyFilters != null
				? geoElementPropertyFilters : Set.of();
		this.constructionElementSetups = constructionElementSetups != null
				? constructionElementSetups : Set.of();
	}

	/**
	 * @return The exam type.
	 */
	public final @Nonnull ExamType getExamType() {
		return examType;
	}

	/**
	 * @return The list of disabled (i.e., not allowed) subapps during exams, or an empty set
	 * if there is no restriction on the available subapps.
	 */
	public final @Nonnull Set<SuiteSubApp> getDisabledSubApps() {
		return disabledSubApps;
	}

	/**
	 * @return The default subapp to switch to if a disabled subapp is active when
	 * the exam starts.
	 */
	public final @Nonnull SuiteSubApp getDefaultSubApp() {
		return defaultSubApp;
	}

	/**
	 * @return The set of disabled features during exam, or an empty set if there's no
	 * restrictions on available features in the apps.
	 */
	public final @Nonnull Set<ExamFeatureRestriction> getFeatureRestrictions() {
		return featureRestrictions;
	}

	/**
	 * Apply the exam restrictions.
	 */
	public void applyTo(
			@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider,
			@Nullable GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nullable Construction construction,
			@Nullable ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
			@Nullable ContextMenuFactory contextMenuFactory) {
		if (commandDispatcher != null) {
			for (CommandFilter commandFilter : commandFilters) {
				commandDispatcher.addCommandFilter(commandFilter);
			}
			commandDispatcher.addCommandArgumentFilter(examCommandArgumentFilter);
			for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
				commandDispatcher.addCommandArgumentFilter(commandArgumentFilter);
			}
		}
		if (algebraProcessor != null) {
			for (ExpressionFilter expressionFilter : inputExpressionFilters) {
				algebraProcessor.addInputExpressionFilter(expressionFilter);
			}
			for (ExpressionFilter expressionFilter : outputExpressionFilters) {
				algebraProcessor.addOutputExpressionFilter(expressionFilter);
			}
			algebraProcessor.reinitCommands();
		}
		if (syntaxFilter != null) {
			if (autoCompleteProvider != null) {
				autoCompleteProvider.addSyntaxFilter(syntaxFilter);
			}
			if (localization != null) {
				localization.getCommandSyntax().addSyntaxFilter(syntaxFilter);
			}
		}
		if (autoCompleteProvider != null) {
			autoCompleteProvider.setFilteredOperations(filteredOperations);
		}
		if (propertiesRegistry != null) {
			propertyRestrictions.forEach((name, restriction) -> {
				Property property = propertiesRegistry.lookup(name, context);
				if (property != null) {
					restriction.applyTo(property);
				}
			});
		}
		if (toolsProvider != null && toolsFilter != null) {
			toolsProvider.addToolsFilter(toolsFilter);
		}
		if (geoElementPropertiesFactory != null) {
			geoElementPropertyFilters.forEach(geoElementPropertiesFactory::addFilter);
		}
		if (construction != null) {
			constructionElementSetups.forEach(construction::addConstructionElementSetup);
		}
		if (scheduledPreviewFromInputBar != null) {
			constructionElementSetups.forEach(
					scheduledPreviewFromInputBar::addConstructionElementSetup);
		}
		if (contextMenuFactory != null) {
			for (ContextMenuItemFilter contextMenuItemFilter : contextMenuItemFilters) {
				contextMenuFactory.addFilter(contextMenuItemFilter);
			}
		}
		if (settings != null) {
			this.restrictedSettings = settings;
			saveSettings(settings);
			applySettingsRestrictions(settings);
		}
	}

	/**
	 * Creates an object that settings can be saved in exam start, and can be easily restored
	 * at exam exit.
	 * @return {@link RestorableSettings}
	 */
	protected RestorableSettings createSavedSettings() {
		return null;
	}

	/**
	 * Re-apply settings changes for this exam type (for ClearAll during exam).
	 */
	public void reapplySettingsRestrictions() {
		if (restrictedSettings != null) {
			applySettingsRestrictions(restrictedSettings);
		}
	}

	/**
	 * Apply settings changes for this exam type.
	 * @apiNote Override this only if the given exam needs custom settings.
	 * @param settings {@link Settings}
	 */
	public void applySettingsRestrictions(@Nonnull Settings settings) {
		// empty by default
	}

	private void saveSettings(Settings settings) {
		savedSettings = createSavedSettings();
		if (savedSettings != null) {
			savedSettings.save(settings);
		}
	}

	/**
	 * Revert changes applied in {@link #applySettingsRestrictions(Settings)}, restoring the
	 * previously saved settings.
	 * @apiNote An override is not needed by default.
	 * @param settings {@link Settings}
	 */
	protected void removeSettingsRestrictions(@Nonnull Settings settings) {
		if (savedSettings != null) {
			savedSettings.restore(settings);
			savedSettings = null;
			restrictedSettings = null;
		}
	}

	/**
	 * Remove the exam restrictions (i.e., undo the changes from
	 * {@link #applyTo(CommandDispatcher, AlgebraProcessor, PropertiesRegistry, Object,
	 * Localization, Settings, AutocompleteProvider, ToolsProvider, GeoElementPropertiesFactory,
	 * Construction, ScheduledPreviewFromInputBar, ContextMenuFactory)}).
	 */
	public void removeFrom(
			@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider,
			@Nullable GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nullable Construction construction,
			@Nullable ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
			@Nullable ContextMenuFactory contextMenuFactory) {
		if (commandDispatcher != null) {
			for (CommandFilter commandFilter : commandFilters) {
				commandDispatcher.removeCommandFilter(commandFilter);
			}
			commandDispatcher.removeCommandArgumentFilter(examCommandArgumentFilter);
			for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
				commandDispatcher.removeCommandArgumentFilter(commandArgumentFilter);
			}
		}
		if (algebraProcessor != null) {
			for (ExpressionFilter expressionFilter : inputExpressionFilters) {
				algebraProcessor.removeInputExpressionFilter(expressionFilter);
			}
			for (ExpressionFilter expressionFilter : outputExpressionFilters) {
				algebraProcessor.removeOutputExpressionFilter(expressionFilter);
			}
			algebraProcessor.reinitCommands();
		}
		if (syntaxFilter != null) {
			if (autoCompleteProvider != null) {
				autoCompleteProvider.removeSyntaxFilter(syntaxFilter);
			}
			if (localization != null) {
				localization.getCommandSyntax().removeSyntaxFilter(syntaxFilter);
			}
		}
		if (autoCompleteProvider != null) {
			autoCompleteProvider.setFilteredOperations(null);
		}
		if (propertiesRegistry != null) {
			propertyRestrictions.forEach((name, restriction) -> {
				Property property = propertiesRegistry.lookup(name, context);
				if (property != null) {
					restriction.removeFrom(property);
				}
			});
		}
		if (toolsProvider != null && toolsFilter != null) {
			toolsProvider.removeToolsFilter(toolsFilter);
		}
		if (geoElementPropertiesFactory != null) {
			geoElementPropertyFilters.forEach(geoElementPropertiesFactory::removeFilter);
		}
		if (construction != null) {
			constructionElementSetups.forEach(construction::removeConstructionElementSetup);
		}
		if (scheduledPreviewFromInputBar != null) {
			constructionElementSetups.forEach(
					scheduledPreviewFromInputBar::removeConstructionElementSetup);
		}
		if (contextMenuFactory != null) {
			for (ContextMenuItemFilter contextMenuItemFilter : contextMenuItemFilters) {
				contextMenuFactory.removeFilter(contextMenuItemFilter);
			}
		}
		if (settings != null) {
			removeSettingsRestrictions(settings);
		}
	}

	// PropertiesRegistryListener

	/**
	 * Handles freezing properties on lazy property instantiation/registration.
	 * @param property A property that just got registered.
	 */
	@Override
	public void propertyRegistered(@Nonnull Property property, Object context) {
		if (propertyRestrictions.containsKey(property.getRawName())) {
			propertyRestrictions.get(property.getRawName()).applyTo(property);
		}
	}

	/**
	 * Handles unfreezing any frozen properties on deregistration.
	 * @param property A property that just got unregistered.
	 */
	@Override
	public void propertyUnregistered(@Nonnull Property property, Object context) {
		if (propertyRestrictions.containsKey(property.getRawName())) {
			propertyRestrictions.get(property.getRawName()).removeFrom(property);
		}
	}
}
