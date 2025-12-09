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

package org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianController3DW;

/**
 * (dummy) controller for 3D view, for browsers that don't support webGL
 * 
 * @author mathieu
 *
 */
public class EuclidianController3DWnoWebGL extends EuclidianController3DW {

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public EuclidianController3DWnoWebGL(Kernel kernel) {
		super(kernel);
	}

	@Override
	public void calculateEnvironment() {
		if (getView() instanceof EuclidianView3DWnoWebGL) {
			((EuclidianView3DWnoWebGL) getView()).onResize();
			getView().repaint();
		}
	}

}
