package org.geogebra.common.util;

import static org.geogebra.common.gui.view.algebra.AlgebraItem.checkAllRHSareIntegers;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.HasSymbolicMode;

public class SymbolicUtil {

	/**
	 * Check if symbolic is Solve/NSolve command
	 *
	 * @param symbolic
	 *        GeoSymbolic input
	 * @return true if symbolic is solve command
	 */
	public static boolean isSolve(GeoSymbolic symbolic) {
		Command topLevelCommand = symbolic.getDefinition().getTopLevelCommand();
		return topLevelCommand != null
				&& (Commands.Solve.getCommand().equals(topLevelCommand.getName())
				|| Commands.NSolve.getCommand().equals(topLevelCommand.getName()));
	}

	private static boolean isNumericOfSolve(GeoSymbolic symbolic) {
		ExpressionNode definition = symbolic.getDefinition();
		if (definition.getLeft() instanceof Command
				&& Commands.Numeric.getCommand()
				.equals(((Command) definition.getLeft()).getName())) {
			Command firstCommand = (Command) definition.getLeft();
			if (firstCommand.getArgumentNumber() > 0
					&& firstCommand.getArgument(0).getLeft() instanceof Command) {
				Command secondCommand = (Command) firstCommand.getArgument(0).getLeft();
				return Commands.Solve.getCommand().equals(secondCommand.getName());
			}
		}
		return false;
	}

	/**
	 * Check if Solve and NSolve give different outputs
	 *
	 * @param symbolic
	 *        GeoSymbolic input
	 * @return true if outputs of symbolic are different
	 *
	 */
	public static boolean isSymbolicSolveDiffers(GeoSymbolic symbolic) {
		GeoSymbolic opposite = getOpposite(symbolic);
		String textOriginal = getValueString(symbolic);
		String textOpposite = getValueString(opposite);

		return !containsUndefinedOrIsEmpty(symbolic) && !containsUndefinedOrIsEmpty(opposite)
				&& !textOriginal.equals(textOpposite);
	}

	private static String getValueString(GeoSymbolic symbolic) {
		return symbolic.toValueString(StringTemplate.defaultTemplate);
	}

	/**
	 * @param geo - GeoElement to check
	 * @return true if expression tree contains an undefined variable or empty list
	 */
	public static boolean containsUndefinedOrIsEmpty(GeoElement geo) {
		return geo.inspect(new UndefinedOrEmptyChecker());
	}

	private static GeoSymbolic getOpposite(GeoSymbolic symbolic) {
		GeoSymbolic opposite = new GeoSymbolic(symbolic.getConstruction());
		opposite.setDefinition(symbolic.getDefinition().deepCopy(symbolic.getKernel()));
		toggleNumericSolve(opposite);
		return opposite;
	}

	/**
	 * Handles the showing/hiding of Solve/NSolve variants
	 * @param symbolic GeoSymbolic input
	 *
	 */
	public static void handleSolveNSolve(GeoSymbolic symbolic) {
		if (isSolve(symbolic)) {
			if (containsUndefinedOrIsEmpty(symbolic)
					&& !containsUndefinedOrIsEmpty(getOpposite(symbolic))) {
				toggleNumericSolve(symbolic);
				if (symbolic.getDefinition().isTopLevelCommand(Commands.Solve.name())) {
					symbolic.setWrapInNumeric(!checkAllRHSareIntegers(symbolic.getTwinGeo()));
				}
			}

			if (!containsUndefinedOrIsEmpty(symbolic)
					&& containsUndefinedOrIsEmpty(getOpposite(symbolic))) {
				if (symbolic.getDefinition().isTopLevelCommand(Commands.Solve.name())) {
					symbolic.setWrapInNumeric(!checkAllRHSareIntegers(symbolic.getTwinGeo()));
				}
			}
		}
	}

	/**
	 * Toggles between symbolic and numeric versions of Solve
	 *
	 * @param symbolic
	 *            GeoSymbolic that we want to change
	 */
	public static void toggleNumericSolve(GeoSymbolic symbolic) {
		Command topLevelCommand = symbolic.getDefinition().getTopLevelCommand();
		boolean isNSolve = Commands.NSolve.getCommand().equals(topLevelCommand.getName());
		Commands opposite = isNSolve
				? Commands.Solve : Commands.NSolve;

		topLevelCommand.setName(opposite.getCommand());
		if (isNSolve && topLevelCommand.getArgumentNumber() == 2
				&& topLevelCommand.getArgument(1).unwrap() instanceof Equation) {
			ExpressionNode eqn = topLevelCommand.removeLastArgument();
			symbolic.setExcludedEquation(eqn);
		} else if (!isNSolve && symbolic.getExcludedEquation() != null) {
			topLevelCommand.addArgument(symbolic.getExcludedEquation());
		}
		symbolic.computeOutput();
	}

