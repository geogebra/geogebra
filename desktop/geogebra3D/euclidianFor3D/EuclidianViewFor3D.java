package geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.DrawableND;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.euclidian.EuclidianViewD;

import java.awt.geom.AffineTransform;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * 
 * @author matthieu
 * 
 */
public class EuclidianViewFor3D extends EuclidianViewD {

	/**
	 * @param ec controller
	 * @param showAxes show the axes
	 * @param showGrid shos the grid
	 * @param evno dock panel id
	 * @param settings euclidian settings
	 */
	public EuclidianViewFor3D(EuclidianControllerD ec, boolean[] showAxes,
			boolean showGrid, int evno, EuclidianSettings settings) {
		super(ec, showAxes, showGrid, evno, settings);

	}

	@Override
	public DrawableND newDrawable(GeoElement geo) {

		// first try super method
		DrawableND d = super.newDrawable(geo);
		if (d != null) {
			return d;
		}

		// try 3D geos
		switch (geo.getGeoClassType()) {
		case ANGLE3D:
			d = new DrawAngleFor3D(this, (GeoAngle) geo);
			break;
		}

		return d;
	}
	
	
	@Override
	public geogebra.common.awt.GAffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){

		//use already computed for this view middlepoint M and eigen vecs ev
		AffineTransform transform = new AffineTransform();			
		transform.setTransform(
				ev[0].getX(),
				ev[0].getY(),
				ev[1].getX(),
				ev[1].getY(),
				M.getX(),
				M.getY());

		return new geogebra.awt.GAffineTransformD(transform);
	}

}
