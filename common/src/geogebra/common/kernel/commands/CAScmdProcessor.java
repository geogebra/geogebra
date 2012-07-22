package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
/**
 * Handles CAS commands in input bar by showing appropriate message
 * @author zbynek
 *
 */
public class CAScmdProcessor extends CommandProcessor {

	/**
	 * @param kernel kernel
	 */
	public CAScmdProcessor(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		throw new MyError(app,app.getPlain("CASViewOnly",c.getName()));
	}

}
