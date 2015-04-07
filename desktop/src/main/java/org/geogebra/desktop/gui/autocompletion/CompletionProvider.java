package org.geogebra.desktop.gui.autocompletion;

import java.util.List;

/**
 * Interface for specifying a custom completion provider.
 * 
 * @param <T>
 *            The type of the completions options (most of the time this will be
 *            simple strings)
 */
public interface CompletionProvider<T> {

	/**
	 * This method is called repeatedly while the user is typing. It should
	 * return a list of suitable completion options for the prefix.
	 * 
	 * @param prefix
	 *            The user input with a minimal length of 1
	 * @return A List of matching completion options (inclusive prefix)
	 */
	List<T> getCompletionOptions(String prefix);

	/**
	 * Converts a completion option to a string which can be displayed.
	 * 
	 * @param option
	 * @return The string which will be displayed in the options popup
	 */
	String toString(T option);

}