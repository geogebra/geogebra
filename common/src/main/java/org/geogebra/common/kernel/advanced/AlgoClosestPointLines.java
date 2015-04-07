package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoIntersectLines;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoLine;

public class AlgoClosestPointLines extends AlgoIntersectLines {

	public AlgoClosestPointLines(Construction cons, String label, GeoLine g,
			GeoLine h) {
		super(cons, label, g, h);
	}

	@Override
	public Commands getClassName() {
		return Commands.ClosestPoint;
	}
}
