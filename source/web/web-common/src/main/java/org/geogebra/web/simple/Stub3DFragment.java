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

package org.geogebra.web.simple;

import org.geogebra.common.geogebra3D.kernel3D.commands.SpatialCommandProcessorFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Replacement for 3D split point
 */
public class Stub3DFragment {

	/**
	 * This tricks GWT compiler into thinking that CommandDispatcher3D is a
	 * valid split point. The method needs to be called, otherwise it's removed
	 * by optimizer and the split point is not valid.
	 */
	public static void load() {
		// we don't want to wait for the actual fragment to load
		// simple if(false) might cause problems with optimization
		if (System.currentTimeMillis() > 1) {
			return;
		}
		GWT.runAsync(SpatialCommandProcessorFactory.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// never called
			}

			@Override
			public void onSuccess() {
				// never called
			}

		});
	}
}
