package org.geogebra.desktop.geogebra3D.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3DCompanion;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.euclidian.EuclidianViewD;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * 
 * @author matthieu
 * 
 */
public class EuclidianViewFor3DD extends EuclidianViewD {

	/**
	 * @param ec
	 *            controller
	 * @param showAxes
	 *            show the axes
	 * @param showGrid
	 *            shos the grid
	 * @param evno
	 *            dock panel id
	 * @param settings
	 *            euclidian settings
	 */
	public EuclidianViewFor3DD(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, int evno, EuclidianSettings settings) {
		super(ec, showAxes, showGrid, evno, settings);

	}

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		return new EuclidianViewFor3DCompanion(this);
	}

}
