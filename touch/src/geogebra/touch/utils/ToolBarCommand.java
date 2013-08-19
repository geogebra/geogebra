package geogebra.touch.utils;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.DefaultResources;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Merging Modes from {@link EuclidianConstants} with their icon from
 * {@link DefaultResources} to one Enum ToolBarCommand.
 * 
 * @author Matthias Meisinger
 * 
 */
public enum ToolBarCommand {
	// Movement
	Move(EuclidianConstants.MODE_MOVE, getLafIcons().move(), null), RecordToSpreadsheet(
			EuclidianConstants.MODE_RECORD_TO_SPREADSHEET, getLafIcons()
					.record_to_spreadsheet(), null), RotateAroundPoint(
			EuclidianConstants.MODE_ROTATE_BY_ANGLE, getLafIcons()
					.rotate_around_point(), null),

	// Point
	AttachDetachPoint(EuclidianConstants.MODE_ATTACH_DETACH, getLafIcons()
			.attach_detach_point(), StyleBarDefaultSettings.Point), ComplexNumbers(
			EuclidianConstants.MODE_COMPLEX_NUMBER, getLafIcons()
					.complex_number(), StyleBarDefaultSettings.Point), IntersectTwoObjects(
			EuclidianConstants.MODE_INTERSECT, getLafIcons()
					.intersect_two_objects(),
			StyleBarDefaultSettings.DependentPoints), MidpointOrCenter(
			EuclidianConstants.MODE_MIDPOINT, getLafIcons()
					.midpoint_or_center(),
			StyleBarDefaultSettings.DependentPoints), NewPoint(
			EuclidianConstants.MODE_POINT, getLafIcons().new_point(),
			StyleBarDefaultSettings.Point), PointOnObject(
			EuclidianConstants.MODE_POINT_ON_OBJECT, getLafIcons()
					.point_on_object(), StyleBarDefaultSettings.Point),

	// Line
	LineThroughTwoPoints(EuclidianConstants.MODE_JOIN, getLafIcons()
			.line_through_two_points(), StyleBarDefaultSettings.Line), PolylineBetweenPoints(
			EuclidianConstants.MODE_POLYLINE, getLafIcons()
					.polyline_between_points(), StyleBarDefaultSettings.Line), RayThroughTwoPoints(
			EuclidianConstants.MODE_RAY,
			getLafIcons().ray_through_two_points(),
			StyleBarDefaultSettings.Line), SegmentBetweenTwoPoints(
			EuclidianConstants.MODE_SEGMENT, getLafIcons()
					.segment_between_two_points(), StyleBarDefaultSettings.Line), VectorBetweenTwoPoints(
			EuclidianConstants.MODE_VECTOR, getLafIcons()
					.vector_between_two_points(), StyleBarDefaultSettings.Line), VectorFromPoint(
			EuclidianConstants.MODE_VECTOR_FROM_POINT, getLafIcons()
					.vector_from_point(), StyleBarDefaultSettings.Line),

	// Special Line
	AngleBisector(EuclidianConstants.MODE_ANGULAR_BISECTOR, getLafIcons()
			.angle_bisector(), StyleBarDefaultSettings.Line), BestFitLine(
			EuclidianConstants.MODE_FITLINE, getLafIcons().best_fit_line(),
			StyleBarDefaultSettings.Line), Locus(EuclidianConstants.MODE_LOCUS,
			getLafIcons().locus(), StyleBarDefaultSettings.Line), ParallelLine(
			EuclidianConstants.MODE_PARALLEL, getLafIcons().parallel_line(),
			StyleBarDefaultSettings.Line), PerpendicularBisector(
			EuclidianConstants.MODE_LINE_BISECTOR, getLafIcons()
					.perpendicular_bisector(), StyleBarDefaultSettings.Line), PerpendicularLine(
			EuclidianConstants.MODE_ORTHOGONAL, getLafIcons()
					.perpendicular_line(), StyleBarDefaultSettings.Line), PolarOrDiameterLine(
			EuclidianConstants.MODE_POLAR_DIAMETER, getLafIcons()
					.polar_or_diameter_line(), StyleBarDefaultSettings.Line), Tangents(
			EuclidianConstants.MODE_TANGENTS, getLafIcons().tangents(),
			StyleBarDefaultSettings.Line),

