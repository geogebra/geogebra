package geogebra.touch.utils;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.ToolbarResources;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.resources.client.ImageResource;


/**
 * Merging Modes from {@link EuclidianConstants} with their icon from
 * {@link DefaultResources} to one Enum ToolBarCommand.
 * 
 * @author Matthias Meisinger
 * 
 */
public enum ToolBarCommand {
	// Movement
	Move(EuclidianConstants.MODE_MOVE, toolIcons().move(), null), RecordToSpreadsheet(
			EuclidianConstants.MODE_RECORD_TO_SPREADSHEET, toolIcons()
					.record_to_spreadsheet(), null), RotateAroundPoint(
			EuclidianConstants.MODE_ROTATE_BY_ANGLE, toolIcons()
					.rotate_around_point(), null),

	// Point
	AttachDetachPoint(EuclidianConstants.MODE_ATTACH_DETACH, toolIcons()
			.attach_detach_point(), StyleBarDefaultSettings.Point), 
	ComplexNumbers(EuclidianConstants.MODE_COMPLEX_NUMBER, toolIcons()
			.complex_number(), StyleBarDefaultSettings.Point), 
	IntersectTwoObjects(EuclidianConstants.MODE_INTERSECT, toolIcons()
			.intersect_two_objects(), StyleBarDefaultSettings.DependentPoints), 
	MidpointOrCenter(EuclidianConstants.MODE_MIDPOINT, toolIcons().midpoint_or_center(),
			StyleBarDefaultSettings.DependentPoints), 
	NewPoint(EuclidianConstants.MODE_POINT, toolIcons().new_point(),StyleBarDefaultSettings.Point), 
	PointOnObject(EuclidianConstants.MODE_POINT_ON_OBJECT, toolIcons()
					.point_on_object(), StyleBarDefaultSettings.PointOnObject),

	// Line
	LineThroughTwoPoints(EuclidianConstants.MODE_JOIN, toolIcons()
			.line_through_two_points(), StyleBarDefaultSettings.Line), PolylineBetweenPoints(
			EuclidianConstants.MODE_POLYLINE, toolIcons()
					.polyline_between_points(), StyleBarDefaultSettings.Line), RayThroughTwoPoints(
			EuclidianConstants.MODE_RAY,
			toolIcons().ray_through_two_points(),
			StyleBarDefaultSettings.Line), SegmentBetweenTwoPoints(
			EuclidianConstants.MODE_SEGMENT, toolIcons()
					.segment_between_two_points(), StyleBarDefaultSettings.Line), VectorBetweenTwoPoints(
			EuclidianConstants.MODE_VECTOR, toolIcons()
					.vector_between_two_points(), StyleBarDefaultSettings.Line), VectorFromPoint(
			EuclidianConstants.MODE_VECTOR_FROM_POINT, toolIcons()
					.vector_from_point(), StyleBarDefaultSettings.Line),

	// Special Line
	AngleBisector(EuclidianConstants.MODE_ANGULAR_BISECTOR, toolIcons()
			.angle_bisector(), StyleBarDefaultSettings.Line), BestFitLine(
			EuclidianConstants.MODE_FITLINE, toolIcons().best_fit_line(),
			StyleBarDefaultSettings.Line), Locus(EuclidianConstants.MODE_LOCUS,
			toolIcons().locus(), StyleBarDefaultSettings.Line), ParallelLine(
			EuclidianConstants.MODE_PARALLEL, toolIcons().parallel_line(),
			StyleBarDefaultSettings.Line), PerpendicularBisector(
			EuclidianConstants.MODE_LINE_BISECTOR, toolIcons()
					.perpendicular_bisector(), StyleBarDefaultSettings.Line), PerpendicularLine(
			EuclidianConstants.MODE_ORTHOGONAL, toolIcons()
					.perpendicular_line(), StyleBarDefaultSettings.Line), PolarOrDiameterLine(
			EuclidianConstants.MODE_POLAR_DIAMETER, toolIcons()
					.polar_or_diameter_line(), StyleBarDefaultSettings.Line), Tangents(
			EuclidianConstants.MODE_TANGENTS, toolIcons().tangents(),
			StyleBarDefaultSettings.Line),

