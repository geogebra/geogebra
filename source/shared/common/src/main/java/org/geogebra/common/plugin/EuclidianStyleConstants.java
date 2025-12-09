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

package org.geogebra.common.plugin;

import java.util.List;

import org.geogebra.common.properties.PropertyResource;

public class EuclidianStyleConstants {
	public static final int LINE_TYPE_POINTWISE = -1;

	public static final int LINE_TYPE_FULL = 0;

	public static final int LINE_TYPE_DASHED_SHORT = 10;

	public static final int LINE_TYPE_DASHED_LONG = 15;

	public static final int LINE_TYPE_DOTTED = 20;

	public static final int LINE_TYPE_DASHED_DOTTED = 30;

	public static final int RIGHT_ANGLE_STYLE_NONE = 0;

	public static final int RIGHT_ANGLE_STYLE_SQUARE = 1;

	public static final int RIGHT_ANGLE_STYLE_DOT = 2;

	public static final int RIGHT_ANGLE_STYLE_L = 3; // Belgian style

	public static final List<PropertyResource> lineStyleIcons = List.of(
			PropertyResource.ICON_LINE_TYPE_FULL, PropertyResource.ICON_LINE_TYPE_DASHED_LONG,
			PropertyResource.ICON_LINE_TYPE_DASHED_SHORT, PropertyResource.ICON_LINE_TYPE_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_DOTTED);

	public static final List<Integer> lineStyleList = List.of(
			EuclidianStyleConstants.LINE_TYPE_FULL,
			EuclidianStyleConstants.LINE_TYPE_DASHED_LONG,
			EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT,
			EuclidianStyleConstants.LINE_TYPE_DOTTED,
			EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED
	);

	/**
	 * size for draggable points ie free points and point on path/region
	 */
	public static final int DEFAULT_POINT_SIZE = 5;

	/**
	 * max size for points
	 */
	public static final int MAX_POINT_SIZE = 9;

	/**
	 * enlarge preview point size for intersection
	 */
	public static final int PREVIEW_POINT_ENLARGE_SIZE_FOR_INTERSECTION = 6;

	/**
	 * enlarge preview point size when on path
	 */
	public static final int PREVIEW_POINT_ENLARGE_SIZE_ON_PATH = 3;

    /**
     * enlarge preview point size when already
     */
    public static final int PREVIEW_POINT_ENLARGE_SIZE_WHEN_ALREADY = 1;

	/**
	 * preview point size when free point
	 */
	public static final int PREVIEW_POINT_SIZE_WHEN_FREE = 3;

	/**
	 * size for non-draggable points eg intersections
	 */
	public static final int DEFAULT_POINT_SIZE_DEPENDENT = 4;

	/* default size of dependent point in Graphing Calculator */
	public static final int DEFAULT_POINT_SIZE_DEPENDENT_GRAPHING = 5;

	public static final int DEFAULT_LINE_THICKNESS = 5;
	
	public static final int AXES_THICKNESS = 3;

	public static final int OBJSTYLE_DEFAULT_LINE_THICKNESS = 5;

	/* default line thickness of angle on Geometry app */
	public static final int OBJSTYLE_DEFAULT_LINE_THICKNESS_ANGLE_GEOMETRY = 4;

	public static final double OBJSTYLE_DEFAULT_ALPHA = 178;
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY = 178; // 0.7 * 255;
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY_ANGLE = 153; // 0.6*255;
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY_POLYGON = 204; // 0.8*255
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY_SECTOR = 204; //
																		// 0.8*255
	/* default line opacity on Geometry app */
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY_GEOMETRY = 204; // 0.8*255

	/* default line opacity on Graphing app */
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY_FUNCTION_GEOMETRY = 204; // 0.8*255
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY_CURVE_GEOMETRY = 204; // 0.8*255
	public static final int OBJSTYLE_DEFAULT_LINE_OPACITY_EQUATION_GEOMETRY = 204; // 0.8*255

	public static final int DEFAULT_ANGLE_SIZE = 30;

	public static final int DEFAULT_LINE_TYPE = LINE_TYPE_FULL;

	public static final int LINE_TYPE_HIDDEN_NONE = 0;

	public static final int LINE_TYPE_HIDDEN_DASHED = 1;

	public static final int LINE_TYPE_HIDDEN_AS_NOT_HIDDEN = 2;

	public static final int DEFAULT_LINE_TYPE_HIDDEN = LINE_TYPE_HIDDEN_DASHED;

	public static final double SELECTION_ADD = 2.0;

	// ggb3D 2008-10-27 : mode constants moved to EuclidianConstants.java

	public static final int AXES_TICK_STYLE_MAJOR_MINOR = 0;

