package org.geogebra.common.gui.toolcategorization;

import javax.annotation.Nonnull;

/**
 * An abstraction for retrieving and filtering available tools.
 */
public interface ToolsProvider {

	/**
	 * @return The currently available tools. Note that the set of available tools can change,
	 * e.g. during exams, or when a file with a custom tool collection is loaded.
	 */
	@Nonnull ToolCollection getAvailableTools();

	/**
	 * Add a (temporary or permanent) tools filter.
	 * @param filter A tools filter.
	 */
	void addToolsFilter(@Nonnull ToolCollectionFilter filter);

	/**
	 * Remove a previously added tools filter.
	 * @param filter A tools filter.
	 */
	void removeToolsFilter(@Nonnull ToolCollectionFilter filter);
}
