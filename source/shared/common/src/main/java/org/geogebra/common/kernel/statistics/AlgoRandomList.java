package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

public class AlgoRandomList extends AlgoRandomUniformList {

	/**
	 * @param cons construction
	 * @param label output label
	 * @param a lower bound for uniform distribution
	 * @param b upper bound for uniform distribution
	 * @param length of list
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
		return cons.getApplication().getRandomIntegerBetween(a, b);
	}
}
