package org.geogebra.common.main;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class ScreenReader {

	public static void updateSelection(App app) {

		if (0 < app.getSelectionManager().getSelectedGeos().size()) {
			GeoElement geo0 = app.getSelectionManager().getSelectedGeos()
					.get(0);
			// do not steal focus from input box
			if (geo0.isGeoInputBox()) {
				return;
			}
			readText(geo0, app);
		}
	}


	public static void readText(GeoElement geo0, App app) {
		StringBuilder sb = new StringBuilder();
		String caption = geo0.getCaptionSimple();
		if (caption == null || "".equals(caption)) {
			sb.append(geo0.getAlgebraDescriptionDefault());
		} else {
			sb.append(caption);
		}

		if (geo0.isEuclidianShowable() && !geo0.isEuclidianVisible()) {
			sb.append(getNotVisibleText(app));
		}

		
		if (app.has(Feature.HELP_AND_SHORTCUTS)) {
			if (geo0.isEuclidianShowable()) {
				if (app.getGuiManager() != null
						&& app.getGuiManager().hasAlgebraView()
						&& !geo0.isGeoInputBox()) {
					if (geo0.isEuclidianVisible()) {
						sb.append(app.getLocalization()
								.getMenu("PressSlashToHide"));
					} else {
						sb.append(app.getLocalization()
								.getMenu("PressSlashToShow"));
					}
				}
			}

			if (geo0.isGeoButton()
					|| geo0.isGeoLocusStroke()) {
				sb.append(app.getLocalization()
						.getMenu("PressEnterToOpenSettings"));
			} else if (!geo0.isGeoInputBox()) {
				sb.append(app.getLocalization().getMenu("PressEnterToEdit"));
			}
			
			if (geo0.isGeoBoolean()){
				if(((GeoBoolean)geo0).getBoolean()){
					sb.append(app.getLocalization()
							.getMenu("PressSpaceCheckboxOff"));
				} else {
					sb.append(app.getLocalization()
							.getMenu("PressSpaceCheckboxOn"));
				}
			}

			if (geo0.isGeoNumeric() && ((GeoNumeric) geo0).isSliderable()) {
				if (((GeoNumeric) geo0).isAnimating()) {
					sb.append(app.getLocalization()
							.getMenu("PressSpaceStopAnimation"));
				} else {
					sb.append(app.getLocalization()
							.getMenu("PressSpaceStartAnimation"));
				}
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

	private static String getNotVisibleText(App app) {
		Localization loc = app.getLocalization();
		return loc.getMenu("not visible");
	}

}
