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

package org.geogebra.common.restrictions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.restrictions.RestorableSettings;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.gui.view.table.dialog.StatisticsFilter;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.ScheduledPreviewFromInputBar;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.DisabledAlgorithms;
import org.geogebra.common.kernel.arithmetic.filter.DeepExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.GeoElementSetup;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Base class for all restrictions.
 */
public class Restrictions {

	private final @NonNull Set<SuiteSubApp> disabledSubApps;
	private final @NonNull SuiteSubApp defaultSubApp;
	private final @NonNull Set<FeatureRestriction> featureRestrictions;
	private final @NonNull Set<ExpressionFilter> inputExpressionFilters;
	private final @NonNull Set<ExpressionFilter> outputExpressionFilters;
	private final @NonNull Set<CommandFilter> commandFilters;
	private final @NonNull Set<CommandArgumentFilter> commandArgumentFilters;
	private final @Nullable OperationFilter operationFilter;
	private final @NonNull Set<ContextMenuItemFilter> contextMenuItemFilters;
	private final @Nullable SyntaxFilter syntaxFilter;
	private final @NonNull ToolCollectionFilter toolsFilter;
	private final @NonNull Map<PropertyKey, PropertyRestriction> propertyRestrictions;
	private final @NonNull GeoElementPropertyFilter restrictedGeoElementVisibilityPropertyFilter;
	private final @NonNull GeoElementSetup restrictedGeoElementVisibilitySetup;
	private final @Nullable EquationBehaviour equationBehaviour;
	private @Nullable EquationBehaviour originalEquationBehaviour;
	private final @NonNull Set<DisabledAlgorithms> disabledAlgorithms;
	private final @Nullable StatisticsFilter statisticsFilter;
	private final @NonNull Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters;
	private final @Nullable AlgebraOutputFilter algebraOutputFilter;
	private @Nullable RestorableSettings savedSettings;
	private @Nullable Settings restrictedSettings = null;
	private @Nullable ConstructionDefaults restrictedDefaults;

	/**
	 * Prevent instantiation, except by subclasses.
	 * @param disabledSubApps An optional set of disabled subapps for this restriction. Passing in
	 * null is equivalent to passing in an empty set.
	 * @param defaultSubApp An optional subapp to activate at the start of an exam, if the
	 * current subapp is in the list of restricted subapps. If null, Graphing will be used as the
	 * default subapp.
	 * @param featureRestrictions An optional set of features to disable.
	 * @param inputExpressionFilters An optional set of expression filters (e.g., ||) to apply
	 * to the algebra inputs.
	 * @param outputExpressionFilters An optional set of expression filters (e.g., ||) to apply
	 * to the algebra outputs.
	 * @param commandFilters An optional command filter to apply.
	 * @param commandArgumentFilters An optional command argument filter to applies.
	 * @param operationFilter An optional operation filter to apply.
	 * @param syntaxFilter An optional syntax filter to apply.
	 * @param toolsFilter An optional filter for tools that should be unavailable.
	 * If this argument is null, the Image tool will still be filtered out (APPS-5214). When
	 * providing a non-null filter here, it should include the Image tool.
	 * @param propertyRestrictions An optional map of properties and restrictions
	 * to be applied to them.
	 * @param statisticsFilter A statistic filter to be applied.
	 */
	protected Restrictions(
			@Nullable Set<SuiteSubApp> disabledSubApps,
			@Nullable SuiteSubApp defaultSubApp,
			@Nullable Set<FeatureRestriction> featureRestrictions,
			@Nullable Set<ExpressionFilter> inputExpressionFilters,
			@Nullable Set<ExpressionFilter> outputExpressionFilters,
			@Nullable Set<CommandFilter> commandFilters,
			@Nullable Set<CommandArgumentFilter> commandArgumentFilters,
			@Nullable OperationFilter operationFilter,
			@Nullable Set<ContextMenuItemFilter> contextMenuItemFilters,
			@Nullable SyntaxFilter syntaxFilter,
			@Nullable ToolCollectionFilter toolsFilter,
			@Nullable Map<PropertyKey, PropertyRestriction> propertyRestrictions,
			@Nullable Set<VisibilityRestriction> visibilityRestrictions,
			@Nullable EquationBehaviour equationBehaviour,
			@Nullable Set<DisabledAlgorithms> disabledAlgorithms,
			@Nullable StatisticsFilter statisticsFilter,
			@Nullable Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters,
			@Nullable AlgebraOutputFilter algebraOutputFilter) {
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
		this.algebraOutputFilter = algebraOutputFilter;
	}

