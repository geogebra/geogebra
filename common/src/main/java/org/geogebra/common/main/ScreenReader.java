package org.geogebra.common.main;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.geos.ScreenReaderSerializationAdapter;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.controller.ExpressionReader;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.ScreenReaderSerializer;
import com.himamis.retex.renderer.share.serialize.DefaultSerializationAdapter;
import com.himamis.retex.renderer.share.serialize.SerializationAdapter;

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
	 * @param app - application
	 * @return first selected geo
	 */
	public static GeoElement getSelectedGeo(App app) {
		if (app.getSelectionManager().getSelectedGeos().size() > 0) {
			return app.getSelectionManager().getSelectedGeos().get(0);
		}

		return null;
	}

	/**
	 * @param app
	 *            application
	 */
	public static void updateSelection(App app) {
		GeoElement selectedGeo = getSelectedGeo(app);

		// do not steal focus from input box, do not read while drawing
		if (selectedGeo != null && !selectedGeo.isGeoInputBox()
				&& app.getMode() != EuclidianConstants.MODE_PEN) {
			readText(selectedGeo);
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
		if (text == null) {
			return;
		}

		// MOW-137: if selection originated in AV we don't want to move focus to EV
		if (app.getGuiManager() != null && app.getGuiManager()
				.getLayout().getDockManager().getFocusedViewId() != app
						.getActiveEuclidianView().getViewID()) {
			return;
		}

		// WLY-298: do not steal focus from input box
		GeoElement selectedGeo = getSelectedGeo(app);
		if (selectedGeo != null && selectedGeo.isGeoInputBox()) {
			return;
		}

		app.getActiveEuclidianView().getScreenReader().readText(text.trim());
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
		readText(text.getItemDisplayString(index, app.getScreenReaderTemplate()) + " "
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

	public static String getDegree(Localization loc) {
		return localize(loc, "degree", "degree");
	}

	public static String getDegrees(Localization loc) {
		return localize(loc, "degrees", "degrees");
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
			public String power(String base, String exponent) {
				return ScreenReader.power(base, exponent, loc);
			}

			@Override
			public void debug(String label) {
				ScreenReader.debug(label);
			}

			@Override
			public SerializationAdapter getAdapter() {
				return getSerializationAdapter(app);
			}
		};
	}

	/**
	 * @param app application
	 * @return serialization adapter
	 */
	public static SerializationAdapter getSerializationAdapter(App app) {
		return app.getScreenReaderTemplate().getStringType()
				== ExpressionNodeConstants.StringType.SCREEN_READER_ASCII
				? new ScreenReaderSerializationAdapter(app.getLocalization())
				: new UtfScreenReaderSerializationAdapter();
	}

	private static class UtfScreenReaderSerializationAdapter extends DefaultSerializationAdapter {

		@Override
		public String transformBrackets(String left, String base, String right) {
			return left + " " + base + right;
		}

		@Override
		public String transformWrapper(String baseString) {
			return ",".equals(baseString) ? ", " : baseString;
		}
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
	public static String getAriaExpression(App app, MathFormula exp, String ariaPreview) {
		try {
			String expr = ScreenReaderSerializer.fullDescription(exp.getRootComponent(),
					getSerializationAdapter(app));
			if (ariaPreview != null) {
				return expr + " = " + ariaPreview;
			}
			return expr;
		} catch (Exception e) {
			return ""; // fallback to MathField serialization handled elsewhere
		}
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

	public static String getPolarSeparator() {
		return " semicolon ";
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
		if ("\u2218".equals(rightStr)) {
			sb.append(rightStr);
		} else {
			appendPower(sb, rightStr, loc);
		}
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
	 * Appends degree(s) to the StringBuilder
	 * @param sb builder
	 * @param value degree value
	 * @param loc localization
	 */
	public static void appendDegrees(StringBuilder sb, String value, Localization loc) {
		if ("1".equals(value) || "-1".equals(value)) {
			sb.append(getDegree(loc));
		} else {
			sb.append(getDegrees(loc));
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
		if ("2".equals(rightStr) || rightStr.isEmpty()) {
			return "square";
		}
		if ("3".equals(rightStr)) {
			return "cube";
		}
		String index = rightStr;
		try {
			double indexVal = MyDouble.parseDouble(loc, index);
			if (DoubleUtil.isInteger(indexVal)) {
				index = loc.getLanguage().getOrdinalNumber((int) indexVal);
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
		if (app.getKernel().getAnimationManager().isRunning()) {
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
		if (app.getKernel().getAnimationManager().isRunning()) {
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
	 * @return converted String; unchanged for desktop, eg "M subscript R minus a" for mobile
	 */
	public static String convertToReadable(String s, App app) {
		if (s == null) {
			return "";
		}
		return getSerializationAdapter(app).convertToReadable(s);
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
