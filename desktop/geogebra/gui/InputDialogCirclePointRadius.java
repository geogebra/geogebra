package geogebra.gui;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 * 
 *
 */
public class InputDialogCirclePointRadius extends InputDialogRadius{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoPoint2 geoPoint1;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point1
	 * @param kernel
	 */
	public InputDialogCirclePointRadius(Application app, String title, InputHandler handler, GeoPoint2 point1, Kernel kernel) {
		super(app, title, handler, kernel);
		
		geoPoint1 = point1;
	}

	@Override
	protected GeoElement createOutput(NumberValue num){
		return kernel.Circle(null, geoPoint1, num);
	}

}
