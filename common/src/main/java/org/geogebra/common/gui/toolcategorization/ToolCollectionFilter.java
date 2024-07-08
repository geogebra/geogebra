package org.geogebra.common.gui.toolcategorization;

/**
 * Filters ToolCollections.
 */
public interface ToolCollectionFilter {

	/**
	 * Filter tools by ID.
	 *
	 * @param tool the id of the tool (aka mode). See the constants in EuclidianConstants
	 * starting with "MODE_" for a list of valid values.
	 * @return true if the tool should be included.
	 */
	boolean isIncluded(int tool);
}
