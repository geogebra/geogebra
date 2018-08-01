package org.geogebra.common.main;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Utility class for reading GeoElement descriptions
 * 
 * @author Judit
 */
public class ScreenReader {

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
		return loc.getMenuDefault("ScreenReader.startFraction",
				"start fraction") + " ";
	}

	public static String getMiddleFraction(Localization loc) {
		return loc.getMenuDefault("ScreenReader.fractionOver", "over") + " ";
	}

	public static String getEndFraction(Localization loc) {
		return loc.getMenuDefault("ScreenReader.endFraction", "end fraction")
				+ " ";
	}

	public static String getTimes(Localization loc) {
		return " " + loc.getMenuDefault("ScreenReader.times", "times") + " ";
	}

	public static String getPlus(Localization loc) {
		return " " + loc.getMenuDefault("ScreenReader.plus", "plus") + " ";
	}

	public static String getMinus(Localization loc) {
		return " " + loc.getMenuDefault("ScreenReader.minus", "minus") + " ";
	}

	public static String getStartCbrt(Localization loc) {
		return loc.getMenuDefault("ScreenReader.startCbrt", "start cube root")
				+ " ";
	}

	public static String getEndCbrt(Localization loc) {
		return loc.getMenuDefault("ScreenReader.endCbrt", "end cube root")
				+ " ";
	}

	public static String getStartSqrt(Localization loc) {
		return loc.getMenuDefault("ScreenReader.startSqrtCbrt",
				"start square root") + " ";
	}

	public static String getEndSqrt(Localization loc) {
		return loc.getMenuDefault("ScreenReader.endSqrt", "end square root")
				+ " ";
	}

	public static String getSquared(Localization loc) {
		return loc.getMenuDefault("ScreenReader.squared", "squared") + " ";
	}

	public static Object getCubed(Localization loc) {
		return loc.getMenuDefault("ScreenReader.cubed", "cubed") + " ";
	}

	public static Object getStartPower(Localization loc) {
		return loc.getMenuDefault("ScreenReader.startSuperscript",
				"start superscript") + " ";
	}

	public static Object getEndPower(Localization loc) {
		return loc.getMenuDefault("ScreenReader.endSuperscript",
				"end superscript") + " ";
	}
}
