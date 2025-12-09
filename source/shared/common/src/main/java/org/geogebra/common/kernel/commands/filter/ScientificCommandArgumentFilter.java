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
