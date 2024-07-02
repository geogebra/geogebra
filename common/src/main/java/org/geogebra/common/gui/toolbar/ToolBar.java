package org.geogebra.common.gui.toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.ownership.GlobalScope;

/**
 * Utility class for toolbar strings
 *
 * @author gabor
 */
public class ToolBar {
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

	/**
	 * any toolbar composed of a set of following menus should be considered as
	 * default toolbar
	 * 
	 * The first group depends on selection tool and rotate tool (different for
	 * desktop x web x phone and prerelease x stable): accept all
	 */
	static private final String[][] DEFAULT_TOOLBAR = {
			{ "0 39 73 62 110", "0 39 73 62", "0 73 62", "0 77 73 62", "0 39 77 73 62",
					"0 77 73 74 62"},
			{ "1 501 67 5 19 72 75 76" }, { "2 15 45 18 65 7 37" },
			{ "4 3 8 9 13 44 58 47" }, { "16 51 64 70" },
			{ "10 34 53 11 24 20 22 21 23" }, { "55 56 57 12" },
			{ "36 46 38 49 50 71 14 68" }, { "30 29 54 32 31 33" },
			{ "25 17 26 60 52 61", "25 60 52 61" }, { "40 41 42 27 28 35 6" } };

	/**
	 * any toolbar composed of a set of following menus should be considered as
	 * default toolbar
	 */
	static private final String[][] DEFAULT_TOOLBAR_3D = { { "0" },
			{ "1 501 5 19 67" }, { "2 15 45 18 7 37" }, { "514 3 9 13 44 47" },
			{ "16", "16 51" },
            { "551 550 11 20 22 21 23 55 56 57 12", "551 550 11 22 23 55 56 57 12" }, { "69" },
			{ "510 511 512 513" },
			{ "533 531 534 532 522 523 537 536 535", "533 531 534 532 522 523 537 536 535 538",
					"533 531 534 532 538 522 523 537 536 535" },
			{ "521 520" }, { "36 38 49 560" }, { "571 30 29 570 31 33" },
			{ "17" }, { "540 40 41 42 27 28 35 6 502" } };

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
		return getAllToolsNoMacros(html5, exam, app.isUnbundled());
	}

	/**
	 * Returns with the default definition of the general tool bar without
	 * macros.
	 * 
	 * @param html5
	 *            true, if all menus must appear. (On the web there are some
	 *            tools, which don't appear.)
	 * @param exam
	 *            true, if exam mode is set.
	 * @param appUnbundled
	 *            whether this is for unbundled app
	 * @return The default definition of the general tool bar without macros.
	 */
	public static String getAllToolsNoMacros(boolean html5, boolean exam, boolean appUnbundled) {

		StringBuilder sb = new StringBuilder();

		// move
		sb.append(EuclidianConstants.MODE_MOVE);
		if (!html5) {
			// these two are old tools that we don't want to enable in HTML5
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_MOVE_ROTATE);
		}
		if (appUnbundled) {
			sb.append(" ");
			sb.append(EuclidianConstants.MODE_SELECT);
		}

		// freehand tools in with move tool
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
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_TEXT);
		// in exam mode image cannot be inserted
		if (!exam) {
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
	 * @param includeGraspableMath whether to include GraspableMath
	 * @param includeH5P whether to include H5P
	 * @param fileFeaturesEnabled whether file pickers are allowed
	 * @return definition for MOW media toolbar
	 */
	public static List<Integer> getNotesMediaToolBar(boolean includeGraspableMath,
			boolean includeH5P, boolean fileFeaturesEnabled) {

		List<Integer>tools = new ArrayList<>(Arrays.asList(
				EuclidianConstants.MODE_MEDIA_TEXT,
				EuclidianConstants.MODE_TABLE,
				EuclidianConstants.MODE_EQUATION,
				EuclidianConstants.MODE_CALCULATOR,
				EuclidianConstants.MODE_EXTENSION,
				EuclidianConstants.MODE_MIND_MAP,
				EuclidianConstants.MODE_IMAGE,
				EuclidianConstants.MODE_CAMERA,
				EuclidianConstants.MODE_VIDEO,
				EuclidianConstants.MODE_AUDIO,
				EuclidianConstants.MODE_PDF
		));

		if (!fileFeaturesEnabled) {
			tools.removeAll(Arrays.asList(
					EuclidianConstants.MODE_IMAGE,
					EuclidianConstants.MODE_PDF
			));
		}
		if (includeGraspableMath) {
			tools.add(EuclidianConstants.MODE_GRASPABLE_MATH);
		}
		if (includeH5P) {
			tools.add(EuclidianConstants.MODE_H5P);
		}

		return tools;
	}

	/**
	 * @return definition for MOW pen toolbar
	 */
	public static List<Integer> getNotesPenToolBar() {
		return Arrays.asList(EuclidianConstants.MODE_SELECT_MOW,
				EuclidianConstants.MODE_PEN,
				EuclidianConstants.MODE_RULER,
				EuclidianConstants.MODE_ERASER,
				EuclidianConstants.MODE_HIGHLIGHTER,
				EuclidianConstants.MODE_PROTRACTOR);
	}

	/**
	 * @return definition for MOW math toolbar
	 */
	public static List<Integer> getNotesShapesToolBar() {
		return Arrays.asList(EuclidianConstants.MODE_SHAPE_RECTANGLE,
				EuclidianConstants.MODE_SHAPE_SQUARE,
				EuclidianConstants.MODE_SHAPE_CIRCLE,
				EuclidianConstants.MODE_SHAPE_ELLIPSE,
				EuclidianConstants.MODE_SHAPE_PENTAGON,
				EuclidianConstants.MODE_SHAPE_FREEFORM,
				EuclidianConstants.MODE_SHAPE_TRIANGLE,
				EuclidianConstants.MODE_SHAPE_LINE,
				EuclidianConstants.MODE_MASK);
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
	 * @param app
	 *            app for feature flag
	 * @return default toolbar (3D)
	 */
	public static String getAllToolsNoMacros3D(App app) {

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
		sb.append(" ");
		sb.append(EuclidianConstants.MODE_REGULAR_POLYGON);
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
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SURFACE_OF_REVOLUTION);
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

	/**
	 * @param toolbarString
	 *            toolbar string
	 * @param toolsRemover
	 *            filter
	 * @return list of modes and submenus
	 * @throws NumberFormatException
	 *             when toolbar string is not valid
	 */
	public static Vector<ToolbarItem> parseToolbarString(String toolbarString,
			ToolsRemover toolsRemover) throws NumberFormatException {
		Vector<ToolbarItem> toolbar = new Vector<>();
		StringBuilder currentNumber = new StringBuilder();
		Vector<Integer> menu = new Vector<>();
		for (int i = 0; i < toolbarString.length(); i++) {
			char ch = toolbarString.charAt(i);
			if ('0' <= ch && ch <= '9') {
				currentNumber.append(ch);
			} else if (ch == '|') {
				flush(currentNumber, menu, toolsRemover);
				if (menu.size() > 0) {
					toolbar.add(new ToolbarItem(menu));
				}
				menu = new Vector<>();
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

	/**
	 * @param toolbarString
	 *            toolbar definition
	 * @param mode
	 *            additional mode
	 * @return new definition
	 */
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
	 *            applocation
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(App app) {
		StringBuilder sb = new StringBuilder();

		sb.append(ToolBar.getAllToolsNoMacros(app.isHTML5Applet(),
				GlobalScope.examController.isExamActive(), app));

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

	/**
	 * Filter for tools
	 */
	static public class ToolsRemover {

		/**
		 * Default constructor
		 */
		public ToolsRemover() {
			init();
		}

		/**
		 * Initialization
		 */
		protected void init() {
			// to override
		}

		/**
		 * @param mode
		 *            mode
		 * @return whether to keep the mode
		 */
		public boolean keep(int mode) {
			return mode != 59 && mode != 1011;
		}
	}

	/**
	 * @param definition
	 *            toolbar definition
	 * @return whether this is default toolbar of any version
	 */
	final static public boolean isDefaultToolbar(String definition) {
		if (definition == null) {
			return false;
		}

		String[] defSplit = split(definition);

		return isDefaultToolbar(defSplit, DEFAULT_TOOLBAR_PRE_5_0_280)
				|| isDefaultToolbar(defSplit, DEFAULT_TOOLBAR)
				|| isDefaultToolbar(defSplit, DEFAULT_TOOLBAR_3D);
	}

	final static private boolean isDefaultToolbar(String[] split, String[][] defaultToolbar) {
		if (split.length != defaultToolbar.length) {
			return false;
		}

		for (int i = 0; i < defaultToolbar.length; i++) {
			boolean found = false;
			String menu = split[i];
			String[] defaults = defaultToolbar[i];
			for (int j = 0; j < defaults.length && !found; j++) {
				if (defaults[j].equals(menu)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}

		return true;
	}

	final static private String[] split(String definition) {
		String def2 = definition.replaceAll(",", ""); // remove comas
		def2 = def2.replaceAll("59", ""); // remove record to spreadsheet tool
		def2 = def2.replaceAll("\\|{2,}", " \\| "); // remove double vertical
		// bars
		def2 = def2.replaceAll(" {2,}", " "); // remove multiple spaces

		return def2.split(" \\| "); // split by tool menus
	}
}
