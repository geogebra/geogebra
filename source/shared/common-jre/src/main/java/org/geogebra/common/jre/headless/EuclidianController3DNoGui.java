package org.geogebra.common.jre.headless;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;

public class EuclidianController3DNoGui extends EuclidianController3D {

	/**
	 * @param app
	 *            app
	 * @param kernel
	 *            kernel
	 */
	public EuclidianController3DNoGui(App app, Kernel kernel) {
		super(app);
		this.kernel = kernel;
	}

	@Override
	protected void resetToolTipManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setView(EuclidianView view) {
		super.setView3D(view);

	}

}
