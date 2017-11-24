package org.geogebra.common.kernel.scripting;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellFormatInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
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

		GeoElement target;

		// SetBackgroundColor["red"]
		if (background && n == 1) {
			GColor col = fromText(c, 0);
			setViewBackground(col);
			return null;
		}

		// SetBackgroundColor[0,1,0]
		if (background && n == 3) {
			GColor col = fromRGB(c, 0);
			setViewBackground(col);
			return null;
		}

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		c.getArgument(0).resolveVariables(argInfo);
		target = resArg(c.getArgument(0), argInfo)[0];
		cons.setSuppressLabelCreation(oldMacroMode);


		switch (n) {
		case 2:

			GColor col = fromText(c, 1);
			String label = c.getArgument(0)
					.toString(StringTemplate.defaultTemplate);

			if (kernel.lookupLabel(label) == null
					&& LabelManager.isValidLabel(label, kernel)
					&& GeoElementSpreadsheet.isSpreadsheetLabel(label)) {

				GPoint coords = GeoElementSpreadsheet.spreadsheetIndices(label);

				CellFormatInterface formatHandler = kernel.getApplication()
						.getSpreadsheetTableModel().getCellFormat(null);

				formatHandler.setFormat(coords, CellFormat.FORMAT_BGCOLOR,
						col);

				return null;
			}
			try {
				if (background) {
					target.setBackgroundColor(col);
				} else {
					target.setObjColor(col);
				}

				target.updateRepaint();
				return target.asArray();
			} catch (Exception e) {
				e.printStackTrace();
				throw argErr(app, c, target);
			}

		case 4:

			label = c.getArgument(0)
					.toString(StringTemplate.defaultTemplate);
			col = fromRGB(c, 1);

			// SetBackgroundColor(A1,1,1,0)
			// for empty cell
			if (kernel.lookupLabel(label) == null
					&& LabelManager.isValidLabel(label, kernel)
					&& GeoElementSpreadsheet.isSpreadsheetLabel(label)) {

				GPoint coords = GeoElementSpreadsheet.spreadsheetIndices(label);

				CellFormatInterface formatHandler = kernel.getApplication()
						.getSpreadsheetTableModel().getCellFormat(null);


				formatHandler.setFormat(coords, CellFormat.FORMAT_BGCOLOR,
						col);

				return null;
			}


			if (background) {
				target.setBackgroundColor(col);
			} else {
				target.setObjColor(col);
			}

			target.updateRepaint();

			return target.asArray();



		default:
			throw argNumErr(c);
		}
	}

	private GColor fromRGB(Command c, int offset) {
		EvalInfo argInfo = new EvalInfo(false);
		GeoElement r = resArg(c.getArgument(offset), argInfo)[0];
		GeoElement g = resArg(c.getArgument(offset + 1), argInfo)[0];
		GeoElement b = resArg(c.getArgument(offset + 2), argInfo)[0];
		int red, blue, green;
		if (r instanceof NumberValue) {
			red = MyDouble.normalize0to255(((NumberValue) r).getDouble());
		} else {
			throw argErr(app, c, r);
		}
		if (g instanceof NumberValue) {
			green = MyDouble.normalize0to255(((NumberValue) g).getDouble());
		} else {
			throw argErr(app, c, g);
		}
		if (b instanceof NumberValue) {
			blue = MyDouble.normalize0to255(((NumberValue) b).getDouble());
		} else {
			throw argErr(app, c, b);
		}
		return GColor.newColor(red, green, blue);
	}

	private GColor fromText(Command c, int offset) {
		EvalInfo argInfo = new EvalInfo(false);
		ExpressionNode[] args = c.getArguments();
		GeoElement color;
		try {
			// resolve second argument
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			args[offset].resolveVariables(argInfo);
			color = resArg(args[offset], argInfo)[0];
			cons.setSuppressLabelCreation(oldMacroMode);
		} catch (Error e) {
			// if there's a problem with the second argument, just wrap in
			// quotes in case it's a color
			// eg SetColor[A,blue] rather than SetColor[A,"blue"]
			color = new GeoText(cons,
					args[offset].toString(StringTemplate.defaultTemplate));
		}
		if (!color.isGeoText()) {
			throw argErr(app, c, color);
		}
		GColor ret = GeoGebraColorConstants.getGeogebraColor(app,
				trim(color.toValueString(StringTemplate.defaultTemplate)));
		if (ret == null && !background) {
			throw argErr(app, c, color);
		}
		return ret;
	}
	/** remove quotes and spaces */
	private static String trim(String color) {
		return StringUtil.removeSpaces(color.replace("\"", ""));
	}

	private void setViewBackground(GColor col) {
		EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
		view.getSettings().setBackground(col);
		view.updateBackground();

	}
}
