package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Regression;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics1;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics2;

import java.util.Set;

import javax.annotation.Nullable;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.cvte.CvteCommandArgumentFilter;
import org.geogebra.common.exam.restrictions.cvte.CvteSyntaxFilter;
import org.geogebra.common.exam.restrictions.cvte.MatrixExpressionFilter;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ScheduledPreviewFromInputBar;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationExpressionFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.ConstructionElementSetup;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;

public final class CvteExamRestrictions extends ExamRestrictions {

	private boolean casEnabled = true;

	CvteExamRestrictions() {
		super(ExamType.CVTE,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.G3D, SuiteSubApp.GEOMETRY,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.GRAPHING,
				createFeatureRestrictions(),
				createInputExpressionFilters(),
				createOutputExpressionFilters(),
				createCommandFilters(),
				createCommandArgumentFilters(),
				getFilteredOperations(),
				createContextMenuItemFilters(),
				createSyntaxFilter(),
				createToolsFilter(),
				null,
				createPropertyFilters(),
				createConstructionElementSetups());
	}

	@Override
	public void applyTo(
			@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider,
			@Nullable GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nullable Construction construction,
			@Nullable ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
			@Nullable ContextMenuFactory contextMenuFactory) {
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
				localization, settings, autoCompleteProvider, toolsProvider,
				geoElementPropertiesFactory, construction, scheduledPreviewFromInputBar,
				contextMenuFactory);
	}

	@Override
	public void removeFrom(
			@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context,
			@Nullable Localization localization,
			@Nullable Settings settings,
			@Nullable AutocompleteProvider autoCompleteProvider,
			@Nullable ToolsProvider toolsProvider,
			@Nullable GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nullable Construction construction,
			@Nullable ScheduledPreviewFromInputBar scheduledPreviewFromInputBar,
			@Nullable ContextMenuFactory contextMenuFactory) {
		super.removeFrom(commandDispatcher, algebraProcessor, propertiesRegistry, context,
				localization, settings, autoCompleteProvider, toolsProvider,
				geoElementPropertiesFactory, construction, scheduledPreviewFromInputBar,
				contextMenuFactory);
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
				Commands.CurveCartesian,
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
		return Set.of(nameFilter);
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

	private static Set<ContextMenuItemFilter> createContextMenuItemFilters() {
		return Set.of(contextMenuItem -> Set.of(Statistics1, Statistics2, Regression).stream()
				.noneMatch(item -> item.isSameItemAs(contextMenuItem)));
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

	private static Set<GeoElementPropertyFilter> createPropertyFilters() {
		return Set.of(new ShowObjectPropertyFilter());
	}

	private static Set<ConstructionElementSetup> createConstructionElementSetups() {
		return Set.of(new EuclidianVisibilitySetup());
	}

	private static final class ShowObjectPropertyFilter implements GeoElementPropertyFilter {
		@Override
		public boolean isAllowed(Property property, GeoElement geoElement) {
			if (property instanceof ShowObjectProperty) {
				return isVisibilityEnabled(geoElement);
			}
			return true;
		}
	}

	private static final class EuclidianVisibilitySetup implements ConstructionElementSetup {
		@Override
		public void applyTo(ConstructionElement constructionElement) {
			if (constructionElement instanceof GeoElement) {
				GeoElement geoElement = (GeoElement) constructionElement;

				if (!isVisibilityEnabled(geoElement)) {
					geoElement.setRestrictedEuclidianVisibility(true);
				}
			}
		}
	}

	/**
	 * Determines whether the visibility of a {@code GeoElement} is enabled during CVTE exam.
	 * <p>
	 * This method is used to decide whether an element's visibility is restricted during CVTE exam.
	 * <p>
	 * If the visibility is enabled, it means that nothing should change after entering exam mode.
	 * <p>
	 * If the visibility is restricted, it means that
	 * the element should never be shown in the Euclidean view,
	 * it shouldn't have a show object property in its settings.
	 * and the visibility toggle button should be disabled in the Algebra view.
	 * @param geoElement the {@code GeoElement} to evaluate
	 * @return {@code true} if the visibility is enabled, {@code false} if it is restricted.
	 */
	@SuppressWarnings({"PMD.SimplifyBooleanReturns", "checkstyle:RegexpSinglelineCheck"})
	public static boolean isVisibilityEnabled(GeoElement geoElement) {
		// Allow explicit equations
		// E.g.: y = 2x
		//       y = 5
		//       y = x^2
		//       y = x^3
		//       y = x^2 - 5x + 2
		if (isExplicitEquation(geoElement)) {
			return true;
		}

		// Allow circles created by "Circle(<Center>, <Radius>)" command
		// or "Circle: Center & Radius" tool
		// E.g.: Circle((0, 0), 2)
		//       Circle(A, 4)
		if (geoElement.getParentAlgorithm() instanceof AlgoCirclePointRadius) {
			return true;
		}

		// Restrict the visibility of any other conic
		// E.g.: x^2 + y^2 = 4
		//       x^2 / 9 + x^2 / 4 = 1
		//       x^2 - y^2 = 4
		if (geoElement.isGeoConic()) {
			return false;
		}

		// Allow linear equations
		// E.g.: x = 0
		//       x + y = 0
		//       2x - 3y = 4
		//       x = y
		//       2x = y
		//       y = 2x
		if (isLinearEquation(geoElement)) {
			return true;
		}

		// Restrict the visibility of any other equation
		// E.g.: x^2 = 0
		//       x^2 = 1
		//       2^x = 0
		//       sin(x) = 0
		//       ln(x) = 0
		//       |x - 3| = 0
		//       x^2 = y
		//       x^3 = y
		//       y^2 = x
		//       y^3 = x
		//       x^3 + y^2 = 2
		if (isEquation(geoElement)) {
			return false;
		}
		
		return true;
	}

	@Nullable
	private static String unwrapVariable(ExpressionValue expressionValue) {
		if (expressionValue instanceof FunctionVariable) {
			return ((FunctionVariable) expressionValue).getSetVarString();
		}
		return null;
	}

	@Nullable
	private static Equation unwrapEquation(GeoElement geoElement) {
		ExpressionNode definition = geoElement.getDefinition();
		if (definition != null && definition.unwrap() instanceof Equation) {
			return (Equation) definition.unwrap();
		}
		if (geoElement instanceof EquationValue) {
			return ((EquationValue) geoElement).getEquation();
		}
		return null;
	}

	private static boolean isEquation(GeoElement geoElement) {
		ExpressionNode definition = geoElement.getDefinition();
		return (definition != null && definition.unwrap() instanceof Equation)
				|| geoElement instanceof EquationValue;
	}

	private static boolean isExplicitEquation(GeoElement geoElement) {
		Equation equation = unwrapEquation(geoElement);
		// A GeoElement is an explicit equation if
		return
				// it is an equation
				equation != null
				// with a single "y" variable on the left-hand side
				&& "y".equals(unwrapVariable(equation.getLHS().unwrap()))
				// and any variables on the right-hand side (if any) are all "x".
				&& equation.getRHS().inspect(value -> "x".equals(unwrapVariable(value)));
	}

	private static boolean isLinearEquation(GeoElement geoElement) {
		return geoElement instanceof GeoLine || geoElement instanceof GeoPlaneND;
	}
}