	// Polygon
	Polygon(EuclidianConstants.MODE_POLYGON, getLafIcons().polygon(),
			StyleBarDefaultSettings.Polygon), RegularPolygon(
			EuclidianConstants.MODE_REGULAR_POLYGON, getLafIcons()
					.regular_polygon(), StyleBarDefaultSettings.Polygon), RigidPolygon(
			EuclidianConstants.MODE_RIGID_POLYGON, getLafIcons()
					.rigid_polygon(), StyleBarDefaultSettings.Polygon), VectorPolygon(
			EuclidianConstants.MODE_VECTOR_POLYGON, getLafIcons()
					.vector_polygon(), StyleBarDefaultSettings.Polygon),

	// Circle and Arc
	CircleThroughThreePoints(EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
			getLafIcons().circle_through_three_points(),
			StyleBarDefaultSettings.Line), CircleWithCenterThroughPoint(
			EuclidianConstants.MODE_CIRCLE_TWO_POINTS, getLafIcons()
					.circle_with_center_through_point(),
			StyleBarDefaultSettings.Line), CircularArcWithCenterBetweenTwoPoints(
			EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS, getLafIcons()
					.circular_arc_with_center_between_two_points(),
			StyleBarDefaultSettings.Line), CircularSectorWithCenterBetweenTwoPoints(
			EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS, getLafIcons()
					.circular_sector_with_center_between_two_points(),
			StyleBarDefaultSettings.Line), CircumCirculuarArcThroughThreePoints(
			EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
			getLafIcons().circumcircular_arc_through_three_points(),
			StyleBarDefaultSettings.Line), CircumCircularSectorThroughThreePoints(
			EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
			getLafIcons().circumcircular_sector_through_three_points(),
			StyleBarDefaultSettings.Line), Compasses(
			EuclidianConstants.MODE_COMPASSES, getLafIcons().compasses(),
			StyleBarDefaultSettings.Line), Semicircle(
			EuclidianConstants.MODE_SEMICIRCLE, getLafIcons().semicircle(),
			StyleBarDefaultSettings.Line),

	// Conic Section
	ConicThroughFivePoints(EuclidianConstants.MODE_CONIC_FIVE_POINTS,
			getLafIcons().conic_through_5_points(),
			StyleBarDefaultSettings.Line), Ellipse(
			EuclidianConstants.MODE_ELLIPSE_THREE_POINTS, getLafIcons()
					.ellipse(), StyleBarDefaultSettings.Line), Hyperbola(
			EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS, getLafIcons()
					.hyperbola(), StyleBarDefaultSettings.Line), Parabola(
			EuclidianConstants.MODE_PARABOLA, getLafIcons().parabola(),
			StyleBarDefaultSettings.Line),

	// Measurement
	Angle(EuclidianConstants.MODE_ANGLE, getLafIcons().angle(),
			StyleBarDefaultSettings.Angle), Area(EuclidianConstants.MODE_AREA,
			getLafIcons().area(), null), CreateList(
			EuclidianConstants.MODE_CREATE_LIST, getLafIcons().create_list(),
			null), DistanceOrLength(EuclidianConstants.MODE_DISTANCE,
			getLafIcons().distance_or_length(), null), Slope(
			EuclidianConstants.MODE_SLOPE, getLafIcons().slope(), null),

