package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoText;
import geogebra.main.GeoGebraColorConstants;

import java.awt.Color;

/**
 *SetColor
 */
class CmdSetColor extends CmdScripting {

	boolean background = false;
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetColor(Kernel kernel) {
		super(kernel);
	}

	public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (!arg[1].isGeoText())
				throw argErr(app, c.getName(), arg[1]);

			try {

				String color = geogebra.util.Util.removeSpaces(
						((GeoText) arg[1]).getTextString());
				// lookup Color
				//HashMap<String, Color> colors = app.getColorsHashMap();
				//Color col = colors.get(color);
				
				Color col = GeoGebraColorConstants.getGeogebraColor(app,  color);

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
					arg[0].setBackgroundColor(new geogebra.awt.Color(col));
				else
					arg[0].setObjColor(new geogebra.awt.Color(col));
				
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
					arg[0].setBackgroundColor(new geogebra.awt.Color(red, green, blue));
				else
					arg[0].setObjColor(new geogebra.awt.Color(red, green, blue));
				
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
