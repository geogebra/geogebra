package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

public class CmdSetBackgroundColor extends CmdSetColor {
	
	public CmdSetBackgroundColor(AbstractKernel kernel) {
		super(kernel);
		background = true;
	}

	final public void perform(Command c) throws MyError {
		super.perform(c);
	}
}
