package geogebra.common.geogebra3D.euclidianFor3D;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * static methods for 2D view for 3D stuff
 * @author Proprietaire
 *
 */
public class EuclidianViewFor3D {
	
	/**
	 * 
	 * @param view view calling
	 * @param geo geo creating the drawable
	 * @return specific drawable
	 */
	static final public DrawableND newDrawable(EuclidianView view, GeoElement geo) {

		DrawableND d = null;
		
		// try 3D geos
		switch (geo.getGeoClassType()) {
		case ANGLE3D:
			d = new DrawAngleFor3D(view, (GeoAngle) geo);
			break;
		}

		return d;
	}
	

	
	/**
	 * @param view view calling
	 * @param point point
	 * @return point coords in view coords
	 */
	static final public Coords getCoordsForView(EuclidianView view, GeoPointND point) {
		return view.getCoordsForView(point.getInhomCoordsInD(3));
	}
	
	
	/**
	 * @param conic conic
	 * @param M middle point
	 * @param ev eigen vectors
	 * @return transform for conic
	 */
	static final public GAffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){

		//use already computed for this view middlepoint M and eigen vecs ev
		GAffineTransform transform = geogebra.common.factories.AwtFactory.prototype.newAffineTransform();
		transform.setTransform(
				ev[0].getX(),
				ev[0].getY(),
				ev[1].getX(),
				ev[1].getY(),
				M.getX(),
				M.getY());

		return transform;
	}
	
	

}
