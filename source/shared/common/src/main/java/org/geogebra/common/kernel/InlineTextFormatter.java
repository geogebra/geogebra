package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.undo.UpdateContentActionStore;

public class InlineTextFormatter {

	private UpdateContentActionStore store;

	/**
	 * @param targetGeos
	 *            geos to be formatter (non-texts are ignored)
	 * @param key
	 *            option name
	 * @param val
	 *            option value
	 */
	public void formatInlineText(List<GeoElement> targetGeos, String key, Object val) {
		formatInlineText(targetGeos, formatter -> {
			formatter.format(key, val);
			return true;
		});
	}

	/**
	 * @param targetGeos
	 *            geos to be formatted (non-texts are ignored)
	 * @param formatFn
	 *            formatting function
	 */
	public void formatInlineText(List<GeoElement> targetGeos,
			Function<HasTextFormat, Boolean> formatFn) {
		boolean changed = false;
		ArrayList<GeoInline> geosToStore = new ArrayList<>();
		for (GeoElement geo : targetGeos) {
			if (geo instanceof HasTextFormatter) {
				geosToStore.add((GeoInline) geo);
			}
		}
		if (!geosToStore.isEmpty()) {
			store = new UpdateContentActionStore(geosToStore);
		}

		for (GeoElement geo : targetGeos) {
			if (geo instanceof HasTextFormatter) {
				changed = formatFn.apply(((HasTextFormatter) geo).getFormatter()) || changed;
			}
		}

		if (changed && store.needUndo() && !geosToStore.isEmpty()) {
			store.storeUndo();
		}
	}
}
