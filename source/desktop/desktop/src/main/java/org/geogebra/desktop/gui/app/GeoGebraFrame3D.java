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

package org.geogebra.desktop.gui.app;

import javax.swing.JFrame;

import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.main.AppD;

/**
 * Frame for geogebra 3D.
 * 
 * @author Mathieu
 *
 */
public class GeoGebraFrame3D extends GeoGebraFrame {

	private static final long serialVersionUID = 1L;

	@Override
	protected AppD createApplication(CommandLineArguments args, JFrame frame) {
		return new App3D(args, frame);
	}

	/**
	 * Create a new 3D geogebra window
	 * 
	 * @param args
	 *            command line arguments
	 * @return new geogebra window
	 */
	public static synchronized GeoGebraFrame createNewWindow3D(
			CommandLineArguments args) {
		return createNewWindow(args, new GeoGebraFrame3D());
	}

	@Override
	protected GeoGebraFrame copy() {
		return new GeoGebraFrame3D();
	}

}
