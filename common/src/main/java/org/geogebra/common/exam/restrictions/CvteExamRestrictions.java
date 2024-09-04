package org.geogebra.common.exam.restrictions;

import java.util.Set;

import javax.annotation.Nullable;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.cvte.CvteCommandArgumentFilter;
import org.geogebra.common.exam.restrictions.cvte.CvteSyntaxFilter;
import org.geogebra.common.exam.restrictions.cvte.MatrixExpressionFilter;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationExpressionFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.commands.selector.EnglishCommandFilter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.PropertiesRegistry;

final class CvteExamRestrictions extends ExamRestrictions {

	private boolean casEnabled = true;

	CvteExamRestrictions() {
		super(ExamType.CVTE,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.G3D, SuiteSubApp.GEOMETRY,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.GRAPHING,
				CvteExamRestrictions.createFeatureRestrictions(),
				CvteExamRestrictions.createInputExpressionFilters(),
				CvteExamRestrictions.createOutputExpressionFilters(),
				CvteExamRestrictions.createCommandFilters(),
				CvteExamRestrictions.createCommandArgumentFilters(),
				CvteExamRestrictions.getFilteredOperations(),
				CvteExamRestrictions.createSyntaxFilter(),
				CvteExamRestrictions.createToolsFilter(),
				null);
	}

	@Override
	public void applyTo(@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider) {
		if (settings != null) {
			casEnabled = settings.getCasSettings().isEnabled();
			// Note: The effect we want to acchieve here is disable the symbolic versions of the
			// Derivative and Integral commands, and replace them on the fly with their numeric
			// counterparts (a requirement of APPS-4871/APPS-4961). This behavior is
			// implemented using the "CAS enabled" flag (deep inside the algebra processor and
			// other places).
			// Careful: setting the "CAS enabled" setting to false here only makes because
			// CvTE exam is restricted to the Graphing subapp of Suite, and the Graphing
			// standalone app disables CAS, but it's enabled in Suite (see app config).
			settings.getCasSettings().setEnabled(false);
		}
		super.applyTo(commandDispatcher, algebraProcessor, propertiesRegistry, context,
				localization, settings, autoCompleteProvider, toolsProvider);
	}

	@Override
	public void removeFrom(@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider) {
		super.removeFrom(commandDispatcher, algebraProcessor, propertiesRegistry, context,
				localization, settings, autoCompleteProvider, toolsProvider);
		if (settings != null) {
			settings.getCasSettings().setEnabled(casEnabled);
		}
	}

	private static Set<ExamFeatureRestriction> createFeatureRestrictions() {
		return Set.of(ExamFeatureRestriction.AUTOMATIC_GRAPH_SELECTION_FOR_FUNCTIONS);
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
				Commands.Tangent,
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

	private static Set<Operation> getFilteredOperations() {
		return Set.of(Operation.CONJUGATE,
				Operation.FRACTIONAL_PART,
				Operation.GAMMA,
				Operation.GAMMA_INCOMPLETE,
				Operation.GAMMA_INCOMPLETE_REGULARIZED,
				Operation.POLYGAMMA,
				Operation.RANDOM);
	}

	private static ToolCollectionFilter createToolsFilter() {
		// Source: https://docs.google.com/spreadsheets/d/1xUnRbtDPGtODKcYhM4tx-uD4G8B1wcpGgSkT4iX8BJA/edit?gid=1199288464#gid=1199288464
		// note: this is the set of *excluded* tools
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

	private static Set<ExpressionFilter> createInputExpressionFilters() {
		return Set.of(new MatrixExpressionFilter(),
				new OperationExpressionFilter(getFilteredOperations()));
	}

	private static Set<ExpressionFilter> createOutputExpressionFilters() {
		return Set.of(new MatrixExpressionFilter());
	}
}
