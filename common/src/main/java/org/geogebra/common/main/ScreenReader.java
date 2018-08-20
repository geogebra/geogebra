package org.geogebra.common.main;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.parser.GParser;

import com.himamis.retex.editor.share.controller.ExpressionReader;

/**
 * Utility class for reading GeoElement descriptions
 * 
 * @author Judit
 */
public class ScreenReader {

	// just in English right now (translations can be added to ggbtrans later if
	// we need)
	final private static String TRANSLATION_PREFIX = "ScreenReader.";

	/**
	 * @param app
	 *            application
	 */
	public static void updateSelection(App app) {
		if (0 < app.getSelectionManager().getSelectedGeos().size()) {
			GeoElement geo0 = app.getSelectionManager().getSelectedGeos().get(0);
			// do not steal focus from input box
			if (geo0.isGeoInputBox()) {
				return;
			}
			readText(geo0);
		}
	}

	/**
	 * @param geo
	 *            selected element
	 */
	public static void readText(GeoElement geo) {
		readText(geo.getAuralText(), geo.getKernel().getApplication());
	}

	private static void readText(String text, App app) {
		// MOW-137 if selection originated in AV we don't want to move
		// focus to EV
		if (text != null && (app.getGuiManager() == null || app.getGuiManager()
				.getLayout().getDockManager().getFocusedViewId() == app
						.getActiveEuclidianView().getViewID())) {

			// dot on end to help screen readers
			app.getActiveEuclidianView()
					.readText(text.trim().endsWith(".") ? text : text + ".");
		}
	}

	// Handling DropDowns

	/**
	 * Reads the selected item of the current drop down.
	 * 
	 * @param geo
	 *            the current geo
	 */
	public static void readDropDownItemSelected(GeoElement geo) {
		if (!geo.isGeoList()) {
			return;
		}
		App app = geo.getKernel().getApplication();
		String text = ((GeoList) geo).getAuralItemSelected();
		readText(text, app);
	}

	/**
	 * Reads the item when the selector moved on it.
	 * 
	 * @param app
	 *            application
	 * @param text
	 *            selected item text to read
	 */
	public static void readDropDownSelectorMoved(App app, String text) {
		String readText = text;
		if ("".equals(text)) {
			readText = app.getLocalization().getMenuDefault("EmptyItem", "Empty item");
		}

		readText(readText, app);
	}

	/**
	 * Reads some instructions at opening the drop down.
	 * 
	 * @param geoList
	 *            drop down
	 */
	public static void readDropDownOpened(GeoList geoList) {
		readText(geoList.getAuralTextAsOpened(), geoList.getKernel().getApplication());
	}

	// End of DropDowns

	/**
	 * Reads text when space is pressed on geo.
	 * 
	 * @param geo
	 *            GeoElement to handle.
	 */
	public static void readSpacePressed(GeoElement geo) {
		App app = geo.getKernel().getApplication();
		readText(geo.getAuralTextForSpace(), app);
	}

	/**
	 * Reads text when geo is moved.
	 * 
	 * @param geo
	 *            GeoElement that has moved.
	 */
	public static void readGeoMoved(GeoElement geo) {
		App app = geo.getKernel().getApplication();
		readText(geo.getAuralTextForMove(), app);
	}

	/**
	 * Reads the current value of the slider specified by geo.
	 * 
	 * @param geo
	 *            the slider to read.
	 */
	public static void readSliderValue(GeoNumeric geo) {
		readText(geo.getAuralCurrentValue(), geo.getKernel().getApplication());
	}

	public static String getStartFraction(Localization loc) {
		return localize(loc, "startFraction", "start fraction");
	}

