package org.geogebra.common.main;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.controller.ExpressionReader;

/**
 * Utility class for reading GeoElement descriptions
 * 
 * @author Judit
 */
@SuppressWarnings("javadoc")
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
		if (app.getGuiManager() == null || app.getGuiManager().getLayout()
				.getDockManager().getFocusedViewId() == app
						.getActiveEuclidianView().getViewID()) {

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
		return loc.getMenuDefault(TRANSLATION_PREFIX + "startFraction",
				"start fraction") + " ";
	}

	public static String getMiddleFraction(Localization loc) {
		return " " + loc.getMenuDefault(TRANSLATION_PREFIX + "fractionOver",
				"over")
				+ " ";
	}

	public static String getEndFraction(Localization loc) {
		return " " + loc.getMenuDefault(TRANSLATION_PREFIX + "endFraction",
				"end fraction") + " ";
	}

	public static String getTimes(Localization loc) {
		return " " + loc.getMenuDefault(TRANSLATION_PREFIX + "times", "times")
				+ " ";
	}

	public static String getPlus(Localization loc) {
		return " " + loc.getMenuDefault(TRANSLATION_PREFIX + "plus", "plus")
				+ " ";
	}

	public static String getMinus(Localization loc) {
		return " " + loc.getMenuDefault(TRANSLATION_PREFIX + "minus", "minus")
				+ " ";
	}

	public static String getStartCbrt(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "startCbrt",
				"start cube root")
				+ " ";
	}

	public static String getEndCbrt(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "endCbrt",
				"end cube root")
				+ " ";
	}

	public static String getStartSqrt(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "startSqrtCbrt",
				"start square root") + " ";
	}

	public static String getEndSqrt(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "endSqrt",
				"end square root")
				+ " ";
	}

	public static String getSquared(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "squared", "squared")
				+ " ";
	}

	public static Object getCubed(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "cubed", "cubed") + " ";
	}

	public static String getStartPower(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "startSuperscript",
				"start superscript") + " ";
	}

	public static String getEndPower(Localization loc) {
		return loc.getMenuDefault(TRANSLATION_PREFIX + "endSuperscript",
				"end superscript") + " ";
	}

	public static ExpressionReader getExpressionReader(final App app) {
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
					return app.getKernel().getParser().parseGeoGebraCAS(serialize, null)
							.toString(StringTemplate.screenReader);
				} catch (org.geogebra.common.kernel.parser.ParseException e) {
					Log.error(serialize);
					throw new RuntimeException(e);
				}
			}
		};
	}

}
