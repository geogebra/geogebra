package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.LabelManager;
import geogebra.common.main.MyError;

/**
 *Rename
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
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			// adapted from resArgs()

			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			ExpressionNode[] args = c.getArguments();
			arg = new GeoElement[args.length];

			// resolve first argument
			args[0].resolveVariables(args[0].getLeft() instanceof Equation);
			arg[0] = resArg(args[0])[0];

			try {
				// resolve second argument
				args[1].resolveVariables(args[1].getLeft() instanceof Equation);
				arg[1] = resArg(args[1])[0];
			} catch (Error e) {
				// if there's a problem with the second argument, just wrap in quotes in case it's a color
				// eg SetColor[A,blue] rather than SetColor[A,"blue"]
				arg[1] = new GeoText(cons, args[1].toString(StringTemplate.defaultTemplate));
			}
			cons.setSuppressLabelCreation(oldMacroMode);


			if (arg[1].isGeoText()) {

				GeoElement geo = arg[0];

				if (LabelManager.checkName(geo, ((GeoText) arg[1]).getTextString())) {
					geo.rename(((GeoText) arg[1]).getTextString());
					geo.updateRepaint();


					return;
				}
				throw argErr(app, c.getName(), arg[1]);
			}
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
