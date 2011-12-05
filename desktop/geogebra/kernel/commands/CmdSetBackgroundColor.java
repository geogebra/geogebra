package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

class CmdSetBackgroundColor extends CmdSetColor {
	
	public CmdSetBackgroundColor(Kernel kernel) {
		super(kernel);
		background = true;
	}

	final public void perform(Command c) throws MyError {
		super.perform(c);
	}
}
