/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.io.ScreenReaderTableAdapter;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.geos.ScreenReaderSerializationAdapter;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.editor.share.controller.ExpressionReader;
import org.geogebra.editor.share.serializer.ScreenReaderSerializer;
import org.geogebra.editor.share.tree.Formula;

import com.himamis.retex.renderer.share.serialize.DefaultSerializationAdapter;
import com.himamis.retex.renderer.share.serialize.SerializationAdapter;
import com.himamis.retex.renderer.share.serialize.TableAdapter;

/**
 * Utility class for reading GeoElement descriptions
 * 
 * @author Judit
 */
public final class ScreenReader {

	final private static String TRANSLATION_PREFIX = "ScreenReader.";

	private ScreenReader() {
		// utility class
	}

	/**
	 * @param app - application
	 * @return first selected geo
	 */
	public static GeoElement getSelectedGeo(App app) {
		if (!app.getSelectionManager().getSelectedGeos().isEmpty()) {
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
		GuiManagerInterface guiManager = app.getGuiManager();
		int viewID = app.getActiveEuclidianView().getViewID();
		if (guiManager != null && guiManager.getLayout() != null
			&& guiManager.getLayout().getDockManager().getFocusedViewId() != viewID) {
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
	 * @param geoList
	 *            the current geo
	 */
	public static void readDropDownItemSelected(GeoList geoList) {
		App app = geoList.getKernel().getApplication();
		ScreenReaderBuilder sb = new ScreenReaderBuilder(geoList.getKernel().getLocalization());
		geoList.appendAuralItemSelected(sb);
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

	private static String getStartFraction(Localization loc) {
		return localize(loc, "startFraction", "start fraction");
	}

	private static String localize(Localization loc, String key, String fallback) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + key, fallback) + " ";
	}

	private static String getMiddleFraction(Localization loc) {
		return " " + localize(loc, "fractionOver", "over");
	}

	private static String getEndFraction(Localization loc) {
		return " " + localize(loc, "endFraction", "end fraction");
	}

	/**
	 * @param loc localization
	 * @return localized times
	 */
	public static String getTimes(Localization loc) {
		return " " + localize(loc, "times", "times");
	}

	/**
	 * @param loc localization
	 * @return localized plus
	 */
	public static String getPlus(Localization loc) {
		return " " + localize(loc, "plus", "plus");
	}

	/**
	 * @param loc localization
	 * @return localized minus
	 */
	public static String getMinus(Localization loc) {
		return " " + localize(loc, "minus", "minus");
	}

	/**
	 * @param loc localization
	 * @return localized start of abs
	 */
	public static String getStartAbs(Localization loc) {
		return localize(loc, "StartAbsoluteValue", "start absolute value") + " ";
	}

	/**
	 * @param loc localization
	 * @return localized end of abs
	 */
	public static String getEndAbs(Localization loc) {
		return " " + localize(loc, "EndAbsoluteValue", "end absolute value");
	}

	/**
	 * @param loc localization
	 * @return localized start of sqrt
	 */
	public static String getStartSqrt(Localization loc) {
		return localize(loc, "startSqrt", "start square root");
	}

	/**
	 * @param loc localization
	 * @return localized end of sqrt
	 */
	public static String getEndSqrt(Localization loc) {
		return " " + localize(loc, "endSqrt", "end square root");
	}

	private static String getSquared(Localization loc) {
		return localize(loc, "squared", "squared");
	}

	private static String getCubed(Localization loc) {
		return localize(loc, "cubed", "cubed");
	}

	/**
	 * @param loc localization
	 * @return localized word "degree"
	 */
	public static String getDegree(Localization loc) {
		return localize(loc, "degree", "degree");
	}

	/**
	 * @param loc localization
	 * @return localized word "degrees"
	 */
	public static String getDegrees(Localization loc) {
		return localize(loc, "degrees", "degrees");
	}

	private static String getStartPower(Localization loc) {
		return localize(loc, "startPower", "to the power of");
	}

	private static String getEndPower(Localization loc) {
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
				: new UtfScreenReaderSerializationAdapter(app.getLocalization());
	}

	private static final class UtfScreenReaderSerializationAdapter
			extends DefaultSerializationAdapter {

		private final TableAdapter tableAdapter;

		private UtfScreenReaderSerializationAdapter(Localization loc) {
			 tableAdapter = new ScreenReaderTableAdapter(loc);
		}

		@Override
		public String transformBrackets(String left, String base, String right) {
			return left + " " + base + right;
		}

		@Override
		public String transformWrapper(String baseString) {
			return ",".equals(baseString) ? ", " : baseString;
		}

		@Override
		public TableAdapter getTableAdapter() {
			return tableAdapter;
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
	public static String getAriaExpression(App app, Formula exp, String ariaPreview) {
		try {
			String expr = ScreenReaderSerializer.fullDescription(exp.getRootNode(),
					getSerializationAdapter(app));
			if (ariaPreview != null) {
				return expr + " = " + ariaPreview;
			}
			return expr;
		} catch (Exception e) {
			return ""; // fallback to MathField serialization handled elsewhere
		}
	}

	/**
	 * @param loc localization
	 * @return localized "open parenthesis"
	 */
	public static String getOpenParenthesis(Localization loc) {
		return " " + localize(loc, "OpenParenthesis", "open parenthesis");
	}

	/**
	 * @param loc localization
	 * @return localized "close parenthesis"
	 */
	public static String getCloseParenthesis(Localization loc) {
		return " " + localize(loc, "CloseParenthesis", "close parenthesis");
	}

	/**
	 * @param loc localization
	 * @return localized "open brace"
	 */
	public static String getOpenBrace(Localization loc) {
		return " " + localize(loc, "OpenBrace", "open brace");
	}

	/**
	 * @param loc localization
	 * @return localized "close brace"
	 */
	public static String getCloseBrace(Localization loc) {
		return " " + localize(loc, "CloseBrace", "close brace");
	}

	/**
	 * @param loc localization
	 * @return localized "open bracket"
	 */
	public static String getOpenBracket(Localization loc) {
		return " " + localize(loc, "OpenBracket", "open bracket");
	}

	/**
	 * @param loc localization
	 * @return localized "close bracket"
	 */
	public static String getCloseBracket(Localization loc) {
		return " " + localize(loc, "CloseBracket", "close bracket");
	}

	/**
	 * @param loc localization
	 * @return localized "semicolon"
	 */
	public static String getSemicolon(Localization loc) {
		return " " + localize(loc, "Semicolon", "semicolon");
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
	 * @param radicand
	 *            radicand
	 * @param index
	 *            index
	 * @param loc
	 *            localization
	 * @return root
	 */
	public static String nroot(String radicand, String index, Localization loc) {
		return loc.getPlainDefault("ScreenReader.startRoot",
				"start %0 root", asRootIndex(index, loc))
				+ ' '
				+ radicand
				+ ' '
				+ loc.getPlainDefault("ScreenReader.endRoot", "end root");
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
			builder.append(loc.getMenuDefault("PressTabToSelectNext",
						"Press tab to select next object"));
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

	/**
	 * @return localized word for comma
	 */
	public static String getComma(Localization loc) {
		return " " + localize(loc, "Comma", "comma");
	}
}
