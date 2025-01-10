package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * SetBackgroundColor[Object,Color]
 *
 */
public class CmdSetBackgroundColor extends CmdSetColor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetBackgroundColor(Kernel kernel) {
		super(kernel);
		background = true;
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		return super.perform(c);
	}
}
