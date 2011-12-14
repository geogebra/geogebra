package geogebra.common.kernel.implicit;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

public class AlgoImplicitPolyThroughPoints extends AlgoElement 
{
	private GeoList P; // input points      
    private GeoImplicitPoly implicitPoly; // output 
	
	public AlgoImplicitPolyThroughPoints(Construction cons, String label, GeoList p)
	{
		super(cons);
		this.P = p;
		
		implicitPoly = new GeoImplicitPoly(cons);
		
		setInputOutput();
		compute();

		implicitPoly.setLabel(label);
	}
	
	public GeoImplicitPoly getImplicitPoly() {
		return implicitPoly;
	}
	
	public GeoList getP() {
		return P;
	}
	
	@Override
	protected void setInputOutput() {
		input = P.getGeoElements();
		output = new GeoElement[1];
		output[0] = implicitPoly;
		setDependencies();
	}

	@Override
	public void compute() {
		implicitPoly.throughPoints(P);
	}
	
	@Override
	public String getClassName() {
		 return "AlgoImplicitPolyThroughPoints";
	}

}
