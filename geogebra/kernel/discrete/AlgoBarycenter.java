package geogebra.kernel.discrete;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;


/**
 * @author Darko Drakulic
 * @version 17-10-2011
 * 
 *  This class make point with given weights respevt to given polygon. 
 *          
 */

public class AlgoBarycenter extends AlgoElement {

	public static final long serialVersionUID = 1L;
	private GeoList poly; // input
	private GeoList list; // input
	private GeoPoint point; // output
	
	public AlgoBarycenter(Construction cons, String label, GeoList A, GeoList B) {
		super(cons);
		this.poly = A;
		this.list = B;
		point = new GeoPoint(cons);
		setInputOutput();
		compute();		
		point.setLabel(label);
	}

	public String getClassName() {
		return "AlgoBarycenter";
	}

	// for AlgoElement
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = poly;
		input[1] = list;
		
		setOutputLength(1);
		setOutput(0, point);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getResult() {
		return point;
	}

	protected final void compute() {
		
		int size = list.size();
		if(!list.isDefined() || size == 0)
		{
			point.setUndefined();
			return;
		}
		if(list.size() != poly.size()){
    		point.setUndefined();
    		return;   		
    	}
		if (!list.getGeoElementForPropertiesDialog().isGeoNumeric() ||
				!poly.getGeoElementForPropertiesDialog().isGeoPoint()) {
    		point.setUndefined();
    		return;   		
    	}
		
		
		int numberOfVertices = poly.size();
		double x = 0, y = 0, sum = 0;
		for(int i=0; i<numberOfVertices; i++)
		{
			x += ((GeoPoint)poly.get(i)).inhomX * ((GeoNumeric)(list.get(i))).getDouble();
			y += ((GeoPoint)poly.get(i)).inhomY * ((GeoNumeric)(list.get(i))).getDouble();
			sum += ((GeoNumeric)(list.get(i))).getDouble();
		}
		
		point.setCoords(x/sum, y/sum, 1);
	}
	
	
	
	
}