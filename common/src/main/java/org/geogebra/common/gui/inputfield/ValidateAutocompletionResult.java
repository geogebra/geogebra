package org.geogebra.common.gui.inputfield;

/**
 * @author gabor
 * 
 *         Just a wrapper class to have validateAutocomletion work both in
 *         Desktop and Web
 *
 */
public class ValidateAutocompletionResult {
	/**
	 * Character Position in TextField
	 */
	public int carPos;
	/**
	 * StringBuilder result
	 */
	public String sb;
	/**
	 * Return value of the function
	 */
	public boolean returnval = true;
}
