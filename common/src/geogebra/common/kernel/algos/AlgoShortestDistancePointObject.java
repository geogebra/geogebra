package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;

public class AlgoShortestDistancePointObject extends AlgoElement implements DistanceAlgo {
	
	private GeoPoint point;
	private GeoElement object;
	private GeoNumeric distance;

    public AlgoShortestDistancePointObject(Construction cons, String label, GeoPoint p, GeoElement o) {
    	super(cons);
    	point = p;
    	object = o;
    	
    	distance = new GeoNumeric(cons);
    	
    	if (!o.isGeoFunction()) {
    		AlgoElement algo;
    		if (o.isGeoPoint()) 
    			algo = new AlgoDistancePoints(cons, label, p, (GeoPoint) o);
    		else 
    			algo = new AlgoDistancePointObject(cons, label, p, o);
    		cons.removeFromConstructionList(algo);
    		distance = ((DistanceAlgo) algo).getDistance();
    		setInputOutput();
    		distance.setLabel(label);
    		return;
    	}
    	compute();
    	setInputOutput();
    	distance.setLabel(label);
    }

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = point;
		input[1] = object;
		setOnlyOutput(distance);
		setDependencies(); // by AlgoElement
	}

	@Override
	public void compute() {
		GeoFunction fun = (GeoFunction) object;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.ShortestDistance;
	}
	
	public GeoNumeric getResult() {
		return distance;

	}

	public GeoNumeric getDistance() {
		return getResult();
	}

}
