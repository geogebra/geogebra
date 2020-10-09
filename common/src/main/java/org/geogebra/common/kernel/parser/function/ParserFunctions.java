package org.geogebra.common.kernel.parser.function;

import java.util.ArrayList;

import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

/**
 * Handles function references for Parser.
 * 
 * @author zbynek
 */
public interface ParserFunctions {

	/**
	 * Updates local names of functions
	 * 
	 * @param loc
	 *            localization
	 */
	void updateLocale(Localization loc);

	/**
	 * @param name
	 *            function name
	 * @param size
	 *            number of arguments
	 * @return operation
	 */
	Operation get(String name, int size);

	/**
	 * Some names cannot be used for elements because of collision with
	 * predefined functions these should also be documented here:
	 * http://wiki.geogebra.org/en/Manual:Naming_Objects
	 * 
	 * @param name
	 *            label
	 * @return true if label is reserved
	 */
	boolean isReserved(String name);

	/**
	 * Find completions for a given prefix (Arnaud 03/10/2011)
	 * 
	 * @param prefix
	 *            the wanted prefix
	 * @return all the built-in functions starting with this prefix (with
	 *         brackets at the end)
	 */
	ArrayList<String> getCompletions(String prefix);

	/**
	 * @param localization
	 *            localization
	 * @param string
	 *            translated function
	 * @return English function name
	 */
	String getInternal(Localization localization, String string);

	/**
	 * @param string
	 *            english function name
	 * @return whether this is a translatable function
	 */
	boolean isTranslatableFunction(String string);

	/**
	 * @param inverseTrig
	 *            whether inverse trig functions should be replaced by deg
	 *            variants
	 */
	void setInverseTrig(boolean inverseTrig);

	/**
	 * @param leftImg function name
	 * @return single argument operation other than x,y,z
	 */
	Operation getSingleArgumentOp(String leftImg);

	/**
	 * @param text autocomplete suggestion
	 * @param loc localization
	 * @return suggestion updated for the editor
	 */
	String toEditorAutocomplete(String text, Localization loc);
}
