package geogebra.common.kernel.implicit;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * Computes implicit polynomial through given points
 *
 */
public class AlgoImplicitPolyThroughPoints extends AlgoElement 
{
	private GeoList P; // input points      
    private GeoImplicitPoly implicitPoly; // output 
	
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param p points on polynomial
	 */
	public AlgoImplicitPolyThroughPoints(Construction cons, String label, GeoList p)
	{
		super(cons);
		this.P = p;
		
		implicitPoly = new GeoImplicitPoly(cons);
		
		setInputOutput();
		compute();

		implicitPoly.setLabel(label);
	}
	
	/**
	 * @return resulting polynomial
	 */
	public GeoImplicitPoly getImplicitPoly() {
		return implicitPoly;
	}
	
	/**
	 * @return input list of points
	 */
	public GeoList getP() {
		return P;
	}
	
	@Override
	protected void setInputOutput() {
		input = P.getGeoElements();
		setOnlyOutput(implicitPoly);
		setDependencies();
	}

	@Override
	public void compute() {
		implicitPoly.throughPoints(P);
	}
	
	@Override
	public Algos getClassName() {
		 return Algos.AlgoImplicitPolyThroughPoints;
	}

	// TODO Consider locusequability

}
