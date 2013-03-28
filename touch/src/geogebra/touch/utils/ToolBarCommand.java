package geogebra.touch.utils;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.touch.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Merging Modes from {@link EuclidianConstants} with their icon from
 * {@link CommonResources} to one Enum ToolBarCommand.
 * 
 * @author Matthias Meisinger
 * 
 */
public enum ToolBarCommand
{
	// Movement
	Move(EuclidianConstants.MODE_MOVE, CommonResources.INSTANCE.move(), null), RecordToSpreadsheet(
			EuclidianConstants.MODE_RECORD_TO_SPREADSHEET,
			CommonResources.INSTANCE.record_to_spreadsheet(), null), RotateAroundPoint(
			EuclidianConstants.MODE_ROTATE_BY_ANGLE, CommonResources.INSTANCE
					.rotate_around_point(), null),

	// Point
	AttachDetachPoint(EuclidianConstants.MODE_ATTACH_DETACH,
			CommonResources.INSTANCE.attach_detach_point(),
			StylingBarEntries.Point), ComplexNumbers(
			EuclidianConstants.MODE_COMPLEX_NUMBER, CommonResources.INSTANCE
					.complex_number(), StylingBarEntries.Point), IntersectTwoObjects(
			EuclidianConstants.MODE_INTERSECT, CommonResources.INSTANCE
					.intersect_two_objects(), StylingBarEntries.DependentPoints), MidpointOrCenter(
			EuclidianConstants.MODE_MIDPOINT, CommonResources.INSTANCE
					.midpoint_or_center(), StylingBarEntries.DependentPoints), NewPoint(
			EuclidianConstants.MODE_POINT,
			CommonResources.INSTANCE.new_point(), StylingBarEntries.Point), PointOnObject(
			EuclidianConstants.MODE_POINT_ON_OBJECT, CommonResources.INSTANCE
					.point_on_object(), StylingBarEntries.Point),

	// Line
	LineThroughTwoPoints(EuclidianConstants.MODE_JOIN, CommonResources.INSTANCE
			.line_through_two_points(), StylingBarEntries.Line), PolylineBetweenPoints(
			EuclidianConstants.MODE_POLYLINE, CommonResources.INSTANCE
					.polyline_between_points(), StylingBarEntries.Line), RayThroughTwoPoints(
			EuclidianConstants.MODE_RAY, CommonResources.INSTANCE
					.ray_through_two_points(), StylingBarEntries.Line), SegmentBetweenTwoPoints(
			EuclidianConstants.MODE_SEGMENT, CommonResources.INSTANCE
					.segment_between_two_points(), StylingBarEntries.Line), VectorBetweenTwoPoints(
			EuclidianConstants.MODE_VECTOR, CommonResources.INSTANCE
					.vector_between_two_points(), StylingBarEntries.Line), VectorFromPoint(
			EuclidianConstants.MODE_VECTOR_FROM_POINT, CommonResources.INSTANCE
					.vector_from_point(), StylingBarEntries.Line),

	// Special Line
	AngleBisector(EuclidianConstants.MODE_ANGULAR_BISECTOR,
			CommonResources.INSTANCE.angle_bisector(), StylingBarEntries.Line), BestFitLine(
			EuclidianConstants.MODE_FITLINE, CommonResources.INSTANCE
					.best_fit_line(), StylingBarEntries.Line), Locus(
			EuclidianConstants.MODE_LOCUS, CommonResources.INSTANCE.locus(),
			StylingBarEntries.Line), ParallelLine(
			EuclidianConstants.MODE_PARALLEL, CommonResources.INSTANCE
					.parallel_line(), StylingBarEntries.Line), PerpendicularBisector(
			EuclidianConstants.MODE_LINE_BISECTOR, CommonResources.INSTANCE
					.perpendicular_bisector(), StylingBarEntries.Line), PerpendicularLine(
			EuclidianConstants.MODE_ORTHOGONAL, CommonResources.INSTANCE
					.perpendicular_line(), StylingBarEntries.Line), PolarOrDiameterLine(
			EuclidianConstants.MODE_POLAR_DIAMETER, CommonResources.INSTANCE
					.polar_or_diameter_line(), StylingBarEntries.Line), Tangents(
			EuclidianConstants.MODE_TANGENTS, CommonResources.INSTANCE
					.tangents(), StylingBarEntries.Line),

