package geogebra.common.kernel.barycentric;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * @author Darko Drakulic
 * @version 17-10-2011
 * 
 *  This class make point with given weights respevt to given polygon. 
 *          
 */

public class AlgoBarycenter extends AlgoElement {

		private GeoList poly; // input
	private GeoList list; // input
	private GeoPoint point; // output
	/**
	 * 
	 * @param cons construction
	 * @param label label
	 * @param A list of points
	 * @param B list of weights
	 */
	public AlgoBarycenter(Construction cons, String label, GeoList A, GeoList B) {
		super(cons);
		this.poly = A;
		this.list = B;
		point = new GeoPoint(cons);
		setInputOutput();
		compute();		
		point.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoBarycenter;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = poly;
		input[1] = list;
		
		setOutputLength(1);
		setOutput(0, point);
		setDependencies(); // done by AlgoElement
	}
	/**
	 * Returns the resulting point
	 * @return the resulting point
	 */
	public GeoPoint getResult() {
		return point;
	}

	@Override
	public final void compute() {
		
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

	// TODO Consider locusequability
	
	
	
	
}