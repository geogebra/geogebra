package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.MyError;

/**
 * Rename
 */
public class CmdRename extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRename(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		EvalInfo argInfo = new EvalInfo(false);
		switch (n) {
		case 2:
			// adapted from resArgs()
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			ExpressionNode[] args = c.getArguments();
			GeoElement[] arg = new GeoElement[args.length];

			// resolve first argument
			args[0].resolveVariables(argInfo);
			arg[0] = resArg(args[0], argInfo);

			try {
				// resolve second argument
				args[1].resolveVariables(argInfo);
				arg[1] = resArg(args[1], argInfo);
			} catch (Error e) {
				// if there's a problem with the second argument, just wrap in
				// quotes in case it's a color
				// eg SetColor[A,blue] rather than SetColor[A,"blue"]
				String val = args[1].toString(StringTemplate.defaultTemplate);
				if (args[1].unwrap() instanceof Command) {
					val = ((Command) args[1].unwrap()).getName();
				}
				arg[1] = new GeoText(cons, val);
			}
			cons.setSuppressLabelCreation(oldMacroMode);

			if (arg[1].isGeoText()) {

				GeoElement geo = arg[0];
				String newLabel = ((GeoText) arg[1]).getTextString();
				try {
					// get rid of trailing spaces, also "a b"->"a"
					newLabel = kernel.getAlgebraProcessor().parseLabel(newLabel);
				} catch (Throwable t) {
					// isValidLabel should fail
				}
				if (LabelManager.isValidLabel(newLabel, kernel, geo)) {
					geo.rename(newLabel);
					geo.updateRepaint();

					return arg;
				}
				throw argErr(c, arg[1]);
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
