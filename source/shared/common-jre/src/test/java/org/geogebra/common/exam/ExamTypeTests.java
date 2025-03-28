package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;

public class ExamTypeTests {

	@ParameterizedTest
	@EnumSource(ExamType.class)
	public void solveAvailabilityInExams(ExamType t) {
		ExamRestrictions examRestrictions = ExamRestrictions.forExamType(t);
		AppCommon helpApp = AppCommonFactory.create3D();
		CommandDispatcher commandDispatcher1 =
				helpApp.getKernel().getAlgebraProcessor().getCommandDispatcher();
		examRestrictions.applyTo(helpApp.getKernel().getAlgoDispatcher(),
				commandDispatcher1,
				helpApp.getKernel().getAlgebraProcessor(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
		Set<Commands> all = Set.of(Commands.CSolve, Commands.CSolutions,
				Commands.NSolve, Commands.NSolutions,
				Commands.Solve, Commands.Solutions);
		assertEquals(expectedCommands(t),
				all.stream().filter(commandDispatcher1::isAllowedByCommandFilters)
						.collect(Collectors.toSet()));
	}

	private static Set<Commands> expectedCommands(ExamType t) {
		switch (t) {
		case GENERIC:
		case IB:
		case BAYERN_CAS:
		case MMS: return Set.of(
				Commands.CSolve, Commands.CSolutions,
				Commands.NSolve, Commands.NSolutions,
				Commands.Solve, Commands.Solutions);
		case REALSCHULE:
		case NIEDERSACHSEN: return Set.of(
				Commands.NSolve, Commands.NSolutions,
				Commands.Solve, Commands.Solutions);
		default:
			return Set.of();
		}
	}
}
