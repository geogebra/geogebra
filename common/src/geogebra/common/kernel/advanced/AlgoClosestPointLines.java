package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoIntersectLines;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoLine;

public class AlgoClosestPointLines extends AlgoIntersectLines{

	public AlgoClosestPointLines(Construction cons, String label, GeoLine g,
			GeoLine h) {
		super(cons, label, g, h);
	}
	@Override
	public Commands getClassName() {
		return Commands.ClosestPoint;
	}
}
