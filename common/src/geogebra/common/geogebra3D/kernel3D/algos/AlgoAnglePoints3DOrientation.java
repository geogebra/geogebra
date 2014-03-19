package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
* angle for three points, oriented
* @author  mathieu
*/
public class AlgoAnglePoints3DOrientation extends AlgoAnglePoints3D{
	
	private GeoDirectionND orientation;

	AlgoAnglePoints3DOrientation(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		super(cons, label, A, B, C, orientation);
	}
	
	public AlgoAnglePoints3DOrientation(Construction cons, GeoDirectionND orientation) {
		super(cons);
		this.orientation = orientation;
	}

	@Override
	protected void setInput(GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation){

		super.setInput(A, B, C, orientation);
		this.orientation = orientation;
	}
	
	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}
	
	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = (GeoElement) getA();
		input[1] = (GeoElement) getB();
		input[2] = (GeoElement) getC();
		input[3] = (GeoElement) orientation;

		setOutputLength(1);
		setOutput(0, getAngle());
		setDependencies(); // done by AlgoElement
	}


    @Override
	public void compute() {
    	
    	super.compute();
    	
    	if (!getAngle().isDefined()){
    		return;
    	}
    	
    	if (vn.dotproduct(orientation.getDirectionInD3()) < 0){
    		GeoAngle a = getAngle();
    		a.setValue(2*Math.PI-a.getValue());
    		vn = vn.mul(-1);
    	}
    }

    @Override
	public String toString(StringTemplate tpl) {

		return loc.getPlain("AngleBetweenABCOrientedByD", getA().getLabel(tpl),
				getB().getLabel(tpl), getC().getLabel(tpl), orientation.getLabel(tpl));
	}
    
    
}
