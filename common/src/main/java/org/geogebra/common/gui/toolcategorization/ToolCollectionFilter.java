package org.geogebra.common.gui.toolcategorization;

/**
 * Filters ToolCollections.
 */
public interface ToolCollectionFilter {

	/**
	 * Filter tools. This method should return true if the tool
	 * should be filtered in (kept) in the collection.
	 *
	 * @param tool the id of the tool
	 * @return true iff the tool should be kept
	 */
	boolean filter(int tool);
}
