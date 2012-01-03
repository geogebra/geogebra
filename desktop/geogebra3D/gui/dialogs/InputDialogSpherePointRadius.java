package geogebra3D.gui.dialogs;


import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.gui.InputHandler;
import geogebra.gui.dialog.InputDialogRadius;
import geogebra.main.Application;

/**
 * 
 *
 */
public class InputDialogSpherePointRadius extends InputDialogRadius{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoPointND geoPoint;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point
	 * @param kernel
	 */
	public InputDialogSpherePointRadius(Application app, String title, InputHandler handler, GeoPointND point, AbstractKernel kernel) {
		super(app, title, handler, kernel);
		
		geoPoint = point;

	}

	@Override
	protected GeoElement createOutput(NumberValue num){
		return kernel.getManager3D().Sphere(null, geoPoint, num);
	}

}
