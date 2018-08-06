package org.geogebra.common.gui.toolcategorization.impl;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFactory;

public abstract class AbstractToolCollectionFactory implements ToolCollectionFactory {

    /**
     * full list of tools
     */
    static final String LEVEL_ADVANCED = "ToolsetLevel.Advanced";
    /**
     * non-empty construction
     */
    static final String LEVEL_STANDARD = "ToolsetLevel.Standard";
    /**
     * for empty construction
     */
    static final String LEVEL_EMPTY_CONSTRUCTION = "ToolsetLevel.Empty";

    /**
     * basic
     */
    static final String CATEGORY_BASIC = "ToolCategory.BasicTools";
    /**
     * edit
     */
    static final String CATEGORY_EDIT = "ToolCategory.Edit";
    /**
     * media
     */
    static final String CATEGORY_MEDIA = "ToolCategory.Media";
    /**
     * construct
     */
    static final String CATEGORY_CONSTRUCT = "ToolCategory.Construct";
    /**
     * measure
     */
    static final String CATEGORY_MEASURE = "ToolCategory.Measure";
    /**
     * points
     */
     static final String CATEGORY_POINTS = "ToolCategory.Points";
    /**
     * lines
     */
     static final String CATEGORY_LINES = "ToolCategory.Lines";
    /**
     * polygons
     */
     static final String CATEGORY_POLYGONS = "ToolCategory.Polygons";
    /**
     * circles
     */
     static final String CATEGORY_CIRCLES = "ToolCategory.Circles";
    /**
     * curves
     */
     static final String CATEGORY_CURVES = "ToolCategory.Curves";
    /**
     * conics
     */
     static final String CATEGORY_CONICS = "ToolCategory.Conics";
    /**
     * transformation
     */
     static final String CATEGORY_TRANSFORM = "ToolCategory.Transform";
    /**
     * special lines
     */
     static final String CATEGORY_SPECIAL_LINES = "ToolCategory.SpecialLines";
    /**
     * others
     */
     static final String CATEGORY_OTHERS = "ToolCategory.Others";
    /**
     * lines and polygons
     */
    // specific to 3D Grapher
     static final String CATEGORY_LINES_AND_POLYGONS = "ToolCategory.LinesAndPolygons";
    /**
     * solids
     */
     static final String CATEGORY_SOLIDS = "ToolCategory.Solids";
    /**
     * planes
     */
     static final String CATEGORY_PLANES = "ToolCategory.Planes";
    /**
     * select and format
     */
     static final String CATEGORY_SELECT_AND_FORMAT = "ToolCategory.SelectAndFormat";

    boolean isPhoneApp = false;

    /**
     * Set to true if this Factory is created for phone apps.
     *
     * @param isPhoneApp if phone app is using this
     */
    public void setPhoneApp(boolean isPhoneApp) {
        this.isPhoneApp = isPhoneApp;
    }
}
