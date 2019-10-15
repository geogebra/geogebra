package org.geogebra.common.gui.toolcategorization;

/**
 * levels of toolset
 *
 * @author csilla
 *
 */
public enum ToolsetLevel {

    /**
     * full list of tools
     */
    ADVANCED("Advanced"),
    /**
     * non-empty construction
     */
    STANDARD("Standard"),
    /**
     * for empty construction
     */
    EMPTY_CONSTRUCTION("Empty");

    private final String level;

    ToolsetLevel(String level) {
        this.level = level;
    }

    /**
     * @return level
     */
    public String getLevel() {
        return "ToolsetLevel." + level;
    }
}
