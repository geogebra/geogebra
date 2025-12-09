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

package org.geogebra.web.geogebra3D.web.main;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianController3DW;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL.EuclidianController3DWnoWebGL;
import org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL.EuclidianView3DWnoWebGL;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

/**
 * @author mathieu
 *
 */
public class App3DW {

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @return new controller for 3D view
	 */
	static final public EuclidianController3DW newEuclidianController3DW(
	        Kernel kernel) {
		if (Browser.supportsWebGL()) {
			return new EuclidianController3DW(kernel);
		}

		return new EuclidianController3DWnoWebGL(kernel);
	}

	/**
	 * 
	 * @param ec
	 *            controller for 3D view
	 * @param settings
	 *            euclidian settings
	 * @return new 3D view
	 */
	static final public EuclidianView3DW newEuclidianView3DW(
	        EuclidianController3DW ec, EuclidianSettings settings) {
		if (Browser.supportsWebGL()) {
			return new EuclidianView3DW(ec, settings);
		}

		return new EuclidianView3DWnoWebGL(ec, settings);
	}

	/**
	 * Sets the physical size of the 3D Canvas.
	 * 
	 * @param app
	 *            application instance
	 *
	 * @param width
	 *            new width
	 * 
	 * @param height
	 *            new height
	 */
	static final public void ggwGraphicsView3DDimChanged(AppW app, int width,
			int height) {
		GDimension dimension = AwtFactory.getPrototype().newDimension(width, height);
		EuclidianView3DW view = (EuclidianView3DW) app.getEuclidianView3D();
		if (!app.getSettings().getEuclidian(3).setPreferredSize(
				dimension)) {
			view.setPreferredSize(dimension);
		}
		view.setCoordinateSpaceSize(width, height);
		view.doRepaint2();
	}

}
