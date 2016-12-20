package org.geogebra.common.kernel.algos;

/**
 * Common interface for Commands and Algos
 */
public interface GetCommand {
	/**
	 * @return internal command name (may be "Expression")
	 */
	public String getCommand();
}
