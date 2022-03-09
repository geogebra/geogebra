package org.geogebra.web.full.gui.util;

import java.util.List;
import java.util.function.Function;

import org.geogebra.common.euclidian.draw.HasTextFormat;
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
		return formatInlineText(targetGeos, formatter -> {
			formatter.format(key, val);
			return true;
		});
	}

	/**
	 * @param targetGeos
	 *            geos to be formatter (non-texts are ignored)
	 * @param formatFn
	 *            formatting function
	 * @return whether format changed
	 */
	public boolean formatInlineText(List<GeoElement> targetGeos,
			Function<HasTextFormat, Boolean> formatFn) {
		boolean changed = false;
		for (GeoElement geo : targetGeos) {
			if (geo instanceof HasTextFormatter) {
				changed = formatFn.apply(((HasTextFormatter) geo).getFormatter()) || changed;
			}
		}
		return changed;
	}
}
