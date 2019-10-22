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
    MEDIA("Media"),
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

    ToolCategory(String header) {
        this.header = header;
    }

    public String getLocalizedHeader(Localization loc) {
        return loc.getMenu("ToolCategory." + header);
    }
}
