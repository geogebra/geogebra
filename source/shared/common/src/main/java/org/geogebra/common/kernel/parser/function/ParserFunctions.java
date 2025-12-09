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

package org.geogebra.common.kernel.parser.function;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

/**
 * Handles function references for Parser.
 * 
 * @author zbynek
 */
public interface ParserFunctions {
	String NROOT_SUFFIX = "( <x>, <n> )";
	String COMBINATORIAL_SUFFIX = "( <n>, <r> )";

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
	 * Find completions for a given prefix
	 *
	 * @param prefix
	 *            the prefix to match function syntaxes against
	 * @return all the built-in functions starting with this prefix (with
	 *         brackets at the end)
	 */
	ArrayList<String> getCompletions(String prefix);

	/**
	 * Find completions for a given prefix
	 * 
	 * @param prefix
	 *            the prefix to match function syntaxes against
	 * @param operationFilter
	 *            an optional filter for operations that should be filtered out (suppressed)
	 * @return all the built-in functions starting with this prefix (with
	 *         brackets at the end)
	 */
	ArrayList<String> getCompletions(String prefix, @CheckForNull OperationFilter operationFilter);

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
