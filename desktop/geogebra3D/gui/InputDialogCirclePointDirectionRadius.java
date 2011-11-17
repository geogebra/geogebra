package geogebra3D.gui;

import geogebra.gui.InputDialogRadius;
import geogebra.gui.InputHandler;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

/**
 * 
 *
 */
public class InputDialogCirclePointDirectionRadius extends InputDialogRadius{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoPointND geoPoint;
	
	private GeoDirectionND forAxis;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point
	 * @param forAxis 
	 * @param kernel
	 */
	public InputDialogCirclePointDirectionRadius(Application app, String title, InputHandler handler, GeoPointND point, GeoDirectionND forAxis, Kernel kernel) {
		super(app, title, handler, kernel);
		
		geoPoint = point;
		this.forAxis = forAxis;

	}

	@Override
	protected GeoElement createOutput(NumberValue num){

		return kernel.getManager3D().Circle3D(
				null,
				geoPoint,
				num,
				forAxis);
	}

}