	public static final int AXES_TICK_STYLE_MAJOR = 1;

	public static final int AXES_TICK_STYLE_NONE = 2;

	// used in the XML, DO NOT CHANGE
	public static final int AXES_RIGHT_ARROW = 1; // also TOP
	public static final int AXES_BOLD = 2;
	public static final int AXES_LEFT_ARROW = 4; // also BOTTOM
	public static final int AXES_FILL_ARROWS = 8;

	// used in the XML, DO NOT CHANGE
	public static final int AXES_LINE_TYPE_FULL = 0;
	public static final int AXES_LINE_TYPE_ARROW = AXES_RIGHT_ARROW;
	public static final int AXES_LINE_TYPE_FULL_BOLD = AXES_BOLD;
	public static final int AXES_LINE_TYPE_ARROW_BOLD = AXES_RIGHT_ARROW
			+ AXES_BOLD;
	public static final int AXES_LINE_TYPE_ARROW_FILLED = AXES_RIGHT_ARROW
			+ AXES_FILL_ARROWS;
	public static final int AXES_LINE_TYPE_TWO_ARROWS = AXES_RIGHT_ARROW
			+ AXES_LEFT_ARROW;
	public static final int AXES_LINE_TYPE_TWO_ARROWS_FILLED = AXES_RIGHT_ARROW
			+ AXES_LEFT_ARROW + AXES_FILL_ARROWS;
	public static final int AXES_LINE_TYPE_ARROW_FILLED_BOLD = AXES_RIGHT_ARROW
			+ AXES_BOLD + AXES_FILL_ARROWS;
	public static final int AXES_LINE_TYPE_TWO_ARROWS_BOLD = AXES_RIGHT_ARROW
			+ AXES_LEFT_ARROW + AXES_BOLD;
	public static final int AXES_LINE_TYPE_TWO_ARROWS_FILLED_BOLD = AXES_RIGHT_ARROW
			+ AXES_LEFT_ARROW + AXES_BOLD + AXES_FILL_ARROWS;

	// for the options menu
	final private static Integer[] lineStyleOptions = {
			EuclidianStyleConstants.AXES_LINE_TYPE_FULL,
			EuclidianStyleConstants.AXES_LINE_TYPE_ARROW,
			EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED,
			EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS,
			EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED };

	public static final int POINT_STYLE_DOT = 0;
	public static final int POINT_STYLE_CROSS = 1;
	public static final int POINT_STYLE_CIRCLE = 2;
	public static final int POINT_STYLE_PLUS = 3;
	public static final int POINT_STYLE_FILLED_DIAMOND = 4;
	public static final int POINT_STYLE_EMPTY_DIAMOND = 5;
	public static final int POINT_STYLE_TRIANGLE_NORTH = 6;
	public static final int POINT_STYLE_TRIANGLE_SOUTH = 7;
	public static final int POINT_STYLE_TRIANGLE_EAST = 8;
	public static final int POINT_STYLE_TRIANGLE_WEST = 9;
	public static final int POINT_STYLE_NO_OUTLINE = 10;
	public static final int MAX_POINT_STYLE = 10;

	public static final int MAX_LAYERS = 9;

	/** point doesn't snap */
	public static final int POINT_CAPTURING_OFF = 0;
	/** point snaps to grid (axes or grid visible or not) */
	public static final int POINT_CAPTURING_ON = 1;
	/** point sticks to grid (axes or grid visible or not) */
	public static final int POINT_CAPTURING_ON_GRID = 2;
	/** point snaps to grid when axes or grid are visible */
	public static final int POINT_CAPTURING_AUTOMATIC = 3;
	public static final int POINT_CAPTURING_STICKY_POINTS = 4;
	public static final double POINT_CAPTURING_GRID = 0.125;

	public static final int POINT_CAPTURING_DEFAULT = POINT_CAPTURING_AUTOMATIC;

	// we don't want POINT_CAPTURING_STICKY_POINTS in the XML!
	public static final int POINT_CAPTURING_XML_MAX = 3;

	public static final int TOOLTIPS_AUTOMATIC = 0;
	public static final int TOOLTIPS_ON = 1;
	public static final int TOOLTIPS_OFF = 2;
	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;

	public static final int NO_AXES = -1;

	public final static int VIEW_DIRECTION_XY = 0;
	public final static int VIEW_DIRECTION_XZ = 1;
	public final static int VIEW_DIRECTION_YZ = 2;

	/**
	 * Get line style for given index.
	 * @param i index
	 * @return line style
	 */
	public static Integer getLineStyleOptions(int i) {
		return lineStyleOptions[i];
	}

	public static Integer getLineStyleOptionsLength() {
		return lineStyleOptions.length;
	}

}
