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

package org.geogebra.web.full.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.html5.main.AppW;

/**
 * Device interface, gives access to browser or tablet native features
 */
public interface GDevice {

	/**
	 * @param app
	 *            application
	 * @return file manager
	 */
	FileManager createFileManager(AppW app);

	/**
	 * @param app
	 *            application
	 * @return whether device is offline
	 */
	boolean isOffline(AppW app);

	/**
	 * TODO make this browser-dependent, not GDevice dependent
	 * 
	 * @param app
	 *            application
	 * @return construction protocol
	 */
	ConstructionProtocolView getConstructionProtocolView(AppW app);

	/**
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 */
	void resizeView(int width, int height);
}
