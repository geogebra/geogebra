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

    /**
     * @return the previous ToolsetLevel, as defined by the enum ordinal,
     * null, if it is the first one
     */
    public ToolsetLevel getPrevious() {
        if (ordinal() > 0) {
            return ToolsetLevel.values()[ordinal() - 1];
        }

        return null;
    }

    /**
     * @return the next ToolsetLevel, as defined by the enum ordinal,
     * null, if it is the last one
     */
    public ToolsetLevel getNext() {
        if (ordinal() < values().length - 1) {
            return ToolsetLevel.values()[ordinal() + 1];
        }

        return null;
    }
}