	private static void toggleNumericWrap(GeoSymbolic symbolic) {
		boolean isNumeric = Commands.Numeric.getCommand()
				.equals(symbolic.getDefinition().getTopLevelCommand().getName());
		if (isNumeric) {
			unwrapFromNumeric(symbolic);
		} else {
			wrapInNumeric(symbolic);
		}
	}

	private static void wrapInNumeric(GeoSymbolic symbolic) {
		Command numeric = new Command(symbolic.getKernel(), "Numeric", false);
		numeric.addArgument(symbolic.getDefinition().deepCopy(symbolic.getKernel()));
		symbolic.setDefinition(numeric.wrap());
		symbolic.computeOutput();
	}

	private static void unwrapFromNumeric(GeoSymbolic symbolic) {
		symbolic.setDefinition(((Command) (symbolic.getDefinition().getLeft())).getArgument(0));
		symbolic.computeOutput();
	}

	/**
	 * Changes the symbolic flag of a geo or its parent algo
	 *
	 * @param geo
	 *            element that we want to change
	 * @return whether it's symbolic after toggle
	 */
	public static boolean toggleSymbolic(GeoElement geo) {
		if (geo instanceof HasSymbolicMode) {
			if (geo.getParentAlgorithm() instanceof AlgoSolve) {
				return !((AlgoSolve) geo.getParentAlgorithm()).toggleNumeric();
			}
			HasSymbolicMode hasSymbolicGeo = (HasSymbolicMode) geo;
			hasSymbolicGeo.setSymbolicMode(!hasSymbolicGeo.isSymbolicMode(), true);

			if (geo instanceof GeoSymbolic) {
				GeoSymbolic symbolic = (GeoSymbolic) geo;
				if (isSolve(symbolic) || isNumericOfSolve(symbolic)) {
					if (symbolic.shouldWrapInNumeric()) {
						toggleNumericWrap(symbolic);
					} else {
						toggleNumericSolve(symbolic);
					}
					symbolic.setDescriptionNeedsUpdateInAV(true);
				}
			}

			geo.updateRepaint();
			return hasSymbolicGeo.isSymbolicMode();

		}
		return false;
	}

	/**
	 * @param geo Element
	 * @return Whether the element is symbolic
	 */
	public static boolean isSymbolicMode(GeoElement geo) {
		return geo instanceof HasSymbolicMode && ((HasSymbolicMode) geo).isSymbolicMode();
	}

	/**
	 * Changes the engineering notation mode flag of a geo
	 * @param geo Element
	 * @return Whether the engineering notation mode flag is set to true after the toggle
	 */
	public static boolean toggleEngineeringNotation(GeoElement geo) {
		if (geo instanceof HasSymbolicMode) {
			HasSymbolicMode hasSymbolicGeo = (HasSymbolicMode) geo;
			hasSymbolicGeo.setEngineeringNotationMode(!hasSymbolicGeo.isEngineeringNotationMode());
			geo.updateRepaint();
			return hasSymbolicGeo.isEngineeringNotationMode();
		}
		return false;
	}

	/**
	 * @param geo Element
	 * @return Whether the element has the engineering notation mode activated
	 */
	public static boolean isEngineeringNotationMode(GeoElement geo) {
		return geo instanceof HasSymbolicMode
				&& ((HasSymbolicMode) geo).isEngineeringNotationMode();
	}

	/**
	 * @param expression to be checked
	 * @return true if numeric approximation should be calculated
	 */
	public static boolean shouldComputeNumericValue(ExpressionValue expression) {
		if (expression != null && expression.isNumberValue()
				&& !(expression.unwrap() instanceof BooleanValue)) {
			ExpressionValue unwrapped = expression.unwrap();
			if (expression.wrap().containsGeoDummyVariable()) {
				return false;
			}
			if (unwrapped instanceof NumberValue) {
				return ((NumberValue) unwrapped).isDefined();
			}
			return true;
		}
		return false;
	}
}
