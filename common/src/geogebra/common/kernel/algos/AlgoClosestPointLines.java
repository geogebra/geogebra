package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoLine;

public class AlgoClosestPointLines extends AlgoIntersectLines{

	public AlgoClosestPointLines(Construction cons, String label, GeoLine g,
			GeoLine h) {
		super(cons, label, g, h);
	}
	public Algos getClassName(){
		return Algos.AlgoClosestPoint;
	}
}
