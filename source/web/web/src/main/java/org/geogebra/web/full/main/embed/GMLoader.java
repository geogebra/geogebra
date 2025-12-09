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

package org.geogebra.web.full.main.embed;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.gwtutil.ScriptLoadCallback;

import jsinterop.base.JsPropertyMap;

/**
 * 
 * Loader for Graspable Math
 *
 */
public class GMLoader {
	static final GMLoader INSTANCE = new GMLoader();
	private Map<Integer, GraspableEmbedElement> loadQueue = new HashMap<>();
	private boolean loadingStarted;

	/**
	 * @param graspableEmbedElement
	 *            GM embedded element
	 * @param embedID
	 *            embed ID
	 */
	public void load(GraspableEmbedElement graspableEmbedElement, int embedID) {
		loadQueue.put(embedID, graspableEmbedElement);
		if (loadingStarted) {
			return;
		}
		loadingStarted = true;
		JavaScriptInjector.loadJS("https://graspablemath.com/shared/libs/gmath/gm-inject.js",
				new ScriptLoadCallback() {

			@Override
			public void onLoad() {
				loadLatest();
			}

			@Override
			public void onError() {
				Log.warn("Could not load Graspable Math API");
			}

			@Override
			public void cancel() {
				// no need to cancel
			}
		});
	}

	protected void loadLatest() {
		NativeGMLoader.loadGM(this::loadFromQueue,
				JsPropertyMap.of("version", "latest", "build", "ggb"));
	}

	/**
	 * Load all elements in the queue
	 */
	protected void loadFromQueue() {
		for (Entry<Integer, GraspableEmbedElement> entry : loadQueue
				.entrySet()) {
			entry.getValue().initCanvas(entry.getKey());
		}
		loadQueue.clear();
	}
}
