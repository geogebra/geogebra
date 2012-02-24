package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;
/**
 * SetBackgroundColor[Object,Color]
 *
 */
public class CmdSetBackgroundColor extends CmdSetColor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSetBackgroundColor(Kernel kernel) {
		super(kernel);
		background = true;
	}

	@Override
	final public void perform(Command c) throws MyError {
		super.perform(c);
	}
}
