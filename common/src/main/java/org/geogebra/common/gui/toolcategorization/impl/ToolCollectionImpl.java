package org.geogebra.common.gui.toolcategorization.impl;

import org.geogebra.common.gui.toolcategorization.ToolCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generic implementation of a ToolCollection.
 */
class ToolCollectionImpl implements ToolCollection {

    private static class ToolsCollection {
        private List<String> categories = new ArrayList<>();
        private List<List<Integer>> tools = new ArrayList<>();
    }

    private int level = -1;
    private List<ToolsCollection> collections = new ArrayList<>();
    private List<String> levels = new ArrayList<>();

    void addLevel(String levelName) {
        levels.add(levelName);
        collections.add(new ToolsCollection());
        level += 1;
    }

    void addCategory(String category, List<Integer> tools) {
        ToolsCollection collection = collections.get(level);
        collection.categories.add(category);
        collection.tools.add(tools);
    }

    void addCategory(String category, Integer... tools) {
        addCategory(category, Arrays.asList(tools));
    }

    void extendCategory(String category, List<Integer> tools) {
        if (level == 0) {
            addCategory(category, tools);
        } else {
            int previousLevel = level - 1;
            int categoryIndex = collections.get(previousLevel).categories.indexOf(category);
            if (categoryIndex < 0) {
                addCategory(category, tools);
            } else {
                List<Integer> previousTools = collections.get(previousLevel).tools.get(categoryIndex);
                List<Integer> newTools = new ArrayList<>(previousTools);
                newTools.addAll(tools);
                addCategory(category, newTools);
            }
        }
    }

    void extendCategory(String category, Integer... tools) {
        extendCategory(category, Arrays.asList(tools));
    }

    @Override
    public List<String> getCategories() {
        return collections.get(level).categories;
    }

    @Override
    public List<Integer> getTools(int category) {
        return collections.get(level).tools.get(category);
    }

    @Override
    public List<String> getLevels() {
        return levels;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        if (level < 0 || level >= levels.size()) {
            throw new UnsupportedOperationException("Level size not in range");
        }
        this.level = level;
    }
}
