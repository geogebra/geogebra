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

import org.geogebra.common.main.Localization;

/**
 * @author csilla
 * 
 *         category names
 *
 */
public enum ToolCategory {

    // from Geometry & Graphing Calculator
    /**
     * basic
     */
    BASIC("BasicTools"),
    /**
     * edit
     */
    EDIT("Edit"),
    /**
     * media
     */
    MEDIA("Media", false),
    /**
     * construct
     */
    CONSTRUCT("Construct"),
    /**
     * measure
     */
    MEASURE("Measure"),
    /**
     * points
     */
    POINTS("Points"),
    /**
     * lines
     */
    LINES("Lines"),
    /**
     * polygons
     */
    POLYGONS("Polygons"),
    /**
     * circles
     */
    CIRCLES("Circles"),
    /**
     * curves
     */
    CURVES("Curves"),
    /**
     * conics
     */
    CONICS("Conics"),
    /**
     * transformation
     */
    TRANSFORM("Transform"),
    /**
     * special lines
     */
    SPECIAL_LINES("SpecialLines"),
    /**
     * others
     */
    OTHERS("Others"),

    // specific to 3D Grapher
    /**
     * lines and polygons
     */
    LINES_AND_POLYGONS("LinesAndPolygons"),
    /**
     * solids
     */
    SOLIDS("Solids"),
    /**
     * planes
     */
    PLANES("Planes"),
    /**
     * select and format
     */
    SELECT_AND_FORMAT("SelectAndFormat");

    private final String header;
    private final boolean allowedInExam;

    ToolCategory(String header) {
        this(header, true);
    }

    ToolCategory(String header, boolean allowedInExam) {
        this.header = header;
        this.allowedInExam = allowedInExam;
    }

    /**
     * Gets localized header text..
     * @param loc localization
     * @return localized header
     */
    public String getLocalizedHeader(Localization loc) {
        return loc.getMenu("ToolCategory." + header);
    }

    public boolean isAllowedInExam() {
        return allowedInExam;
    }
}
