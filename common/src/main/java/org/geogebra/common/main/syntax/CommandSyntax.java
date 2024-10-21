package org.geogebra.common.main.syntax;

/**
 * Interface to get the syntax of the commands for
 * autocompletion or help.
 *
 */
public interface CommandSyntax {

	/**
	 * @param internalCommandName the internal command name of the command.
	 * @param dim dimension of te application
	 * @return the syntax of the command.
	 */
	String getCommandSyntax(String internalCommandName, int dim);

	/**
	 * @param internalCommandName the internal command name of the CAS command.
	 * @return the syntax of the CAS command.
	 */
	String getCommandSyntaxCAS(String internalCommandName);
}
