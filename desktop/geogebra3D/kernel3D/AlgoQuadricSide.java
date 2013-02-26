package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;

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
		
		((GeoQuadric3DPart) getQuadric()).setFromMeta(inputQuadric);
		
		compute();
	}

	public AlgoQuadricSide(Construction c, String label, GeoQuadric3DLimited inputQuadric) {		

		this(c,inputQuadric,false);
		getQuadric().setLabel(label);
	}
	
	
	private GeoQuadric3DLimited getInputQuadric(){
		return (GeoQuadric3DLimited) getSecondInput();
	}
	
	

	@Override
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


	@Override
	public void remove() {
		if(removed)
			return;
		super.remove();
		if (isHelperAlgo)
			getInputQuadric().remove();
	}       


	@Override
	protected Coords getDirection() {
		return null;
	}
	
	/*
    final public String toString() {
    	return loc.getPlain("SideOfABetweenBC",((GeoElement) getInputQuadric()).getLabel(),point.getLabel(),pointThrough.getLabel());
    }
	 */

	@Override
	public Commands getClassName() {
        return Commands.QuadricSide;
    }
	

}
