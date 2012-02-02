package geogebra.common.kernel.discrete;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoList;

public class AlgoConvexHull extends AlgoHull{

	public AlgoConvexHull(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public Algos getClassName() {
        return Algos.AlgoConvexHull;
    }

}
