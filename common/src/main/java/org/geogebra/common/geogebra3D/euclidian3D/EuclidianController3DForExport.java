package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.main.App;

/**
 * 3D controller not-displayed 3D view (this controller just avoids NPE)
 */
public class EuclidianController3DForExport extends EuclidianController3D {

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 */
	public EuclidianController3DForExport(App app) {
		super(app);
		setKernel(app.getKernel());
	}

	@Override
	protected void initToolTipManager() {
		// no need
	}

	@Override
	protected void resetToolTipManager() {
		// no need
	}

}
