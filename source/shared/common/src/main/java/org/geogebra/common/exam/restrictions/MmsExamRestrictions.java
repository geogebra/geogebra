package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GEOMETRY;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.PROBABILITY;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;
import static org.geogebra.common.kernel.commands.Commands.Append;
import static org.geogebra.common.kernel.commands.Commands.BarChart;
import static org.geogebra.common.kernel.commands.Commands.BinomialCoefficient;
import static org.geogebra.common.kernel.commands.Commands.BinomialDist;
import static org.geogebra.common.kernel.commands.Commands.Cross;
import static org.geogebra.common.kernel.commands.Commands.Derivative;
import static org.geogebra.common.kernel.commands.Commands.Division;
import static org.geogebra.common.kernel.commands.Commands.Dot;
import static org.geogebra.common.kernel.commands.Commands.Element;
import static org.geogebra.common.kernel.commands.Commands.Expand;
import static org.geogebra.common.kernel.commands.Commands.First;
import static org.geogebra.common.kernel.commands.Commands.Flatten;
import static org.geogebra.common.kernel.commands.Commands.Identity;
import static org.geogebra.common.kernel.commands.Commands.IndexOf;
import static org.geogebra.common.kernel.commands.Commands.Insert;
import static org.geogebra.common.kernel.commands.Commands.Integral;
import static org.geogebra.common.kernel.commands.Commands.IntegralSymbolic;
import static org.geogebra.common.kernel.commands.Commands.Invert;
import static org.geogebra.common.kernel.commands.Commands.Join;
import static org.geogebra.common.kernel.commands.Commands.Last;
import static org.geogebra.common.kernel.commands.Commands.LeftSide;
import static org.geogebra.common.kernel.commands.Commands.Length;
import static org.geogebra.common.kernel.commands.Commands.Limit;
import static org.geogebra.common.kernel.commands.Commands.LimitAbove;
import static org.geogebra.common.kernel.commands.Commands.LimitBelow;
import static org.geogebra.common.kernel.commands.Commands.Mod;
import static org.geogebra.common.kernel.commands.Commands.NIntegral;
import static org.geogebra.common.kernel.commands.Commands.NSolutions;
import static org.geogebra.common.kernel.commands.Commands.NSolve;
import static org.geogebra.common.kernel.commands.Commands.Normal;
import static org.geogebra.common.kernel.commands.Commands.Numeric;
import static org.geogebra.common.kernel.commands.Commands.Product;
import static org.geogebra.common.kernel.commands.Commands.RandomElement;
import static org.geogebra.common.kernel.commands.Commands.Remove;
import static org.geogebra.common.kernel.commands.Commands.Reverse;
import static org.geogebra.common.kernel.commands.Commands.RightSide;
import static org.geogebra.common.kernel.commands.Commands.Sample;
import static org.geogebra.common.kernel.commands.Commands.SampleSD;
import static org.geogebra.common.kernel.commands.Commands.Sequence;
import static org.geogebra.common.kernel.commands.Commands.Shuffle;
import static org.geogebra.common.kernel.commands.Commands.SigmaXX;
import static org.geogebra.common.kernel.commands.Commands.SigmaXY;
import static org.geogebra.common.kernel.commands.Commands.Slider;
import static org.geogebra.common.kernel.commands.Commands.Solutions;
import static org.geogebra.common.kernel.commands.Commands.Solve;
import static org.geogebra.common.kernel.commands.Commands.Sort;
import static org.geogebra.common.kernel.commands.Commands.StepGraph;
import static org.geogebra.common.kernel.commands.Commands.StickGraph;
import static org.geogebra.common.kernel.commands.Commands.Substitute;
import static org.geogebra.common.kernel.commands.Commands.Sum;
import static org.geogebra.common.kernel.commands.Commands.Take;
import static org.geogebra.common.kernel.commands.Commands.Transpose;
import static org.geogebra.common.kernel.commands.Commands.nCr;
import static org.geogebra.common.kernel.commands.Commands.stdev;
import static org.geogebra.common.kernel.statistics.Statistic.COVARIANCE;
import static org.geogebra.common.kernel.statistics.Statistic.MAX;
import static org.geogebra.common.kernel.statistics.Statistic.MEAN;
import static org.geogebra.common.kernel.statistics.Statistic.MEDIAN;
import static org.geogebra.common.kernel.statistics.Statistic.MIN;
import static org.geogebra.common.kernel.statistics.Statistic.PMCC;
import static org.geogebra.common.kernel.statistics.Statistic.Q1;
import static org.geogebra.common.kernel.statistics.Statistic.Q3;
import static org.geogebra.common.kernel.statistics.Statistic.SD;
import static org.geogebra.common.plugin.Operation.ALT;
import static org.geogebra.common.plugin.Operation.AND;
import static org.geogebra.common.plugin.Operation.ARCTAN2;
import static org.geogebra.common.plugin.Operation.ARCTAN2D;
import static org.geogebra.common.plugin.Operation.ARG;
import static org.geogebra.common.plugin.Operation.BETA;
import static org.geogebra.common.plugin.Operation.BETA_INCOMPLETE;
import static org.geogebra.common.plugin.Operation.BETA_INCOMPLETE_REGULARIZED;
import static org.geogebra.common.plugin.Operation.CI;
import static org.geogebra.common.plugin.Operation.CONJUGATE;
import static org.geogebra.common.plugin.Operation.DIVIDE;
import static org.geogebra.common.plugin.Operation.EI;
import static org.geogebra.common.plugin.Operation.EQUAL_BOOLEAN;
import static org.geogebra.common.plugin.Operation.ERF;
import static org.geogebra.common.plugin.Operation.FUNCTION;
import static org.geogebra.common.plugin.Operation.FUNCTION_NVAR;
import static org.geogebra.common.plugin.Operation.GAMMA;
import static org.geogebra.common.plugin.Operation.GAMMA_INCOMPLETE;
import static org.geogebra.common.plugin.Operation.GAMMA_INCOMPLETE_REGULARIZED;
import static org.geogebra.common.plugin.Operation.GREATER;
import static org.geogebra.common.plugin.Operation.GREATER_EQUAL;
import static org.geogebra.common.plugin.Operation.IMAGINARY;
import static org.geogebra.common.plugin.Operation.IMPLICATION;
import static org.geogebra.common.plugin.Operation.IS_ELEMENT_OF;
import static org.geogebra.common.plugin.Operation.IS_SUBSET_OF;
import static org.geogebra.common.plugin.Operation.IS_SUBSET_OF_STRICT;
import static org.geogebra.common.plugin.Operation.LAMBERTW;
import static org.geogebra.common.plugin.Operation.LESS;
import static org.geogebra.common.plugin.Operation.LESS_EQUAL;
import static org.geogebra.common.plugin.Operation.MINUS;
import static org.geogebra.common.plugin.Operation.MULTIPLY;
import static org.geogebra.common.plugin.Operation.NOT;
import static org.geogebra.common.plugin.Operation.NOT_EQUAL;
import static org.geogebra.common.plugin.Operation.OR;
import static org.geogebra.common.plugin.Operation.PARALLEL;
import static org.geogebra.common.plugin.Operation.PERPENDICULAR;
import static org.geogebra.common.plugin.Operation.PLUS;
import static org.geogebra.common.plugin.Operation.POLYGAMMA;
import static org.geogebra.common.plugin.Operation.POWER;
import static org.geogebra.common.plugin.Operation.PSI;
import static org.geogebra.common.plugin.Operation.RANDOM;
import static org.geogebra.common.plugin.Operation.REAL;
import static org.geogebra.common.plugin.Operation.SET_DIFFERENCE;
import static org.geogebra.common.plugin.Operation.SI;
import static org.geogebra.common.plugin.Operation.XOR;
import static org.geogebra.common.plugin.Operation.ZETA;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.contextmenu.AlgebraContextMenuItem;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.visibility.HiddenInequalityVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.HiddenVectorVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.gui.view.table.dialog.StatisticsFilter;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.filter.AllowedExpressionsProvider;
import org.geogebra.common.kernel.arithmetic.filter.ComplexExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.DeepExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionNodeFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;

