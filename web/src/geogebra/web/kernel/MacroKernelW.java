package geogebra.web.kernel;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MacroKernel;

public class MacroKernelW extends MacroKernel implements KernelWInterface {

	public MacroKernelW(Kernel parentKernel) {
		super(parentKernel);
		MAX_SPREADSHEET_COLUMNS_VISIBLE = 26;//1..26
		MAX_SPREADSHEET_ROWS_VISIBLE = 200;//1..200
	}

	public MacroKernel newMacroKernel() {
		return new MacroKernelW(this);
	}
}
