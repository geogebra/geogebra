package geogebra.web.kernel;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MacroKernel;
import geogebra.common.main.App;

/**
 * @author gabor
 * 
 * For GWT.runAsync calls
 *
 */
public class KernelW extends Kernel implements KernelWInterface {

	public KernelW() {
		super();
		MAX_SPREADSHEET_COLUMNS_VISIBLE = 26;//1..26
		MAX_SPREADSHEET_ROWS_VISIBLE = 200;//1..200
	}

	public KernelW(App app) {
	    super(app);
		MAX_SPREADSHEET_COLUMNS_VISIBLE = 26;
		MAX_SPREADSHEET_ROWS_VISIBLE = 200;
    }

	public MacroKernel newMacroKernel() {
		return new MacroKernelW(this);
	}
}
