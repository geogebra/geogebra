package org.geogebra.common.kernel.commands.filter;

import static org.geogebra.common.kernel.commands.Commands.BinomialDist;
import static org.geogebra.common.kernel.commands.Commands.Normal;
import static org.geogebra.common.main.syntax.Syntax.ArgumentMatcher.isNumber;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.syntax.Syntax;

public final class ScientificCommandArgumentFilter implements CommandArgumentFilter {
	private final Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
			BinomialDist, Set.of(
					Syntax.of(BinomialDist, isNumber(), isNumber(), GeoElement::isGeoBoolean),
					Syntax.of(BinomialDist, isNumber(), isNumber(), isNumber(),
							GeoElement::isGeoBoolean),
					Syntax.of(BinomialDist, isNumber(), isNumber(), GeoElement::isGeoList)),
			Normal, Set.of(
					Syntax.of(Normal, isNumber(), isNumber(), isNumber()),
					Syntax.of(Normal, isNumber(), isNumber(), isNumber(), GeoElement::isGeoBoolean),
					Syntax.of(Normal, isNumber(), isNumber(), isNumber(), isNumber())));

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor) {
		Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesForRestrictedCommands, command, commandProcessor);
	}
}
