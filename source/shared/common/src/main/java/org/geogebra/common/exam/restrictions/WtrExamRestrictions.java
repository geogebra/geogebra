package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GEOMETRY;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.PROBABILITY;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;
import static org.geogebra.common.exam.restrictions.ExamFeatureRestriction.DISABLE_MIXED_NUMBERS;
import static org.geogebra.common.exam.restrictions.ExamFeatureRestriction.RATIONALIZATION;
import static org.geogebra.common.exam.restrictions.ExamFeatureRestriction.SURD;
import static org.geogebra.common.kernel.commands.Commands.BinomialCoefficient;
import static org.geogebra.common.kernel.commands.Commands.BinomialDist;
import static org.geogebra.common.kernel.commands.Commands.Normal;
import static org.geogebra.common.kernel.commands.Commands.nCr;
import static org.geogebra.common.main.syntax.Syntax.ArgumentMatcher.isNumber;
import static org.geogebra.common.plugin.Operation.ABS;
import static org.geogebra.common.plugin.Operation.ACOSH;
import static org.geogebra.common.plugin.Operation.ARCCOS;
import static org.geogebra.common.plugin.Operation.ARCCOSD;
import static org.geogebra.common.plugin.Operation.ARCSIN;
import static org.geogebra.common.plugin.Operation.ARCSIND;
import static org.geogebra.common.plugin.Operation.ARCTAN;
import static org.geogebra.common.plugin.Operation.ARCTAND;
import static org.geogebra.common.plugin.Operation.ASINH;
import static org.geogebra.common.plugin.Operation.ATANH;
import static org.geogebra.common.plugin.Operation.CBRT;
import static org.geogebra.common.plugin.Operation.CEIL;
import static org.geogebra.common.plugin.Operation.COS;
import static org.geogebra.common.plugin.Operation.COSH;
import static org.geogebra.common.plugin.Operation.COT;
import static org.geogebra.common.plugin.Operation.COTH;
import static org.geogebra.common.plugin.Operation.CSC;
import static org.geogebra.common.plugin.Operation.CSCH;
import static org.geogebra.common.plugin.Operation.DIVIDE;
import static org.geogebra.common.plugin.Operation.EXP;
import static org.geogebra.common.plugin.Operation.FACTORIAL;
import static org.geogebra.common.plugin.Operation.FLOOR;
import static org.geogebra.common.plugin.Operation.LOG;
import static org.geogebra.common.plugin.Operation.LOG10;
import static org.geogebra.common.plugin.Operation.LOG2;
import static org.geogebra.common.plugin.Operation.LOGB;
import static org.geogebra.common.plugin.Operation.MINUS;
import static org.geogebra.common.plugin.Operation.MULTIPLY;
import static org.geogebra.common.plugin.Operation.NCR;
import static org.geogebra.common.plugin.Operation.NROOT;
import static org.geogebra.common.plugin.Operation.PLUS;
import static org.geogebra.common.plugin.Operation.POWER;
import static org.geogebra.common.plugin.Operation.ROUND;
import static org.geogebra.common.plugin.Operation.ROUND2;
import static org.geogebra.common.plugin.Operation.SEC;
import static org.geogebra.common.plugin.Operation.SECH;
import static org.geogebra.common.plugin.Operation.SGN;
import static org.geogebra.common.plugin.Operation.SIN;
import static org.geogebra.common.plugin.Operation.SINH;
import static org.geogebra.common.plugin.Operation.SQRT;
import static org.geogebra.common.plugin.Operation.TAN;
import static org.geogebra.common.plugin.Operation.TANH;
import static org.geogebra.common.util.StreamUtils.filter;
import static org.geogebra.common.util.StreamUtils.streamOf;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.expression.ExpressionRestriction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.arithmetic.filter.RadianGradianFilter;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.Syntax;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.Operation;

