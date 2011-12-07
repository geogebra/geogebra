package geogebra3D.kernel3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 * @author ggb3D
 *
 */
public class AlgoQuadricSide extends AlgoQuadric {
	
	
	private boolean isHelperAlgo;
	

	
	/**
	 * @param c construction
	 * @param inputQuadric 
	 */
	public AlgoQuadricSide(Construction c, GeoQuadric3DLimited inputQuadric, boolean isHelperAlgo) {		
		super(c,inputQuadric,null,new AlgoQuadricComputerSide());

		this.isHelperAlgo=isHelperAlgo;
		
		setInputOutput(new GeoElement[] {inputQuadric}, new GeoElement[] {getQuadric()});
		
		compute();
	}

	public AlgoQuadricSide(Construction c, String label, GeoQuadric3DLimited inputQuadric) {		

		this(c,inputQuadric,false);
		getQuadric().setLabel(label);
	}
	
	
	private GeoQuadric3DLimited getInputQuadric(){
		return (GeoQuadric3DLimited) getSecondInput();
	}
	
	

	public void compute() {
				
		//check origin
		if (!getInputQuadric().isDefined()){
			getQuadric().setUndefined();
			return;
		}
		
		//compute the quadric
		getQuadric().setDefined();
		getQuadric().setType(getInputQuadric().getType());
		getComputer().setQuadric(getQuadric(), getInputQuadric().getMidpoint3D(), getInputQuadric().getEigenvec3D(2), getInputQuadric().getHalfAxis(0));
		((GeoQuadric3DPart) getQuadric()).setLimits(getInputQuadric().getMin(), getInputQuadric().getMax());
	
		((GeoQuadric3DPart) getQuadric()).calcArea();
		
	
	}


	public void remove() {
		super.remove();
		if (isHelperAlgo)
			getInputQuadric().remove();
	}       


	protected Coords getDirection() {
		return null;
	}
	
	/*
    final public String toString() {
    	return app.getPlain("SideOfABetweenBC",((GeoElement) getInputQuadric()).getLabel(),point.getLabel(),pointThrough.getLabel());
    }
	 */
	
	

}
