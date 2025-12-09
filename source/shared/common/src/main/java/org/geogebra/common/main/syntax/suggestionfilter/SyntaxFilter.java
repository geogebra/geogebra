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

package org.geogebra.common.main.syntax.suggestionfilter;

/**
 * Filters the disallowed syntaxes of the restricted commands.
 * (Restricted commands are the ones that have some of their arguments filtered by the
 * CommandArgumentFilter.)
 */
public interface SyntaxFilter {

	/**
	 * @param internalCommandName internal command name
	 * @param syntax multiple command syntaxes separated by '\n'
	 * @return the syntax parameter string without the lines of the disallowed argument lists
	 */
	String getFilteredSyntax(String internalCommandName, String syntax);
}
