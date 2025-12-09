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

package org.geogebra.common.gui.toolcategorization;

import java.util.Collection;
import java.util.List;

/**
 * This object holds a set of tools which are categorized.
 * These tools are also organized in levels with the lowest level containing
 * the least amount of tools and the highest level the most amount.
 */
public interface ToolCollection {

    /**
     * The list of categories in this toolset.
     *
     * @return the list of categories, may contain null
     */
    List<ToolCategory> getCategories();

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
    Collection<ToolsetLevel> getLevels();

    /**
     * The current level of the toolset.
     *
     * @return the level of the toolset
     */
    ToolsetLevel getLevel();

    /**
     * Set the current level of the toolset.
     *
     * @param level the toolset level
     */
    void setLevel(ToolsetLevel level);

    /**
     * Filter this ToolCollection with the specified filter. Removes any tools from this
     * collection for which {@code filter.isIncluded(tool)} returns false.
     *
     * @param filter filter
     */
    void filter(ToolCollectionFilter filter);

    /**
     * Look for the given tool in this collection.
     * @param tool - tool (mode)
     * @return true if tool is part of this collection, false otherwise
     */
    boolean contains(int tool);
}
