package geogebra.mobile.utils;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.mobile.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Merging Modes from {@link EuclidianConstants} with their icon from {@link CommonResources} to one Enum ToolBarCommand.
 * @author Matthias Meisinger
 *
 */
public enum ToolBarCommand
{		
	// Movement
	Move(EuclidianConstants.MODE_MOVE, CommonResources.INSTANCE.move()),
	RecordToSpreadsheet(EuclidianConstants.MODE_RECORD_TO_SPREADSHEET, CommonResources.INSTANCE.record_to_spreadsheet()),
	RotateAroundPoint(EuclidianConstants.MODE_ROTATE_BY_ANGLE, CommonResources.INSTANCE.rotate_around_point()),
	
	// Point
	AttachDetachPoint(EuclidianConstants.MODE_ATTACH_DETACH, CommonResources.INSTANCE.attach_detach_point()),
	ComplexNumbers(EuclidianConstants.MODE_COMPLEX_NUMBER, CommonResources.INSTANCE.complex_number()),
	IntersectTwoObjects(EuclidianConstants.MODE_INTERSECT, CommonResources.INSTANCE.intersect_two_objects()),
	MidpointOrCenter(EuclidianConstants.MODE_MIDPOINT, CommonResources.INSTANCE.midpoint_or_center()),
	NewPoint(EuclidianConstants.MODE_POINT, CommonResources.INSTANCE.new_point()),
	PointOnObject(EuclidianConstants.MODE_POINT_ON_OBJECT, CommonResources.INSTANCE.point_on_object()),
	
	// Line
	LineThroughTwoPoints(EuclidianConstants.MODE_JOIN, CommonResources.INSTANCE.line_through_two_points()),
	PolylineBetweenPoints(EuclidianConstants.MODE_POLYLINE, CommonResources.INSTANCE.polyline_between_points()),
	RayThroughTwoPoints(EuclidianConstants.MODE_RAY, CommonResources.INSTANCE.ray_through_two_points()),
	SegmentBetweenTwoPoints(EuclidianConstants.MODE_SEGMENT, CommonResources.INSTANCE.segment_between_two_points()),
	SegmentWithGivenLengthFromPoint(EuclidianConstants.MODE_SEGMENT_FIXED, CommonResources.INSTANCE.segment_with_given_length_from_point()),
	VectorBetweenTwoPoints(EuclidianConstants.MODE_VECTOR, CommonResources.INSTANCE.vector_between_two_points()),
	VectorFromPoint(EuclidianConstants.MODE_VECTOR_FROM_POINT, CommonResources.INSTANCE.vector_from_point()),
	
	// Special Line
	AngleBisector(EuclidianConstants.MODE_ANGULAR_BISECTOR, CommonResources.INSTANCE.angle_bisector()),
	BestFitLine(EuclidianConstants.MODE_FITLINE, CommonResources.INSTANCE.best_fit_line()),
	Locus(EuclidianConstants.MODE_LOCUS, CommonResources.INSTANCE.locus()),
	ParallelLine(EuclidianConstants.MODE_PARALLEL, CommonResources.INSTANCE.parallel_line()),
	PerpendicularBisector(EuclidianConstants.MODE_LINE_BISECTOR, CommonResources.INSTANCE.perpendicular_bisector()),
	PerpendicularLine(EuclidianConstants.MODE_ORTHOGONAL, CommonResources.INSTANCE.perpendicular_line()),
	PolarOrDiameterLine(EuclidianConstants.MODE_POLAR_DIAMETER, CommonResources.INSTANCE.polar_or_diameter_line()),
	Tangents(EuclidianConstants.MODE_TANGENTS, CommonResources.INSTANCE.tangents()),
	
	// Polygon
	Polygon(EuclidianConstants.MODE_POLYGON, CommonResources.INSTANCE.polygon()),
	RegularPolygon(EuclidianConstants.MODE_REGULAR_POLYGON, CommonResources.INSTANCE.regular_polygon()),
	RigidPolygon(EuclidianConstants.MODE_RIGID_POLYGON, CommonResources.INSTANCE.rigid_polygon()),
	VectorPolygon(EuclidianConstants.MODE_VECTOR_POLYGON, CommonResources.INSTANCE.vector_polygon()),
	
	// Circle and Arc
	CircleThroughThreePoints(EuclidianConstants.MODE_CIRCLE_THREE_POINTS, CommonResources.INSTANCE.circle_through_three_points()),
	CircleWithCenterAndRadius(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS, CommonResources.INSTANCE.circle_with_center_and_radius()),
	CircleWithCenterThroughPoint(EuclidianConstants.MODE_CIRCLE_TWO_POINTS, CommonResources.INSTANCE.circle_with_center_through_point()),
	CircularArcWithCenterBetweenTwoPoints(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS, CommonResources.INSTANCE.circular_arc_with_center_between_two_points()),
	CircularSectorWithCenterBetweenTwoPoints(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS, CommonResources.INSTANCE.circular_sector_with_center_between_two_points()),
	CircumCirculuarArcThroughThreePoints(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS, CommonResources.INSTANCE.circumcircular_arc_through_three_points()),
	CircumCircularSectorThroughThreePoints(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS, CommonResources.INSTANCE.circumcircular_sector_through_three_points()),
	Compasses(EuclidianConstants.MODE_COMPASSES, CommonResources.INSTANCE.compasses()),
	Semicircle(EuclidianConstants.MODE_SEMICIRCLE, CommonResources.INSTANCE.semicircle()),
	
