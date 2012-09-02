package geogebra.common.kernel.scripting;

import geogebra.common.awt.GColor;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.main.MyError;
import geogebra.common.util.StringUtil;

/**
 *SetColor
 */
public class CmdSetColor extends CmdScripting {
	/** true for CmdSetBackgroundColor */
	protected boolean background = false;
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetColor(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		if (n == 2) {
			// adapted from resArgs()
			
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
		} else {
			arg = resArgs(c);
		}
		
		switch (n) {
		case 2:

			if (!arg[1].isGeoText())
				throw argErr(app, c.getName(), arg[1]);

			try {

				String color = StringUtil.removeSpaces(
						((GeoText) arg[1]).getTextString());
				// lookup Color
				//HashMap<String, Color> colors = app.getColorsHashMap();
				//Color col = colors.get(color);
				
				GColor col = GeoGebraColorConstants.getGeogebraColor(app,  color);

				// support for translated color names
				//if (col == null) {
				//	// translate to English
				//	color = app.reverseGetColor(color).toUpperCase();
				//	col = (Color) colors.get(color);
				//	// Application.debug(color);
				//}

				if (col == null) 
					throw argErr(app, c.getName(), arg[1]);
				
				
				if (background)
					arg[0].setBackgroundColor(col);
				else
					arg[0].setObjColor(col);
				
				arg[0].updateRepaint();				
				
				return;

			} catch (Exception e) {
				e.printStackTrace();
				throw argErr(app, c.getName(), arg[0]);
			}

		case 4:
			boolean[] ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())) {
				int red = (int) (((NumberValue) arg[1]).getDouble() * 255);
				if (red < 0)
					red = 0;
				else if (red > 255)
					red = 255;
				int green = (int) (((NumberValue) arg[2]).getDouble() * 255);
				if (green < 0)
					green = 0;
				else if (green > 255)
					green = 255;
				int blue = (int) (((NumberValue) arg[3]).getDouble() * 255);
				if (blue < 0)
					blue = 0;
				else if (blue > 255)
					blue = 255;

				if (background)
					arg[0].setBackgroundColor(AwtFactory.prototype.newColor(red, green, blue));
				else
					arg[0].setObjColor(AwtFactory.prototype.newColor(red, green, blue));
				
				arg[0].updateRepaint();
				
				return;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
