package org.geogebra.desktop.gui.app;

import javax.swing.JFrame;

import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.main.AppD;

/**
 * Frame for geogebra 3D.
 * 
 * @author matthieu
 *
 */
public class GeoGebraFrame3D extends GeoGebraFrame {

	private static final long serialVersionUID = 1L;

	public static synchronized void main(CommandLineArguments args) {
		GeoGebraFrame.init(args, new GeoGebraFrame3D());
	}

	@Override
	protected AppD createApplication(CommandLineArguments args, JFrame frame) {
		return new App3D(args, frame, true);
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

	protected GeoGebraFrame copy() {
		return new GeoGebraFrame3D();
	}

}