public class WtrExamRestrictions extends ExamRestrictions {
	WtrExamRestrictions() {
		super(ExamType.WTR,
				Set.of(GRAPHING, GEOMETRY, G3D, CAS, PROBABILITY),
				SCIENTIFIC,
				Set.of(SURD, RATIONALIZATION, DISABLE_MIXED_NUMBERS),
				createInputExpressionFilters(),
				createOutputExpressionFilters(),
				createCommandFilters(),
				createCommandArgumentFilters(),
				createOperationFilter(),
				null,
				createSyntaxFilter(),
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}

	private static Set<ExpressionFilter> createInputExpressionFilters() {
		return Set.of(ExpressionRestriction.toFilter(
				new RestrictComplexExpressions(),
				new RestrictBooleanExpressions(),
				new AllowBooleanCommandArguments(),
				new RestrictLists()),
				new RadianGradianFilter());
	}

	private static Set<ExpressionFilter> createOutputExpressionFilters() {
		return Set.of(ExpressionRestriction.toFilter(
				new RestrictComplexExpressions(),
				new RestrictBooleanExpressions(),
				new AllowBooleanCommandArguments(),
				new RestrictLists()));
	}

	private static Set<CommandFilter> createCommandFilters() {
		return Set.of(new CommandNameFilter(false, BinomialCoefficient, nCr, BinomialDist, Normal));
	}

	private static OperationFilter createOperationFilter() {
		Set<Operation> allowedOperations = Set.of(
				PLUS, MINUS, MULTIPLY, DIVIDE, POWER, FACTORIAL, ABS, SGN, FLOOR, CEIL, ROUND,
				ROUND2, SQRT, CBRT, NROOT, EXP, LOG, LOG2, LOG10, LOGB, COS, SIN, TAN, SEC, CSC,
				COT, ARCCOS, ARCCOSD, ARCSIN, ARCSIND, ARCTAN, ARCTAND, COSH, SINH, TANH, SECH,
				CSCH, COTH, ACOSH, ASINH, ATANH, NCR);
		return allowedOperations::contains;
	}

	private static SyntaxFilter createSyntaxFilter() {
		return new WtrSyntaxFilter();
    }

	private static Set<CommandArgumentFilter> createCommandArgumentFilters() {
		return Set.of(new WtrCommandArgumentFilter());
	}

	private static final class WtrSyntaxFilter extends LineSelectorSyntaxFilter {

		private WtrSyntaxFilter() {
			// BinomialDist(<Number of Trials>, <Probability of Success>, <Variable Value>,
			// <Boolean Cumulative>)
			addSelector(BinomialDist, 2);
			// Normal( <Mean>, <Standard Deviation>, <Variable Value> )
			// Normal( <Mean>, <Standard Deviation>, <Variable Value u> , <Variable Value v>)
			addSelector(Normal, 0, 2);
		}
	}

	private static final class WtrCommandArgumentFilter implements CommandArgumentFilter {
		private final Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
				BinomialDist, Set.of(
						Syntax.of(BinomialDist,
								isNumber(), isNumber(), isNumber(), GeoElement::isGeoBoolean)),
				Normal, Set.of(
						Syntax.of(Normal, isNumber(), isNumber(), isNumber()),
						Syntax.of(Normal, isNumber(), isNumber(), isNumber(), isNumber())));

		@Override
		public void checkAllowed(Command command, CommandProcessor commandProcessor)
				throws MyError {
			Syntax.checkRestrictedSyntaxes(
					allowedSyntaxesForRestrictedCommands, command, commandProcessor);
		}
	}

	private static final class RestrictComplexExpressions implements ExpressionRestriction {
		@Override
		public @Nonnull Set<ExpressionValue> getRestrictedSubExpressions(
				@Nonnull ExpressionValue expression) {
			return filter(expression, subExpression ->
					subExpression.getValueType() == ValueType.COMPLEX);
		}
	}

	private static final class RestrictBooleanExpressions implements ExpressionRestriction {
		@Override
		public @Nonnull Set<ExpressionValue> getRestrictedSubExpressions(
				@Nonnull ExpressionValue expression) {
			return filter(expression, subExpression -> subExpression instanceof BooleanValue);
		}
	}

	private static final class AllowBooleanCommandArguments implements ExpressionRestriction {
		@Override
		public @Nonnull Set<ExpressionValue> getAllowedSubExpressions(@Nonnull ExpressionValue expression) {
			return streamOf(expression)
					// For commands,
					.filter(subExpression -> subExpression instanceof Command)
					// (downcast)
					.map(command -> (Command) command)
					// iterate through arguments,
					.flatMap(command -> Arrays.stream(command.getArguments())
							// (unwrap expression value)
							.map(ExpressionNode::unwrap)
							// and allow booleans
							.filter(argument -> argument instanceof BooleanValue))
					.collect(Collectors.toSet());
		}
	}

	private static final class RestrictLists implements ExpressionRestriction {
		@Override
		public @Nonnull Set<ExpressionValue> getRestrictedSubExpressions(
				@Nonnull ExpressionValue expression) {
			return filter(expression, subExpression -> subExpression instanceof MyList);
		}
	}

	@Override
	public void applySettingsRestrictions(@Nonnull Settings settings,
			@Nonnull ConstructionDefaults defaults) {
		super.applySettingsRestrictions(settings, defaults);
		settings.getAlgebra().setAngleConversionRestricted(true);
	}

	@Override
	public void removeSettingsRestrictions(@Nonnull Settings settings,
			@Nonnull ConstructionDefaults defaults) {
		super.removeSettingsRestrictions(settings, defaults);
		settings.getAlgebra().setAngleConversionRestricted(false);
	}
}
