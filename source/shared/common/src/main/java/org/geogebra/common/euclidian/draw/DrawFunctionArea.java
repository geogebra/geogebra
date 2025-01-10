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