	// Transformation
	ReflectObjectAboutCircle(EuclidianConstants.MODE_MIRROR_AT_CIRCLE,
			getLafIcons().reflect_object_about_circle(), null), ReflectObjectAboutLine(
			EuclidianConstants.MODE_MIRROR_AT_LINE, getLafIcons()
					.reflect_object_about_line(), null), ReflectObjectAboutPoint(
			EuclidianConstants.MODE_MIRROR_AT_POINT, getLafIcons()
					.reflect_object_about_point(), null), RotateObjectByAngle(
			EuclidianConstants.MODE_ROTATE_BY_ANGLE, getLafIcons()
					.rotate_object_about_point_by_angle(), null), TranslateObjectByVector(
			EuclidianConstants.MODE_TRANSLATE_BY_VECTOR, getLafIcons()
					.translate_object_by_vector(), null), Dilate(
			EuclidianConstants.MODE_DILATE_FROM_POINT, getLafIcons()
					.dilate_object_from_point_by_factor(), null),

	// Special Object
	FreehandShape(EuclidianConstants.MODE_FREEHAND_SHAPE, getLafIcons()
			.freehand_shape(), StyleBarDefaultSettings.Line), InsertImage(
			EuclidianConstants.MODE_IMAGE, getLafIcons().insert_image(), null), InsertText(
			EuclidianConstants.MODE_TEXT, getLafIcons().insert_text(), null), Pen(
			EuclidianConstants.MODE_PEN, getLafIcons().pen(),
			StyleBarDefaultSettings.Line), RelationBetweenTwoObjects(
			EuclidianConstants.MODE_RELATION, getLafIcons()
					.relation_between_two_objects(), null),

	// Action Object
	CheckBoxToShowHideObjects(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
			getLafIcons().checkbox_to_show_hide_objects(), null), InsertButton(
			EuclidianConstants.MODE_BUTTON_ACTION, getLafIcons()
					.insert_button(), null), InsertInputBox(
			EuclidianConstants.MODE_TEXTFIELD_ACTION, getLafIcons()
					.insert_input_box(), null), Slider(
			EuclidianConstants.MODE_SLIDER, getLafIcons().slider(),
			StyleBarDefaultSettings.Line),

	// General Tools
	CopyVisualStyle(EuclidianConstants.MODE_COPY_VISUAL_STYLE, getLafIcons()
			.copy_visual_style(), null), DeleteObject(
			EuclidianConstants.MODE_DELETE, getLafIcons().delete_object(), null), MoveGraphicsView(
			EuclidianConstants.MODE_TRANSLATEVIEW, getLafIcons()
					.move_graphics_view(), null), ShowHideLabel(
			EuclidianConstants.MODE_SHOW_HIDE_LABEL, getLafIcons()
					.show_hide_label(), null), ShowHideObject(
			EuclidianConstants.MODE_SHOW_HIDE_OBJECT, getLafIcons()
					.show_hide_object(), null), ZoomIn(
			EuclidianConstants.MODE_ZOOM_IN, getLafIcons().zoom_in(), null), ZoomOut(
			EuclidianConstants.MODE_ZOOM_OUT, getLafIcons().zoom_out(), null),

	// others
	Move_Mobile(EuclidianConstants.MODE_MOVE, getLafIcons()
			.arrow_cursor_finger(), StyleBarDefaultSettings.Move), Select(-1,
			getLafIcons().arrow_cursor_grab(), StyleBarDefaultSettings.Move);

	private static DefaultResources getLafIcons() {
		return TouchEntryPoint.getLookAndFeel().getIcons();
	}

	private int mode;
	private SVGResource icon;

	private StyleBarDefaultSettings entries;

	ToolBarCommand(final int mode, final SVGResource icon,
			final StyleBarDefaultSettings stylingBarEntries) {
		this.mode = mode;
		this.icon = icon;
		this.entries = stylingBarEntries;
	}

	public SVGResource getIcon() {
		return this.icon;
	}

	/**
	 * 
	 * @return the Mode from {@link EuclidianConstants} of this ToolBarCommand
	 */
	public int getMode() {
		return this.mode;

	}

	public StyleBarDefaultSettings getStyleBarEntries() {
		return this.entries;
	}
}
