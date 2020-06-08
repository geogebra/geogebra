package org.geogebra.web.full.gui.util;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;

public class InlineTextFormatter {

	/**
	 * @param targetGeos
	 *            geos to be formatter (non-texts are ignored)
	 * @param key
	 *            option name
	 * @param val
	 *            option value
	 * @return whether format changed
	 */
	public boolean formatInlineText(List<GeoElement> targetGeos, String key, Object val) {
		boolean changed = false;
		for (GeoElement geo : targetGeos) {
			if (geo instanceof HasTextFormatter) {
				((HasTextFormatter) geo).getFormatter().format(key, val);
				changed = true;
			}
		}

		return changed;
	}
}
