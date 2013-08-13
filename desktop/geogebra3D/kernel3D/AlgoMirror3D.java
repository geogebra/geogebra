package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoMirror;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for mirror at 3D point / 3D line
 * @author mathieu
 *
 */
public class AlgoMirror3D extends AlgoMirror {
	
	private GeoPlane3D mirrorPlane;

	/**
	 * mirror at point
	 * @param cons construction
	 * @param in input
	 * @param point mirror point
	 */
	public AlgoMirror3D(Construction cons, GeoElement in, GeoPointND point) {
		super(cons, in, point);
	}
	
	/**
	 * mirror at line
	 * @param cons construction
	 * @param in input
	 * @param line mirror line
	 */
	public AlgoMirror3D(Construction cons, GeoElement in, GeoLineND line) {
		super(cons, in, line);
	}
	
	/**
	 * mirror at line
	 * @param cons construction
	 * @param in input
	 * @param line mirror line
	 */
	public AlgoMirror3D(Construction cons, GeoElement in, GeoPlane3D plane) {
    	super(cons);     
        mirrorPlane = plane;
    	endOfConstruction(cons, in, plane);
	}
	
	
    @Override
	protected void computeRegardingMirror(){
    	if (mirror == mirrorPlane) {
        	((MirrorableAtPlane) out).mirror(mirrorPlane);
        } else  {
        	super.computeRegardingMirror();
        }
        
    }
    
    @Override
	protected GeoElement copy(GeoElement geo){
    	if (mirror.isGeoElement3D())
    		return ((Kernel3D) kernel).copy3D(geo);
		return super.copy(geo);
    }
    
    @Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if((geo instanceof GeoFunction || geo instanceof GeoCurveCartesian) && mirror.isGeoElement3D())
			return new GeoCurveCartesian3D(cons);

		return super.getResultTemplate(geo);
	}
    
    @Override
	protected GeoElement copyInternal(Construction cons, GeoElement geo){
    	if (mirror.isGeoElement3D())
    		return ((Kernel3D) kernel).copyInternal3D(cons,geo);
		return super.copyInternal(cons,geo);
    }
    
	@Override
	protected void setOutGeo(){
    	if(inGeo instanceof GeoFunction && mirror.isGeoElement3D()){
    		AlgoTransformation3D.toGeoCurveCartesian(kernel, (GeoFunction)inGeo, (GeoCurveCartesian3D)outGeo);
    	} else{   	
    		super.setOutGeo();
    	}
	}
	
    @Override
	protected Coords getMirrorCoords(){
    	return mirrorPoint.getInhomCoordsInD(3);
    }

}
