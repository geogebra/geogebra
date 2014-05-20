package geogebra.common.geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianControllerCreator;
import geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

/**
 * class for Euclidian Controller used in ggb3D
 * @author matthieu
 *
 */
public abstract class EuclidianControllerFor3D extends EuclidianController {

	/**
	 * constructor
	 * @param kernel kernel
	 */
	public EuclidianControllerFor3D(App app) {
		super(app);
	}
	
	
	
	@Override
	protected EuclidianControllerCreator newCreator(){
		return new EuclidianControllerCreatorFor3D(this);
	}
	

	
	
	
	protected GeoPointND getSingleIntersectionPointFrom2D(GeoElement a, GeoElement b, boolean coords2D) {
		return creator.getSingleIntersectionPoint(a, b, coords2D);
	}

	




	
	

	



	
	
	
	
	/**
	 * 
	 * @param p0 center
	 * @param p1 point on circle
	 * @return circle in the current plane
	 */
	protected GeoElement[] createCircle2For3D(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { kernel.getManager3D().Circle3D(null, p0, p1,
				view.getDirection()) };
	}
	
	
	/**
	 * 
	 * @param cons construction
	 * @param center center
	 * @param radius radius
	 * @return circle in the current plane
	 */
	protected GeoConicND circleFor3D(Construction cons, GeoPointND center, NumberValue radius){
		return kernel.getManager3D().Circle3D(null, center, radius, view.getDirection());
	}

	
	
	
	
	
	@Override
	protected final void moveLine(boolean repaint) {

		if (movedGeoLine.isGeoElement3D()){
			GeoLine3D line = (GeoLine3D) movedGeoLine;
			Coords v = line.getDirectionInD3();
			line.setCoord(new Coords(xRW, yRW, 0, 1), v);
			updateAfterMove(line, repaint);
		}else{
			super.moveLine(repaint);			
		}
	}
	
	
	@Override
	protected void moveVector(double x, double y){
		if (movedGeoVector.isGeoElement3D()){
			((GeoVector3D) movedGeoVector).setCoords(x, y, 0, 0);
		}else{
			super.moveVector(x, y);			
		}
	}
	
}
