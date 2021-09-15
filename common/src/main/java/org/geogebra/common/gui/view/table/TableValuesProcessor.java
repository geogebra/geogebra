package org.geogebra.common.gui.view.table;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoList;

/**
 * Processes the input for GeoLists.
 */
public interface TableValuesProcessor {

	/**
	 * Process the input and set the list value
	 * @param input string input
	 * @param list the list that should contain the value
	 * @param index the index of the value
	 */
	public void processInput(@Nonnull String input, @Nonnull GeoList list, int index);
}
