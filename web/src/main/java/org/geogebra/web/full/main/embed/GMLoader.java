package org.geogebra.web.full.main.embed;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ScriptLoadCallback;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;

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
		ScriptElement gmInject = Document.get().createScriptElement();
		gmInject.setSrc(
				"https://graspablemath.com/shared/libs/gmath/gm-inject.js");
		loadingStarted = true;
		ResourcesInjector.loadJS(gmInject, new ScriptLoadCallback() {

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
