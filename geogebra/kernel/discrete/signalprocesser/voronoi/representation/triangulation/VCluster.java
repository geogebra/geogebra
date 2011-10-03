package geogebra.kernel.discrete.signalprocesser.voronoi.representation.triangulation;
import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;

import java.util.ArrayList;

public class VCluster extends ArrayList<VPoint> {
    
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
