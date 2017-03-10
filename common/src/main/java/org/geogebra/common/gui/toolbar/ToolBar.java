package org.geogebra.common.gui.toolbar;

import java.util.List;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

/**
 * @author gabor
 *
 *         This class is not a superclass of ToolBar, only common method stack
 */
public class ToolBar {

	/**
	 * Integer used to indicate a separator in the toolbar.
	 */
	public static final Integer SEPARATOR = Integer.valueOf(-1);

	/**
	 * Returns with the default definition of the general tool bar without
	 * macros.
	 * 
	 * @param html5
	 *            true, if all menus must appear. (On the web there are some
	 *            tools, which don't appear.)
	 * @param exam
	 *            true, if exam mode is set.
	 * @param app
	 *            TODO
	 * @return The default definition of the general tool bar without macros.
	 */
	public static String getAllToolsNoMacros(boolean html5, boolean exam,
			App app) {

		StringBuilder sb = new StringBuilder();

		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		if (!html5) {
			// these two are old tools that we don't want to enable in HTML5
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_MOVE_ROTATE);
		}

		// freehand tools in with move tool
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_FREEHAND_SHAPE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PEN);
		if (app != null && app.has(Feature.ERASER)) {
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_ERASER);
		}

		// points
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_EXTREMUM);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROOTS);

		// basic lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_JOIN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_RAY);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLYLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR_FROM_POINT);

		// advanced lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ORTHOGONAL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_LINE_BISECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGULAR_BISECTOR);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TANGENTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLAR_DIAMETER);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_FITLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_LOCUS);

		// polygon
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RIGID_POLYGON);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR_POLYGON);

		// circles, arcs
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPASSES);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SEMICIRCLE);
		sb.append("  ");
		sb.append(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);

		// conics
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARABOLA);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIC_FIVE_POINTS);

		// measurements
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGLE_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DISTANCE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_AREA);
		sb.append("  ");
		sb.append(EuclidianConstants.MODE_SLOPE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CREATE_LIST);
		sb.append("  ");
		sb.append(EuclidianConstants.MODE_RELATION);
		sb.append("  ");
		sb.append(EuclidianConstants.MODE_FUNCTION_INSPECTOR);

		// transformations
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);

		// objects with actions
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_SLIDER);
		// in exam mode text and image cannot be inserted
		if (!exam) {
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_TEXT);

			sb.append(" ");
			sb.append(EuclidianConstants.MODE_IMAGE);
		}

		sb.append(" ");
		sb.append(EuclidianConstants.MODE_BUTTON_ACTION);

		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);

		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TEXTFIELD_ACTION);

		// properties
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);

		return sb.toString();
	}

	/**
	 * @return toolBar definition string for white board
	 */
	public static String getWBToolBarDefString() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_MOVE);
		sb.append(" | ");
		// write tools + erasers
		sb.append(EuclidianConstants.MODE_PEN + " ");
		sb.append(EuclidianConstants.MODE_FREEHAND_SHAPE + " ");
		sb.append(EuclidianConstants.MODE_DELETE + " ");
		sb.append(EuclidianConstants.MODE_ERASER + " | ");
		// geometry objects
		sb.append(EuclidianConstants.MODE_JOIN + " ");
		sb.append(EuclidianConstants.MODE_POLYGON + " ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON + " ");
		sb.append(EuclidianConstants.MODE_CIRCLE_TWO_POINTS + " ");
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS + " | ");
		// shapes
		sb.append(EuclidianConstants.MODE_SHAPE_TRIANGLE + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_SQUARE + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_RECTANGLE + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_POLYGON + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_FREEFORM + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_CIRCLE + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_ELLIPSE + " ");
		sb.append(EuclidianConstants.MODE_SHAPE_LINE + " | ");
		sb.append(EuclidianConstants.MODE_TEXT + " ");
		sb.append(EuclidianConstants.MODE_IMAGE);
		return sb.toString();
	}

	public static String getMOWMediaToolBarDefString() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_TEXT + " | ");
		sb.append(EuclidianConstants.MODE_IMAGE + " | ");
		sb.append(EuclidianConstants.MODE_VIDEO + " | ");
		sb.append(EuclidianConstants.MODE_AUDIO + " | ");
		sb.append(EuclidianConstants.MODE_GEOGEBRA + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_TRIANGLE + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_RECTANGLE + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_POLYGON + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_FREEFORM + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_ELLIPSE);

		return sb.toString();
	}

	public static String getMOWToolsShapesDefString() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_SHAPE_LINE + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_SQUARE + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_RECTANGLE + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_CIRCLE + " | ");
		sb.append(EuclidianConstants.MODE_FREEHAND_SHAPE + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_ELLIPSE + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_POLYGON + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_FREEFORM + " | ");
		sb.append(EuclidianConstants.MODE_SHAPE_TRIANGLE);

		return sb.toString();
	}

	public static String getMOWToolsPointsDefString() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_POINT + " | ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT + " | ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH + " | ");
		sb.append(EuclidianConstants.MODE_INTERSECT + " | ");
		sb.append(EuclidianConstants.MODE_MIDPOINT + " | ");
		sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER + " | ");
		sb.append(EuclidianConstants.MODE_EXTREMUM + " | ");
		sb.append(EuclidianConstants.MODE_ROOTS);

		return sb.toString();
	}

	public static String getMOWToolsLinesDefString() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_LINE_BISECTOR + " | ");
		sb.append(EuclidianConstants.MODE_SEGMENT + " | ");
		sb.append(EuclidianConstants.MODE_RAY + " | ");
		sb.append(EuclidianConstants.MODE_POLYLINE + " | ");
		sb.append(EuclidianConstants.MODE_VECTOR + " | ");
		sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER);

		return sb.toString();
	}

	public static String getMOWToolsDefString() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_SHAPE_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHAPE_SQUARE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHAPE_RECTANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHAPE_CIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_FREEHAND_SHAPE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHAPE_ELLIPSE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHAPE_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHAPE_FREEFORM);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHAPE_TRIANGLE);
		sb.append(" | ");

		// Points
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_EXTREMUM);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROOTS);
		sb.append(" | ");

		// Lines
		sb.append(EuclidianConstants.MODE_LINE_BISECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RAY);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLYLINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER);
		sb.append(" | ");

		// Polygons
		sb.append(EuclidianConstants.MODE_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RIGID_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR_POLYGON);
		sb.append(" | ");

		// Circles, arcs
		sb.append(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPASSES);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SEMICIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
		// conics
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CONIC_FIVE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARABOLA);

		// reflects
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);

		// advanced lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ORTHOGONAL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_LINE_BISECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGULAR_BISECTOR);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TANGENTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLAR_DIAMETER);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_FITLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_LOCUS);

		// measurements

		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGLE_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DISTANCE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_AREA);

		return sb.toString();
	}

	/**
	 * @return The default definition of the general tool bar without macros.
	 */
	public static String getAllToolsNoMacrosForPlane() {
		StringBuilder sb = new StringBuilder();

		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_MOVE_ROTATE);
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_RECORD_TO_SPREADSHEET);

		// points
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		// sb.append(" , ");
		// sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER);

		// basic lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_JOIN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_RAY);
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_POLYLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR_FROM_POINT);

		// advanced lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ORTHOGONAL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_LINE_BISECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGULAR_BISECTOR);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TANGENTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLAR_DIAMETER);
		// sb.append(" , ");
		// sb.append(EuclidianView.MODE_FITLINE);
		// sb.append(" , ");
		// sb.append(EuclidianView.MODE_LOCUS);

		// polygon
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_RIGID_POLYGON);
		// sb.append(" ");
		// sb.append(EuclidianView.MODE_POLYLINE);

		// circles, arcs
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPASSES);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SEMICIRCLE);
		sb.append("  ");
		sb.append(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);

		// conics
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARABOLA);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIC_FIVE_POINTS);

		// measurements

		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGLE_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DISTANCE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_AREA);
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_SLOPE);

		// transformations

		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		// sb.append(" "); sb.append(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);

		// dialogs

		sb.append(" | ");
		// sb.append(EuclidianConstants.MODE_SLIDER);
		// sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TEXT);
		/*
		 * sb.append(" "); sb.append(EuclidianView.MODE_IMAGE); sb.append(" ");
		 * sb.append(EuclidianView.MODE_PEN); sb.append(" , ");
		 * sb.append(EuclidianView.MODE_RELATION); sb.append(" ");
		 * sb.append(EuclidianView.MODE_PROBABILITY_CALCULATOR); sb.append(" ");
		 * sb.append(EuclidianView.MODE_FUNCTION_INSPECTOR);
		 */

		// objects with actions
		/*
		 * sb.append(" | "); sb.append(EuclidianView.MODE_SHOW_HIDE_CHECKBOX);
		 * sb.append(" "); sb.append(EuclidianView.MODE_BUTTON_ACTION);
		 * sb.append(" "); sb.append(EuclidianView.MODE_TEXTFIELD_ACTION);
		 */

		// properties
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);

		return sb.toString();
	}

	/**
	 * @return default toolbar (3D)
	 */
	public static String getAllToolsNoMacros3D() {

		StringBuilder sb = new StringBuilder();

		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		sb.append(" | ");

		// points
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
		// sb.append( " , ");
		// sb.append( EuclidianConstants.MODE_COMPLEX_NUMBER );
		sb.append(" | ");

		// lines
		sb.append(EuclidianConstants.MODE_JOIN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT_FIXED);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RAY);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR_FROM_POINT);
		sb.append(" | ");

		// specific lines
		sb.append(EuclidianConstants.MODE_ORTHOGONAL_THREE_D);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGULAR_BISECTOR);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TANGENTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLAR_DIAMETER);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_LOCUS);
		sb.append(" | ");

		// polygons
		sb.append(EuclidianConstants.MODE_POLYGON);
		sb.append(" | ");

		// conics
		sb.append(EuclidianConstants.MODE_CIRCLE_AXIS_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append(" , ");
		// sb.append(EuclidianView.MODE_SEMICIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);

		// sb.append( " | ");
		sb.append(" , "); // regroup conic stuff to avoid too much toolbar items

		// conics
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARABOLA);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIC_FIVE_POINTS);
		sb.append(" | ");

		// intersection curve
		sb.append(EuclidianConstants.MODE_INTERSECTION_CURVE);
		sb.append(" | ");

		// planes
		sb.append(EuclidianConstants.MODE_PLANE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PLANE);
		sb.append(" , ");

		// specific planes
		sb.append(EuclidianConstants.MODE_ORTHOGONAL_PLANE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL_PLANE);
		sb.append(" | ");

		// prisms/pyramids/cones/cylinders
		sb.append(EuclidianConstants.MODE_PYRAMID);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PRISM);

		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIFY);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_EXTRUSION);

		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS);

		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TETRAHEDRON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CUBE);

		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_NET);
		sb.append(" | ");

		// spheres
		sb.append(EuclidianConstants.MODE_SPHERE_TWO_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SPHERE_POINT_RADIUS);
		sb.append(" | ");

		// measures
		sb.append(EuclidianConstants.MODE_ANGLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DISTANCE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_AREA);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VOLUME);
		sb.append(" | ");

		// transformations
		sb.append(EuclidianConstants.MODE_MIRROR_AT_PLANE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROTATE_AROUND_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);
		sb.append(" | ");

		// texts, sliders, etc.
		sb.append(EuclidianConstants.MODE_TEXT);
		sb.append(" | ");

		// view control
		sb.append(EuclidianConstants.MODE_ROTATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);
		sb.append(" , ");

		sb.append(EuclidianConstants.MODE_VIEW_IN_FRONT_OF);

		return sb.toString();
	}

	public static String getAllToolsNoMacrosPhone(App app) {
		StringBuilder sb = new StringBuilder();

		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_FREEHAND_SHAPE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PEN);

		// points
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_COMPLEX_NUMBER);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_EXTREMUM);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_ROOTS);

		// basic lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_JOIN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT_FIXED);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_RAY);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLYLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR_FROM_POINT);

		// advanced lines
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ORTHOGONAL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_LINE_BISECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGULAR_BISECTOR);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TANGENTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLAR_DIAMETER);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_FITLINE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_LOCUS);

		// polygon
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RIGID_POLYGON);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR_POLYGON);

		// circles, arcs
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COMPASSES);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SEMICIRCLE);
		sb.append("  ");
		sb.append(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);

		// conics
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARABOLA);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIC_FIVE_POINTS);

		// measurements
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_ANGLE);
		sb.append(" ");

		sb.append(EuclidianConstants.MODE_ANGLE_FIXED);
		sb.append(" , ");

		sb.append(EuclidianConstants.MODE_DISTANCE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_AREA);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SLOPE);

		// transformations
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ROTATE_BY_ANGLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT);

		//
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_RELATION);

		// objects with actions
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_SLIDER);

		// properties
		sb.append(" | ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);

		return sb.toString();
	}

	public static String getAllToolsNoMacros3DPhone(App app) {
		StringBuilder sb = new StringBuilder();

		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		sb.append(" | ");

		// points
		sb.append(EuclidianConstants.MODE_POINT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POINT_ON_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_INTERSECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIDPOINT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_ATTACH_DETACH);
		// sb.append( " , ");
		// sb.append( EuclidianConstants.MODE_COMPLEX_NUMBER );
		sb.append(" | ");

		// lines
		sb.append(EuclidianConstants.MODE_JOIN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SEGMENT_FIXED); // dialog OK, result
															// undefined -
															// MOB-545
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_RAY);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VECTOR_FROM_POINT);
		sb.append(" | ");

		// specific lines
		sb.append(EuclidianConstants.MODE_ORTHOGONAL_THREE_D);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ANGULAR_BISECTOR);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TANGENTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_POLAR_DIAMETER);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_LOCUS); // exception: MOB-546 -- maybe
													// related to MOB-542
		sb.append(" | ");

		// polygons
		sb.append(EuclidianConstants.MODE_POLYGON); // preview shows and
													// disappear: MOB-468
		sb.append(" | ");

		// conics
		sb.append(EuclidianConstants.MODE_CIRCLE_AXIS_POINT);
		sb.append(" ");
		// sb.append(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION); //
		// needs dialog: MOB-556
		// sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		sb.append(" , ");
		// sb.append(EuclidianView.MODE_SEMICIRCLE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);

		// sb.append( " | ");
		sb.append(" , "); // regroup conic stuff to avoid too much toolbar items

		// conics
		sb.append(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARABOLA);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIC_FIVE_POINTS);
		sb.append(" | ");

		// intersection curve
		sb.append(EuclidianConstants.MODE_INTERSECTION_CURVE); // OK by clicking
																// several
																// objects, not
																// clicking
																// intersection:
																// MOB-547
		sb.append(" | ");

		// planes
		sb.append(EuclidianConstants.MODE_PLANE_THREE_POINTS);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PLANE);
		sb.append(" , ");

		// specific planes
		sb.append(EuclidianConstants.MODE_ORTHOGONAL_PLANE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PARALLEL_PLANE);
		sb.append(" | ");

		// prisms/pyramids/cones/cylinders
		sb.append(EuclidianConstants.MODE_PYRAMID); // preview problem, maybe
													// the same as polygon
													// MOB-468
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_PRISM); // preview problem, maybe the
													// same as polygon MOB-468

		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_CONIFY); // OK on drag, dialog missing
													// on click: MOB-557
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_EXTRUSION); // OK on drag, dialog
														// missing on click:
														// MOB-557

		// sb.append(" , ");
		// sb.append(EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS); // needs
		// dialog: MOB-558
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS); //
		// needs dialog: MOB-558

		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_TETRAHEDRON);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_CUBE);

		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_NET); // OK -- slider hard to find:
												// MOB-548
		sb.append(" | ");

		// spheres
		sb.append(EuclidianConstants.MODE_SPHERE_TWO_POINTS);
		// sb.append(" ");
		// sb.append(EuclidianConstants.MODE_SPHERE_POINT_RADIUS); // needs
		// dialog: MOB-559
		sb.append(" | ");

		// measures
		sb.append(EuclidianConstants.MODE_ANGLE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DISTANCE); // text not visible in 3D
														// view: MOB-549
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_AREA); // text not visible in 3D view:
													// MOB-549
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_VOLUME); // text not visible in 3D
													// view: MOB-549
		sb.append(" | ");

		// transformations
		sb.append(EuclidianConstants.MODE_MIRROR_AT_PLANE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_LINE);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_MIRROR_AT_POINT);
		sb.append(" ");
		// sb.append(EuclidianConstants.MODE_ROTATE_AROUND_LINE); // needs
		// dialog: MOB-560
		// sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_DILATE_FROM_POINT); // OK -- check
																// output visual
																// style:
																// MOB-550
		sb.append(" | ");

		// texts, sliders, etc.
		// sb.append(EuclidianConstants.MODE_TEXT); // needs dialog
		// sb.append(" | ");

		// view control
		sb.append(EuclidianConstants.MODE_ROTATEVIEW);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TRANSLATEVIEW); // check clipping box
															// update: MOB-551
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_IN);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_ZOOM_OUT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_COPY_VISUAL_STYLE); // works reverse
																// (as in 2D
																// view)
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_DELETE);
		sb.append(" , ");

		sb.append(EuclidianConstants.MODE_VIEW_IN_FRONT_OF);

		return sb.toString();
	}

	/**
	 * Parses a toolbar definition string like "0 , 1 2 | 3 4 5 || 7 8 9" where
	 * the int values are mode numbers, "," adds a separator within a menu, "|"
	 * starts a new menu and "||" adds a separator before starting a new menu.
	 * 
	 * @param toolbarString
	 *            toolbar definition string
	 * 
	 * @return toolbar as nested Vector objects with Integers for the modes.
	 *         Note: separators have negative values.
	 * @throws NumberFormatException
	 *             when non-integer is encountered
	 */
	public static Vector<ToolbarItem> parseToolbarString(String toolbarString)
			throws NumberFormatException {
		return parseToolbarString(toolbarString, defaultToolsRemover);
	}

	public static Vector<ToolbarItem> parseToolbarString(String toolbarString,
			ToolsRemover toolsRemover) throws NumberFormatException {
		Vector<ToolbarItem> toolbar = new Vector<ToolbarItem>();
		StringBuilder currentNumber = new StringBuilder();
		Vector<Integer> menu = new Vector<Integer>();
		for (int i = 0; i < toolbarString.length(); i++) {
			char ch = toolbarString.charAt(i);
			if ('0' <= ch && ch <= '9') {
				currentNumber.append(ch);
			} else if (ch == '|') {
				flush(currentNumber, menu, toolsRemover);
				if (menu.size() > 0) {
					toolbar.add(new ToolbarItem(menu));
				}
				menu = new Vector<Integer>();
			} else if (ch == ',') {
				flush(currentNumber, menu, toolsRemover);
				menu.add(SEPARATOR);
			} else {
				flush(currentNumber, menu, toolsRemover);
			}
		}
		flush(currentNumber, menu, toolsRemover);
		if (menu.size() > 0) {
			toolbar.add(new ToolbarItem(menu));
		}
		return toolbar;
	}

	private static void flush(StringBuilder currentNumber, Vector<Integer> menu,
			ToolsRemover toolsRemover) {
		if (currentNumber.length() > 0) {
			int mode = Integer.parseInt(currentNumber.toString());
			if (toolsRemover.keep(mode)) {
				menu.addElement(mode);
			}
			currentNumber.setLength(0);
		}

	}

	public static String addMode(String toolbarString, int mode) {
		int pos = toolbarString.lastIndexOf('|');
		if (pos > 0) {
			String after = toolbarString.substring(pos + 1).trim();
			int digits = (EuclidianConstants.MACRO_MODE_ID_OFFSET + "")
					.length();
			boolean mayStartWithMacro = after.length() >= digits;
			for (int i = 0; i < digits && mayStartWithMacro; i++) {
				if (after.charAt(i) > '9' || after.charAt(i) < '0') {
					mayStartWithMacro = false;
				}
			}
			if (mayStartWithMacro) {
				String before = toolbarString.substring(0, pos).trim();
				return before + " | " + mode + " " + after;
			}
		}
		return toolbarString + " | " + mode;
	}

	/**
	 * @param app
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(App app) {
		StringBuilder sb = new StringBuilder();

		sb.append(ToolBar.getAllToolsNoMacros(true, app.isExam(), app));

		// macros
		Kernel kernel = app.getKernel();
		int macroNumber = kernel.getMacroNumber();

		// check if at least one macro is shown
		// to avoid strange GUI
		boolean at_least_one_shown = false;
		for (int i = 0; i < macroNumber; i++) {
			Macro macro = kernel.getMacro(i);
			if (macro.isShowInToolBar()) {
				at_least_one_shown = true;
				break;
			}
		}

		if (macroNumber > 0 && at_least_one_shown) {
			sb.append(" || ");
			for (int i = 0; i < macroNumber; i++) {
				Macro macro = kernel.getMacro(i);
				if (macro.isShowInToolBar()) {
					sb.append(i + EuclidianConstants.MACRO_MODE_ID_OFFSET);
					sb.append(" ");
				}
			}
		}

		return sb.toString();
	}

	public static void removeSeparators(List<ToolbarItem> standardToolbar) {
		for (ToolbarItem item : standardToolbar) {
			Vector<Integer> menu = item.getMenu();
			// iterate through menu backwards because items might be removed
			for (int i = menu.size() - 1; i > -1; i--) {
				if (ToolBar.SEPARATOR.equals(menu.get(i))) {
					menu.remove(i);
				}
			}
		}
	}

	static public class ToolsRemover {

		public ToolsRemover() {
			init();
		}

		protected void init() {
			// to override
		}

		public boolean keep(int mode) {
			if (mode != 59 && mode != 1011) {
				return true;
			}
			return false;
		}
	}

	static private ToolsRemover defaultToolsRemover = new ToolsRemover();

	/**
	 * any toolbar composed of a set of following menus should be considered as
	 * default toolbar (old default toolbar)
	 */
	static private final String[][] DEFAULT_TOOLBAR_PRE_5_0_280 = {
			{ "0", "0 39" }, { "1 501 67 5 19 72", "1 501 67 5 19 72 75 76" },
			{ "2 15 45 18 65 7 37" }, { "4 3 8 9 13 44 58 47" },
			{ "16 51 64 70" }, { "10 34 53 11 24 20 22 21 23" },
			{ "55 56 57 12" }, { "36 46 38 49 50 71" }, { "30 29 54 32 31 33" },
			{ "17 26 62 73 14 68", "17 26 62 14 66 68",
					"17 26 62 73 14 66 68" },
			{ "25 52 60 61" }, { "40 41 42 27 28 35 6" } };

	final static public boolean isOldDefaultToolbar(String definition) {

		// Log.debug("\n"+definition);

		if (definition == null) {
			return false;
		}

		String def2 = definition.replaceAll(",", ""); // remove comas
		def2 = def2.replaceAll("59", ""); // remove record to spreadsheet tool
		def2 = def2.replaceAll("\\|{2,}", " \\| "); // remove double vertical
													// bars
		def2 = def2.replaceAll(" {2,}", " "); // remove multiple spaces

		String[] split = def2.split(" \\| "); // split by tool menus

		// Log.debug("lengths:"+split.length+"/"+DEFAULT_TOOLBAR_PRE_5_0_280.length);
		//
		// for (int i = 0 ; i < DEFAULT_TOOLBAR_PRE_5_0_280.length && i <
		// split.length; i++){
		// String menu = split[i];
		// String[] defaults = DEFAULT_TOOLBAR_PRE_5_0_280[i];
		// String out = "\n"+menu+"/";
		// for (int j = 0 ; j < defaults.length ; j++){
		// out+=defaults[j]+",";
		// }
		// Log.debug(out);
		// }

		if (split.length != DEFAULT_TOOLBAR_PRE_5_0_280.length) {
			return false;
		}

		// Log.debug("\ntest");

		for (int i = 0; i < DEFAULT_TOOLBAR_PRE_5_0_280.length; i++) {
			boolean found = false;
			String menu = split[i];
			String[] defaults = DEFAULT_TOOLBAR_PRE_5_0_280[i];
			for (int j = 0; j < defaults.length && !found; j++) {
				// Log.debug("\n"+menu+"/"+defaults[j]);
				if (defaults[j].equals(menu)) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}

		// Log.debug("\n >> default toolbar");

		return true;
	}

}
