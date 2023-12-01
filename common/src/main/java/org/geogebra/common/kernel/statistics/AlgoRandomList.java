package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class AlgoRandomList extends AlgoRandomUniformList {
	private GeoNumberValue a;
	private GeoNumberValue b;
	private GeoNumberValue length;
	private GeoList list;

	/**
	 * @param cons construction
	 * @param label output label
	 * @param a lower bound for uniform distribution
	 * @param b upper bound for uniform distribution
	 * @param length
	 */
	public AlgoRandomList(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue length) {
		super(cons, label, a, b, length);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Random;
	}

	@Override
	public double getRandomNumber(double a, double b) {
		return Double.valueOf(cons.getApplication().getRandomIntegerBetween(a, b));
	}
}
