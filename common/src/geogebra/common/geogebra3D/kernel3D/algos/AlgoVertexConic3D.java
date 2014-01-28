package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoVertexConic;
import geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Class for vertices of a 3D conic
 * @author mathieu
 *
 */
public class AlgoVertexConic3D extends AlgoVertexConic{

	/**
	 * constructor
	 * @param cons
	 * @param labels
	 * @param c
	 */
	public AlgoVertexConic3D(Construction cons, String[] labels, GeoConicND c) {
		super(cons, labels, c);
	}
	
    @Override
	protected void createVertex(Construction cons){
    	vertex = new GeoPoint3D[4];       
    	for (int i=0; i < vertex.length; i++) {
    		vertex[i] = new GeoPoint3D(cons);
    	}
    }

    @Override
	protected void setCoords(int i, double x, double y){
    	((GeoPoint3D) vertex[i]).setCoords(c.getCoordSys().getPoint(x, y));
    }
}
