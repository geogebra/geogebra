package org.geogebra.common.kernel.scripting;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.MyError;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

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
		if (n == 0) {
			throw argNumErr(c);
		}

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
		target = resArg(c.getArgument(0), argInfo);
		cons.setSuppressLabelCreation(oldMacroMode);

		switch (n) {
		case 2:

			GColor col = fromText(c, 1);
			String label = c.getArgument(0)
					.toString(StringTemplate.defaultTemplate);

			if (kernel.lookupLabel(label) == null
					&& LabelManager.isValidLabel(label, kernel, null)
					&& GeoElementSpreadsheet.isSpreadsheetLabel(label)) {

				SpreadsheetCoords coords = GeoElementSpreadsheet.spreadsheetIndices(label);

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
					target.setObjColor(col.deriveWithAlpha(255));
					if (col.getAlpha() > 0 && col.getAlpha() < 255) {
						target.setAlphaValue(col.getAlpha() / 255.0);
					}
				}

				target.updateVisualStyleRepaint(GProperty.COLOR);
				return target.asArray();
			} catch (Exception e) {
				Log.debug(e);
				throw argErr(c, target);
			}

		case 4:

			label = c.getArgument(0)
					.toString(StringTemplate.defaultTemplate);
			col = fromRGB(c, 1);

			// SetBackgroundColor(A1,1,1,0)
			// for empty cell
			if (kernel.lookupLabel(label) == null
					&& LabelManager.isValidLabel(label, kernel, null)
					&& GeoElementSpreadsheet.isSpreadsheetLabel(label)) {

				SpreadsheetCoords coords = GeoElementSpreadsheet.spreadsheetIndices(label);

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

			target.updateVisualStyleRepaint(GProperty.COLOR);

			return target.asArray();

		default:
			throw argNumErr(c);
		}

	}

	private GColor fromRGB(Command c, int offset) {
		EvalInfo argInfo = new EvalInfo(false);
		boolean oldMacroMode = cons.isSuppressLabelsActive();

		try {
			cons.setSuppressLabelCreation(true);
			GeoElement r = resArg(c.getArgument(offset), argInfo);
			GeoElement g = resArg(c.getArgument(offset + 1), argInfo);
			GeoElement b = resArg(c.getArgument(offset + 2), argInfo);

			int red, blue, green;
			if (r.isNumberValue()) {
				red = MyDouble.normalize0to255(r.evaluateDouble());
			} else {
				throw argErr(c, r);
			}
			if (g.isNumberValue()) {
				green = MyDouble.normalize0to255(g.evaluateDouble());
			} else {
				throw argErr(c, g);
			}
			if (b.isNumberValue()) {
				blue = MyDouble.normalize0to255(b.evaluateDouble());
			} else {
				throw argErr(c, b);
			}
			return GColor.newColor(red, green, blue);
		} finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
	}

	private GColor fromText(Command c, int offset) {
		EvalInfo argInfo = new EvalInfo(false);
		ExpressionNode[] args = c.getArguments();
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		GeoElement color;
		GColor ret;

		try {
			// resolve second argument
			cons.setSuppressLabelCreation(true);
			args[offset].resolveVariables(argInfo);
			color = resArg(args[offset], argInfo);

		} catch (Error e) {
			// if there's a problem with the second argument, just wrap in
			// quotes in case it's a color
			// eg SetColor[A,blue] rather than SetColor[A,"blue"]
			color = new GeoText(cons,
					args[offset].toString(StringTemplate.defaultTemplate));
		} finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		if (!color.isGeoText()) {
			throw argErr(c, color);
		}

		String colorText = trim(color.toValueString(StringTemplate.defaultTemplate));
		if (colorText.startsWith("#")) {

			if (colorText.length() != 7 && colorText.length() != 9) {
				throw argErr(c, color);
			}

			int red;
			int green;
			int blue;
			int alpha = 255;
			String rgb;

			if (colorText.length() == 7) {
				// SetColor(text1,"#ffff00")
				rgb = colorText.substring(1);
			} else {
				// eg SetBackgroundColor(text1,"#80ffff00")
				// to set opacity of background color
				rgb = colorText.substring(3);
				alpha = Integer.parseInt(colorText.substring(1, 3), 16);
			}

			red = Integer.parseInt(rgb.substring(0, 2), 16);
			green = Integer.parseInt(rgb.substring(2, 4), 16);
			blue = Integer.parseInt(rgb.substring(4, 6), 16);
			ret = GColor.newColor(red, green, blue, alpha);
		} else {
			ret = GeoGebraColorConstants.getGeogebraColor(app, colorText);
		}

		if (ret == null && !background) {
			throw argErr(c, color);
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
