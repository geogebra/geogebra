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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Order determines order in Two Variable Regression Analysis menu For each
 * String, getMenu(s) must be defined
 *
 * @author Michael Borcherds
 */
public enum Regression {
	NONE("None", Commands.FitPoly),
	LINEAR("Linear", Commands.FitPoly),
	LOG("Log", Commands.FitLog),
	POLY("Polynomial", Commands.FitPoly),
	POW("Power", Commands.FitPow),
	EXP("Exponential", Commands.FitExp),
	GROWTH("Growth", Commands.FitGrowth),
	SIN("Sin", Commands.FitSin),
	LOGISTIC("Logistic", Commands.FitLogistic);

	private final Commands command;
	// getMenu(label) must be defined
	private final String label;

	/**
	 * @param label label
	 * @param command command
	 */
	Regression(String label, Commands command) {
		this.label = label;
		this.command = command;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * @param kernel kernel
	 * @param order degree of polynomial for polynomial regression
	 * @param list list of points
	 * @return command
	 */
	public Command buildCommand(Kernel kernel, int order, ExpressionValue list) {
		Command cmd = new Command(kernel, command.name(), false);
		cmd.addArgument(list.wrap());
		if (command == Commands.FitPoly) {
			int degree = this == POLY ? order : 1;
			cmd.addArgument(new GeoNumeric(kernel.getConstruction(), degree).wrap());
		}
		return cmd;
	}

	public Commands getCommand() {
		return command;
	}

}
