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

package org.geogebra.common.exam.restrictions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.gui.view.table.dialog.StatisticsFilter;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.algos.DisabledAlgorithms;
import org.geogebra.common.kernel.arithmetic.filter.DeepExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ExamCommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.GeoElementSetup;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;

/**
 * Represents restrictions that apply during exams.
 * <p>
 * Restrictions that are specific to the different exam types are represented as subclasses
 * of this class.
 * Restrictions that apply to all exam types should be implemented in this class (in {@link #applyTo}).
 * <p>
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
public class ExamRestrictions {

	private final @Nonnull ExamType examType;
	private final @Nonnull Set<SuiteSubApp> disabledSubApps;
	private final @Nonnull SuiteSubApp defaultSubApp;
	private final @Nonnull Set<ExamFeatureRestriction> featureRestrictions;
	private final @Nonnull Set<ExpressionFilter> inputExpressionFilters;
	private final @Nonnull Set<ExpressionFilter> outputExpressionFilters;
	private final @Nonnull Set<CommandFilter> commandFilters;
	private final @CheckForNull OperationFilter operationFilter;
	private final @Nonnull Set<CommandArgumentFilter> commandArgumentFilters;
	private final @Nonnull Set<ContextMenuItemFilter> contextMenuItemFilters;
	private final @Nonnull Set<DisabledAlgorithms> disabledAlgorithms;
	private final @Nonnull Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters;
	private final @CheckForNull StatisticsFilter statisticsFilter;
	// filter independent of exam region
	private final CommandArgumentFilter examCommandArgumentFilter =
			new ExamCommandArgumentFilter();
	private final SyntaxFilter syntaxFilter;
	private final ToolCollectionFilter toolsFilter;
	private final Map<PropertyKey, PropertyRestriction> propertyRestrictions;
	private final GeoElementPropertyFilter restrictedGeoElementVisibilityPropertyFilter;
	private final GeoElementSetup restrictedGeoElementVisibilitySetup;
	private final @CheckForNull EquationBehaviour equationBehaviour;
	private @CheckForNull EquationBehaviour originalEquationBehaviour;
	private RestorableSettings savedSettings;
	private Settings restrictedSettings = null;
	private ConstructionDefaults restrictedDefaults;

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
		case BAYERN_GR:
			return new RealschuleExamRestrictions();
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		case MMS:
			return new MmsExamRestrictions();
		case WTR:
			return new WtrExamRestrictions();
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
	 * @param operationFilter An optional operation filter to apply during exams.
	 * @param syntaxFilter An optional syntax filter to apply during exams.
	 * @param toolsFilter An optional filter for tools that should be unavailable during the exam.
	 * If this argument is null, the Image tool will still be filtered out (APPS-5214). When
	 * providing a non-null filter here, it should include the Image tool.
	 * @param propertyRestrictions An optional map of properties and restrictions
	 * to be applied to them during the exam.
	 * @param statisticsFilter A statistic filter to be applied during the exam.
	 */
	protected ExamRestrictions(
			@Nonnull ExamType examType,
			@CheckForNull Set<SuiteSubApp> disabledSubApps,
			@CheckForNull SuiteSubApp defaultSubApp,
			@CheckForNull Set<ExamFeatureRestriction> featureRestrictions,
			@CheckForNull Set<ExpressionFilter> inputExpressionFilters,
			@CheckForNull Set<ExpressionFilter> outputExpressionFilters,
			@CheckForNull Set<CommandFilter> commandFilters,
			@CheckForNull Set<CommandArgumentFilter> commandArgumentFilters,
			@CheckForNull OperationFilter operationFilter,
			@CheckForNull Set<ContextMenuItemFilter> contextMenuItemFilters,
			@CheckForNull SyntaxFilter syntaxFilter,
			@CheckForNull ToolCollectionFilter toolsFilter,
			@CheckForNull Map<PropertyKey, PropertyRestriction> propertyRestrictions,
			@CheckForNull Set<VisibilityRestriction> visibilityRestrictions,
			@CheckForNull EquationBehaviour equationBehaviour,
			@CheckForNull Set<DisabledAlgorithms> disabledAlgorithms,
			@CheckForNull StatisticsFilter statisticsFilter,
			@CheckForNull Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters) {
		this.examType = examType;
		this.disabledSubApps = disabledSubApps != null ? disabledSubApps : Set.of();
		this.defaultSubApp = defaultSubApp != null ? defaultSubApp : SuiteSubApp.GRAPHING;
		this.featureRestrictions = featureRestrictions != null ? featureRestrictions : Set.of();
		this.inputExpressionFilters =
				createExpressionFilters(inputExpressionFilters, operationFilter);
		this.outputExpressionFilters =
				createExpressionFilters(outputExpressionFilters, operationFilter);
		this.commandFilters = commandFilters != null ? commandFilters : Set.of();
		this.commandArgumentFilters = commandArgumentFilters != null
				? commandArgumentFilters : Set.of();
		this.operationFilter = operationFilter;
		this.contextMenuItemFilters =
				contextMenuItemFilters != null ? contextMenuItemFilters : Set.of();
		this.syntaxFilter = syntaxFilter;
		this.toolsFilter = toolsFilter != null ? toolsFilter
				: new ToolCollectionSetFilter(EuclidianConstants.MODE_IMAGE);
		this.propertyRestrictions = propertyRestrictions != null ? propertyRestrictions : Map.of();
		this.restrictedGeoElementVisibilityPropertyFilter =
				createRestrictedGeoElementVisibilityPropertyFilter(visibilityRestrictions != null
						? visibilityRestrictions : Set.of());
		this.restrictedGeoElementVisibilitySetup =
				createRestrictedGeoElementVisibilitySetup(visibilityRestrictions != null
						? visibilityRestrictions : Set.of());
		this.equationBehaviour = equationBehaviour;
		this.disabledAlgorithms = disabledAlgorithms != null ? disabledAlgorithms : Set.of();
		this.statisticsFilter = statisticsFilter;
		this.algebraOutputFormatFilters =
				algebraOutputFormatFilters != null ? algebraOutputFormatFilters : Set.of();

		assert this.commandArgumentFilters.isEmpty()
				|| this.syntaxFilter != null : "If commandArgumentFilter is specified, syntax"
				+ "filter must be present to filter syntaxes.";
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
	 * @return The set of property restrictions.
	 */
	public final @Nonnull Map<PropertyKey, PropertyRestriction> getPropertyRestrictions() {
		return propertyRestrictions;
	}

	/**
	 * Apply the exam restrictions.
	 */
	public void applyTo(
			@Nonnull ExamController.ContextDependencies dependencies,
			@CheckForNull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@CheckForNull ContextMenuFactory contextMenuFactory) {
		dependencies.algoDispatcher.addDisabledAlgorithms(disabledAlgorithms);
		for (CommandFilter commandFilter : commandFilters) {
			dependencies.commandDispatcher.addCommandFilter(commandFilter);
		}
		dependencies.commandDispatcher.addCommandArgumentFilter(examCommandArgumentFilter);
		for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
			dependencies.commandDispatcher.addCommandArgumentFilter(commandArgumentFilter);
		}
		for (ExpressionFilter expressionFilter : inputExpressionFilters) {
			dependencies.algebraProcessor.addInputExpressionFilter(expressionFilter);
		}
		for (ExpressionFilter expressionFilter : outputExpressionFilters) {
			dependencies.algebraProcessor.addOutputExpressionFilter(expressionFilter);
		}
		dependencies.algebraProcessor.reinitCommands();
		if (equationBehaviour != null) {
			originalEquationBehaviour = dependencies.algebraProcessor.getKernel()
					.getEquationBehaviour();
			dependencies.algebraProcessor.getKernel().setEquationBehaviour(equationBehaviour);
		}
		dependencies.statisticGroupsBuilder.setStatisticsFilter(statisticsFilter);
		if (syntaxFilter != null) {
			if (dependencies.autoCompleteProvider != null) {
				dependencies.autoCompleteProvider.addSyntaxFilter(syntaxFilter);
			}
			dependencies.localization.getCommandSyntax().addSyntaxFilter(syntaxFilter);
		}
		if (dependencies.autoCompleteProvider != null) {
			dependencies.autoCompleteProvider.setOperationFilter(operationFilter);
		}
		if (dependencies.propertiesRegistry != null) {
			propertyRestrictions.forEach((key, restriction) -> {
				Property property = dependencies.propertiesRegistry.lookup(key);
				if (property != null) {
					restriction.applyTo(property);
				}
			});
		}
		if (dependencies.toolsProvider != null && toolsFilter != null) {
			dependencies.toolsProvider.addToolsFilter(toolsFilter);
		}
		if (geoElementPropertiesFactory != null) {
			geoElementPropertiesFactory.addFilter(restrictedGeoElementVisibilityPropertyFilter);
			propertyRestrictions.forEach(geoElementPropertiesFactory::addRestriction);
		}
		dependencies.algebraProcessor.addGeoElementSetup(restrictedGeoElementVisibilitySetup);
		if (dependencies.scheduledPreviewFromInputBar != null) {
			dependencies.scheduledPreviewFromInputBar.addGeoElementSetup(
					restrictedGeoElementVisibilitySetup);
		}
		if (contextMenuFactory != null) {
			contextMenuItemFilters.forEach(contextMenuFactory::addFilter);
		}
		if (dependencies.settings != null) {
			if (dependencies.construction != null) {
				this.restrictedDefaults = dependencies.construction.getConstructionDefaults();
				saveSettings(dependencies.settings, restrictedDefaults);
				applySettingsRestrictions(dependencies.settings, restrictedDefaults);
				dependencies.construction.initUndoInfo();
			}
			this.restrictedSettings = dependencies.settings;
			this.algebraOutputFormatFilters.forEach(
					dependencies.settings.getAlgebra()::addAlgebraOutputFormatFilter);
		}
	}

	/**
	 * Remove the exam restrictions (i.e., undo the changes from
	 * {@link #applyTo}).
	 */
	public void removeFrom(
			@Nonnull ExamController.ContextDependencies dependencies,
			@CheckForNull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@CheckForNull ContextMenuFactory contextMenuFactory) {
		dependencies.algoDispatcher.removeDisabledAlgorithms(disabledAlgorithms);
		for (CommandFilter commandFilter : commandFilters) {
			dependencies.commandDispatcher.removeCommandFilter(commandFilter);
		}
		dependencies.commandDispatcher.removeCommandArgumentFilter(examCommandArgumentFilter);
		for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
			dependencies.commandDispatcher.removeCommandArgumentFilter(commandArgumentFilter);
		}
		for (ExpressionFilter expressionFilter : inputExpressionFilters) {
			dependencies.algebraProcessor.removeInputExpressionFilter(expressionFilter);
		}
		for (ExpressionFilter expressionFilter : outputExpressionFilters) {
			dependencies.algebraProcessor.removeOutputExpressionFilter(expressionFilter);
		}
		dependencies.algebraProcessor.reinitCommands();
		if (equationBehaviour != null) { // only restore it if we overwrote it
			dependencies.algebraProcessor.getKernel()
					.setEquationBehaviour(originalEquationBehaviour);
		}
		dependencies.statisticGroupsBuilder.setStatisticsFilter(null);
		if (syntaxFilter != null) {
			if (dependencies.autoCompleteProvider != null) {
				dependencies.autoCompleteProvider.removeSyntaxFilter(syntaxFilter);
			}
			dependencies.localization.getCommandSyntax().removeSyntaxFilter(syntaxFilter);
		}
		if (dependencies.autoCompleteProvider != null) {
			dependencies.autoCompleteProvider.setOperationFilter(null);
		}
		if (dependencies.propertiesRegistry != null) {
			propertyRestrictions.forEach((key, restriction) -> {
				Property property = dependencies.propertiesRegistry.lookup(key);
				if (property != null) {
					restriction.removeFrom(property);
				}
			});
		}
		if (dependencies.toolsProvider != null && toolsFilter != null) {
			dependencies.toolsProvider.removeToolsFilter(toolsFilter);
		}
		if (geoElementPropertiesFactory != null) {
			geoElementPropertiesFactory.removeFilter(restrictedGeoElementVisibilityPropertyFilter);
			propertyRestrictions.forEach(geoElementPropertiesFactory::removeRestriction);
		}
		dependencies.algebraProcessor.removeGeoElementSetup(restrictedGeoElementVisibilitySetup);
		if (dependencies.scheduledPreviewFromInputBar != null) {
			dependencies.scheduledPreviewFromInputBar
					.removeGeoElementSetup(restrictedGeoElementVisibilitySetup);
		}
		if (contextMenuFactory != null) {
			contextMenuItemFilters.forEach(contextMenuFactory::removeFilter);
		}
		if (dependencies.settings != null) {
			if (dependencies.construction != null) {
				removeSettingsRestrictions(dependencies.settings,
						dependencies.construction.getConstructionDefaults());
			}
			algebraOutputFormatFilters.forEach(
					dependencies.settings.getAlgebra()::removeAlgebraOutputFormatFilter);
		}
	}

	// Settings

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
			applySettingsRestrictions(restrictedSettings, restrictedDefaults);
		}
	}

	/**
	 * Apply settings changes for this exam type.
	 * @param settings {@link Settings}
	 * @apiNote Override this only if the given exam needs custom settings.
	 */
	public void applySettingsRestrictions(@Nonnull Settings settings,
			@Nonnull ConstructionDefaults defaults) {
		// empty by default
	}

	private void saveSettings(Settings settings, ConstructionDefaults defaults) {
		savedSettings = createSavedSettings();
		if (savedSettings != null) {
			savedSettings.save(settings, defaults);
		}
	}

	/**
	 * Revert changes applied in {@link #applySettingsRestrictions}, restoring the
	 * previously saved settings.
	 * @param settings {@link Settings}
	 * @apiNote An override is not needed by default.
	 */
	protected void removeSettingsRestrictions(@Nonnull Settings settings,
			ConstructionDefaults defaults) {
		if (savedSettings != null) {
			savedSettings.restore(settings, defaults);
			savedSettings = null;
			restrictedSettings = null;
		}
	}

	private static Set<ExpressionFilter> createExpressionFilters(
			@CheckForNull Set<ExpressionFilter> expressionFilters,
			@CheckForNull OperationFilter operationFilter
	) {
		HashSet<ExpressionFilter> filters = new HashSet<>();
		if (expressionFilters != null) {
			filters.addAll(expressionFilters);
		}
		if (operationFilter != null) {
			filters.add(new DeepExpressionFilter(operationFilter.toExpressionFilter()));
		}
		return filters;
	}

	private static GeoElementSetup createRestrictedGeoElementVisibilitySetup(
			Set<VisibilityRestriction> visibilityRestrictions) {
		return geoElementND -> {
			if (VisibilityRestriction.isVisibilityRestricted(geoElementND.toGeoElement(),
					visibilityRestrictions)) {
				geoElementND.toGeoElement().setRestrictedEuclidianVisibility(true);
				return true;
			}
			return false;
		};
	}

	private static GeoElementPropertyFilter createRestrictedGeoElementVisibilityPropertyFilter(
			Set<VisibilityRestriction> visibilityRestrictions) {
		return (property, geoElement) -> {
			if (property instanceof ShowObjectProperty) {
				return !VisibilityRestriction.isVisibilityRestricted(geoElement,
						visibilityRestrictions);
			}
			return true;
		};
	}
}
