package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.ArrayList;

public class AlgoFirstLocus extends AlgoFirst {

	public AlgoFirstLocus(Construction cons, String label, GeoLocus inputLocus,
			GeoNumeric n) {
		super(cons, label, inputLocus, n);


	}

    @Override
	public Commands getClassName() {
        return Commands.First;
    }
    
    @Override
	public final void compute() {
    	
    	ArrayList<MyPoint> points = ((GeoLocus)inputList).getPoints();
    	
    	size = points.size();
    	int outsize = n == null ? 1 : (int)n.getDouble();
    	
    	if (!inputList.isDefined() ||  size == 0 || outsize < 0 || outsize > size) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	
    	if (outsize == 0) {
        	outputList.clear();
    		return; // return empty list
    	}
    	
    	int outputListSize = outputList.size();
    	
    	// remove extra elements
    	if (outputList.size() > outsize) {
    		for (int i = outputListSize - 1 ; i >= outsize ; i-- ) {
	    		outputList.remove(i);
    		}
    	}
    	
    	// avoid label creation
    	boolean oldSuppressLabels = cons.isSuppressLabelsActive();
    	cons.setSuppressLabelCreation(true);
    	
    	for (int i=0 ; i < outsize ; i++) {
    		MyPoint mp = points.get(i);
   		
    		if (i < outputList.size()) {
    			// recycle existing GeoPoint
    			// important to avoid memory problems
    			// (and quicker?)
    			GeoPoint p = (GeoPoint)outputList.get(i); 
    			p.setCoords(mp.x, mp.y, 1.0);
    			p.updateRepaint();
    		} else {
	    		GeoPoint p = new GeoPoint(cons, null, mp.x, mp.y, 1.0);
	    		outputList.add(p);
    		}
    	}
    	
    	cons.setSuppressLabelCreation(oldSuppressLabels);

   }

}
