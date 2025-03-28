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

import java.util.Set;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.filter.ComplexExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelector;
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
				null);
	}

	private static Set<ExpressionFilter> createInputExpressionFilters() {
		return Set.of(new ComplexExpressionFilter());
	}

	private static Set<ExpressionFilter> createOutputExpressionFilters() {
		return Set.of(new ComplexExpressionFilter());
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

	private static final class WtrSyntaxFilter implements SyntaxFilter {
		@Override
		public String getFilteredSyntax(String internalCommandName, String syntax) {
			if (BinomialDist.name().equals(internalCommandName)) {
				// BinomialDist(<Number of Trials>, <Probability of Success>, <Variable Value>,
				// <Boolean Cumulative>)
				return LineSelector.select(syntax, 2);
			} else if (Normal.name().equals(internalCommandName)) {
				// Normal( <Mean>, <Standard Deviation>, <Variable Value> )
				// Normal( <Mean>, <Standard Deviation>, <Variable Value u> , <Variable Value v>)
				return LineSelector.select(syntax, 0, 2);
			}
			return syntax;
		}
	}

	private static final class WtrCommandArgumentFilter implements CommandArgumentFilter {
		@Override
		public void checkAllowed(Command command, CommandProcessor commandProcessor)
				throws MyError {
			String internalCommandName = command.getName();
			if (BinomialDist.name().equals(internalCommandName)) {
				checkBinomialDistribution(command, commandProcessor);
			} else if (Normal.name().equals(internalCommandName)) {
				checkNormalDistribution(command, commandProcessor);
			}
		}

		private void checkBinomialDistribution(Command command, CommandProcessor commandProcessor) {
			// only BinomialDist(<Number of Trials>, <Probability of Success>,
			// <Variable value>, <Boolean Cumulative>) allowed
			GeoElement[] arguments = commandProcessor.resArgs(command);
			if (arguments.length != 4) {
				throw commandProcessor.argNumErr(command, arguments.length);
			}
			GeoElement firstArgument = arguments[0];
			if (!isNumberValue(firstArgument)) {
				throw commandProcessor.argErr(command, firstArgument);
			}
			GeoElement secondArgument = arguments[1];
			if (!isNumberValue(secondArgument)) {
				throw commandProcessor.argErr(command, secondArgument);
			}
			GeoElement thirdArgument = arguments[2];
			if (!isNumberValue(thirdArgument)) {
				throw commandProcessor.argErr(command, thirdArgument);
			}
			GeoElement fourthArgument = arguments[3];
			if (!fourthArgument.isGeoBoolean()) {
				throw commandProcessor.argErr(command, fourthArgument);
			}
		}

		private void checkNormalDistribution(Command command, CommandProcessor commandProcessor) {
			// only Normal(<Mean>, <Standard deviation>, <Variable Value>)
			// and Normal(<Mean>, <Standard deviation>, <Variable Value u>, <Variable Value v>)
			// are allowed
			GeoElement[] arguments = commandProcessor.resArgs(command);
			if (arguments.length != 3 && arguments.length != 4) {
				throw commandProcessor.argNumErr(command, arguments.length);
			}
			GeoElement firstArgument = arguments[0];
			if (!isNumberValue(firstArgument)) {
				throw commandProcessor.argErr(command, firstArgument);
			}
			GeoElement secondArgument = arguments[1];
			if (!isNumberValue(secondArgument)) {
				throw commandProcessor.argErr(command, secondArgument);
			}
			GeoElement thirdArgument = arguments[2];
			if (!isNumberValue(thirdArgument)) {
				throw commandProcessor.argErr(command, thirdArgument);
			}
			if (arguments.length != 4) {
				return;
			}
			GeoElement fourthArgument = arguments[3];
			if (!isNumberValue(fourthArgument)) {
				throw commandProcessor.argErr(command, fourthArgument);
			}
		}

		private boolean isNumberValue(GeoElement geoElement) {
			return geoElement.isNumberValue() && !geoElement.isGeoBoolean();
		}
	}
}
