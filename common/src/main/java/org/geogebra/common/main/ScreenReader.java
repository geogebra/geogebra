package org.geogebra.common.main;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.EventType;

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
			GeoElement geo0 = app.getSelectionManager().getSelectedGeos()
					.get(0);
			// do not steal focus from input box
			if (geo0.isGeoInputBox() && !app.has(Feature.HELP_AND_SHORTCUTS)) {
				return;
			}
			readText(geo0, app);
		}
	}

	public static void dropDownItemSelected(App app, GeoElement geo){
		String item = ((GeoList) geo).getSelectedElement().getAlgebraDescriptionDefault();
		String readText = app.getLocalization().getPlainArray("DropDownItemSelected",
				"Item %0 selected. Drop down closed. ",
				new String[] { item });
		app.getActiveEuclidianView().readText(readText);
	}

	/**
	 * @param geo0
	 *            selected element
	 * @param app
	 *            application
	 */
	public static void readText(GeoElement geo0, App app) {
		StringBuilder sb = new StringBuilder();
		String caption = geo0.getCaption(StringTemplate.defaultTemplate);
		if (caption == null || "".equals(caption)) {
			sb.append(geo0.translatedTypeStringForAlgebraView());
			sb.append(' ');
			sb.append(geo0.getAlgebraDescriptionDefault());
		} else {
			sb.append(caption);
		}

		if (app.has(Feature.HELP_AND_SHORTCUTS)) {
			if (geo0.isEuclidianShowable()) {
				if (app.getGuiManager() != null
						&& app.getGuiManager().hasAlgebraView()
						&& !geo0.isGeoInputBox()) {
					if (geo0.isEuclidianVisible()) {
						appendSentence(sb, "PressSlashToHide", "Press / to hide object", null, app);
					} else {
						appendSentence(sb, "PressSlashToShow", "Press / to show object", null, app);
					}
				}
			}
			if ((geo0.isGeoButton() && !geo0.isGeoInputBox())
					|| geo0.isPenStroke()) {
				appendSentence(sb, "PressEnterToOpenSettings", "Press enter to open settings", null, app);
			} else if (!geo0.isGeoInputBox() && app.showToolBar()) {
				appendSentence(sb, "PressEnterToEdit", "Press enter to edit", null, app);
			}
			if (geo0.isGeoBoolean()) {
				if (((GeoBoolean) geo0).getBoolean()) {
					appendSentence(sb, "PressSpaceCheckboxOff", "Press space to uncheck checkbox", null, app);
				} else {
					appendSentence(sb, "PressSpaceCheckboxOn", "Press space to check checkbox", null, app);
				}
			}
			if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSliderable()) {
				GeoNumeric geoNum = (GeoNumeric) geo0;
				if (geoNum.isAnimating()) {
					appendSentence(sb, "PressSpaceStopAnimation", "Press space to stop animation", null, app);
				} else {
					appendSentence(sb, "PressSpaceStartAnimation", "Press space to start animation", null, app);
				}
				if (geoNum.getIntervalMax() != geoNum.getValue()) {
					appendSentence(sb, "PressUpToIncrease", "Press up arrow to increase the value", null, app);
				}
				if (geoNum.getIntervalMin() != geoNum.getValue()) {
					appendSentence(sb, "PressDownToDecrease", "Press down arrow to decrease the value", null, app);
				}
			}
			if (geo0.getScript(EventType.CLICK) != null
					&& geo0.getScript(EventType.CLICK).getText().length() > 0) {
				appendSentence(sb, "PressSpaceToRunScript", "Press space to run script", null, app);
			}
			if (geo0.isGeoPoint() && (geo0.isIndependent()
					|| geo0.isPointOnPath() || geo0.isPointInRegion())) {
				appendSentence(sb, "PressArrowsToMove", "Press the arrow keys to move the object", null, app);
			}
			if (geo0.isGeoList() && app.has(Feature.READ_DROPDOWNS)) {
				appendSentence(sb, "DropDownSelected",
						"Drop down %0 menu selected",
						new String[] { geo0.getNameDescription() }, app);
				appendSentence(sb, "PressSpaceToOpen", "Press space to open", null, app);
			}
		}
		// MOW-137 if selection originated in AV we don't want to move
		// focus to EV
		if (app.getGuiManager() == null || app.getGuiManager().getLayout()
				.getDockManager().getFocusedViewId() == app
						.getActiveEuclidianView().getViewID()) {
			app.getActiveEuclidianView().readText(sb.toString());
		}
	}

	private static void appendSentence(StringBuilder sb, String string,
			String stringDefault, String[] args, App app) {
		sb.append(" ");
		if (app.has(Feature.READ_DROPDOWNS)) {
			if (args != null) {
				sb.append(app.getLocalization().getPlainArray(string, stringDefault, args));
			} else {
				sb.append(app.getLocalization().getMenuDefault(string, stringDefault));
			}
		} else {
			sb.append(app.getLocalization().getMenuDefault(string, stringDefault));
		}
		sb.append(".");
	}
}