	/**
	 * @return The list of disabled (i.e., not allowed) subapps, or an empty set
	 * if there is no restriction on the available subapps.
	 */
	public final @NonNull Set<SuiteSubApp> getDisabledSubApps() {
		return disabledSubApps;
	}

	/**
	 * @return The default subapp to switch to if a disabled subapp is active when
	 * the exam starts.
	 */
	public final @NonNull SuiteSubApp getDefaultSubApp() {
		return defaultSubApp;
	}

	/**
	 * @return The set of disabled features, or an empty set if there's no
	 * restrictions on available features in the apps.
	 */
	public final @NonNull Set<FeatureRestriction> getFeatureRestrictions() {
		return featureRestrictions;
	}

	/**
	 * @return The set of property restrictions.
	 */
	public final @NonNull Map<PropertyKey, PropertyRestriction> getPropertyRestrictions() {
		return propertyRestrictions;
	}

	/**
	 * @return The context menu items filters (may be empty).
	 */
	public final @NonNull Set<ContextMenuItemFilter> getContextMenuItemFilters() {
		return contextMenuItemFilters;
	}

	/**
	 * Apply the restrictions.
	 */
	public void applyTo(@NonNull ContextDependencies cd) {
		cd.algoDispatcher.addDisabledAlgorithms(disabledAlgorithms);
		for (CommandFilter commandFilter : commandFilters) {
			cd.commandDispatcher.addCommandFilter(commandFilter);
		}
		for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
			cd.commandDispatcher.addCommandArgumentFilter(commandArgumentFilter);
		}
		for (ExpressionFilter expressionFilter : inputExpressionFilters) {
			cd.algebraProcessor.addInputExpressionFilter(expressionFilter);
		}
		for (ExpressionFilter expressionFilter : outputExpressionFilters) {
			cd.algebraProcessor.addOutputExpressionFilter(expressionFilter);
		}
		cd.algebraProcessor.reinitCommands();
		if (equationBehaviour != null) {
			originalEquationBehaviour = cd.algebraProcessor.getKernel().getEquationBehaviour();
			cd.algebraProcessor.getKernel().setEquationBehaviour(equationBehaviour);
		}
		if (cd.statisticGroupsBuilder != null) {
			cd.statisticGroupsBuilder.setStatisticsFilter(statisticsFilter);
		}
		if (syntaxFilter != null) {
			if (cd.autoCompleteProvider != null) {
				cd.autoCompleteProvider.addSyntaxFilter(syntaxFilter);
			}
			cd.localization.getCommandSyntax().addSyntaxFilter(syntaxFilter);
		}
		if (cd.autoCompleteProvider != null) {
			cd.autoCompleteProvider.setOperationFilter(operationFilter);
		}
		if (cd.propertiesRegistry != null) {
			propertyRestrictions.forEach((key, restriction) -> {
				Property property = cd.propertiesRegistry.lookup(key);
				if (property != null) {
					restriction.applyTo(property);
				}
			});
		}
		if (cd.toolsProvider != null && toolsFilter != null) {
			cd.toolsProvider.addToolsFilter(toolsFilter);
		}
		if (cd.geoElementPropertiesFactory != null) {
			cd.geoElementPropertiesFactory.addFilter(restrictedGeoElementVisibilityPropertyFilter);
			propertyRestrictions.forEach(cd.geoElementPropertiesFactory::addRestriction);
		}
		cd.algebraProcessor.addGeoElementSetup(restrictedGeoElementVisibilitySetup);
		if (cd.scheduledPreviewFromInputBar != null) {
			cd.scheduledPreviewFromInputBar.addGeoElementSetup(restrictedGeoElementVisibilitySetup);
		}
		if (cd.settings != null) {
			if (cd.construction != null) {
				this.restrictedDefaults = cd.construction.getConstructionDefaults();
				saveSettings(cd.settings, restrictedDefaults);
				applySettingsRestrictions(cd.settings, restrictedDefaults);
				cd.construction.initUndoInfo();
			}
			this.restrictedSettings = cd.settings;
			this.algebraOutputFormatFilters.forEach(
					cd.settings.getAlgebra()::addAlgebraOutputFormatFilter);
		}
		if (cd.algebraOutputFiltering != null) {
			cd.algebraOutputFiltering.setAlgebraOutputFilter(wrapAlgebraOutputFilter(
					cd.algebraOutputFiltering.createBaseAlgebraOutputFilter()));
		}
	}

	/**
	 * Remove the restrictions (i.e., undo the changes from {@link #applyTo}).
	 */
	public void removeFrom(@NonNull ContextDependencies cd) {
		cd.algoDispatcher.removeDisabledAlgorithms(disabledAlgorithms);
		for (CommandFilter commandFilter : commandFilters) {
			cd.commandDispatcher.removeCommandFilter(commandFilter);
		}
		for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
			cd.commandDispatcher.removeCommandArgumentFilter(commandArgumentFilter);
		}
		for (ExpressionFilter expressionFilter : inputExpressionFilters) {
			cd.algebraProcessor.removeInputExpressionFilter(expressionFilter);
		}
		for (ExpressionFilter expressionFilter : outputExpressionFilters) {
			cd.algebraProcessor.removeOutputExpressionFilter(expressionFilter);
		}
		cd.algebraProcessor.reinitCommands();
		if (equationBehaviour != null) { // only restore it if we overwrote it
			cd.algebraProcessor.getKernel().setEquationBehaviour(originalEquationBehaviour);
		}
		if (cd.statisticGroupsBuilder != null) {
			cd.statisticGroupsBuilder.setStatisticsFilter(null);
		}
		if (syntaxFilter != null) {
			if (cd.autoCompleteProvider != null) {
				cd.autoCompleteProvider.removeSyntaxFilter(syntaxFilter);
			}
			cd.localization.getCommandSyntax().removeSyntaxFilter(syntaxFilter);
		}
		if (cd.autoCompleteProvider != null) {
			cd.autoCompleteProvider.setOperationFilter(null);
		}
		if (cd.propertiesRegistry != null) {
			propertyRestrictions.forEach((key, restriction) -> {
				Property property = cd.propertiesRegistry.lookup(key);
				if (property != null) {
					restriction.removeFrom(property);
				}
			});
		}
		if (cd.toolsProvider != null && toolsFilter != null) {
			cd.toolsProvider.removeToolsFilter(toolsFilter);
		}
		if (cd.geoElementPropertiesFactory != null) {
			cd.geoElementPropertiesFactory.removeFilter(
					restrictedGeoElementVisibilityPropertyFilter);
			propertyRestrictions.forEach(cd.geoElementPropertiesFactory::removeRestriction);
		}
		cd.algebraProcessor.removeGeoElementSetup(restrictedGeoElementVisibilitySetup);
		if (cd.scheduledPreviewFromInputBar != null) {
			cd.scheduledPreviewFromInputBar.removeGeoElementSetup(
					restrictedGeoElementVisibilitySetup);
		}
		if (cd.settings != null) {
			if (cd.construction != null) {
				removeSettingsRestrictions(cd.settings, cd.construction.getConstructionDefaults());
			}
			algebraOutputFormatFilters.forEach(
					cd.settings.getAlgebra()::removeAlgebraOutputFormatFilter);
		}
		if (cd.algebraOutputFiltering != null) {
			cd.algebraOutputFiltering.setAlgebraOutputFilter(
					cd.algebraOutputFiltering.createBaseAlgebraOutputFilter());
		}
	}

	// Settings

	// TODO refactor settings handling

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
	public void applySettingsRestrictions(Settings settings, ConstructionDefaults defaults) {
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
	protected void removeSettingsRestrictions(Settings settings, ConstructionDefaults defaults) {
		if (savedSettings != null) {
			savedSettings.restore(settings, defaults);
			savedSettings = null;
			restrictedSettings = null;
		}
	}

	// Helpers

	private static Set<ExpressionFilter> createExpressionFilters(
			@Nullable Set<ExpressionFilter> expressionFilters,
			@Nullable OperationFilter operationFilter
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

	private @NonNull AlgebraOutputFilter wrapAlgebraOutputFilter(@NonNull AlgebraOutputFilter base) {
		if (algebraOutputFilter == null) {
			return base;
		}
		return element -> algebraOutputFilter.isAllowed(element) && base.isAllowed(element);
	}

	public record ContextDependencies(
		@NonNull AlgoDispatcher algoDispatcher,
		@NonNull CommandDispatcher commandDispatcher,
		@NonNull AlgebraProcessor algebraProcessor,
		@NonNull PropertiesRegistry propertiesRegistry,
		@NonNull Localization localization,
		@NonNull Settings settings,
		@Nullable StatisticGroupsBuilder statisticGroupsBuilder,
		@Nullable AutocompleteProvider autoCompleteProvider,
		@Nullable ToolsProvider toolsProvider,
		@Nullable ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
		@Nullable Construction construction,
		@Nullable GeoElementPropertiesFactory geoElementPropertiesFactory,
		@Nullable AlgebraOutputFiltering algebraOutputFiltering) { }
}
