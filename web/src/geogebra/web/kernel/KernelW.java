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
	
	public static final int MAX_SPREADSHEET_COLUMNS_VISIBLE_WEB = 26;
	public static final int MAX_SPREADSHEET_ROWS_VISIBLE_WEB = 200;

	public KernelW() {
		super();
		MAX_SPREADSHEET_COLUMNS_VISIBLE = MAX_SPREADSHEET_COLUMNS_VISIBLE_WEB;//1..26
		MAX_SPREADSHEET_ROWS_VISIBLE = MAX_SPREADSHEET_ROWS_VISIBLE_WEB;//1..200
	}

	public KernelW(App app) {
	    super(app);
		MAX_SPREADSHEET_COLUMNS_VISIBLE = MAX_SPREADSHEET_COLUMNS_VISIBLE_WEB;
		MAX_SPREADSHEET_ROWS_VISIBLE = MAX_SPREADSHEET_ROWS_VISIBLE_WEB;
    }

	public MacroKernel newMacroKernel() {
		return new MacroKernelW(this);
	}
}
