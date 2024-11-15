package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Regression;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics1;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics2;

import java.util.HashMap;
import java.util.Map;
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

@SuppressWarnings("PMD.SimplifyBooleanReturns")
final class CvteExamRestrictions extends ExamRestrictions {

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

	private static boolean isVisibilityEnabled(GeoElement geoElement) {
		// Allow any explicit equation
		if (isExplicitEquation(geoElement)) {
			return true;
		}

		// Allow circles created by "Circle(<Center>, <Radius>)" command
		// or "Circle: Center & Radius" tool
		if (isCircleCreatedWithPointRadiusAlgorithm(geoElement)) {
			return true;
		}

		// Restrict the visibility of equations with the exception of linear equations
		if (geoElement instanceof EquationValue && !isLinearEquation(geoElement)) {
			return false;
		}

		// Restrict the visibility of any other conic
		if (geoElement.isGeoConic()) {
			return false;
		}

		return true;
	}

	private static Map<String, Integer> getVariableCount(ExpressionValue equation) {
		Map<String, Integer> variableCount = new HashMap<>();
		equation.inspect(expressionValue -> {
            if (expressionValue instanceof FunctionVariable) {
                String variable = ((FunctionVariable) expressionValue).getSetVarString();
                if (!variableCount.containsKey(variable)) {
                    variableCount.put(variable, 1);
                } else {
                    variableCount.put(variable, variableCount.get(variable) + 1);
                }
            }
            return false;
        });
		return variableCount;
	}

	private static boolean isFunctionVariable(ExpressionNode expressionNode) {
		return expressionNode.getRight() == null
				&& expressionNode.getLeft() != null
				&& expressionNode.getLeft() instanceof FunctionVariable
				&& expressionNode.isOperation(Operation.NO_OPERATION);
	}

	@Nullable
	private static String unwrapVariable(ExpressionNode expressionNode) {
		if (isFunctionVariable(expressionNode)) {
			return ((FunctionVariable) expressionNode.getLeft()).getSetVarString();
		}
		return null;
	}

	private static boolean isEquation(GeoElement geoElement) {
		ExpressionNode definition = geoElement.getDefinition();
		return definition != null && definition.unwrap() instanceof Equation;
	}

	private static boolean isExplicitEquation(GeoElement geoElement) {
		return isEquation(geoElement) && isExplicitEquation(unwrapEquation(geoElement));
	}

	private static boolean isCircleCreatedWithPointRadiusAlgorithm(GeoElement geoElement) {
		return geoElement.isGeoConic()
				&& geoElement.getParentAlgorithm() instanceof AlgoCirclePointRadius;
	}

	@Nullable
	private static Equation unwrapEquation(GeoElement geoElement) {
		if (isEquation(geoElement)) {
			return (Equation) geoElement.getDefinition().unwrap();
		}
		return null;
	}

	private static boolean isExplicitEquation(Equation equation) {
		Map<String, Integer> variableCount = getVariableCount(equation);
		// An equation is explicit if it has a single "y" variable
		return variableCount.get("y") != null && variableCount.get("y") == 1
				// which is on the left side of the equation alone
				&& "y".equals(unwrapVariable(equation.getLHS()))
				// and all other variables are either "x" or none
				&& Set.of("x", "y").containsAll(variableCount.keySet());
	}

	private static boolean isLinearEquation(GeoElement geoElement) {
		return geoElement instanceof GeoLine || geoElement instanceof GeoPlaneND;
	}
}
