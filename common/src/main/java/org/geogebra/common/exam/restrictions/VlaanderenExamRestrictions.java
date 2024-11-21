package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationExpressionFilter;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.plugin.Operation;

final class VlaanderenExamRestrictions extends ExamRestrictions {

	VlaanderenExamRestrictions() {
		super(ExamType.VLAANDEREN,
				Set.of(SuiteSubApp.CAS),
				SuiteSubApp.GRAPHING,
				null,
				createExpressionFilters(),
				createExpressionFilters(),
				createCommandFilters(),
				null,
				null,
				null,
				null,
				null,
				null);
	}

	private static Set<CommandFilter> createCommandFilters() {
		CommandNameFilter nameFilter = new CommandNameFilter(true,
				Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.ImplicitDerivative, Commands.IntegralSymbolic, Commands.IntegralBetween,
				Commands.NIntegral, Commands.Solve, Commands.SolveQuartic, Commands.SolveODE,
				Commands.SolveCubic, Commands.Solutions, Commands.NSolve, Commands.NSolveODE,
				Commands.NSolutions);
		return Set.of(nameFilter);
	}

	private static Set<ExpressionFilter> createExpressionFilters() {
		OperationExpressionFilter operationFilter = new OperationExpressionFilter(
				Operation.DERIVATIVE);
		return Set.of(operationFilter);
	}
}
