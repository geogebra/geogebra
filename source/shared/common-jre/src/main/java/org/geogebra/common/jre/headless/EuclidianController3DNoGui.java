/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
