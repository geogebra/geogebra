package geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianViewCompanion;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3DCompanion;
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
	protected EuclidianViewCompanion newEuclidianViewCompanion(){
		return new EuclidianViewFor3DCompanion(this);
	}
	
	
	
}
