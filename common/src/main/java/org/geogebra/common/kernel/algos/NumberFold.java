package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Helper for Sum/Product when numbers are involved
 */
public class NumberFold implements FoldComputer {

	private GeoNumeric result;
	private double x;

	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return this.result = new GeoNumeric(cons);
	}

	public void add(GeoElement p, Operation op) {
		if (op == Operation.MULTIPLY) {
			x *= ((GeoNumberValue) p).getDouble();
		} else {
			x += ((GeoNumberValue) p).getDouble();
		}
	}

	public void setFrom(GeoElement geoElement, Kernel kernel) {
		x = ((GeoNumberValue) geoElement).getDouble();
	}

	public boolean check(GeoElement geoElement) {
		return geoElement instanceof GeoNumberValue;
	}

	public void finish() {
		result.setValue(x);

	}

}
