package org.geogebra.common.main;

import org.geogebra.common.kernel.geos.GeoElement;

public class ScreenReader {

	public static void updateSelection(App app) {
		if (app.has(Feature.READ_OBJECT_NAME_AT_SELECTING)) {
			if (0 < app.getSelectionManager().getSelectedGeos().size()) {
				GeoElement geo0 = app.getSelectionManager().getSelectedGeos()
						.get(0);
				// do not steal focus from input box
				if (geo0.isGeoInputBox()) {
					return;
				}
				String text = geo0.getCaptionSimple();
				if (text == null || "".equals(text)) {
					text = geo0.getAlgebraDescriptionDefault();
				}
				// MOW-137 if selection originated in AV we don't want to move
				// focus to EV
				if (app.getGuiManager() == null || app.getGuiManager()
						.getLayout().getDockManager().getFocusedViewId() == app
								.getActiveEuclidianView().getViewID()) {
					app.getActiveEuclidianView().readText(text);
				}
			}
		}

	}

}
