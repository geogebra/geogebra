package org.geogebra.common.exam.restrictions.ib;

import static org.geogebra.common.kernel.commands.Commands.Integral;
import static org.geogebra.common.kernel.commands.Commands.Invert;
import static org.geogebra.common.kernel.commands.Commands.Tangent;
import static org.geogebra.common.main.syntax.Syntax.ArgumentMatcher.isNumber;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.syntax.Syntax;

public final class IBCommandArgumentFilter implements CommandArgumentFilter {
	private final Map<Commands, Set<Syntax>> allowedSyntaxesOfRestrictedCommands = Map.of(
			Integral, Set.of(
					Syntax.of(Integral, GeoElement::isGeoFunction, isNumber(), isNumber())),
			Invert, Set.of(
					Syntax.of(Invert, GeoElement::isMatrix)),
			Tangent, Set.of(
					Syntax.of(Tangent, GeoElement::isGeoPoint, GeoElement::isGeoFunction),
					Syntax.of(Tangent, isNumber(), GeoElement::isGeoFunction)));

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor) throws MyError {
		Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesOfRestrictedCommands, command, commandProcessor);
	}
}
