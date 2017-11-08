package org.geogebra.common.main;

import org.geogebra.common.kernel.geos.GeoElement;

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