public class MmsExamRestrictions extends ExamRestrictions {

	/**
	 * Restrictions for IQB MMS
	 */
	protected MmsExamRestrictions() {
		super(ExamType.MMS,
				Set.of(GRAPHING, GEOMETRY, G3D, PROBABILITY, SCIENTIFIC),
				CAS,
				createFeatureRestrictions(),
				createInputExpressionFilters(),
				createOutputExpressionFilters(),
				createCommandFilters(),
				createCommandArgumentFilters(),
				createOperationFilter(),
				createContextMenuItemFilters(),
				createSyntaxFilter(),
				null,
				null,
				createVisibilityRestrictions(),
				null,
				null,
				createStatisticsFilter(),
				createAlgebraOutputFormatFilters());
	}

	private static Set<ExamFeatureRestriction> createFeatureRestrictions() {
		return Set.of(ExamFeatureRestriction.DATA_TABLE_REGRESSION,
				ExamFeatureRestriction.HIDE_CALCULATED_EQUATION,
				ExamFeatureRestriction.HIDE_SPECIAL_POINTS,
				ExamFeatureRestriction.SPREADSHEET,
				ExamFeatureRestriction.SURD,
				ExamFeatureRestriction.RATIONALIZATION,
				ExamFeatureRestriction.DISABLE_MIXED_NUMBERS);
	}

