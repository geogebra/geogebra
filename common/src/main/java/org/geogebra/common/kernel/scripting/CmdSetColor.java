package org.geogebra.common.kernel.scripting;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellFormatInterface;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
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
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.LabelManager;
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
		GeoElement[] arg = null;

		// SetBackgroundColor["red"]
		if (background && n == 1) {

			ExpressionNode en = c.getArgument(0);

			ExpressionValue ev = en.evaluate(StringTemplate.defaultTemplate);

			String color = ev.toString(StringTemplate.defaultTemplate);

			try {

				// lookup Color
				GColor col = GeoGebraColorConstants.getGeogebraColor(app,
						trim(color));

				// SetBackgroundColor("none") is NOT OK
				if (col == null) {
					throw argErr(app, c, ev);
				}

				setViewBackground(col);

				return null;

			} catch (Exception e) {
				e.printStackTrace();
				throw argErr(app, c, ev);
			}

		}

		// SetBackgroundColor[0,1,0]
		if (background && n == 3) {

			GeoElement[] args = resArgs(c);
			GeoElement enR = args[0];
			GeoElement enG = args[1];
			GeoElement enB = args[2];
			double redD = enR.evaluateDouble();
			double greenD = enG.evaluateDouble();
			double blueD = enB.evaluateDouble();

			if (Double.isNaN(redD) || Double.isInfinite(redD)) {
				throw argErr(app, c,
						enR.evaluate(StringTemplate.defaultTemplate));
			}
			if (Double.isNaN(greenD) || Double.isInfinite(greenD)) {
				throw argErr(app, c,
						enG.evaluate(StringTemplate.defaultTemplate));
			}
			if (Double.isNaN(blueD) || Double.isInfinite(blueD)) {
				throw argErr(app, c,
						enB.evaluate(StringTemplate.defaultTemplate));
			}

			int red = MyDouble.normalize0to255(redD);
			int green = MyDouble.normalize0to255(greenD);
			int blue = MyDouble.normalize0to255(blueD);

			GColor col = GColor.newColor(red, green, blue);
			setViewBackground(col);
			return null;

		}

		if (n == 2) {
			// not using resArgs as we need to cope with eg
			// SetBackgroundColor(A1,red)
			// when neither A1 nor red are defined geos.

			String label = c.getArgument(0)
					.toString(StringTemplate.defaultTemplate);
			String color = StringUtil.removeSpaces(
					c.getArgument(1).toString(StringTemplate.defaultTemplate));

			if (kernel.lookupLabel(label) == null
					&& LabelManager.isValidLabel(label, kernel)
					&& GeoElementSpreadsheet.isSpreadsheetLabel(label)) {

				GPoint coords = GeoElementSpreadsheet.spreadsheetIndices(label);

				SpreadsheetViewInterface spreadsheet = kernel.getApplication()
						.getGuiManager().getSpreadsheetView();
				CellFormatInterface formatHandler = spreadsheet
						.getSpreadsheetTable().getCellFormatHandler();


				GColor bgCol = GeoGebraColorConstants.getGeogebraColor(app,
						trim(color));

				formatHandler.setFormat(coords, CellFormat.FORMAT_BGCOLOR,
						bgCol);

				return null;
			}

			ExpressionNode[] args = c.getArguments();
			arg = new GeoElement[args.length];

			// resolve first argument
			args[0].resolveVariables(argInfo);
			arg[0] = resArg(args[0], argInfo)[0];

			try {
				// resolve second argument
				args[1].resolveVariables(argInfo);
				arg[1] = resArg(args[1], argInfo)[0];
			} catch (Error e) {
				// if there's a problem with the second argument, just wrap in
				// quotes in case it's a color
				// eg SetColor[A,blue] rather than SetColor[A,"blue"]
				arg[1] = new GeoText(cons,
						args[1].toString(StringTemplate.defaultTemplate));
			}
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		switch (n) {
		case 2:

			if (!arg[1].isGeoText()) {
				throw argErr(app, c, arg[1]);
			}

			try {

				String color = ((GeoText) arg[1]).getTextString();
				// lookup Color
				// HashMap<String, Color> colors = app.getColorsHashMap();
				// Color col = colors.get(color);

				GColor col = GeoGebraColorConstants.getGeogebraColor(app,
						trim(color));


				// SetBackgroundColor(text1, "none") is OK
				if (col == null && !background) {
					throw argErr(app, c, arg[1]);
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
				throw argErr(app, c, arg[0]);
			}

		case 4:

			String label = c.getArgument(0)
					.toString(StringTemplate.defaultTemplate);
			double r = c.getArgument(1).evaluateDouble();
			double g = c.getArgument(2).evaluateDouble();
			double b = c.getArgument(3).evaluateDouble();

			// SetBackgroundColor(A1,1,1,0)
			// for empty cell
			if (kernel.lookupLabel(label) == null
					&& LabelManager.isValidLabel(label, kernel)
					&& GeoElementSpreadsheet.isSpreadsheetLabel(label)) {

				GPoint coords = GeoElementSpreadsheet.spreadsheetIndices(label);

				SpreadsheetViewInterface spreadsheet = kernel.getApplication()
						.getGuiManager().getSpreadsheetView();
				CellFormatInterface formatHandler = spreadsheet
						.getSpreadsheetTable().getCellFormatHandler();

				GColor bgCol = GColor.newColor(r, g, b);

				formatHandler.setFormat(coords, CellFormat.FORMAT_BGCOLOR,
						bgCol);

				return null;
			}

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

				if (background) {
					arg[0].setBackgroundColor(
							GColor.newColor(red, green, blue));
				} else {
					arg[0].setObjColor(GColor.newColor(red, green, blue));
				}

				arg[0].updateRepaint();

				return arg;

			} else if (!ok[1]) {
				throw argErr(app, c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(app, c, arg[2]);
			} else {
				throw argErr(app, c, arg[3]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/** remove quotes and spaces */
	private String trim(String color) {
		return StringUtil.removeSpaces(color.replace("\"", ""));
	}

	private void setViewBackground(GColor col) {
		EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
		view.getSettings().setBackground(col);
		view.updateBackground();

	}
}
