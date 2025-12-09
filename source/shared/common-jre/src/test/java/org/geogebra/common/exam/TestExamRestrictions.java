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

import static org.geogebra.common.contextmenu.InputContextMenuItem.Expression;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POINT;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;
import static org.geogebra.common.plugin.Operation.AND;
import static org.geogebra.common.plugin.Operation.OR;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.exam.restrictions.PropertyRestriction;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.DisabledAlgorithms;
import org.geogebra.common.kernel.arithmetic.filter.ComplexExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.arithmetic.filter.RadianGradianFilter;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;

final class TestExamRestrictions extends ExamRestrictions {

	int appliedCount = 0;

	TestExamRestrictions(ExamType examType) {
		super(examType,
				Set.of(SuiteSubApp.CAS),
				SuiteSubApp.GRAPHING,
				Set.of(ExamFeatureRestriction.HIDE_SPECIAL_POINTS),
				createExpressionFilters(),
				null,
				createCommandFilters(),
				createCommandArgumentFilter(),
				createOperationFilter(),
				createContextMenuItemFilters(),
				createSyntaxFilter(),
				createToolCollectionFilter(),
				createPropertyRestrictions(),
				createVisibilityRestrictions(),
				null,
				createDisabledAlgorithms(),
				null,
				null);
	}

	@Override
	public void applyTo(@Nonnull ExamController.ContextDependencies dependencies,
			@CheckForNull PropertiesRegistry propertiesRegistry,
			@CheckForNull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@CheckForNull ContextMenuFactory contextMenuFactory) {
		super.applyTo(dependencies, propertiesRegistry, geoElementPropertiesFactory,
				contextMenuFactory);
		appliedCount++;
	}

	@Override
	public void removeFrom(@Nonnull ExamController.ContextDependencies dependencies,
			@CheckForNull PropertiesRegistry propertiesRegistry,
			@CheckForNull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@CheckForNull ContextMenuFactory contextMenuFactory) {
		super.removeFrom(dependencies, propertiesRegistry, geoElementPropertiesFactory,
				contextMenuFactory);
		appliedCount--;
	}

	private static Set<CommandFilter> createCommandFilters() {
		CommandNameFilter nameFilter = new CommandNameFilter(true,
				Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.IntegralSymbolic, Commands.IntegralBetween, Commands.NIntegral,
				Commands.Solve, Commands.SolveQuartic, Commands.SolveODE, Commands.SolveCubic,
				Commands.Solutions, Commands.NSolve, Commands.NSolveODE, Commands.NSolutions);
		return Set.of(nameFilter);
	}

	private static Set<ExpressionFilter> createExpressionFilters() {
		return Set.of(new ComplexExpressionFilter(),
				new RadianGradianFilter());
	}

	private static ToolCollectionFilter createToolCollectionFilter() {
		return new ToolCollectionSetFilter(MODE_POINT);
	}

	private static Set<ContextMenuItemFilter> createContextMenuItemFilters() {
		return Set.of(item -> !item.equals(Expression));
	}

	private static SyntaxFilter createSyntaxFilter() {
		LineSelectorSyntaxFilter filter = new LineSelectorSyntaxFilter();
		// Max [ <Function>, <Start x-Value>, <End x-Value> ]
		filter.addSelector(Commands.Max, 4);
		// Filter NDerivative
		filter.addSelector(Commands.NDerivative, 1);
		filter.addSelector(Commands.NDerivative, 2);
		return filter;
	}

	private static Set<CommandArgumentFilter> createCommandArgumentFilter() {
		return Set.of((command, commandProcessor) -> {
			if (command.getName().equals(Commands.Max.name())) {
				if (command.getArgumentNumber() != 3) {
					throw commandProcessor.argNumErr(command, command.getArgumentNumber());
				}
			}
		});
	}

	private static OperationFilter createOperationFilter() {
		Set<Operation> restrictedOperations = Set.of(OR, AND);
		return operation -> !restrictedOperations.contains(operation);
	}

	private static Map<String, PropertyRestriction> createPropertyRestrictions() {
		return Map.of("AngleUnit", new PropertyRestriction(true, value ->
				value != Integer.valueOf(Kernel.ANGLE_DEGREES_MINUTES_SECONDS)));
	}

	static Set<VisibilityRestriction> createVisibilityRestrictions() {
		return Set.of(geoElement -> geoElement.isGeoPoint() ? HIDE : IGNORE,
				geoElement -> geoElement.isInequality() ? HIDE : IGNORE);
	}

	private static Set<DisabledAlgorithms> createDisabledAlgorithms() {
		return Set.of(DisabledAlgorithms.TangentPointConic,
				DisabledAlgorithms.TangentLineConic);
	}
}
