package org.geogebra.common.kernel.commands.filter;

import static org.geogebra.common.kernel.commands.Commands.CopyFreeObject;
import static org.geogebra.common.kernel.commands.Commands.SetFixed;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.syntax.Syntax;

/**
 * Filters out the commands that are not enabled in the exam mode
 */
public final class ExamCommandArgumentFilter implements CommandArgumentFilter {
	private final Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
			SetFixed, Set.of(
					Syntax.of(SetFixed, this::notFunctionOrEquationFromUser,
							GeoElement::isGeoBoolean),
					Syntax.of(SetFixed, this::notFunctionOrEquationFromUser,
							GeoElement::isGeoBoolean, GeoElement::isGeoBoolean)),
			CopyFreeObject, Set.of(
					Syntax.of(CopyFreeObject, this::notEquationValue)));

	private boolean notFunctionOrEquationFromUser(GeoElement argument) {
		return !argument.isFunctionOrEquationFromUser();
	}

	private boolean notEquationValue(GeoElement argument) {
		return !(argument instanceof EquationValue);
	}

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor) {
		Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesForRestrictedCommands, command, commandProcessor);
	}
}
