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
				null,
				null);
	}

	private static Set<CommandFilter> createCommandFilters() {
		CommandNameFilter nameFilter = new CommandNameFilter(true,
				Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.ImplicitDerivative, Commands.IntegralSymbolic, Commands.IntegralBetween,
				Commands.NIntegral, Commands.Solve, Commands.SolveQuartic, Commands.SolveODE,
				Commands.SolveCubic, Commands.Solutions, Commands.NSolve, Commands.NSolveODE,
				Commands.NSolutions, Commands.CSolve, Commands.CSolutions);
		return Set.of(nameFilter);
	}

	private static OperationFilter createOperationFilter() {
		return operation -> operation != DERIVATIVE;
	}
}
