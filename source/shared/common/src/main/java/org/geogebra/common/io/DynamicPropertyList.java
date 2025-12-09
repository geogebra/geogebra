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

package org.geogebra.common.io;

import java.util.LinkedList;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;

public class DynamicPropertyList {

	private LinkedList<GeoExpPair> pairs = new LinkedList<>();

	/**
	 * Consumer of (geo, property value) pairs.
	 */
	public interface Handler {
		/**
		 * @param geo construction element
		 * @param val property value
		 * @throws CircularDefinitionException if property value depends on geo and it shouldn't
		 */
		void accept(GeoElement geo, String val) throws CircularDefinitionException;
	}

	private static class GeoExpPair {
		private GeoElement geoElement;
		String exp;

		GeoExpPair(GeoElement g, String exp) {
			setGeo(g);
			this.exp = exp;
		}

		GeoElement getGeo() {
			return geoElement;
		}

		void setGeo(GeoElement geo) {
			this.geoElement = geo;
		}
	}

	/**
	 * @param geo construction element
	 * @param propertyValue property value
	 */
	public void add(GeoElement geo, String propertyValue) {
		pairs.add(new GeoExpPair(geo, propertyValue));
	}

	/**
	 * Apply property to all elements and clears the list
	 * @param handler property setter
	 */
	public void process(Handler handler) {
		for (GeoExpPair pair: pairs) {
			try {
				handler.accept(pair.getGeo(), pair.exp);
			} catch (RuntimeException | CircularDefinitionException ex) {
				Log.debug(ex);
			}
		}
		clear();
	}

	/**
	 * Clear all stored (geo, expression) pairs.
	 */
	public void clear() {
		pairs.clear();
	}
}
