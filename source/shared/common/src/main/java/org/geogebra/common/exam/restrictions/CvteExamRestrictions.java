/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GEOMETRY;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.PROBABILITY;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Regression;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics1;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics2;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.ALLOW;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;
import static org.geogebra.common.plugin.Operation.ALT;
import static org.geogebra.common.plugin.Operation.ARG;
import static org.geogebra.common.plugin.Operation.BETA;
import static org.geogebra.common.plugin.Operation.BETA_INCOMPLETE;
import static org.geogebra.common.plugin.Operation.BETA_INCOMPLETE_REGULARIZED;
import static org.geogebra.common.plugin.Operation.CI;
import static org.geogebra.common.plugin.Operation.CONJUGATE;
import static org.geogebra.common.plugin.Operation.EI;
import static org.geogebra.common.plugin.Operation.ERF;
import static org.geogebra.common.plugin.Operation.FRACTIONAL_PART;
import static org.geogebra.common.plugin.Operation.GAMMA;
import static org.geogebra.common.plugin.Operation.GAMMA_INCOMPLETE;
import static org.geogebra.common.plugin.Operation.GAMMA_INCOMPLETE_REGULARIZED;
import static org.geogebra.common.plugin.Operation.LAMBERTW;
import static org.geogebra.common.plugin.Operation.NPR;
import static org.geogebra.common.plugin.Operation.POLYGAMMA;
import static org.geogebra.common.plugin.Operation.PRODUCT;
import static org.geogebra.common.plugin.Operation.PSI;
import static org.geogebra.common.plugin.Operation.RANDOM;
import static org.geogebra.common.plugin.Operation.SI;
import static org.geogebra.common.plugin.Operation.VECTORPRODUCT;
import static org.geogebra.common.plugin.Operation.ZETA;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.cvte.CvteCommandArgumentFilter;
import org.geogebra.common.exam.restrictions.cvte.CvteEquationBehaviour;
import org.geogebra.common.exam.restrictions.cvte.MatrixExpressionFilter;
import org.geogebra.common.exam.restrictions.visibility.HiddenInequalityVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.HiddenVectorVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.filter.CompositeExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.DeepExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.arithmetic.filter.graphing.AbsExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.graphing.InnerProductExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.graphing.PowerInnerProductExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.graphing.VectorProductExpressionFilter;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;

public final class CvteExamRestrictions extends ExamRestrictions {

	private boolean casEnabled = true;

	CvteExamRestrictions() {
		super(ExamType.CVTE,
				Set.of(CAS, G3D, GEOMETRY, PROBABILITY, SCIENTIFIC),
				GRAPHING,
				createFeatureRestrictions(),
				createInputExpressionFilters(),
				createOutputExpressionFilters(),
				createCommandFilters(),
				createCommandArgumentFilters(),
				createOperationFilter(),
				createContextMenuItemFilters(),
				createSyntaxFilter(),
				createToolsFilter(),
				createPropertyRestrictions(),
				createVisibilityRestrictions(),
				createEquationBehaviour(),
				null,
				null,
				null);
	}

	@Override
	public void applyTo(
			@Nonnull ExamController.ContextDependencies dependencies,
			@CheckForNull PropertiesRegistry propertiesRegistry,
			@CheckForNull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@CheckForNull ContextMenuFactory contextMenuFactory) {
		if (dependencies.settings != null) {
			casEnabled = dependencies.settings.getCasSettings().isEnabled();
			// Note: The effect we want to achieve here is disable the symbolic versions of the
			// Derivative and Integral commands, and replace them on the fly with their numeric
			// counterparts (a requirement of APPS-4871/APPS-4961). This behavior is
			// implemented using the "CAS enabled" flag (deep inside the algebra processor and
			// other places).
			// Careful: setting the "CAS enabled" setting to false here only makes because
			// CvTE exam is restricted to the Graphing subapp of Suite, and the Graphing
			// standalone app disables CAS, but it's enabled in Suite (see app config).
			dependencies.settings.getCasSettings().setEnabled(false);
		}
		super.applyTo(dependencies, propertiesRegistry,
				geoElementPropertiesFactory, contextMenuFactory);
	}