	// Polygon
	Polygon(EuclidianConstants.MODE_POLYGON, toolIcons().polygon(),
			StyleBarDefaultSettings.Polygon), RegularPolygon(
			EuclidianConstants.MODE_REGULAR_POLYGON, toolIcons()
					.regular_polygon(), StyleBarDefaultSettings.Polygon), RigidPolygon(
			EuclidianConstants.MODE_RIGID_POLYGON, toolIcons()
					.rigid_polygon(), StyleBarDefaultSettings.Polygon), VectorPolygon(
			EuclidianConstants.MODE_VECTOR_POLYGON, toolIcons()
					.vector_polygon(), StyleBarDefaultSettings.Polygon),

	// Circle and Arc
	CircleThroughThreePoints(EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
			toolIcons().circle_through_three_points(),
			StyleBarDefaultSettings.Line), CircleWithCenterThroughPoint(
			EuclidianConstants.MODE_CIRCLE_TWO_POINTS, toolIcons()
					.circle_with_center_through_point(),
			StyleBarDefaultSettings.Line), CircularArcWithCenterBetweenTwoPoints(
			EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS, toolIcons()
					.circular_arc_with_center_between_two_points(),
			StyleBarDefaultSettings.Line), CircularSectorWithCenterBetweenTwoPoints(
			EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS, toolIcons()
					.circular_sector_with_center_between_two_points(),
			StyleBarDefaultSettings.Line), CircumCirculuarArcThroughThreePoints(
			EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
			toolIcons().circumcircular_arc_through_three_points(),
			StyleBarDefaultSettings.Line), CircumCircularSectorThroughThreePoints(
			EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
			toolIcons().circumcircular_sector_through_three_points(),
			StyleBarDefaultSettings.Line), Compasses(
			EuclidianConstants.MODE_COMPASSES, toolIcons().compasses(),
			StyleBarDefaultSettings.Line), Semicircle(
			EuclidianConstants.MODE_SEMICIRCLE, toolIcons().semicircle(),
			StyleBarDefaultSettings.Line),

	// Conic Section
	ConicThroughFivePoints(EuclidianConstants.MODE_CONIC_FIVE_POINTS,
			toolIcons().conic_through_5_points(),
			StyleBarDefaultSettings.Line), Ellipse(
			EuclidianConstants.MODE_ELLIPSE_THREE_POINTS, toolIcons()
					.ellipse(), StyleBarDefaultSettings.Line), Hyperbola(
			EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS, toolIcons()
					.hyperbola(), StyleBarDefaultSettings.Line), Parabola(
			EuclidianConstants.MODE_PARABOLA, toolIcons().parabola(),
			StyleBarDefaultSettings.Line),

	// Measurement
	Angle(EuclidianConstants.MODE_ANGLE, toolIcons().angle(),
			StyleBarDefaultSettings.Angle), Area(EuclidianConstants.MODE_AREA,
			toolIcons().area(), null), CreateList(
			EuclidianConstants.MODE_CREATE_LIST, toolIcons().create_list(),
			null), DistanceOrLength(EuclidianConstants.MODE_DISTANCE,
			toolIcons().distance_or_length(), null), Slope(
			EuclidianConstants.MODE_SLOPE, toolIcons().slope(), null),

