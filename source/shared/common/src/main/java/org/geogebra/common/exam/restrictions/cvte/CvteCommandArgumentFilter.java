package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.main.MyError;

public final class CvteCommandArgumentFilter implements CommandArgumentFilter {

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor)
			throws MyError {
		String internalCommandName = command.getName();
		if (Commands.Circle.name().equals(internalCommandName)) {
			checkCircle(command, commandProcessor);
		} else if (Commands.Extremum.name().equals(internalCommandName)) {
			checkExtremum(command, commandProcessor);
		} else if (Commands.Root.name().equals(internalCommandName)) {
			checkRoot(command, commandProcessor);
		} else if (Commands.Intersect.name().equals(internalCommandName)) {
			checkIntersect(command, commandProcessor);
		}
	}

	private void checkCircle(Command command, CommandProcessor commandProcessor) throws MyError {
		GeoElement[] arguments = commandProcessor.resArgs(command);
		// only Circle(<Center>, <Radius>) allowed
		if (arguments.length != 2) {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
		GeoElement firstArgument = arguments[0];
		if (!firstArgument.isGeoPoint()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		GeoElement secondArgument = arguments[1];
		if (!secondArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, secondArgument);
		}
	}

	private void checkExtremum(Command command, CommandProcessor commandProcessor) throws MyError {
		// only Extremum(<Function>, <Start x-Value>, <End x-Value>) allowed
		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length != 3) {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
		GeoElement firstArgument = arguments[0];
		if (!firstArgument.isGeoFunction()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		GeoElement secondArgument = arguments[1];
		if (!secondArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, secondArgument);
		}
		GeoElement thirdArgument = arguments[2];
		if (!thirdArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, thirdArgument);
		}
	}

	private void checkRoot(Command command, CommandProcessor commandProcessor) throws MyError {
		// only Root( <Function>, <Start x-Value>, <End x-Value> ) allowed
		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length != 3) {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
		GeoElement firstArgument = arguments[0];
		if (!firstArgument.isGeoFunction()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		GeoElement secondArgument = arguments[1];
		if (!secondArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, secondArgument);
		}
		GeoElement thirdArgument = arguments[2];
		if (!thirdArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, thirdArgument);
		}
	}

	private void checkIntersect(Command command, CommandProcessor commandProcessor) throws MyError {
		// For Intersect( <Object>, <Object> ) and Intersect( <Object>, <Object>, <Number> ),
		// the only Objects allowed are those that can be displayed in 2D graphics.
		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length == 2 || arguments.length == 3) {
			GeoElement firstArgument = arguments[0];
			if (!isDisplayableIn2DGraphics(firstArgument)) {
				throw commandProcessor.argErr(command, firstArgument);
			}

			GeoElement secondArgument = arguments[1];
			if (!isDisplayableIn2DGraphics(secondArgument)) {
				throw commandProcessor.argErr(command, secondArgument);
			}
		}
	}

	private boolean isDisplayableIn2DGraphics(GeoElement geoElement) {
		return geoElement instanceof GeoLine
				|| geoElement instanceof GeoFunction
				|| geoElement instanceof GeoImplicitCurve || geoElement instanceof GeoCurveCartesian
				|| geoElement instanceof GeoConic;
	}
}