	@Override
	public void removeFrom(
			@Nonnull ExamController.ContextDependencies dependencies,
			@CheckForNull PropertiesRegistry propertiesRegistry,
			@CheckForNull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@CheckForNull ContextMenuFactory contextMenuFactory) {
		super.removeFrom(dependencies, propertiesRegistry,
				geoElementPropertiesFactory, contextMenuFactory);
		if (dependencies.settings != null) {
			dependencies.settings.getCasSettings().setEnabled(casEnabled);
		}
	}

	private static Set<ExamFeatureRestriction> createFeatureRestrictions() {
		return Set.of(
				ExamFeatureRestriction.AUTOMATIC_GRAPH_SELECTION_FOR_FUNCTIONS,
				ExamFeatureRestriction.HIDE_CALCULATED_EQUATION,
				ExamFeatureRestriction.RESTRICT_CHANGING_EQUATION_FORM,
				ExamFeatureRestriction.SURD,
				ExamFeatureRestriction.RATIONALIZATION);
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
		LineSelectorSyntaxFilter filter = new LineSelectorSyntaxFilter();
		// allow only Circle(<Center>, <Radius>)
		filter.addSelector(Commands.Circle, 0);
		// allow only Extremum(<Function>, <Start x-Value>, <End x-Value>)
		filter.addSelector(Commands.Extremum, 1);
		// allow only Root( <Function>, <Start x-Value>, <End x-Value> )
		filter.addSelector(Commands.Root, 2);
		// allow only Invert( <Function> )
		filter.addSelector(Commands.Invert, 1);
		return filter;
	}

	private static OperationFilter createOperationFilter() {
		Set<Operation> restrictedOperations = Set.of(
				CONJUGATE, FRACTIONAL_PART, GAMMA, GAMMA_INCOMPLETE, GAMMA_INCOMPLETE_REGULARIZED,
				POLYGAMMA, RANDOM, NPR, PRODUCT, VECTORPRODUCT, ARG, ALT, BETA, BETA_INCOMPLETE,
				BETA_INCOMPLETE_REGULARIZED, ERF, PSI, SI, CI, EI, ZETA, LAMBERTW);
		return operation -> !restrictedOperations.contains(operation);
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
		List<ExpressionFilter> filters = List.of(
				new MatrixExpressionFilter(),
				new AbsExpressionFilter(),
				new InnerProductExpressionFilter(),
				new PowerInnerProductExpressionFilter(),
				new VectorProductExpressionFilter());
		return Set.of(new DeepExpressionFilter(new CompositeExpressionFilter(filters)));
	}

	private static Set<ExpressionFilter> createOutputExpressionFilters() {
		return Set.of(new MatrixExpressionFilter());
	}

	private static Map<PropertyKey, PropertyRestriction> createPropertyRestrictions() {
		// "freeze" the equation form properties
		return Map.of(
				PropertyKey.of(LinearEquationFormProperty.class),
				new PropertyRestriction(true, null),
				PropertyKey.of(QuadraticEquationFormProperty.class),
				new PropertyRestriction(true, null));
	}

	private static EquationBehaviour createEquationBehaviour() {
		return new CvteEquationBehaviour();
	}

	/**
	 * Creates a set of visibility restrictions for exam mode.
	 * @return the set of visibility restrictions
	 */
	public static Set<VisibilityRestriction> createVisibilityRestrictions() {
		return Set.of(
				new HiddenEquationVisibilityRestriction(),
				new AllowedExplicitEquationVisibilityRestriction(),
				new AllowedLinearEquationVisibilityRestriction(),
				new HiddenConicVisibilityRestriction(),
				new HiddenInequalityVisibilityRestriction(),
				new HiddenVectorVisibilityRestriction(),
				new AllowedCenterAndRadiusCircleCommandVisibilityRestriction());
	}