	// Transformation
	ReflectObjectAboutCircle(EuclidianConstants.MODE_MIRROR_AT_CIRCLE,
			toolIcons().reflect_object_about_circle(), null), ReflectObjectAboutLine(
			EuclidianConstants.MODE_MIRROR_AT_LINE, toolIcons()
					.reflect_object_about_line(), null), ReflectObjectAboutPoint(
			EuclidianConstants.MODE_MIRROR_AT_POINT, toolIcons()
					.reflect_object_about_point(), null), RotateObjectByAngle(
			EuclidianConstants.MODE_ROTATE_BY_ANGLE, toolIcons()
					.rotate_object_about_point_by_angle(), null), TranslateObjectByVector(
			EuclidianConstants.MODE_TRANSLATE_BY_VECTOR, toolIcons()
					.translate_object_by_vector(), null), Dilate(
			EuclidianConstants.MODE_DILATE_FROM_POINT, toolIcons()
					.dilate_object_from_point_by_factor(), null),

	// Special Object
	FreehandShape(EuclidianConstants.MODE_FREEHAND_SHAPE, toolIcons()
			.freehand_shape(), StyleBarDefaultSettings.Line), InsertImage(
			EuclidianConstants.MODE_IMAGE, toolIcons().insert_image(), null), InsertText(
			EuclidianConstants.MODE_TEXT, toolIcons().insert_text(), null), Pen(
			EuclidianConstants.MODE_PEN, toolIcons().pen(),
			StyleBarDefaultSettings.Line), RelationBetweenTwoObjects(
			EuclidianConstants.MODE_RELATION, toolIcons()
					.relation_between_two_objects(), null),

	// Action Object
	CheckBoxToShowHideObjects(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
			toolIcons().checkbox_to_show_hide_objects(), null), InsertButton(
			EuclidianConstants.MODE_BUTTON_ACTION, toolIcons()
					.insert_button(), null), InsertInputBox(
			EuclidianConstants.MODE_TEXTFIELD_ACTION, toolIcons()
					.insert_input_box(), null), Slider(
			EuclidianConstants.MODE_SLIDER, toolIcons().slider(),
			StyleBarDefaultSettings.Line),

	// General Tools
	CopyVisualStyle(EuclidianConstants.MODE_COPY_VISUAL_STYLE, toolIcons()
			.copy_visual_style(), null), DeleteObject(
			EuclidianConstants.MODE_DELETE, toolIcons().delete_object(), null), MoveGraphicsView(
			EuclidianConstants.MODE_TRANSLATEVIEW, toolIcons()
					.move_graphics_view(), null), ShowHideLabel(
			EuclidianConstants.MODE_SHOW_HIDE_LABEL, toolIcons()
					.show_hide_label(), null), ShowHideObject(
			EuclidianConstants.MODE_SHOW_HIDE_OBJECT, toolIcons()
					.show_hide_object(), null), ZoomIn(
			EuclidianConstants.MODE_ZOOM_IN, toolIcons().zoom_in(), null), ZoomOut(
			EuclidianConstants.MODE_ZOOM_OUT, toolIcons().zoom_out(), null),

	// others
	Move_Mobile(EuclidianConstants.MODE_MOVE, getLafIcons()
			.arrow_cursor_finger(), StyleBarDefaultSettings.Move), 
	Select(-1,
			getLafIcons().arrow_cursor_grab(), StyleBarDefaultSettings.Move), CirclePointRadius(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS,
			toolIcons().circle_with_center_and_radius(), StyleBarDefaultSettings.Line),
	SegmentFixed(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS,
						toolIcons().segment_with_given_length_from_point(), StyleBarDefaultSettings.Line),
	AngleFixed(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS,
									toolIcons().angle_fixed(), StyleBarDefaultSettings.Line);

	private static ToolbarResources toolIcons() {
		return ToolbarResources.INSTANCE;
	}
	private static DefaultResources getLafIcons() {
		return TouchEntryPoint.getLookAndFeel().getIcons();
	}

	private int mode;
	private ImageResource icon;

	private StyleBarDefaultSettings entries;

	ToolBarCommand(final int mode, final ImageResource icon,
			final StyleBarDefaultSettings stylingBarEntries) {
		this.mode = mode;
		this.icon = icon;
		this.entries = stylingBarEntries;
	}

	public ImageResource getIcon() {
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
