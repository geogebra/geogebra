package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation;
import java.util.ArrayList;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

public class VCluster extends ArrayList<VPoint> {
    
	private static final long serialVersionUID = 1L;

	public VPoint calculateAveragePoint() {
        VPoint average = new VPoint(0,0);
        for ( VPoint point : this ) {
            average.x += point.x;
            average.y += point.y;
        }
        average.x /= super.size();
        average.y /= super.size();
        return average;
    }
    
}
