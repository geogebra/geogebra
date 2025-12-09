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

package org.geogebra.common.exam.restrictions.cvte;

import static org.geogebra.common.kernel.commands.Commands.Circle;
import static org.geogebra.common.kernel.commands.Commands.Extremum;
import static org.geogebra.common.kernel.commands.Commands.Intersect;
import static org.geogebra.common.kernel.commands.Commands.Root;
import static org.geogebra.common.main.syntax.Syntax.ArgumentMatcher.isNumber;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.exam.restrictions.CvteExamRestrictions;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.syntax.Syntax;

public final class CvteCommandArgumentFilter implements CommandArgumentFilter {
	private final Set<VisibilityRestriction> visibilityRestrictions =
			CvteExamRestrictions.createVisibilityRestrictions();
	private final Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
			Circle, Set.of(
					Syntax.of(Circle, GeoElement::isGeoPoint, isNumber())),
			Extremum, Set.of(
					Syntax.of(Extremum, GeoElement::isGeoFunction, isNumber(), isNumber())),
			Root, Set.of(
					Syntax.of(Root, GeoElement::isGeoFunction, isNumber(), isNumber())),
			Intersect, Set.of(
					Syntax.of(Intersect, isElementWithUnrestrictedVisibility(),
							isElementWithUnrestrictedVisibility()),
					Syntax.of(Intersect, isElementWithUnrestrictedVisibility(),
							isElementWithUnrestrictedVisibility(), any()),
					Syntax.of(Intersect, isElementWithUnrestrictedVisibility(),
							isElementWithUnrestrictedVisibility(), any(), any())));

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor)
			throws MyError {
		Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesForRestrictedCommands, command, commandProcessor);
	}

	private Syntax.ArgumentMatcher isElementWithUnrestrictedVisibility() {
		return argument -> !VisibilityRestriction.isVisibilityRestricted(argument,
				visibilityRestrictions);
	}

	private Syntax.ArgumentMatcher any() {
		return argument -> true;
	}
}