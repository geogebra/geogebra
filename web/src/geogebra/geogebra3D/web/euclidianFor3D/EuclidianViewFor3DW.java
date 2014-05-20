package geogebra.geogebra3D.web.euclidianFor3D;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3D;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * 
 * @author mathieu
 * 
 */
public class EuclidianViewFor3DW extends EuclidianViewW {

	/**
	 * @param euclidianViewPanel
	 * @param euclidiancontroller
	 * @param showAxes
	 * @param showGrid
	 * @param evNo
	 * @param settings
	 */
	public EuclidianViewFor3DW(EuclidianPanelWAbstract euclidianViewPanel,
            EuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, int evNo, EuclidianSettings settings) {
		super(euclidianViewPanel, euclidiancontroller, showAxes, showGrid, evNo, settings);
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
