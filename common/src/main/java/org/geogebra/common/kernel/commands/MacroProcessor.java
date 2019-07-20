/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;

/**
 * Processes the use of macros from the command line.
 */
public class MacroProcessor extends CommandProcessor {

	/**
	 * Creates new macro processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public MacroProcessor(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		// resolve command arguments
		GeoElement[] arg = resArgs(c);
		Macro macro = c.getMacro();

		TestGeo[] macroInputTypes = macro.getInputTypes();

		// wrong number of arguments
		if (arg.length != macroInputTypes.length) {
			boolean lengthOk = false;

			// check if we have a polygon in the arguments
			// if yes, let's use its points
			if (arg.length > 0 && arg[0].isGeoPolygon()) {
				GeoPointND[] points = ((GeoPolygon) arg[0]).getPoints();
				arg = new GeoElement[points.length];
				for (int i = 0; i < points.length; i++) {
					arg[i] = (GeoElement) points[i];
				}
				lengthOk = arg.length == macroInputTypes.length;
			}

			if (!lengthOk) {
				StringBuilder sb = new StringBuilder();
				sb.append(loc.getMenu("Macro"));
				sb.append(" ");
				sb.append(macro.getCommandName());
				sb.append(":\n");
				sb.append(Errors.IllegalArgumentNumber.getError(loc));
				sb.append(": ");
				sb.append(arg.length);
				sb.append("\n\nSyntax:\n");
				sb.append(macro.toString());
				throw new MyError(loc, sb.toString());
			}
		}

		// check whether the types of the arguments are ok for our macro
		for (int i = 0; i < macroInputTypes.length; i++) {
			if (!macroInputTypes[i].check(arg[i])) {
				StringBuilder sb = new StringBuilder();
				sb.append(loc.getMenu("Macro"));
				sb.append(" ");
				sb.append(macro.getCommandName());
				sb.append(":\n");
				sb.append(Errors.IllegalArgument.getError(loc));
				sb.append(": ");
				sb.append(arg[i].getNameDescription());
				sb.append("\n\nSyntax:\n");
				sb.append(macro.toString());
				throw new MyError(loc, sb.toString());
			}
		}

		// if we get here we have the right arguments for our macro
		return kernel.useMacro(c.getLabels(), macro, arg);
	}
}