package org.geogebra.common.exam.restrictions.ib;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;

public final class IBCommandArgumentFilter extends BaseCommandArgumentFilter {

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor) throws MyError {
		if (isCommand(command, Commands.Tangent)) {
			checkTangentCommand(command, commandProcessor);
		} else if (isCommand(command, Commands.Integral)) {
			if (command.getArgumentNumber() != 3) {
				throw commandProcessor.argNumErr(command, command.getArgumentNumber());
			}
		} else if (isCommand(command, Commands.Invert)) {
			GeoElement[] elements = commandProcessor.resArgs(command);
			if (elements.length == 1 && elements[0] instanceof GeoFunction) {
				throw commandProcessor.argErr(command, elements[0]);
			}
		}
	}

	private void checkTangentCommand(Command command, CommandProcessor processor) throws MyError {
		GeoElement[] elements = processor.resArgs(command);
		if (elements.length == 2) {
			// Point, Conic and inverse
			if ((elements[0].isGeoPoint() && elements[1].isGeoConic())
					|| (elements[0].isGeoConic() && elements[1].isGeoPoint())
					// Line, Conic
					|| (elements[0].isGeoLine() && elements[1].isGeoConic())
					// Conic, Conic
					|| (elements[0].isGeoConic() && elements[1].isGeoConic())
					// Point, Implicit Curve and inverse
					|| (elements[0].isGeoPoint() && elements[1].isGeoImplicitCurve()
					|| (elements[0].isGeoImplicitCurve() && elements[1].isGeoPoint()))) {
				throw processor.argErr(command, elements[0]);
			}
		}
	}
}