	// Polygon
	Polygon(EuclidianConstants.MODE_POLYGON,
			CommonResources.INSTANCE.polygon(), StylingBarEntries.Polygon), RegularPolygon(
			EuclidianConstants.MODE_REGULAR_POLYGON, CommonResources.INSTANCE
					.regular_polygon(), StylingBarEntries.Polygon), RigidPolygon(
			EuclidianConstants.MODE_RIGID_POLYGON, CommonResources.INSTANCE
					.rigid_polygon(), StylingBarEntries.Polygon), VectorPolygon(
			EuclidianConstants.MODE_VECTOR_POLYGON, CommonResources.INSTANCE
					.vector_polygon(), StylingBarEntries.Polygon),

	// Circle and Arc
	CircleThroughThreePoints(EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
			CommonResources.INSTANCE.circle_through_three_points(),
			StylingBarEntries.Line), CircleWithCenterThroughPoint(
			EuclidianConstants.MODE_CIRCLE_TWO_POINTS, CommonResources.INSTANCE
					.circle_with_center_through_point(), StylingBarEntries.Line), CircularArcWithCenterBetweenTwoPoints(
			EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS,
			CommonResources.INSTANCE
					.circular_arc_with_center_between_two_points(),
			StylingBarEntries.Line), CircularSectorWithCenterBetweenTwoPoints(
			EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS,
			CommonResources.INSTANCE
					.circular_sector_with_center_between_two_points(),
			StylingBarEntries.Line), CircumCirculuarArcThroughThreePoints(
			EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
			CommonResources.INSTANCE.circumcircular_arc_through_three_points(),
			StylingBarEntries.Line), CircumCircularSectorThroughThreePoints(
			EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
			CommonResources.INSTANCE
					.circumcircular_sector_through_three_points(),
			StylingBarEntries.Line), Compasses(
			EuclidianConstants.MODE_COMPASSES, CommonResources.INSTANCE
					.compasses(), StylingBarEntries.Line), Semicircle(
			EuclidianConstants.MODE_SEMICIRCLE, CommonResources.INSTANCE
					.semicircle(), StylingBarEntries.Line),

	// Conic Section
	ConicThroughFivePoints(EuclidianConstants.MODE_CONIC_FIVE_POINTS,
			CommonResources.INSTANCE.conic_through_5_points(),
			StylingBarEntries.Line), Ellipse(
			EuclidianConstants.MODE_ELLIPSE_THREE_POINTS,
			CommonResources.INSTANCE.ellipse(), StylingBarEntries.Line), Hyperbola(
			EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS,
			CommonResources.INSTANCE.hyperbola(), StylingBarEntries.Line), Parabola(
			EuclidianConstants.MODE_PARABOLA, CommonResources.INSTANCE
					.parabola(), StylingBarEntries.Line),

	// Measurement
	Angle(EuclidianConstants.MODE_ANGLE, CommonResources.INSTANCE.angle(), null), Area(
			EuclidianConstants.MODE_AREA, CommonResources.INSTANCE.area(), null), CreateList(
			EuclidianConstants.MODE_CREATE_LIST, CommonResources.INSTANCE
					.create_list(), null), DistanceOrLength(
			EuclidianConstants.MODE_DISTANCE, CommonResources.INSTANCE
					.distance_or_length(), null), Slope(
			EuclidianConstants.MODE_SLOPE, CommonResources.INSTANCE.slope(),
			null),

