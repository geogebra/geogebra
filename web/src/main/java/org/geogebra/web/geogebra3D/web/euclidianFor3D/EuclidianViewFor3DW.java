package org.geogebra.web.geogebra3D.web.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3DCompanion;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;

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
		super(euclidianViewPanel, euclidiancontroller, showAxes, showGrid,
		        evNo, settings);
	}

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		return new EuclidianViewFor3DCompanion(this);
	}

}