	private static String localize(Localization loc, String string, String string2) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + string, string2) + " ";
	}

	public static String getMiddleFraction(Localization loc) {
		return " " + localize(loc, "fractionOver", "over");
	}

	public static String getEndFraction(Localization loc) {
		return " " + localize(loc, "endFraction", "end fraction");
	}

	public static String getTimes(Localization loc) {
		return " " + localize(loc, "times", "times");
	}

	public static String getPlus(Localization loc) {
		return " " + localize(loc, "plus", "plus");
	}

	public static String getMinus(Localization loc) {
		return " " + localize(loc, "minus", "minus");
	}

	public static String getStartCbrt(Localization loc) {
		return localize(loc, "startCbrt", "start cube root");
	}

	public static String getEndCbrt(Localization loc) {
		return localize(loc, "endCbrt", "end cube root");
	}

	public static String getStartSqrt(Localization loc) {
		return localize(loc, "startSqrtCbrt", "start square root");
	}

	public static String getEndSqrt(Localization loc) {
		return localize(loc, "endSqrt", "end square root");
	}

	public static String getSquared(Localization loc) {
		return localize(loc, "squared", "squared");
	}

	public static Object getCubed(Localization loc) {
		return localize(loc, "cubed", "cubed");
	}

	public static String getStartPower(Localization loc) {
		return localize(loc, "startSuperscript", "start superscript");
	}

	public static String getEndPower(Localization loc) {
		return " " + localize(loc, "endSuperscript", "end superscript");
	}

	/**
	 * @param app
	 *            application
	 * @return expression to speech converter
	 */
	public static ExpressionReader getExpressionReader(final App app) {
		final Localization loc = app.getLocalization();
		final GParser parser = new GParser(app.getKernel(), app.getKernel().getConstruction());
		parser.setSilent(true);
		return new ExpressionReader() {

			@Override
			public String localize(String key, String... parameters) {
				String out = key;
				for (int i = 0; i < parameters.length; i++) {
					out = out.replace("%" + i, parameters[i]);
				}
				return out;
			}

			@Override
			public String mathExpression(String serialize) {
				try {
					return parser.parseGeoGebraCAS(serialize, null)
							.toString(StringTemplate.screenReader);
				} catch (org.geogebra.common.kernel.parser.ParseException | Error e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String power(String base, String exponent) {
				return ScreenReader.power(base, exponent, loc);
			}

			@Override
			public String fraction(String numerator, String denominator) {
				StringBuilder sb = new StringBuilder();
				ScreenReader.fraction(sb, numerator, denominator, loc);
				return sb.toString();
			}

			@Override
			public String squareRoot(String arg) {

				return ScreenReader.getStartSqrt(loc) + arg + ScreenReader.getEndSqrt(loc);
			}
		};
	}

	/**
	 * 
	 * @param app
	 *            {@link App}
	 * @param exp
	 *            The expression to read.
	 * @param ariaPreview
	 *            preview of the expression to read.
	 * @return the full aural representation of the expression with its preview if
	 *         any.
	 */
	public static String getAriaExpression(App app, String exp, String ariaPreview) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(ScreenReader.getExpressionReader(app).mathExpression(exp));
			if (ariaPreview != null) {
				sb.append(" = ");
				sb.append(ariaPreview);
			}
		} catch (Exception e) {
			// do nothing
		}
		return sb.toString();
	}

	public static String getLeftBracket() {
		return " open parenthesis ";
	}

	public static String getRightBracket() {
		return " close parenthesis ";
	}

	/**
	 * @param leftStr
	 *            base
	 * @param rightStr
	 *            exponent
	 * @param loc
	 *            localization
	 * @return "x squared", "x cubed" or
	 *         "x start superscript a plus 1 end superscript"
	 */
	public static String power(String leftStr, String rightStr, Localization loc) {
		StringBuilder sb = new StringBuilder();
		sb.append(leftStr);
		sb.append(" ");
		if ("2".equals(rightStr)) {
			sb.append(ScreenReader.getSquared(loc));
		} else if ("3".equals(rightStr)) {
			sb.append(ScreenReader.getCubed(loc));
		} else {
			sb.append(ScreenReader.getStartPower(loc));
			sb.append(rightStr);
			sb.append(ScreenReader.getEndPower(loc));
		}
		return sb.toString();
	}

	/**
	 * @param sb
	 *            string builder
	 * @param leftStr
	 *            numerator
	 * @param rightStr
	 *            denominator
	 * @param loc
	 *            localization
	 */
	public static void fraction(StringBuilder sb, String leftStr, String rightStr,
			Localization loc) {
		sb.append(ScreenReader.getStartFraction(loc));
		sb.append(leftStr);
		sb.append(ScreenReader.getMiddleFraction(loc));
		sb.append(rightStr);
		sb.append(ScreenReader.getEndFraction(loc));
	}
}