	// Transformation
	ReflectObjectAboutCircle(EuclidianConstants.MODE_MIRROR_AT_CIRCLE,
			CommonResources.INSTANCE.reflect_object_about_circle(), null), ReflectObjectAboutLine(
			EuclidianConstants.MODE_MIRROR_AT_LINE, CommonResources.INSTANCE
					.reflect_object_about_line(), null), ReflectObjectAboutPoint(
			EuclidianConstants.MODE_MIRROR_AT_POINT, CommonResources.INSTANCE
					.reflect_object_about_point(), null), RotateObjectByAngle(
			EuclidianConstants.MODE_ROTATE_BY_ANGLE, CommonResources.INSTANCE
					.rotate_object_about_point_by_angle(), null), TranslateObjectByVector(
			EuclidianConstants.MODE_TRANSLATE_BY_VECTOR,
			CommonResources.INSTANCE.translate_object_by_vector(), null),

	// Special Object
	FreehandShape(EuclidianConstants.MODE_FREEHAND_SHAPE,
			CommonResources.INSTANCE.freehand_shape(), null), InsertImage(
			EuclidianConstants.MODE_IMAGE, CommonResources.INSTANCE
					.insert_image(), null), InsertText(
			EuclidianConstants.MODE_TEXT, CommonResources.INSTANCE
					.insert_text(), null), Pen(EuclidianConstants.MODE_PEN,
			CommonResources.INSTANCE.pen(), StylingBarEntries.Line), RelationBetweenTwoObjects(
			EuclidianConstants.MODE_RELATION, CommonResources.INSTANCE
					.relation_between_two_objects(), null),

	// Action Object
	CheckBoxToShowHideObjects(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
			CommonResources.INSTANCE.checkbox_to_show_hide_objects(), null), InsertButton(
			EuclidianConstants.MODE_BUTTON_ACTION, CommonResources.INSTANCE
					.insert_button(), null), InsertInputBox(
			EuclidianConstants.MODE_TEXTFIELD_ACTION, CommonResources.INSTANCE
					.insert_input_box(), null), Slider(
			EuclidianConstants.MODE_SLIDER, CommonResources.INSTANCE.slider(),
			StylingBarEntries.Line),

	// General Tools
	CopyVisualStyle(EuclidianConstants.MODE_COPY_VISUAL_STYLE,
			CommonResources.INSTANCE.copy_visual_style(), null), DeleteObject(
			EuclidianConstants.MODE_DELETE, CommonResources.INSTANCE
					.delete_object(), null), MoveGraphicsView(
			EuclidianConstants.MODE_TRANSLATEVIEW, CommonResources.INSTANCE
					.move_graphics_view(), null), ShowHideLabel(
			EuclidianConstants.MODE_SHOW_HIDE_LABEL, CommonResources.INSTANCE
					.show_hide_label(), null), ShowHideObject(
			EuclidianConstants.MODE_SHOW_HIDE_OBJECT, CommonResources.INSTANCE
					.show_hide_object(), null), ZoomIn(
			EuclidianConstants.MODE_ZOOM_IN,
			CommonResources.INSTANCE.zoom_in(), null), ZoomOut(
			EuclidianConstants.MODE_ZOOM_OUT, CommonResources.INSTANCE
					.zoom_out(), null),

	// others
	Move_Mobile(EuclidianConstants.MODE_MOVE, CommonResources.INSTANCE
			.arrow_cursor_finger(), null), Select(-1, CommonResources.INSTANCE
			.arrow_cursor_grab(), null);

	int mode;
	SVGResource icon;
	StylingBarEntries entries;

	ToolBarCommand(int mode, SVGResource icon,
			StylingBarEntries stylingBarEntries)
	{
		this.mode = mode;
		this.icon = icon;
		this.entries = stylingBarEntries;
	}

	/**
	 * 
	 * @return the Mode from {@link EuclidianConstants} of this ToolBarCommand
	 */
	public int getMode()
	{
		return this.mode;

	}

	public StylingBarEntries getStylingBarEntries()
	{
		return this.entries;
	}

	public SVGResource getIcon()
  {
	  return this.icon;
  }
}
