package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationExpressionFilter;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.EnglishCommandFilter;
import org.geogebra.common.kernel.commands.selector.NameCommandFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;

final class VlaanderenExamRestrictions extends ExamRestrictions {

	VlaanderenExamRestrictions() {
		super(ExamType.VLAANDEREN,
				Set.of(SuiteSubApp.CAS),
				SuiteSubApp.GRAPHING,
				VlaanderenExamRestrictions.createExpressionFilter(),
				VlaanderenExamRestrictions.createCommandFilter(),
				null,
				Set.of("AngleUnit"));
	}

	// replaces exam-related method in CommandFilterFactory
	private static CommandFilter createCommandFilter() {
		NameCommandFilter nameFilter = new NameCommandFilter(true,
				Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.IntegralSymbolic, Commands.IntegralBetween, Commands.NIntegral,
				Commands.Solve, Commands.SolveQuartic, Commands.SolveODE, Commands.SolveCubic,
				Commands.Solutions, Commands.NSolve, Commands.NSolveODE, Commands.NSolutions);
		return new EnglishCommandFilter(nameFilter);
	}

	private static ExpressionFilter createExpressionFilter() {
		return new OperationExpressionFilter(Operation.OR, Operation.AND);
	}

	@Override
	protected void freezeValue(ValuedProperty property) {
		if (property instanceof AngleUnitProperty) {
		}
	}

	@Override
	protected void unfreezeValue(ValuedProperty property) {
		if (property instanceof AngleUnitProperty) {
		}
	}

	@Override
	public boolean isSelectionAllowed(GeoElementND geoND) {
		if (geoND.isFunctionOrEquationFromUser()) {
			return
		}
			return true;
	}
}
