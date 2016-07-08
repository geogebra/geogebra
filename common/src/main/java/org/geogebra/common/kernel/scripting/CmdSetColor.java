package org.geogebra.common.kernel.scripting;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.StringUtil;

/**
 * SetColor
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
	protected GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		EvalInfo argInfo = new EvalInfo(false);
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		GeoElement[] arg;

		// SetBackgroundColor["red"]
		if (background && n == 1) {

			ExpressionNode en = c.getArguments()[0];
			ExpressionValue ev = en.evaluate(StringTemplate.defaultTemplate);

			String color = ev.toString(StringTemplate.defaultTemplate);

			try {

				color = StringUtil.removeSpaces(color.replace("\"", ""));
				// lookup Color
				GColor col = GeoGebraColorConstants.getGeogebraColor(app,
						color);

				// SetBackgroundColor("none") is NOT OK
				if (col == null) {
					throw argErr(app, c.getName(), ev);
				}

				EuclidianViewInterfaceCommon view = app
						.getActiveEuclidianView();
				view.setBackground(col);
				view.updateBackground();

				return null;

			} catch (Exception e) {
				e.printStackTrace();
				throw argErr(app, c.getName(), ev);
			}

		}

		// SetBackgroundColor[0,1,0]
		if (background && n == 3) {

			ExpressionNode[] args = c.getArguments();
			ExpressionNode enR = args[0];
			ExpressionNode enG = args[1];
			ExpressionNode enB = args[2];
			double redD = enR.evaluateDouble();
			double greenD = enG.evaluateDouble();
			double blueD = enB.evaluateDouble();

			if (Double.isNaN(redD) || Double.isInfinite(redD)) {
				throw argErr(app, c.getName(),
						enR.evaluate(StringTemplate.defaultTemplate));
			}
			if (Double.isNaN(greenD) || Double.isInfinite(greenD)) {
				throw argErr(app, c.getName(),
						enG.evaluate(StringTemplate.defaultTemplate));
			}
			if (Double.isNaN(blueD) || Double.isInfinite(blueD)) {
				throw argErr(app, c.getName(),
						enB.evaluate(StringTemplate.defaultTemplate));
			}

			int red = MyDouble.normalize0to255(redD);
			int green = MyDouble.normalize0to255(greenD);
			int blue = MyDouble.normalize0to255(blueD);

			EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
			view.setBackground(AwtFactory.prototype.newColor(red, green, blue));
			view.updateBackground();

			return null;

		}

		if (n == 2) {
			// adapted from resArgs()

			ExpressionNode[] args = c.getArguments();
			arg = new GeoElement[args.length];

			// resolve first argument
			args[0].resolveVariables();
			arg[0] = resArg(args[0], argInfo)[0];

			try {
				// resolve second argument
				args[1].resolveVariables();
				arg[1] = resArg(args[1], argInfo)[0];
			} catch (Error e) {
				// if there's a problem with the second argument, just wrap in
				// quotes in case it's a color
				// eg SetColor[A,blue] rather than SetColor[A,"blue"]
				arg[1] = new GeoText(cons,
						args[1].toString(StringTemplate.defaultTemplate));
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

				String color = StringUtil.removeSpaces(((GeoText) arg[1])
						.getTextString());
				// lookup Color
				// HashMap<String, Color> colors = app.getColorsHashMap();
				// Color col = colors.get(color);

				GColor col = GeoGebraColorConstants
						.getGeogebraColor(app, color);

				// support for translated color names
				// if (col == null) {
				// // translate to English
				// color = app.reverseGetColor(color).toUpperCase();
				// col = (Color) colors.get(color);
				// // Application.debug(color);
				// }

				// SetBackgroundColor(text1, "none") is OK
				if (col == null && !background) {
					throw argErr(app, c.getName(), arg[1]);
				}

				if (background) {
					arg[0].setBackgroundColor(col);
				} else {
					arg[0].setObjColor(col);
				}

				arg[0].updateRepaint();

				return arg;

			} catch (Exception e) {
				e.printStackTrace();
				throw argErr(app, c.getName(), arg[0]);
			}

		case 4:
			boolean[] ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1] instanceof NumberValue)
					&& (ok[2] = arg[2] instanceof NumberValue)
					&& (ok[3] = arg[3] instanceof NumberValue)) {

				int red = MyDouble
						.normalize0to255(((NumberValue) arg[1]).getDouble());
				int green = MyDouble
						.normalize0to255(((NumberValue) arg[2]).getDouble());
				int blue = MyDouble
						.normalize0to255(((NumberValue) arg[3]).getDouble());

				if (background)
					arg[0].setBackgroundColor(AwtFactory.prototype.newColor(
							red, green, blue));
				else
					arg[0].setObjColor(AwtFactory.prototype.newColor(red,
							green, blue));

				arg[0].updateRepaint();

				return arg;

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
