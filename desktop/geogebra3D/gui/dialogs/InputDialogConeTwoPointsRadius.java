package geogebra3D.gui.dialogs;


import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.gui.dialog.InputDialogRadius;
import geogebra.main.AppD;

/**
 * 
 *
 */
public class InputDialogConeTwoPointsRadius extends InputDialogRadius{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoPointND a, b;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point
	 * @param kernel
	 */
	public InputDialogConeTwoPointsRadius(AppD app, String title, InputHandler handler, GeoPointND a, GeoPointND b, Kernel kernel) {
		super(app, title, handler, kernel);
		
		this.a = a;
		this.b = b;

	}

	@Override
	protected GeoElement createOutput(NumberValue num){
		return kernel.getManager3D().ConeLimited(null, a, b, num)[0];
	}

}
