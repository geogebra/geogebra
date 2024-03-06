package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamRegion;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.EnglishCommandFilter;
import org.geogebra.common.kernel.commands.selector.NameCommandFilter;

final class VlaanderenExamRestrictions extends ExamRestrictions {

	VlaanderenExamRestrictions() {
		super(ExamRegion.VLAANDEREN,
				Set.of(SuiteSubApp.CAS),
				SuiteSubApp.GRAPHING,
				null,
				VlaanderenExamRestrictions.createCommandFilters(),
				null,
				null);
	}

	private static Set<CommandFilter> createCommandFilters() {
		NameCommandFilter nameFilter = new NameCommandFilter(true,
				Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.IntegralSymbolic, Commands.IntegralBetween, Commands.NIntegral,
				Commands.Solve, Commands.SolveQuartic, Commands.SolveODE, Commands.SolveCubic,
				Commands.Solutions, Commands.NSolve, Commands.NSolveODE, Commands.NSolutions);
		return Set.of(new EnglishCommandFilter(nameFilter));
	}
}
