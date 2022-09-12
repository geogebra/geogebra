package org.geogebra.common.util;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoSymbolic;

public class SymbolicUtil {

	public static boolean isSymbolicSolve(GeoSymbolic symbolic) {
		Command topLevelCommand = symbolic.getDefinition().getTopLevelCommand();
		return topLevelCommand != null
				&& (Commands.Solve.getCommand().equals(topLevelCommand.getName())
				|| Commands.NSolve.getCommand().equals(topLevelCommand.getName()));
	}

	public static boolean isSymbolicSolveDiffers(GeoSymbolic symbolic) {
		String textOriginal = getValueString(symbolic);
		String textOpposite = getOppositeValueString(symbolic);
		return isDefined(textOriginal) && isDefined(textOpposite)
				&& !textOriginal.equals(textOpposite);
	}

	private static String getValueString(GeoSymbolic symbolic) {
		return symbolic.toValueString(StringTemplate.defaultTemplate);
	}

	private static boolean isDefined(String valueString) {
		return !GeoFunction.isUndefined(valueString);
	}

	private static GeoSymbolic getOpposite(GeoSymbolic symbolic) {
		GeoSymbolic opposite = new GeoSymbolic(symbolic.getConstruction());
		opposite.setDefinition(symbolic.getDefinition().deepCopy(symbolic.getKernel()));
		toggleNumeric(opposite);
		return opposite;
	}

	private static String getOppositeValueString(GeoSymbolic symbolic) {
		return getValueString(getOpposite(symbolic));
	}


	/**
	 * @param symbolic GeoSymbolic input
	 * handles the showing/hiding of Solve/NSolve variants
	 */
	public static void handleSolveNSolve(GeoSymbolic symbolic) {
		if (isSymbolicSolve(symbolic)) {
			if (!isDefined(getValueString(symbolic))
					&& isDefined(getOppositeValueString(symbolic))) {
				toggleNumeric(symbolic);
				if (Commands.Solve.name()
						.equals(symbolic.getDefinition().getTopLevelCommand().getName())) {
					symbolic.wrapInNumeric();
				}
			}
		}
	}

	public static void toggleNumeric(GeoSymbolic symbolic) {
		Commands opposite = Commands.NSolve.getCommand()
				.equals(symbolic.getDefinition().getTopLevelCommand().getName())
				? Commands.Solve : Commands.NSolve;

		symbolic.getDefinition().getTopLevelCommand().setName(opposite.getCommand());
		symbolic.computeOutput();
	}
}
