package org.geogebra.web.html5.util;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.FragmentPrefetcher;

import com.google.gwt.core.client.impl.LoadingStrategyBase;
import com.google.gwt.core.client.impl.ScriptTagLoadingStrategy.ScriptTagDownloadStrategy;
import com.google.gwt.core.client.impl.XhrLoadingStrategy.XhrDownloadStrategy;
import com.google.gwt.user.client.Window.Location;

/**
 * Download strategy that works with appcache / service worker over HTTP and
 * still supports file protocol (via script tag)
 *
 */
public class OfflineLoadingStrategy extends LoadingStrategyBase {

	/**
	 * @param downloadStrategy
	 *            internal download strategy
	 */
	public OfflineLoadingStrategy(DownloadStrategy downloadStrategy) {
		super(downloadStrategy);
	}

	private static DownloadStrategy makeDownloadStrategy() {
		return Location.getProtocol().startsWith("http")
				? new XhrDownloadStrategy() {
					@Override
					public void tryDownload(RequestData request) {
						int fragment = request.getFragment();
						setAsyncCallback(fragment, request);
						if (!loadWithPrefetch(request)) {
							super.tryDownload(request);
						}
					}
				} : new ScriptTagDownloadStrategy();

	}

	/**
	 * @param request
	 *            request
	 * @return whether there was a FragmentPrefatcher for handling this
	 */
	protected static boolean loadWithPrefetch(final RequestData request) {
		int fragment = request.getFragment();
		AsyncOperation<String> callback = new AsyncOperation<String>() {
			@Override
			public void callback(String code) {
				request.tryInstall(code);
			}
		};
		FragmentPrefetcher prefetch = FragmentPrefetcher
				.forSplitPoint(fragment);
		if (prefetch != null) {
			prefetch.runAfterPrefetch(callback);
			return true;
		}
		return false;
	}

	/**
	 * @param fragment
	 *            fragment number
	 * @param request
	 *            request
	 */
	static native void setAsyncCallback(int fragment,
			RequestData request) /*-{
    __gwtModuleFunction['runAsyncCallback' + fragment] = $entry(function(code, instance) {
      @com.google.gwt.core.client.impl.ScriptTagLoadingStrategy::asyncCallback(Lcom/google/gwt/core/client/impl/LoadingStrategyBase$RequestData;Ljava/lang/String;)(
        request, code);
    });
  }-*/;

	/**
	 * Create the strategy
	 */
	public OfflineLoadingStrategy() {
		super(makeDownloadStrategy());
	}
}
