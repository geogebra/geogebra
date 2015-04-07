package org.geogebra.web.html5.kernel;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MacroKernel;

public class MacroKernelW extends MacroKernel implements KernelWInterface {

	public MacroKernelW(Kernel parentKernel) {
		super(parentKernel);
		MAX_SPREADSHEET_COLUMNS_VISIBLE = KernelW.MAX_SPREADSHEET_COLUMNS_VISIBLE_WEB;
		MAX_SPREADSHEET_ROWS_VISIBLE = KernelW.MAX_SPREADSHEET_ROWS_VISIBLE_WEB;

	}

	public MacroKernel newMacroKernel() {
		return new MacroKernelW(this);
	}
}
