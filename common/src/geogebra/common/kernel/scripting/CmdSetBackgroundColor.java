package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.main.MyError;
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
	protected
	final void perform(Command c) throws MyError {
		super.perform(c);
	}
}
