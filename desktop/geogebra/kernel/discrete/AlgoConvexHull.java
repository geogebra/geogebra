package geogebra.kernel.discrete;

import geogebra.common.kernel.geos.GeoList;
import geogebra.kernel.Construction;

public class AlgoConvexHull extends AlgoHull{

	public AlgoConvexHull(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoConvexHull";
    }

}
