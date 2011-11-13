package geogebra.kernel.discrete;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.MyPoint;
import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;
import geogebra.kernel.discrete.tsp.method.tsp.BranchBound;
import geogebra.kernel.discrete.tsp.method.tsp.Opt3;
import geogebra.kernel.discrete.tsp.model.Node;
import geogebra.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

public class AlgoTravelingSalesman extends AlgoHull{

	public AlgoTravelingSalesman(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoTravelingSalesman";
    }
    
    protected void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size < 3) {
    		locus.setUndefined();
    		return;
    	} 
    	
        if (vl == null) vl = new ArrayList<VPoint>();
        else vl.clear();
   	
		double inhom[] = new double[2];
		
		Opt3 opt3 = new Opt3();
		final BranchBound construction = new BranchBound(500, opt3);

		Node[] nodes = new Node[size];

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				nodes[i] = new Node(inhom[0], inhom[1]);
			}
		}
        
        int[] route = construction.method(nodes);
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
       for (int i = 0 ; i < size ; i++) {
        	Node n = nodes[route[i]];
            al.add(new MyPoint(n.getX(), n.getY(), i != 0));

        }
        
       // join up
   	Node n = nodes[route[0]];
      al.add(new MyPoint(n.getX(), n.getY(), true));

		locus.setPoints(al);
		locus.setDefined(true);

        
       
    }


}
