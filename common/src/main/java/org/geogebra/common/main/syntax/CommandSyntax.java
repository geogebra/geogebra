package org.geogebra.common.main.syntax;

/**
 * Interface to get the syntax of the commands for
 * autocompletion or help.
 *
 */
public interface CommandSyntax {

	/**
	 *
	 * @param key
	 * 				the internal key of the command.
	 * @param dim
	 * 				dimension of te application
	 * @return
	 * 				the syntax of the command.
	 */
	String getCommandSyntax(String key, int dim);

	/**
	 *
	 * @param key
	 * 				the internal key of the CAS command.
	 * @return
	 * 				the syntax of the CAS command.
	 */
	String getCommandSyntaxCAS(String key);
}
