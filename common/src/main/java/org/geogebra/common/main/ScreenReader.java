package org.geogebra.common.main;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
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

	/**
	 * @param geo0
	 *            selected element
	 * @param app
	 *            application
	 */
	public static void readText(GeoElement geo0, App app) {
		StringBuilder sb = new StringBuilder();
		String caption = geo0.getCaptionSimple();
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
						appendSentence(sb, "PressSlashToHide", app);
					} else {
						appendSentence(sb, "PressSlashToShow", app);
					}
				}
			}
			if ((geo0.isGeoButton() && !geo0.isGeoInputBox())
					|| geo0.isPenStroke()) {
				appendSentence(sb, "PressEnterToOpenSettings", app);
			} else if (!geo0.isGeoInputBox()) {
				appendSentence(sb, "PressEnterToEdit", app);
			}
			if (geo0.isGeoBoolean()){
				if(((GeoBoolean)geo0).getBoolean()){
					appendSentence(sb, "PressSpaceCheckboxOff", app);
				} else {
					appendSentence(sb, "PressSpaceCheckboxOn", app);
				}
			}
			if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSliderable()) {
				GeoNumeric geoNum = (GeoNumeric) geo0;
				if (geoNum.isAnimating()) {
					appendSentence(sb, "PressSpaceStopAnimation", app);
				} else {
					appendSentence(sb, "PressSpaceStartAnimation", app);
				}
				if (geoNum.getIntervalMax() != geoNum.getValue()) {
					appendSentence(sb, "PressUpToIncrease", app);
				}
				if (geoNum.getIntervalMin() != geoNum.getValue()) {
					appendSentence(sb, "PressDownToDecrease", app);
				}
			}
			if (geo0.getScript(EventType.CLICK) != null
					&& geo0.getScript(EventType.CLICK).getText().length() > 0) {
				appendSentence(sb, "PressSpaceToRunScript", app);
			}
			if (geo0.isGeoPoint() && (geo0.isIndependent()
					|| geo0.isPointOnPath() || geo0.isPointInRegion())) {
				appendSentence(sb, "PressArrowsToMove", app);
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
			App app) {
		sb.append(" ");
		sb.append(app.getLocalization().getMenu(string));
		sb.append(".");
	}
}
