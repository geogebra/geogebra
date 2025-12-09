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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Common class for integral drawables
 */
public abstract class DrawFunctionArea extends Drawable {
	/**
	 * @param cmd
	 *            command
	 * @param i
	 *            index
	 * @return i-th argument of command as function
	 */
	protected GeoFunction asFunction(Command cmd, int i) {
		ExpressionValue arg0 = cmd.getArgument(i).unwrap();
		if (arg0 instanceof GeoCasCell) {
			// https://help.geogebra.org/topic/integraaltussen-wordt-grafisch-verkeerd-weergegeven-via-cas
			return (GeoFunction) ((GeoCasCell) arg0).getTwinGeo();
		}
		return new GeoFunction(
				view.getApplication().getKernel().getConstruction(),
				new Function(geo.getKernel(),
						cmd.getArgument(i).wrap().replaceCasCommands()));
	}

	/**
	 * @param cmd
	 *            command
	 * @param i
	 *            index
	 * @return i-th argument of command as MyDouble
	 */
	protected NumberValue asDouble(Command cmd, int i) {
		ExpressionValue arg2 = cmd.getArgument(i).unwrap();
		if (arg2 instanceof GeoCasCell) {
			return new MyDouble(cmd.getKernel(),
					((GeoCasCell) arg2).getTwinGeo().evaluateDouble());
		}
		return new MyDouble(cmd.getKernel(), cmd.getArgument(i).wrap()
				.replaceCasCommands().evaluateDouble());
	}
}
