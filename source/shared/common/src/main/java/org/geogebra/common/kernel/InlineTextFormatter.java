/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
				HasTextFormat formatter = ((HasTextFormatter) geo).getFormatter();
				if (formatter != null) {
					changed = formatFn.apply(formatter) || changed;
				}
			}
		}

		if (changed && store.needUndo() && !geosToStore.isEmpty()) {
			store.storeUndo();
		}
	}
}
