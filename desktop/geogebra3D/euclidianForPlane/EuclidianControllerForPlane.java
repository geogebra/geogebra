package geogebra3D.euclidianForPlane;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.euclidianFor3D.EuclidianControllerFor3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.util.ArrayList;

/**
 * Controler for 2D view created from a plane
 * @author matthieu
 *
 */
public class EuclidianControllerForPlane extends EuclidianControllerFor3D {

	public EuclidianControllerForPlane(Kernel kernel) {
		super(kernel);
	}
	
	
	private Coords getCoordsFromView(double x, double y){
		return ((EuclidianViewForPlane) view).getCoordsFromView(new Coords(x,y,0,1));
	}
		
	@Override
	protected void movePoint(boolean repaint) {
		
		Coords coords = getCoordsFromView(xRW,yRW);
		
		//Application.debug("xRW, yRW= "+xRW+", "+yRW+"\n3D coords:\n"+coords);
		
		//cancel 3D controller stuff
		if (((GeoElement) movedGeoPoint).isGeoElement3D()){
			((GeoPoint3D) movedGeoPoint).setWillingCoords(null);	
			((GeoPoint3D) movedGeoPoint).setWillingDirection(null);
		}
		
		movedGeoPoint.setCoords(coords, true);
		((GeoElement) movedGeoPoint).updateCascade();
		
		movedGeoPointDragged = true;

		if (repaint)
			kernel.notifyRepaint();
	}	
	
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex){
	
		Coords coords = getCoordsFromView(xRW,yRW);
		
		GeoPointND ret = kernel.getManager3D().Point3DIn(null, ((EuclidianView) view).getPlaneContaining(), coords, !forPreviewable);
		return ret;
	}
	
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path, boolean complex){
		Coords coords = getCoordsFromView(xRW,yRW);
		return createNewPoint(forPreviewable, path, coords.getX(), coords.getY(), coords.getZ(), complex);
	}
	
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region, boolean complex){
		Coords coords = getCoordsFromView(xRW,yRW);
		return createNewPoint(forPreviewable, region, coords.getX(), coords.getY(), coords.getZ(), complex);
	}

	@Override
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1){
		return createCircle2ForPoints3D(p0, p1);
	}

	@Override
	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line){
		return new GeoElement[] {(GeoElement) getKernel().getManager3D().OrthogonalLine3D(null,point, line, ((EuclidianView) view).getDirection())};		

	}
	
	@Override
	protected void processModeLock(GeoPointND point){
		Coords coords = ((EuclidianView) view).getCoordsForView(point.getInhomCoordsInD(3));
		xRW = coords.getX();
		yRW = coords.getY();
	}
	
	@Override
	protected void processModeLock(Path path){
		GeoPointND p = createNewPoint(true, path, false);
		((GeoElement) p).update();
		Coords coords = ((EuclidianView) view).getCoordsForView(p.getInhomCoordsInD(3));
		xRW = coords.getX();
		yRW = coords.getY();
	}

	@Override
	protected ArrayList<GeoElement> removeParentsOfView(ArrayList<GeoElement> list){
		ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
		for (GeoElement geo : list)
			if (view.isMoveable(geo))
				ret.add(geo);
		return ret;
	}
	
}
