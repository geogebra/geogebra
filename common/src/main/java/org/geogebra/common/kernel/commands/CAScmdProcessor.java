package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Handles CAS commands in input bar by showing appropriate message
 * 
 * @author zbynek
 *
 */
public class CAScmdProcessor extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CAScmdProcessor(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		throw new MyError(loc,
				loc.getPlain("CASViewOnly", loc.getCommand(c.getName())));
	}

}
