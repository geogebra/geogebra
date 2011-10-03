package geogebra.gui.app;

import geogebra.CommandLineArguments;
import geogebra.main.Application;
import geogebra3D.Application3D;

import javax.swing.JFrame;

/**
 * Frame for geogebra 3D.
 * 
 * @author matthieu
 *
 */
public class GeoGebraFrame3D extends GeoGebraFrame {
	
	
	public static synchronized void main(CommandLineArguments args) {		
		GeoGebraFrame.init(args,new GeoGebraFrame3D());
	}
	
	protected Application createApplication(CommandLineArguments args, JFrame frame){	
		return new Application3D(args, frame, true);
	}

}
