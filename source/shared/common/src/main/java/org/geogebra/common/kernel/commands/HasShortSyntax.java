package org.geogebra.common.kernel.commands;

/**
 * Interface for commands that can be represented as expressions
 * 
 * @author Zbynek
 *
 */
public interface HasShortSyntax {
	/**
	 * @param b
	 *            whether to serialize this as an expression
	 */
	public void setShortSyntax(boolean b);
}
