package org.geogebra.common.gui.toolcategorization;

/**
 * levels of toolset
 *
 * @author csilla
 *
 */
public enum ToolsetLevel {

    /**
     * for empty construction
     */
    EMPTY_CONSTRUCTION("Empty", 0),
    /**
     * non-empty construction
     */
    STANDARD("Standard", 1),
    /**
     * full list of tools
     */
    ADVANCED("Advanced", 2);

    private final String level;
    private final int index;

    ToolsetLevel(String level, int index) {
        this.level = level;
        this.index = index;
    }

    /**
     * @return level
     */
    public String getLevel() {
        return "ToolsetLevel." + level;
    }

    public int getIndex() {
        return index;
    }
}