	private static Set<ExpressionFilter> createInputExpressionFilters() {
		return Set.of(
				new ComplexExpressionFilter(),
				new DeepExpressionFilter(new MmsListOperationFilter()),
				new DeepExpressionFilter(new MmsFunctionExpressionFilter()),
				new DeepExpressionFilter(
						OperationFilter.restricting(restrictedInequalityOperations())
								.toExpressionFilter())
						.allowWhen(operatorInInequality())
		);
	}

	private static Set<ExpressionFilter> createOutputExpressionFilters() {
		return Set.of(new ComplexExpressionFilter());
	}

	private static Set<CommandFilter> createCommandFilters() {
		CommandFilter filter =
				new CommandNameFilter(false, Append, BarChart, BinomialCoefficient, BinomialDist,
						Cross, Derivative, Division, Dot, Element, Expand, First, Flatten,
						Identity, IndexOf, Insert, Integral, IntegralSymbolic, Invert, Join, Last,
						LeftSide, Length, Limit, LimitAbove, LimitBelow, Mod, nCr, NIntegral,
						NSolutions, NSolve, Product, RandomElement, Remove, Reverse, RightSide,
						Sample, SampleSD, Sequence, Shuffle, SigmaXX, SigmaXY, Slider, Solutions,
						Solve, Sort, stdev, StepGraph, StickGraph, Sum, Take, Transpose, Numeric,
						Substitute, Normal);
		return Set.of(filter);
	}

	private static Set<CommandArgumentFilter> createCommandArgumentFilters() {
		return Set.of(new MmsCommandArgumentFilter());
	}

	private static OperationFilter createOperationFilter() {
		Set<Operation> restrictedOperations = Set.of(
				ARG, CONJUGATE, REAL, IMAGINARY, ALT, RANDOM, ARCTAN2, ARCTAN2D, BETA,
				BETA_INCOMPLETE, BETA_INCOMPLETE_REGULARIZED, GAMMA, GAMMA_INCOMPLETE,
				GAMMA_INCOMPLETE_REGULARIZED, ERF, PSI, POLYGAMMA, SI, CI, EI, ZETA, LAMBERTW,
				EQUAL_BOOLEAN, NOT_EQUAL, AND, OR, NOT, XOR, IMPLICATION,
				PARALLEL, PERPENDICULAR, IS_ELEMENT_OF, IS_SUBSET_OF, IS_SUBSET_OF_STRICT,
				SET_DIFFERENCE);
		return OperationFilter.restricting(restrictedOperations);
	}

	private static Set<Operation> restrictedInequalityOperations() {
		return Set.of(LESS, GREATER, LESS_EQUAL, GREATER_EQUAL);
	}

