package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;
import static org.geogebra.common.plugin.Operation.DERIVATIVE;

import java.util.Set;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

final class VlaanderenExamRestrictions extends ExamRestrictions {

	VlaanderenExamRestrictions() {
		super(ExamType.VLAANDEREN,
				Set.of(CAS, SCIENTIFIC),
				GRAPHING,
				null,
				null,
				null,
				createCommandFilters(),
				null,
				createOperationFilter(),
				null,
				null,
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

	private static OperationFilter createOperationFilter() {
		return operation -> operation != DERIVATIVE;
	}
}
