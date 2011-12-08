package geogebra.kernel.discrete;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoList;

public class AlgoConvexHull extends AlgoHull{

	public AlgoConvexHull(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoConvexHull";
    }

}