	private static Set<ContextMenuItemFilter> createContextMenuItemFilters() {
		return Set.of(contextMenuItem -> contextMenuItem != AlgebraContextMenuItem.Statistics
				&& contextMenuItem != AlgebraContextMenuItem.SpecialPoints);
	}

	private static SyntaxFilter createSyntaxFilter() {
		LineSelectorSyntaxFilter filter = new LineSelectorSyntaxFilter();
		filter.addSelector(Invert, 0);
		filter.addSelector(Length, 0);
		filter.addSelector(Normal, 0, 3);
		filter.addSelector(Product, 0);
		filter.addSelector(SigmaXX, 1);
		filter.addSelector(SigmaXY, 1);
		filter.addSelector(stdev, 0);
		filter.addSelector(Sum, 0);
		return filter;
	}

	private static StatisticsFilter createStatisticsFilter() {
		Set<Statistic> filteredStatistics = Set.of(MEAN, SD, MIN, Q1, MEDIAN,
				Q3, MAX, PMCC, COVARIANCE);
		return statistic -> !filteredStatistics.contains(statistic);
	}

	private static Set<AlgebraOutputFormatFilter> createAlgebraOutputFormatFilters() {
		return Set.of(new PolarCoordinateCartesianFormatFilter());
	}

	/**
	 * Output format filter for polar coordinate points to restrict cartesian output format.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>
	 *         (3; π / 3)
	 *         <ul>
	 *             <li>
	 *                 Restricted (3 / 2, 3 * √3 / 2) output format
	 *                 ({@link AlgebraOutputFormat#EXACT})
	 *             </li>
	 *             <li>
	 *                 Allowed (3; 1.0471975511966 rad) output format
	 *                 ({@link AlgebraOutputFormat#APPROXIMATION})
	 *             </li>
	 *         </ul>
	 *     </li>
	 *     <li>
	 *         (1; 2)
	 *         <ul>
	 *             <li>
	 *                 Restricted (cos(2), sin(2)) output format
	 *                 ({@link AlgebraOutputFormat#EXACT})
	 *             </li>
	 *             <li>
	 *                 Allowed (1; 2 rad) output format
	 *                 ({@link AlgebraOutputFormat#APPROXIMATION})
	 *             </li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	private static final class PolarCoordinateCartesianFormatFilter
			implements AlgebraOutputFormatFilter {
		@SuppressWarnings("PMD.SimplifyBooleanReturns")
		@Override
		public boolean isAllowed(GeoElement geoElement, AlgebraOutputFormat outputFormat) {
			GeoElementND unwrappedElement = geoElement.unwrapSymbolic();
			// Restrict the exact (calculated cartesian) output format for polar coordinates
			boolean blocked = unwrappedElement instanceof GeoPoint
					&& ((GeoPoint) unwrappedElement).isPolar()
					&& outputFormat == AlgebraOutputFormat.EXACT;
			return !blocked;
		}
	}

	/**
	 * Creates a set of visibility restrictions for exam mode.
	 * <p> This method is used and exposed as {@code public} only for unit tests. </p>
	 * @return the set of visibility restrictions
	 */
	public static Set<VisibilityRestriction> createVisibilityRestrictions() {
		return Set.of(
				new HiddenIntegralAreaVisibilityRestriction(),
				new HiddenInequalityVisibilityRestriction(),
				new HiddenVectorVisibilityRestriction(),
				new HiddenImplicitCurveVisibilityRestriction());
	}

	/**
	 * Restricts the visibility of integrals with area.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>Integral(f, -5, 5)</li>
	 *     <li>Integral(f, x, -5, 5)</li>
	 *     <li>NIntegral(f, -5, 5)</li>
	 * </ul>
	 */
	private static final class HiddenIntegralAreaVisibilityRestriction
			implements VisibilityRestriction {
		@Nonnull
		@Override
		public Effect getEffect(GeoElement geoElement) {
			GeoElementND unwrappedTwin = geoElement.unwrapSymbolic();
			return (unwrappedTwin != null && unwrappedTwin
					.getParentAlgorithm() instanceof AlgoIntegralDefinite) ? HIDE : IGNORE;
		}
	}