	// Conic Section
	ConicThroughFivePoints(EuclidianConstants.MODE_CONIC_FIVE_POINTS, CommonResources.INSTANCE.conic_through_5_points()),
	Ellipse(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS, CommonResources.INSTANCE.ellipse()),
	Hyperbola(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS, CommonResources.INSTANCE.hyperbola()),
	Parabola(EuclidianConstants.MODE_PARABOLA, CommonResources.INSTANCE.parabola()),
	
	// Measurement
	Angle(EuclidianConstants.MODE_ANGLE, CommonResources.INSTANCE.angle()),
	AngleWithGivenSize(EuclidianConstants.MODE_ANGLE_FIXED, CommonResources.INSTANCE.angle_with_given_size()),
	Area(EuclidianConstants.MODE_AREA, CommonResources.INSTANCE.area()),
	CreateList(EuclidianConstants.MODE_CREATE_LIST, CommonResources.INSTANCE.create_list()),
	DistanceOrLength(EuclidianConstants.MODE_DISTANCE, CommonResources.INSTANCE.distance_or_length()),
	Slope(EuclidianConstants.MODE_SLOPE, CommonResources.INSTANCE.slope()),
	
	// Transformation
	DilateObjectFromPointByFactor(EuclidianConstants.MODE_DILATE_FROM_POINT, CommonResources.INSTANCE.dilate_object_from_point_by_factor()),
	ReflectObjectAboutCircle(EuclidianConstants.MODE_MIRROR_AT_CIRCLE, CommonResources.INSTANCE.reflect_object_about_circle()),
	ReflectObjectAboutLine(EuclidianConstants.MODE_MIRROR_AT_LINE, CommonResources.INSTANCE.reflect_object_about_line()),
	ReflectObjectAboutPoint(EuclidianConstants.MODE_MIRROR_AT_POINT, CommonResources.INSTANCE.reflect_object_about_point()),
	RotateObjectAboutPointByAngle(EuclidianConstants.MODE_MOVE_ROTATE, CommonResources.INSTANCE.rotate_object_about_point_by_angle()),
	TranslateObjectByVector(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR, CommonResources.INSTANCE.translate_object_by_vector()),
	
	// Special Object
	FreehandShape(EuclidianConstants.MODE_FREEHAND_SHAPE, CommonResources.INSTANCE.freehand_shape()),
	FunctionInspector(EuclidianConstants.MODE_FUNCTION_INSPECTOR, CommonResources.INSTANCE.function_inspector()),
	InsertImage(EuclidianConstants.MODE_IMAGE, CommonResources.INSTANCE.insert_image()),
	InsertText(EuclidianConstants.MODE_TEXT, CommonResources.INSTANCE.insert_text()),
	Pen(EuclidianConstants.MODE_PEN, CommonResources.INSTANCE.pen()),
	ProbabilityCalculator(EuclidianConstants.MODE_PROBABILITY_CALCULATOR, CommonResources.INSTANCE.probability_calculator()),
	RelationBetweenTwoObjects(EuclidianConstants.MODE_RELATION, CommonResources.INSTANCE.relation_between_two_objects()),
	
	// Action Object
	CheckBoxToShowHideObjects(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX, CommonResources.INSTANCE.checkbox_to_show_hide_objects()),
	InsertButton(EuclidianConstants.MODE_BUTTON_ACTION, CommonResources.INSTANCE.insert_button()),
	InsertInputBox(EuclidianConstants.MODE_TEXTFIELD_ACTION, CommonResources.INSTANCE.insert_input_box()),
	Slider(EuclidianConstants.MODE_SLIDER, CommonResources.INSTANCE.slider()),
	
	// General Tools
	CopyVisualStyle(EuclidianConstants.MODE_COPY_VISUAL_STYLE, CommonResources.INSTANCE.copy_visual_style()),
	DeleteObject(EuclidianConstants.MODE_DELETE, CommonResources.INSTANCE.delete_object()),
	MoveGraphicsView(EuclidianConstants.MODE_TRANSLATEVIEW, CommonResources.INSTANCE.move_graphics_view()),
	ShowHideLabel(EuclidianConstants.MODE_SHOW_HIDE_LABEL, CommonResources.INSTANCE.show_hide_label()),
	ShowHideObject(EuclidianConstants.MODE_SHOW_HIDE_OBJECT, CommonResources.INSTANCE.show_hide_object()),
	ZoomIn(EuclidianConstants.MODE_ZOOM_IN, CommonResources.INSTANCE.zoom_in()),
	ZoomOut(EuclidianConstants.MODE_ZOOM_OUT, CommonResources.INSTANCE.zoom_out());

	int mode;
	SVGResource icon;

	ToolBarCommand(int mode, SVGResource icon)
	{		
		this.mode = mode;
		this.icon = icon;
	}
	

	/**
	 * The url used to link the resource
	 * @return "url(" + this.icon.getSafeUri().asString() + ")";	  
	 */
	public String getIconUrlAsString()
  {	
	  return	"url(" + this.icon.getSafeUri().asString() + ")";	  
  }
	
	/**
	 * 
	 * @return the Mode from {@link EuclidianConstants} of this ToolBarCommand
	 */
	public int getMode()
  {
	  return this.mode;
	  
  }
}

