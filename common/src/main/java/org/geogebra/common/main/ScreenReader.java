package org.geogebra.common.main;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.geos.ScreenReaderSerializationAdapter;
import org.geogebra.common.kernel.parser.GParser;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

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
			if (geo0.isGeoInputBox()
					|| app.getMode() == EuclidianConstants.MODE_PEN) {
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
		readText(getAuralText(geo, new ScreenReaderBuilder(geo.getKernel().getLocalization())),
				geo.getKernel().getApplication());
	}

	private static void readText(String text, App app) {
		// MOW-137 if selection originated in AV we don't want to move
		// focus to EV
		if (text != null && (app.getGuiManager() == null || app.getGuiManager()
				.getLayout().getDockManager().getFocusedViewId() == app
						.getActiveEuclidianView().getViewID())) {

			// dot on end to help screen readers
			app.getActiveEuclidianView().getScreenReader()
					.readText(text.trim());
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
		ScreenReaderBuilder sb = new ScreenReaderBuilder(geo.getKernel().getLocalization());
		((GeoList) geo).appendAuralItemSelected(sb);
		readText(sb.toString(), app);
	}

	/**
	 * Reads the item when the selector moved on it.
	 * 
	 * @param app
	 *            application
	 * @param text
	 *            selected item text to read
	 */
	public static void readDropDownSelectorMoved(App app, GeoList text, int index) {
		readText(text.getItemDisplayString(index, StringTemplate.screenReader) + " "
				+ text.getIndexDescription(index), app);
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

	private static String localize(Localization loc, String key, String fallback) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + key, fallback) + " ";
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
		return " " + localize(loc, "endCbrt", "end cube root");
	}

	public static String getStartAbs(Localization loc) {
		return localize(loc, "startAbs", "start absolute value");
	}

	public static String getEndAbs(Localization loc) {
		return " " + localize(loc, "endAbs", " end absolute value");
	}

	public static String getStartSqrt(Localization loc) {
		return localize(loc, "startSqrtCbrt", "start square root");
	}

	public static String getEndSqrt(Localization loc) {
		return " " + localize(loc, "endSqrt", "end square root");
	}

	public static String getSquared(Localization loc) {
		return localize(loc, "squared", "squared");
	}

	public static String getCubed(Localization loc) {
		return localize(loc, "cubed", "cubed");
	}

	public static String getStartPower(Localization loc) {
		return localize(loc, "startPower", "to the power of");
	}

	public static String getEndPower(Localization loc) {
		return " " + localize(loc, "endPower", "end power");
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
					ValidExpression expr = parser.parseGeoGebraCAS(serialize, null);
					expr.inspect(new Inspecting() {
						@Override
						public boolean check(ExpressionValue v) {
							if (v instanceof Command) {
								((Command) v).setAllowEvaluationForTypeCheck(false);
							}

							return false;
						}
					});
					return expr.toString(StringTemplate.screenReader);
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
				return ScreenReader.getStartSqrt(loc) + arg
						+ ScreenReader.getEndSqrt(loc);
			}

			@Override
			public String nroot(String radicand, String index) {
				return ScreenReader.nroot(radicand, index, loc);
			}

			@Override
			public String inParentheses(String content) {
				if (StringUtil.emptyTrim(content)) {
					return localize("empty %0", "parentheses");
				}
				return ScreenReader.getOpenParenthesis() + content
						+ ScreenReader.getCloseParenthesis();
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
			return ""; // fallback to MathField serialization handled elsewhere
		}
		return sb.toString();
	}

	public static String getOpenParenthesis() {
		return " open parenthesis ";
	}

	public static String getCloseParenthesis() {
		return " close parenthesis ";
	}

	public static String getOpenBrace() {
		return " open brace ";
	}

	public static String getCloseBrace() {
		return " close brace ";
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
		appendPower(sb, rightStr, loc);
		return sb.toString();
	}

	/**
	 * Appends exponent to the StringBuilder
	 * @param sb builder
	 * @param exponent exponent
	 * @param loc localization
	 */
	public static void appendPower(StringBuilder sb, String exponent, Localization loc) {
		if ("2".equals(exponent)) {
			sb.append(ScreenReader.getSquared(loc));
		} else if ("3".equals(exponent)) {
			sb.append(ScreenReader.getCubed(loc));
		} else {
			sb.append(ScreenReader.getStartPower(loc));
			sb.append(exponent);
			sb.append(ScreenReader.getEndPower(loc));
		}
	}

	/**
	 * @param leftStr
	 *            radicand
	 * @param rightStr
	 *            index
	 * @param loc
	 *            localization
	 * @return root
	 */
	public static String nroot(String leftStr, String rightStr, Localization loc) {
		StringBuilder sb = new StringBuilder();
		sb.append(loc.getPlainDefault("ScreenReader.startRoot",
				"start %0 root", asRootIndex(rightStr, loc)));
		sb.append(' ');
		sb.append(leftStr);
		sb.append(' ');
		sb.append(loc.getPlainDefault("ScreenReader.endRoot", "end root"));
		return sb.toString();
	}

	private static String asRootIndex(String rightStr, Localization loc) {
		if ("2".equals(rightStr)) {
			return "square";
		}
		if ("3".equals(rightStr)) {
			return "cube";
		}
		String index = rightStr;
		try {
			double indexVal = MyDouble.parseDouble(loc, index);
			if (DoubleUtil.isInteger(indexVal)) {
				index = loc.getOrdinalNumber((int) indexVal);
			}
		} catch (MyError e) {
			Log.trace("Not a number");
		}
		return index;
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

	/**
	 * Reads information about the play button on EV.
	 * 
	 * @param app
	 *            the application.
	 */
	public static void readEVPlay(App app) {
		Localization loc = app.getLocalization();
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		sb.append(loc.getMenu("PlayButton"));
		sb.endSentence();
		if (app.getKernel().getAnimatonManager().isRunning()) {
			sb.append(
					loc.getMenuDefault("PressSpaceStopAnimation", "Press space to stop animation"));
		} else {
			sb.append(loc.getMenuDefault("PressSpaceStartAnimation",
					"Press space to start animation"));
		}
		sb.endSentence();
		readText(sb.toString(), app);
	}

	/**
	 * Reads information about the animation state.
	 * 
	 * @param app
	 *            the application.
	 */
	public static void readAnimationState(App app) {
		String text;
		Localization loc = app.getLocalization();
		if (app.getKernel().getAnimatonManager().isRunning()) {
			text = loc.getMenuDefault("AnimationStarted", "animation is started");
		} else {
			text = loc.getMenuDefault("AnimationStopped", "animation is stopped");
		}
		ScreenReader.readText(text, app);
	}

	/**
	 * @param sel
	 *            selected object
	 * @param builder
	 *            screen reader output builder
	 * @return aural text + info about next/prev objects
	 */
	public static String getAuralText(GeoElement sel, ScreenReaderBuilder builder) {
		sel.getAuralText(builder);

		if (!builder.isMobile()) {
			builder.appendSpace();
			Localization loc = sel.getKernel().getLocalization();
			if (sel.getKernel().getApplication().getSelectionManager()
					.hasNext(sel)) {
				builder.append(loc.getMenuDefault("PressTabToSelectNext",
						"Press tab to select next object"));
			} else {
				// e.g. zoom panel
				builder.append(loc.getMenuDefault("PressTabToSelectControls",
						"Press tab to select controls"));
			}
		}
		return builder.toString();
	}

	/**
	 * @param s
	 *            String to convert eg M_R-a
	 * @return converted String eg M subscript R minus a
	 */
	public static String convertToReadable(String s, Localization loc) {
		StringBuilder sb = new StringBuilder();
		ScreenReaderSerializationAdapter adapter = new ScreenReaderSerializationAdapter(loc);
		for (int i = 0; i < s.length(); i++) {
			char character = s.charAt(i);
			if (character == '_') {
				sb.append(" subscript ");
			} else {
				sb.append(adapter.convertCharacter(character));
			}
		}
		return sb.toString();
	}

	/**
	 * @param text
	 *            debug string with a prefix (for console filter)
	 */
	public static void debug(String text) {
		Log.debug("read text: " + text);
	}

	public static String getComma() {
		return " comma ";
	}
}
