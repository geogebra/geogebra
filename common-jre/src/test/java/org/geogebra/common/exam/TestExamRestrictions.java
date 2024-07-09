package org.geogebra.common.exam;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.kernel.arithmetic.filter.ComplexExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.RadianExpressionFilter;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.commands.selector.EnglishCommandFilter;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.ValuedProperty;

final class TestExamRestrictions extends ExamRestrictions {

	TestExamRestrictions(ExamType examType) {
		super(examType,
				Set.of(SuiteSubApp.CAS),
				SuiteSubApp.GRAPHING,
				Set.of(ExamFeatureRestriction.DATA_TABLE_REGRESSION),
				TestExamRestrictions.createExpressionFilters(),
				TestExamRestrictions.createCommandFilters(),
				null,
				null,
				null,
				Set.of("AngleUnit"));
	}

	private static Set<CommandFilter> createCommandFilters() {
		CommandNameFilter nameFilter = new CommandNameFilter(true,
				Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.IntegralSymbolic, Commands.IntegralBetween, Commands.NIntegral,
				Commands.Solve, Commands.SolveQuartic, Commands.SolveODE, Commands.SolveCubic,
				Commands.Solutions, Commands.NSolve, Commands.NSolveODE, Commands.NSolutions);
		return Set.of(new EnglishCommandFilter(nameFilter));
	}

	private static Set<ExpressionFilter> createExpressionFilters() {
		return Set.of(
				new OperationExpressionFilter(Operation.OR, Operation.AND),
				new ComplexExpressionFilter(),
				new RadianExpressionFilter());
	}

	@Override
	protected void freezeValue(ValuedProperty property) {
		// tested in AbstractPropertyTests/AbstractValuedPropertyTests
	}

	@Override
	protected void unfreezeValue(ValuedProperty property) {
		// tested in AbstractPropertyTests/AbstractValuedPropertyTests
	}
}
