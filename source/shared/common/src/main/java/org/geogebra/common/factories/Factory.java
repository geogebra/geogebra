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

package org.geogebra.common.factories;

import org.geogebra.common.javax.swing.RelationPane;

public abstract class Factory {
	private static final Object lock = new Object();
	private static volatile Factory prototype;

	/**
	 * @param subTitle relation pane subtitle
	 * @return relation pane
	 */
	public abstract RelationPane newRelationPane(String subTitle);

	/**
	 * @return might return null. Use App.getFactory()
	 */
	public static Factory getPrototype() {
		return prototype;
	}

	/**
	 * @param p prototype
	 */
	public static void setPrototype(Factory p) {
		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

}
