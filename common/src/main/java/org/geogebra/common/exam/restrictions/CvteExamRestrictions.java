package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.commands.selector.EnglishCommandFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

public class CvteExamRestrictions extends ExamRestrictions {

	CvteExamRestrictions() {
		super(ExamType.CVTE,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.G3D, SuiteSubApp.GEOMETRY,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.CAS,
				null,
				null,
				CvteExamRestrictions.createCommandFilters(),
				CvteExamRestrictions.createCommandArgumentFilters(),
				CvteExamRestrictions.createSyntaxFilter(),
				CvteExamRestrictions.createToolsFilter(),
				null);
	}

	private static Set<CommandFilter> createCommandFilters() {
		// Source: https://docs.google.com/spreadsheets/d/1xUnRbtDPGtODKcYhM4tx-uD4G8B1wcpGgSkT4iX8BJA/edit?gid=215139506#gid=215139506
		// note: this is the set of *allowed* commands
		CommandNameFilter nameFilter = new CommandNameFilter(false,
				Commands.BinomialCoefficient,
				Commands.Circle,
				Commands.Curve,
				Commands.Delete,
				Commands.Derivative,
				Commands.Extremum,
				Commands.If,
				Commands.Integral,
				Commands.Intersect,
				Commands.Invert,
				Commands.Iteration,
				Commands.IterationList,
				Commands.Max,
				Commands.mean,
				Commands.Median,
				Commands.Min,
				Commands.Mode,
				Commands.nCr,
				Commands.NDerivative,
				Commands.NIntegral,
				Commands.nPr,
				Commands.Root,
				Commands.Roots,
				Commands.SampleSD,
				Commands.SampleSDX,
				Commands.SampleSDY,
				Commands.SampleVariance,
				Commands.SD,
				Commands.SDX,
				Commands.SDY,
				Commands.Sequence,
				Commands.Slider,
				Commands.stdev,
				Commands.stdevp,
				Commands.Sum,
				Commands.ZoomIn,
				Commands.ZoomOut);
		return Set.of(new EnglishCommandFilter(nameFilter));
	}

	private static Set<CommandArgumentFilter> createCommandArgumentFilters() {
		return Set.of(new CvteCommandArgumentFilter());
	}

	private static SyntaxFilter createSyntaxFilter() {
		return new CvteSyntaxFilter();
	}

	private static ToolCollectionFilter createToolsFilter() {
		// Source: https://docs.google.com/spreadsheets/d/1xUnRbtDPGtODKcYhM4tx-uD4G8B1wcpGgSkT4iX8BJA/edit?gid=1199288464#gid=1199288464
		return new ToolCollectionSetFilter(
				EuclidianConstants.MODE_POINT,
				EuclidianConstants.MODE_FITLINE,
				EuclidianConstants.MODE_IMAGE,
				EuclidianConstants.MODE_TEXT,
				EuclidianConstants.MODE_ANGLE,
				EuclidianConstants.MODE_DISTANCE,
				EuclidianConstants.MODE_AREA,
				EuclidianConstants.MODE_ANGLE_FIXED,
				EuclidianConstants.MODE_SLOPE,
				EuclidianConstants.MODE_POINT_ON_OBJECT,
				EuclidianConstants.MODE_ATTACH_DETACH,
				EuclidianConstants.MODE_COMPLEX_NUMBER,
				EuclidianConstants.MODE_CREATE_LIST,
				EuclidianConstants.MODE_MIDPOINT,
				EuclidianConstants.MODE_ORTHOGONAL,
				EuclidianConstants.MODE_LINE_BISECTOR,
				EuclidianConstants.MODE_PARALLEL,
				EuclidianConstants.MODE_ANGULAR_BISECTOR,
				EuclidianConstants.MODE_LOCUS,
				EuclidianConstants.MODE_SEGMENT,
				EuclidianConstants.MODE_JOIN,
				EuclidianConstants.MODE_RAY,
				EuclidianConstants.MODE_VECTOR,
				EuclidianConstants.MODE_SEGMENT_FIXED,
				EuclidianConstants.MODE_VECTOR_FROM_POINT,
				EuclidianConstants.MODE_POLAR_DIAMETER,
				EuclidianConstants.MODE_POLYLINE,
				EuclidianConstants.MODE_POLYGON,
				EuclidianConstants.MODE_REGULAR_POLYGON,
				EuclidianConstants.MODE_VECTOR_POLYGON,
				EuclidianConstants.MODE_RIGID_POLYGON,
				EuclidianConstants.MODE_CIRCLE_TWO_POINTS,
				EuclidianConstants.MODE_COMPASSES,
				EuclidianConstants.MODE_SEMICIRCLE,
				EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
				EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS,
				EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS,
				EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
				EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
				EuclidianConstants.MODE_ELLIPSE_THREE_POINTS,
				EuclidianConstants.MODE_CONIC_FIVE_POINTS,
				EuclidianConstants.MODE_PARABOLA,
				EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS,
				EuclidianConstants.MODE_MIRROR_AT_LINE,
				EuclidianConstants.MODE_MIRROR_AT_POINT,
				EuclidianConstants.MODE_TRANSLATE_BY_VECTOR,
				EuclidianConstants.MODE_ROTATE_BY_ANGLE,
				EuclidianConstants.MODE_DILATE_FROM_POINT,
				EuclidianConstants.MODE_MIRROR_AT_CIRCLE,
				EuclidianConstants.MODE_PEN,
				EuclidianConstants.MODE_FREEHAND_SHAPE,
				EuclidianConstants.MODE_RELATION,
				EuclidianConstants.MODE_BUTTON_ACTION,
				EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
				EuclidianConstants.MODE_TEXTFIELD_ACTION
		);
	}
}
