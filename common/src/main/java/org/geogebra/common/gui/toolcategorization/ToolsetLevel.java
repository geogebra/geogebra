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
    EMPTY_CONSTRUCTION("Empty"),
    /**
     * non-empty construction
     */
    STANDARD("Standard"),
    /**
     * full list of tools
     */
    ADVANCED("Advanced");

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

    public ToolsetLevel getPrevious() {
        if (ordinal() > 0) {
            return ToolsetLevel.values()[ordinal() - 1];
        }

        return null;
    }

    public ToolsetLevel getNext() {
        if (ordinal() < values().length - 1) {
            return ToolsetLevel.values()[ordinal() + 1];
        }

        return null;
    }
}
