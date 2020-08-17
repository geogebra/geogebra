package org.geogebra.common.main.syntax.suggestionfilter;

/**
 * Filters the disallowed syntaxes of the restricted commands.
 * (Restricted commands are the ones that have some of their arguments filtered by the
 * CommandArgumentFilter.)
 */
public interface SyntaxFilter {

	/**
	 * @param commandName command name
	 * @param syntax multiple command syntaxes separated by '\n'
	 * @return the syntax parameter string without the lines of the disallowed argument lists
	 */
	String getFilteredSyntax(String commandName, String syntax);
}
