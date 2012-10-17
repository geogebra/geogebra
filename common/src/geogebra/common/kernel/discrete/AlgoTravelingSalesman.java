package geogebra.common.kernel.discrete;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.discrete.tsp.method.tsp.BranchBound;
import geogebra.common.kernel.discrete.tsp.method.tsp.Opt3;
import geogebra.common.kernel.discrete.tsp.model.Node;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

public class AlgoTravelingSalesman extends AlgoHull {

	public AlgoTravelingSalesman(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public Algos getClassName() {
        return Algos.AlgoTravelingSalesman;
    }
    
    public void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size < 3) {
    		locus.setUndefined();
    		return;
    	} 
    	
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
