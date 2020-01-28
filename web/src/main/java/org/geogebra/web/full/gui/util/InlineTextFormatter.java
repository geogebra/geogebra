package org.geogebra.web.full.gui.util;

import java.util.List;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class InlineTextFormatter {
	private final App app;

	public InlineTextFormatter(App app) {
		this.app = app;
	}

	/**
	 * @param targetGeos
	 *            geos to be formatter (non-texts are ignored)
	 * @param key
	 *            option name
	 * @param val
	 *            option value
	 * @return whether format changed
	 */
	public boolean formatInlineText(List<GeoElement> targetGeos,
			String key, Object val) {
		boolean changed = false;
		for (GeoElement geo : targetGeos) {
			changed = formatInlineText(geo, key, val);
		}

		return changed;
	}

	/**
	 * @param geo
	 *            geo to be formatter)
	 * @param key
	 *            option name
	 * @param val
	 *            option value
	 * @return whether format changed
	 */
	public boolean formatInlineText(GeoElement geo, String key, Object val) {
		DrawableND draw = app.getActiveEuclidianView().getDrawableFor(geo);
		if (draw instanceof DrawInlineText) {
			((DrawInlineText) draw).format(key, val);
			return true;
		}

		return false;
	}

}
