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

package org.geogebra.common.gui.toolcategorization.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.gui.toolcategorization.ToolCategory;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.ToolsetLevel;

/**
 * Generic implementation of a ToolCollection.
 */
class ToolCollectionImpl implements ToolCollection {

	private Map<ToolsetLevel, ToolsCollection> levels = new HashMap<>();
    private ToolsetLevel level;

    private static final class ToolsCollection {
        private List<ToolCategory> categories = new ArrayList<>();
        private List<List<Integer>> tools = new ArrayList<>();
    }

    void addLevel(ToolsetLevel toolsetLevel) {
        levels.put(toolsetLevel, new ToolsCollection());
        level = toolsetLevel;
    }

    void addCategory(ToolCategory category, List<Integer> tools) {
        ToolsCollection collection = levels.get(level);
        collection.categories.add(category);
        collection.tools.add(tools);
    }

    void addCategory(ToolCategory category, Integer... tools) {
        addCategory(category, Arrays.asList(tools));
    }

    void extendCategory(ToolCategory category, List<Integer> tools) {
        int categoryIndex = -1;

        ToolsetLevel previousLevel = level.getPrevious();
        if (levels.get(previousLevel) != null) {
            categoryIndex = levels.get(previousLevel).categories.indexOf(category);
        }

        if (categoryIndex < 0) {
            addCategory(category, tools);
        } else {
            List<Integer> previousTools =
                    levels.get(previousLevel).tools.get(categoryIndex);
            List<Integer> newTools = new ArrayList<>(previousTools);
            newTools.addAll(tools);
            addCategory(category, newTools);
        }
    }

    void extendCategory(ToolCategory category, Integer... tools) {
        extendCategory(category, Arrays.asList(tools));
    }

    @Override
    public List<ToolCategory> getCategories() {
        return levels.get(level).categories;
    }

    @Override
    public List<Integer> getTools(int category) {
        return levels.get(level).tools.get(category);
    }

    @Override
    public Collection<ToolsetLevel> getLevels() {
        return levels.keySet();
    }

    @Override
    public ToolsetLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(ToolsetLevel level) {
        if (!levels.containsKey(level)) {
            throw new UnsupportedOperationException("Toolset level not supported");
        }
        this.level = level;
    }

    @Override
    public void filter(ToolCollectionFilter filter) {
        for (ToolsCollection collection : levels.values()) {
            for (int i = collection.tools.size() - 1; i >= 0; i--) {
                List<Integer> tools = collection.tools.get(i);
                List<Integer> includedTools = new ArrayList<>();
                for (Integer tool : tools) {
                    if (filter.isIncluded(tool)) {
                        includedTools.add(tool);
                    }
                }
                if (includedTools.size() == 0) {
                    collection.tools.remove(i);
                    collection.categories.remove(i);
                } else {
                    collection.tools.set(i, includedTools);
                }
            }
        }
    }

    @Override
    public boolean contains(int mode) {
        for (ToolsCollection level: levels.values()) {
            for (List<Integer> tools : level.tools) {
                if (tools.contains(mode)) {
                    return true;
                }
            }
        }
        return false;
    }
}