	/**
	 * Restricts the visibility of implicit curves.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>
	 *         {@link GeoConic}s in implicit form:
	 *         <ul>
	 *             <li>x^2 = 1</li>
	 *             <li>y - x^2 = 0</li>
	 *             <li>x^2 = y</li>
	 *             <li>x^2 + y^2 = 4</li>
	 *             <li>x^2 / 9 + y^2 / 4 = 1</li>
	 *             <li>x^2 - y^2 = 4</li>
	 *         </ul>
	 *     </li>
	 *     <li>
	 *         {@link GeoImplicitCurve}s:
	 *         <ul>
	 *             <li>2^x = 2</li>
	 *             <li>sin(x) = 0</li>
	 *             <li>x^3 + y^2 = 2</li>
	 *             <li>y^3 = x</li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	private static final class HiddenImplicitCurveVisibilityRestriction
			implements VisibilityRestriction {
		@Nonnull
		@Override
		public Effect getEffect(GeoElement geoElement) {
			return isImplicitCurve(geoElement) ? HIDE : IGNORE;
		}

		private boolean isImplicitCurve(GeoElement geoElement) {
			if (isGeoImplicitCurve(geoElement)) {
				return true;
			}
			if (isExplicitEquation(geoElement)) {
				return false;
			}
			return isGeoConic(geoElement);
		}

		private static boolean isGeoImplicitCurve(GeoElement geoElement) {
			return geoElement instanceof GeoImplicitCurve || geoElement instanceof GeoSymbolic
					&& ((GeoSymbolic) geoElement).getTwinGeo() instanceof GeoImplicitCurve;
		}

		private static boolean isGeoConic(GeoElement geoElement) {
			return geoElement instanceof GeoConic || geoElement instanceof GeoSymbolic
					&& ((GeoSymbolic) geoElement).getTwinGeo() instanceof GeoConic;
		}

		@SuppressWarnings("PMD.SimplifyBooleanReturns")
		private static boolean isExplicitEquation(GeoElement geoElement) {
			Equation equation = unwrapEquation(geoElement);
			if (equation == null) {
				return false;
			}
			// Explicit equations should have a single "y" variable on the left-hand side
			if (!"y".equals(unwrapVariable(equation.getLHS().unwrap()))) {
				return false;
			}
			// and all the variables (if any) on the right-hand side should be "x".
			if (!allVariablesAreX(equation.getRHS())) {
				return false;
			}
			return true;
		}

		private static boolean allVariablesAreX(ExpressionNode expressionNode) {
			return expressionNode.none(value -> {
				String variable = unwrapVariable(value);
				return variable != null && !variable.equals("x");
			});
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

		@Nullable
		private static String unwrapVariable(ExpressionValue expressionValue) {
			if (expressionValue instanceof FunctionVariable) {
				return ((FunctionVariable) expressionValue).getSetVarString();
			}
			return null;
		}
	}

	private static AllowedExpressionsProvider operatorInInequality() {
		Set<Operation> inequalityOperators = restrictedInequalityOperations();
		return expressionValue -> {
			if (expressionValue instanceof ExpressionNode) {
				ExpressionNode node = (ExpressionNode) expressionValue;
				if (node.containsFreeFunctionVariable(null)
						&& inequalityOperators.contains(node.getOperation())) {
					return List.of(node);
				}
			}
			return null;
		};
	}

	private static final class MmsCommandArgumentFilter extends BaseCommandArgumentFilter {

		@Override
		public void checkAllowed(Command command, CommandProcessor commandProcessor)
				throws MyError {
			if (isCommand(command, BinomialDist)) {
				if (command.getArgumentNumber() < 3) {
					throw commandProcessor.argNumErr(command, command.getArgumentNumber());
				} else if (command.getArgument(2).getValueType() == ValueType.BOOLEAN) {
					throw commandProcessor.argErr(command, command.getArgument(2));
				}
			} else if (isCommand(command, Invert)) {
				if (command.getArgumentNumber() == 1) {
					GeoElement[] args = commandProcessor.resArgs(command);
					if (args[0].isGeoFunction()) {
						throw commandProcessor.argErr(command, command.getArgument(0));
					}
				}
			} else if (isCommand(command, Length)) {
				if (command.getArgumentNumber() == 1) {
					GeoElement[] args = commandProcessor.resArgs(command);
					if (!args[0].isGeoList()) {
						throw commandProcessor.argErr(command, command.getArgument(0));
					}
				} else {
					throw commandProcessor.argNumErr(command, command.getArgumentNumber());
				}
			} else if (isCommand(command, Normal)) {
				if (command.getArgumentNumber() == 4) {
					GeoElement[] args = commandProcessor.resArgs(command);
					if (!isNumberValue(args[3])) {
						throw commandProcessor.argErr(command, command.getArgument(3));
					}
				} else if (command.getArgumentNumber() == 3) {
					GeoElement[] args = commandProcessor.resArgs(command);
					if (!isNumberValue(args[2])) {
						throw commandProcessor.argErr(command, command.getArgument(2));
					}
				} else {
					throw commandProcessor.argNumErr(command, command.getArgumentNumber());
				}
			} else if (isCommand(command, Product)) {
				restrictArgumentCount(command, commandProcessor, 4);
			} else if (isCommand(command, SampleSD)) {
				restrictArgumentCount(command, commandProcessor, 2);
			} else if (isCommand(command, SigmaXX)) {
				if (command.getArgumentNumber() == 1) {
					GeoElement[] args = commandProcessor.resArgs(command);
					if (args[0].isGeoList()) {
						GeoList list = (GeoList) args[0];
						if (list.size() <= 0 || !list.get(0).isGeoNumeric()) {
							throw commandProcessor.argErr(command, command.getArgument(0));
						}
					}
				} else {
					restrictArgumentCount(command, commandProcessor, 2);
				}
			} else if (isCommand(command, SigmaXY)) {
				restrictArgumentCount(command, commandProcessor, 1);
			} else if (isCommand(command, stdev)) {
				restrictArgumentCount(command, commandProcessor, 2);
			} else if (isCommand(command, Sum)) {
				restrictArgumentCount(command, commandProcessor, 4);
			}
		}

		private boolean isNumberValue(GeoElement geoElement) {
			return geoElement.isNumberValue() && !geoElement.isGeoBoolean();
		}
	}

	private static boolean isList(ExpressionValue value) {
		return value.evaluatesToList() && value.getListDepth() != 2;
	}

	private static boolean isNumber(ExpressionValue value) {
		return value.getValueType() == ValueType.NUMBER;
	}

	private static final class MmsListOperationFilter extends ExpressionNodeFilter {

		private static final Set<Operation> operations =
				Set.of(PLUS, MINUS, MULTIPLY, DIVIDE, POWER);

		@Override
		protected boolean isExpressionNodeAllowed(ExpressionNode expressionNode) {
			if (operations.stream().noneMatch(expressionNode::isOperation)) {
				return true;
			}
			ExpressionValue left = expressionNode.getLeft();
			ExpressionValue right = expressionNode.getRight();
			if (isList(left)) {
				return !isList(right) && !isNumber(right);
			} else if (isNumber(left)) {
				return !isList(right);
			}
			return true;
		}
	}

	private static final class MmsFunctionExpressionFilter extends ExpressionNodeFilter {

		@Override
		protected boolean isExpressionNodeAllowed(ExpressionNode expressionNode) {
			Operation operation = expressionNode.getOperation();
			if (Operation.isSimpleFunction(operation)) {
				return !isList(expressionNode.getLeft());
			}
			if (operation == FUNCTION) {
				return !isList(expressionNode.getRight());
			}
			if (operation == FUNCTION_NVAR) {
				return !(expressionNode.getRight() instanceof MyList)
						|| ((MyList) expressionNode.getRight()).elements()
							.noneMatch(MmsExamRestrictions::isList);
			}
			return true;
		}
	}
}
