package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.tsp.method.tsp.BranchBound;
import org.geogebra.common.kernel.discrete.tsp.method.tsp.Opt3;
import org.geogebra.common.kernel.discrete.tsp.model.Node;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoTravelingSalesman extends AlgoHull {

	public AlgoTravelingSalesman(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public Commands getClassName() {
        return Commands.TravelingSalesman;
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
