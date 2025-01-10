package org.geogebra.web.html5.main;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.util.HttpRequestW;

import com.google.gwt.core.client.GWT;

/**
 * Creates HTTP requests to fetch a fragment before it's needed. Prefetched
 * content can be consumed by OfflineLoadingStrategy
 */
public final class FragmentPrefetcher implements AjaxCallback {
	private static Map<Integer, FragmentPrefetcher> idToPrefetcher = new HashMap<>();

	private AsyncOperation<String> fetchCallback;
	private String content;

	private int splitPoint;

	private FragmentPrefetcher(int splitPoint) {
		this.splitPoint = splitPoint;
	}

	/**
	 * @param splitPoint
	 *            split point number
	 * @return whether prefetch is currently in progress
	 */
	public static FragmentPrefetcher forSplitPoint(int splitPoint) {
		return idToPrefetcher.get(splitPoint);
	}

	/**
	 * @param callback
	 *            callback
	 */
	public void runAfterPrefetch(AsyncOperation<String> callback) {
		fetchCallback = callback;
		resolveCallbacks();
	}

	private void resolveCallbacks() {
		if (content != null && fetchCallback != null) {
			idToPrefetcher.remove(splitPoint);
			fetchCallback.callback(content);
		}
	}

	/**
	 * @param splitPoint
	 *            fragment ID
	 */
	public static void prefetch(int splitPoint) {
		if (forSplitPoint(splitPoint) == null) {
			final FragmentPrefetcher fragmentPrefetcher = new FragmentPrefetcher(
					splitPoint);
			idToPrefetcher.put(splitPoint, fragmentPrefetcher);
			fragmentPrefetcher.fetch();
		}
	}

	private void fetch() {
		final String url = GWT.getModuleBaseURL() + "deferredjs/"
				+ GWT.getPermutationStrongName() + "/" + splitPoint
				+ ".cache.js";
		new HttpRequestW().sendRequestPost("GET", url, null, this);
	}

	@Override
	public void onSuccess(String response) {
		content = response;
		resolveCallbacks();
	}

	@Override
	public void onError(String error) {
		Log.warn("Prefetch failed for fragment " + splitPoint);
	}
}
