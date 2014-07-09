package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPointND;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoEllipseHyperbolaFociPoint3D extends AlgoEllipseHyperbolaFociPointND {

	public AlgoEllipseHyperbolaFociPoint3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, GeoDirectionND orientation, 
            int type) {
		super(cons, label, A, B, C, orientation, type);
	}
	
	public AlgoEllipseHyperbolaFociPoint3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, 
            int type) {
		this(cons, label, A, B, C, null, type);
	}
	
    @Override
	protected GeoConicND newGeoConic(Construction cons){
    	GeoConic3D ret = new GeoConic3D(cons);
    	ret.setCoordSys(new CoordSys(2));
    	return ret;
    }
    
    private GeoPoint A2d, B2d, C2d;
    
    @Override
	protected void setInputOutput() {
    	
    	super.setInputOutput();
    	
    	A2d = new GeoPoint(cons); 
    	B2d = new GeoPoint(cons); 
    	C2d = new GeoPoint(cons);
    }
    
    
    @Override
	protected GeoPoint getA2d(){
    	return A2d;
    }
    
  
    @Override
	protected GeoPoint getB2d(){
    	return B2d;
    }
    
    @Override
	protected GeoPoint getC2d(){
    	return C2d;
    }
    
    /**
     * @param cs ellipse coord sys
     * @param Ac first focus coords
     * @param Bc second focus coords
     * @param Cc point on ellipse coords
     * @return true if coord sys is possible
     */
    protected boolean setCoordSys(CoordSys cs, Coords Ac, Coords Bc, Coords Cc){
    	
    	// set the coord sys
    	cs.addPoint(Ac);
       	cs.addPoint(Bc);
       	cs.addPoint(Cc);
       	
       	return cs.makeOrthoMatrix(false, false);
    }
    
    @Override
	public void compute() {
    	
    	CoordSys cs = conic.getCoordSys();
    	cs.resetCoordSys();
    	
    	Coords Ac = A.getInhomCoordsInD(3);
    	Coords Bc = B.getInhomCoordsInD(3);
    	Coords Cc = C.getInhomCoordsInD(3);
    	
       	
       	if (!setCoordSys(cs, Ac, Bc, Cc)){
       		conic.setUndefined();
       		return;
       	}

       	// project the points on the coord sys
       	CoordMatrix4x4 matrix = cs.getMatrixOrthonormal();
       	Coords[] project = Ac.projectPlane(matrix);
       	A2d.setCoords(project[1].getX(), project[1].getY(), project[1].getW());
       	project = Bc.projectPlane(matrix);
       	B2d.setCoords(project[1].getX(), project[1].getY(), project[1].getW());
       	project = Cc.projectPlane(matrix);
       	C2d.setCoords(project[1].getX(), project[1].getY(), project[1].getW());
           	
    	
    	super.compute();
    }
    
    
    

}
