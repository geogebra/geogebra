package geogebra3D.euclidianFor3D;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3D;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.euclidian.EuclidianViewD;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * 
 * @author matthieu
 * 
 */
public class EuclidianViewFor3DD extends EuclidianViewD {

	/**
	 * @param ec controller
	 * @param showAxes show the axes
	 * @param showGrid shos the grid
	 * @param evno dock panel id
	 * @param settings euclidian settings
	 */
	public EuclidianViewFor3DD(EuclidianController ec, boolean[] showAxes,
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
		return EuclidianViewFor3D.newDrawable(this, geo);
	}
	
	
	@Override
	public GAffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){
		return EuclidianViewFor3D.getTransform(conic, M, ev);
	}
	
	
	@Override
	public Coords getCoordsForView(GeoPointND point) {
		return EuclidianViewFor3D.getCoordsForView(this, point);
	}
	
	
}
