package geogebra.geogebra3D.web.kernel3D;

import geogebra.common.geogebra3D.kernel3D.Kernel3D;
import geogebra.common.main.App;
import geogebra.web.kernel.KernelW;

/**
 * for 3D
 * @author mathieu
 *
 */
public class KernelW3D extends Kernel3D{


	/**
	 * constructor
	 * @param app application
	 */
	public KernelW3D(App app) {
	    super(app);
		MAX_SPREADSHEET_COLUMNS_VISIBLE = KernelW.MAX_SPREADSHEET_COLUMNS_VISIBLE_WEB;
		MAX_SPREADSHEET_ROWS_VISIBLE = KernelW.MAX_SPREADSHEET_ROWS_VISIBLE_WEB;
		//Window.alert("KernelW3D : I will be threeD :-)");
    }


}
