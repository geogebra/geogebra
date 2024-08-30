package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGLE_FIXED;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGULAR_BISECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AREA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_POINT_RADIUS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_TWO_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_COMPASSES;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CONIC_FIVE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DILATE_FROM_POINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DISTANCE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_FREEHAND_SHAPE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_IMAGE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_LINE_BISECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_LOCUS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIDPOINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_CIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_LINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_POINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE_ROTATE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ORTHOGONAL;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PARABOLA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PARALLEL;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLAR_DIAMETER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLYGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLYLINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RAY;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_REGULAR_POLYGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RELATION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RIGID_POLYGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEGMENT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEGMENT_FIXED;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEMICIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_LINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TEXT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR_FROM_POINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR_POLYGON;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;

public final class IBExamRestrictions extends ExamRestrictions {

	IBExamRestrictions() {
		super(ExamType.IB,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.G3D),
				SuiteSubApp.GRAPHING,
				null,
				null,
				null,
				null,
				null,
				null,
				IBExamRestrictions.createToolCollectionFilter(),
				null);
	}

	private static ToolCollectionFilter createToolCollectionFilter() {
		return new ToolCollectionSetFilter(MODE_IMAGE, MODE_TEXT, MODE_ANGLE, MODE_DISTANCE,
				MODE_AREA, MODE_ANGLE_FIXED, MODE_MIDPOINT, MODE_ORTHOGONAL, MODE_LINE_BISECTOR,
				MODE_PARALLEL, MODE_ANGULAR_BISECTOR, MODE_LOCUS, MODE_SEGMENT, MODE_SHAPE_LINE,
				MODE_RAY, MODE_VECTOR, MODE_SEGMENT_FIXED, MODE_VECTOR_FROM_POINT,
				MODE_POLAR_DIAMETER, MODE_POLYLINE, MODE_POLYGON, MODE_REGULAR_POLYGON,
				MODE_VECTOR_POLYGON, MODE_RIGID_POLYGON, MODE_CIRCLE_TWO_POINTS, MODE_COMPASSES,
				MODE_SEMICIRCLE, MODE_CIRCLE_POINT_RADIUS, MODE_CIRCLE_THREE_POINTS,
				MODE_CIRCLE_ARC_THREE_POINTS, MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
				MODE_CIRCLE_SECTOR_THREE_POINTS, MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
				MODE_ELLIPSE_THREE_POINTS, MODE_CONIC_FIVE_POINTS, MODE_PARABOLA,
				MODE_HYPERBOLA_THREE_POINTS, MODE_MIRROR_AT_LINE, MODE_MIRROR_AT_POINT,
				MODE_TRANSLATE_BY_VECTOR, MODE_MOVE_ROTATE, MODE_DILATE_FROM_POINT,
				MODE_MIRROR_AT_CIRCLE, MODE_FREEHAND_SHAPE, MODE_RELATION);
	}
}
