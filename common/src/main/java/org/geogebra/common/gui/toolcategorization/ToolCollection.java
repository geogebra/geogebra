package org.geogebra.common.gui.toolcategorization;

import java.util.List;

/**
 * This object holds a set of tools which are categorized.
 * These tools are also organized in levels with the lowest level containing
 * the least amount of tools and the highest level the most amount.
 */
public interface ToolCollection {

    /**
     * The list of categories in this toolset. The list can contain null.
     *
     * @return the list of category names
     */
    List<String> getCategories();

    /**
     * The list of tools for specified category.
     *
     * @param category the index of the category
     * @return list of tools
     */
    List<Integer> getTools(int category);

    /**
     * The list of levels available.
     *
     * @return the list of levels
     */
    List<String> getLevels();

    /**
     * The current level of the toolset.
     *
     * @return the level of the toolset
     */
    int getLevel();

    /**
     * Set the current level of the toolset.
     *
     * @param level the toolset level
     */
    void setLevel(int level);

    /**
     * Filter this ToolCollection with the speicified filter.
     *
     * @param filter filter
     */
    void filter(ToolCollectionFilter filter);
}
