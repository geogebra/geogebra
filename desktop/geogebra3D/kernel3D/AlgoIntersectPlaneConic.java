package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;



public class AlgoIntersectPlaneConic extends AlgoIntersectConic3D {
	


	public AlgoIntersectPlaneConic(Construction cons, String[] labels,
			GeoConicND c, GeoCoordSys2D plane) {	
		this(cons, labels, plane, c);
	}
	
	public AlgoIntersectPlaneConic(Construction cons, String[] labels,
			GeoCoordSys2D plane, GeoConicND c) {	
		this(cons, plane, c);
		GeoElement.setLabels(labels, P);    
	}
	
	public AlgoIntersectPlaneConic(Construction cons, GeoCoordSys2D plane, GeoConicND c) {		
		super(cons, (GeoElement) plane, c);
	}
	
	
	public AlgoIntersectPlaneConic(Construction cons) {		
		super(cons);
	}

	private Coords[] intersection;

	@Override
	public void compute() {
		
		intersect((GeoCoordSys2D) getFirtGeo(), c, P);
		
	}

	public final void intersect(GeoCoordSys2D plane, GeoConicND c, GeoPoint3D[] P){
		//calc intersection line of the plane and the plane including the conic
		intersection = CoordMatrixUtil.intersectPlanes(
				plane.getCoordSys().getMatrixOrthonormal(),
				c.getCoordSys().getMatrixOrthonormal());

		super.intersect(c, P);
	}
	

	@Override
	public Commands getClassName() {
        return Commands.Intersect;
    }

	@Override
	protected Coords getFirstGeoStartInhomCoords() {
		return intersection[0];
	}

	@Override
	protected Coords getFirstGeoDirectionInD3() {
		return intersection[1];
	}

	@Override
	protected boolean getFirstGeoRespectLimitedPath(Coords p) {
		return true;
	}

	@Override
	protected void checkIsOnFirstGeo(GeoPoint3D p) {
		//nothing to do here
	}
	
}

