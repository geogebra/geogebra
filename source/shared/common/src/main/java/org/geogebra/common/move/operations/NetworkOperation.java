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

package org.geogebra.common.move.operations;

import org.geogebra.common.move.views.BooleanRenderable;

/**
 * Base for offline and online operations
 * @author gabor
 */
public class NetworkOperation extends BaseOperation<BooleanRenderable> {
	/**
	 * The Application is online, or not
	 */
	protected boolean online;

	/**
	 * Creates a new offlineOperation class for Offline functionality
	 * 
	 * @param online
	 *            whether the initial state is online
	 */
	public NetworkOperation(boolean online) {
		this.online = online;
	}

	/**
	 * @return if app state is online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * @param online
	 *            online state Sets the online state of the app (used from
	 *            events)
	 */
	public void setOnline(boolean online) {
		this.online = online;
		dispatchEvent(new BooleanEvent(online));
	}

}