	/**
	 * Allows the visibility of explicit equations.
	 * <p> Examples: </p>
	 * <ul>
	 *     <li>y = 2x</li>
	 *     <li>y = 5</li>
	 *     <li>y = x^2</li>
	 *     <li>y = x^3</li>
	 *     <li>y = x^2 - 5x + 2</li>
	 * </ul>
	 */
	private static final class AllowedExplicitEquationVisibilityRestriction
			implements VisibilityRestriction {
		@Override
		public @Nonnull Effect getEffect(GeoElement geoElement) {
			return isExplicitEquation(geoElement) ? ALLOW : IGNORE;
		}
	}

	/**
	 * Allows the visibility of circles created by {@code Circle(<Center>, <Radius>)} command or {@code Circle: Center & Radius} tool.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>Circle((0, 0), 2)</li>
	 *     <li>Circle(A, 4)</li>
	 * </ul>
	 */
	private static final class AllowedCenterAndRadiusCircleCommandVisibilityRestriction
			implements VisibilityRestriction {
		@Override
		public @Nonnull Effect getEffect(GeoElement geoElement) {
			return geoElement.getParentAlgorithm() instanceof AlgoCirclePointRadius
					? ALLOW : IGNORE;
		}
	}

	/**
	 * Allows the visibility of linear equations.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>x = 0</li>
	 *     <li>x + y = 0</li>
	 *     <li>2x - 3y = 4</li>
	 *     <li>x = y</li>
	 *     <li>2x = y</li>
	 *     <li>y = 2x</li>
	 * </ul>
	 */
	private static final class AllowedLinearEquationVisibilityRestriction
			implements VisibilityRestriction {
		@Override
		public @Nonnull Effect getEffect(GeoElement geoElement) {
			return isLinearEquation(geoElement) ? ALLOW : IGNORE;
		}
	}

	/**
	 * Restricts the visibility of conics.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>x^2 + y^2 = 4</li>
	 *     <li>x^2 / 9 + x^2 / 4 = 1</li>
	 *     <li>x^2 - y^2 = 4</li>
	 *     <li>Circle((0, 0), 2)</li>
	 * </ul>
	 */
	private static final class HiddenConicVisibilityRestriction
			implements VisibilityRestriction {
		@Override
		public @Nonnull Effect getEffect(GeoElement geoElement) {
			return geoElement.isGeoConic() ? HIDE : IGNORE;
		}
	}

	/**
	 * Restricts the visibility of equations.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>x = 0</li>
	 *     <li>2x = y</li>
	 *     <li>x^2 = 1</li>
	 *     <li>2^x = 0</li>
	 *     <li>sin(x) = 0</li>
	 *     <li>ln(x) = 0</li>
	 *     <li>|x - 3| = 0</li>
	 *     <li>x^2 = y</li>
	 *     <li>x^3 = y</li>
	 *     <li>y^2 = x</li>
	 *     <li>y^3 = x</li>
	 *     <li>x^3 + y^2 = 2</li>
	 *     <li>x^2 / 9 + x^2 / 4 = 1</li>
	 * </ul>
	 */
	private static final class HiddenEquationVisibilityRestriction
			implements VisibilityRestriction {
		@Override
		public @Nonnull Effect getEffect(GeoElement geoElement) {
			return isEquation(geoElement) ? HIDE : IGNORE;
		}
	}

	private static @CheckForNull String unwrapVariable(ExpressionValue expressionValue) {
		if (expressionValue instanceof FunctionVariable) {
			return ((FunctionVariable) expressionValue).getSetVarString();
		}
		return null;
	}

	private static @CheckForNull Equation unwrapEquation(GeoElement geoElement) {
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
		if (equation == null) {
			return false;
		}
		// Explicit equations should have a single "y" variable on the left-hand side
		if (!"y".equals(unwrapVariable(equation.getLHS().unwrap()))) {
			return false;
		}
		// and all variables (if any) on the right-hand side should be "x".
		if (anyVariableDiffersFromX(equation.getRHS())) {
			return false;
		}
		return true;
	}

	private static boolean anyVariableDiffersFromX(ExpressionNode expressionNode) {
		return expressionNode.any(value -> {
			String variable = unwrapVariable(value);
			return variable != null && !variable.equals("x");
		});
	}

	private static boolean isLinearEquation(GeoElement geoElement) {
		return geoElement instanceof GeoLine || geoElement instanceof GeoPlaneND;
	}
}
